package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
 * PageManager manages the pages(of type Page), it swaps(push and pop) the pages
 * when requested, it uses PageAnimationManager to animate the transition when
 * swapping Pages.
 *
 * @see net.neevek.android.lib.paginize.Page
 * @see net.neevek.android.lib.paginize.anim.PageAnimator
 */
public class PageManager {
  private final String TAG = PageManager.class.getSimpleName();
  private final boolean DEBUG = true;

  private ViewGroup mContainerView;

  // the stack to hold the pages
  private LinkedList<Page> mPageStack = new LinkedList<Page>();

  // the page on top of the stack
  private Page mCurPage;

  // the PageAnimator to use to animate transitions when swapping pages
  private PageAnimator mPageAnimator;

  public PageManager(ViewGroup containerView) {
    this(containerView, null);
  }

  public PageManager(ViewGroup containerView, PageAnimator pageAnimator) {
    mContainerView = containerView;
    mPageAnimator = pageAnimator;
  }

  public void setPageAnimator(PageAnimator pageAnimator) {
    mPageAnimator = pageAnimator;
  }

  public PageAnimator getPageAnimator() {
    return mPageAnimator;
  }

  public void pushPage(Page page) {
    pushPage(page, null, false);
  }

  public void pushPage(Page page, Object arg, boolean animated) {
    pushPage(page, arg, animated, false);
  }

  public void pushPage(final Page newPage, final Object arg, boolean animated, boolean hint) {
    if (newPage == mCurPage) {
      return;
    }

    final Page oldPage = mCurPage;

    mCurPage = newPage;
    mPageStack.addLast(newPage);
    mContainerView.addView(newPage.getView());
    newPage.onAttach();

    if (DEBUG) {
      Log.d(TAG, String.format(">>>> pushPage, pagestack=%d, %s, arg=%s", mPageStack.size(), newPage, arg));
    }

    if (oldPage != null) {
      if (newPage.keepSingleInstance() && newPage.getClass() == oldPage.getClass()) {
        mPageStack.removeLastOccurrence(oldPage);
        mContainerView.removeView(oldPage.getView());
        oldPage.onDetach();
        oldPage.onHidden();

      } else {
        oldPage.onCovered();
      }
    }

    if (animated && mPageAnimator != null && !newPage.onPushPageAnimation(oldPage, newPage, hint)) {
      mPageAnimator.onPushPageAnimation(oldPage, newPage, hint);
    }

    int animationDuration = newPage.getAnimationDuration();
    if (animationDuration == -1 && mPageAnimator != null) {
      animationDuration = mPageAnimator.getAnimationDuration();
    }
    if (animated && animationDuration != -1) {
      newPage.postDelayed(new Runnable() {
        @Override
        public void run() {
          hideOldPageIfNeeded(oldPage, newPage);

          newPage.onShown(arg);
        }
      }, animationDuration);
    } else {
      hideOldPageIfNeeded(oldPage, newPage);

      newPage.onShown(arg);
    }
  }

  private void hideOldPageIfNeeded(Page oldPage, Page newPage) {
    if (oldPage != null && newPage.getType() != Page.TYPE.TYPE_DIALOG) {
      oldPage.getView().setVisibility(View.GONE);
    }
  }

  public void popPage(boolean animated) {
    popPage(animated, false);
  }

  /**
   * @param animated true to animate the transition
   * @param hint     true=left, false=right
   */
  public void popPage(boolean animated, boolean hint) {
    popTopNPages(1, animated, hint);
  }

