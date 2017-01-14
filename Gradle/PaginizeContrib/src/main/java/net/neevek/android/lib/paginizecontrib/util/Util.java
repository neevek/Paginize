package net.neevek.android.lib.paginizecontrib.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by neevek on 09/01/2017.
 */
public class Util {
  public static float dp2px(int dp, DisplayMetrics displayMetrics) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
  }
}
