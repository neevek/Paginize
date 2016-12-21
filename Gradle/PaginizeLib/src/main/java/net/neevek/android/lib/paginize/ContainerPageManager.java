package net.neevek.android.lib.paginize;

import android.view.View;

import java.util.HashSet;
import java.util.Set;

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
 * This class wraps the logics used by ContainerPage and ContainerInnerPage, both implement InnerPageContainer
 */
class ContainerPageManager extends InnerPageContainerManager {
  private InnerPageEventNotifier mInnerPageEventNotifier;

  private Set<InnerPage> mInnerPageSet = new HashSet<InnerPage>();

  ContainerPageManager(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  void setInnerPageEventNotifier(
      InnerPageEventNotifier innerPageEventNotifier) {
    mInnerPageEventNotifier = innerPageEventNotifier;
  }

  void setPage(InnerPage newPage) {
    InnerPage oldPage = getCurrentInnerPage();
    if (newPage == oldPage) {
      return;
    }

    if (oldPage != null) {
      oldPage.onHide();
      oldPage.onHidden();
      oldPage.getView().setVisibility(View.GONE);

      if (mInnerPageEventNotifier != null) {
        mInnerPageEventNotifier.onInnerPageHidden(oldPage);
      }
    }

    if (newPage != null) {
      View newPageView = newPage.getView();

      if (getContainerView().indexOfChild(newPageView) == -1) {
        getContainerView().addView(newPageView);
      }

      newPageView.bringToFront();
      newPageView.setVisibility(View.VISIBLE);
      newPage.onShow();
      newPage.onShown();

      if (mInnerPageEventNotifier != null) {
        mInnerPageEventNotifier.onInnerPageShown(newPage);
      }
    } else {
      getContainerView().setVisibility(View.GONE);
    }

    setCurrentInnerPage(newPage);

    if (newPage != null && !mInnerPageSet.contains(newPage)) {
      mInnerPageSet.add(newPage);
    }
  }

  // this method is rarely needed
  void removePage(InnerPage page) {
    getContainerView().removeView(page.getView());
  }

  void unsetPage() {
    setPage(null);
  }

  void onShow() {
    // Leave it empty!
    // For InnerPages in ContainerPage or ContainerInnerPage, do not mirror the onShow() callback
    // their onShow() callbacks are called when they are set in ContainerPage or ContainerInnerPage
    // see ContainerPageManager.setPage()
  }

  void onShown() {
    // Leave it empty!
    // For InnerPages in ContainerPage or ContainerInnerPage, do not mirror the onShown() callback
    // their onShown() callbacks are called when they are set in ContainerPage or ContainerInnerPage
    // see ContainerPageManager.setPage()
  }

  void onDestroy() {
    for (InnerPage innerPage : mInnerPageSet) {
      if (innerPage != null) {
        innerPage.onDestroy();
      }
    }
  }
}
