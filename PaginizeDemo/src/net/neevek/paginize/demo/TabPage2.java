package net.neevek.paginize.demo;


import net.neevek.lib.android.paginize.InnerPage;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab2)
public class TabPage2 extends InnerPage {
    public TabPage2(PageActivity context) {
        super(context);
    }
}
