package net.neevek.android.demo.paginize.pages;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.ViewPagerPage;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 8/13/16.
 */
@PageLayout(R.layout.page_tab)
@InnerPageContainerLayoutResId(R.id.layout_content_container)
public class SwipeableTabPage extends ViewPagerPage {
  private static final String TAG = SwipeableTabPage.class.getName();

  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;

  public SwipeableTabPage(PageActivity pageActivity) {
    super(pageActivity);

    setupHeaderBar();
    setupTabLayout(R.id.layout_tab_container, true);
    setupInnerPages();
  }

  private void setupHeaderBar() {
    mTbHeaderBar.setTitle("SwipeableTabPage");
    ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_for_main_tab_page,
        new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (getPageCount() > 0) {
          removePage(0);
        }
        return true;
      }
    });

    ToolbarHelper.setNavigationIconEnabled(mTbHeaderBar, true, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide(true);
      }
    });
  }

  private void setupInnerPages() {
    addPage(new SimpleInnerPage(this).setName("SimpleInnerPage for Tab1").setHtml(
        "This is an <b>InnerPage</b> put inside of an <b>ViewPagerPage</b>, " +
            "which uses <i>ViewPager</i> internally to support swipe for switching " +
            "tabs. ViewPagerPage is also one of the 2 kinds of <b>InnerPageContainer</b>" +
            "(another is <b>ContainerPage</b>). With <b>Paginize</b>, screen " +
            "property is supposed to be divided into small parts of <b>InnerPage</b>s, " +
            "the granularity is controlled by you, the user of the library.<br/><br/> " +
            "<b>Page</b>s are just view wrappers, they are pushed onto a stack, this <b>Page</b> " +
            "can be popped from the stack by pressing the LEFT ARROW on the top-left corner, " +
            "pressing the BACK button of the device or even <b>swiping from the left edge of " +
            "the screen</b>, the swiping feature is provided by the library and can " +
            "be easily enabled by calling 2 methods, see the code in <b>MainActivity</b>."),
        "Tab1", R.drawable.home_selector);
    addPage(new SimpleInnerPage(this).setName("SimpleInnerPage for Tab2")
        .setHtml("This is a long long text that demonstrates scrolling. <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ <br> ~ END"), "Tab2", R.drawable.home_selector);
    addPage(new SimpleInnerPage(this).setName("SimpleInnerPage for Tab3"), "Tab3", R.drawable.home_selector);
    addPage(new NestedSwipeableTabPage(this), "Nested", R.drawable.home_selector);
  }

  @Override
  public void onShow() {
    super.onShow();
    Log.i(TAG, "SwipeableTabPage onShow()");
  }

  @Override
  public void onShown() {
    super.onShown();
    Log.i(TAG, "SwipeableTabPage onShown()");
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.i(TAG, "SwipeableTabPage onHide()");
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.i(TAG, "SwipeableTabPage onHidden()");
  }
}
