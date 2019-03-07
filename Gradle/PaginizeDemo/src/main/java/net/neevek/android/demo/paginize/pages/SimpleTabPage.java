package net.neevek.android.demo.paginize.pages;

import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.ContainerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.annotation.PageLayout;

import androidx.appcompat.widget.Toolbar;

/**
 * Created by neevek on 16/8/25.
 */
@PageLayout(R.layout.page_tab)
@InnerPageContainerLayoutResId(R.id.layout_content_container)
public class SimpleTabPage extends ContainerPage {
  private static final String TAG = SimpleTabPage.class.getName();

  @InjectView(R.id.layout_tab_container)
  private TabLayout mLayoutTabContainer;
  @InjectView(R.id.tb_header_bar)
  private Toolbar mTbHeaderBar;

  private InnerPage[] mTabPageArray;

  public SimpleTabPage(PageActivity pageActivity) {
    super(pageActivity);

    setupHeaderBar();
    setupInnerPages();
  }

  private void setupHeaderBar() {
    mTbHeaderBar.setTitle("SimpleTabPage");
    ToolbarHelper.setNavigationIconEnabled(mTbHeaderBar, true, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hide(true);
      }
    });
  }

  private void setupInnerPages() {
    mTabPageArray = new InnerPage[] {
        new SimpleInnerPage(this).setName("SimpleInnerPage for Tab1").setHtml(
            "SimpleTabPage is a <b>ContainerPage</b>, which is normally used " +
            "to implement tabbed pages, it is the same as <b>ViewPagerPage</b> " +
            "except for not supporting horizontal scroll to switch pages."
        ),
        new SimpleInnerPage(this).setName("SimpleInnerPage for Tab2"),
        new SimpleInnerPage(this).setName("SimpleInnerPage for Tab3"),
        new SimpleInnerPage(this).setName("SimpleInnerPage for Tab4"),
    };

    mLayoutTabContainer.addTab(mLayoutTabContainer.newTab().setText("Tab1").setIcon(R.drawable.home_selector));
    mLayoutTabContainer.addTab(mLayoutTabContainer.newTab().setText("Tab2").setIcon(R.drawable.home_selector));
    mLayoutTabContainer.addTab(mLayoutTabContainer.newTab().setText("Tab3").setIcon(R.drawable.home_selector));
    mLayoutTabContainer.addTab(mLayoutTabContainer.newTab().setText("Tab4").setIcon(R.drawable.home_selector));
    mLayoutTabContainer.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      public void onTabSelected(TabLayout.Tab tab) {
        setInnerPage(mTabPageArray[tab.getPosition()]);
      }
      public void onTabUnselected(TabLayout.Tab tab) { }
      public void onTabReselected(TabLayout.Tab tab) { }
    });

    // set first page as the first page to show
    setInnerPage(mTabPageArray[0]);
  }


  @Override
  public void onShow() {
    super.onShow();
    Log.i(TAG, "SimpleTabPage onShow()");
  }

  @Override
  public void onShown() {
    super.onShown();
    Log.i(TAG, "SimpleTabPage onShown()");
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.i(TAG, "SimpleTabPage onHide()");
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.i(TAG, "SimpleTabPage onHidden()");
  }
}
