package net.neevek.paginize.demo;

import android.view.View;
import android.widget.TextView;
import net.neevek.lib.android.paginize.Page;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;

/**
 * Created by xiejm on 6/4/14.
 */
@PageLayout(R.layout.page_frame)
public abstract class FramePage extends Page implements View.OnClickListener {
    @InjectView(value = R.id.tv_back, listeners = View.OnClickListener.class) TextView mTvBack;
    @InjectView(value = R.id.tv_title, listeners = View.OnClickListener.class) TextView mTvTitle;
    @InjectView(value = R.id.tv_next, listeners = View.OnClickListener.class) TextView mTvNext;


    public FramePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackButtonClicked();
                break;
            case R.id.tv_next:
                onNextButtonClicked();
                break;
        }
    }

    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }

    protected void onBackButtonClicked() {
        hideWithAnimation(true);
    }

    protected abstract void onNextButtonClicked();
}
