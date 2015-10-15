package net.neevek.android.lib.paginize;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.FrameLayout;
import net.neevek.android.lib.paginize.anim.PageAnimator;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;

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
 * PageManager manages the pages(of type Page), it swaps(push and pop) the pages
 * when requested, it uses PageAnimationManager to animate the transition when
 * swapping Pages.
 *
 * @see net.neevek.android.lib.paginize.Page
 * @see net.neevek.android.lib.paginize.anim.PageAnimator
 */
public final class PageManager {
  private final String SAVE_PAGE_STACK_KEY = "_paginize_page_stack";
  private final String TAG = PageManager.class.getSimpleName();

  private PageActivity mPageActivity;
  private ViewGroup mContainerView;
  // a mask view that intercepts all touch events when a page is in a process of pushing or popping
  private View mViewTransparentMask;
  // on Kitkat or greater, this is used to set background color of the status bar
  private View mStatusBarBackgroundView;
  private boolean mInitializedForTranslucentStatus;

  private boolean mEnableDebug;

  // the stack to hold the pages
  private LinkedList<Page> mPageStack = new LinkedList<Page>();
  // the page on top of the stack
  private Page mCurPage;
  // the PageAnimator to animate transitions when swapping pages
  private PageAnimator mPageAnimator;
  private boolean mAnimating;


  public PageManager(PageActivity pageActivity) {
    this(pageActivity, null);
  }

