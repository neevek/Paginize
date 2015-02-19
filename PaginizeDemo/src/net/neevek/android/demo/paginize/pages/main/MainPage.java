package net.neevek.android.demo.paginize.pages.main;

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
  @InjectView(value = R.id.rb_nav_btn1, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
  private RadioButton mRbNavBtn1;

  // see use of @ListenerDefs below
//  @InjectView(value = R.id.rb_nav_btn2, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class)
//  private RadioButton mRbNavBtn2;

  private TabPage1 mTabPage1 = new TabPage1(getContext());
  private TabPage2 mTabPage2 = new TabPage2(getContext());

  // demonstrate how @ListenerDefs can be used.
  // here we do not need a reference to R.id.rb_nav_btn2, so we can inject listeners for it by
  // annotating @ListenerDefs on the constructor, which has the same effect as using
  // @InjectView
  @ListenerDefs({ @SetListeners(view = R.id.rb_nav_btn2, listenerTypes = {View.OnClickListener.class}, listener = InnerListener.class) })
  public MainPage(PageActivity pageActivity) {
    super(pageActivity);

    setTitle("Home!");

    mRbNavBtn1.setChecked(true);
    setInnerPage(mTabPage1, null);
  }

  class InnerListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.rb_nav_btn1:
          setInnerPage(mTabPage1, null);
          break;
        case R.id.rb_nav_btn2:
          setInnerPage(mTabPage2, null);
          break;
      }
    }
  }
}
