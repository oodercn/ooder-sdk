package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@CustomClass( viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.BLOCKCONFIG, componentType = ComponentType.BLOCK)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface BlockFieldAnnotation {

    @NotNull
    BorderType borderType() default BorderType.none;

    boolean resizer() default false;

    @NotNull
    Dock dock() default Dock.none;

    String sideBarCaption() default "";

    String sideBarType() default "";

    SideBarStatusType sideBarStatus() default SideBarStatusType.expand;

    String sideBarSize() default "";

    String background() default "";

}
