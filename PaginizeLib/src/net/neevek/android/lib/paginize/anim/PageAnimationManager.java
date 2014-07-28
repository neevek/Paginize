package net.neevek.android.lib.paginize.anim;

import net.neevek.android.lib.paginize.Page;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 10:53 AM
 */
public interface PageAnimationManager {
    void onPushPageAnimation(Page oldPage, Page newPage, boolean hint);
    void onPopPageAnimation(Page oldPage, Page newPage, boolean hint);
    int getAnimationDuration();
}
