package net.neevek.android.demo.paginize.pages;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
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

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 8/6/16.
 */
@PageLayout(R.layout.page_frame)
public abstract class FramePage extends Page {
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbToolbar;

  @InjectView(R.id.layout_content_container)
  private View mLayoutContainer;

  private View mLayoutLoading;
  private View mLayoutError;

  private Animation mAminFadeIn;
  private Animation mAminFadeOut;

  public FramePage(PageActivity pageActivity) {
    super(pageActivity);

    if (getContext().getPageCount() > 0) {
      ToolbarHelper.setNavigationIconEnabled(mTbToolbar, true, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onNavigationIconClicked(v);
        }
      });
    }
  }

  protected final void setupMenu(@MenuRes int menuResId) {
    ToolbarHelper.setupMenu(mTbToolbar, menuResId, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        return FramePage.this.onMenuItemClick(item);
      }
    });
  }

  protected final Toolbar getToolbar() {
    return mTbToolbar;
  }

  protected final void setTitle(String title) {
    mTbToolbar.setTitle(title);
  }

  protected final void setNavigationIcon(@DrawableRes int resId) {
    mTbToolbar.setNavigationIcon(resId);
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
  public void onHide() {
    InputMethodManager imm = (InputMethodManager) getContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  protected void showLoadingUI() {
    showLoadingUI(null);
  }

  protected void showLoadingUI(final CharSequence text) {
    if (getContext().getMainLooper() == Looper.myLooper()) {
      showLoadingUIInternal(text);

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showLoadingUIInternal(text);
        }
      });
    }
  }

  protected void showErrorUI() {
    showErrorUI(null, true, null, null);
  }

  /**
   * @param text the error text, may be null, default is "Loading Failed"
   * @param showRetryButton - whether to show the retry button
   * @param retryButtonText may be null, default is "Retry"
   * @param errorDrawable a default icon will be used if null
   */
  protected void showErrorUI(final CharSequence text,
                             final boolean showRetryButton,
                             final CharSequence retryButtonText,
                             final Drawable errorDrawable) {
    if (getContext().getMainLooper() == Looper.myLooper()) {
      showErrorUIInternal(text, showRetryButton, retryButtonText, errorDrawable);

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showErrorUIInternal(text, showRetryButton, retryButtonText, errorDrawable);
        }
      });
    }
  }

  protected void showContentUI() {
    if (getContext().getMainLooper() == Looper.myLooper()) {
      showContentUIInternal();

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          showContentUIInternal();
        }
      });
    }
  }

  private void showLoadingUIInternal(CharSequence text) {
    if (mLayoutLoading == null) {
      mLayoutLoading = ((ViewStub) findViewById(R.id.stub_loading_layout)).inflate();
    }

    if (mLayoutError != null && mLayoutError.getVisibility() != View.GONE) {
      mLayoutError.setVisibility(View.GONE);
      mLayoutError.startAnimation(getAminFadeOut());
    }

    if (text != null) {
      ((TextView)mLayoutLoading.findViewById(R.id.tv_loading_text)).setText(text);
    }

    mLayoutContainer.setVisibility(View.GONE);
    mLayoutContainer.startAnimation(getAminFadeOut());

    mLayoutLoading.setVisibility(View.VISIBLE);
  }

  private void showErrorUIInternal(CharSequence text,
                                   boolean showRetryButton,
                                   CharSequence retryButtonText,
                                   Drawable errorDrawable) {
    if (mLayoutError == null) {
      mLayoutError = ((ViewStub) findViewById(R.id.stub_error_layout)).inflate();
    }

    if (showRetryButton) {
      Button btnRetry = (Button)mLayoutError.findViewById(R.id.btn_retry);
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
      mLayoutError.findViewById(R.id.btn_retry).setVisibility(View.GONE);
    }

    if (errorDrawable != null) {
      ((ImageView)mLayoutError.findViewById(R.id.iv_error)).setImageDrawable(errorDrawable);
    }

    if (mLayoutLoading != null && mLayoutLoading.getVisibility() != View.GONE) {
      mLayoutLoading.setVisibility(View.GONE);
      mLayoutLoading.startAnimation(getAminFadeOut());
    }

    if (text != null) {
      ((TextView)mLayoutError.findViewById(R.id.tv_error_text)).setText(text);
    }

    if (mLayoutContainer.getVisibility() != View.GONE) {
      mLayoutContainer.setVisibility(View.GONE);
      mLayoutContainer.startAnimation(getAminFadeOut());
    }

    mLayoutError.setVisibility(View.VISIBLE);
    mLayoutError.startAnimation(getAminFadeIn());
  }

  private void showContentUIInternal() {
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

  protected boolean isContentUIVisible() {
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
