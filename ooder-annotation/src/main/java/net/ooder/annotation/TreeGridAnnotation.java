package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.event.CustomTreeGridEvent;
import net.ooder.annotation.menu.TreeGridMenu;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.Dock;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TreeGridAnnotation {

    TreeGridMenu[] customMenu() default {};

    TreeGridMenu[] bottombarMenu() default {};

    CustomTreeGridEvent[] event() default {};

    boolean isFormField() default true;

    boolean togglePlaceholder() default true;

    boolean directInput() default true;

    boolean editable() default false;

    boolean initFold() default true;

    boolean animCollapse() default false;

    boolean rowResizer() default false;

    boolean colHidable() default false;

    boolean colResizer() default true;

    boolean colMovable() default false;

    boolean noCtrlKey() default true;

    int freezedColumn() default 0;

    int freezedRow() default 0;

    boolean colSortable() default true;

    boolean altRowsBg() default true;

    @NotNull
    boolean showHeader() default true;

    Dock dock() default Dock.fill;

    String uidColumn() default "";

    @NotNull
    String valueSeparator() default ";";

    String currencyTpl() default "$ *";

    String numberTpl() default "";

    @NotNull
    String rowHeight() default "3.0em";

    Class[] customService() default {};

    ComponentType[] bindTypes() default {ComponentType.TREEGRID};

}
