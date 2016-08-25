package net.neevek.android.demo.paginize.pages;

import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import net.neevek.android.demo.paginize.R;
import net.neevek.android.lib.paginize.PageActivity;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.InsertPageLayout;

/**
 * Created by neevek on 3/16/14.
 */
@InsertPageLayout(value = R.layout.page_simple, parent = R.id.layout_content_container)
public class CustomTransitionAnimationPage extends FramePage {
  private final static int ANIMATION_DURATION = 350;

  @InjectView(R.id.tv_text)
  private TextView mTvText;

  public CustomTransitionAnimationPage(PageActivity pageActivity) {
    super(pageActivity);

    showLoadingUI();

    setTitle("Custom Transition Animation");
    mTvText.setText(Html.fromHtml(
        "This page demonstrates how custom transition animation can be " +
        "implemented with Paginize.<br><br> Note, <b>Swipe to hide</b> is " +
        "disabled for this page(its <i>canSwipeToHide()</i> method returns " +
        "<b>false</b>).<br><br>" +
        "Also, this page demonstrates how the elegant <b>Loading... effect</b> " +
        "implemented in <b>FramePage</> is used. And remember, it can be " +
        "reused all over the entire app, pages just inherit <b>FramePage</b> " +
        "to have the features."
    ));
  }

  @Override
  public void onShown() {
    super.onShown();
    postDelayed(new Runnable() {
      @Override
      public void run() {
        // simulate delay for loading data from network
        // show content ui after data being loaded
        showContentUI();
      }
    }, 2000);
  }

  @Override
  public boolean canSwipeToHide() {
    return false;
  }

  @Override
  public boolean onPushPageAnimation(
      View oldPageView, View newPageView, AnimationDirection direction) {
    if (oldPageView != null) {
      // animate oldPageView if needed
    }
    if (newPageView != null) {
      newPageView.startAnimation(
          AnimationUtils.loadAnimation(getContext(), R.anim.push_bottom_in));
    }
    return true;
  }

  @Override
  public boolean onPopPageAnimation(
      View oldPageView, View newPageView, AnimationDirection direction) {
    if (oldPageView != null) {
      oldPageView.startAnimation(
          AnimationUtils.loadAnimation(getContext(), R.anim.push_top_out));
    }
    if (newPageView != null) {
      // animate newPageView if needed
    }
    return true;
  }

  @Override
  public int getAnimationDuration() {
    return ANIMATION_DURATION;
  }

}

