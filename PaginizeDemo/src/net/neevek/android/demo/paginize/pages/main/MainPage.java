package net.neevek.android.demo.paginize.pages.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FrameInnerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.annotation.ListenerMarker;

/**
 * Created by neevek on 3/16/14.
 */
@InsertPageLayout(R.layout.page_main)
@InnerPageContainerLayoutResId(R.id.layout_container)
public class MainPage extends FrameInnerPage {
  private final static String TAG = MainPage.class.getSimpleName();
  private final static String SAVE_INDEX = "main_page_inner_page_index";

  @InjectView(value = R.id.rb_nav_btn1, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn1;
  @InjectView(value = R.id.rb_nav_btn2, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn2;
  @InjectView(value = R.id.rb_nav_btn3, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn3;

  private int mSelectIndex = 0;

  private TabPage1 mTabPage1 = new TabPage1(this);
  private TabPage2 mTabPage2 = new TabPage2(this);
  private NestContainerPage mTabPage3 = new NestContainerPage(this);

  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    getPageManager().setStatusBarBackgroundColor(0xff00796b);

    setTitle("Home");
    mRbNavBtn1.setChecked(true);
    setInnerPage(mTabPage1, null);
  }

  @Override
  public void onShow(Object arg) {
    super.onShow(arg);
    Log.d(TAG, "onShow called");
  }

  @Override
  public void onShown(Object obj) {
    super.onShown(obj);
    Log.d(TAG, "onShown called");
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.d(TAG, "onHide called");
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.d(TAG, "onHidden called");
  }

  @Override
  public void onUncover(Object arg) {
    super.onUncover(arg);
    getPageManager().setStatusBarBackgroundColor(0xff00796b);
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
        case R.id.rb_nav_btn3:
          mSelectIndex = 2;
          setInnerPage(mTabPage3, null);
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
    InnerPage currentInnerPage = getCurrentInnerPage();
    if (currentInnerPage != null) {
      currentInnerPage.onSaveInstanceState(outState);
    }
    outState.putInt(SAVE_INDEX, mSelectIndex);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    int index = savedInstanceState.getInt(SAVE_INDEX);
    if (index != mSelectIndex) {
      switch (index) {
        case 0:
          mRbNavBtn1.setChecked(true);
          mTabPage1.onRestoreInstanceState(savedInstanceState);
          setInnerPage(mTabPage1, null);
          break;
        case 1:
          mRbNavBtn2.setChecked(true);
          mTabPage2.onRestoreInstanceState(savedInstanceState);
          setInnerPage(mTabPage2, null);
          break;
        case 2:
          mRbNavBtn3.setChecked(true);
          mTabPage3.onRestoreInstanceState(savedInstanceState);
          setInnerPage(mTabPage3, null);
          break;
      }

      mSelectIndex = index;
    }
  }
}
