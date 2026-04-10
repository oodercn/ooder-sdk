package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.Dock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.RICHEDITOR)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})

public @interface RichEditorAnnotation {


    String id() default "";

    boolean dynLoad() default false;

    boolean selectable() default true;

    String value() default "";

    @NotNull
    Dock dock() default Dock.fill;

    String width() default "32.0em";

    String height() default "25.0em";

    String frameStyle() default "";

    String cmdList() default "font1;font2;align;list;font4;font3;insert;clear;html";

    String cmdFilter() default "";

    boolean enableBar() default true;

    String textType() default "html";

    int steps() default 0;

    String labelCaption() default "";


}
