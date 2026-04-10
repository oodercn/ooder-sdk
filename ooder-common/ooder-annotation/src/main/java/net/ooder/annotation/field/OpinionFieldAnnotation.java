package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.OPINIONCONFIG, componentType = ComponentType.OPINION)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface OpinionFieldAnnotation {


    String bgimg() default "";

    @NotNull
    Class<? extends Enum> enumClass() default Enum.class;

    String backgroundColor() default "transparent";


    Class[] customService() default {};

    @NotNull
    BorderType borderType() default BorderType.none;

}
