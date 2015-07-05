package net.neevek.android.demo.paginize.pages.viewpager;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.BaseInnerPageContainer;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.ViewPagerPage;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 6/16/14.
 */
@PageLayout(R.layout.page_simple_text)
public class ViewPagerInnerPage extends InnerPage implements View.OnClickListener {
  private final static String TAG = ViewPagerInnerPage.class.getSimpleName();

  @InjectView(R.id.tv_text)
  private TextView mTvSimpleText;

  @ListenerDefs({
          @SetListeners(view = R.id.btn_jump, listenerTypes = {View.OnClickListener.class} ),
  })
  public ViewPagerInnerPage(BaseInnerPageContainer baseInnerPageContainer) {
    super(baseInnerPageContainer);
  }

  public ViewPagerInnerPage setText(String text) {
    mTvSimpleText.setText(text);
    return this;
  }

  @Override
  public void onShown(Object obj) {
    super.onShown(obj);
    Log.d(TAG, "onShown() called: " + this);
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.d(TAG, "onHidden() called: " + this);
  }

  @Override
  public void onClick(View v) {
    ViewPagerPage viewPagerPage = (ViewPagerPage)getInnerPageContainer();
    int count = viewPagerPage.getPageCount();
    int nextPageIndex = viewPagerPage.getCurrentPageIndex() + 1;
    if (nextPageIndex < count) {
      viewPagerPage.setCurrentPage(nextPageIndex, true);
    } else {
      Toast.makeText(getContext(), "reached last page", Toast.LENGTH_SHORT).show();
    }
  }
}
