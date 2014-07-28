package net.neevek.android.lib.paginize.anim;

import net.neevek.android.lib.paginize.Page;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 10/17/13
 * Time: 10:53 AM
 */
public interface PageAnimator {
    /**
     * called when a new Page is pushed onto the stack
     *
     * @param oldPage the page on top of the stack before the newPage is pushed, may be null
     * @param newPage the new page to be pushed onto the stack
     * @param hint a context related value used by the implementation
     * @return true if implemented
     */
    boolean onPushPageAnimation(Page oldPage, Page newPage, boolean hint);

    /**
     * called when a Page is popped out from stack
     *
     * @param oldPage the page on top of the stack before the it is popped
     * @param newPage the page on top of the stack after oldPage is popped, may be null
     * @param hint a context related value used by the implementation
     * @return true if implemented
     */
    boolean onPopPageAnimation(Page oldPage, Page newPage, boolean hint);
    int getAnimationDuration();
}
