package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import net.neevek.android.lib.paginize.annotation.ViewPagerResId;
import net.neevek.android.lib.paginize.exception.InjectFailedException;

/**
 * A Page that is used to hold a ViewPager
 *
 * Created by neevek on 6/15/14.
 */
public abstract class ViewPagerPage extends Page {
    private ViewPager mViewPager;

    public ViewPagerPage(PageActivity pageActivity) {
        super(pageActivity);

        Class clazz = getClass();

        ViewPagerResId resIdAnnotation = null;

        try {
            do {
                if (clazz.isAnnotationPresent(ViewPagerResId.class)) {
                    resIdAnnotation = (ViewPagerResId)clazz.getAnnotation(ViewPagerResId.class);
                    break;
                }
            } while ((clazz = clazz.getSuperclass()) != ViewPagerPage.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new InjectFailedException(e);
        }

        if (resIdAnnotation == null) {
            throw new IllegalStateException("Must specify a ViewPager resource Id for "+ clazz.getSimpleName() +" with the @ViewPagerResId annotation.");
        }

        View view = getView().findViewById(resIdAnnotation.value());
        if (view == null) {
            throw new IllegalStateException("Can not find the View with the specified resource ID: " + resIdAnnotation.value());
        }
        if (!(view instanceof ViewPager)) {
            throw new IllegalStateException("The specified View with @ViewPagerResId is not of type ViewPager.");
        }

        mViewPager = (ViewPager)view;
    }

    protected ViewPager getViewPager() {
        return mViewPager;
    }

    private PagePagerAdapter getPagePagerAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter instanceof PagePagerAdapter) {
            return (PagePagerAdapter)adapter;
        }
        return null;
    }

    public boolean onBackPressed() {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            return adapter.getItem(mViewPager.getCurrentItem()).onBackPressed();
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onPause() {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onPause();
        }
    }

    public void onResume() {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onResume();
        }
    }

    public void onShown(Object arg) {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onShown(arg);
        }
    }

    public void onHidden() {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onHidden();
        }
    }

    public void onCovered() {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onCovered();
        }
    }

    public void onUncovered(Object arg) {
        PagePagerAdapter adapter = getPagePagerAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            adapter.getItem(mViewPager.getCurrentItem()).onUncovered(arg);
        }
    }
}
