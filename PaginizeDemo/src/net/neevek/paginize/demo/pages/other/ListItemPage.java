package net.neevek.paginize.demo.pages.other;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.neevek.lib.android.paginize.Page;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;
import net.neevek.paginize.demo.R;

/**
 * Created by neevek on 3/16/14.
 */

@PageLayout(R.layout.page_list_item)
public class ListItemPage extends Page implements View.OnClickListener {

    @InjectView(value = R.id.btn_back, listeners = {View.OnClickListener.class})
    private Button mBtnBack;

    @InjectView(R.id.tv_text)
    private TextView mTvText;


    public ListItemPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onShown(Object arg) {
        mTvText.setText(arg.toString());
    }

    @Override
    public void onHidden() {
        mTvText.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                hideWithAnimation(true);
                break;
        }
    }
}
