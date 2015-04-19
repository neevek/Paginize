package net.neevek.android.lib.paginize;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimator;
import net.neevek.android.lib.paginize.exception.InjectFailedException;
import net.neevek.android.lib.paginize.util.AnnotationUtils;
import net.neevek.android.lib.paginize.util.ViewFinder;

import java.lang.reflect.InvocationTargetException;
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
 * To use the Paginize framework, the host Activity must extend PageActivity,
 * which holds a PageManager to manage all pages.
 *
 * @see net.neevek.android.lib.paginize.PageManager
 */
public class PageActivity extends Activity {
  private PageManager mPageManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPageManager = new PageManager(this, (ViewGroup) findViewById(android.R.id.content));

    try {
      initAnnotatedFields();

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }
  }

  private void initAnnotatedFields() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    Class clazz = getClass();

    List<Class> list = new ArrayList<Class>();
    do {
      list.add(clazz);

      if (mPageManager.getPageAnimator() == null) {
        InjectPageAnimator pamAnnotation = (InjectPageAnimator) clazz.getAnnotation(InjectPageAnimator.class);
        if (pamAnnotation != null) {
          mPageManager.setPageAnimator(pamAnnotation.value().newInstance());
        }
      }
    } while ((clazz = clazz.getSuperclass()) != PageActivity.class);

    ViewFinder viewFinder = new ViewFinder() {
      public View findViewById(int id) {
        return PageActivity.this.findViewById(id);
      }
    };
    for (int i = list.size() - 1; i >= 0; --i) {
      AnnotationUtils.initAnnotatedFields(list.get(i), this, viewFinder, false);
    }
  }

  public PageManager getPageManager() {
    return mPageManager;
  }

  public void hideTopPage() {
    getPageManager().popPage(false, false);
  }

  public int getPageCount() {
    return mPageManager.getPageCount();
  }

  @Override
  public void onBackPressed() {
    if (!mPageManager.onBackPressed()) {
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mPageManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onResume() {
    mPageManager.onResume();
    super.onResume();
  }

  @Override
  protected void onPause() {
    mPageManager.onPause();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    mPageManager.onDestroy();
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mPageManager.onKeyDown(keyCode, event)) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mPageManager.onKeyUp(keyCode, event)) {
      return true;
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mPageManager.onTouchEvent(event)) {
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mPageManager.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mPageManager.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mPageManager.onRestoreInstanceState(savedInstanceState);
  }
}
