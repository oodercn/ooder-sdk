package net.ooder.annotation.fchart;

import net.ooder.annotation.NotNull;
import org.omg.CORBA.portable.ValueOutputStream;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartAnnAnnotation {
    boolean autoscale() default true;

    boolean constrainscale() default true;

    boolean scaletext() default true;

    boolean scaleimages() default true;

    int xshift() default -1;

    int yshift() default -1;

    int grpyshift() default -1;

    int origw() default -1;

    int origh() default -1;

    int grpxshift() default -1;


    @NotNull
    boolean autoReload() default true;

    Class customItems() default Void.class;

    @NotNull
    Class bindService() default Void.class;


}
