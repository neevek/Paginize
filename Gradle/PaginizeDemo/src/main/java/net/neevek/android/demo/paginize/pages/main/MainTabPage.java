package net.neevek.android.demo.paginize.pages.main;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
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
 * Created by xiaohei on 8/13/16.
 */
@PageLayout(R.layout.page_main_tab)
@InnerPageContainerLayoutResId(R.id.layout_content_container)
public class MainTabPage extends ViewPagerPage {
  @InjectView(R.id.layout_tab_container)
  private TabLayout mLayoutTabContainer;
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;

  public MainTabPage(PageActivity pageActivity) {
    super(pageActivity);

    setupTabLayout(mLayoutTabContainer, true);
    addPage(new MainTabInnerPage(this).setText("page1"), "TAB1", R.drawable.home);
    addPage(new MainTabInnerPage(this).setText("page2"), "TAB2", R.drawable.home);
    addPage(new MainTabInnerPage(this).setText("page3"), "TAB3", R.drawable.home);
    addPage(new MainTabInnerPage(this).setText("page4"), "TAB4", R.drawable.home);

    ToolbarHelper.setNavigationIconEnabled(mTbHeaderBar, true, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide(true);
      }
    });

    mTbHeaderBar.setTitle("主页");
    ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main3, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        removePage(0);
        return true;
      }
    });
  }


}
