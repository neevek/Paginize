package net.neevek.paginize.demo.pages.other;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import net.neevek.lib.android.paginize.PageActivity;
import net.neevek.lib.android.paginize.annotation.InheritPageLayout;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.paginize.demo.R;
import net.neevek.paginize.demo.pages.general.FramePage;

/**
 * Created by neevek on 3/16/14.
 */

@InheritPageLayout(R.layout.page_test)
public class TestPage extends FramePage implements View.OnClickListener {

    @InjectView(value = R.id.btn_back, listeners = {View.OnClickListener.class})
    private Button mBtnBack;

    public TestPage(PageActivity pageActivity) {
        super(pageActivity);

        setTitle("Test Page!");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                hideWithAnimation(true);
                break;
            default:
                super.onClick(v);
        }
    }


    @Override
    protected void onNextButtonClicked() {
        Toast.makeText(mContext, "Next button clicked", Toast.LENGTH_SHORT).show();
    }
}
