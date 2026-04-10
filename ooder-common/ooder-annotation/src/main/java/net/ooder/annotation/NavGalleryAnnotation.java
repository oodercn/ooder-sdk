package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.ui.UIPositionType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NavGalleryAnnotation {

    String bgimg() default "";

    String imageClass() default "";

    @NotNull
    int columns() default 5;

    int rows() default 1;

    boolean autoImgSize() default false;

    boolean autoItemSize() default false;

    boolean iconOnly() default false;

    @NotNull
    String iconFontSize() default "4em";

    UIPositionType position() default UIPositionType.RELATIVE;

    @NotNull
    BorderType borderType() default BorderType.none;

    @NotNull
    String height() default "7em";

    String itemMargin() default "";

    String itemPadding() default "";

    @NotNull
    String itemWidth() default "5em";

    String itemHeight() default "-1";

    String imgWidth() default "-1";

    String imgHeight() default "-1";

    @NotNull
    Dock dock() default Dock.left;

}
