package net.neevek.android.demo.paginize.pages.other;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */

@PageLayout(R.layout.page_list_item)
public class ListItemPage extends Page implements View.OnClickListener {

  @InjectView(value = R.id.btn_back, listenerTypes = {View.OnClickListener.class})
  private Button mBtnBack;

  @InjectView(R.id.tv_text)
  private TextView mTvText;


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
        hide(true, true);
        break;
    }
  }
}
