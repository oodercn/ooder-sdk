package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@CustomClass( moduleType = ModuleViewType.TITLEBLOCKCONFIG, viewType = CustomViewType.COMPONENT, componentType = ComponentType.TITLEBLOCK)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface TitleBlockFieldAnnotation {

    Class bindClass() default Void.class;

    String bgimg() default "";

    Class<? extends Enum> enumClass() default Enum.class;

    String backgroundColor() default "transparent";

    Class[] customService() default {};

    @NotNull
    BorderType borderType() default BorderType.none;


}
