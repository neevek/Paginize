package net.neevek.android.demo.paginize.pages;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.ListenerMarker;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_custom)
public class CustomPage extends Page {
  @InjectView(value = R.id.tv_title)
  TextView mTvTitle;
  @InjectView(value = R.id.tv_text)
  TextView mTvText;

  // we tend to use @ListenerDefs and @SetListeners instead of @InjectView
  // if we do not need references of the views
  @ListenerDefs({
      @SetListeners(view = R.id.tv_back,
           listenerTypes = View.OnClickListener.class,
                listener = MyOnClickListener.class),
      @SetListeners(view = R.id.tv_next,
           listenerTypes = View.OnClickListener.class,
                listener = MyOnClickListener.class)
  })
  public CustomPage(PageActivity pageActivity) {
    super(pageActivity);
    mTvTitle.setText("Custom Page");
    mTvText.setText(Html.fromHtml(
        "This page demonstrates an implementation of a page with a customized " +
        "header bar without using any widgets from the support library."));
  }

  // this annotation is necessary, we use this annotation to prevent obfuscation
  @ListenerMarker
  class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.tv_back:
          hide(true);
          break;
        case R.id.tv_next:
          Toast.makeText(getContext(), "Next Button clicked",
              Toast.LENGTH_SHORT).show();
          break;
      }
    }
  }
}
