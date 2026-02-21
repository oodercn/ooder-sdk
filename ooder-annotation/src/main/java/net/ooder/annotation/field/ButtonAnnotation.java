package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.BUTTON)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ButtonAnnotation {


    String id() default "";

    String html() default "";

    String image() default "";

    String value() default "";

    ImagePos imagePos() default ImagePos.center;

    String imageBgSize() default "";

    String iconFontCode() default "";

    HAlignType hAlign() default HAlignType.center;

    VAlignType vAlign() default VAlignType.top;

    String fontColor() default "";

    String fontSize() default "";


    String fontWeight() default "";

    String fontFamily() default "";

    ButtonType buttonType() default ButtonType.normal;


}
