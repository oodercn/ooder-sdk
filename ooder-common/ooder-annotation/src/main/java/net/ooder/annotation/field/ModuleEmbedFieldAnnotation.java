package net.ooder.annotation.field;

import net.ooder.annotation.ui.AppendType;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.EmbedType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.MODULEPLACEHOLDER)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})

public @interface ModuleEmbedFieldAnnotation {
    String src() default "";

    boolean dynLoad() default false;

    EmbedType embed() default EmbedType.module;

    Class bindClass() default Void.class;

    AppendType append() default AppendType.ref;
}
