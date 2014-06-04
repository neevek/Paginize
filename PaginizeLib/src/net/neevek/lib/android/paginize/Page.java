package net.neevek.lib.android.paginize;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.lib.android.paginize.annotation.*;
import net.neevek.lib.android.paginize.exception.InjectFailedException;
import net.neevek.lib.android.paginize.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Page encapsulates a View(usually a layout with complex UIs),
 * which is to be put into a ViewGroup and finally be shown on screen.
 *
 * Page is managed by PageManager, we call variants of the PageManager.pushPage()
 * method to put a Page in a stack, which is maintained by PageManager
 *
 * Date: 2/28/13
 * Time: 3:06 PM
 *
 * @author i@neevek.net
 * @version 1.0.0
 * @since 1.0.0
 */

public class Page {
    private View mView;
    // default page type should be normal here.
    private TYPE mType = TYPE.TYPE_NORMAL;
    private Object mReturnData;

    // protected variables to be accessed in subclasses.
    protected PageActivity mContext;

    public static enum TYPE {
        TYPE_NORMAL,
        TYPE_DIALOG,
    }

    public Page(PageActivity pageActivity) {
        mContext = pageActivity;
        Class clazz = getClass();

        try {
            if (clazz.getSuperclass() == Page.class) {
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
                } while ((clazz = clazz.getSuperclass()) != Page.class);

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

                } else if (InjectInnerPage.class.isAssignableFrom(anno.getClass())) {
                    Class type = field.getType();

                    if (!InnerPage.class.isAssignableFrom(type)) {
                        throw new InjectFailedException(type.getName() + " is not type of InnerPage");
                    }

                    field.setAccessible(true);
                    field.set(this, type.getConstructor(PageActivity.class).newInstance(mContext));
                }
            }
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

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

    public View getView() {
        return mView;
    }

    public void setType(TYPE type) {
        mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    // returns true so PageManager will keep only one instance of a certain type of Page
    // when multiple instances of that type of Page are pushed continuously onto the page stack.
    public boolean keepSingleInstance() {
        return false;
    }

    public Object getReturnData() {
        return mReturnData;
    }

    public void setReturnData(Object data) {
        mReturnData = data;
    }

    public PageManager getPageManager() {
        return mContext.getPageManager();
    }

    //**************** methods to show & hide current page ****************//
    public void show() {
        show(null, false);
    }

    public void show(Object arg, boolean animated) {
        show(arg, animated, false);
    }

    public void show(Object arg, boolean animated, boolean hint) {
        getPageManager().pushPage(this, arg, animated, hint);
    }

    protected void hide() {
        if (getPageManager().getTopPage() == this) {
            getPageManager().popPage(false, false);
        }
    }

    protected void hideWithAnimation(final boolean hint) {
        if (getPageManager().getTopPage() == this) {
            getPageManager().popPage(true, hint);
        }
    }

    protected void hideWithAnimationDelayed(final boolean hint) {
        if (getPageManager().getTopPage() == this) {
            getView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPageManager().popPage(true, hint);
                }
            }, 500);
        }
    }

    protected void hideTopPage() {
        getPageManager().popPage(false, false);
    }

    //**************** methods to show a new page ****************//
    public void showPage(Class <? extends Page > pageClass, Object arg, boolean animated) {
        try {
            Page page = pageClass.getConstructor(PageActivity.class).newInstance(mContext);
            page.show(arg, animated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isKeptInStack() {
        return mContext.getPageManager().isPageKeptInStack(this);
    }

    public boolean isAttached() {
        return getView().getParent() != null;
    }
}