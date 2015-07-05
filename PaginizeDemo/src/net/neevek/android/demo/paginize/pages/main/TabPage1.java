package net.neevek.android.demo.paginize.pages.main;

import android.util.Log;
import android.view.View;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.other.AlertPage;
import net.neevek.android.demo.paginize.pages.other.ListPage;
import net.neevek.android.demo.paginize.pages.other.TestPage;
import net.neevek.android.demo.paginize.pages.viewpager.MyViewPagerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.ListenerMarker;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab1)
public class TabPage1 extends InnerPage //implements View.OnClickListener
{
  private final static String TAG = TabPage1.class.getSimpleName();
  //    @InjectView(value = R.id.btn_next_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnNextPage;
//    @InjectView(value = R.id.btn_list_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnListPage;
//    @InjectView(value = R.id.btn_show_alert, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnShowAnAlertPage;
//    @InjectView(value = R.id.btn_show_view_pager_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnShowViewPagerPage;

  // demonstrate how @ListenerDefs can be used.
  // here we do not need references to all these views, so we can inject listeners for them by
  // annotating the constructor with @ListenerDefs, which has the same effect as using @InjectView
  @ListenerDefs({
      @SetListeners(view = R.id.btn_next_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
      @SetListeners(view = R.id.btn_list_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
      @SetListeners(view = R.id.btn_push_multiple_pages, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
      @SetListeners(view = R.id.btn_show_alert, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
      @SetListeners(view = R.id.btn_show_view_pager_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class)
  })
  public TabPage1(ViewWrapper innerPageContainer) {
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

  //    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_next_page:
//                new TestPage(getContext()).show(null, true);
//                break;
//            case R.id.btn_list_page:
//                new ListPage(getContext()).show(null, true);
//                break;
//            case R.id.btn_show_alert:
//                new AlertPage(getContext()).show();
//                break;
//            case R.id.btn_show_view_pager_page:
//                new MyViewPagerPage(getContext()).show(null, true);
//                break;
//        }
//    }

  @ListenerMarker
  class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_next_page:
          new TestPage(getContext()).show(null, true);
          break;
        case R.id.btn_list_page:
          new ListPage(getContext()).show(null, true);
          break;
        case R.id.btn_push_multiple_pages:
          getContext().getPageManager().pushPages(new Page[]{ new TestPage(getContext()), new ListPage(getContext())}, null, true);
          break;
        case R.id.btn_show_alert:
          new AlertPage(getContext()).show();
          break;
        case R.id.btn_show_view_pager_page:
          new MyViewPagerPage(getContext()).show(null, true);
          break;
      }
    }
  }
}
