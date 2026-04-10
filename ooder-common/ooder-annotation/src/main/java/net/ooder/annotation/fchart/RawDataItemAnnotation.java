package net.ooder.annotation.fchart;

import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface RawDataItemAnnotation {
    String label() default "";

    String value() default "";

    String displayvalue() default "";

    String color() default "";

    String link() default "";

    String tooltext() default "";

    boolean showlabel() default true;

    boolean showvalue() default true;

    boolean dashed() default true;

    int alpha() default 1;

    String labelfont() default "";

    String labelfontcolor() default "";

    int labelfontsize() default -1;

    boolean labelfontbold() default true;

    String labelfontitalic() default "";

    String labelbgcolor() default "";

    String labelbordercolor() default "";

    int labelalpha() default 1;

    int labelbgalpha() default 1;

    int labelborderalpha() default 1;

    int labelborderpadding() default 0;

    int labelborderradius() default 0;

    int labelborderthickness() default 0;

    boolean labelborderdashed() default true;

    int labelborderdashlen() default 0;

    int labelborderdashgap() default 0;

    String labellink() default "";

    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    @NotNull
    Class bindService() default Void.class;


}
