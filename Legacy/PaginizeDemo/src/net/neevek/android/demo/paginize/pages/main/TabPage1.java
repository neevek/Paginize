package net.neevek.android.demo.paginize.pages.main;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.other.AlertPage;
import net.neevek.android.demo.paginize.pages.other.ListPage;
import net.neevek.android.demo.paginize.pages.other.TestPage;
import net.neevek.android.demo.paginize.pages.viewpager.MyViewPagerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.*;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab1)
public class TabPage1 extends InnerPage //implements View.OnClickListener
{
  private final static String TAG = TabPage1.class.getSimpleName();

//    @InjectView(value = R.id.btn_next_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnNextPage;

    @InjectView(R.id.tv_main_text)
    private TextView mTvMainText;

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

    mTvMainText.setText(Html.fromHtml("<b>Paginize</b> is a library that eases development of Android applications, it is simple yet powerful! This is a demo that shows how to easily create *Pages* and use features of the library."));
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
          new AlertPage(getContext())
                  .setTitle("Information")
                  .setConfirmText("OK")
                  .setCancelOnTouchOutside(false)
                  .setContent("This is an alert page, which can be customized and used to replace the builtin dialog widget.")
                  .setOnButtonClickListener(new AlertPage.OnButtonClickListener() {
                    @Override
                    public void onConfirmed() {
                      Toast.makeText(getContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled() {
                      Toast.makeText(getContext(), "Cancel clicked!", Toast.LENGTH_SHORT).show();
                    }
                  })
                  .show();
          break;
        case R.id.btn_show_view_pager_page:
          new MyViewPagerPage(getContext()).show(null, true);
          break;
      }
    }
  }
}
