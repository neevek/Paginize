package net.neevek.android.demo.paginize.paginizecontrib;

import android.view.View;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.SetListeners;
import net.neevek.android.lib.paginizecontrib.page.BaseInnerPage;

/**
 * Created by neevek on 14/01/2017.
 */
@InsertPageLayout(value = R.layout.page_paginize_contrib_tabs_inner, parent = R.id.paginize_contrib_layout_content_container)
public class PaginizeContribInnerPage extends BaseInnerPage implements View.OnClickListener {
  @ListenerDefs({
      @SetListeners(view = R.id.btn_show_base_page, listenerTypes = View.OnClickListener.class),
  })
  public PaginizeContribInnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  public PaginizeContribInnerPage setPageTitle(String title) {
    setTitle(title);
    return this;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_show_base_page:
        new PaginizeContribPage(getContext()).show(true);
        break;
    }
  }
}
