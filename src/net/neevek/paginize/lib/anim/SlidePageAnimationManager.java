package net.neevek.paginize.lib.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlidePageAnimationManager implements PageAnimationManager {
    private final static int ANIMATION_DURATION = 250;
    private Animation mPushInFromRightAnimation;
    private Animation mPullOutFromRightAnimation;
    private Animation mPushInFromLeftAnimation;
    private Animation mPullOutFromLeftAnimation;

    public SlidePageAnimationManager() {
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
    public void onPushPageAnimation(View oldPageView, View newPageView, boolean hint) {
        if (oldPageView != null) {
            oldPageView.startAnimation(mPullOutFromRightAnimation);
        }

        if (hint) {
            newPageView.startAnimation(mPushInFromLeftAnimation);
        } else {
            newPageView.startAnimation(mPushInFromRightAnimation);
        }
    }

    @Override
    public void onPopPageAnimation(View oldPageView, View newPageView, boolean hint) {
        if (hint) {
            oldPageView.startAnimation(mPullOutFromLeftAnimation);
        } else {
            oldPageView.startAnimation(mPullOutFromRightAnimation);
        }

        if (newPageView != null) {
            newPageView.startAnimation(mPushInFromLeftAnimation);
        }
    }

    @Override
    public int getAnimationDuration() {
        return ANIMATION_DURATION;
    }
}
