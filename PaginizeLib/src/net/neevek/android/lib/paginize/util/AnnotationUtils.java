package net.neevek.android.lib.paginize.util;

import android.text.TextWatcher;
import android.view.View;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.exception.NotImplementedInterfaceException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neevek on 1/1/14.
 */
public class AnnotationUtils {
    private static Map<Class, String> sSetListenerMethodMap = new HashMap<Class, String>();
    static {
        sSetListenerMethodMap.put(TextWatcher.class, "addTextChangedListener");
    }

    private static void setListenersForView(Class clazz, InjectView annotation, View view, Object listener) throws InvocationTargetException
        , IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class[] listeners = annotation.listeners();

        for (int j = 0; j < listeners.length; ++j) {
            Class listenerClass = listeners[j];

            if (!listenerClass.isAssignableFrom(clazz)) {
                throw new NotImplementedInterfaceException(clazz.getName() + " does not implement " + listenerClass.getName());
            }

            String methodName = sSetListenerMethodMap.get(listenerClass);
            if (methodName == null) {
                methodName = listenerClass.getSimpleName();

                // for interfaces from android.support.v4.**, Class.getSimpleName() may return names that contain the dollar sign
                // I have no idea whether this is a bug, the following workaround fixes the problem
                int index = methodName.lastIndexOf('$');
                if (index != -1) {
                    methodName = methodName.substring(index + 1);
                }
                methodName = "set" + methodName;

                sSetListenerMethodMap.put(listenerClass, methodName);
            }

            try {
                Method method = view.getClass().getMethod(methodName, listenerClass);
                method.invoke(view, listener);
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodException("No such method: " + listenerClass.getSimpleName() + "." + methodName
                        + ", you have to manually add the set-listener method to sSetListenerMethodMap.");
            }
        }
    }

    public static void initAnnotatedFields(Class clazz, Object object, ViewFinder viewFinder) throws InvocationTargetException
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
                    View view = viewFinder.findViewById(annotation.value());
                    field.setAccessible(true);
                    field.set(object, view);

                    if (annotation.listeners().length > 0) {
                        AnnotationUtils.setListenersForView(clazz, annotation, view, object);
                    }

                }
            }
        }
    }
}
