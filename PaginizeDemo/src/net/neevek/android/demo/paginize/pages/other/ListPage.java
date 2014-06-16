package net.neevek.android.demo.paginize.pages.other;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FramePage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InheritPageLayout;
import net.neevek.android.lib.paginize.annotation.InjectView;

/**
 * Created by neevek on 3/16/14.
 */

@InheritPageLayout(R.layout.page_list)
public class ListPage extends FramePage implements AdapterView.OnItemClickListener {

    @InjectView(value = android.R.id.list, listeners = {AdapterView.OnItemClickListener.class})
    private ListView mLvData;

    private ListItemPage mListItemPage = new ListItemPage(mContext);

    public ListPage(PageActivity pageActivity) {
        super(pageActivity);

        setTitle("List Page!");

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

    @Override
    protected void onNextButtonClicked() {
        Toast.makeText(mContext, "Next button in ListPage was clicked", Toast.LENGTH_SHORT).show();
    }
}
