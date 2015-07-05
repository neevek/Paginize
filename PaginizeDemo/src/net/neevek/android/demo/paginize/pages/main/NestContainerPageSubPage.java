package net.neevek.android.demo.paginize.pages.main;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.other.AlertPage;
import net.neevek.android.demo.paginize.pages.other.ListPage;
import net.neevek.android.demo.paginize.pages.other.TestPage;
import net.neevek.android.demo.paginize.pages.viewpager.MyViewPagerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.ListenerMarker;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.nest_container_page_sub_page)
public class NestContainerPageSubPage extends InnerPage {
  private final static String TAG = NestContainerPageSubPage.class.getSimpleName();

  @InjectView(R.id.tv_text)
  private TextView mTvText;

  public NestContainerPageSubPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  public NestContainerPageSubPage setText(String text) {
    mTvText.setText(text);
    return this;
  }

  @Override
  public void onShow(Object arg) {
    super.onShow(arg);
    Log.d(TAG, "onShow called");
  }

  @Override
  public void onShown(Object obj) {
    super.onShown(obj);
    Log.d(TAG, "onShown called");
  }

  @Override
  public void onHide() {
    super.onHidden();
    Log.d(TAG, "onHide called");
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.d(TAG, "onHidden called");
  }
}
