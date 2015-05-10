package net.neevek.android.lib.paginize.anim;

import android.view.animation.*;
import net.neevek.android.lib.paginize.Page;

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

public class SquashPageAnimator implements PageAnimator {
  private final static int ANIMATION_DURATION = 400;
  private Animation mPushInAnimation;
  private Animation mPushOutAnimation;
  private Animation mPopInAnimation;
  private Animation mPopOutAnimation;

  public SquashPageAnimator() {
    initAnimations();
  }

  private void initAnimations() {
    mPushInAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
    mPushInAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mPushInAnimation.setDuration(ANIMATION_DURATION);

    mPushOutAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
    mPushOutAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mPushOutAnimation.setDuration(ANIMATION_DURATION);

    mPopInAnimation = new ScaleAnimation(0, 1, 1, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
    mPopInAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mPopInAnimation.setDuration(ANIMATION_DURATION);

    mPopOutAnimation = new ScaleAnimation(1, 0, 1, 1, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
    mPopOutAnimation.setInterpolator(new DecelerateInterpolator(2.5f));
    mPopOutAnimation.setDuration(ANIMATION_DURATION);
  }

  @Override
  public boolean onPushPageAnimation(Page oldPage, Page newPage) {
    if (oldPage != null) {
      oldPage.getView().startAnimation(mPushOutAnimation);
    }

    newPage.getView().startAnimation(mPushInAnimation);

    return true;
  }

  @Override
  public boolean onPopPageAnimation(Page oldPage, Page newPage) {
    oldPage.getView().startAnimation(mPopOutAnimation);

    if (newPage != null) {
      newPage.getView().startAnimation(mPopInAnimation);
    }

    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }
}
