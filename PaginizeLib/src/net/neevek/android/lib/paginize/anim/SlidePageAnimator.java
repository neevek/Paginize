package net.neevek.android.lib.paginize.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public final class SlidePageAnimator implements PageAnimator {
  private final static int ANIMATION_DURATION = 250;
  private Animation mPushInFromRightAnimation;
  private Animation mPullOutFromRightAnimation;
  private Animation mPushInFromLeftAnimation;
  private Animation mPullOutFromLeftAnimation;

  public SlidePageAnimator() {
    initAnimations();
  }

  private void initAnimations() {
    mPushInFromRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0
        , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    mPushInFromRightAnimation.setDuration(ANIMATION_DURATION);
    mPullOutFromLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1
        , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    mPullOutFromLeftAnimation.setDuration(ANIMATION_DURATION);
    mPushInFromLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0
        , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    mPushInFromLeftAnimation.setDuration(ANIMATION_DURATION);
    mPullOutFromRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1
        , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    mPullOutFromRightAnimation.setDuration(ANIMATION_DURATION);
  }

  @Override
  public boolean onPushPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    if (animationDirection == AnimationDirection.FROM_RIGHT) {
      if (oldPageView != null) {
        oldPageView.startAnimation(mPullOutFromRightAnimation);
      }
      newPageView.startAnimation(mPushInFromRightAnimation);
    } else {
      if (oldPageView != null) {
        oldPageView.startAnimation(mPullOutFromLeftAnimation);
      }
      newPageView.startAnimation(mPushInFromLeftAnimation);
    }

    return true;
  }

  @Override
  public boolean onPopPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    if (animationDirection == AnimationDirection.FROM_LEFT) {
      oldPageView.startAnimation(mPullOutFromLeftAnimation);
      if (newPageView != null) {
        newPageView.startAnimation(mPushInFromLeftAnimation);
      }
    } else {
      oldPageView.startAnimation(mPullOutFromRightAnimation);
      if (newPageView != null) {
        newPageView.startAnimation(mPushInFromRightAnimation);
      }
    }

    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }
}
