package net.neevek.android.demo.paginize.pages.general;

import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.ContainerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by xiejm on 6/4/14.
 */
@PageLayout(R.layout.page_frame_inner)
public abstract class FrameInnerPage extends ContainerPage {
  @InjectView(value = R.id.tv_title)
  TextView mTvTitle;

  public FrameInnerPage(PageActivity pageActivity) {
    super(pageActivity);
  }

  protected void setTitle(String title) {
    mTvTitle.setText(title);
  }
}
