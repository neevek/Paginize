package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

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
 * InnerPageManager manages InnerPages, it swaps(setPage) the pages
 * when requested. It is used to manage InnerPages in an InnerPageContainer.
 *
 * @see net.neevek.android.lib.paginize.InnerPage
 */
public class InnerPageManager {
  private final String SAVE_INNER_PAGE_MANAGER_KEY = "_paginize_inner_page_manager_" + getClass().getName();
  private PageActivity mPageActivity;
  private ViewGroup mContainerView;

  private InnerPage mCurPage;

  public InnerPageManager(PageActivity pageActivity, ViewGroup containerView) {
    mPageActivity = pageActivity;
    mContainerView = containerView;
  }

  public void setPage(InnerPage newPage, Object data) {
    InnerPage oldPage = mCurPage;
    if (newPage == oldPage) {
      return;
    }

    if (oldPage != null) {
      oldPage.onReplaced();
      oldPage.getView().setVisibility(View.GONE);

      View currentFocusedView = oldPage.getContext().getCurrentFocus();
      if (currentFocusedView != null) {
        currentFocusedView.clearFocus();
      }
    }

    if (newPage != null) {
      View newPageView = newPage.getView();

      if (mContainerView.indexOfChild(newPageView) == -1) {
        mContainerView.addView(newPageView);
        newPage.onAttached();
      }

      newPageView.bringToFront();
      newPageView.setVisibility(View.VISIBLE);
      newPage.onSet(data);
      newPageView.requestFocus();
    }

    mCurPage = newPage;
  }

  // this method is rarely needed
  public void removePage(InnerPage page) {
    mContainerView.removeView(page.getView());
    page.onDetached();
  }

  public void unsetPage() {
    setPage(null, null);
  }

  public boolean onBackPressed() {
    if (mCurPage != null) {
      return mCurPage.onBackPressed();
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

      outState.putString(SAVE_INNER_PAGE_MANAGER_KEY, mCurPage.getClass().getName());
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    String clazzString = savedInstanceState.getString(SAVE_INNER_PAGE_MANAGER_KEY);

    Class clazz = null;
    try {
      clazz = Class.forName(clazzString);
      Constructor ctor = clazz.getDeclaredConstructor(PageActivity.class);
      InnerPage p = (InnerPage) ctor.newInstance(mPageActivity);
      setPage(p, null);

      p.onRestoreInstanceState(savedInstanceState);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("No <init>(PageActivity) constructor in InnerPage: " + clazz.getName()
          + ", which is required for page restore/recovery to work.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onShow(Object arg) {
    if (mCurPage != null) {
      mCurPage.onShow(arg);
    }
  }

  public void onShown(Object arg) {
    if (mCurPage != null) {
      mCurPage.onShown(arg);
    }
  }

  public void onHide() {
    if (mCurPage != null) {
      mCurPage.onHide();
    }
  }

  public void onHidden() {
    if (mCurPage != null) {
      mCurPage.onHidden();
    }
  }

  public void onCover() {
    if (mCurPage != null) {
      mCurPage.onCover();
    }
  }

  public void onCovered() {
    if (mCurPage != null) {
      mCurPage.onCovered();
    }
  }

  public void onUncover(Object arg) {
    if (mCurPage != null) {
      mCurPage.onUncover(arg);
    }
  }

  public void onUncovered(Object arg) {
    if (mCurPage != null) {
      mCurPage.onUncovered(arg);
    }
  }

  public InnerPage getCurrentPage() {
    return mCurPage;
  }
}
