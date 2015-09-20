package net.neevek.android.demo.paginize.pages.general;

import android.view.View;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.*;

/**
 * Created by xiejm on 6/4/14.
 */
@PageLayout(R.layout.page_frame)
public abstract class FramePage extends Page {
  @InjectView(value = R.id.tv_title)
  TextView mTvTitle;

  private int mOldStatusColor;

  // we tend to use @ListenerDefs and @SetListeners instead of @InjectView if we do not need references of the views
  @ListenerDefs({
      @SetListeners(view = R.id.tv_back, listenerTypes = View.OnClickListener.class, listener = MyOnClickListener.class),
      @SetListeners(view = R.id.tv_next, listenerTypes = View.OnClickListener.class, listener = MyOnClickListener.class)
  })
  public FramePage(PageActivity pageActivity) {
    super(pageActivity);

    mOldStatusColor = getPageManager().getStatusBarBackgroundColor();
    getPageManager().setStatusBarBackgroundColor(0x689f38);
  }

  protected void setTitle(String title) {
    mTvTitle.setText(title);
  }

  protected void onBackButtonClicked() {
    hide(true);
  }

  protected void onNextButtonClicked() {

  }

  // this annotation is necessary, we use this annotation to prevent obfuscation
  @ListenerMarker
  class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.tv_back:
          onBackButtonClicked();
          break;
        case R.id.tv_next:
          onNextButtonClicked();
          break;
      }
    }
  }

  @Override
  public void onHide() {
    super.onHide();
    getPageManager().setStatusBarBackgroundColor(mOldStatusColor);
  }
}
