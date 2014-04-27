package net.neevek.paginize.lib;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

/**
 * InnerPageManager manages the InnerPages, it swaps(setPage) the pages
 * when requested. It is usually used in a Page that is designed to hold
 * multiple InnerPages.
 *
 * Date: 9/30/13
 * Time: 10:32 AM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
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
