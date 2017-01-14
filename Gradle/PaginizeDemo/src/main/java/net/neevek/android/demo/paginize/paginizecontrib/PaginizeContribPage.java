package net.neevek.android.demo.paginize.paginizecontrib;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.SetListeners;
import net.neevek.android.lib.paginizecontrib.page.BasePage;
import net.neevek.android.lib.paginizecontrib.page.OptionMenuPage;
import net.neevek.android.lib.paginizecontrib.page.SlideInMenuPage;

/**
 * Created by neevek on 14/01/2017.
 */
@InsertPageLayout(value = R.layout.page_paginize_contrib_test, parent = R.id.paginize_contrib_layout_content_container)
public class PaginizeContribPage extends BasePage implements View.OnClickListener {
  @ListenerDefs({
      @SetListeners(view = R.id.btn_show_base_page, listenerTypes = View.OnClickListener.class),
      @SetListeners(view = R.id.btn_show_option_menu_page, listenerTypes = View.OnClickListener.class),
      @SetListeners(view = R.id.btn_show_slide_in_menu_page, listenerTypes = View.OnClickListener.class),
  })
  public PaginizeContribPage(PageActivity pageActivity) {
    super(pageActivity);
    setTitle("Page with PaginizeContrib");
    showLoadingView("Loading...");
    setMenu(R.menu.menu_main);
    postDelayed(new Runnable() {
      public void run() {
        showErrorView("Loading failed", null, true, "Retry");
      }
    }, 2000);
  }

  @Override
  protected void onRetryButtonClicked() {
    showLoadingView();
    postDelayed(new Runnable() {
      public void run() {
        showContentView();
      }
    }, 2000);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_show_base_page:
        Toast.makeText(getContext(), "Stop here!", Toast.LENGTH_SHORT).show();
        break;
      case R.id.btn_show_option_menu_page:
        new OptionMenuPage(getContext())
            .addOptionMenuItem("Option1")
            .addOptionMenuItem("Option2")
            .addOptionMenuItem("Option3")
            .addOptionMenuItem("Option4")
            .addOptionMenuItem("Option5")
            .addOptionMenuItem("Option6")
            .addOptionMenuItem("Option7")
            .setOnMenuItemClickListener(new OptionMenuPage.OnMenuItemClickListener() {
              @Override
              public void onMenuItemClicked(int index, String title) {
                Toast.makeText(getContext(), "option clicked: " + title, Toast.LENGTH_SHORT).show();
              }
            })
            .show(true);
        break;
      case R.id.btn_show_slide_in_menu_page:
        new SlideInMenuPage(getContext())
            .addOptionMenuItem("Option1")
            .addOptionMenuItem("Option2")
            .addOptionMenuItem("Option3")
            .addOptionMenuItem("Option4")
            .addOptionMenuItem("Option5")
            .addOptionMenuItem("Option6")
            .addOptionMenuItem("Option7")
            .setOnMenuItemClickListener(new SlideInMenuPage.OnMenuItemClickListener() {
              @Override
              public void onMenuItemClicked(int index, String title) {
                Toast.makeText(getContext(), "option clicked: " + title, Toast.LENGTH_SHORT).show();
              }
            })
            .show(true);
        break;
    }
  }

  @Override
  protected boolean onMenuItemClick(MenuItem item) {
    new OptionMenuPage(getContext())
        .addOptionMenuItem(">> Option1")
        .addOptionMenuItem(">> Option2")
        .setOnMenuItemClickListener(new OptionMenuPage.OnMenuItemClickListener() {
          @Override
          public void onMenuItemClicked(int index, String title) {
            Toast.makeText(getContext(), ">> option clicked: " + title, Toast.LENGTH_SHORT).show();
          }
        })
        .show(true);
    return true;
  }
}
