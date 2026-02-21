package net.ooder.annotation;


import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.SelModeType;
import net.ooder.annotation.NotNull;
import javafx.scene.control.TreeItem;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface TreeGridItemAnnotation {

    boolean initFold() default false;

    int index() default 1;

    boolean animCollapse() default false;

    boolean dynDestory() default false;

    boolean dynLoad() default true;

    @NotNull
    boolean lazyLoad() default false;

    @NotNull
    boolean autoReload() default true;

    String imageClass() default "ri-tree-line";

    String valueSeparator() default ";";

    String groupName() default "";

    Class bindService() default Void.class;

    Class<? extends TreeItem> customItems() default TreeItem.class;

    String caption() default "";

    SelModeType selMode() default SelModeType.single;

    ComponentType[] bindTypes() default {ComponentType.TREEBAR, ComponentType.TREEVIEW};
}
