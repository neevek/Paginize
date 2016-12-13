package net.neevek.android.lib.paginize;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import net.neevek.android.lib.paginize.anim.PageAnimator;

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
  final static int SWIPE_TRANSITION_ANIMATION_DURATION = 500;
  private final static int SHADOW_VIEW_WIDTH = 20;                // in DIP
  private final static int SWIPE_TO_HIDE_THRESHOLD = 60;          // in DIP
  private final static int SWIPE_TO_HIDE_EDGE_SLOPE = 50;         // in DIP

  private PageManager mPageManager;
  private SwipeableContainerView mSwipeableContainerView;
  private Map<String, Animation> mAnimationCache = new HashMap<String,Animation>();

  ContainerViewManager(PageManager pageManager) {
    mPageManager = pageManager;
  }

  private void animateView(View view, int fromXType, float fromXValue,
                   int toXType, float toXValue,
                   Animation.AnimationListener animationListener) {
    animateView(view, fromXType, fromXValue, toXType, toXValue, false,
        animationListener);
  }

  void animateView(View view, int fromXType, float fromXValue,
                   int toXType, float toXValue, boolean cacheAnimationObj,
                   Animation.AnimationListener animationListener) {
    ViewGroup.MarginLayoutParams lp =
        (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    lp.leftMargin = 0;
    lp.rightMargin = 0;
    view.requestLayout();

    Animation animation = null;
    if (cacheAnimationObj) {
      String key = new StringBuilder()
              .append(fromXType).append('-').append(fromXValue).append(',')
              .append(toXType).append('-').append(toXValue).toString();
      animation = mAnimationCache.get(key);
      if (animation == null) {
        animation = createAnimation(fromXType, fromXValue, toXType, toXValue,
            animationListener);
        mAnimationCache.put(key, animation);
      }
    }

    if (animation == null) {
      animation = createAnimation(fromXType, fromXValue, toXType, toXValue,
          animationListener);
    }

    view.startAnimation(animation);
  }

  private Animation createAnimation(
      int fromXType, float fromXValue,
      int toXType, float toXValue,
      Animation.AnimationListener animationListener) {
    Animation animation = new TranslateAnimation(fromXType, fromXValue, toXType,
        toXValue, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    animation.setDuration(mPageManager.getTransitionAnimationDuration());
    animation.setInterpolator(new DecelerateInterpolator(2.0f));
    animation.setAnimationListener(animationListener);
    return animation;
  }

  void animateShadowViewForHiding(
      int anchorLeft, PageAnimator.AnimationDirection animationDirection) {
    mSwipeableContainerView.mShadowView.setVisibility(View.VISIBLE);
    mSwipeableContainerView.mShadowView.bringToFront();

    Animation.AnimationListener animationListener =
        new Animation.AnimationListener() {
          public void onAnimationStart(Animation animation) {
          }

          public void onAnimationRepeat(Animation animation) {
          }

          public void onAnimationEnd(Animation animation) {
            mSwipeableContainerView.mShadowView.setVisibility(View.INVISIBLE);
          }
        };

    if (animationDirection == PageAnimator.AnimationDirection.FROM_LEFT) {
      animateView(mSwipeableContainerView.mShadowView, Animation.ABSOLUTE,
          anchorLeft - mSwipeableContainerView.mShadowView.getWidth(),
          Animation.RELATIVE_TO_PARENT, 1f, animationListener);
    } else {
      animateView(mSwipeableContainerView.mShadowView,
          Animation.RELATIVE_TO_PARENT, 1, Animation.ABSOLUTE,
          -mSwipeableContainerView.getWidth(), animationListener);
    }
  }

  void animateShadowViewForShowing(
      PageAnimator.AnimationDirection animationDirection) {
    mSwipeableContainerView.mShadowView.setVisibility(View.VISIBLE);
    mSwipeableContainerView.mShadowView.bringToFront();

    Animation.AnimationListener animationListener =
        new Animation.AnimationListener() {
          public void onAnimationStart(Animation animation) {
          }

          public void onAnimationRepeat(Animation animation) {
          }

          public void onAnimationEnd(Animation animation) {
            mSwipeableContainerView.mShadowView.setVisibility(View.INVISIBLE);
          }
        };

    if (animationDirection == PageAnimator.AnimationDirection.FROM_RIGHT) {
      animateView(mSwipeableContainerView.mShadowView, Animation.ABSOLUTE,
          mSwipeableContainerView.getWidth() - mSwipeableContainerView.mShadowView.getWidth(),
          Animation.RELATIVE_TO_SELF, -1, animationListener);
    } else {
      animateView(mSwipeableContainerView.mShadowView, Animation.RELATIVE_TO_SELF,
          -1, Animation.ABSOLUTE,
          mSwipeableContainerView.getWidth() - mSwipeableContainerView.mShadowView.getWidth(),
          animationListener);
    }
  }

  SwipeableContainerView createContainerView(Context context) {
    mSwipeableContainerView = new SwipeableContainerView(context);
    return mSwipeableContainerView;
  }

  void enableSwipeToHide(boolean applyInsetsToShadow) {
    if (mSwipeableContainerView != null) {
      mSwipeableContainerView.enableSwipeToHide(applyInsetsToShadow);
    }
  }

  boolean canSwipeToHide() {
    return mSwipeableContainerView != null &&
        mSwipeableContainerView.mSwipeToHide;
  }

  private class SwipeableContainerView extends FrameLayout {
    private ShadowView mShadowView;
    private View mCurrentView;
    private View mPrevView;
    private MarginLayoutParams mShadowViewLP;
    private MarginLayoutParams mCurrentViewLP;
    private MarginLayoutParams mPrevViewLP;

    private int mInitialX;
    private boolean mIsDragging;
    private float mTouchSlope;
    private float mEdgeSlope;
    private int mSwipeToHideThreshold;

    private boolean mSwipeToHide;
    private boolean mApplyInsetsToShadow;

    public SwipeableContainerView(Context context) {
      super(context);
      mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
      mEdgeSlope = (int)(SWIPE_TO_HIDE_EDGE_SLOPE *
          getResources().getDisplayMetrics().density);
      mSwipeToHideThreshold = (int)(SWIPE_TO_HIDE_THRESHOLD *
          getResources().getDisplayMetrics().density);
    }

    @TargetApi(20)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
      if (insets != null) {
        if (mApplyInsetsToShadow && mShadowView != null) {
          MarginLayoutParams lp = (MarginLayoutParams)mShadowView.getLayoutParams();
          if (lp.topMargin != insets.getSystemWindowInsetTop()) {
            lp.topMargin = insets.getSystemWindowInsetTop();
          }
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
          getChildAt(i).dispatchApplyWindowInsets(insets);
        }
      }
      return insets;
    }

    public void enableSwipeToHide(boolean applyInsetsToShadow) {
      if (!mSwipeToHide) {
        mSwipeToHide = true;
        mApplyInsetsToShadow = applyInsetsToShadow;

        mShadowView = new ShadowView(getContext());
        addView(mShadowView, new MarginLayoutParams(
            (int)(SHADOW_VIEW_WIDTH*getResources().getDisplayMetrics().density),
            ViewGroup.LayoutParams.MATCH_PARENT));

        mShadowView.setVisibility(INVISIBLE);
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
          if (!mIsDragging &&
              mPageManager.getTopPage().getView().getAnimation() == null &&
              mInitialX <= mEdgeSlope && x - mInitialX > mTouchSlope) {
            mIsDragging = true;

            Page topPage = mPageManager.getTopPage();
            mCurrentView = topPage.getView();
            int index = pageCount - topPage.getDefaultPageCountToPop() - 1;
            if (index >= 0) {
              mPrevView = mPageManager.getPageStack().get(index).getView();
              mPrevView.setVisibility(VISIBLE);
              mPrevViewLP = (MarginLayoutParams)mPrevView.getLayoutParams();
            }

            mCurrentViewLP = (MarginLayoutParams)mCurrentView.getLayoutParams();
            mShadowViewLP = (MarginLayoutParams)mShadowView.getLayoutParams();

            mInitialX = x;
            mShadowView.bringToFront();
            post(new Runnable() {
              @Override
              public void run() {
                mShadowViewLP.leftMargin = -mShadowView.getWidth();
                mShadowView.setVisibility(VISIBLE);
                mShadowView.requestLayout();
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
              mPrevViewLP.leftMargin = -(int)(getWidth() * 0.5f - (delta * 0.5f));
              mPrevViewLP.rightMargin = -(-(int)(getWidth() * 0.5f - (delta * 0.5f)));
            }
            if (mCurrentView != null) {
              mCurrentViewLP.leftMargin = delta;
              mCurrentViewLP.rightMargin = -delta;
              mShadowViewLP.leftMargin = delta - mShadowView.getWidth();
            }

            mSwipeableContainerView.requestLayout();

            return true;
          }
          break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
          if (mIsDragging) {
            mIsDragging = false;
            int currentViewLeft = mCurrentView.getLeft();

            if (currentViewLeft > mSwipeToHideThreshold) {
              mPageManager.popTopNPages(
                  mPageManager.getTopPage().getDefaultPageCountToPop(), true);
            } else if (currentViewLeft > 0) {
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
        animateView(mPrevView, Animation.ABSOLUTE,
            mPrevView.getLeft(), Animation.RELATIVE_TO_PARENT, -0.5f, null);
      }
      animateView(mCurrentView, Animation.ABSOLUTE,
          mCurrentView.getLeft(), Animation.RELATIVE_TO_PARENT, 0, null);
      animateView(mShadowView, Animation.ABSOLUTE,
          mShadowView.getLeft(), Animation.ABSOLUTE, -mShadowView.getWidth(),
          new Animation.AnimationListener() {
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
      setGradientBackground();
    }

    private void setGradientBackground() {
      GradientDrawable bg = new GradientDrawable(
          GradientDrawable.Orientation.LEFT_RIGHT,
          new int[] { 0x00000000, 0x20000000 });
      if (Build.VERSION.SDK_INT >= 16) {
        setBackground(bg);
      } else {
        setBackgroundDrawable(bg);
      }
    }
  }
}
