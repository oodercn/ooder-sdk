package net.ooder.annotation;

import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.event.CustomMFormEvent;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MFormAnnotation {

    ComponentType[] bindTypes() default {ComponentType.MFORMLAYOUT, ComponentType.FORMLAYOUT, ComponentType.TREEVIEW, ComponentType.PANEL, ComponentType.DIALOG};

    BorderType borderType() default BorderType.none;

    @NotNull
    String background() default "#FFFFFF";

    OverflowType Overflow() default OverflowType.hidden;

    String showEffects() default "Flip V";

    String hideEffects() default "Flip V";

    //是否表头
    boolean floatHandler() default false;

    //是否 显示网格线
    @NotNull
    boolean solidTreeGridlines() default false;

    @NotNull
    int col() default 1;

    @NotNull
    FormLayModeType mode() default FormLayModeType.none;

    @NotNull
    StretchType stretchH() default StretchType.all;

    @NotNull
    StretchType stretchHeight() default StretchType.none;

    int rowHeaderWidth() default -1;

    int columnHeaderHeight() default -1;

    int defaultRowSize() default -1;

    int defaultColumnSize() default -1;

    @NotNull
    int defaultRowHeight() default 35;

    int defaultColWidth() default 120;

    @NotNull
    int defaultLabelWidth() default 100;

    @NotNull
    int lineSpacing() default 10;

    @NotNull
    HAlignType textAlign() default HAlignType.left;

    CustomFormMenu[] customMenu() default {};

    CustomMFormEvent[] mevent() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
