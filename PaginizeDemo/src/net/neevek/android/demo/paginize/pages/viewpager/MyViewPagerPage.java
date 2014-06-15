package net.neevek.android.demo.paginize.pages.viewpager;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.ViewPagerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.annotation.ViewPagerResId;

/**
 * Created by neevek on 6/16/14.
 */
@PageLayout(R.layout.page_view_pager_container)
@ViewPagerResId(R.id.vp_main)
public class MyViewPagerPage extends ViewPagerPage implements View.OnClickListener {
//    @InjectViewWrapper
//    private ViewPageSubPage1 p1;
//    @InjectViewWrapper
//    private ViewPageSubPage2 p2;
    @InjectView(value = R.id.tv_back, listeners = View.OnClickListener.class) TextView mTvBack;
    @InjectView(value = R.id.tv_title) TextView mTvTitle;
    @InjectView(value = R.id.tv_next, listeners = View.OnClickListener.class) TextView mTvNext;

    private ViewWrapper mViewWrappers[] = {new ViewPageSubPage1(mContext), new ViewPageSubPage2(mContext)};

    public MyViewPagerPage(PageActivity pageActivity) {
        super(pageActivity);

        mTvTitle.setText("Test ViewPagerPage");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                hideWithAnimation(true);
                break;
            case R.id.tv_next:
                Toast.makeText(mContext, "Next button clicked!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected ViewPagerPage.PagePagerAdapter getPagePagerAdapter() {
        return new PagePagerAdapter() {
            @Override
            public ViewWrapper getItem(int position) {
                return mViewWrappers[position];
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }
}
