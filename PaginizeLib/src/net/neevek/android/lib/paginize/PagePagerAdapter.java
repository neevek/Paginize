package net.neevek.android.lib.paginize;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by neevek on 6/16/14.
 */
public abstract class PagePagerAdapter extends PagerAdapter {
  public abstract ViewWrapper getItem(int position);

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    ViewWrapper viewWrapper = getItem(position);
    container.addView(viewWrapper.getView());
    viewWrapper.onAttach();
    return viewWrapper;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    ViewWrapper viewWrapper = (ViewWrapper) object;
    container.removeView(viewWrapper.getView());
    viewWrapper.onDetach();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    ViewWrapper viewWrapper = (ViewWrapper) object;
    return viewWrapper.getView() == view;
  }
}
