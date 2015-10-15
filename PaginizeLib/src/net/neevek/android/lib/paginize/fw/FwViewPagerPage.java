package net.neevek.android.lib.paginize.fw;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;

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
 * and ContainerPage is that this class offers page swiping while ContainerPage does not.
 *
 * @see FwViewPagerPageManager
 * @see FwViewPagerInnerPage
 */
public abstract class FwViewPagerPage extends FwPage implements FwInnerPageContainer {
  private FwViewPagerPageManager mViewPagerPageManager;

  public FwViewPagerPage(FwPageManager pageManager) {
    super(pageManager);
    mViewPagerPageManager = new FwViewPagerPageManager(this);
    mViewPagerPageManager.setPageScrollListener(new FwViewPagerPageScrollListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        FwViewPagerPage.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
      }

      @Override
      public void onPageSelected(int position) {
        FwViewPagerPage.this.onPageSelected(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        FwViewPagerPage.this.onPageScrollStateChanged(state);
      }
    });
  }

  public void setAlwaysKeepInnerPagesInViewHierarchy(boolean alwaysKeepInnerPagesInViewHierarchy) {
    mViewPagerPageManager.setAlwaysKeepInnerPagesInViewHierarchy(alwaysKeepInnerPagesInViewHierarchy);
  }

  public void addPage(FwInnerPage page) {
    mViewPagerPageManager.addPage(page);
  }

  public void removePage(int index) {
    mViewPagerPageManager.removePage(index);
  }

  public void setCurrentPage(int index, boolean animated) {
    mViewPagerPageManager.setCurrentPage(index, animated);
  }

  public int getCurrentPageIndex() {
    return mViewPagerPageManager.getCurrentPageIndex();
  }

  public int getPageCount() {
    return mViewPagerPageManager.getPageCount();
  }

  public void setHorizontalFadingEdgeEnabled(boolean enabled) {
    mViewPagerPageManager.setHorizontalFadingEdgeEnabled(enabled);
  }

  public void setFadingEdgeLength(int length) {
    mViewPagerPageManager.setFadingEdgeLength(length);
  }

  protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {  }
  protected void onPageSelected(int position) { }
  protected void onPageScrollStateChanged(int state) { }

  @Override
  public ViewGroup getContainerView() {
    return mViewPagerPageManager.getContainerView();
  }

  @Override
  public void onShow(Object arg) {
    mViewPagerPageManager.onShow(arg);
  }

  @Override
  public void onShown(Object arg) {
    mViewPagerPageManager.onShown(arg);
  }

  @Override
  public boolean onBackPressed() {
    return mViewPagerPageManager.onBackPressed();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return mViewPagerPageManager.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return mViewPagerPageManager.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return mViewPagerPageManager.onTouchEvent(event);
  }

  @Override
  public void onHide() {
    mViewPagerPageManager.onHide();
  }

  @Override
  public void onHidden() {
    mViewPagerPageManager.onHidden();
  }

  @Override
  public void onCover() {
    mViewPagerPageManager.onCover();
  }

  @Override
  public void onCovered() {
    mViewPagerPageManager.onCovered();
  }

  @Override
  public void onUncover(Object arg) {
    mViewPagerPageManager.onUncover(arg);
  }

  @Override
  public void onUncovered(Object arg) {
    mViewPagerPageManager.onUncovered(arg);
  }
}
