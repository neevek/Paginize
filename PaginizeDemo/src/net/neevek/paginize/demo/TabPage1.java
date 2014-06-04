package net.neevek.paginize.demo;

import android.view.View;
import android.widget.Button;
import net.neevek.lib.android.paginize.InnerPage;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab1)
public class TabPage1 extends InnerPage implements View.OnClickListener {

    @InjectView(value = R.id.btn_next_page, listeners = {View.OnClickListener.class})
    private Button mBtnNextPage;
    @InjectView(value = R.id.btn_list_page, listeners = {View.OnClickListener.class})
    private Button mBtnListPage;
    @InjectView(value = R.id.btn_show_alert, listeners = {View.OnClickListener.class})
    private Button mBtnShowAnAlertPage;
    @InjectView(value = R.id.btn_sub_page1, listeners = {View.OnClickListener.class})
    private Button mBtnSubPage1;
    @InjectView(value = R.id.btn_sub_page2, listeners = {View.OnClickListener.class})
    private Button mBtnSubPage2;

    public TabPage1(PageActivity context) {
        super(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_page:
                new TestPage(mContext).show(null, true);
                break;
            case R.id.btn_list_page:
                new ListPage(mContext).show(null, true);
                break;
            case R.id.btn_show_alert:
                new AlertPage(mContext).show();
                break;

            case R.id.btn_sub_page1:
                new SubPage1(mContext).show(null, true);
                break;
            case R.id.btn_sub_page2:
                new SubPage2(mContext).show(null, true);
                break;
        }
    }
}
