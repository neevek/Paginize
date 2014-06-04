package net.neevek.paginize.demo;

import android.view.View;
import android.widget.RadioButton;
import net.neevek.lib.android.paginize.InnerPageContainer;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectInnerPage;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.lib.android.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.page_main)
@InnerPageContainerLayoutResId(R.id.layout_container)
public class MainPage extends InnerPageContainer implements View.OnClickListener {
    @InjectView(value = R.id.rb_nav_btn1, listeners = {View.OnClickListener.class})
    private RadioButton mRbNavBtn1;
    @InjectView(value = R.id.rb_nav_btn2, listeners = {View.OnClickListener.class})
    private RadioButton mRbNavBtn2;

    @InjectInnerPage
    private TabPage1 mTabPage1;
    @InjectInnerPage
    private TabPage2 mTabPage2;

    public MainPage(PageActivity pageActivity) {
        super(pageActivity);

        mRbNavBtn1.setChecked(true);
        setInnerPage(mTabPage1, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_nav_btn1:
                setInnerPage(mTabPage1, null);
                break;
            case R.id.rb_nav_btn2:
                setInnerPage(mTabPage2, null);
                break;
        }
    }
}
