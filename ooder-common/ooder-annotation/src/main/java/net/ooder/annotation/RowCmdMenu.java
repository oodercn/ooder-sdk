package net.ooder.annotation;

import net.ooder.annotation.ui.CmdButtonType;
import net.ooder.annotation.ui.CmdTPosType;
import net.ooder.annotation.ui.TagCmdsAlign;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface RowCmdMenu {
    @NotNull
    String id() default "";

    @NotNull
    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.left;

    @NotNull
    CmdButtonType buttonType() default CmdButtonType.button;

    @NotNull
    CmdTPosType pos() default CmdTPosType.row;

    String caption() default "";

    String itemClass() default "";

    String itemStyle() default "";

    String tips() default "";

    boolean disabled() default false;

    @NotNull
    boolean lazy() default false;

    @NotNull
    boolean dynLoad() default false;

    Class[] menuClass() default {};

}
