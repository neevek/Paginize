package net.neevek.paginize.demo.pages.general;

import android.widget.TextView;
import net.neevek.lib.android.paginize.InnerPageContainer;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;
import net.neevek.paginize.demo.R;

/**
 * Created by xiejm on 6/4/14.
 */
@PageLayout(R.layout.page_frame_inner)
public abstract class FrameInnerPage extends InnerPageContainer {
    @InjectView(value = R.id.tv_title) TextView mTvTitle;


    public FrameInnerPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    protected void setTitle(String title) {
        mTvTitle.setText(title);
    }
}
