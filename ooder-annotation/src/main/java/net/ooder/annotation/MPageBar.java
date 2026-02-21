package net.ooder.annotation;

import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MPageBar {


    String uriTpl() default "#*";

    String textTpl() default "*";

    boolean disabled() default false;

    boolean readonly() default false;

    boolean autoTips() default false;

    String parentID() default "";

    String value() default "1:1:1";

    @NotNull
    boolean hiddenBar() default true;

    @NotNull
    String pageCaption() default "分页";

    String prevMark() default "";

    String nextMark() default "";

    @NotNull
    int pageCount() default 15;

    @NotNull
    String height() default "0em";

    @NotNull
    Dock dock() default Dock.bottom;

    @NotNull
    boolean showMoreBtns() default true;

}
