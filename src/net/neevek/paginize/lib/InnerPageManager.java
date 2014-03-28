package net.neevek.paginize.lib;

import android.content.Intent;
import android.os.Build;
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

    public void setPage(InnerPage page, Object data) {
        if (page == mCurPage) {
            return;
        }

        if (mCurPage != null) {
            mCurPage.onReplaced();
        }

        if (mContainerView.getChildCount() > 0)
            mContainerView.removeViewAt(0);

        mCurPage = page;
        if (mCurPage != null) {
            mCurPage.getView().requestFocus();
            if (Build.VERSION.SDK_INT <= 15) {
                // this is a hack that fixed a problem that on some 4.0.4 devices(Galaxy S3, MIUI 4.0.4)
                // ListView items may not be clickable when the View is brought to the top
                mCurPage.getView().setVisibility(View.GONE);
                mCurPage.getView().setVisibility(View.VISIBLE);
            }
        }

        if (page != null) {
            View currentPageView = mCurPage.getView();
            mContainerView.addView(currentPageView);
            mCurPage.onSet(data);
        }
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
