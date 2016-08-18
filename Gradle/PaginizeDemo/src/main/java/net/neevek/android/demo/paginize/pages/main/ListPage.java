package net.neevek.android.demo.paginize.pages.main;

import android.view.View;
import android.widget.TextView;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */
@InsertPageLayout(R.layout.page_main)
public class ListPage extends FramePage implements View.OnClickListener {
  @InjectView(R.id.tv_text)
  private TextView mTvText;

  // Instead of using @InjectView for injecting the view and setting listeners,
  // here @ListenerDefs and @SetListeners are used for the reason that we do not
  // need references to the buttons, also findViewById() is not used because
  // consistency is important, views and listeners are better be injected.
  @ListenerDefs({
      @SetListeners(view = R.id.btn_open_tab_page, listenerTypes = View.OnClickListener.class),
      @SetListeners(view = R.id.btn_open_test_page, listenerTypes = View.OnClickListener.class),
  })
  public ListPage(PageActivity pageActivity) {
    super(pageActivity);
    setTitle("Paginize · Demo");
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_open_tab_page:
        new MainTabPage(getContext()).show(true);
        break;
      case R.id.btn_open_test_page:
        new TestPage(getContext()).show(true);
        break;
    }
  }
}
