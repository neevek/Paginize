package net.neevek.android.demo.paginize.pages.main;

import android.util.Log;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.ViewPagerInnerPage;
import net.neevek.android.lib.paginize.ViewWrapper;
import net.neevek.android.lib.paginize.annotation.InnerPageContainerLayoutResId;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Author: neevek
 * Date: 7/5/15 6:44 PM
 */
@PageLayout(R.layout.nest_container_page)
@InnerPageContainerLayoutResId(R.id.nest_container)
public class NestContainerPage extends ViewPagerInnerPage {
    private final static String TAG = NestContainerPage.class.getSimpleName();

    public NestContainerPage(ViewWrapper innerPageContainer) {
        super(innerPageContainer);

        addPage(new NestContainerPageSubPage(this).setText("subpage 0, try swiping from right to left"));
        addPage(new NestContainerPageSubPage(this).setText("subpage 1, try swiping from right to left"));
        addPage(new NestContainerPageSubPage(this).setText("subpage 2, try swiping from right to left"));
        addPage(new NestContainerPageSubPage(this).setText("subpage 3, try swiping from left to right"));
    }

    @Override
    public void onShow(Object arg) {
        super.onShow(arg);
        Log.d(TAG, "onShow called");
    }

    @Override
    public void onShown(Object obj) {
        super.onShown(obj);
        Log.d(TAG, "onShown called");
    }

    @Override
    public void onHide() {
        super.onHide();
        Log.d(TAG, "onHide called");
    }

    @Override
    public void onHidden() {
        super.onHidden();
        Log.d(TAG, "onHidden called");
    }
}
