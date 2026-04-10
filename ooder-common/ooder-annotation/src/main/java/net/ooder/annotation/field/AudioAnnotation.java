package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.PreloadType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType=ComponentType.AUDIO)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AudioAnnotation {
    boolean selectable() default true;

    String width() default "18.0em";

    String height() default "5.0em";

    String src() default "media";

    boolean cover() default false;

    boolean controls() default true;

    PreloadType preload() default PreloadType.none;

    boolean loop() default false;

    boolean muted() default false;

    int volume() default 1;

    boolean autoplay() default false;

}
