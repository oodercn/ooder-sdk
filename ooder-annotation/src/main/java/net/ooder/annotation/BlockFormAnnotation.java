package net.ooder.annotation;


import net.ooder.annotation.NotNull;
import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.SideBarStatusType;
import net.ooder.annotation.ui.StretchType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BlockFormAnnotation {

    BorderType borderType() default BorderType.outset;

    boolean resizer() default false;


    @NotNull
    int defaultRowHeight() default 24;

    @NotNull
    int defaultColWidth() default 200;

    @NotNull
    int defaultLabelWidth() default 100;

    int col() default 0;

    @NotNull
    int lineSpacing() default 10;

    @NotNull
    StretchType stretchH() default StretchType.all;

    @NotNull
    StretchType stretchHeight() default StretchType.none;


    String sideBarCaption() default "";

    String sideBarType() default "";

    SideBarStatusType sideBarStatus() default SideBarStatusType.expand;

    String sideBarSize() default "";

    String background() default "";

    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
