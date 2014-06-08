package net.neevek.android.lib.paginize.annotation;


import net.neevek.android.lib.paginize.anim.PageAnimationManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by neevek on 12/27/13.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectPageAnimationManager {
    Class<? extends PageAnimationManager> value();
}
