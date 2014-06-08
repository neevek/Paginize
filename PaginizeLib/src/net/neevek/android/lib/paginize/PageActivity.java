package net.neevek.android.lib.paginize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import net.neevek.android.lib.paginize.annotation.InjectPage;
import net.neevek.android.lib.paginize.annotation.InjectPageAnimationManager;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.exception.InjectFailedException;
import net.neevek.android.lib.paginize.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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

    private void initAnnotatedFields() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class clazz = getClass();

        List<Class> lists = new ArrayList<Class>();
        do {
            lists.add(clazz);

            if (mPageManager.getPageAnimationManager() == null) {
                InjectPageAnimationManager pamAnnotation = (InjectPageAnimationManager)clazz.getAnnotation(InjectPageAnimationManager.class);
                if (pamAnnotation != null) {
                    mPageManager.setPageAnimationManager(pamAnnotation.value().newInstance());
                }
            }
        } while ((clazz = clazz.getSuperclass()) != PageActivity.class);

        for (int i = lists.size() - 1; i >= 0; --i) {
            initAnnotatedFields(lists.get(i));
        }
    }

    private void initAnnotatedFields(Class clazz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
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
