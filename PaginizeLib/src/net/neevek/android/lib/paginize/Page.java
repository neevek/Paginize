package net.neevek.android.lib.paginize;

import net.neevek.android.lib.paginize.anim.PageAnimator;

/**
 * A Page encapsulates a View(usually a layout with complex UIs),
 * which is to be put into a ViewGroup and finally be shown on screen.
 *
 * Page is managed by PageManager, we call variants of the PageManager.pushPage()
 * method to put a Page in a stack, which is maintained by PageManager
 *
 * Date: 2/28/13
 * Time: 3:06 PM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class Page extends ViewWrapper implements PageAnimator {
    // default page type should be normal here.
    private TYPE mType = TYPE.TYPE_NORMAL;
    private Object mReturnData;

    public static enum TYPE {
        TYPE_NORMAL,
        TYPE_DIALOG,
    }

    public Page(PageActivity pageActivity) {
        super(pageActivity);
    }

    public void setType(TYPE type) {
        mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    // returns true so PageManager will keep only one instance of a certain type of Page
    // when multiple instances of that type of Page are pushed continuously onto the page stack.
    public boolean keepSingleInstance() {
        return false;
    }

    public Object getReturnData() {
        return mReturnData;
    }

    public void setReturnData(Object data) {
        mReturnData = data;
    }

    public PageManager getPageManager() {
        return mContext.getPageManager();
    }

    //**************** methods to show & hide current page ****************//
    public void show() {
        show(null, false);
    }

    public void show(Object arg, boolean animated) {
        show(arg, animated, false);
    }

    public void show(Object arg, boolean animated, boolean hint) {
        getPageManager().pushPage(this, arg, animated, hint);
    }

    protected void hide() {
        if (getPageManager().getTopPage() == this) {
            getPageManager().popPage(false, false);
        }
    }

    protected void hideWithAnimation(final boolean hint) {
        if (getPageManager().getTopPage() == this) {
            getPageManager().popPage(true, hint);
        }
    }

    protected void hideWithAnimationDelayed(final boolean hint) {
        if (getPageManager().getTopPage() == this) {
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPageManager().popPage(true, hint);
                }
            }, 500);
        }
    }

    public boolean isKeptInStack() {
        return mContext.getPageManager().isPageKeptInStack(this);
    }

    @Override
    public boolean onPushPageAnimation(Page oldPage, Page newPage, boolean hint) {
        return false;
    }

    @Override
    public boolean onPopPageAnimation(Page oldPage, Page newPage, boolean hint) {
        return false;
    }

    @Override
    public int getAnimationDuration() {
        return -1;
    }
}