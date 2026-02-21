package net.ooder.annotation;

import net.ooder.annotation.menu.CustomMenuType;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.MENUBAR)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MenuBarMenu {
    @NotNull
    CustomMenuType menuType() default CustomMenuType.MENUBAR;

    PositionType position() default PositionType.module;

    String caption() default "";

    String id() default "";

    String parentId() default "";

    String domainId() default "default";

    Dock dock() default Dock.top;

    CustomMenu[] menus() default {};

    @NotNull
    HAlignType hAlign() default HAlignType.left;

    VAlignType vAlign() default VAlignType.top;

    @NotNull
    String top() default "";

    String left() default "";

    Class serviceClass() default Void.class;

    Class[] menuClasses() default {};

    String imageClass() default "";


    boolean autoIconColor() default true;

    boolean autoItemColor() default false;

    boolean autoFontColor() default false;


    @NotNull
    int index() default 100;


    int autoShowTime() default 50;

    @NotNull
    boolean showCaption() default true;

    boolean handler() default false;

    @NotNull
    boolean lazy() default false;

    @NotNull
    boolean dynLoad() default false;


}
