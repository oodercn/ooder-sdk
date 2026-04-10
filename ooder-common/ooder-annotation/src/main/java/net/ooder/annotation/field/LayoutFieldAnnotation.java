package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;
import net.ooder.annotation.LayoutItemAnnotation;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.LayoutType;

import java.lang.annotation.*;

@Inherited

@CustomClass( viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.LAYOUTCONFIG, componentType = ComponentType.LAYOUT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface LayoutFieldAnnotation {

    LayoutItemAnnotation[] layoutItems() default {};

    @NotNull
    LayoutType type() default LayoutType.horizontal;

    boolean dragSortable() default false;

    boolean flexSize() default false;

    @NotNull
    BorderType borderType() default BorderType.none;

    @NotNull
    boolean transparent() default true;

    String[] listKey() default {};

    String left() default "0em";

    String top() default "0em";

    @NotNull
    Class<? extends Enum> enumClass() default Enum.class;

    int conLayoutColumns() default -1;


}
