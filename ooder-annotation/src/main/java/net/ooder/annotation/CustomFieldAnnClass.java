package net.ooder.annotation;

import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.ModuleViewType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface CustomFieldAnnClass {

    Class beanClass();

    ComponentType componentType();

    ComboInputType[] inputType() default ComboInputType.input;

    ModuleViewType moduleType() default ModuleViewType.NONE;
}
