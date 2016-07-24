package net.neevek.android.demo.paginize.pages.viewpager;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.ViewPagerPage;
import net.neevek.android.lib.paginize.annotation.*;

/**
 * Created by neevek on 6/16/14.
 */
@PageLayout(R.layout.page_view_pager_container)
@InnerPageContainerLayoutResId(R.id.layout_container)
public class MyViewPagerPage extends ViewPagerPage implements View.OnClickListener {
  private final static String TAG = MyViewPagerPage.class.getSimpleName();

  @InjectView(value = R.id.tv_title)
  TextView mTvTitle;

  private int mOldStatusColor;

  @ListenerDefs({
          @SetListeners(view = R.id.tv_back, listenerTypes = View.OnClickListener.class),
          @SetListeners(view = R.id.tv_next, listenerTypes = View.OnClickListener.class),
  })
  public MyViewPagerPage(PageActivity pageActivity) {
    super(pageActivity);

    mOldStatusColor = getPageManager().getStatusBarBackgroundColor();
    getPageManager().setStatusBarBackgroundColor(0x1976d2);

    mTvTitle.setText("Test ViewPagerPage");

    setHorizontalFadingEdgeEnabled(false);

    addPage(new ViewPagerItemInnerPage(this).setText("subpage 0"));
    addPage(new ViewPagerItemInnerPage(this).setText("subpage 1"));
    addPage(new ViewPagerItemInnerPage(this).setText("subpage 2"));
    addPage(new ViewPagerItemInnerPage(this).setText("subpage 3"));
    addPage(new ViewPagerItemInnerPage(this).setText("subpage 4"));
    addPage(new ViewPagerItemListInnerPage(this));
  }

  @Override
  public void onHide() {
    super.onHide();
    getPageManager().setStatusBarBackgroundColor(mOldStatusColor);
  }

  @Override
  protected void onPageScrolled(int index, float indexOffset, int indexOffsetPixels) {
    super.onPageScrolled(index, indexOffset, indexOffsetPixels);
//    Log.d(TAG, "onPageScrolled(): index=" + index + ", " + indexOffset + ", " + indexOffsetPixels);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_back:
        hide(true);
        break;
      case R.id.tv_next:
        Toast.makeText(getContext(), "Next button clicked!", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
