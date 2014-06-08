package net.neevek.android.demo.paginize.pages.main;

import android.view.View;
import android.widget.RadioButton;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.demo.paginize.pages.general.FrameInnerPage;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InheritPageLayout;
import net.neevek.android.lib.paginize.annotation.InjectInnerPage;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;

/**
 * Created by neevek on 3/16/14.
 */
@InheritPageLayout(R.layout.page_main)
@InnerPageContainerLayoutResId(R.id.layout_container)
public class MainPage extends FrameInnerPage implements View.OnClickListener {
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

        setTitle("Home!");

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
