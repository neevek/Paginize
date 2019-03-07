package net.neevek.android.lib.paginizecontrib.page;

import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.google.android.material.tabs.TabLayout;

import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.ViewPagerPage;
import net.neevek.android.lib.paginize.annotation.InjectViewByName;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutName;
import net.neevek.android.lib.paginize.annotation.PageLayoutName;
import net.neevek.android.lib.paginizecontrib.P;
import net.neevek.android.lib.paginizecontrib.R;
import net.neevek.android.lib.paginizecontrib.util.ToolbarHelper;

import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by neevek on 08/01/2017.
 */
@PageLayoutName(P.layout.paginize_contrib_page_base_tabs)
@InnerPageContainerLayoutName(P.id.paginize_contrib_layout_content_container)
public class BaseTabsPage extends ViewPagerPage {
  private View mViewToolbarContainer;
  private Toolbar mToolbar;

  @InjectViewByName(P.id.paginize_contrib_layout_tab_container)
  private TabLayout mTabLayout;

  public BaseTabsPage(PageActivity pageActivity) {
    super(pageActivity);
    setupTabLayout(mTabLayout, true);
    setHorizontalFadingEdgeEnabled(false);
  }

  protected void setToolbarEnabled(boolean enabled) {
    if (enabled) {
      if (mViewToolbarContainer != null &&
          mViewToolbarContainer.getVisibility() == View.VISIBLE) {
        return;
      }

      if (mViewToolbarContainer == null) {
        mViewToolbarContainer = ((ViewStub) findViewById(
            R.id.paginize_contrib_stub_layout_toolbar)).inflate();

        mToolbar = (Toolbar) mViewToolbarContainer
            .findViewById(R.id.paginize_contrib_toolbar);
      }

      mViewToolbarContainer.setVisibility(View.VISIBLE);

      if (getContext().getPageCount() > 0) {
        ToolbarHelper.setNavigationIconEnabled(mToolbar, true, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onNavigationIconClicked(v);
          }
        });
      }

    } else if (mViewToolbarContainer != null) {
      mViewToolbarContainer.setVisibility(View.GONE);
    }
  }

  protected TabLayout getTabLayout() {
    return mTabLayout;
  }

  protected final void setMenu(@MenuRes int menuResId) {
    setToolbarEnabled(true);

    ToolbarHelper.setupMenu(mToolbar, menuResId, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        return BaseTabsPage.this.onMenuItemClick(item);
      }
    });
  }

  protected final Toolbar getToolbar() {
    return mToolbar;
  }

  protected final void setTitle(@StringRes int resId) {
    setToolbarEnabled(true);
    mToolbar.setTitle(resId);
  }

  protected final void setTitle(CharSequence title) {
    setToolbarEnabled(true);
    mToolbar.setTitle(title);
  }

  protected final void setNavigationIcon(@DrawableRes int resId) {
    setToolbarEnabled(true);
    mToolbar.setNavigationIcon(resId);
  }

  protected final void setNavigationIcon(Drawable icon) {
    setToolbarEnabled(true);
    mToolbar.setNavigationIcon(icon);
  }

  protected void onNavigationIconClicked(View v) {
    hide(true);
  }

  protected boolean onMenuItemClick(MenuItem item) {
    return false;
  }
}
