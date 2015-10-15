package net.neevek.android.lib.paginize;

import android.view.View;

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
//    private final String SAVE_INNER_PAGE_MANAGER_KEY = "_paginize_inner_page_manager_" + getClass().getName();
    private ViewWrapper mInnerPageContainer;

    public ContainerPageManager(ViewWrapper innerPageContainer) {
        super(innerPageContainer);
        mInnerPageContainer = innerPageContainer;
    }

    public void setPage(InnerPage newPage, Object data) {
        InnerPage oldPage = getCurrentInnerPage();
        if (newPage == oldPage) {
            return;
        }

        if (oldPage != null) {
            oldPage.onHide();
            oldPage.onHidden();
            oldPage.getView().setVisibility(View.GONE);
        }

        if (newPage != null) {
            View newPageView = newPage.getView();

            if (getContainerView().indexOfChild(newPageView) == -1) {
                getContainerView().addView(newPageView);
            }

            newPageView.bringToFront();
            newPageView.setVisibility(View.VISIBLE);
            newPage.onShow(data);
            newPage.onShown(data);
        } else {
            getContainerView().setVisibility(View.GONE);
        }

        setCurrentInnerPage(newPage);
    }

    // this method is rarely needed
    public void removePage(InnerPage page) {
        getContainerView().removeView(page.getView());
    }

    public void unsetPage() {
        setPage(null, null);
    }

    public void onShow(Object arg) {
        // Leave it empty!
        // For InnerPages in ContainerPage or ContainerInnerPage, do not mirror the onShow() callback
        // their onShow() callbacks are called when they are set in ContainerPage or ContainerInnerPage
        // see ContainerPageManager.setPage()
    }

    public void onShown(Object arg) {
        // Leave it empty!
        // For InnerPages in ContainerPage or ContainerInnerPage, do not mirror the onShown() callback
        // their onShown() callbacks are called when they are set in ContainerPage or ContainerInnerPage
        // see ContainerPageManager.setPage()
    }

    // for ContainerPage or ContainerInnerPage, re-recreating the current inner page may NOT be desired
    // in most cases, because inner pages are normally created in the subclass.
//    public void onSaveInstanceState(Bundle outState) {
//        InnerPage currentPage = getCurrentInnerPage();
//        if (currentPage != null && currentPage.shouldSaveInstanceState()) {
//            currentPage.onSaveInstanceState(outState);
//
//            outState.putString(SAVE_INNER_PAGE_MANAGER_KEY, currentPage.getClass().getName());
//        }
//    }
//
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        String clazzString = savedInstanceState.getString(SAVE_INNER_PAGE_MANAGER_KEY);
//
//        Class clazz = null;
//        try {
//            clazz = Class.forName(clazzString);
//            Constructor ctor = clazz.getDeclaredConstructor(ViewWrapper.class);
//            InnerPage p = (InnerPage) ctor.newInstance(mInnerPageContainer);
//            setPage(p, null);
//
//            p.onRestoreInstanceState(savedInstanceState);
//        } catch (NoSuchMethodException e) {
//            Log.e("Paginize", "No <init>(PageActivity) constructor in InnerPage: " + clazz.getName()
//                    + ", which is required for page restore/recovery to work.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
