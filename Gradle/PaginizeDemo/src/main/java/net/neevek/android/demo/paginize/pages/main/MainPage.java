package net.neevek.android.demo.paginize.pages.main;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.util.ToolbarHelper;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_main)
public class MainPage extends Page {
  @InjectView(R.id.tb_header_bar)
  private Toolbar mToolbar;

  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    mToolbar.setTitle("第一页标题");
    ToolbarHelper.setupMenu(mToolbar, R.menu.menu_main, new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(getContext(), "Hello Settings.", Toast.LENGTH_SHORT).show();
        return false;
      }
    });

    findViewById(R.id.btn_open_detail_page).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new DetailPage(getContext())
            .setArgument("Hello World Text!")
            .show(true);
      }
    });

  }
}
