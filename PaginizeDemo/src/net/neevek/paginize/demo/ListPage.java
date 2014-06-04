package net.neevek.paginize.demo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.neevek.lib.android.paginize.Page;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectPage;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */

@PageLayout(R.layout.page_list)
public class ListPage extends Page implements AdapterView.OnItemClickListener {

    @InjectView(value = android.R.id.list, listeners = {AdapterView.OnItemClickListener.class})
    private ListView mLvData;

    // you can reuse a page like this
    @InjectPage
    private ListItemPage mListItemPage;

    public ListPage(PageActivity pageActivity) {
        super(pageActivity);

        String[] data = new String[100];
        for (int i = 0; i < 100; ++i) {
            data[i] = "item " + i;
        }
        mLvData.setAdapter(new ArrayAdapter(mContext, R.layout.listview_item, data));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // reuse the page here
        mListItemPage.show(parent.getItemAtPosition(position), true);
        // you can also create a new page when you need one
//        new ListItemPage(mContext).show(arg, true);
    }
}
