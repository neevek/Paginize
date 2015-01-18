package net.neevek.android.demo.paginize.pages.viewpager;

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
public class ViewPageSubPage2 extends ViewWrapper {
  @InjectView(R.id.tv_text)
  private TextView mTvSimpleText;

  public ViewPageSubPage2(PageActivity pageActivity) {
    super(pageActivity);

    mTvSimpleText.setText("Try swiping from left to right");
  }
}
