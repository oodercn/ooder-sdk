package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, componentType = ComponentType.STATUSBUTTONS)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface StatusButtonsAnnotation {


    String barHeight() default "2.5em";

    @NotNull
    UIPositionType position() default UIPositionType.STATIC;

    @NotNull
    String width() default "auto";

    @NotNull
    AlignType align() default AlignType.center;

    String itemMargin() default "";

    String itemPadding() default "";

    String itemWidth() default "";

    @NotNull
    AlignType itemAlign() default AlignType.center;

    Dock dock() default Dock.fill;

    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.right;

    @NotNull
    BorderType borderType() default BorderType.flat;

    @NotNull
    StatusItemType itemType() default StatusItemType.button;

    boolean autoIconColor() default true;

    boolean autoItemColor() default false;

    boolean autoFontColor() default false;

    boolean connected() default false;

}
