package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutName;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
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
 * InnerPageContainerManager wraps the logics for initializing an
 * InnerPageContainer( ContainerPage, ContainerInnerPage, ViewPagerPage,
 * ViewPagerInnerPage )
 */
class InnerPageContainerManager {
  private ViewWrapper mInnerPageContainer;
  private ViewGroup mContainerView;
  private InnerPage mCurrentInnerPage;

  InnerPageContainerManager(ViewWrapper innerPageContainer) {
    mInnerPageContainer = innerPageContainer;
    Class clazz = innerPageContainer.getClass();

    int resId = 0;
    try {
      do {
        if (clazz.isAnnotationPresent(InnerPageContainerLayoutResId.class)) {
          resId = ((InnerPageContainerLayoutResId)
              clazz.getAnnotation(InnerPageContainerLayoutResId.class)).value();
          break;
        } else if (clazz.isAnnotationPresent(InnerPageContainerLayoutName.class)) {
          String name = ((InnerPageContainerLayoutName)
              clazz.getAnnotation(InnerPageContainerLayoutName.class)).value();
          resId = innerPageContainer.getResources().getIdentifier(
                  name, "id", innerPageContainer.getContext().getPackageName());
          break;
        }
      } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }

    if (resId == 0) {
      throw new IllegalStateException("Must specify a valid layout for " +
          "InnerPageContainer with the @InnerPageContainerLayoutResId " +
          "or @InnerPageContainerLayoutName annotation.");
    }

    View container =
        innerPageContainer.getView().findViewById(resId);
    if (container == null) {
      throw new IllegalStateException("Can not find the layout with the " +
          "specified resource ID: " + resId);
    }
    if (!(container instanceof ViewGroup)) {
      throw new IllegalStateException("The specified layout for " +
          "InnerPageContainer is not of type ViewGroup.");
    }

    mContainerView = (ViewGroup)container;
  }

  ViewWrapper getInnerPageContainer() {
    return mInnerPageContainer;
  }

  ViewGroup getContainerView() {
    return mContainerView;
  }

  void setCurrentInnerPage(InnerPage page) {
    mCurrentInnerPage = page;
  }

  InnerPage getCurrentInnerPage() {
    return mCurrentInnerPage;
  }

  boolean onBackPressed() {
    if (mCurrentInnerPage != null) {
      return mCurrentInnerPage.onBackPressed();
    }

    return false;
  }

  void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onActivityResult(requestCode, resultCode, data);
    }
  }

  void onPause() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onPause();
    }
  }

  void onResume() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onResume();
    }
  }

  boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mCurrentInnerPage != null) {
      if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
        return mCurrentInnerPage.onMenuPressed();

      } else {
        return mCurrentInnerPage.onKeyDown(keyCode, event);
      }
    }
    return false;
  }

  boolean onKeyUp(int keyCode, KeyEvent event) {
    if (mCurrentInnerPage != null) {
      return mCurrentInnerPage.onKeyUp(keyCode, event);
    }
    return false;
  }

  boolean onTouchEvent(MotionEvent event) {
    if (mCurrentInnerPage != null) {
      return mCurrentInnerPage.onTouchEvent(event);
    }
    return false;
  }

  void onConfigurationChanged(Configuration newConfig) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onConfigurationChanged(newConfig);
    }
  }

  void onShow() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onShow();
    }
  }

  void onShown() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onShown();
    }
  }

  void onHide() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onHide();
    }
  }

  void onHidden() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onHidden();
    }
  }

  void onCover() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onCover();
    }
  }

  void onCovered() {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onCovered();
    }
  }

  void onUncover(Object arg) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onUncover(arg);
    }
  }

  void onUncovered(Object arg) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onUncovered(arg);
    }
  }

  void onSaveInstanceState(Bundle outState) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onSaveInstanceState(outState);
    }
  }

  void onRestoreInstanceState(Bundle savedInstanceState) {
    if (mCurrentInnerPage != null) {
      mCurrentInnerPage.onRestoreInstanceState(savedInstanceState);
    }
  }
}
