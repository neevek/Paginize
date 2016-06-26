package net.neevek.android.lib.paginize;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
 * This class wraps the logics used by ViewPagerPage and ViewPagerInnerPage, both implement InnerPageContainer
 */
public class ViewPagerPageManager extends InnerPageContainerManager {
    private final String SAVE_VIEWPAGER_PAGE_MANAGER_KEY = "_paginize_viewpager_page_manager_" + getClass().getName();

    private List<InnerPage> mInnerPageList = new ArrayList<InnerPage>();
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private int mLastSelectedPage;
    private boolean mAlwaysKeepInnerPagesInViewHierarchy;

    private ViewPagerPageScrollListener mPageScrollListener;

    private InnerPageEventNotifier mInnerPageEventNotifier;

    public ViewPagerPageManager(ViewWrapper innerPageContainer) {
        super(innerPageContainer);
        initViewPager(innerPageContainer.getContext());
    }

    public void setInnerPageEventNotifier(InnerPageEventNotifier innerPageEventNotifier) {
        mInnerPageEventNotifier = innerPageEventNotifier;
    }

    private void initViewPager(Context context) {
        mViewPager = new ViewPager(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(lp);

        mPagerAdapter = new InnerPagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new InnerPageChangeListener());

        getContainerView().addView(mViewPager);
    }

    public void setAlwaysKeepInnerPagesInViewHierarchy(boolean alwaysKeepInnerPagesInViewHierarchy) {
        mAlwaysKeepInnerPagesInViewHierarchy = alwaysKeepInnerPagesInViewHierarchy;
    }

    public void addPage(InnerPage page) {
        if (getCurrentInnerPage() == null) {
            setCurrentInnerPage(page);
        }
        mInnerPageList.add(page);
        mPagerAdapter.notifyDataSetChanged();
    }

    public void removePage(int index) {
        if (index > mInnerPageList.size()) {
            throw new IllegalArgumentException("index is too large: " + index + ", actual: " + mInnerPageList.size());
        }

        mInnerPageList.remove(index);
        if (index == mLastSelectedPage) {
            if (mLastSelectedPage - 1 >= 0) {
                mLastSelectedPage -= 1;
            } else {
                mLastSelectedPage = 0;
            }
        }
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setCurrentPage(int index, boolean animated) {
        if (index > mInnerPageList.size()) {
            throw new IllegalArgumentException("index is too large: " + index + ", actual: " + mInnerPageList.size());
        }

        if (mInnerPageList.size() == 0) {
            return;
        }

        try {
            mViewPager.setCurrentItem(index, animated);
            mLastSelectedPage = index;
        } catch (Exception e) {
            if (animated) { // ignore animated, which may cause crash
                try {   // not sure if it would crash
                    mViewPager.setCurrentItem(index, false);
                    mLastSelectedPage = index;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public int getCurrentPageIndex() {
        return mViewPager.getCurrentItem();
    }

    public InnerPage getPage(int index) {
        if (index > mInnerPageList.size()) {
            throw new IllegalArgumentException("index is too large: " + index + ", actual: " + mInnerPageList.size());
        }

        return mInnerPageList.get(index);
    }

    public int getPageCount() {
        return mInnerPageList.size();
    }

    public void setHorizontalFadingEdgeEnabled(boolean enabled) {
        mViewPager.setHorizontalFadingEdgeEnabled(enabled);
    }

    public void setFadingEdgeLength(int length) {
        mViewPager.setFadingEdgeLength(length);
    }

    public void setPageScrollListener(ViewPagerPageScrollListener pageScrollListener) {
        mPageScrollListener = pageScrollListener;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY, getCurrentPageIndex());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        final int lastShownPageIndex = savedInstanceState.getInt(SAVE_VIEWPAGER_PAGE_MANAGER_KEY);
        if (lastShownPageIndex < getPageCount()) {
            setCurrentPage(lastShownPageIndex, false);
        }
    }

    class InnerPagePagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            InnerPage innerPage = mInnerPageList.get(position);
            if (!mAlwaysKeepInnerPagesInViewHierarchy || container.indexOfChild(innerPage.getView()) == -1) {
                container.addView(innerPage.getView());
            }

            return innerPage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (!mAlwaysKeepInnerPagesInViewHierarchy) {
                InnerPage innerPage = (InnerPage) object;
                container.removeView(innerPage.getView());
            }
        }

        @Override
        public int getCount() {
            return mInnerPageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            InnerPage innerPage = (InnerPage)o;
            return view == innerPage.getView();
        }

        @Override
        public int getItemPosition(Object object) {
            // see http://stackoverflow.com/a/7287121/668963
            return POSITION_NONE;
        }
    }

    class InnerPageChangeListener implements ViewPager.OnPageChangeListener {
        private boolean markNewPageSelected;
        private InnerPage oldPage;

        @Override
        public void onPageScrolled(int index, float indexOffset, int indexOffsetPixels) {
            if (mPageScrollListener != null) {
                mPageScrollListener.onPageScrolled(index, indexOffset, indexOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getScrollX() % mViewPager.getWidth() != 0) {
                markNewPageSelected = true;
            }

            oldPage = mInnerPageList.get(mLastSelectedPage);
            oldPage.onHide();
            if (!markNewPageSelected) {
                oldPage.onHidden();
                if (mInnerPageEventNotifier != null) {
                    mInnerPageEventNotifier.onInnerPageHidden(oldPage);
                }
            }

            mLastSelectedPage = position;

            InnerPage newPage = mInnerPageList.get(mLastSelectedPage);
            setCurrentInnerPage(newPage);

            newPage.onShow(null);
            if (!markNewPageSelected) {
                newPage.onShown(null);
                if (mInnerPageEventNotifier != null) {
                    mInnerPageEventNotifier.onInnerPageShown(newPage);
                }
            }

            if (mPageScrollListener != null) {
                mPageScrollListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mPageScrollListener != null) {
                mPageScrollListener.onPageScrollStateChanged(state);
            }

            if (markNewPageSelected && state == ViewPager.SCROLL_STATE_IDLE) {
                markNewPageSelected = false;

                if (oldPage != null) {
                    oldPage.onHide();
                    oldPage.onHidden();
                    if (mInnerPageEventNotifier != null) {
                        mInnerPageEventNotifier.onInnerPageHidden(oldPage);
                    }
                    InnerPage newPage = mInnerPageList.get(mLastSelectedPage);

                    if (newPage != null) {
                        newPage.onShow(null);
                        newPage.onShown(null);
                        if (mInnerPageEventNotifier != null) {
                            mInnerPageEventNotifier.onInnerPageShown(newPage);
                        }
                    }
                }
            }
        }
    }
}
