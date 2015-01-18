package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.view.View;
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
 * InnerPageManager manages the InnerPages, it swaps(setPage) the pages
 * when requested. It is usually used in a Page that is designed to hold
 * multiple InnerPages.
 *
 * @see net.neevek.android.lib.paginize.InnerPage
 */
public class InnerPageManager {
  private ViewGroup mContainerView;

  private InnerPage mCurPage;

  public InnerPageManager(ViewGroup containerView) {
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
    }

    if (newPage != null) {
      View newPageView = newPage.getView();

      if (mContainerView.indexOfChild(newPageView) == -1) {
        mContainerView.addView(newPageView);
        newPage.onAttach();
      }

      newPageView.bringToFront();
      newPageView.setVisibility(View.VISIBLE);
      newPage.onSet(data);
    }

    mCurPage = newPage;
  }

  // this method is rarely needed
  public void removePage(InnerPage page) {
    mContainerView.removeView(page.getView());
    page.onDetach();
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

  public void onShown(Object arg) {
    if (mCurPage != null) {
      mCurPage.onShown(arg);
    }
  }

  public void onHidden() {
    if (mCurPage != null) {
      mCurPage.onHidden();
    }
  }

  public void onCovered() {
    if (mCurPage != null) {
      mCurPage.onCovered();
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
