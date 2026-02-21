package net.ooder.annotation;


import net.ooder.annotation.event.CustomTreeEvent;
import net.ooder.annotation.menu.TreeMenu;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.SelModeType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface TreeAnnotation {
    @NotNull
    boolean formField() default true;

    @NotNull
    boolean initFold() default false;

    boolean animCollapse() default false;

    boolean dynDestory() default false;

    @NotNull
    boolean lazyLoad() default false;

    @NotNull
    boolean helpBar() default false;

    @NotNull
    boolean autoReload() default true;

    boolean togglePlaceholder() default true;

    boolean noCtrlKey() default true;

    @NotNull
    int size() default 280;

    boolean group() default false;

    @NotNull
    boolean singleOpen() default false;

    String valueSeparator() default ";";

    @NotNull
    Class bindService() default Void.class;

    String caption() default "";

    String optBtn() default "ood-uicmd-opt";

    Class[] customService() default {};

    Class<? extends Enum> enumClass() default Enum.class;

    TreeMenu[] contextMenu() default {};

    TreeMenu[] rowMenu() default {};

    TreeMenu[] customMenu() default {};

    TreeMenu[] bottombarMenu() default {};


    boolean autoIconColor() default true;

    boolean autoItemColor() default false;

    boolean autoFontColor() default false;


    CustomTreeEvent[] event() default {};

    ComponentType[] bindTypes() default {ComponentType.TREEBAR, ComponentType.TREEVIEW, ComponentType.MTREEVIEW};

    @NotNull
    SelModeType selMode() default SelModeType.single;
}
