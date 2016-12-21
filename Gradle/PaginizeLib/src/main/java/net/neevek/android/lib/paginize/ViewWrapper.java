package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.InsertPageLayoutByName;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.PageLayoutName;
import net.neevek.android.lib.paginize.exception.InjectFailedException;
import net.neevek.android.lib.paginize.util.AnnotationUtils;
import net.neevek.android.lib.paginize.util.ViewFinder;

import java.util.ArrayList;
import java.util.List;

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
 * This class encapsulates a {@link View}, and declares a set of
 * lifecycle methods.
 *
 * @see Page
 * @see InnerPage
 */
public abstract class ViewWrapper {
  private PageActivity mContext;
  private View mView;
  /* package */ View mViewCurrentFocus;

  public ViewWrapper(PageActivity pageActivity) {
    mContext = pageActivity;
    init();
  }

  private void init() {
    try {
      List<Class> classList = loadAnnotatedLayoutAndCollectPageClasses();
      if (classList.size() > 1) {
        resolveLayoutInheritanceIfNeeded(classList);
      }

      final ViewFinder viewFinder = getViewFinder();
      for (int i = classList.size() - 1; i >= 0; --i) {
        AnnotationUtils.initAnnotatedFields(
            classList.get(i), this, viewFinder, false);
        AnnotationUtils.handleAnnotatedConstructors(
            classList.get(i), this, viewFinder, false);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }
  }

  private void resolveLayoutInheritanceIfNeeded(List<Class> classList) {
    // -2 because a Page with @PageLayout should not have @InsertPageLayout,
    // or @InsertPageLayoutByName, which will be silently ignored.
    for (int i = classList.size() - 2; i >= 0; --i) {
      Class cls = classList.get(i);
      int layoutResId = 0;
      int parentLayoutId = 0;

      if (cls.isAnnotationPresent(InsertPageLayout.class)) {
        InsertPageLayout anno =
            (InsertPageLayout) cls.getAnnotation(InsertPageLayout.class);
        layoutResId = anno.value();
        parentLayoutId = anno.parent();

        if (layoutResId == 0) {
          throw new IllegalArgumentException("The layout name '"+ anno.value() +
                  "' specified in @InsertPageLayout is not found.");
        }

      } else if (cls.isAnnotationPresent(InsertPageLayoutByName.class)) {
        InsertPageLayoutByName anno =
                (InsertPageLayoutByName) cls.getAnnotation(InsertPageLayoutByName.class);
        if (anno.value().length() > 0) {
          layoutResId = getResources().getIdentifier(
                  anno.value(), "layout", getContext().getPackageName());
        }
        if (layoutResId == 0) {
          throw new IllegalArgumentException("The layout name '"+ anno.value() +
                  "' specified in @InsertPageLayoutByName is not found.");
        }

        if (anno.parent().length() > 0) {
          parentLayoutId = getResources().getIdentifier(
                  anno.parent(), "id", getContext().getPackageName());
          if (parentLayoutId == 0) {
            throw new IllegalArgumentException("The parent '"+ anno.parent() +
                    "' specified in @InsertPageLayoutByName is not found.");
          }
        }
      } else {

        continue;
      }

      if (parentLayoutId != 0) {
        ViewGroup root = (ViewGroup)
                mView.findViewById(parentLayoutId);
        if (root == null) {
          throw new IllegalArgumentException("The parent specified in " +
                  "@InsertPageLayout or @InsertPageLayoutByName is not found.");
        }
        mContext.getLayoutInflater()
                .inflate(layoutResId, root, true);
      } else {
        mContext.getLayoutInflater().inflate(
                layoutResId, (ViewGroup) mView, true);
      }

    }
  }

  private List<Class> loadAnnotatedLayoutAndCollectPageClasses() {
    List<Class> list = new ArrayList<Class>(4);

    Class clazz  = getClass();
    do {
      list.add(clazz);

      if (mView == null) {
        int resId = 0;
        if (clazz.isAnnotationPresent(PageLayout.class)) {
          resId = ((PageLayout) clazz.getAnnotation(PageLayout.class)).value();
        } else if (clazz.isAnnotationPresent(PageLayoutName.class)) {
          String name = ((PageLayoutName) clazz.getAnnotation(PageLayoutName.class)).value();
          resId = getResources().getIdentifier(name, "layout", getContext().getPackageName());
        }

        if (resId != 0) {
          mView = mContext.getLayoutInflater().inflate(resId, null);
        }
      }
    } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);

    if (mView == null) {
      throw new IllegalArgumentException("Must specify a layout resource" +
          " with the @PageLayout or @PageLayoutName annotation on " + clazz.getName());
    }
    return list;
  }

  private ViewFinder getViewFinder() {
    final String packageName = getContext().getPackageName();
    return new ViewFinder() {
      public View findViewById(int id) {
        return ViewWrapper.this.findViewById(id);
      }

      @Override
      public int findViewIdByName(String name) {
        return getResources().getIdentifier(name, "id", packageName);
      }
    };
  }

  protected View lazyInitializeLayout(int layoutResId) {
    return lazyInitializeLayout(layoutResId, null, false);
  }

