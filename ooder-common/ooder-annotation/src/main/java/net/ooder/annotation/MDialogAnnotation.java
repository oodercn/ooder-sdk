package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.DiaStatusType;
import net.ooder.annotation.ui.Dock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MDialogAnnotation {

    String caption() default "";

    String tips() default "";

    int left() default 220;

    int top() default 0;

    @NotNull
    String showEffects() default "Flip H";

    @NotNull
    String hideEffects() default "Flip H";

    String iframeAutoLoad() default "";

    String ajaxAutoLoad() default "";


    String name() default "";

    String imageClass() default "";

    String initPos() default "";

    @NotNull
    Dock dock() default Dock.height;

    @NotNull
    DiaStatusType status() default DiaStatusType.right;

    boolean cmd() default true;

    boolean movable() default true;

    boolean resizer() default true;

    boolean modal() default false;

    boolean locked() default false;

    String fromRegion() default "";

    String minWidth() default "-1";

    String minHeight() default "-1";


    @NotNull
    String width() default "400";

    @NotNull
    String height() default "600";


}
