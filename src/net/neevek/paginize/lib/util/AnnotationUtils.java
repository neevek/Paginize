package net.neevek.paginize.lib.util;

import android.text.TextWatcher;
import android.view.View;
import net.neevek.paginize.lib.annotation.InjectView;
import net.neevek.paginize.lib.exception.NotImplementedInterfaceException;

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

    public static void setListenersForView(Class clazz, InjectView annotation, View view, Object listener) throws InvocationTargetException
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
}
