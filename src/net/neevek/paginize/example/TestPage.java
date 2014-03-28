package net.neevek.paginize.example;

import android.view.View;
import android.widget.Button;
import net.neevek.paginize.R;
import net.neevek.paginize.lib.Page;
import net.neevek.paginize.lib.PageActivity;
import net.neevek.paginize.lib.annotation.InjectView;
import net.neevek.paginize.lib.annotation.PageLayout;

/**
 * Created by neevek on 3/16/14.
 */

@PageLayout(R.layout.page_test)
public class TestPage extends Page implements View.OnClickListener {

    @InjectView(value = R.id.btn_back, listeners = {View.OnClickListener.class})
    private Button mBtnBack;

    public TestPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                hideWithAnimation(true);
                break;
        }
    }
}
