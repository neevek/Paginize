package net.neevek.lib.android.paginize;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.lib.android.paginize.annotation.InheritPageLayout;
import net.neevek.lib.android.paginize.annotation.InjectPage;
import net.neevek.lib.android.paginize.annotation.InjectView;
import net.neevek.lib.android.paginize.annotation.PageLayout;
import net.neevek.lib.android.paginize.exception.InjectFailedException;
import net.neevek.lib.android.paginize.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * An InnerPage encapsulates a View(usually a layout with complex UIs),
 * which is to be put into a ViewGroup and finally be shown on screen.
 *
 * InnerPage is designed to be used in a UI layout that contains multiple views
 * that can be swapped at runtime.
 *
 * InnerPage is managed by InnerPageManager, we call InnerPageManager.setPage()
 * method to set an InnerPage as the current page.
 *
 * Date: 2/28/13
 * Time: 3:06 PM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
 */
public class InnerPage {
    private View mView;
    // protected variables to be accessed in subclasses.
    protected PageActivity mContext;

    public InnerPage(PageActivity context) {
        mContext = context;
        Class clazz = getClass();

        try {
            if (clazz.getSuperclass() == InnerPage.class) {
                if (!clazz.isAnnotationPresent(PageLayout.class)) {
                    throw new IllegalArgumentException("Must specify a layout resource with the @PageLayout annotation on " + clazz.getName());
                }

                mView = mContext.getLayoutInflater().inflate(((PageLayout)clazz.getAnnotation(PageLayout.class)).value(), null);
                initAnnotatedFields(clazz);

            } else {
                List<Class> list = new ArrayList<Class>();

                do {
                    list.add(clazz);

                    if (mView == null && clazz.isAnnotationPresent(PageLayout.class)) {
                        mView = mContext.getLayoutInflater().inflate(((PageLayout)clazz.getAnnotation(PageLayout.class)).value(), null);
                    }
                } while ((clazz = clazz.getSuperclass()) != InnerPage.class);

                if (mView == null) {
                    throw new IllegalArgumentException("Must specify a layout resource with the @PageLayout annotation on " + clazz.getName());
                }

                clazz = getClass();
                if (clazz.isAnnotationPresent(InheritPageLayout.class)) {
                    InheritPageLayout inheritPageLayoutAnno = (InheritPageLayout)clazz.getAnnotation(InheritPageLayout.class);
                    if (inheritPageLayoutAnno.root() != -1) {
                        ViewGroup root = (ViewGroup)mView.findViewById(inheritPageLayoutAnno.root());
                        if (root == null) {
                            throw new IllegalArgumentException("The root specified in @InheritPageLayout is not found.");
                        }
                        mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), root, true);
                    } else {
                        mContext.getLayoutInflater().inflate(inheritPageLayoutAnno.value(), (ViewGroup)mView, true);
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
                }
            }
        }
    }

    public View getView() {
        return mView;
    }

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    //**************** methods to show a new page ****************//
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

    protected void hideTopPage() {
        mContext.hideTopPage();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { }
    public void onPause() { }
    public void onResume() { }
    public void onSet(Object obj) { }
    public void onReplaced() { }
    public void onShown(Object obj) { }
    public void onHidden() { }
    public void onCovered() {}
    public void onUncovered(Object obj) {}

    public boolean onBackPressed() {
        return false;
    }
}