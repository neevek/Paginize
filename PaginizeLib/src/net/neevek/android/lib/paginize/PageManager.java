package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.anim.PageAnimationManager;

import java.util.Iterator;
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

    public PageAnimationManager getPageAnimationManager() {
        return mPageAnimationManager;
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

        Page oldPage = mCurPage;

        mCurPage = newPage;
        mPageStack.addLast(newPage);
        mContainerView.addView(newPage.getView());
        newPage.onAttach();

        if (oldPage != null) {
            if (newPage.keepSingleInstance() && newPage.getClass() == oldPage.getClass()) {
                mPageStack.removeLastOccurrence(oldPage);
                mContainerView.removeView(oldPage.getView());
                oldPage.onDetach();
                oldPage.onHidden();
            } else {
                if (newPage.getType() != Page.TYPE.TYPE_DIALOG) {
                    oldPage.getView().setVisibility(View.GONE);
                }

                oldPage.onCovered();
            }
        }

        if (animated && mPageAnimationManager != null) {
            mPageAnimationManager.onPushPageAnimation(oldPage != null ? oldPage.getView() : null, newPage.getView(), hint);
        }

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
            mContainerView.removeView(page.getView());
            page.onDetach();
            page.onHidden();
        }

        popPageInternal(oldPage, animated, hint);
    }

    /**
     * "pop" operation ends if destPage is found,
     * if destPage is not found, the method call is a no-op
     *
     * @param destPage page as the destination for this pop operation
     * @param animated
     * @param hint
     */
    public void popToPage(Page destPage, boolean animated, boolean hint) {
        if (destPage == null) {
            throw new IllegalArgumentException("cannot call popToPage() with null destPage.");
        }

        if (mPageStack.size() <= 0 || mPageStack.lastIndexOf(destPage) == -1 || mPageStack.peekLast() == destPage) {
            return;
        }


        Page oldPage = mPageStack.removeLast();

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

    /**
     * "pop" operation ends if the pageClass is found,
     * if the class is not found, the method call is a no-op
     *
     * @param pageClass class of page as the destination for this pop operation
     * @param animated
     * @param hint
     */
    public void popToClass(Class<? extends Page> pageClass, boolean animated, boolean hint) {
        if (pageClass == null) {
            throw new IllegalArgumentException("cannot call popToClass() with null pageClass.");
        }

        popToClasses(new Class[]{ pageClass }, animated, hint);
    }

    /**
     * "pop" operation ends when one of the classes specified by pageClasses is found,
     * if none of the classes is found, the method call is a no-op
     *
     * @param pageClasses classes of pages as the destination for this pop operation
     * @param animated true if the transition should be animated
     * @param hint a hint for the PageAnimationManager
     */
    public void popToClasses(Class<? extends Page>[] pageClasses, boolean animated, boolean hint) {
        if (pageClasses == null || pageClasses.length == 0) {
            throw new IllegalArgumentException("cannot call popToClasses() with null or empty pageClasses.");
        }

        if (mPageStack.size() <= 0) {
            return;
        }

        boolean hasDestClass = false;
        Iterator<Page> it = mPageStack.descendingIterator();
        while (it.hasNext()) {
            for (Class pageClass : pageClasses) {
                if (it.next().getClass() == pageClass) {
                    hasDestClass = true;
                    break;
                }
            }
        }

        if (!hasDestClass) {
            return;
        }

        Page oldPage = mPageStack.removeLast();

        while (mPageStack.size() > 1) {
            for (Class pageClass : pageClasses) {
                if (mPageStack.peekLast().getClass() == pageClass) {
                    break;
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
        removedPage.onDetach();

        mCurPage = prevPage;

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
