package net.neevek.android.lib.paginize.anim;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import net.neevek.android.lib.paginize.Page;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlidePageAnimator implements PageAnimator {
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
  public boolean onPushPageAnimation(Page oldPage, Page newPage, boolean hint) {
    if (oldPage != null) {
      oldPage.getView().startAnimation(mPullOutFromRightAnimation);
    }

    if (hint) {
      newPage.getView().startAnimation(mPushInFromLeftAnimation);
    } else {
      newPage.getView().startAnimation(mPushInFromRightAnimation);
    }

    return true;
  }

  @Override
  public boolean onPopPageAnimation(Page oldPage, Page newPage, boolean hint) {
    if (hint) {
      oldPage.getView().startAnimation(mPullOutFromLeftAnimation);
    } else {
      oldPage.getView().startAnimation(mPullOutFromRightAnimation);
    }

    if (newPage != null) {
      newPage.getView().startAnimation(mPushInFromLeftAnimation);
    }

    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }
}
