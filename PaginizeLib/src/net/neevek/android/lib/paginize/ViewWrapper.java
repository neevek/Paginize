package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.annotation.InheritPageLayout;
import net.neevek.android.lib.paginize.annotation.PageLayout;
import net.neevek.android.lib.paginize.exception.InjectFailedException;
import net.neevek.android.lib.paginize.util.AnnotationUtils;
import net.neevek.android.lib.paginize.util.ViewFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neevek on 6/15/14.
 */
public abstract class ViewWrapper {
  /**
   * This field will be made private in the future to make the API consistent
   *
   * @deprecated use {@link #getContext()} instead.
   */
  protected PageActivity mContext;
  private View mView;

  public ViewWrapper(PageActivity pageActivity) {
    mContext = pageActivity;
    init();
  }

  private void init() {
    Class clazz = getClass();

    try {
      List<Class> list = new ArrayList<Class>(4);

      do {
        list.add(clazz);

        if (mView == null && clazz.isAnnotationPresent(PageLayout.class)) {
          mView = mContext.getLayoutInflater().inflate(((PageLayout) clazz.getAnnotation(PageLayout.class)).value(), null);
        }
      } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);

      if (mView == null) {
        throw new IllegalArgumentException("Must specify a layout resource with the @PageLayout annotation on " + clazz.getName());
      }

      if (list.size() > 1) {
        // -2 because a Page with @PageLayout should not have @InheritPageLayout, which will be silently ignored.
        for (int i = list.size() - 2; i >= 0; --i) {
          clazz = list.get(i);
          if (clazz.isAnnotationPresent(InheritPageLayout.class)) {
            InheritPageLayout inheritPageLayoutAnno = (InheritPageLayout) clazz.getAnnotation(InheritPageLayout.class);
            if (inheritPageLayoutAnno.root() != -1) {
              ViewGroup root = (ViewGroup) mView.findViewById(inheritPageLayoutAnno.root());
              if (root == null) {
                throw new IllegalArgumentException("The root specified in @InheritPageLayout is not found.");
              }
              mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), root, true);
            } else {
              mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), (ViewGroup) mView, true);
            }
          }
        }
      }

      ViewFinder viewFinder = new ViewFinder() {
        public View findViewById(int id) {
          return ViewWrapper.this.findViewById(id);
        }
      };

      for (int i = list.size() - 1; i >= 0; --i) {
        AnnotationUtils.initAnnotatedFields(list.get(i), this, viewFinder);
        AnnotationUtils.handleAnnotatedPageConstructors(list.get(i), this, viewFinder);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new InjectFailedException(e);
    }
  }

  public PageActivity getContext() {
    return mContext;
  }

  public View getView() {
    return mView;
  }

  public View findViewById(int id) {
    return mView.findViewById(id);
  }

  protected void hideTopPage() {
    mContext.hideTopPage();
  }

  public boolean isAttached() {
    return mView.getParent() != null;
  }

  /**
   * called when added to the view hierarchy of the host activity *
   */
  public void onAttach() {
  }

  /**
   * called when removed from the view hierarchy of the host activity *
   */
  public void onDetach() {
  }

  /**
   * onBackPressed mirrors Activity.onBackPressed, only the current page(page on the top of the stack) receives this call *
   */
  public boolean onBackPressed() {
    return false;
  }

  /**
   * onActivityResult mirrors Activity.onActivityResult, only the current page(page on the top of the stack) receives this call *
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  }

  /**
   * onPause mirrors Activity.onPause, only the current page(page on the top of the stack) receives this call *
   */
  public void onPause() {
  }

  /**
   * onResume mirrors Activity.onResume, only the current page(page on the top of the stack) receives this call *
   */
  public void onResume() {
  }

  /**
   * onShown is called after the page is pushed on the page stack *
   */
  public void onShown(Object arg) {
  }

  /**
   * onHidden is called after the page is popped out of the page stack *
   */
  public void onHidden() {
  }

  /**
   * onCovered is called for the current page when a new page is pushed on the page stack *
   */
  public void onCovered() {
  }

  /**
   * onUncovered is called for the previous page when the current page is popped out of the page stack *
   */
  public void onUncovered(Object arg) {
  }
}
