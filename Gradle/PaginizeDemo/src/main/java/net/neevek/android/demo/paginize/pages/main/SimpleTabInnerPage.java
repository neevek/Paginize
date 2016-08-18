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
@PageLayout(R.layout.page_simple_tab_content)
public class SimpleTabInnerPage extends InnerPage {
  @InjectView(R.id.tv_text)
  private TextView mTvText;

  public SimpleTabInnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  public SimpleTabInnerPage setText(String text) {
    mTvText.setText(text);
    return this;
  }
}
