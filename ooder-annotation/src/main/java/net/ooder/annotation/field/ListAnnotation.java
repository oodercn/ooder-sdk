package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, componentType = ComponentType.LIST)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})

public @interface ListAnnotation {


    String id() default "";

    @NotNull
    SelModeType selMode() default SelModeType.single;

    @NotNull
    BorderType borderType() default BorderType.flat;

    boolean noCtrlKey() default true;

    @NotNull
    String width() default "auto";

    @NotNull
    String height() default "15em";

    @NotNull
    Dock dock() default Dock.none;

    int maxHeight() default 420;

    ItemRow itemRow() default ItemRow.none;

    String optBtn() default "";

    @NotNull
    String tagCmds() default "[]";

    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.right;

    String labelCaption() default "";



}