  /**
   * inject views after the ViewWrapper is constructed
   */
  protected View lazyInitializeLayout(int layoutResId,
                                      ViewGroup root,
                                      boolean attachToRoot) {
    final View view = mContext.getLayoutInflater()
        .inflate(layoutResId, root, attachToRoot);

    final String packageName = getContext().getPackageName();
    final ViewFinder viewFinder = new ViewFinder() {
      public View findViewById(int id) {
        return view.findViewById(id);
      }

      @Override
      public int findViewIdByName(String name) {
        return getResources().getIdentifier(name, "id", packageName);
      }
    };

    try {
      Class<?> clazz = null;
      String viewWrapperClassName = ViewWrapper.class.getName();
      for (StackTraceElement trace : Thread.currentThread().getStackTrace()) {
        String className = trace.getClassName();
        if (viewWrapperClassName.equals(className)) {
          continue;
        }

        Class<?> cls = Class.forName(className);
        if (ViewWrapper.class.isAssignableFrom(cls)) {
          clazz = cls;
          break;
        }
      }

      if (clazz != null) {
        AnnotationUtils.initAnnotatedFields(clazz, this, viewFinder, true);
        AnnotationUtils.handleAnnotatedConstructors(
            clazz, this, viewFinder, true);
      } else {
        // normally this code won't be reached
        AnnotationUtils.initAnnotatedFields(getClass(), this, viewFinder, true);
        AnnotationUtils.handleAnnotatedConstructors(
            getClass(), this, viewFinder, true);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }

    return view;
  }

  public PageActivity getContext() {
    return mContext;
  }

  public View getView() {
    return mView;
  }

  protected View findViewById(int id) {
    return mView.findViewById(id);
  }

  protected String getString(int resId) {
    return mContext.getString(resId);
  }

  protected String getString(int resId, Object... args) {
    return mContext.getString(resId, args);
  }

  protected Resources getResources() {
    return mContext.getResources();
  }

  protected void hideTopPage() {
    mContext.hideTopPage();
  }

  protected boolean isAttached() {
    return mView.getParent() != null;
  }

  public boolean post(Runnable action) {
    if (mView != null) {
      return mView.post(action);
    }
    return false;
  }

  public boolean postDelayed(Runnable action, long delayMillis) {
    if (mView != null) {
      return mView.postDelayed(action, delayMillis);
    }
    return false;
  }

  /**
   * onShow is called when the page is pushed on the page stack,
   * at this point the Page is still not be visible
   *
   * @see PageManager
   */
  public void onShow() {
  }

  /**
   * onShown is called after the page is pushed on the page stack
   *
   * @see PageManager
   */
  public void onShown() {
  }

  /**
   * onHide is called before the page is popped out of the page stack,
   * at this point the Page is still visible
   *
   * @see PageManager
   */
  public void onHide() {
  }

  /**
   * onHidden is called after the page is popped out of the page stack
   *
   * @see PageManager
   */
  public void onHidden() {
  }

  /**
   * onDestroy is called after onHidden()
   *
   * @see PageManager
   */
  public void onDestroy() {
  }

  /**
   * onCover is called for the current ViewWrapper before a new
   * ViewWrapper is pushed on the page stack
   *
   * @see PageManager
   */
  public void onCover() {
    mViewCurrentFocus = getContext().getCurrentFocus();
  }

  /**
   * onCovered is called for the current ViewWrapper when a new
   * ViewWrapper is pushed on the page stack
   *
   * @see PageManager
   */
  public void onCovered() {
  }

  /**
   * onUncovered is called for the previous page before the current page
   * is popped out of the page stack
   *
   * @see PageManager
   */
  public void onUncover(Object arg) {
    if (mViewCurrentFocus != null) {
      mViewCurrentFocus.requestFocus();
    }
  }

  /**
   * onUncovered is called for the previous page when the current page
   * is popped out of the page stack
   *
   * @see PageManager
   */
  public void onUncovered(Object arg) {
  }

  /**
   * onBackPressed mirrors Activity.onBackPressed, only the current
   * page(on top of the stack) receives this call
   *
   * @see PageManager
   */
  public boolean onBackPressed() {
    return false;
  }

  /**
   * onMenuPressed is called when KeyEvent for onKeyDown() is KEYCODE_MENU,
   * only the current page(on top of the stack) receives this call
   *
   * @see PageManager
   */
  public boolean onMenuPressed() {
    return false;
  }

  /**
   * onActivityResult mirrors Activity.onActivityResult, only the current
   * page(on top of the stack) receives this call
   *
   * @see PageManager
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }

  /**
   * onPause mirrors Activity.onPause, only the current page
   * (on top of the stack) receives this call
   *
   * @see PageManager
   */
  public void onPause() {
  }

  /**
   * onResume mirrors Activity.onResume, only the current page
   * (on top of the stack) receives this call
   *
   * @see PageManager
   */
  public void onResume() {
  }

  /**
   * onKeyDown mirrors Activity.onKeyDown
   *
   * @see PageManager
   */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return false;
  }

  /**
   * onKeyUp mirrors Activity.onKeyUp
   *
   * @see PageManager
   */
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return false;
  }

  /**
   * onTouchEvent mirrors Activity.onTouchEvent
   *
   * @see PageManager
   */
  public boolean onTouchEvent(MotionEvent event) {
    return false;
  }

  /**
   * onConfigurationChanged mirrors Activity.onConfigurationChanged
   *
   * @see PageManager
   */
  public void onConfigurationChanged(Configuration newConfig) {
  }

  /**
   * onSaveInstanceState mirrors Activity.onSaveInstanceState
   *
   * @see PageManager
   */
  public void onSaveInstanceState(Bundle outState) {
  }

  /**
   * onRestoreInstanceState mirrors Activity.onRestoreInstanceState
   *
   * @see PageManager
   */
  public void onRestoreInstanceState(Bundle savedInstanceState) {
  }

  public boolean shouldSaveInstanceState() {
    return true;
  }
}
