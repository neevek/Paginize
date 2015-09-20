package net.neevek.android.demo.paginize.pages.other;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.PageLayout;

/**
 * Author: neevek
 * Date: 2/1/15 4:12 PM
 */
@PageLayout(R.layout.page_alert)
public class AlertPage extends Page implements View.OnClickListener {
    @InjectView(R.id.tv_content)
    private TextView mTvContent;
    @InjectView(R.id.tv_title)
    private TextView mTvTitle;
    @InjectView(R.id.layout_title)
    private View mLayoutTitle;
    @InjectView(value = R.id.btn_confirm, listenerTypes = View.OnClickListener.class)
    private Button mBtnConfirm;
    @InjectView(value = R.id.btn_cancel, listenerTypes = View.OnClickListener.class)
    private Button mBtnCancel;

    private OnButtonClickListener mOnButtonClickListener;
    private boolean mCancelable = true;
    private boolean mCancelOnTouchOutside = true;
    private boolean mHideOnButtonClicked = true;
    private boolean mHideOnBackPressed = true;

    public AlertPage(PageActivity pageActivity) {
        super(pageActivity);
        setType(TYPE.TYPE_DIALOG);
        interceptAllTouchEvents();
    }

    private void interceptAllTouchEvents() {
        View.OnTouchListener listener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() != R.id.layout_question_alert && mCancelOnTouchOutside) {
                    hide(false);
                }
                return true;
            }
        };

        getView().setOnTouchListener(listener);
        findViewById(R.id.layout_question_alert).setOnTouchListener(listener);
    }

    public AlertPage setPageType(TYPE type) {
        setType(type);
        return this;
    }

    public AlertPage setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
            mLayoutTitle.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public AlertPage setContent(String text) {
        mTvContent.setText(text);
        return this;
    }

    public AlertPage setContentView(View view) {
        ViewGroup parent = (ViewGroup)mTvContent.getParent();
        int index = parent.indexOfChild(mTvContent);
        parent.removeViewAt(index);
        parent.addView(view, index);
        return this;
    }

    public AlertPage setConfirmText(String text) {
        mBtnConfirm.setText(text);
        return this;
    }

    public AlertPage setCancelText(String text) {
        mBtnCancel.setText(text);
        return this;
    }

    public AlertPage hideCancelButton() {
        mBtnCancel.setVisibility(View.GONE);
        findViewById(R.id.view_divider).setVisibility(View.GONE);
        return this;
    }

    public AlertPage hideButtons() {
        findViewById(R.id.view_btn_top_divider).setVisibility(View.GONE);
        findViewById(R.id.layout_btns).setVisibility(View.GONE);
        return this;
    }

    public AlertPage setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
        return this;
    }

    public AlertPage setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public AlertPage setCancelOnTouchOutside(boolean flag) {
        mCancelOnTouchOutside = flag;
        return this;
    }

    public AlertPage setConfirmButtonTextColor(int colorId) {
        mBtnConfirm.setTextColor(colorId);
        return this;
    }

    public AlertPage setCancelButtonTextColor(int color) {
        mBtnCancel.setTextColor(color);
        return this;
    }

    public AlertPage setConfirmButtonTextBold() {
        mBtnConfirm.getPaint().setFakeBoldText(true);
        return this;
    }

    public AlertPage setCancelButtonTextBold() {
        mBtnCancel.getPaint().setFakeBoldText(true);
        return this;
    }

    public AlertPage setHideOnBackPressed(boolean needHide) {
        mHideOnBackPressed = needHide;
        return this;
    }

    public AlertPage setHideOnButtonClicked(boolean hideOnButtonClicked) {
        mHideOnButtonClicked = hideOnButtonClicked;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (mHideOnButtonClicked) {
            hide(false);
        }
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onConfirmed();
                }
                break;
            case R.id.btn_cancel:
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onCanceled();
                }
                break;
        }

    }

    @Override
    public boolean onBackPressed() {
        if (mHideOnBackPressed) {
            hide(false);
        }
        return true;
    }

    @Override
    public void hide(boolean animated) {
        if (mCancelable) {
            super.hide(animated);
        }
    }

    @Override
    public boolean shouldSaveInstanceState() {
        return false;
    }

    public static interface OnButtonClickListener {
        void onConfirmed();
        void onCanceled();
    }
}
