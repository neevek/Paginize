package net.neevek.android.lib.paginize.anim;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
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

public final class ZoomPageAnimator implements PageAnimator {
  private final static int ANIMATION_DURATION = 200;
  private Animation mInAnimation;
  private Animation mOutAnimation;

  public ZoomPageAnimator() {
    initAnimations();
  }

  private void initAnimations() {
    Animation inScaleAnimation = new ScaleAnimation(1.2f, 1, 1.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    Animation inAlphaAnimation = new AlphaAnimation(0.0f, 1f);
    AnimationSet inAnimationSet = new AnimationSet(true);
    inAnimationSet.setDuration(ANIMATION_DURATION);
    inAnimationSet.addAnimation(inScaleAnimation);
    inAnimationSet.addAnimation(inAlphaAnimation);
    mInAnimation = inAnimationSet;

    Animation outScaleAnimation = new ScaleAnimation(1, 1.4f, 1, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    Animation outAlphaAnimation = new AlphaAnimation(1f, 0f);
    AnimationSet outAnimationSet = new AnimationSet(true);
    outAnimationSet.setDuration(ANIMATION_DURATION);
    outAnimationSet.addAnimation(outScaleAnimation);
    outAnimationSet.addAnimation(outAlphaAnimation);
    mOutAnimation = outAnimationSet;
  }

  @Override
  public boolean onPushPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    newPageView.startAnimation(mInAnimation);

    return true;
  }

  @Override
  public boolean onPopPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    oldPageView.startAnimation(mOutAnimation);

    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }
}
