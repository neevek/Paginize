package net.neevek.android.demo.paginize.pages.main;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;

/**
 * Created by xiaohei on 8/6/16.
 */
@InsertPageLayout(R.layout.page_detail)
public class DetailPage extends FramePage {
  public DetailPage(PageActivity pageActivity) {
    super(pageActivity);

    setTitle("测试详情");
    setupMenu(R.menu.menu_main2);

    findViewById(R.id.btn_detail2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        new MainTabPage(getContext())
            .show(true);
      }
    });
  }

  @Override
  protected boolean onMenuItemClick(MenuItem item) {
    Toast.makeText(getContext(), "another page", Toast.LENGTH_SHORT).show();
    return true;
  }
}
