package net.neevek.android.demo.paginize.pages.main;


import android.os.Bundle;
import android.util.Log;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab2)
public class TabPage2 extends InnerPage {
  private final static String TAG = TabPage2.class.getSimpleName();

  final static String SAVED_DATA_KEY = "some_key";

  public TabPage2(ViewWrapper innerPageContainer) {
    super(innerPageContainer);
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

  /**
   * Note: the following code are NOT needed if you are not going to support state recovery for
   *       device rotation or Activity recreation on low memory.
   */

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(SAVED_DATA_KEY, "THIS IS THE SAVED DATA!");
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    String savedData = savedInstanceState.getString(SAVED_DATA_KEY);
    System.out.println(">>>>>>> saved data: " + savedData);
  }
}
