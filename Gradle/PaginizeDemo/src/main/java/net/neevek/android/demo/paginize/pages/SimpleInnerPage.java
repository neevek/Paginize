package net.neevek.android.demo.paginize.pages;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 8/14/16.
 */
@PageLayout(R.layout.page_simple)
public class SimpleInnerPage extends InnerPage {
  private static final String TAG = SimpleInnerPage.class.getName();
  private String mName;

  @InjectView(R.id.tv_text)
  private TextView mTvText;

  public SimpleInnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
  }

  public SimpleInnerPage setName(String name) {
    mName = name;
    setText(name);
    return this;
  }

  public SimpleInnerPage setText(CharSequence text) {
    mTvText.setText(text);
    return this;
  }

  public SimpleInnerPage setHtml(String text) {
    mTvText.setText(Html.fromHtml(text));
    return this;
  }

  @Override
  public void onShow() {
    super.onShow();
    Log.i(TAG, String.format("SimpleInnerPage onShow(): %s", mName));
  }

  @Override
  public void onShown() {
    super.onShown();
    Log.i(TAG, String.format("SimpleInnerPage onShown(): %s", mName));
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.i(TAG, String.format("SimpleInnerPage onHide(): %s", mName));
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.i(TAG, String.format("SimpleInnerPage onHidden(): %s", mName));
  }
}
