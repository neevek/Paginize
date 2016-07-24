package net.neevek.android.lib.paginize.util;

import android.os.Build;
import android.text.TextWatcher;
import android.view.View;
import net.neevek.android.lib.paginize.annotation.InjectView;
import net.neevek.android.lib.paginize.annotation.ListenerDefs;
import net.neevek.android.lib.paginize.annotation.SetListeners;
import net.neevek.android.lib.paginize.exception.NotImplementedInterfaceException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Utility class to handle Annotations and inject objects accordingly
 *
 * @see net.neevek.android.lib.paginize.ViewWrapper
 * @see net.neevek.android.lib.paginize.PageActivity
 */
public final class AnnotationUtils {
  private static Map<Class, String> sSetListenerMethodMap = new HashMap<Class, String>();

  static {
    sSetListenerMethodMap.put(TextWatcher.class, "addTextChangedListener");
    if (Build.VERSION.SDK_INT >= 11) {
      sSetListenerMethodMap.put(View.OnLayoutChangeListener.class, "addOnLayoutChangeListener");
    }
    if (Build.VERSION.SDK_INT >= 12) {
      sSetListenerMethodMap.put(View.OnAttachStateChangeListener.class, "addOnAttachStateChangeListener");
    }
  }

  private static void setListenersForView(View view, Class[] listenerTypes, Object listener) throws InvocationTargetException
      , IllegalAccessException, NoSuchMethodException, InstantiationException {

    for (int j = 0; j < listenerTypes.length; ++j) {
      Class listenerClass = listenerTypes[j];

      if (!listenerClass.isAssignableFrom(listener.getClass())) {
        throw new NotImplementedInterfaceException("When injecting listener for view 0x" + Integer.toHexString(view.getId()) + ", we find that "
            + listener.getClass().getName() + " does not implement " + listenerClass.getName());
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
            + ", you have to manually add the set-listener method to sSetListenerMethodMap to support injecting listener for view 0x" + Integer.toHexString(view.getId()));
      }
    }
  }

  public static void handleAnnotatedConstructors(Class clazz, Object object, ViewFinder viewFinder, boolean initForLazy) throws InvocationTargetException
      , IllegalAccessException, NoSuchMethodException, InstantiationException {

    Constructor[] constructors = clazz.getConstructors();
    for (int i = 0; i < constructors.length; ++i) {

      Constructor constructor = constructors[i];
      Annotation[] annotations = constructor.getAnnotations();
      for (int j = 0; j < annotations.length; ++j) {

        Annotation anno = annotations[j];
        if (!(anno instanceof ListenerDefs)) {
          continue;
        }

        ListenerDefs annoContainer = (ListenerDefs) anno;
        if (annoContainer.value().length == 0) {
          continue;
        }

        Map<Class, Object> targetListenerCache = new HashMap<Class, Object>();

        Annotation[] setListenerAnnoArray = annoContainer.value();
        for (int k = 0; k < setListenerAnnoArray.length; ++k) {
          SetListeners setListenersAnno = (SetListeners) setListenerAnnoArray[k];

          if ((!initForLazy && setListenersAnno.lazy()) || (initForLazy && !setListenersAnno.lazy())) {
            continue;
          }

          View view = viewFinder.findViewById(setListenersAnno.view());
          if (view == null) {
            if (initForLazy && setListenersAnno.lazy()) {
              // view == null is tolerable in this case, we assume that this field
              // be injected later with another call to ViewWrapper.lazyInitializeLayout().
              continue;
            }

            throw new IllegalArgumentException("The view specified in @SetListeners is not found: 0x" + Integer.toHexString(setListenersAnno.view()) +
                ", if this field is meant to be injected lazily, remember to specify the 'lazy' attribute.");
          }

          Object targetListener = getTargetListener(clazz, object, targetListenerCache, setListenersAnno.listener(), "@SetListeners");

          if (targetListener == null) {
            targetListener = object;
          }

          AnnotationUtils.setListenersForView(view, setListenersAnno.listenerTypes(), targetListener);
        }

      }
    }
  }

  public static void initAnnotatedFields(Class clazz, Object object, ViewFinder viewFinder, boolean initForLazy) throws InvocationTargetException
      , IllegalAccessException, NoSuchMethodException, InstantiationException {
    Field fields[] = clazz.getDeclaredFields();

    Map<Class, Object> targetListenerCache = new HashMap<Class, Object>();

    for (int i = 0; i < fields.length; ++i) {
      Field field = fields[i];
      Annotation[] annotations = field.getAnnotations();

      if (annotations == null || annotations.length == 0) {
        continue;
      }

      for (int j = 0; j < annotations.length; ++j) {
        Annotation anno = annotations[j];

        if (!InjectView.class.isAssignableFrom(anno.getClass())) {
          continue;
        }

        InjectView injectViewAnno = (InjectView) anno;
        if ((!initForLazy && injectViewAnno.lazy()) || (initForLazy && !injectViewAnno.lazy())) {
          continue;
        }

        View view = viewFinder.findViewById(injectViewAnno.value());
        if (view == null) {
          if (initForLazy && injectViewAnno.lazy()) {
            // view == null is tolerable in this case, we assume that this field
            // be injected later with another call to ViewWrapper.lazyInitializeLayout().
            continue;
          }

          throw new IllegalArgumentException("View 0x"+ Integer.toHexString(injectViewAnno.value()) +" specified in @InjectView on this field is not found: " + field.getName()
              + ", if this field is meant to be injected lazily, remember to specify the 'lazy' attribute.");
        }

        try {
          field.setAccessible(true);
          field.set(object, view);

        } catch (IllegalAccessException e) {
          String errMsg = "@InjectView() on '" + field.getName() + "' failed. ";
          if (field.getType() != view.getClass()) {
            errMsg += (view.getClass().getSimpleName() + " cannot be cast to " + field.getType().getSimpleName());
          }
          throw new IllegalAccessException(errMsg);

        } catch (IllegalArgumentException e) {
          String errMsg = "@InjectView() on '" + field.getName() + "' failed. ";
          if (field.getType() != view.getClass()) {
            errMsg += (view.getClass().getSimpleName() + " cannot be cast to " + field.getType().getSimpleName());
          }
          throw new IllegalArgumentException(errMsg);
        }


        Class[] listenerTypes = injectViewAnno.listenerTypes();
        if (listenerTypes == null || listenerTypes.length == 0) {
          continue;
        }

        Object targetListener = getTargetListener(clazz, object, targetListenerCache, injectViewAnno.listener(), "@InjectView");
        if (targetListener == null) {
          targetListener = object;
        }
        AnnotationUtils.setListenersForView(view, injectViewAnno.listenerTypes(), targetListener);

      }
    }
  }

  private static Object getTargetListener(Class clazz,
                                          Object object,
                                          Map<Class, Object> targetListenerCache,
                                          Class targetListenerClass,
                                          String tag)
      throws InstantiationException, IllegalAccessException, InvocationTargetException {

    if (targetListenerClass == null || targetListenerClass == void.class) {
      return null;
    }

    Object targetListener = targetListenerCache.get(targetListenerClass);

    if (targetListener == null) {
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

        targetListenerCache.put(targetListenerClass, targetListener);
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("The 'listener' field: '"+ targetListenerClass.getSimpleName() +"' in " + tag + " must contain a default constructor without arguments.");
      }
    }

    return targetListener;
  }
}
