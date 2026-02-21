package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.SLIDER)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})

public @interface SliderAnnotation {

    String id() default "";

    String width() default "15.0em";

    String height() default "4.0em";

    int precision() default 1;

    String numberTpl() default "* - 1% ~ 2%";

    @NotNull
    int steps() default 1;


    String value() default "1:20";

    LayoutType layoutType() default LayoutType.horizontal;

    boolean isRange() default true;

    boolean showIncreaseHandle() default true;

    boolean showDecreaseHandle() default true;

    String labelSize() default "4";

    LabelPos labelPos() default LabelPos.left;

    String labelGap() default "4.0em";

    String labelCaption() default "";

    HAlignType labelHAlign() default HAlignType.right;

    VAlignType labelVAlign() default VAlignType.top;


}
