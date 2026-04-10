package net.ooder.annotation;


import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.ui.UIPositionType;
import net.ooder.annotation.ui.VisibilityType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface UIAnnotation {

    @NotNull
    public Dock dock() default Dock.none;

    @NotNull
    public VisibilityType visibility() default VisibilityType.visible;

    public String display() default "";

    public boolean selectable() default true;

    public String renderer() default "";

    public String imageClass() default "";

    public String left() default "";

    public String right() default "";

    public String top() default "";

    public String bottom() default "";

    public boolean dynLoad() default false;

    public boolean shadows() default false;

    public int zIndex() default 1;

    public int tabindex() default 1;

    public String width() default "";

    public String height() default "";

    public UIPositionType position() default UIPositionType.STATIC;

}
