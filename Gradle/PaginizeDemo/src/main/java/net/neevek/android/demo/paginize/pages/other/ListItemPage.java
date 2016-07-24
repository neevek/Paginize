package net.neevek.android.demo.paginize.pages.other;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FramePage;
import net.neevek.android.demo.paginize.pages.main.MainPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */

@InsertPageLayout(R.layout.page_list_item)
public class ListItemPage extends FramePage implements View.OnClickListener {
  private final static String SAVED_ARGUMENT_KEY = "saved_list_item_page_arg";

  @InjectView(R.id.tv_text)
  private TextView mTvText;

  @ListenerDefs({
          @SetListeners(view = R.id.btn_back, listenerTypes = View.OnClickListener.class),
          @SetListeners(view = R.id.btn_nav_to_main_page, listenerTypes = View.OnClickListener.class),
  })
  public ListItemPage(PageActivity pageActivity) {
    super(pageActivity);
  }

  @Override
  public void onShown(Object arg) {
//        mTvText.setText(arg.toString());
  }

  public ListItemPage setText(String text) {
    mTvText.setText(text);
    return this;
  }

  @Override
  public void onHidden() {
    mTvText.setText("");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_back:
        hide(true);
        break;

      case R.id.btn_nav_to_main_page:
//        getPageManager().popTopNPages(getPageManager().getPageCount(), true);
//        getPageManager().pushPage(new MainPage(getContext()), null, true, AnimationDirection.FROM_LEFT);

        // we can also use popToClass()
        getPageManager().popToClass(MainPage.class, true, AnimationDirection.FROM_LEFT);
        break;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(SAVED_ARGUMENT_KEY, mTvText.getText().toString());
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    String text = savedInstanceState.getString(SAVED_ARGUMENT_KEY);
    mTvText.setText(text);
  }
}
