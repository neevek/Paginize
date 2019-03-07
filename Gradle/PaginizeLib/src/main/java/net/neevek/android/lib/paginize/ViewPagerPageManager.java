package net.neevek.android.lib.paginize;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * This class wraps the logics used by ViewPagerPage and ViewPagerInnerPage,
 * both implement InnerPageContainer
 */
class ViewPagerPageManager extends InnerPageContainerManager {
  private final String SAVE_VIEWPAGER_PAGE_MANAGER_KEY =
      "_paginize_viewpager_page_manager_" + getClass().getName();

  private List<InnerPage> mInnerPageList = new ArrayList<InnerPage>();
  private PagerAdapter mPagerAdapter;
  private TabLayout mTabLayout;
  private ViewPager mViewPager;
  private int mLastSelectedPage;
  private boolean mAlwaysKeepInnerPagesInViewHierarchy;

  private ViewPagerPageScrollListener mPageScrollListener;
  private InnerPageEventNotifier mInnerPageEventNotifier;
  private SetCurrentPageTask mSetCurrentPageTask;

  private boolean mPageSmoothScroll = true;

  ViewPagerPageManager(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
    initViewPager(innerPageContainer.getContext());
  }

  void setInnerPageEventNotifier(
      InnerPageEventNotifier innerPageEventNotifier) {
    mInnerPageEventNotifier = innerPageEventNotifier;
  }

