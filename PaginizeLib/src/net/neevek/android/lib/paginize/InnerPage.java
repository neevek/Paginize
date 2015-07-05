package net.neevek.android.lib.paginize;

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
 * An InnerPage encapsulates a View(usually a layout with complex UI components),
 * which is to be put into a ViewGroup and finally be shown on screen.
 * <p/>
 * InnerPage is designed to be used in a layout that contains multiple views
 * that can be swapped at runtime, such as a Page with multiple tabs.
 * <p/>
 * InnerPage is managed by InnerPageManager, we call InnerPageManager.setPage()
 * method to set an InnerPage as the current page.
 *
 * @see ContainerPage
 */
public abstract class InnerPage extends ViewWrapper {
  private ViewWrapper mInnerPageContainer;

  public InnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer.getContext());
    mInnerPageContainer = innerPageContainer;
  }

  public void onShown(Object obj) {
    if (mViewCurrentFocus != null) {
      mViewCurrentFocus.requestFocus();
    }
  }

  public void onHidden() {
    mViewCurrentFocus = getContext().getCurrentFocus();
  }

  public ViewWrapper getInnerPageContainer() {
    return mInnerPageContainer;
  }
}