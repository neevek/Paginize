package net.neevek.android.demo.paginize.pages;

import android.support.design.widget.TabLayout;
import android.util.Log;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.ViewPagerInnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 8/16/16.
 */
@PageLayout(R.layout.page_nested_tab)
@InnerPageContainerLayoutResId(R.id.layout_content_container)
public class NestedSwipeableTabPage extends ViewPagerInnerPage {
  private static final String TAG = SimpleTabPage.class.getName();

  @InjectView(R.id.layout_tab_container)
  private TabLayout mLayoutTabContainer;

  @SuppressWarnings("deprecation")
  public NestedSwipeableTabPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);

    setupTabLayout(mLayoutTabContainer, true);
    addPage(new SimpleInnerPage(this).setName("Nested SimpleInnerPage 1").setHtml(
        "This is a nested sub-page, which demonstrates how a Page can be " +
        "<b>logically</b> broken down into arbitrarily small parts, so that each " +
        "part <i>tells an isolated story of its own</i>.<br><br> The benefit is much " +
        "more obvious when <b>Paginize</b> is used in big and complex projects."), "Tab1");
    addPage(new SimpleInnerPage(this).setName("Nested SimpleInnerPage 2"), "Tab2");
    addPage(new SimpleInnerPage(this).setName("Nested SimpleInnerPage 3"), "Tab3");
  }

  public void onShow() {
    super.onShow();
    Log.i(TAG, "NestedSwipeableTabPage onShow()");
  }

  @Override
  public void onShown() {
    super.onShown();
    Log.i(TAG, "NestedSwipeableTabPage onShown()");
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.i(TAG, "NestedSwipeableTabPage onHide()");
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.i(TAG, "NestedSwipeableTabPage onHidden()");
  }
}
