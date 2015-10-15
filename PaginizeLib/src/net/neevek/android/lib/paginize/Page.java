package net.neevek.android.lib.paginize;

import android.view.View;
import net.neevek.android.lib.paginize.anim.PageAnimator;

/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * A Page encapsulates a View(usually a layout with complex UI components),
 * which is to be put into a ViewGroup and finally be shown on screen.
 * <p/>
 * Page is managed by PageManager, we call variants of the PageManager.pushPage()
 * method to put a Page in a stack, which is maintained by PageManager
 * <p/>
 * A Page is designed to wrap a full screen View(the status bar is not counted
 * in here), as compared to InnerPage, which wraps a View that occupies only part
 * of the screen.
 *
 * @see ContainerPage
 * @see net.neevek.android.lib.paginize.InnerPage
 */
public abstract class Page extends ViewWrapper implements PageAnimator {
  // default page type should be normal here.
  private TYPE mType = TYPE.TYPE_NORMAL;
  // this as passed as the argument for onUncovered
  private Object mReturnData;

  public static enum TYPE {
    TYPE_NORMAL,
    TYPE_DIALOG,
  }

  public Page(PageActivity pageActivity) {
    super(pageActivity);
  }

  protected void setType(TYPE type) {
    mType = type;
  }

  protected TYPE getType() {
    return mType;
  }

  protected Object getReturnData() {
    return mReturnData;
  }

  protected void setReturnData(Object data) {
    mReturnData = data;
  }

  protected PageManager getPageManager() {
    return getContext().getPageManager();
  }

  public void show() {
    show(null, false);
  }

  public void show(Object arg, boolean animated) {
    getPageManager().pushPage(this, arg, animated);
  }

  protected void hide(boolean animated) {
    if (getPageManager().getTopPage() == this) {
      getPageManager().popPage(animated);
    }
  }

  protected void hideDelayed(final boolean animated) {
    hideDelayed(animated, 500);
  }

  protected void hideDelayed(final boolean animated, int delayed) {
    if (getPageManager().getTopPage() == this) {
      getView().postDelayed(new Runnable() {
        @Override
        public void run() {
          getPageManager().popPage(animated);
        }
      }, delayed);
    }
  }

  public boolean isKeptInStack() {
    return getContext().getPageManager().isPageKeptInStack(this);
  }

  @Override
  public boolean onPushPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    return false;
  }

  @Override
  public boolean onPopPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    return false;
  }

  @Override
  public int getAnimationDuration() {
    return -1;
  }
}