package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.LABEL)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface LabelAnnotation {

    String id() default "";

    boolean selectable() default true;

    String clock() default "hh : mm : ss";

    String image() default "";

    ImagePos imagePos() default ImagePos.center;

    String imageBgSize() default "";

    String imageClass() default "";

    String iconFontCode() default "";

    HAlignType hAlign() default HAlignType.right;

    VAlignType vAlign() default VAlignType.middle;

    String fontColor() default "";

    String fontSize() default "";

    String fontWeight() default "";

    String fontFamily() default "";

    UIPositionType position() default UIPositionType.STATIC;

    boolean isFormField() default true;

    String excelCellFormula() default "";


}
