package net.neevek.android.demo.paginize.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.LinearLayout;

import net.neevek.android.demo.paginize.R;

import androidx.core.view.ViewCompat;

/**
 * A custom LinearLayout that draws colorPrimaryDark for the status bar
 * Created by neevek on 8/13/16.
 */
public class CustomLinearLayout extends LinearLayout {
  private Rect mInsets;
  private boolean mPaddingSet;
  private boolean mUseDefaultStatusBarBackground = true;
  private Drawable mStatusBarBackground;

  public CustomLinearLayout(Context context) {
    super(context);
    applyFitsSystemWindowIfNeeded();
  }

  public CustomLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    applyFitsSystemWindowIfNeeded();
  }

  @TargetApi(11)
  public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    applyFitsSystemWindowIfNeeded();
  }

  @TargetApi(21)
  public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    applyFitsSystemWindowIfNeeded();
  }

  public void setStatusBarBackground(Drawable statusBarBackground) {
    mUseDefaultStatusBarBackground = false;
    mStatusBarBackground = statusBarBackground;
  }

  private void applyFitsSystemWindowIfNeeded() {
    if (Build.VERSION.SDK_INT >= 20 && ViewCompat.getFitsSystemWindows(this)) {
      setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (!mPaddingSet && ViewCompat.getFitsSystemWindows(this)) {
      ViewCompat.requestApplyInsets(this);
      setWillNotDraw(false);
      mPaddingSet = true;
    }
  }

  @TargetApi(20)
  public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
    if (mInsets == null) {
      mInsets = new Rect();
    }
    mInsets.set(insets.getSystemWindowInsetLeft(),
        insets.getSystemWindowInsetTop(),
        insets.getSystemWindowInsetRight(),
        insets.getSystemWindowInsetBottom());

    setPadding(mInsets.left, mInsets.top, mInsets.right, mInsets.bottom);
    return insets;
  }

  @Override
  protected void onDraw(Canvas c) {
    super.onDraw(c);
    if (mInsets != null && mInsets.top > 0 &&
        (mStatusBarBackground != null || mUseDefaultStatusBarBackground)) {
      if (mStatusBarBackground == null && mUseDefaultStatusBarBackground) {
        TypedArray a = getContext().getTheme()
            .obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
        mStatusBarBackground = new ColorDrawable(a.getColor(0, 0));
        a.recycle();
      }
      mStatusBarBackground.setBounds(0, 0, getWidth(), mInsets.top);
      mStatusBarBackground.draw(c);
    }
  }
}
