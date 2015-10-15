package net.neevek.android.lib.paginize.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

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

public final class SquashPageAnimator implements PageAnimator {
  private final static int ANIMATION_DURATION = 400;
  private Animation mExpandInFromRightAnimation;
  private Animation mShrinkOutFromRightAnimation;
  private Animation mExpanndInFromLeftAnimation;
  private Animation mShrinkOutFromLeftAnimation;

  public SquashPageAnimator() {
    initAnimations();
  }

  private void initAnimations() {
    mExpandInFromRightAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
    mExpandInFromRightAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mExpandInFromRightAnimation.setDuration(ANIMATION_DURATION);

    mShrinkOutFromRightAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
    mShrinkOutFromRightAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mShrinkOutFromRightAnimation.setDuration(ANIMATION_DURATION);

    mExpanndInFromLeftAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
    mExpanndInFromLeftAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mExpanndInFromLeftAnimation.setDuration(ANIMATION_DURATION);

    mShrinkOutFromLeftAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
    mShrinkOutFromLeftAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mShrinkOutFromLeftAnimation.setDuration(ANIMATION_DURATION);
  }

  @Override
  public boolean onPushPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    if (animationDirection == AnimationDirection.FROM_RIGHT) {
      if (oldPageView != null) {
        oldPageView.startAnimation(mShrinkOutFromRightAnimation);
      }
      newPageView.startAnimation(mExpandInFromRightAnimation);

    } else {
      if (oldPageView != null) {
        oldPageView.startAnimation(mShrinkOutFromLeftAnimation);
      }
      newPageView.startAnimation(mExpanndInFromLeftAnimation);
    }

    return true;
  }

  @Override
  public boolean onPopPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    if (animationDirection == AnimationDirection.FROM_LEFT) {
      oldPageView.startAnimation(mShrinkOutFromLeftAnimation);
      if (newPageView != null) {
        newPageView.startAnimation(mExpanndInFromLeftAnimation);
      }

    } else {
      oldPageView.startAnimation(mShrinkOutFromRightAnimation);
      if (newPageView != null) {
        newPageView.startAnimation(mExpandInFromRightAnimation);
      }
    }

    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }
}
