package net.neevek.android.demo.paginize.pages.main;

import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by xiaohei on 8/6/16.
 */
@PageLayout(R.layout.page_frame)
public abstract class FramePage extends Page {
  @InjectView(R.id.tb_header_bar)
  private Toolbar mToolbar;

  public FramePage(PageActivity pageActivity) {
    super(pageActivity);

    if (getContext().getPageCount() > 0) {
      ToolbarHelper.setNavigationIconEnabled(mToolbar, true, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onNavigationIconClicked(v);
        }
      });
    }
  }

  protected void setupMenu(@MenuRes int menuResId) {
    ToolbarHelper.setupMenu(mToolbar, menuResId, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        return FramePage.this.onMenuItemClick(item);
      }
    });
  }

  protected Toolbar getToolbar() {
    return mToolbar;
  }

  protected void setTitle(String title) {
    mToolbar.setTitle(title);
  }

  protected void setNavigationIcon(@DrawableRes int resId) {
    mToolbar.setNavigationIcon(resId);
  }

  protected void onNavigationIconClicked(View v) {
    hide(true);
  }

  protected boolean onMenuItemClick(MenuItem item) {
    return false;
  }
}
