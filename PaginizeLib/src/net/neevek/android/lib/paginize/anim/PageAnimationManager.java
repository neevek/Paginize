package net.neevek.android.lib.paginize.anim;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 10:53 AM
 */
public interface PageAnimationManager {
    void onPushPageAnimation(View oldPageView, View newPageView, boolean hint);
    // hint only applies to TranslateAnimation
    void onPopPageAnimation(View oldPageView, View newPageView, boolean hint);
    int getAnimationDuration();
}
