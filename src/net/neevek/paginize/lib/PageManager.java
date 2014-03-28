package net.neevek.paginize.lib;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.paginize.lib.anim.PageAnimationManager;

import java.util.LinkedList;

/**
 * PageManager manages the pages(of type Page), it swaps(push and pop) the pages
 * when requested, it uses PageAnimationManager to animate the transition when
 * swapping Pages.
 *
 * Date: 9/30/13
 * Time: 10:32 AM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
 */
public class PageManager {
    private ViewGroup mContainerView;

    private LinkedList<Page> mPageStack = new LinkedList<Page>();
    private Page mCurPage;

    private PageAnimationManager mPageAnimationManager;

    public PageManager(ViewGroup containerView) {
        this(containerView, null);
    }

    public PageManager(ViewGroup containerView, PageAnimationManager pageAnimationManager) {
        mContainerView = containerView;
        mPageAnimationManager = pageAnimationManager;
    }

    public void setPageAnimationManager(PageAnimationManager pageAnimationManager) {
        mPageAnimationManager = pageAnimationManager;
    }

    public void pushPage(final Page page, final Object arg, boolean animated, boolean hint) {
        Page.TYPE pageType = page.getType();
        Page oldPage = mCurPage;

        if (oldPage != null) {
            if (page.keepSingleInstance() && page.getClass() == oldPage.getClass()) {
                mPageStack.removeLast().onHidden();

            } else {
                oldPage.onCovered();
            }
        }

        mCurPage = page;
        mPageStack.addLast(mCurPage);
        View currentPageView = mCurPage.getView();

        if (animated && mPageAnimationManager != null) {
            mPageAnimationManager.onPushPageAnimation(oldPage != null ? oldPage.getView() : null, currentPageView, hint);
        }

        int viewCount = mContainerView.getChildCount();

        if (viewCount > 0 && pageType == Page.TYPE.TYPE_NORMAL)
            mContainerView.removeViewAt(viewCount - 1);

        mContainerView.addView(currentPageView);

        if (animated && mPageAnimationManager != null) {
            mCurPage.getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page.onShown(arg);
                }
            }, mPageAnimationManager.getAnimationDuration());
        } else {
            page.onShown(arg);
        }
    }

    public void popPage(boolean animated, boolean hint) {
        popTopNPages(1, animated, hint);
    }

    public void popTopNPages(int n, boolean animated, boolean hint) {
        if (mPageStack.size() > 0) {
            Page oldPage = mPageStack.removeLast();
            --n;    // for mPageStack.removeLast() above

            while (--n >= 0) {
                Page page = mPageStack.removeLast();
                page.onHidden();
            }

            popPageInternal(oldPage, animated, hint);
        }
    }

    public void popPagesTillSpecifiedPage(Page destPage, boolean animated, boolean hint) {
        if (mPageStack.size() > 0) {
            if (mPageStack.peekLast() == destPage) {
                return;
            }

            Page oldPage = mPageStack.removeLast();

            while (mPageStack.size() > 1) {
                if (mPageStack.peekLast() == destPage) {
                    break;
                }

                Page page = mPageStack.removeLast();
                page.onHidden();
            }

            popPageInternal(oldPage, animated, hint);
        }
    }

    private void popPageInternal(final Page oldPage, boolean animated, boolean hint) {
        if (animated && mPageAnimationManager != null) {
            oldPage.getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    oldPage.onHidden();
                }
            }, mPageAnimationManager.getAnimationDuration());
        } else {
            oldPage.onHidden();
        }

        if (mPageStack.size() > 0) {
            mCurPage = mPageStack.getLast();
            View currentPageView = mCurPage.getView();

            if (animated && mPageAnimationManager != null) {
                mPageAnimationManager.onPopPageAnimation(oldPage.getView(), currentPageView, hint);
            }

            // remove the view of the top page
            mContainerView.removeViewAt(mContainerView.getChildCount() - 1);

            // if the oldPage is of type TYPE_DIALOG, we don't need
            // to add the current page view to mContainerView, because it
            // was never removed, see pushPage.
            if (oldPage.getType() != Page.TYPE.TYPE_DIALOG)
                mContainerView.addView(currentPageView);

            if (Build.VERSION.SDK_INT <= 15) {
                // this is a hack that fixed a problem that on some 4.0.4 devices(Galaxy S3, MIUI 4.0.4)
                // ListView items may not be clickable when the View is brought to the top
                currentPageView.setVisibility(View.GONE);
                currentPageView.setVisibility(View.VISIBLE);
            } else {
                currentPageView.requestFocus();
            }

            mCurPage.onUncovered(oldPage.getReturnData());

        } else {
            mCurPage = null;

            if (animated && mPageAnimationManager != null) {
                mPageAnimationManager.onPopPageAnimation(oldPage.getView(), null, hint);
            }
        }
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
