package net.neevek.android.lib.paginize;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

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
 * An adapter that provides ViewWrappers for
 * {@link net.neevek.android.lib.paginize.ViewPagerPage}
 * and {@link net.neevek.android.lib.paginize.ViewPagerInnerPage}
 *
 * @see net.neevek.android.lib.paginize.ViewPagerPage
 * @see net.neevek.android.lib.paginize.ViewPagerInnerPage
 */
public abstract class PagePagerAdapter extends PagerAdapter {
  public abstract ViewWrapper getItem(int position);

  /**
   * @return a ViewWrapper at the position
   */
  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    ViewWrapper viewWrapper = getItem(position);
    container.addView(viewWrapper.getView());
    viewWrapper.onAttached();
    return viewWrapper;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    ViewWrapper viewWrapper = (ViewWrapper) object;
    container.removeView(viewWrapper.getView());
    viewWrapper.onDetached();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    ViewWrapper viewWrapper = (ViewWrapper) object;
    return viewWrapper.getView() == view;
  }
}
