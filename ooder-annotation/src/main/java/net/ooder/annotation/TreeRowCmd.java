package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.menu.TreeRowMenu;
import net.ooder.annotation.ui.CmdButtonType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.TagCmdsAlign;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface TreeRowCmd {
    @NotNull
    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.floatright;

    @NotNull
    CmdButtonType buttonType() default CmdButtonType.text;

    TreeRowMenu[] rowMenu() default {};

    @NotNull
    String id() default "";

    String caption() default "";

    String itemStyle() default "";

    String tips() default "";

    boolean disabled() default false;

    @NotNull
    boolean lazy() default false;

    boolean showCaption() default false;

    @NotNull
    boolean dynLoad() default false;

    @NotNull
    Class[] menuClass() default Void.class;

    ComponentType[] bindTypes() default {ComponentType.TREEBAR, ComponentType.TREEVIEW};


}
