package net.neevek.android.lib.paginize;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

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
 * This is the main view container
 */
class ContainerViewManager {
    final static int SWIPE_TRANSITION_ANIMATION_DURATION = 180;
    private final static int SHADOW_VIEW_WIDTH = 20;    // in DIP

    private PageManager mPageManager;
    private SwipeableContainerView mSwipeableContainerView;

    private Map<String, Animation> mAnimationCache = new HashMap<String,Animation>();

    ContainerViewManager(PageManager pageManager) {
        mPageManager = pageManager;
    }

    void animateView(View view, int fromXType, int fromXValue, int toXType, float toXValue, Animation.AnimationListener animationListener) {
        animateView(view, fromXType, fromXValue, toXType, toXValue, false, animationListener);
    }

    void animateView(View view, int fromXType, int fromXValue, int toXType, float toXValue, boolean cacheAnimationObj, Animation.AnimationListener animationListener) {
        if (view instanceof ShadowView) {
            view.setRight(view.getWidth());
        }
        view.setLeft(0);

        Animation animation = null;
        if (cacheAnimationObj) {
            String key = new StringBuilder().append(fromXType).append(fromXValue).append(toXType).append(toXValue).toString();
            animation = mAnimationCache.get(key);
            if (animation == null) {
                animation = createAnimation(fromXType, fromXValue, toXType, toXValue, animationListener);
                mAnimationCache.put(key.toString(), animation);
            }
        }

        if (animation == null) {
            animation = createAnimation(fromXType, fromXValue, toXType, toXValue, animationListener);
        }

        view.startAnimation(animation);
    }

    private Animation createAnimation(int fromXType, int fromXValue, int toXType, float toXValue, Animation.AnimationListener animationListener) {
        Animation animation = new TranslateAnimation(fromXType, fromXValue, toXType, toXValue
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(mPageManager.getTransitionAnimationDuration());
        animation.setAnimationListener(animationListener);
        return animation;
    }

    void animateShadowViewForHiding(int anchorLeft) {
        mSwipeableContainerView.mShadowView.setVisibility(View.VISIBLE);
        mSwipeableContainerView.mShadowView.bringToFront();
        animateView(mSwipeableContainerView.mShadowView, Animation.ABSOLUTE, anchorLeft - mSwipeableContainerView.mShadowView.getWidth(), Animation.RELATIVE_TO_PARENT, 1f, new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                mSwipeableContainerView.mShadowView.setVisibility(View.INVISIBLE);
            }
        });
    }

    void animateShadowViewForShowing() {
        mSwipeableContainerView.mShadowView.setVisibility(View.VISIBLE);
        mSwipeableContainerView.mShadowView.bringToFront();
        animateView(mSwipeableContainerView.mShadowView, Animation.ABSOLUTE, mSwipeableContainerView.getWidth() - mSwipeableContainerView.mShadowView.getWidth(), Animation.RELATIVE_TO_SELF, -1, new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                mSwipeableContainerView.mShadowView.setVisibility(View.INVISIBLE);
            }
        });
    }

    SwipeableContainerView createContainerView(Context context) {
        mSwipeableContainerView = new SwipeableContainerView(context);
        return mSwipeableContainerView;
    }

    void enableSwipeToHide() {
        if (mSwipeableContainerView != null) {
            mSwipeableContainerView.enableSwipeToHide();
        }
    }

    boolean canSwipeToHide() {
        return mSwipeableContainerView != null && mSwipeableContainerView.mSwipeToHide;
    }

    private class SwipeableContainerView extends FrameLayout {
        private ShadowView mShadowView;

        private int mInitialX;
        private boolean mIsDragging;
        private float mTouchSlope;
        private float mEdgeSlope;
        private int mSwipeToHideThreshold;

        private View mCurrentView;
        private View mPrevView;

        private boolean mSwipeToHide;

        public SwipeableContainerView(Context context) {
            super(context);
            mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
            mEdgeSlope = ViewConfiguration.get(context).getScaledEdgeSlop() * 2.5f;
            mSwipeToHideThreshold = (int)(100 * getResources().getDisplayMetrics().density);
        }

        public void enableSwipeToHide() {
            if (!mSwipeToHide) {
                mSwipeToHide = true;

                mShadowView = new ShadowView(getContext());
                mShadowView.setVisibility(INVISIBLE);
                addView(mShadowView);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!mSwipeToHide) {
                return super.onInterceptTouchEvent(ev);
            }

            int pageCount = mPageManager.getPageCount();
            if (pageCount < 2) {
                return super.onInterceptTouchEvent(ev);
            }

            if (!mPageManager.getTopPage().canSwipeToHide()) {
                return super.onInterceptTouchEvent(ev);
            }

            final int x = (int)ev.getRawX();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mInitialX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mIsDragging && mPageManager.getTopPage().getView().getAnimation() == null && mInitialX <= mEdgeSlope && x - mInitialX > mTouchSlope) {
                        mIsDragging = true;

                        mCurrentView = mPageManager.getTopPage().getView();
                        mPrevView = mPageManager.getPageStack().get(pageCount - 2).getView();
                        mPrevView.setVisibility(VISIBLE);

                        mInitialX = x;
                        mShadowView.bringToFront();
                        post(new Runnable() {
                            @Override
                            public void run() {
                                mShadowView.setVisibility(VISIBLE);
                            }
                        });
                        return true;
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!mSwipeToHide) {
                return super.onTouchEvent(event);
            }

            int x = (int)event.getRawX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (mIsDragging) {
                        int delta = x - mInitialX;
                        if (delta < 0) {
                            delta = 0;
                        }

                        if (mPrevView != null) {
                            mPrevView.setLeft(-(int)(getWidth() * 0.5f - (delta * 0.5f)));
                        }
                        if (mCurrentView != null) {
                            mCurrentView.setLeft(delta);

                            mShadowView.setLeft(delta - mShadowView.getWidth());
                            mShadowView.setRight(delta);
                        }
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mIsDragging) {
                        mIsDragging = false;

                        if (x > mSwipeToHideThreshold) {
                            mPageManager.popPage(true);
                        } else if (mCurrentView.getLeft() > 0) {
                            cancelSwipeToHide();
                        } else {
                            mShadowView.setVisibility(INVISIBLE);
                        }

                        return true;
                    }
                    break;

            }

            return super.onTouchEvent(event);
        }

        private void cancelSwipeToHide() {
            if (mPrevView != null) {
                animateView(mPrevView, Animation.ABSOLUTE, mPrevView.getLeft(), Animation.RELATIVE_TO_PARENT, -0.5f, null);
            }
            animateView(mCurrentView, Animation.ABSOLUTE, mCurrentView.getLeft(), Animation.RELATIVE_TO_PARENT, 0, null);
            animateView(mShadowView, Animation.ABSOLUTE, mShadowView.getLeft(), Animation.ABSOLUTE, -mShadowView.getWidth(), new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) { }
                public void onAnimationRepeat(Animation animation) { }
                public void onAnimationEnd(Animation animation) {
                    mShadowView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private class ShadowView extends View {
        public ShadowView(Context context) {
            super(context);

            setLayoutParams(new ViewGroup.LayoutParams((int) (SHADOW_VIEW_WIDTH * getResources().getDisplayMetrics().density), ViewGroup.LayoutParams.MATCH_PARENT));
            setGredientBackground();
        }

        private void setGredientBackground() {
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] { 0x00000000, 0x20000000 });
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(bg);
            } else {
                setBackgroundDrawable(bg);
            }
        }
    }
}
