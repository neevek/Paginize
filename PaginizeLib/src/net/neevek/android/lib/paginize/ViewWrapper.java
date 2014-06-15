package net.neevek.android.lib.paginize;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.annotation.*;
import net.neevek.android.lib.paginize.exception.InjectFailedException;
import net.neevek.android.lib.paginize.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neevek on 6/15/14.
 */
public abstract class ViewWrapper {
    private View mView;
    // protected variables to be accessed in subclasses.
    protected PageActivity mContext;

    public ViewWrapper(PageActivity pageActivity) {
        mContext = pageActivity;
        init();
    }

    private void init() {
        Class clazz = getClass();

        try {
            if (clazz.isAnnotationPresent(PageLayout.class)) {
                setView(mContext.getLayoutInflater().inflate(((PageLayout)clazz.getAnnotation(PageLayout.class)).value(), null));
                initAnnotatedFields(clazz);

            } else {
                List<Class> list = new ArrayList<Class>();

                View view = null;
                do {
                    list.add(clazz);

                    if (view == null && clazz.isAnnotationPresent(PageLayout.class)) {
                        view = mContext.getLayoutInflater().inflate(((PageLayout)clazz.getAnnotation(PageLayout.class)).value(), null);
                        setView(view);
                    }
                } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);

                if (view == null) {
                    throw new IllegalArgumentException("Must specify a layout resource with the @PageLayout annotation on " + clazz.getName());
                }

                clazz = getClass();
                if (clazz.isAnnotationPresent(InheritPageLayout.class)) {
                    InheritPageLayout inheritPageLayoutAnno = (InheritPageLayout)clazz.getAnnotation(InheritPageLayout.class);
                    if (inheritPageLayoutAnno.root() != -1) {
                        ViewGroup root = (ViewGroup)view.findViewById(inheritPageLayoutAnno.root());
                        if (root == null) {
                            throw new IllegalArgumentException("The root specified in @InheritPageLayout is not found.");
                        }
                        mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), root, true);
                    } else {
                        mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), (ViewGroup)view, true);
                    }
                }

                for (int i = list.size() - 1; i >= 0; --i) {
                    initAnnotatedFields(list.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new InjectFailedException(e);
        }
    }

    private void initAnnotatedFields(Class clazz) throws InvocationTargetException
            , IllegalAccessException, NoSuchMethodException, InstantiationException {
        Field fields[] = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            Annotation[] annotations = field.getAnnotations();

            if (annotations == null || annotations.length == 0) {
                continue;
            }

            for (int j = 0; j < annotations.length; ++j) {
                Annotation anno = annotations[j];

                if (InjectView.class.isAssignableFrom(anno.getClass())) {
                    InjectView annotation = (InjectView)anno;
                    View view = findViewById(annotation.value());
                    field.setAccessible(true);
                    field.set(this, view);

                    if (annotation.listeners().length > 0) {
                        AnnotationUtils.setListenersForView(clazz, annotation, view, this);
                    }

                } else if (InjectPage.class.isAssignableFrom(anno.getClass())) {
                    Class type = field.getType();

                    if (!Page.class.isAssignableFrom(type)) {
                        throw new InjectFailedException(type.getName() + " is not type of Page");
                    }

                    field.setAccessible(true);
                    field.set(this, type.getConstructor(PageActivity.class).newInstance(mContext));

                } else if (InjectInnerPage.class.isAssignableFrom(anno.getClass())) {
                    Class type = field.getType();

                    if (!InnerPage.class.isAssignableFrom(type)) {
                        throw new InjectFailedException(type.getName() + " is not type of InnerPage");
                    }

                    field.setAccessible(true);
                    field.set(this, type.getConstructor(PageActivity.class).newInstance(mContext));
                } else if (InjectViewWrapper.class.isAssignableFrom(anno.getClass())) {
                    Class type = field.getType();

                    if (!ViewWrapper.class.isAssignableFrom(type)) {
                        throw new InjectFailedException(type.getName() + " is not type of ViewWrapper");
                    }

                    field.setAccessible(true);
                    field.set(this, type.getConstructor(PageActivity.class).newInstance(mContext));
                }
            }
        }
    }

    public void setView(View view) {
        mView = view;
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

    public void showPage(Class <? extends Page > pageClass, boolean animated) {
        showPage(pageClass, animated, null);
    }

    public void showPage(Class <? extends Page > pageClass, boolean animated, Object arg) {
        try {
            Page page = pageClass.getConstructor(PageActivity.class).newInstance(mContext);
            page.show(arg, animated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** onBackPressed mirrors Activity.onBackPressed, only the current page(page on the top of the stack) receives this call **/
    public boolean onBackPressed() { return false; }

    /** onActivityResult mirrors Activity.onActivityResult, only the current page(page on the top of the stack) receives this call **/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  }

    /** onPause mirrors Activity.onPause, only the current page(page on the top of the stack) receives this call **/
    public void onPause() { }

    /** onResume mirrors Activity.onResume, only the current page(page on the top of the stack) receives this call **/
    public void onResume() { }

    /** onShown is called after the page is pushed on the page stack **/
    public void onShown(Object arg) { }

    /** onHidden is called after the page is popped out of the page stack **/
    public void onHidden() { }

    /** onCovered is called for the current page when a new page is pushed on the page stack **/
    public void onCovered() { }

    /** onUncovered is called for the previous page when the current page is popped out of the page stack **/
    public void onUncovered(Object arg) { }
}
