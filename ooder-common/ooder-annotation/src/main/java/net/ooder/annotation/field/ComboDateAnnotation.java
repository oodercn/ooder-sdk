package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMBOBOX, inputType = {ComboInputType.date, ComboInputType.datetime, ComboInputType.time}, componentType = ComponentType.COMBOINPUT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComboDateAnnotation {
    String dateEditorTpl() default "";
}
