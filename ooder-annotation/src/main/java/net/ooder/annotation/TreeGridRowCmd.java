package net.ooder.annotation;

import net.ooder.annotation.action.ActiveModeType;
import net.ooder.annotation.action.EditModeType;
import net.ooder.annotation.action.HotRowModeType;
import net.ooder.annotation.menu.TreeGridRowMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface TreeGridRowCmd {
    @NotNull
    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.right;

    TreeGridRowMenu[] rowMenu() default {};

    @NotNull
    CmdButtonType buttonType() default CmdButtonType.text;

    HotRowModeType hotRowMode() default HotRowModeType.show;

    @NotNull
    EditModeType editMode() default EditModeType.inline;


    ActiveModeType activeMode() default ActiveModeType.row;


    TreeModeType treeMode() default TreeModeType.inhandler;

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
