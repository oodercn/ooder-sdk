package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.DiaStatusType;
import net.ooder.annotation.ui.Dock;

import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.DIALOG)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface DialogAnnotation {

    String caption() default "";

    String tips() default "";

    @NotNull
    String left() default "220";

    @NotNull
    String top() default "100";

    Dock dock() default Dock.none;

    @NotNull
    String showEffects() default "Classic";

    String hideEffects() default "";

    String iframeAutoLoad() default "";

    String ajaxAutoLoad() default "";

    String initPos() default "";

    DiaStatusType status() default DiaStatusType.normal;

    String name() default "";

    @NotNull
    String imageClass() default "ri-window-line";

    String id() default "";

    boolean cmd() default true;

    boolean movable() default true;

    boolean resizer() default true;

    boolean modal() default true;

    boolean locked() default false;

    String fromRegion() default "";

    String minWidth() default "-1";

    String minHeight() default "-1";

    @NotNull
    String width() default "25em";

    @NotNull
    String height() default "36em";


}