  public void popTopNPages(int n, boolean animated, boolean hint) {
    if (n <= 0 || mPageStack.size() <= 0) {
      return;
    }

    Page oldPage = mPageStack.removeLast();
    --n;    // for mPageStack.removeLast() above

    while (--n >= 0) {
      Page page = mPageStack.removeLast();
      mContainerView.removeView(page.getView());
      page.onDetach();
      page.onHidden();

      if (DEBUG) {
        Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), page));
      }
    }

    popPageInternal(oldPage, animated, hint);
  }

  public void popToPage(Page destPage, boolean animated) {
    popToPage(destPage, animated, false);
  }

  /**
   * "pop" operation ends if destPage is found,
   * if destPage is not found, the method call is a no-op
   *
   * @param destPage page as the destination for this pop operation
   * @param animated true to animate the transition
   * @param hint used by the PageAnimator
   */
  public void popToPage(Page destPage, boolean animated, boolean hint) {
    if (destPage == null) {
      throw new IllegalArgumentException("cannot call popToPage() with null destPage.");
    }

    if (mPageStack.size() <= 0 || mPageStack.lastIndexOf(destPage) == -1 || mPageStack.peekLast() == destPage) {
      return;
    }

    Page oldPage = mPageStack.removeLast();

    if (DEBUG) {
      Log.d(TAG, String.format(">>>> popPage, pagestack=%d, %s", mPageStack.size(), oldPage));
    }

    while (mPageStack.size() > 1) {
      if (mPageStack.peekLast() == destPage) {
        break;
      }

      Page page = mPageStack.removeLast();
      mContainerView.removeView(page.getView());
      page.onDetach();
      page.onHidden();
    }

    popPageInternal(oldPage, animated, hint);
  }

  public void popToClass(Class<? extends Page> pageClass, boolean animated) {
    popToClass(pageClass, animated, false);
  }

  /**
   * "pop" operation ends if the pageClass is found,
   * if the class is not found, the method call is a no-op
   *
   * @param pageClass class of page as the destination for this pop operation
   * @param animated true to animate the transition
   * @param hint used by the PageAnimator
   */
  public void popToClass(Class<? extends Page> pageClass, boolean animated, boolean hint) {
    if (pageClass == null) {
      throw new IllegalArgumentException("cannot call popToClass() with null pageClass.");
    }

    popToClasses(new Class[]{pageClass}, animated, hint);
  }

  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated) {
    popToClasses(pageClasses, animated, false);
  }

  /**
   * "pop" operation ends when one of the classes specified by pageClasses is found,
   * if none of the classes is found, the method call is a no-op
   *
   * @param pageClasses classes of pages as the destination for this pop operation
   * @param animated true to animate the transition
   * @param hint used by the PageAnimator
   */
  public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated, boolean hint) {
    if (pageClasses == null || pageClasses.length == 0) {
      throw new IllegalArgumentException("cannot call popToClasses() with null or empty pageClasses.");
    }

    if (mPageStack.size() <= 0) {
      return;
    }

    // is topPage the page we want to navigate to? if so, we do not need to do anything
    Class topPageClass = mPageStack.peekLast().getClass();
    for (Class pageClass : pageClasses) {
      if (pageClass == topPageClass) {
        return;
      }
    }

    // the page we want to navigate to does not exist? if so, we do not need to do anything
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
      mContainerView.removeView(page.getView());
      page.onDetach();
      page.onHidden();
    }

    popPageInternal(oldPage, animated, hint);
  }

  private void popPageInternal(final Page removedPage, boolean animated, boolean hint) {
    final Page prevPage;
    if (mPageStack.size() > 0) {    // this check is always necessary
      prevPage = mPageStack.getLast();

      if (animated && mPageAnimator != null && !removedPage.onPopPageAnimation(removedPage, prevPage, hint)) {
        mPageAnimator.onPopPageAnimation(removedPage, prevPage, hint);
      }

      prevPage.getView().setVisibility(View.VISIBLE);
    } else {
      prevPage = null;

      if (animated && mPageAnimator != null && !removedPage.onPopPageAnimation(removedPage, null, hint)) {
        mPageAnimator.onPopPageAnimation(removedPage, null, hint);
      }
    }

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
    removedPage.onDetach();
    removedPage.onHidden();
    mCurPage = prevPage;

    if (prevPage != null) {
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
        popPage(false, false);  // for pages of DIALOG type, do not apply animation.

      } else {
        popPage(true, true);
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
    if (mCurPage != null) {
      mCurPage.onConfigurationChanged(newConfig);
    }
  }

  public void onSaveInstanceState(Bundle outState) {
    if (mCurPage != null) {
      mCurPage.onSaveInstanceState(outState);
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (mCurPage != null) {
      mCurPage.onRestoreInstanceState(savedInstanceState);
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
