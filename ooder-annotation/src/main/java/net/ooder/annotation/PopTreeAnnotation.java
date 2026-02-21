package net.ooder.annotation;

import net.ooder.annotation.ui.ComponentType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface PopTreeAnnotation {

    String caption() default "";

    ComponentType[] bindTypes() default {ComponentType.TREEBAR, ComponentType.MTREEVIEW,ComponentType.TREEVIEW};
}
