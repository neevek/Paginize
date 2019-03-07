package net.neevek.android.demo.paginize.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;

import net.neevek.android.demo.paginize.R;

import androidx.annotation.MenuRes;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by neevek on 8/13/16.
 */
public class ToolbarHelper {
  @SuppressLint("ResourceType")
  public static void setupMenu(Toolbar toolbar,
                               @MenuRes int menuResId,
                               Toolbar.OnMenuItemClickListener listener) {
    if (menuResId > 0) {
      toolbar.inflateMenu(menuResId);
      if (listener != null) {
        toolbar.setOnMenuItemClickListener(listener);
      }
    }
  }

  public static void setNavigationIconEnabled(Toolbar toolbar,
                                              boolean enable,
                                              View.OnClickListener listener) {
    Drawable navIcon = toolbar.getNavigationIcon();
    if (enable) {
      if (navIcon != null) {
        navIcon.setVisible(true, false);
      } else {
        toolbar.setNavigationIcon(R.drawable.btn_nav_back);
        if (listener != null) {
          toolbar.setNavigationOnClickListener(listener);
        }
      }
    } else if (navIcon != null){
      navIcon.setVisible(false, false);
    }
  }
}
