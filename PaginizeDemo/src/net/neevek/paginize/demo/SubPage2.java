package net.neevek.paginize.demo;

import android.widget.Toast;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InheritPageLayout;

/**
 * Created by xiejm on 6/4/14.
 */
@InheritPageLayout(R.layout.page_sub2)
public class SubPage2 extends FramePage {
    public SubPage2(PageActivity pageActivity) {
        super(pageActivity);

        setTitle("SubPage2");
    }

    @Override
    protected void onBackButtonClicked() {
        Toast.makeText(mContext, "Back button clicked on SubPage2", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNextButtonClicked() {
        Toast.makeText(mContext, "Next button clicked on SubPage2", Toast.LENGTH_SHORT).show();
    }
}