  public PageManager(PageActivity pageActivity, PageAnimator pageAnimator) {
    mPageActivity = pageActivity;
    mPageAnimator = pageAnimator;

    mContainerView = new FrameLayout(pageActivity);

    mViewTransparentMask = new View(pageActivity);
    mViewTransparentMask.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    mViewTransparentMask.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // intercept all touch events
        return true;
      }
    });
    mContainerView.addView(mViewTransparentMask);
    mViewTransparentMask.setBackgroundColor(0x00ffffff);

    pageActivity.setContentView(mContainerView);
  }

  public void setDebug(boolean debug) {
      mEnableDebug = debug;
  }

  public void setPageAnimator(PageAnimator pageAnimator) {
    mPageAnimator = pageAnimator;
  }

  public PageAnimator getPageAnimator() {
    return mPageAnimator;
  }

  public int getStatusBarBackgroundColor() {
    return mStatusBarBackgroundView != null ? ((ColorDrawable)mStatusBarBackgroundView.getBackground()).getColor() : 0;
  }

  @TargetApi(19)
  public void setStatusBarBackgroundColor(int color) {
    if (Build.VERSION.SDK_INT < 19) {
      return;
    }

    if (!mInitializedForTranslucentStatus) {
      mInitializedForTranslucentStatus = true;

      mPageActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      setDrawsSystemBarBackgroundsFlag();

      mContainerView.setFitsSystemWindows(true);

      int statusBarHeight = getStatusBarHeight();
      mStatusBarBackgroundView = new View(mPageActivity);
      ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
      // after setting setFitsSystemWindows(true), top padding of the container view will be set to height of the status bar
      // here we set negative margin for the status view so it is placed right behind the translucent system status bar.
      lp.topMargin = -statusBarHeight;

//      ((ViewGroup)mPageActivity.findViewById(android.R.id.content)).addView(mStatusBarBackgroundView, lp);
      ((ViewGroup)mContainerView.getParent()).addView(mStatusBarBackgroundView, lp);
    }

    if ((color & 0xff000000) == 0) {
      color |= 0xff000000;
    }

    mStatusBarBackgroundView.setBackgroundColor(color);
  }

  @TargetApi(21)
  private void setDrawsSystemBarBackgroundsFlag() {
    if (Build.VERSION.SDK_INT >= 21) {
      mPageActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
  }

  private int getStatusBarHeight() {
    Resources res = mPageActivity.getResources();
    int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return res.getDimensionPixelSize(resourceId);
    }
    return 0;
  }

  public void pushPages(Page[] pages) {
    pushPages(pages, null, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Page[] pages, Object arg, boolean animated) {
    pushPages(pages, arg, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  /**
   * 'arg' will be passed to the last page in the array
   **/
  public void pushPages(Page[] pages, Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pages == null || pages.length == 0) {
      return;
    }

    Page firstOldPage = mCurPage;
    Page tmpOldPage = null;
    for (int i = 0; i < pages.length - 1; ++i) {
      pushPageInternal(pages[i], tmpOldPage, null, false, animationDirection);
      tmpOldPage = pages[i];
    }
    pushPageInternal(pages[pages.length - 1], firstOldPage, arg, animated, animationDirection);
  }

  public void pushPages(Pair<Page, Object>[] pagePacks) {
    pushPages(pagePacks, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Pair<Page, Object>[] pagePacks, boolean animated) {
    pushPages(pagePacks, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Pair<Page, Object>[] pagePacks, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pagePacks == null || pagePacks.length == 0) {
      return;
    }

    Page firstOldPage = mCurPage;
    Page tmpOldPage = null;
    for (int i = 0; i < pagePacks.length - 1; ++i) {
      Pair<Page, Object> pagePack = pagePacks[i];
      if (pagePack.first == null) {
        continue;
      }
      pushPageInternal(pagePack.first, tmpOldPage, pagePack.second, false, animationDirection);
      tmpOldPage = pagePack.first;
    }

    Pair<Page, Object> lastPagePack = pagePacks[pagePacks.length - 1];
    if (lastPagePack.first != null) {
      pushPageInternal(lastPagePack.first, firstOldPage, lastPagePack.second, animated, animationDirection);
    }
  }

  public void pushPage(Page page) {
    pushPage(page, null, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(Page newPage, Object arg, boolean animated) {
    pushPage(newPage, arg, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(Page newPage, Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (newPage == mCurPage) {
      return;
    }

    pushPageInternal(newPage, mCurPage, arg, animated, animationDirection);
  }

  private void pushPageInternal(final Page newPage, final Page oldPage, final Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (animated) {
      mAnimating = true;
    }

    newPage.onShow(arg);

    if (oldPage != null) {
      if (animated) {
        // when a new page is being pushed, ensures the oldPage always be visible during the animation
        // transition if animated is true
        oldPage.getView().bringToFront();
      }

      oldPage.onCover();
    }

    mCurPage = newPage;
    mPageStack.addLast(newPage);
    mContainerView.addView(newPage.getView());

    mViewTransparentMask.bringToFront();

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> pushPage, pagestack=%d, %s, arg=%s", mPageStack.size(), newPage, arg));
    }

    if (animated && !newPage.onPushPageAnimation(oldPage != null ? oldPage.getView() : null, newPage.getView(), animationDirection) && mPageAnimator != null) {
      mPageAnimator.onPushPageAnimation(oldPage != null ? oldPage.getView() : null, newPage.getView(), animationDirection);
    }

    int animationDuration = newPage.getAnimationDuration();
    if (animationDuration == -1 && mPageAnimator != null) {
      animationDuration = mPageAnimator.getAnimationDuration();
    }
    if (animated && animationDuration != -1) {
      newPage.postDelayed(new Runnable() {
        @Override
        public void run() {
          doFinalWorkForPushPage(oldPage, newPage, arg);
        }
      }, animationDuration);
    } else {
      doFinalWorkForPushPage(oldPage, newPage, arg);
    }
  }

  private void doFinalWorkForPushPage(Page oldPage, Page newPage, Object arg) {
    if (newPage == getTopPage()) {
      newPage.getView().bringToFront();
    }

    if (oldPage != null) {
      if (newPage.getType() != Page.TYPE.TYPE_DIALOG) {
        oldPage.getView().setVisibility(View.GONE);
      }

      oldPage.onCovered();
    }

    newPage.onShown(arg);
    newPage.getView().requestFocus();
    mAnimating = false;
  }

  public void popPage(boolean animated) {
    popTopNPages(1, animated);
  }

  /**
   * @param animated true to animate the transition
   */
  public void popPage(boolean animated, PageAnimator.AnimationDirection animationDirection) {
    popTopNPages(1, animated, animationDirection);
  }

  public void popTopNPages(int n, boolean animated) {
    popTopNPages(n, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  public void popTopNPages(int n, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (n <= 0 || mPageStack.size() <= 0) {
      return;
    }

    Page oldPage = mPageStack.removeLast();
    --n;    // for mPageStack.removeLast() above

    while (--n >= 0) {
      Page page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();

      if (mEnableDebug) {
        Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), page));
      }
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  public void popToPage(Page destPage, boolean animated) {
    popToPage(destPage, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends if destPage is found,
   * if destPage is not found, the method call is a no-op
   *
   * @param destPage page as the destination for this pop operation
   * @param animated true to animate the transition
   */
  public void popToPage(Page destPage, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (destPage == null) {
      throw new IllegalArgumentException("cannot call popToPage() with null destPage.");
    }

    if (mPageStack.size() <= 0 || mPageStack.lastIndexOf(destPage) == -1 || mPageStack.peekLast() == destPage) {
      return;
    }

    Page oldPage = mPageStack.removeLast();

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), oldPage));
    }

    while (mPageStack.size() > 1) {
      if (mPageStack.peekLast() == destPage) {
        break;
      }

      Page page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  public void popToClass(Class<? extends Page> pageClass, boolean animated) {
    popToClass(pageClass, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends if the pageClass is found,
   * if the class is not found, the method call is a no-op
   *
   * @param pageClass class of page as the destination for this pop operation
   * @param animated  true to animate the transition
   */
  public void popToClass(Class<? extends Page> pageClass, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pageClass == null) {
      throw new IllegalArgumentException("cannot call popToClass() with null pageClass.");
    }

    popToClasses(new Class[]{pageClass}, animated, animationDirection);
  }

  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated) {
    popToClasses(pageClasses, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends when one of the classes specified by pageClasses is found,
   * if none of the classes is found, the method call is a no-op
   *
   * @param pageClasses classes of pages as the destination for this pop operation
   * @param animated    true to animate the transition
   */
  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (pageClasses == null || pageClasses.length == 0) {
      throw new IllegalArgumentException("cannot call popToClasses() with null or empty pageClasses.");
    }

    if (mPageStack.size() <= 0) {
      return;
    }

    // do nothing if the topPage is the page we want to navigate to
    Class topPageClass = mPageStack.peekLast().getClass();
    for (Class pageClass : pageClasses) {
      if (pageClass == topPageClass) {
        return;
      }
    }

    // do nothing if the page we want to navigate to does not exist
    boolean hasDestClass = false;
    Iterator<Page> it = mPageStack.descendingIterator();

    LOOP1:
    while (it.hasNext()) {
      Class destPageClass = it.next().getClass();

      for (Class pageClass : pageClasses) {
        if (destPageClass == pageClass) {
          hasDestClass = true;
          break LOOP1;
        }
      }
    }
    if (!hasDestClass) {
      return;
    }

    Page oldPage = mPageStack.removeLast();

    LOOP2:
    while (mPageStack.size() > 1) {
      Class lastPageClass = mPageStack.peekLast().getClass();

      for (Class pageClass : pageClasses) {
        if (lastPageClass == pageClass) {
          break LOOP2;
        }
      }

      Page page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  private void popPageInternal(final Page removedPage, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), removedPage));
    }

    removedPage.onHide();

    final Page prevPage;
    if (mPageStack.size() > 0) {    // this check is always necessary
      prevPage = mPageStack.getLast();
      prevPage.onUncover(removedPage.getReturnData());

      if (animated && !removedPage.onPopPageAnimation(removedPage.getView(), prevPage.getView(), animationDirection) && mPageAnimator != null) {
        mPageAnimator.onPopPageAnimation(removedPage.getView(), prevPage.getView(), animationDirection);
      }

      prevPage.getView().setVisibility(View.VISIBLE);
    } else {
      prevPage = null;

      if (animated && !removedPage.onPopPageAnimation(removedPage.getView(), null, animationDirection) && mPageAnimator != null) {
        mPageAnimator.onPopPageAnimation(removedPage.getView(), null, animationDirection);
      }
    }

    mViewTransparentMask.bringToFront();
    mCurPage = prevPage;

    int animationDuration = removedPage.getAnimationDuration();
    if (animationDuration == -1 && mPageAnimator != null) {
      animationDuration = mPageAnimator.getAnimationDuration();
    }
    if (animated && animationDuration != -1) {
      removedPage.postDelayed(new Runnable() {
        @Override
        public void run() {
          doFinalWorkForPopPageInternal(removedPage, prevPage);
        }
      }, animationDuration);

    } else {
      doFinalWorkForPopPageInternal(removedPage, prevPage);
    }
  }

  private void doFinalWorkForPopPageInternal(Page removedPage, Page prevPage) {
    mContainerView.removeView(removedPage.getView());
    removedPage.onHidden();

    if (prevPage != null) {
      if (prevPage == getTopPage()) {
        prevPage.getView().bringToFront();
      }
      prevPage.onUncovered(removedPage.getReturnData());
    }
  }

  public int lastIndexOfPage(Class<? extends Page> pageClass) {
    if (mPageStack.size() == 0) {
      return -1;
    }

    int index = mPageStack.size();
    Iterator<Page> it = mPageStack.descendingIterator();
    while (it.hasNext()) {
      --index;

      if (it.next().getClass() == pageClass) {
        return index;
      }
    }

    return -1;
  }

  public boolean onBackPressed() {
    if (mCurPage == null) {
      return false;
    }

    if (mCurPage.onBackPressed()) {
      return true;
    }

    // we do not pop the last page, let the activity handle this BACK-press
    if (getPageCount() > 1) {
      if (mCurPage.getType() == Page.TYPE.TYPE_DIALOG) {
        popPage(false);  // for pages of DIALOG type, do not apply animation.

      } else {
        popPage(true);
      }

      return true;
    }

    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mCurPage != null) {
      mCurPage.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void onPause() {
    if (mCurPage != null) {
      mCurPage.onPause();
    }
  }

  public void onResume() {
    if (mCurPage != null) {
      mCurPage.onResume();
    }
  }

  public void onDestroy() {
    if (mCurPage != null) {
      mCurPage.onHide();
      // we do not pop the top page, we simply call onHidden on it
      mCurPage.onHidden();
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mCurPage != null) {
      if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
        return mCurPage.onMenuPressed();

      } else {
        return mCurPage.onKeyDown(keyCode, event);
      }
    }
    return false;
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mCurPage != null) {
      return mCurPage.onKeyUp(keyCode, event);
    }
    return false;
  }

  public boolean onTouchEvent(MotionEvent event) {
    if (mCurPage != null) {
      return mCurPage.onTouchEvent(event);
    }
    return false;
  }

  public void onConfigurationChanged(Configuration newConfig) {
    for (int i = 0; i < mPageStack.size(); ++i) {
      Page p = mPageStack.get(i);
      p.onConfigurationChanged(newConfig);
    }
  }

  public void onSaveInstanceState(Bundle outState) {
    String[] clazzArray = new String[mPageStack.size()];
    for (int i = 0; i < mPageStack.size(); ++i) {
      Page p = mPageStack.get(i);
      if (p.shouldSaveInstanceState()) {
        p.onSaveInstanceState(outState);

        clazzArray[i] = p.getClass().getName();
      }
    }
    outState.putStringArray(SAVE_PAGE_STACK_KEY, clazzArray);
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    String[] clazzArray = savedInstanceState.getStringArray(SAVE_PAGE_STACK_KEY);
    Class clazz = null;
    try {
      for (int i = 0; i < clazzArray.length; ++i) {
        clazz = Class.forName(clazzArray[i]);
        Constructor ctor = clazz.getDeclaredConstructor(PageActivity.class);
        Page p = (Page) ctor.newInstance(mPageActivity);
        pushPage(p);

        p.onRestoreInstanceState(savedInstanceState);
      }
    } catch (NoSuchMethodException e) {
      Log.e("PageManager", "No <init>(PageActivity) constructor in Page: " + clazz.getName()
          + ", which is required for page restore/recovery to work.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Page getTopPage() {
    return mCurPage;
  }

  public int getPageCount() {
    return mPageStack.size();
  }

  boolean isPageKeptInStack(Page page) {
    return mPageStack.indexOf(page) != -1;
  }
}
