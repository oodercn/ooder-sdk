package net.ooder.annotation;


import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FormAnnotation {

    ComponentType[] bindTypes() default {ComponentType.FORMLAYOUT, ComponentType.TREEVIEW, ComponentType.PANEL, ComponentType.DIALOG};

    //是否表头
    boolean floatHandler() default false;

    boolean autoLayout() default true;

    //是否 显示网格线
    @NotNull
    boolean solidTreeGridlines() default true;

    @NotNull
    int col() default 2;

    @NotNull
    int row() default 5;

    @NotNull
    FormLayModeType mode() default FormLayModeType.write;

    @NotNull
    StretchType stretchH() default StretchType.all;

    @NotNull
    StretchType stretchHeight() default StretchType.none;


    BorderType borderType() default BorderType.none;

    String background() default "#FFFFFF";

    OverflowType Overflow() default OverflowType.hidden;

    String showEffects() default "Flip V";

    String hideEffects() default "Flip V";

    int rowHeaderWidth() default -1;

    int columnHeaderHeight() default -1;

    int defaultRowSize() default -1;

    int defaultColumnSize() default -1;

    @NotNull
    int defaultRowHeight() default 35;

    int defaultColWidth() default 150;

    @NotNull
    int defaultLabelWidth() default 150;

    @NotNull
    int lineSpacing() default 10;

    @NotNull
    HAlignType textAlign() default HAlignType.center;

    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
