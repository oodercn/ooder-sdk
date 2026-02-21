package net.ooder.annotation;

import net.ooder.annotation.menu.TreeGridRowMenu;
import net.ooder.annotation.ui.CmdButtonType;
import net.ooder.annotation.ui.CmdTPosType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.TagCmdsAlign;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface GroupCmd {
    @NotNull
    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.left;

    @NotNull
    CmdButtonType buttonType() default CmdButtonType.text;

    TreeGridRowMenu[] rowMenu() default {};

    @NotNull
    CmdTPosType pos() default CmdTPosType.row;

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

    Class[] menuClass() default Void.class;

    ComponentType[] bindTypes() default {ComponentType.TREEGRID};


}
