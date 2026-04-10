package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.SelModeType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RowHead {
    @NotNull
    String rowHandlerWidth() default "5.0em";

    String gridHandlerCaption() default "";

    boolean firstCellEditable() default false;

    @NotNull
    boolean rowNumbered() default true;

    @NotNull
    boolean rowHandler() default true;

    @NotNull
    String headerHeight() default "2.0em";

    @NotNull
    SelModeType selMode() default SelModeType.multibycheckbox;

}
