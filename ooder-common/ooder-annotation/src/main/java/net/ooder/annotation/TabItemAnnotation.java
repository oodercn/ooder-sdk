package net.ooder.annotation;


import net.ooder.annotation.field.TabItem;
import net.ooder.annotation.ui.FontColorEnum;
import net.ooder.annotation.ui.IconColorEnum;
import net.ooder.annotation.ui.ItemColorEnum;
import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface TabItemAnnotation {
    int index() default -1;

    String imageClass() default "";

    boolean closeBtn() default false;

    boolean popBtn() default true;

    String caption() default "";

    String groupName() default "";

    @NotNull
    boolean lazyLoad() default false;

    @NotNull
    boolean initFold() default false;

    @NotNull
    boolean lazyAppend() default true;

    @NotNull
    boolean autoReload() default true;


    @NotNull
    boolean dynDestory() default false;

    Class<? extends TabItem> customItems() default TabItem.class;

    Class[] bindClass() default {};

    CustomFormEvent[] event() default {};


    IconColorEnum iconColor() default IconColorEnum.NONE;

    ItemColorEnum itemColor() default ItemColorEnum.NONE;

    FontColorEnum fontColor() default FontColorEnum.NONE;


}