  private void initViewPager(Context context) {
    mViewPager = new ViewPager(context);
    mViewPager.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));

    mPagerAdapter = new InnerPagePagerAdapter();
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.addOnPageChangeListener(new InnerPageChangeListener());

    getContainerView().addView(mViewPager);
  }

  void setAlwaysKeepInnerPagesInViewHierarchy(
      boolean alwaysKeepInnerPagesInViewHierarchy) {
    mAlwaysKeepInnerPagesInViewHierarchy = alwaysKeepInnerPagesInViewHierarchy;
  }

  void setPageSmoothScrollEnabled(boolean enabled) {
    mPageSmoothScroll = enabled;
  }

  void setupTabLayout(int tabLayoutResId, final boolean smoothScroll) {
    setupTabLayout((TabLayout) getInnerPageContainer()
        .findViewById(tabLayoutResId), smoothScroll);
  }

  void setupTabLayout(TabLayout tabLayout, final boolean smoothScroll) {
    if (tabLayout == null) {
      throw new IllegalArgumentException("The specified TabLayout is null");
    }
    mTabLayout = tabLayout;
    mPageSmoothScroll = smoothScroll;
    mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      public void onTabSelected(TabLayout.Tab tab) {
        if (tab != null) {
          mViewPager.setCurrentItem(tab.getPosition(), mPageSmoothScroll);
        }
      }
      public void onTabUnselected(TabLayout.Tab tab) { }
      public void onTabReselected(TabLayout.Tab tab) { }
    });
    mViewPager.addOnPageChangeListener(
        new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
  }

  void addPage(InnerPage page, CharSequence tabText, Drawable tabIcon) {
    if (tabText != null || tabIcon != null) {
      checkTabLayout();
      mTabLayout.addTab(mTabLayout.newTab().setText(tabText).setIcon(tabIcon));
    }
    addPage(page);
  }

  void addPage(InnerPage page, CharSequence tabText,
                      int tabIcon) {
    if (tabText != null || tabIcon > 0) {
      checkTabLayout();

      TabLayout.Tab tab = mTabLayout.newTab().setText(tabText);
      if (tabIcon > 0) {
        tab.setIcon(tabIcon);
      }
      mTabLayout.addTab(tab);
    }
    addPage(page);
  }

  void addPage(InnerPage page, CharSequence tabText) {
    addPage(page, tabText, null);
  }

  void addPage(InnerPage page, View tabView) {
    checkTabLayout();
    mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabView));
    addPage(page);
  }

  void addPage(InnerPage page, int tabView) {
    checkTabLayout();
    mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabView));
    addPage(page);
  }

  private void checkTabLayout() {
    if (mTabLayout == null) {
      throw new IllegalStateException(
          "TabLayout not set, call setupTabLayout() first");
    }
  }

  void addPage(InnerPage page) {
    if (getCurrentInnerPage() == null) {
      setCurrentInnerPage(page);
    }
    mInnerPageList.add(page);
    mPagerAdapter.notifyDataSetChanged();
  }

  void removePage(int index) {
    checkPageIndex(index);

    InnerPage removedPage = mInnerPageList.remove(index);
    removedPage.onHide();
    removedPage.onHidden();
    removedPage.onDestroy();

    if (mLastSelectedPage - 1 >= 0) {
      mLastSelectedPage -= 1;
    } else {
      mLastSelectedPage = 0;
    }
    if (mTabLayout != null) {
      mTabLayout.removeTabAt(index);
    }
    mPagerAdapter.notifyDataSetChanged();
    setCurrentPage(mLastSelectedPage, false);
  }

  void setCurrentPage(final int index, final boolean animated) {
    if (mSetCurrentPageTask != null) {
      return;
    }

    if (mViewPager.getWidth() == 0) {
      mSetCurrentPageTask = new SetCurrentPageTask(index, animated);
      mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(mSetCurrentPageTask);
    } else {
      setCurrentPageInternal(index, animated);
    }
  }

  private void setCurrentPageInternal(final int index, final boolean animated) {
    if (mInnerPageList.size() == 0) {
      return;
    }

    checkPageIndex(index);
    mViewPager.setCurrentItem(index, animated);
    mLastSelectedPage = index;
  }

  int getCurrentPageIndex() {
    if (mSetCurrentPageTask != null) {
      return mSetCurrentPageTask.getPendingPageIndex();
    }
    return mViewPager.getCurrentItem();
  }

  InnerPage getPage(int index) {
    checkPageIndex(index);

    return mInnerPageList.get(index);
  }

  int getPageCount() {
    return mInnerPageList.size();
  }

  void setHorizontalFadingEdgeEnabled(boolean enabled) {
    mViewPager.setHorizontalFadingEdgeEnabled(enabled);
  }

  void setFadingEdgeLength(int length) {
    mViewPager.setFadingEdgeLength(length);
  }

  void setPageScrollListener(
      ViewPagerPageScrollListener pageScrollListener) {
    mPageScrollListener = pageScrollListener;
  }

  void onSaveInstanceState(Bundle outState) {
    outState.putInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY, getCurrentPageIndex());
    for (int i = 0; i < mInnerPageList.size(); ++i) {
      mInnerPageList.get(i).onSaveInstanceState(outState);
    }
  }

  void onRestoreInstanceState(Bundle savedInstanceState) {
    final int lastShownPageIndex =
        savedInstanceState.getInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY);
    if (lastShownPageIndex < getPageCount()) {
      setCurrentPage(lastShownPageIndex, false);
    }
    for (int i = 0; i < mInnerPageList.size(); ++i) {
      mInnerPageList.get(i).onRestoreInstanceState(savedInstanceState);
    }
  }

  private void checkPageIndex(int index) {
    if (index >= mInnerPageList.size()) {
      throw new IllegalArgumentException("index is too large: " + index +
          ", actual: " + mInnerPageList.size());
    }
  }

  int indexOf(InnerPage innerPage) {
    return mInnerPageList.indexOf(innerPage);
  }

  void onDestroy() {
    for (InnerPage innerPage : mInnerPageList) {
      if (innerPage != null) {
        innerPage.onDestroy();
      }
    }
  }

  class InnerPagePagerAdapter extends PagerAdapter {
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      InnerPage innerPage = mInnerPageList.get(position);
      if (!mAlwaysKeepInnerPagesInViewHierarchy ||
          container.indexOfChild(innerPage.getView()) == -1) {
        container.addView(innerPage.getView());
      }

      return innerPage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      if (!mAlwaysKeepInnerPagesInViewHierarchy) {
        InnerPage innerPage = (InnerPage) object;
        container.removeView(innerPage.getView());
      }
    }

    @Override
    public int getCount() {
      return mInnerPageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
      InnerPage innerPage = (InnerPage)o;
      return view == innerPage.getView();
    }

    @Override
    public int getItemPosition(Object object) {
      // see http://stackoverflow.com/a/7287121/668963
      return POSITION_NONE;
    }
  }

  class InnerPageChangeListener implements ViewPager.OnPageChangeListener {
    private boolean markNewPageSelected;
    private InnerPage oldPage;

    @Override
    public void onPageScrolled(int index, float indexOffset,
                               int indexOffsetPixels) {
      if (mPageScrollListener != null) {
        mPageScrollListener.onPageScrolled(index, indexOffset, indexOffsetPixels);
      }
    }

    @Override
    public void onPageSelected(int position) {
      if (mViewPager.getScrollX() % mViewPager.getWidth() != 0) {
        markNewPageSelected = true;
      }

      oldPage = mInnerPageList.get(mLastSelectedPage);
      oldPage.onHide();
      if (!markNewPageSelected) {
        oldPage.onHidden();
        if (mInnerPageEventNotifier != null) {
          mInnerPageEventNotifier.onInnerPageHidden(oldPage);
        }
      }

      mLastSelectedPage = position;

      InnerPage newPage = mInnerPageList.get(mLastSelectedPage);
      setCurrentInnerPage(newPage);

      newPage.onShow();
      if (!markNewPageSelected) {
        newPage.onShown();
        if (mInnerPageEventNotifier != null) {
          mInnerPageEventNotifier.onInnerPageShown(newPage);
        }
      }

      if (mPageScrollListener != null) {
        mPageScrollListener.onPageSelected(position);
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      if (mPageScrollListener != null) {
        mPageScrollListener.onPageScrollStateChanged(state);
      }

      if (markNewPageSelected && state == ViewPager.SCROLL_STATE_IDLE) {
        markNewPageSelected = false;

        if (oldPage != null) {
          oldPage.onHide();
          oldPage.onHidden();
          if (mInnerPageEventNotifier != null) {
            mInnerPageEventNotifier.onInnerPageHidden(oldPage);
          }
          InnerPage newPage = mInnerPageList.get(mLastSelectedPage);

          if (newPage != null) {
            newPage.onShow();
            newPage.onShown();
            if (mInnerPageEventNotifier != null) {
              mInnerPageEventNotifier.onInnerPageShown(newPage);
            }
          }
        }
      }
    }
  }

  class SetCurrentPageTask implements ViewTreeObserver.OnGlobalLayoutListener {
    private int index;
    private boolean animated;
    SetCurrentPageTask(int index, boolean animated) {
      this.index = index;
      this.animated = animated;
    }

    int getPendingPageIndex() {
      return index;
    }

    @Override
    public void onGlobalLayout() {
      if (mViewPager.getWidth() > 0) {
        if (Build.VERSION.SDK_INT >= 16) {
          mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
          mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        mSetCurrentPageTask = null;
        setCurrentPageInternal(index, animated);
      }
    }
  }
}
