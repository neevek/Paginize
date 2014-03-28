package net.neevek.paginize.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.paginize.lib.anim.PageAnimationManager;
import net.neevek.paginize.lib.annotation.InjectPage;
import net.neevek.paginize.lib.annotation.InjectPageAnimationManager;
import net.neevek.paginize.lib.annotation.InjectView;
import net.neevek.paginize.lib.exception.InjectFailedException;
import net.neevek.paginize.lib.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by neevek on 12/26/13.
 */
public class PageActivity extends Activity {
    private PageManager mPageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageManager = new PageManager((ViewGroup)findViewById(android.R.id.content));

        try {
            initAnnotatedFields();

        } catch (Exception e) {
            e.printStackTrace();
            throw new InjectFailedException(e);
        }
    }

    private void initAnnotatedFields() throws InvocationTargetException
        , IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class clazz = getClass();

        InjectPageAnimationManager pamAnnotation = (InjectPageAnimationManager)clazz.getAnnotation(InjectPageAnimationManager.class);
        if (pamAnnotation != null) {
            Class<? extends PageAnimationManager> pamClass = pamAnnotation.value();
            mPageManager.setPageAnimationManager(pamClass.newInstance());
        }

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
                    field.set(this, type.getConstructor(PageActivity.class).newInstance(this));

                }
            }
        }
    }

    public PageManager getPageManager() {
        return mPageManager;
    }

    //**************** methods to show a new page ****************//
    public void showPage(Class <? extends Page > pageClass, boolean animated) {
        showPage(pageClass, animated, null);
    }

    public void showPage(Class <? extends Page > pageClass, boolean animated, Object arg) {
        try {
            Page page = pageClass.getConstructor(PageActivity.class).newInstance(this);
            page.show(arg, animated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideTopPage() {
        getPageManager().popPage(false, false);
    }

    public int getPageCount() {
        return mPageManager.getPageCount();
    }

    @Override
    public void onBackPressed() {
        if (!mPageManager.onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPageManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPageManager.onDestroy();
    }


}
