package net.ooder.annotation.fchart;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ChartEffectAnnotation {

    boolean showhovereffect() default true;

    boolean plothovereffect() default true;

    String plotfillhovercolor() default "";

    int plotfillhoveralpha() default 0;


}
