package net.ooder.annotation;

import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PanelFormAnnotation {

    Dock dock() default Dock.none;

    String caption() default "";

    String html() default "";

    String image() default "";

    ImagePos imagePos() default ImagePos.center;

    String imageBgSize() default "";

    String imageClass() default "";

    String iconFontCode() default "";

    BorderType borderType() default BorderType.inset;

    boolean noFrame() default false;

    HAlignType hAlign() default HAlignType.left;

    ToggleIconType toggleIcon() default ToggleIconType.taggle;

    boolean toggle() default true;


    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
