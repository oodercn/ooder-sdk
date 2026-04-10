package net.ooder.annotation.fchart;


import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartAnnGroupAnnotation {
    String id() default "";

    int x() default -1;

    int y() default -1;

    boolean showbelow() default true;

    boolean autoscale() default true;

    boolean constrainscale() default true;

    boolean scaletext() default true;

    boolean scaleimages() default true;

    int xshift() default -1;

    int yshift() default -1;


    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    Class bindService() default Void.class;

}
