package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.exception.InjectFailedException;

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
 * ViewPagerPage wraps a ViewPager, manages InnerPages, the main difference between ViewPagerPage
 * and InnerPageContainer is that this class offers page swiping while InnerPageContainer does not.
 *
 * @see InnerPage
 */
public abstract class ViewPagerPage extends BaseInnerPageContainer {
  private final String SAVE_INNER_PAGE_MANAGER_KEY = "_paginize_viewpager_page_manager_" + getClass().getName();

  private InnerPage mCurPage;

  private List<InnerPage> mInnerPageList = new ArrayList<InnerPage>();
  private PagerAdapter mPagerAdapter;
  private ViewPager mViewPager;
  private int mLastSelectedPage;

  public ViewPagerPage(PageActivity pageActivity) {
    super(pageActivity);
    initViewPager();
  }

  private void initViewPager() {
    mViewPager = new ViewPager(getContext());
    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mViewPager.setLayoutParams(lp);

    mPagerAdapter = new InnerPagePagerAdapter();
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.setOnPageChangeListener(new InnerPageChangeListener());

    getContainerView().addView(mViewPager);
  }

  public void addPage(InnerPage page) {
    if (mCurPage == null) {
      mCurPage = page;
    }
    mInnerPageList.add(page);
    mPagerAdapter.notifyDataSetChanged();
  }

  public void setCurrentPage(int index, boolean animated) {
    if (index > mInnerPageList.size()) {
      throw new IllegalArgumentException("index is too large: " + index + ", actual: " + mInnerPageList.size());
    }

    mViewPager.setCurrentItem(index, animated);
    mCurPage = mInnerPageList.get(index);
  }

  public int getCurrentPageIndex() {
    return mViewPager.getCurrentItem();
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

  @Override
  public void onShow(Object arg) {
    if (mCurPage != null) {
      mCurPage.onShow(arg);
    }
  }

  @Override
  public void onShown(Object arg) {
    if (mCurPage != null) {
      mCurPage.onShown(arg);
    }
  }

  @Override
  public void onResume() {
    if (mCurPage != null) {
      mCurPage.onResume();
    } else {
      super.onResume();
    }
  }

  @Override
  public void onPause() {
    if (mCurPage != null) {
      mCurPage.onPause();
    } else {
      super.onPause();
    }
  }

  @Override
  public boolean onBackPressed() {
    if (mCurPage != null) {
      return mCurPage.onBackPressed();
    }
    return super.onBackPressed();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mCurPage != null) {
      mCurPage.onActivityResult(requestCode, resultCode, data);
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mCurPage != null && mCurPage.onKeyDown(keyCode, event)) {
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mCurPage != null && mCurPage.onKeyUp(keyCode, event)) {
      return true;
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mCurPage != null && mCurPage.onTouchEvent(event)) {
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mCurPage != null) {
      mCurPage.onConfigurationChanged(newConfig);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mCurPage != null) {
      mCurPage.onSaveInstanceState(outState);
    }
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (mCurPage != null) {
      mCurPage.onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  public void onHide() {
    if (mCurPage != null) {
      mCurPage.onHide();
    }
  }

  @Override
  public void onHidden() {
    if (mCurPage != null) {
      mCurPage.onHidden();
    }
  }

  @Override
  public void onCover() {
    if (mCurPage != null) {
      mCurPage.onCover();
    }
  }

  @Override
  public void onCovered() {
    if (mCurPage != null) {
      mCurPage.onCovered();
    }
  }

  @Override
  public void onUncover(Object arg) {
    if (mCurPage != null) {
      mCurPage.onUncover(arg);
    }
  }

  @Override
  public void onUncovered(Object arg) {
    if (mCurPage != null) {
      mCurPage.onUncovered(arg);
    }
  }

  protected void onPageScrolled(int index, float indexOffset, int indexOffsetPixels) {  }

  class InnerPagePagerAdapter extends PagerAdapter {
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      InnerPage innerPage = mInnerPageList.get(position);
      container.addView(innerPage.getView());

      return innerPage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      InnerPage innerPage = (InnerPage) object;
      container.removeView(innerPage.getView());
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
  }

  class InnerPageChangeListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      ViewPagerPage.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
      InnerPage oldPage = mInnerPageList.get(mLastSelectedPage);
      oldPage.onHide();
      oldPage.onHidden();

      mLastSelectedPage = position;

      InnerPage newPage = mInnerPageList.get(mLastSelectedPage);
      newPage.onShow(null);
      newPage.onShown(null);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
  }
}
