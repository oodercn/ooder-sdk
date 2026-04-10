package net.ooder.annotation;

import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ContainerAnnotation {
    String panelBgClr() default "";

    String panelBgImg() default "";

    String panelBgImgPos() default "";

    AttachmentType panelBgImgAttachment() default AttachmentType.none;

    int conLayoutColumns() default 1;

    boolean conDockRelative() default false;

    ThemesType sandboxTheme() default ThemesType.webflat;

    String formMethod() default "";

    String formTarget() default "";

    String formDataPath() default "";

    String formAction() default "";

    String formEnctype() default "";

    String className() default "";

    String dropKeys() default "";

    String dragKey() default "";

    String iframeAutoLoad() default "";

    String ajaxAutoLoad() default "";

    String html() default "";

    OverflowType overflow() default OverflowType.auto;

    String panelBgImgRepeat() default "";

    String set() default "";

    boolean selectable() default true;

    String autoTips() default "";

    SpaceUnitType spaceUnit() default SpaceUnitType.em;

    HoverPopType hoverPopType() default HoverPopType.inner;

    String hoverPop() default "";

    String showEffects() default "";

    String hideEffects() default "";

    int rotate() default -1;

    CustomAnimType activeAnim() default CustomAnimType.none;


}
