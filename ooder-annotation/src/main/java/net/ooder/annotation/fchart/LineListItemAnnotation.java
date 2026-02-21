package net.ooder.annotation.fchart;

import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.VAlignType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface LineListItemAnnotation {
    int startvalue() default -1;

    String color() default "";

    String displayvalue() default "";

    boolean showontop() default true;

    ValuePosition parentyaxis() default ValuePosition.NONE;

    int endvalue() default -1;

    boolean istrendzone() default true;

    int thickness() default -1;

    int alpha() default -1;

    int dashed() default -1;

    int dashlen() default -1;

    int dashgap() default -1;

    boolean valueonright() default true;

    String tooltext() default "";

    //VLine
    int lineposition() default -1;

    boolean showlabelborder() default true;

    String label() default "";

    int labelposition() default -1;

    HAlignType labelhalign() default HAlignType.center;

    VAlignType labelvalign() default VAlignType.middle;

    int startindex() default -1;

    int endindex() default -1;

    boolean displayalways() default true;

    int displaywhencount() default -1;

    boolean valueontop() default true;

    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    @NotNull
    Class bindService() default Void.class;


}
