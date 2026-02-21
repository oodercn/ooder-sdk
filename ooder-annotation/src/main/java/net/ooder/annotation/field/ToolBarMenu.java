package net.ooder.annotation.field;


import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.CustomMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.TOOLBAR)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ToolBarMenu {


    HAlignType hAlign() default HAlignType.left;

    VAlignType vAlign() default VAlignType.middle;

    PositionType position() default PositionType.module;

    @NotNull
    String iconFontSize() default "1.5em";

    String groupId() default "";

    String id() default "";

    Dock dock() default Dock.top;

    CustomMenu[] menus() default {};

    boolean autoIconColor() default true;

    boolean autoItemColor() default false;

    boolean autoFontColor() default false;

    boolean showCaption() default true;

    boolean disabled() default false;

    boolean lazy() default false;

    @NotNull
    boolean dynLoad() default false;

    Class[] menuClasses() default {};

    @NotNull
    public boolean handler() default false;

    public boolean formField() default true;

    Class serviceClass() default Void.class;

    public Class bindService() default Void.class;


}
