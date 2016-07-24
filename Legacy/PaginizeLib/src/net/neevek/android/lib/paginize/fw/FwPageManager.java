package net.neevek.android.lib.paginize.fw;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.FrameLayout;
import net.neevek.android.lib.paginize.anim.PageAnimator;

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
 * FwPageManager manages the pages(of type FwPage), it swaps(push and pop) the pages
 * when requested, it uses PageAnimationManager to animate the transition when
 * swapping Pages.
 *
 * @see FwPage
 * @see PageAnimator
 */
public final class FwPageManager {
  private final String TAG = FwPageManager.class.getSimpleName();

  private Context mContext;
  private WindowManager mWindowManager;
  private ViewGroup mContainerView;
  // a mask view that intercepts all touch events when a page is in a process of pushing or popping
  private View mViewTransparentMask;

  private boolean mEnableDebug;

  // the stack to hold the pages
  private LinkedList<FwPage> mPageStack = new LinkedList<FwPage>();
  // the page on top of the stack
  private FwPage mCurPage;
  // the PageAnimator to animate transitions when swapping pages
  private PageAnimator mPageAnimator;
  private boolean mAnimating;

  private FwWindowType mWindowType;
  private FwPageManagerListener mFwPageManagerListener;
  private Object mUserData;

  public FwPageManager(Context context) {
    this(context, null, FwWindowType.GLOBAL);
  }

  public FwPageManager(Context context, PageAnimator pageAnimator, FwWindowType windowType) {
    mPageAnimator = pageAnimator;
    mWindowType = windowType;

    if (windowType == FwWindowType.APPLICATION) {
      mContext = context;
    } else {
      mContext = context.getApplicationContext();
    }

    mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

    initContainerView();
  }

  private void initContainerView() {
    mContainerView = new FrameLayout(mContext) {
      @Override
      public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) && event.getAction() == KeyEvent.ACTION_UP) {
          onBackPressed();
          return true;
        }
        return super.dispatchKeyEvent(event);
      }
    };
  }

  private void addContainerViewIfNeeded() {
    if (mContainerView.getParent() != null) {
      return;
    }

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            mWindowType == FwWindowType.GLOBAL ? WindowManager.LayoutParams.TYPE_SYSTEM_ALERT : WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);

    lp.gravity = Gravity.TOP | Gravity.LEFT;

    mWindowManager.addView(mContainerView, lp);

    mViewTransparentMask = new View(mContext);
