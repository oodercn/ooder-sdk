package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Chart3DFunctionalAnnotation {
    boolean is2d() default true;

    boolean clustered() default true;

    String chartorder() default "";

    boolean chartontop() default true;

    boolean autoscaling() default true;

    boolean allowscaling() default true;

    int startangx() default -1;

    int startangy() default -1;

    int endangx() default -1;

    int endangy() default -1;

    int cameraangx() default -1;

    int cameraangy() default -1;

    int lightangx() default -1;

    int lightangy() default -1;

    int intensity() default -1;

    boolean dynamicshading() default true;

    boolean bright2d() default true;

    boolean allowrotation() default true;

    boolean constrainverticalrotation() default true;

    int minverticalrotangle() default -1;

    int maxverticalrotangle() default -1;

    boolean constrainhorizontalrotation() default true;

    int minhorizontalrotangle() default -1;

    int maxhorizontalrotangle() default -1;

    boolean showplotborder() default true;

    int zdepth() default -1;
}
