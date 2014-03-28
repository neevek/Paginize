package net.neevek.paginize.example;

import net.neevek.paginize.R;
import net.neevek.paginize.lib.InnerPage;
import net.neevek.paginize.lib.PageActivity;
import net.neevek.paginize.lib.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@PageLayout(R.layout.inner_page_tab2)
public class TabPage2 extends InnerPage {
    public TabPage2(PageActivity context) {
        super(context);
    }
}
