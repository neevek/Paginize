package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

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
 * @see Page
 * @see PageAnimator
 */
public final class PageManager {
  private final String SAVED_PAGE_STACK = "_paginize_page_stack";
  private final String SAVED_PAGE_BUNDLE = "_paginize_page_bundle_";
  private final String TAG = PageManager.class.getSimpleName();

  private PageActivity mPageActivity;
  private ViewGroup mContainerView;
  // a mask view that intercepts all touch events when
  // a page is in a process of pushing or popping
  private View mViewTransparentMask;

  private boolean mEnableDebug;

  // the stack to hold the pages
  private LinkedList<Page> mPageStack = new LinkedList<Page>();
  // the page on top of the stack
  private Page mCurPage;
  // the PageAnimator to animate transitions when swapping pages
  private PageAnimator mPageAnimator;
  private boolean mAnimating;
  private boolean mUseSwipePageTransitionEffect;
  private ContainerViewManager mContainerViewManager;

  private PageEventNotifier mPageEventNotifier;

  public PageManager(PageActivity pageActivity) {
    this(pageActivity, null);
  }

  public PageManager(PageActivity pageActivity, PageAnimator pageAnimator) {
    mPageActivity = pageActivity;
    mPageAnimator = pageAnimator;

    mContainerViewManager = new ContainerViewManager(this);
    mContainerView = mContainerViewManager.createContainerView(pageActivity);

    mViewTransparentMask = new View(pageActivity);
    mViewTransparentMask.setLayoutParams(
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
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

  public int getTransitionAnimationDuration() {
    if (mUseSwipePageTransitionEffect) {
      return ContainerViewManager.SWIPE_TRANSITION_ANIMATION_DURATION;
    }
    if (mPageAnimator != null) {
      return mPageAnimator.getAnimationDuration();
    }
    return 0;
  }

  /**
   * when "swipeToHide" is enabled, PageAnimator annotated on the PageActivity
   * will be disabled, the builtin swipe-to-hide transition effect will be used
   */
  public void enableSwipeToHide() {
    enableSwipeToHide(false);
  }

  /**
   * when "swipeToHide" is enabled, PageAnimator annotated on the PageActivity
   * will be disabled, the builtin swipe-to-hide transition effect will be used
   *
   * @param applyInsetsToShadow whether to apply the insets(currently only top
   *                            inset) to the shadow view, which appears when
   *                            the current page is being "dragged" or "swiped"
   *                            to hide.
   */
  public void enableSwipeToHide(boolean applyInsetsToShadow) {
    mContainerViewManager.enableSwipeToHide(applyInsetsToShadow);
  }

  /**
   * calling this method will override whatever PageAnimator specified with
   * InjectPageAnimator annotation, and use the swipe page transition effect
   */
  public void useSwipePageTransitionEffect() {
    enableSwipeToHide();
    mUseSwipePageTransitionEffect = true;
  }

  public void setPageEventNotifier(PageEventNotifier pageEventNotifier) {
    mPageEventNotifier = pageEventNotifier;
  }

  public void pushPages(Page[] pages) {
    pushPages(pages, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Page[] pages, boolean animated) {
    pushPages(pages, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Page[] pages, boolean animated,
                        PageAnimator.AnimationDirection animationDirection) {
    if (pages == null || pages.length == 0) {
      return;
    }

    Page firstOldPage = mCurPage;
    Page tmpOldPage = null;
    for (int i = 0; i < pages.length - 1; ++i) {
      pushPageInternal(pages[i], tmpOldPage, false, animationDirection);
      tmpOldPage = pages[i];
    }
    Page topPage = pages[pages.length - 1];
    topPage.setDefaultPageCountToPop(pages.length);
    pushPageInternal(topPage, firstOldPage, animated, animationDirection);
  }

  public void pushPage(Page page) {
    pushPage(page, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(Page newPage, boolean animated) {
    pushPage(newPage, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(Page newPage, boolean animated,
                       PageAnimator.AnimationDirection animationDirection) {
    if (newPage == mCurPage) {
      return;
    }

    pushPageInternal(newPage, mCurPage, animated, animationDirection);
  }

  private void pushPageInternal(
      final Page newPage, final Page oldPage, boolean animated,
      PageAnimator.AnimationDirection animationDirection) {
    if (animated) {
      mAnimating = true;
    }

    newPage.onShow();

    if (oldPage != null) {
      if (animated) {
        // when a new page is being pushed, ensures the oldPage always be
        // visible during the animation transition if animated is true
        oldPage.getView().bringToFront();
      }

      oldPage.onCover();
    }

    mCurPage = newPage;
    mPageStack.addLast(newPage);
    mContainerView.addView(newPage.getView());

    mViewTransparentMask.bringToFront();

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> pushPage, pageStack=%d, %s",
          mPageStack.size(), newPage));
    }

    if (animated && !newPage.onPushPageAnimation(
        oldPage != null ? oldPage.getView() : null,
        newPage.getView(), animationDirection)) {
      if (mUseSwipePageTransitionEffect) {
        swipeToShow(oldPage != null ? oldPage.getView() : null,
            newPage.getView(), animationDirection);
      } else if (mPageAnimator != null) {
        mPageAnimator.onPushPageAnimation(oldPage != null ?
            oldPage.getView() : null, newPage.getView(), animationDirection);
      }
    }

    int animationDuration = newPage.getAnimationDuration();
    if (animationDuration == -1) {
      animationDuration = getTransitionAnimationDuration();
    }
    if (animated && animationDuration != -1) {
      newPage.postDelayed(new Runnable() {
        @Override
        public void run() {
          doFinalWorkForPushPage(oldPage, newPage);
        }
      }, animationDuration);
    } else {
      doFinalWorkForPushPage(oldPage, newPage);
    }
  }

  private void doFinalWorkForPushPage(Page oldPage, Page newPage) {
    if (newPage == getTopPage()) {
      newPage.getView().bringToFront();
    }

    if (oldPage != null) {
      if (newPage.getType() != Page.TYPE.TYPE_DIALOG) {
        oldPage.getView().setVisibility(View.GONE);
      }

      oldPage.onCovered();
    }

    newPage.onShown();
    newPage.getView().requestFocus();
    mAnimating = false;

    if (mPageEventNotifier != null) {
      mPageEventNotifier.onPageShown(newPage);
    }
  }

  /**
   * Be cautious using this method, which will silently delete
   * Page that is not on the top, onUncover() and onUncovered() for
   * the previous page will NOT be called
   * @param index - index of the page to delete
     */
  public void deletePage(int index) {
    if (index < 0 || index >= getPageCount()) {
      return;
    }
    if (index == getPageCount() - 1) {
      popPage(false);
      return;
    }

    final Page removedPage = mPageStack.remove(index);
    removedPage.onHide();
    // without using post here, removeView will cause flicker
    removedPage.getView().post(new Runnable() {
        @Override
        public void run() {
          mContainerView.removeView(removedPage.getView());
        }
    });
    removedPage.onHidden();
    removedPage.onDestroy();

    if (mPageEventNotifier != null) {
      mPageEventNotifier.onPageHidden(removedPage);
    }

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> deletePage(%d), pageStack=%d, %s",
              index, mPageStack.size(), mCurPage));
    }
  }

  public void popPage(boolean animated) {
    popTopNPages(1, animated);
  }

  /**
   * @param animated true to animate the transition
   */
  public void popPage(boolean animated,
                      PageAnimator.AnimationDirection animationDirection) {
    popTopNPages(1, animated, animationDirection);
  }

  public void popTopNPages(int n, boolean animated) {
    popTopNPages(n, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  public void popTopNPages(int n, boolean animated,
                           PageAnimator.AnimationDirection animationDirection) {
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
      page.onDestroy();

      if (mPageEventNotifier != null) {
        mPageEventNotifier.onPageHidden(page);
      }

      if (mEnableDebug) {
        Log.d(TAG, String.format(">>>> popPage, pageStack=%d, %s",
            mPageStack.size(), page));
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
  public void popToPage(Page destPage, boolean animated,
                        PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (destPage == null) {
      throw new IllegalArgumentException(
          "cannot call popToPage() with null destPage.");
    }

    if (mPageStack.size() <= 0 ||
        mPageStack.lastIndexOf(destPage) == -1 ||
        mPageStack.peekLast() == destPage) {
      return;
    }

    Page oldPage = mPageStack.removeLast();

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pageStack=%d, %s",
          mPageStack.size(), oldPage));
    }

    while (mPageStack.size() > 1) {
      if (mPageStack.peekLast() == destPage) {
        break;
      }

      Page page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();
      page.onDestroy();

      if (mPageEventNotifier != null) {
        mPageEventNotifier.onPageHidden(page);
      }
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
  public void popToClass(Class<? extends Page> pageClass, boolean animated,
                         PageAnimator.AnimationDirection animationDirection) {
    if (pageClass == null) {
      throw new IllegalArgumentException(
          "cannot call popToClass() with null pageClass.");
    }

    popToClasses(new Class[] {pageClass}, animated, animationDirection);
  }

  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated) {
    popToClasses(pageClasses, animated,
        PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends when one of the classes specified by pageClasses is
   * found, if none of the classes is found, the method call is a no-op
   *
   * @param pageClasses classes of pages as the destination for this pop operation
   * @param animated    true to animate the transition
   */
  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated,
                           PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (pageClasses == null || pageClasses.length == 0) {
      throw new IllegalArgumentException("cannot call popToClasses() " +
          "with null or empty pageClasses.");
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
      page.onDestroy();

      if (mPageEventNotifier != null) {
        mPageEventNotifier.onPageHidden(page);
      }
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  private void popPageInternal(
      final Page removedPage, boolean animated,
      PageAnimator.AnimationDirection animationDirection) {
    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s",
          mPageStack.size(), removedPage));
    }

    removedPage.onHide();

    final Page prevPage;
    if (mPageStack.size() > 0) {    // this check is always necessary
      prevPage = mPageStack.getLast();
      prevPage.onUncover(removedPage.getReturnData());

      if (animated) {
        // for TYPE_DIALOG, only custom animation is used
        if (removedPage.getType() != Page.TYPE.TYPE_DIALOG &&
            mContainerViewManager.canSwipeToHide() &&
            removedPage.getView().getLeft() > 0) {
          swipeToHide(removedPage.getView(), prevPage.getView(),
              animationDirection, false);
        } else if (!removedPage.onPopPageAnimation(removedPage.getView(),
            prevPage.getView(), animationDirection)) {
          if (mUseSwipePageTransitionEffect) {
            swipeToHide(removedPage.getView(), prevPage.getView(),
                animationDirection, true);
          } else if (mPageAnimator != null) {
            mPageAnimator.onPopPageAnimation(removedPage.getView(),
                prevPage.getView(), animationDirection);
          }
        }
      }

      prevPage.getView().setVisibility(View.VISIBLE);
    } else {
      prevPage = null;

      // the order matters here
      if (animated &&
          !removedPage.onPopPageAnimation(removedPage.getView(), null, animationDirection) &&
          removedPage.getType() != Page.TYPE.TYPE_DIALOG &&
          mPageAnimator != null) {
        mPageAnimator.onPopPageAnimation( removedPage.getView(), null, animationDirection);
      }
    }

    mViewTransparentMask.bringToFront();
    mCurPage = prevPage;

    int animationDuration = removedPage.getAnimationDuration();
    if (animationDuration == -1) {
      animationDuration = getTransitionAnimationDuration();
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
    removedPage.onDestroy();
    if (mPageEventNotifier != null) {
      mPageEventNotifier.onPageHidden(removedPage);
    }

    if (prevPage != null) {
      if (prevPage == getTopPage()) {
        prevPage.getView().bringToFront();
      }
      prevPage.onUncovered(removedPage.getReturnData());
    }
  }

  private void swipeToShow(final View oldPageView, final View newPageView,
                           PageAnimator.AnimationDirection animationDirection) {
    mContainerViewManager.animateShadowViewForShowing(animationDirection);

    if (animationDirection == PageAnimator.AnimationDirection.FROM_RIGHT) {
      if (oldPageView != null) {
        mContainerViewManager.animateView(
            oldPageView, Animation.RELATIVE_TO_PARENT, 0,
            Animation.RELATIVE_TO_PARENT, -0.5f, true, null);
      }
      mContainerViewManager.animateView(
          newPageView, Animation.RELATIVE_TO_PARENT, 1,
          Animation.RELATIVE_TO_PARENT, 0, true, null);

    } else {
      if (oldPageView != null) {
        mContainerViewManager.animateView(
            oldPageView, Animation.RELATIVE_TO_PARENT, -0.5f,
            Animation.RELATIVE_TO_PARENT, 0, true, null);
      }
      mContainerViewManager.animateView(
          newPageView, Animation.RELATIVE_TO_PARENT, -1,
          Animation.RELATIVE_TO_PARENT, 0, true, null);
    }
  }

  private void swipeToHide(final View oldPageView, final View newPageView,
                           PageAnimator.AnimationDirection animationDirection,
                           final boolean cacheAnimationObj) {
    int oldPageViewLeft = oldPageView.getLeft();

    if (animationDirection == PageAnimator.AnimationDirection.FROM_LEFT) {
      mContainerViewManager.animateShadowViewForHiding(oldPageViewLeft, animationDirection);
      mContainerViewManager.animateView(oldPageView, Animation.ABSOLUTE, oldPageViewLeft,
          Animation.RELATIVE_TO_PARENT, 1, cacheAnimationObj, null);
      if (newPageView != null) {
        int newPageViewLeft = newPageView.getLeft();
        if (newPageViewLeft >= 0) {
          newPageViewLeft = -mContainerView.getWidth() / 2;
        }

        mContainerViewManager.animateView(
            newPageView, Animation.ABSOLUTE, newPageViewLeft,
            Animation.RELATIVE_TO_PARENT, 0, cacheAnimationObj, null);
      }

    } else {
      mContainerViewManager.animateView(
          oldPageView, Animation.RELATIVE_TO_PARENT, 0,
          Animation.RELATIVE_TO_PARENT, -1, cacheAnimationObj, null);
      if (newPageView != null) {
        mContainerViewManager.animateView(
            newPageView, Animation.RELATIVE_TO_PARENT, 0.5f,
            Animation.RELATIVE_TO_PARENT, 0, cacheAnimationObj, null);
      }
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
      popTopNPages(mCurPage.getDefaultPageCountToPop(), true);
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
      mCurPage.onDestroy();

      if (mPageEventNotifier != null) {
        mPageEventNotifier.onPageHidden(mCurPage);
      }
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
    final String[] clsArray = new String[mPageStack.size()];
    for (int i = 0; i < mPageStack.size(); ++i) {
      final Page p = mPageStack.get(i);
      p.onSaveInstanceState(p.getBundle());

      final String clsName = p.getClass().getName();
      clsArray[i] = clsName;

      final String key = SAVED_PAGE_BUNDLE + i + clsName;
      outState.putBundle(key, p.getBundle());
    }
    outState.putStringArray(SAVED_PAGE_STACK, clsArray);
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    final String[] clsArray = savedInstanceState.getStringArray(SAVED_PAGE_STACK);
    if (clsArray == null) {
      return;
    }

    Class cls = null;
    try {
      for (int i = 0; i < clsArray.length; ++i) {
        final String clsName = clsArray[i];
        final String key = SAVED_PAGE_BUNDLE + i + clsName;

        cls = Class.forName(clsName);
        final Constructor ctor = cls.getDeclaredConstructor(PageActivity.class);
        ctor.setAccessible(true);
        final Page p = (Page) ctor.newInstance(mPageActivity);
        Bundle bundle = savedInstanceState.getBundle(key);
        p.setBundle(bundle);
        p.onRestoreInstanceState(bundle);

        pushPage(p);
      }
    } catch (NoSuchMethodException e) {
      Log.e("PageManager", "No <init>(PageActivity) constructor in Page: " +
          cls.getName() +
          ", which is required for page restore/recovery to work.");

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

  public Page getPage(int index) {
    if (index < 0 || index >= mPageStack.size()) {
      throw new IllegalArgumentException("invalid index");
    }

    return mPageStack.get(index);
  }

  public int indexOfPage(Page page) {
    return mPageStack.indexOf(page);
  }

  boolean isPageKeptInStack(Page page) {
    return mPageStack.indexOf(page) != -1;
  }

  LinkedList<Page> getPageStack() {
    return mPageStack;
  }
}
