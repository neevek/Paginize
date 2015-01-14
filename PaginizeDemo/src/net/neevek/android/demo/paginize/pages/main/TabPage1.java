package net.neevek.android.demo.paginize.pages.main;

import android.view.View;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.other.AlertPage;
import net.neevek.android.demo.paginize.pages.other.ListPage;
import net.neevek.android.demo.paginize.pages.other.TestPage;
import net.neevek.android.demo.paginize.pages.viewpager.MyViewPagerPage;
import net.neevek.android.lib.paginize.InnerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.DecoratePageConstructor;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.SetListeners;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab1)
public class TabPage1 extends InnerPage //implements View.OnClickListener
{

//    @InjectView(value = R.id.btn_next_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnNextPage;
//    @InjectView(value = R.id.btn_list_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnListPage;
//    @InjectView(value = R.id.btn_show_alert, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnShowAnAlertPage;
//    @InjectView(value = R.id.btn_show_view_pager_page, listenerTypes = {View.OnClickListener.class})
//    private Button mBtnShowViewPagerPage;

    @DecoratePageConstructor(viewListeners = {
            @SetListeners(view = R.id.btn_next_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
            @SetListeners(view = R.id.btn_list_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
            @SetListeners(view = R.id.btn_show_alert, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class),
            @SetListeners(view = R.id.btn_show_view_pager_page, listenerTypes = {View.OnClickListener.class}, listener = MyOnClickListener.class)
    })
    public TabPage1(PageActivity context) {
        super(context);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_next_page:
//                new TestPage(mContext).show(null, true);
//                break;
//            case R.id.btn_list_page:
//                new ListPage(mContext).show(null, true);
//                break;
//            case R.id.btn_show_alert:
//                new AlertPage(mContext).show();
//                break;
//            case R.id.btn_show_view_pager_page:
//                new MyViewPagerPage(mContext).show(null, true);
//                break;
//        }
//    }

    class MyOnClickListener implements View.OnClickListener {
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
                case R.id.btn_show_view_pager_page:
                    new MyViewPagerPage(mContext).show(null, true);
                    break;
            }
        }
    }
}
