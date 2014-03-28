package net.neevek.paginize.example;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import net.neevek.paginize.R;
import net.neevek.paginize.lib.Page;
import net.neevek.paginize.lib.PageActivity;
import net.neevek.paginize.lib.annotation.InjectView;
import net.neevek.paginize.lib.annotation.PageLayout;


@PageLayout(R.layout.page_alert)
public class AlertPage extends Page implements View.OnClickListener {

    @InjectView(value = R.id.btn_confirm, listeners = {View.OnClickListener.class})
    private Button mBtnConfirm;

    public AlertPage(PageActivity pageActivity) {
        super(pageActivity);
        setType(TYPE.TYPE_DIALOG);
        interceptAllTouchEvents();
    }

    private void interceptAllTouchEvents() {
        getView().setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                hide();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        hide();
        return true;
    }
}
