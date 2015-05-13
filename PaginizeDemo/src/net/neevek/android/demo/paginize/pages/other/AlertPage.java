package net.neevek.android.demo.paginize.pages.other;

import android.view.MotionEvent;
import android.view.View;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;


@PageLayout(R.layout.page_alert)
public class AlertPage extends Page implements View.OnClickListener {

  @ListenerDefs({
          @SetListeners(view = R.id.btn_confirm, listenerTypes = View.OnClickListener.class)
  })
  public AlertPage(PageActivity pageActivity) {
    super(pageActivity);
    setType(Page.TYPE.TYPE_DIALOG);
    interceptAllTouchEvents();
  }

  private void interceptAllTouchEvents() {
    getView().setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_confirm:
        hide(false);
        break;
    }
  }

  @Override
  public boolean onBackPressed() {
    hide(false);
    return true;
  }
}
