package net.neevek.android.lib.paginize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: neevek
 * Date: 12/24/13
 * Time: 10:59 PM
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritPageLayout {
    int value();

    // -1 if the specified layout is to be appended to root
    // of the inherited layout
    int root() default -1;
}
