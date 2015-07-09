package net.neevek.android.demo.paginize.pages.viewpager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.other.ListItemPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Created by neevek on 6/16/14.
 */
@PageLayout(R.layout.page_list)
public class ViewPagerItemListInnerPage extends InnerPage implements AdapterView.OnItemClickListener {
  private final static String TAG = ViewPagerItemListInnerPage.class.getSimpleName();

  @InjectView(value = android.R.id.list, listenerTypes = {AdapterView.OnItemClickListener.class})
  private ListView mLvData;

  public ViewPagerItemListInnerPage(ViewWrapper innerPageContainer) {
    super(innerPageContainer);

    String[] data = new String[100];
    for (int i = 0; i < 100; ++i) {
      data[i] = "item " + i;
    }
    mLvData.setAdapter(new ArrayAdapter(getContext(), R.layout.listview_item, data));
  }

  @Override
  public void onShow(Object obj) {
    super.onShow(obj);
    Log.d(TAG, "onShow() called: " + this);
  }

  @Override
  public void onShown(Object obj) {
    super.onShown(obj);
    Log.d(TAG, "onShown() called: " + this);
  }

  @Override
  public void onHide() {
    super.onHide();
    Log.d(TAG, "onHide() called: " + this);
  }

  @Override
  public void onHidden() {
    super.onHidden();
    Log.d(TAG, "onHidden() called: " + this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    // reuse the page here
//        mListItemPage.show(parent.getItemAtPosition(position), true);

    // you can also create a new page when you need one
    new ListItemPage(getContext())
            .setText((String) parent.getItemAtPosition(position))
            .show(null, true);
  }

}
