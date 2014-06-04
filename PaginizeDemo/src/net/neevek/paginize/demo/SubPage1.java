package net.neevek.paginize.demo;

import android.widget.Toast;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InheritPageLayout;

/**
 * Created by xiejm on 6/4/14.
 */
@InheritPageLayout(R.layout.page_sub1)
public class SubPage1 extends FramePage {
    public SubPage1(PageActivity pageActivity) {
        super(pageActivity);

        setTitle("SubPage1");
    }

    @Override
    protected void onBackButtonClicked() {
        Toast.makeText(mContext, "Back button clicked on SubPage1", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNextButtonClicked() {
        Toast.makeText(mContext, "Next button clicked on SubPage1", Toast.LENGTH_SHORT).show();
    }
}
