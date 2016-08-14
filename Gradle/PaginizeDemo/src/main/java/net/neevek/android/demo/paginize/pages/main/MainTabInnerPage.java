package net.neevek.android.demo.paginize.pages.main;

import android.widget.TextView;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by xiaohei on 8/14/16.
 */
@PageLayout(R.layout.page_main_tab_inner_page)
public class MainTabInnerPage extends InnerPage {
  @InjectView(R.id.tv_text)
  private TextView mTvText;

  public MainTabInnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  public MainTabInnerPage setText(String text) {
    mTvText.setText(text);
    return this;
  }
}
