package net.neevek.android.lib.paginize.anim;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import net.neevek.android.lib.paginize.Page;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 11:06 AM
 */
public class ZoomPageAnimationManager implements PageAnimationManager {
    private final static int ANIMATION_DURATION = 200;
    private Animation mInAnimation;
    private Animation mOutAnimation;

    private Animation mFadeOutAnimation;

    public ZoomPageAnimationManager() {
        initAnimations();
    }

    private void initAnimations() {
        Animation inScaleAnimation = new ScaleAnimation(1.1f, 1, 1.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation inAlphaAnimation = new AlphaAnimation(0.3f, 1f);
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

        mFadeOutAnimation = new AlphaAnimation(0.8f, 0.0f);
        mFadeOutAnimation.setDuration(ANIMATION_DURATION);
    }

    @Override
    public void onPushPageAnimation(Page oldPage, Page newPage, boolean hint) {
        if (oldPage != null) {
            oldPage.getView().startAnimation(mFadeOutAnimation);
        }

        newPage.getView().startAnimation(mInAnimation);
    }

    @Override
    public void onPopPageAnimation(Page oldPage, Page newPage, boolean hint) {
        oldPage.getView().startAnimation(mOutAnimation);
    }

    @Override
    public int getAnimationDuration() {
        return ANIMATION_DURATION;
    }
}
