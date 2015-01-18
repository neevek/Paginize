package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import net.neevek.android.lib.paginize.annotation.ViewPagerResId;
import net.neevek.android.lib.paginize.exception.InjectFailedException;

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
 * This class encapsulates a {@link android.support.v4.view.ViewPager},
 * which makes it easy to create swipe pages.
 *
 * @see net.neevek.android.lib.paginize.ViewPagerInnerPage
 * @see net.neevek.android.lib.paginize.PagePagerAdapter
 */
public abstract class ViewPagerPage extends Page {
  private ViewPager mViewPager;

  public ViewPagerPage(PageActivity pageActivity) {
    super(pageActivity);

    Class clazz = getClass();

    ViewPagerResId resIdAnnotation = null;

    try {
      do {
        if (clazz.isAnnotationPresent(ViewPagerResId.class)) {
          resIdAnnotation = (ViewPagerResId) clazz.getAnnotation(ViewPagerResId.class);
          break;
        }
      } while ((clazz = clazz.getSuperclass()) != ViewPagerPage.class);

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }

    if (resIdAnnotation == null) {
      throw new IllegalStateException("Must specify a ViewPager resource Id for " + clazz.getSimpleName() + " with the @ViewPagerResId annotation.");
    }

    View view = getView().findViewById(resIdAnnotation.value());
    if (view == null) {
      throw new IllegalStateException("Can not find the View with the specified resource ID: " + resIdAnnotation.value());
    }
    if (!(view instanceof ViewPager)) {
      throw new IllegalStateException("The specified View with @ViewPagerResId is not of type ViewPager.");
    }

    mViewPager = (ViewPager) view;
  }

  protected ViewPager getViewPager() {
    return mViewPager;
  }

  private PagePagerAdapter getPagePagerAdapter() {
    PagerAdapter adapter = mViewPager.getAdapter();
    if (adapter instanceof PagePagerAdapter) {
      return (PagePagerAdapter) adapter;
    }
    return null;
  }

  public void onAttach() {
    // do nothing here, onAttach is called on the ViewWrapper when
    // it is added to ViewPager
  }

  public void onDetach() {
    // do nothing here, onAttach is called on the ViewWrapper when
    // it is removed from ViewPager
  }

  public boolean onBackPressed() {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      return adapter.getItem(mViewPager.getCurrentItem()).onBackPressed();
    }

    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }
  }

  public void onPause() {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onPause();
    }
  }

  public void onResume() {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onResume();
    }
  }

  public void onShown(Object arg) {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onShown(arg);
    }
  }

  public void onHidden() {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onHidden();
    }
  }

  public void onCovered() {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onCovered();
    }
  }

  public void onUncovered(Object arg) {
    PagePagerAdapter adapter = getPagePagerAdapter();
    if (adapter != null && adapter.getCount() > 0) {
      adapter.getItem(mViewPager.getCurrentItem()).onUncovered(arg);
    }
  }
}
