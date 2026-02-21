package net.ooder.annotation;


import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.LabelPos;
import net.ooder.annotation.ui.VAlignType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Label {

    String labelCaption() default "";

    @NotNull
    String labelSize() default "6.0em";

    String labelGap() default "";

    @NotNull
    int manualWidth() default 150;

    @NotNull
    LabelPos labelPos() default LabelPos.left;


    HAlignType labelHAlign() default HAlignType.right;


    VAlignType labelVAlign() default VAlignType.top;


}
