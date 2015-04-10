package net.neevek.android.demo.paginize.pages.viewpager;

import android.util.Log;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 6/16/14.
 */
@PageLayout(R.layout.page_simple_text)
public class ViewPageSubPage1 extends ViewWrapper {
  private final static String TAG = ViewPageSubPage1.class.getSimpleName();

  @InjectView(R.id.tv_text)
  private TextView mTvSimpleText;

  public ViewPageSubPage1(PageActivity pageActivity) {
    super(pageActivity);

    mTvSimpleText.setText("Try swiping from right to left");
  }

  @Override
  public void onAttached() {
    Log.d(TAG, "onAttached() called: " + this);
  }

  @Override
  public void onDetached() {
    Log.d(TAG, "onDetached() called: " + this);
  }
}
