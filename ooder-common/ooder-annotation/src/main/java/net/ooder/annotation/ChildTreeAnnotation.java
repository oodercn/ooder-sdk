package net.ooder.annotation;

import net.ooder.annotation.ui.*;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.event.CustomTreeEvent;
import net.ooder.annotation.menu.TreeMenu;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface ChildTreeAnnotation {

    boolean formField() default true;

    boolean initFold() default false;

    int index() default 1;

    boolean animCollapse() default false;

    boolean dynDestory() default false;

    boolean dynLoad() default true;

    @NotNull
    boolean lazyLoad() default false;

    @NotNull
    boolean deepSearch() default false;

    @NotNull
    boolean autoHidden() default true;

    @NotNull
    boolean canEditor() default true;

    boolean togglePlaceholder() default true;

    boolean group() default false;

    String imageClass() default "ri-tree-line";

    String valueSeparator() default ";";

    String groupName() default "";


    String image() default "";

    boolean popBtn() default false;

    BorderType borderType() default BorderType.none;

    boolean activeLast() default true;

    ComboInputType type() default ComboInputType.none;

    String bindClassName() default "";


    boolean closeBtn() default false;

    @NotNull
    boolean singleOpen() default false;

    Class[] bindClass() default {};

    Class<? extends TreeItem> customItems() default TreeItem.class;

    String caption() default "";


    boolean autoIconColor() default true;

    boolean autoItemColor() default false;

    boolean autoFontColor() default false;

    IconColorEnum iconColor() default IconColorEnum.NONE;

    ItemColorEnum itemColor() default ItemColorEnum.NONE;

    FontColorEnum fontColor() default FontColorEnum.NONE;



    CustomFormMenu[] bottombarMenu() default {};

    CustomTreeEvent[] event() default {};

    TreeMenu[] contextMenu() default {};

    SelModeType selMode() default SelModeType.single;

    ComponentType[] bindTypes() default {ComponentType.TREEBAR, ComponentType.TREEVIEW};
}
