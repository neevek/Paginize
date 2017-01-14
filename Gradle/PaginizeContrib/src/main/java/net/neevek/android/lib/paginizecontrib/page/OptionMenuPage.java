package net.neevek.android.lib.paginizecontrib.page;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.neevek.android.lib.paginize.Page;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectViewByName;
import net.neevek.android.lib.paginize.annotation.PageLayoutName;
import net.neevek.android.lib.paginizecontrib.P;
import net.neevek.android.lib.paginizecontrib.R;
import net.neevek.android.lib.paginizecontrib.util.Util;

/**
 * Created by neevek on 08/01/2017.
 */
@PageLayoutName(P.layout.paginize_contrib_page_option_menu)
public class OptionMenuPage extends Page implements View.OnClickListener {
  private final static int ITEM_HEIGHT = 48;  // dp
  private final static int ITEM_PADDING = 16;  // dp
  private final static int ITEM_TEXT_SIZE = 16;  // dp
  private final static int MENU_ITEM_ID = 0;

  @InjectViewByName(P.id.paginize_contrib_layout_option_menu_scroll_view)
  private ScrollView mScrollView;
  @InjectViewByName(P.id.paginize_contrib_layout_option_menu_item_container)
  private LinearLayout mMenuItemContainer;

  private OnMenuItemClickListener mListener;

  private int mVisibleItemCountForPortrait = 6;
  private int mVisibleItemCountForLandscape = 4;

  public OptionMenuPage(PageActivity pageActivity) {
    super(pageActivity);
    setType(TYPE.TYPE_DIALOG);
    getView().setOnClickListener(this);

    if (Build.VERSION.SDK_INT >= 21) {
      mScrollView.setClipToOutline(true);
    }
    setMenuItemContainerWidth();
  }

  private void setMenuItemContainerWidth() {
    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mScrollView.getLayoutParams();
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      lp.weight = 0.75f;
    } else {
      lp.weight = 0.45f;
    }
    mScrollView.requestLayout();
  }

  private void setMenuItemContainerHeightIfNeeded() {
    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mScrollView.getLayoutParams();
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      if (mMenuItemContainer.getChildCount() <= mVisibleItemCountForPortrait) {
        return;
      }
      lp.height = getItemHeight() * mVisibleItemCountForPortrait;
    } else {
      if (mMenuItemContainer.getChildCount() <= mVisibleItemCountForLandscape) {
        return;
      }
      lp.height = getItemHeight() * mVisibleItemCountForLandscape;
    }
    mScrollView.requestLayout();
  }

  public OptionMenuPage addOptionMenuItem(String title) {
    int childCount = mMenuItemContainer.getChildCount();
    mMenuItemContainer.addView(createView(title), childCount);

    setMenuItemContainerHeightIfNeeded();
    return this;
  }

  public OptionMenuPage setVisibleItemCountForLandscape(int visibleItemCountForLandscape) {
    mVisibleItemCountForLandscape = visibleItemCountForLandscape;
    return this;
  }

  public OptionMenuPage setVisibleItemCountForPortrait(int visibleItemCountForPortrait) {
    mVisibleItemCountForPortrait = visibleItemCountForPortrait;
    return this;
  }

  public OptionMenuPage setOnMenuItemClickListener(OnMenuItemClickListener listener) {
    mListener = listener;
    return this;
  }

  private View createView(String title) {
    LinearLayout linearLayout = createLinearLayout();

    linearLayout.addView(createOptionMenuItem(title));
    if (linearLayout.getChildCount() > 0) {
      linearLayout.addView(createDivider());
    }
    return linearLayout;
  }

  private LinearLayout createLinearLayout() {
    LinearLayout linearLayout = new LinearLayout(getContext());
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams linearLayoutParams
        = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    linearLayout.setLayoutParams(linearLayoutParams);
    linearLayout.setGravity(Gravity.CENTER_VERTICAL);
    return linearLayout;
  }

  private View createDivider() {
    View divider = new View(getContext());
    LinearLayout.LayoutParams linearParams  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    divider.setLayoutParams(linearParams);
    divider.setBackgroundColor(getContext().getResources().getColor(R.color.paginize_contrib_divider));
    return divider;
  }

  private TextView createOptionMenuItem(String title) {
    int padding = getItemPadding();

    TextView button = new TextView(getContext());
    LinearLayout.LayoutParams buttonLayoutParams
        = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemHeight());
    button.setLayoutParams(buttonLayoutParams);
    button.setPadding(padding, 0, padding, 0);
    button.setSingleLine();
    button.setId(MENU_ITEM_ID);
    button.setGravity(Gravity.CENTER_VERTICAL);
    button.setBackgroundResource(R.drawable.paginize_contrib_item_selector);
    button.setTextColor(getResources().getColor(R.color.paginize_contrib_primary_text));
    button.setTextSize(ITEM_TEXT_SIZE);
    button.setText(title);
    button.setTag(new Pair<Integer, String>(mMenuItemContainer.getChildCount(), title));
    button.setOnClickListener(this);
    return button;
  }

  private int getItemPadding() {
    return (int) Util.dp2px(ITEM_PADDING, getContext().getResources().getDisplayMetrics());
  }

  private int getItemHeight() {
    return (int) Util.dp2px(ITEM_HEIGHT, getContext().getResources().getDisplayMetrics());
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case MENU_ITEM_ID:
        hide(true);

        if (mListener != null) {
          Pair<Integer, String> pair = (Pair<Integer, String>)v.getTag();
          mListener.onMenuItemClicked(pair.first, pair.second);
        }
        break;

      default:
        hide(true);
    }

  }

  @Override
  public boolean onBackPressed() {
    hide(true);
    return true;
  }

  @Override
  public boolean shouldSaveInstanceState() {
    return false;
  }

  public interface OnMenuItemClickListener {
    void onMenuItemClicked(int index, String title);
  }

  @Override
  public boolean onPushPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    ObjectAnimator.ofFloat(newPageView, "alpha", 0.f, 1.f).setDuration(getAnimationDuration()).start();
    return true;
  }

  @Override
  public boolean onPopPageAnimation(View oldPageView, View newPageView, AnimationDirection animationDirection) {
    ObjectAnimator.ofFloat(oldPageView, "alpha", 1.f, 0.f).setDuration(getAnimationDuration()).start();
    return true;
  }

  @Override
  public int getAnimationDuration() {
    return 300;
  }
}

