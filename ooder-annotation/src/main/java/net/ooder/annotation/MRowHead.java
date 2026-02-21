package net.ooder.annotation;


import net.ooder.annotation.ui.SelModeType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MRowHead {
    @NotNull
    String rowHandlerWidth() default "0em";

    String gridHandlerCaption() default "";

    boolean firstCellEditable() default false;

    @NotNull
    boolean rowNumbered() default false;

    @NotNull
    boolean rowHandler() default false;

    @NotNull
    String headerHeight() default "0em";

    @NotNull
    SelModeType selMode() default SelModeType.none;

}
