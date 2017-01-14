package net.neevek.android.lib.paginizecontrib.page;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectViewByName;
import net.neevek.android.lib.paginize.annotation.PageLayoutName;
import net.neevek.android.lib.paginizecontrib.P;
import net.neevek.android.lib.paginizecontrib.R;
import net.neevek.android.lib.paginizecontrib.util.ToolbarHelper;

/**
 * Created by neevek on 07/01/2017.
 */
@PageLayoutName(P.layout.paginize_contrib_page_base)
public class BasePage extends Page {
  @InjectViewByName(P.id.paginize_contrib_layout_content_container)
  private View mLayoutContainer;

  private View mLayoutLoading;
  private View mLayoutError;

  private View mViewToolbarContainer;
  private Toolbar mToolbar;

  private Animation mAminFadeIn;
  private Animation mAminFadeOut;

  public BasePage(PageActivity pageActivity) {
    super(pageActivity);
  }

  protected void setToolbarEnabled(boolean enabled) {
    if (enabled) {
      if (mViewToolbarContainer != null &&
          mViewToolbarContainer.getVisibility() == View.VISIBLE) {
        return;
      }

      if (mViewToolbarContainer == null) {
        mViewToolbarContainer = ((ViewStub)findViewById(
            R.id.paginize_contrib_stub_layout_toolbar)).inflate();

        mToolbar = (Toolbar) mViewToolbarContainer
            .findViewById(R.id.paginize_contrib_toolbar);
      }

      mViewToolbarContainer.setVisibility(View.VISIBLE);

      if (getContext().getPageCount() > 0) {
        ToolbarHelper.setNavigationIconEnabled(mToolbar, true, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onNavigationIconClicked(v);
          }
        });
      }

    } else if (mViewToolbarContainer != null) {
      mViewToolbarContainer.setVisibility(View.GONE);
    }
  }

  protected final void setMenu(@MenuRes int menuResId) {
    setToolbarEnabled(true);

    ToolbarHelper.setupMenu(mToolbar, menuResId, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        return BasePage.this.onMenuItemClick(item);
      }
    });
  }

  protected final Toolbar getToolbar() {
    return mToolbar;
  }

  protected final void setTitle(@StringRes int resId) {
    setToolbarEnabled(true);
    mToolbar.setTitle(resId);
  }

  protected final void setTitle(CharSequence title) {
    setToolbarEnabled(true);
    mToolbar.setTitle(title);
  }

  protected final void setNavigationIcon(@DrawableRes int resId) {
    setToolbarEnabled(true);
    mToolbar.setNavigationIcon(resId);
  }

  protected final void setNavigationIcon(Drawable icon) {
    setToolbarEnabled(true);
    mToolbar.setNavigationIcon(icon);
  }

  protected void onNavigationIconClicked(View v) {
    hide(true);
  }

  protected boolean onMenuItemClick(MenuItem item) {
    return false;
  }

  protected void onRetryButtonClicked() {

  }

  @Override
  public void onDestroy() {
    InputMethodManager imm = (InputMethodManager) getContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  protected void showLoadingView() {
    showLoadingView(null);
  }

  protected void showLoadingView(@StringRes int resId) {
    showLoadingView(getString(resId));
  }

  protected void showLoadingView(final CharSequence text) {
    if (getContext().getMainLooper() == Looper.myLooper()) {
      showLoadingViewInternal(text);

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showLoadingViewInternal(text);
        }
      });
    }
  }

  protected void showErrorView() {
    showErrorView(null, null, false, null);
  }

  /**
   * @param errorTextResId the error text, may be 0, default is "Loading Failed"
   * @param errorIconResId the error icon, may be 0, default icon will be used if 0
   * @param showRetryButton whether to show the retry button
   * @param retryButtonTextResId may be 0, default is "Retry"
   */
  protected void showErrorView(@StringRes int errorTextResId,
                               @DrawableRes int errorIconResId,
                               boolean showRetryButton,
                               @StringRes int retryButtonTextResId) {
    Drawable errorIcon;
    if (Build.VERSION.SDK_INT >= 21) {
      errorIcon = getResources()
          .getDrawable(errorIconResId, getContext().getTheme());
    } else {
      errorIcon = getResources()
          .getDrawable(errorIconResId);
    }

    showErrorView(
        getString(errorTextResId),
        errorIcon,
        showRetryButton,
        getString(retryButtonTextResId)
        );
  }

  /**
   * @param errorText the error text, may be null, default is "Loading Failed"
   * @param errorIcon the error icon, may be null, default icon will be used if null
   * @param showRetryButton whether to show the retry button
   * @param retryButtonText may be null, default is "Retry"
   */
  protected void showErrorView(final CharSequence errorText,
                               final Drawable errorIcon,
                               final boolean showRetryButton,
                               final CharSequence retryButtonText) {
    if (getContext().getMainLooper() == Looper.myLooper()) {
      showErrorViewInternal(errorText, showRetryButton, retryButtonText, errorIcon);

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showErrorViewInternal(errorText, showRetryButton, retryButtonText, errorIcon);
        }
      });
    }
  }

  protected void showContentView() {
    if (isContentViewVisible()) {
      return;
    }

    if (getContext().getMainLooper() == Looper.myLooper()) {
      showContentViewInternal();

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showContentViewInternal();
        }
      });
    }
  }

  private void showLoadingViewInternal(CharSequence text) {
    if (mLayoutLoading == null) {
      mLayoutLoading = ((ViewStub) findViewById(R.id.paginize_contrib_stub_loading_layout)).inflate();
    }

    if (mLayoutContainer != null && mLayoutContainer.getVisibility() != View.GONE) {
      mLayoutContainer.setVisibility(View.GONE);
    }

    if (mLayoutError != null && mLayoutError.getVisibility() != View.GONE) {
      mLayoutError.setVisibility(View.GONE);
      mLayoutError.startAnimation(getAminFadeOut());
    }

    TextView tvLoading = (TextView)mLayoutLoading.findViewById(R.id.paginize_contrib_tv_loading_text);
    if (text != null) {
      tvLoading.setVisibility(View.VISIBLE);
      tvLoading.setText(text);
    } else {
      tvLoading.setVisibility(View.GONE);
    }

    mLayoutLoading.setVisibility(View.VISIBLE);
  }

  private void showErrorViewInternal(CharSequence text,
                                     boolean showRetryButton,
                                     CharSequence retryButtonText,
                                     Drawable errorDrawable) {
    if (mLayoutError == null) {
      mLayoutError = ((ViewStub) findViewById(R.id.paginize_contrib_stub_error_layout)).inflate();
    }

    if (showRetryButton) {
      Button btnRetry = (Button)mLayoutError.findViewById(R.id.paginize_contrib_btn_retry);
      if (retryButtonText != null) {
        btnRetry.setText(retryButtonText);
      }

      btnRetry.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onRetryButtonClicked();
        }
      });

    } else {
      mLayoutError.findViewById(R.id.paginize_contrib_btn_retry).setVisibility(View.GONE);
    }

    if (errorDrawable != null) {
      ((ImageView)mLayoutError.findViewById(R.id.paginize_contrib_iv_error)).setImageDrawable(errorDrawable);
    }

    if (mLayoutLoading != null && mLayoutLoading.getVisibility() != View.GONE) {
      mLayoutLoading.setVisibility(View.GONE);
      mLayoutLoading.startAnimation(getAminFadeOut());
    }

    if (text != null) {
      ((TextView)mLayoutError.findViewById(R.id.paginize_contrib_tv_error_text)).setText(text);
    }

    if (mLayoutContainer.getVisibility() != View.GONE) {
      mLayoutContainer.setVisibility(View.GONE);
      mLayoutContainer.startAnimation(getAminFadeOut());
    }

    mLayoutError.setVisibility(View.VISIBLE);
    mLayoutError.startAnimation(getAminFadeIn());
  }

  private void showContentViewInternal() {
    if (mLayoutLoading != null && mLayoutLoading.getVisibility() != View.GONE) {
      mLayoutLoading.setVisibility(View.GONE);
      mLayoutLoading.startAnimation(getAminFadeOut());
    }

    if (mLayoutError != null && mLayoutError.getVisibility() != View.GONE) {
      mLayoutError.setVisibility(View.GONE);
      mLayoutError.startAnimation(getAminFadeOut());
    }

    mLayoutContainer.setVisibility(View.VISIBLE);
    mLayoutContainer.startAnimation(getAminFadeIn());
  }

  protected boolean isContentViewVisible() {
    return mLayoutContainer.getVisibility() == View.VISIBLE;
  }

  public Animation getAminFadeIn() {
    if (mAminFadeIn == null) {
      mAminFadeIn = new AlphaAnimation(0f, 1f);
      mAminFadeIn.setDuration(200);
    }
    return mAminFadeIn;
  }

  public Animation getAminFadeOut() {
    if (mAminFadeOut == null) {
      mAminFadeOut = new AlphaAnimation(1f, 0f);
      mAminFadeOut.setDuration(200);
    }
    return mAminFadeOut;
  }
}
