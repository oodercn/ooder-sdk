package net.ooder.annotation;

import net.ooder.annotation.event.CustomTreeEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.menu.CustomGalleryMenu;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface TitleBlockItemAnnotation {

    String msgnum() default "";

    String more() default "";

    String title() default "";

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


    CustomFormMenu[] customMenu() default {};

    CustomFormMenu[] bottombarMenu() default {};

    CustomTreeEvent[] event() default {};

    CustomGalleryMenu[] contextMenu() default {};

    IconColorEnum iconColor() default IconColorEnum.NONE;

    ItemColorEnum itemColor() default ItemColorEnum.NONE;

    FontColorEnum fontColor() default FontColorEnum.NONE;

    SelModeType selMode() default SelModeType.single;

    ComponentType[] bindTypes() default {ComponentType.GALLERY, ComponentType.BUTTONLAYOUT, ComponentType.TITLEBLOCK};
}