//    mViewTransparentMask.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//    mViewTransparentMask.setOnTouchListener(new View.OnTouchListener() {
//      @Override
//      public boolean onTouch(View v, MotionEvent event) {
//        // intercept all touch events
//        return true;
//      }
//    });
//    mViewTransparentMask.setBackgroundColor(0x00ffffff);
//    mContainerView.addView(mViewTransparentMask);
  }

  public void setUserData(Object userData) {
    mUserData = userData;
  }

  public Object getUserData() {
    return mUserData;
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

  public Context getContext() {
    return mContext;
  }

  public void setPageManagerListener(FwPageManagerListener fwPageManagerListener) {
    mFwPageManagerListener = fwPageManagerListener;
  }

  public void pushPages(FwPage[] pages) {
    pushPages(pages, null, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(FwPage[] pages, Object arg, boolean animated) {
    pushPages(pages, arg, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  /**
   * 'arg' will be passed to the last page in the array
   **/
  public void pushPages(FwPage[] pages, Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pages == null || pages.length == 0) {
      return;
    }

    FwPage firstOldPage = mCurPage;
    FwPage tmpOldPage = null;
    for (int i = 0; i < pages.length - 1; ++i) {
      pushPageInternal(pages[i], tmpOldPage, null, false, animationDirection);
      tmpOldPage = pages[i];
    }
    pushPageInternal(pages[pages.length - 1], firstOldPage, arg, animated, animationDirection);
  }

  public void pushPages(Pair<FwPage, Object>[] pagePacks) {
    pushPages(pagePacks, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Pair<FwPage, Object>[] pagePacks, boolean animated) {
    pushPages(pagePacks, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPages(Pair<FwPage, Object>[] pagePacks, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pagePacks == null || pagePacks.length == 0) {
      return;
    }

    FwPage firstOldPage = mCurPage;
    FwPage tmpOldPage = null;
    for (int i = 0; i < pagePacks.length - 1; ++i) {
      Pair<FwPage, Object> pagePack = pagePacks[i];
      if (pagePack.first == null) {
        continue;
      }
      pushPageInternal(pagePack.first, tmpOldPage, pagePack.second, false, animationDirection);
      tmpOldPage = pagePack.first;
    }

    Pair<FwPage, Object> lastPagePack = pagePacks[pagePacks.length - 1];
    if (lastPagePack.first != null) {
      pushPageInternal(lastPagePack.first, firstOldPage, lastPagePack.second, animated, animationDirection);
    }
  }

  public void pushPage(FwPage page) {
    pushPage(page, null, false, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(FwPage newPage, Object arg, boolean animated) {
    pushPage(newPage, arg, animated, PageAnimator.AnimationDirection.FROM_RIGHT);
  }

  public void pushPage(FwPage newPage, Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (newPage == mCurPage) {
      return;
    }

    pushPageInternal(newPage, mCurPage, arg, animated, animationDirection);
  }

  private void pushPageInternal(final FwPage newPage, final FwPage oldPage, final Object arg, boolean animated, PageAnimator.AnimationDirection animationDirection) {
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

    addContainerViewIfNeeded();

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

  private void doFinalWorkForPushPage(FwPage oldPage, FwPage newPage, Object arg) {
    if (newPage == getTopPage()) {
      newPage.getView().bringToFront();
    }

    if (oldPage != null) {
      if (newPage.getType() != FwPage.TYPE.TYPE_DIALOG) {
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

    FwPage oldPage = mPageStack.removeLast();
    --n;    // for mPageStack.removeLast() above

    while (--n >= 0) {
      FwPage page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();

      if (mEnableDebug) {
        Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), page));
      }
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  public void popToPage(FwPage destPage, boolean animated) {
    popToPage(destPage, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends if destPage is found,
   * if destPage is not found, the method call is a no-op
   *
   * @param destPage page as the destination for this pop operation
   * @param animated true to animate the transition
   */
  public void popToPage(FwPage destPage, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mAnimating) {
      return;
    }
    if (destPage == null) {
      throw new IllegalArgumentException("cannot call popToPage() with null destPage.");
    }

    if (mPageStack.size() <= 0 || mPageStack.lastIndexOf(destPage) == -1 || mPageStack.peekLast() == destPage) {
      return;
    }

    FwPage oldPage = mPageStack.removeLast();

    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), oldPage));
    }

    while (mPageStack.size() > 1) {
      if (mPageStack.peekLast() == destPage) {
        break;
      }

      FwPage page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  public void popToClass(Class<? extends FwPage> pageClass, boolean animated) {
    popToClass(pageClass, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends if the pageClass is found,
   * if the class is not found, the method call is a no-op
   *
   * @param pageClass class of page as the destination for this pop operation
   * @param animated  true to animate the transition
   */
  public void popToClass(Class<? extends FwPage> pageClass, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (pageClass == null) {
      throw new IllegalArgumentException("cannot call popToClass() with null pageClass.");
    }

    popToClasses(new Class[]{pageClass}, animated, animationDirection);
  }

  public void popToClasses(Class<? extends FwPage>[] pageClasses, boolean animated) {
    popToClasses(pageClasses, animated, PageAnimator.AnimationDirection.FROM_LEFT);
  }

  /**
   * "pop" operation ends when one of the classes specified by pageClasses is found,
   * if none of the classes is found, the method call is a no-op
   *
   * @param pageClasses classes of pages as the destination for this pop operation
   * @param animated    true to animate the transition
   */
  public void popToClasses(Class<? extends FwPage>[] pageClasses, boolean animated, PageAnimator.AnimationDirection animationDirection) {
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
    Iterator<FwPage> it = mPageStack.descendingIterator();

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

    FwPage oldPage = mPageStack.removeLast();

    LOOP2:
    while (mPageStack.size() > 1) {
      Class lastPageClass = mPageStack.peekLast().getClass();

      for (Class pageClass : pageClasses) {
        if (lastPageClass == pageClass) {
          break LOOP2;
        }
      }

      FwPage page = mPageStack.removeLast();
      page.onHide();
      mContainerView.removeView(page.getView());
      page.onHidden();
    }

    popPageInternal(oldPage, animated, animationDirection);
  }

  private void popPageInternal(final FwPage removedPage, boolean animated, PageAnimator.AnimationDirection animationDirection) {
    if (mEnableDebug) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), removedPage));
    }

    removedPage.onHide();

    final FwPage prevPage;
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

  private void doFinalWorkForPopPageInternal(FwPage removedPage, FwPage prevPage) {
    mContainerView.removeView(removedPage.getView());
    removedPage.onHidden();

    if (prevPage != null) {
      if (prevPage == getTopPage()) {
        prevPage.getView().bringToFront();
      }
      prevPage.onUncovered(removedPage.getReturnData());
    }

    if (getPageCount() == 0) {
      onPageStackCleared();
    }
  }

  public int lastIndexOfPage(Class<? extends FwPage> pageClass) {
    if (mPageStack.size() == 0) {
      return -1;
    }

    int index = mPageStack.size();
    Iterator<FwPage> it = mPageStack.descendingIterator();
    while (it.hasNext()) {
      --index;

      if (it.next().getClass() == pageClass) {
        return index;
      }
    }

    return -1;
  }

  public void clearPageStack() {
    popTopNPages(getPageCount(), false);
    onPageStackCleared();
  }

  public void onBackPressed() {
    if (mCurPage == null) {
      return;
    }

    if (mCurPage.onBackPressed()) {
      return;
    }

    // we do not pop the last page, let the activity handle this BACK-press
    if (getPageCount() > 0) {
      if (mCurPage.getType() == FwPage.TYPE.TYPE_DIALOG) {
        popPage(false);  // for pages of DIALOG type, do not apply animation.

      } else {
        popPage(true);
      }

    }
  }

  private void onPageStackCleared() {
    if (mContainerView.getParent() != null) {
      mWindowManager.removeView(mContainerView);

      if (mFwPageManagerListener != null) {
        mFwPageManagerListener.onPageStackCleared();
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

  public FwPage getTopPage() {
    return mCurPage;
  }

  public int getPageCount() {
    return mPageStack.size();
  }

  boolean isPageKeptInStack(FwPage page) {
    return mPageStack.indexOf(page) != -1;
  }
}
