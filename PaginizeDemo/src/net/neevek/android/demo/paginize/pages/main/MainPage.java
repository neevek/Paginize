package net.neevek.android.demo.paginize.pages.main;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FrameInnerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.*;

/**
 * Created by neevek on 3/16/14.
 */
@InheritPageLayout(R.layout.page_main)
@InnerPageContainerLayoutResId(R.id.layout_container)
public class MainPage extends FrameInnerPage {
  private final static String SAVE_INDEX = "main_page_inner_page_index";

  @InjectView(value = R.id.rb_nav_btn1, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn1;
  @InjectView(value = R.id.rb_nav_btn2, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn2;

  private int mSelectIndex = 0;

  private TabPage1 mTabPage1 = new TabPage1(getContext());
  private TabPage2 mTabPage2 = new TabPage2(getContext());

  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    setTitle("Home!");

    mRbNavBtn1.setChecked(true);
    setInnerPage(mTabPage1, null);
  }

  @ListenerMarker
  class InnerListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.rb_nav_btn1:
          mSelectIndex = 0;
          setInnerPage(mTabPage1, null);
          break;
        case R.id.rb_nav_btn2:
          mSelectIndex = 1;
          setInnerPage(mTabPage2, null);
          break;
      }
    }
  }

  /**
   * Note: the following code are NOT needed if you are not going to support state recovery for
   *       device rotation or Activity recreation on low memory.
   */

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(SAVE_INDEX, mRbNavBtn1.isChecked() ? 0 : 1);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    int index = savedInstanceState.getInt(SAVE_INDEX);
    if (index != mSelectIndex) {
      if (index == 0) {
        mRbNavBtn1.setChecked(true);
      } else {
        mRbNavBtn2.setChecked(true);
      }

      mSelectIndex = index;
    }
  }
}
