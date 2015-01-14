package net.neevek.android.lib.paginize.util;

import android.text.TextWatcher;
import android.view.View;
import net.neevek.android.lib.paginize.annotation.DecoratePageConstructor;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.SetListeners;
import net.neevek.android.lib.paginize.exception.NotImplementedInterfaceException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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

    private static void setListenersForView(View view, Class[] listenerTypes, Object listener) throws InvocationTargetException
        , IllegalAccessException, NoSuchMethodException, InstantiationException {

        for (int j = 0; j < listenerTypes.length; ++j) {
            Class listenerClass = listenerTypes[j];

            if (!listenerClass.isAssignableFrom(listener.getClass())) {
                throw new NotImplementedInterfaceException(listener.getClass().getName() + " does not implement " + listenerClass.getName());
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

    public static void handleAnnotatedPageConstructors(Class clazz, Object object, ViewFinder viewFinder) throws InvocationTargetException
            , IllegalAccessException, NoSuchMethodException, InstantiationException {

        Constructor[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; ++i) {

            Constructor constructor = constructors[i];
            Annotation[] annotations = constructor.getAnnotations();
            for (int j = 0; j < annotations.length; ++j) {

                Annotation anno = annotations[j];
                if (!(anno instanceof DecoratePageConstructor)) {
                    continue;
                }

                DecoratePageConstructor annoContainer = (DecoratePageConstructor)anno;
                if (annoContainer.viewListeners().length == 0) {
                    continue;
                }

                Annotation[] listenerAnno = annoContainer.viewListeners();
                for (int k = 0; k < listenerAnno.length; ++k) {
                    SetListeners setListenersAnno = (SetListeners) listenerAnno[k];
                    View view = viewFinder.findViewById(setListenersAnno.view());
                    if (view == null) {
                        throw new IllegalArgumentException("The view specified in @SetListeners is not found.");
                    }

                    Object targetListener = object;
                    if (setListenersAnno.listener() != null && setListenersAnno.listener() != void.class) {
                        Class targetListenerClass = setListenersAnno.listener();
                        try {
                            boolean isStatic = Modifier.isStatic(targetListenerClass.getModifiers());
                            if (isStatic) {
                                Constructor ctor = targetListenerClass.getDeclaredConstructor();
                                ctor.setAccessible(true);
                                targetListener = ctor.newInstance();

                            } else {
                                Constructor ctor = targetListenerClass.getDeclaredConstructor(clazz);
                                ctor.setAccessible(true);
                                targetListener = ctor.newInstance(object);
                            }
                        } catch (NoSuchMethodException e) {
                            throw new IllegalArgumentException("The 'listener' field in @SetListeners must contain a default constructor without arguments.");
                        }
                    }

                    AnnotationUtils.setListenersForView(view, setListenersAnno.listenerTypes(), targetListener);
                }

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

                    if (annotation.listenerTypes().length > 0) {
                        AnnotationUtils.setListenersForView(view, annotation.listenerTypes(), object);
                    }

                }
            }
        }
    }
}
