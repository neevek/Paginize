package net.neevek.android.demo.paginize.pages.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * Created by xiaohei on 8/8/16.
 */
public class MyLayout extends LinearLayout {
  public MyLayout(Context context) {
    super(context);
//    setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  public MyLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
//    setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
//    setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
  }

  @TargetApi(21)
  public MyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public WindowInsets onApplyWindowInsets(WindowInsets insets) {
    return super.onApplyWindowInsets(insets);
  }

  @Override
  public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
    super.setOnApplyWindowInsetsListener(listener);
  }

  @TargetApi(23)
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    WindowInsets insets = getRootWindowInsets();
    System.out.println(">>>>>>>>>>>>> inserts: " + insets);

    ColorDrawable bgDrawable = new ColorDrawable(0xff998877);
    bgDrawable.setBounds(0, 0, getWidth(), insets.getSystemWindowInsetTop());
    bgDrawable.draw(canvas);
  }
}
