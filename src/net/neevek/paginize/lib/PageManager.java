package net.neevek.paginize.lib;

import android.content.Intent;
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
    private boolean mAnimating;

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

    public void pushPage(Page page) {
        pushPage(page, null, false);
    }

    public void pushPage(Page page, Object arg, boolean animated) {
        pushPage(page, arg, animated, false);
    }

    public void pushPage(final Page newPage, final Object arg, boolean animated, boolean hint) {
        Page oldPage = mCurPage;

        if (oldPage != null) {
            if (newPage.keepSingleInstance() && newPage.getClass() == oldPage.getClass()) {
                oldPage.onHidden();
                mContainerView.removeView(oldPage.getView());

            } else {
                oldPage.onCovered();
                if (newPage.getType() != Page.TYPE.TYPE_DIALOG) {
                    oldPage.getView().setVisibility(View.GONE);
                }
            }
        }

        View newPageView = newPage.getView();

        if (animated && mPageAnimationManager != null) {
            mPageAnimationManager.onPushPageAnimation(oldPage != null ? oldPage.getView() : null, newPageView, hint);
        }

        mContainerView.addView(newPageView);

        if (animated && mPageAnimationManager != null) {
            mAnimating = true;
            newPage.getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimating = false;
                    newPage.onShown(arg);
                }
            }, mPageAnimationManager.getAnimationDuration());
        } else {
            newPage.onShown(arg);
        }

        mCurPage = newPage;
        mPageStack.addLast(newPage);
    }

    public void popPage() {
        popPage(false, false);
    }

    /**
     * @param animated
     * @param hint true=left, false=right
     */
    public void popPage(boolean animated, boolean hint) {
        popTopNPages(1, animated, hint);
    }

    public void popTopNPages(int n, boolean animated, boolean hint) {
        if (n <= 0 || mAnimating || mPageStack.size() <= 0) {
            return;
        }

        Page oldPage = mPageStack.removeLast();
        --n;    // for mPageStack.removeLast() above

        while (--n >= 0) {
            Page page = mPageStack.removeLast();
            page.onHidden();
            mContainerView.removeView(page.getView());
        }

        popPageInternal(oldPage, animated, hint);
    }

    public void popPagesTillSpecifiedPage(Page destPage, boolean animated, boolean hint) {
        if (destPage == null) {
            throw new IllegalArgumentException("cannot call popPagesTillSpecifiedPage() with null destPage.");
        }

        if (mAnimating || mPageStack.size() <= 0 || mPageStack.peekLast() == destPage) {
            return;
        }

        Page oldPage = mPageStack.removeLast();

        while (mPageStack.size() > 1) {
            Page page = mPageStack.removeLast();
            page.onHidden();
            mContainerView.removeView(page.getView());

            if (mPageStack.peekLast() == destPage) {
                break;
            }
        }

        popPageInternal(oldPage, animated, hint);
    }

    private void popPageInternal(final Page removedPage, boolean animated, boolean hint) {
        final Page prevPage;
        if (mPageStack.size() > 0) {    // this check is always necessary
            prevPage = mPageStack.getLast();
            View prevPageView = prevPage.getView();

            if (animated && mPageAnimationManager != null) {
                mPageAnimationManager.onPopPageAnimation(removedPage.getView(), prevPageView, hint);
            }

            prevPageView.setVisibility(View.VISIBLE);
        } else {
            prevPage = null;

            if (animated && mPageAnimationManager != null) {
                mPageAnimationManager.onPopPageAnimation(removedPage.getView(), null, hint);
            }
        }

        mContainerView.removeView(removedPage.getView());

        if (animated && mPageAnimationManager != null) {
            mAnimating = true;
            removedPage.getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimating = false;
                    removedPage.onHidden();

                    if (prevPage != null) {
                        prevPage .onUncovered(removedPage.getReturnData());
                    }
                }
            }, mPageAnimationManager.getAnimationDuration());
        } else {
            removedPage.onHidden();

            if (prevPage != null) {
                prevPage.onUncovered(removedPage.getReturnData());
            }
        }

        mCurPage = prevPage;
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

    public LinkedList<Page> getPageStack() {
        return mPageStack;
    }
}
