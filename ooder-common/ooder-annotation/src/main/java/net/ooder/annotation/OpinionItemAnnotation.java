package net.ooder.annotation;

import net.ooder.annotation.event.CustomOpinionEvent;
import net.ooder.annotation.menu.OpinionMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface OpinionItemAnnotation {

    String comment() default "";

    String renderer() default "";

    String imagePos() default "";

    String imageBgSize() default "";

    String imageRepeat() default "";

    String imageClass() default "";

    String iconFontSize() default "";


    String iconStyle() default "";

    String flagText() default "";

    String flagClass() default "";

    String flagStyle() default "";

    String image() default "";

    String valueSeparator() default "";


    String euClassName() default "";

    BorderType borderType() default BorderType.none;

    boolean activeLast() default true;


    CustomOpinionEvent[] event() default {};

    OpinionMenu[] contextMenu() default {};

    SelModeType selMode() default SelModeType.single;

    IconColorEnum iconColor() default IconColorEnum.NONE;

    ItemColorEnum itemColor() default ItemColorEnum.NONE;

    FontColorEnum fontColor() default FontColorEnum.NONE;

    ComponentType[] bindTypes() default {ComponentType.OPINION, ComponentType.BUTTONLAYOUT, ComponentType.TITLEBLOCK};
}
