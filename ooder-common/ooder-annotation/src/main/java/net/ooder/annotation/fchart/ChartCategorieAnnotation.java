package net.ooder.annotation.fchart;

import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartCategorieAnnotation {

    boolean showlabel() default true;

    String tooltext() default "";

    String font() default "";

    String fontcolor() default "";

    boolean fontbold() default true;

    boolean fontitalic() default true;

    String bgcolor() default "";

    String bordercolor() default "";

    int alpha() default -1;

    int bgalpha() default -1;

    int borderalpha() default -1;

    int borderpadding() default -1;

    int borderradius() default -1;

    int borderthickness() default -1;

    boolean borderdashed() default true;

    int borderdashLen() default -1;

    int borderdashgap() default -1;

    String link() default "";


    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    @NotNull
    Class bindService() default Void.class;


}
