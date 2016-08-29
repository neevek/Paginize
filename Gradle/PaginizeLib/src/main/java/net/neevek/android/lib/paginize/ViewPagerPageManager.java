package net.neevek.android.lib.paginize;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
 * This class wraps the logics used by ViewPagerPage and ViewPagerInnerPage, both implement InnerPageContainer
 */
public class ViewPagerPageManager extends InnerPageContainerManager {
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

  public ViewPagerPageManager(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
    initViewPager(innerPageContainer.getContext());
  }

  public void setInnerPageEventNotifier(
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

  public void setAlwaysKeepInnerPagesInViewHierarchy(
      boolean alwaysKeepInnerPagesInViewHierarchy) {
    mAlwaysKeepInnerPagesInViewHierarchy = alwaysKeepInnerPagesInViewHierarchy;
  }

  public void setupTabLayout(int tabLayoutResId, final boolean smoothScroll) {
    setupTabLayout((TabLayout) getInnerPageContainer()
        .findViewById(tabLayoutResId), smoothScroll);
  }

  public void setupTabLayout(TabLayout tabLayout, final boolean smoothScroll) {
    if (tabLayout == null) {
      throw new IllegalArgumentException("The specified TabLayout is null");
    }
    mTabLayout = tabLayout;
    mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      public void onTabSelected(TabLayout.Tab tab) {
        if (tab != null) {
          mViewPager.setCurrentItem(tab.getPosition(), smoothScroll);
        }
      }
      public void onTabUnselected(TabLayout.Tab tab) { }
      public void onTabReselected(TabLayout.Tab tab) { }
    });
    mViewPager.addOnPageChangeListener(
        new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
  }

  public void addPage(InnerPage page, CharSequence tabText, Drawable tabIcon) {
    if (tabText != null || tabIcon != null) {
      checkTabLayout();
      mTabLayout.addTab(mTabLayout.newTab().setText(tabText).setIcon(tabIcon));
    }
    addPage(page);
  }

  public void addPage(InnerPage page, CharSequence tabText,
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

  public void addPage(InnerPage page, CharSequence tabText) {
    addPage(page, tabText, null);
  }

  public void addPage(InnerPage page, View tabView) {
    checkTabLayout();
    mTabLayout.addTab(mTabLayout.newTab().setCustomView(tabView));
    addPage(page);
  }

  public void addPage(InnerPage page, int tabView) {
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

  public void addPage(InnerPage page) {
    if (getCurrentInnerPage() == null) {
      setCurrentInnerPage(page);
    }
    mInnerPageList.add(page);
    mPagerAdapter.notifyDataSetChanged();
  }

  public void removePage(int index) {
    checkPageIndex(index);

    mInnerPageList.remove(index);
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

  public void setCurrentPage(int index, boolean animated) {
    if (mInnerPageList.size() == 0) {
      return;
    }

    checkPageIndex(index);

    try {
      mViewPager.setCurrentItem(index, animated);
      mLastSelectedPage = index;
    } catch (Exception e) {
      if (animated) { // ignore animated, which may cause crash
        try {   // not sure if it would crash
          mViewPager.setCurrentItem(index, false);
          mLastSelectedPage = index;
        } catch (Exception e2) {
          e2.printStackTrace();
        }
      }
    }
  }

  public int getCurrentPageIndex() {
    return mViewPager.getCurrentItem();
  }

  public InnerPage getPage(int index) {
    checkPageIndex(index);

    return mInnerPageList.get(index);
  }

  public int getPageCount() {
    return mInnerPageList.size();
  }

  public void setHorizontalFadingEdgeEnabled(boolean enabled) {
    mViewPager.setHorizontalFadingEdgeEnabled(enabled);
  }

  public void setFadingEdgeLength(int length) {
    mViewPager.setFadingEdgeLength(length);
  }

  public void setPageScrollListener(
      ViewPagerPageScrollListener pageScrollListener) {
    mPageScrollListener = pageScrollListener;
  }

  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY, getCurrentPageIndex());
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    final int lastShownPageIndex =
        savedInstanceState.getInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY);
    if (lastShownPageIndex < getPageCount()) {
      setCurrentPage(lastShownPageIndex, false);
    }
  }

  private void checkPageIndex(int index) {
    if (index >= mInnerPageList.size()) {
      throw new IllegalArgumentException("index is too large: " + index +
          ", actual: " + mInnerPageList.size());
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
}
