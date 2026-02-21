package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.LabelPos;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.TEXTEDITOR)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TextEditorAnnotation {
    boolean dynLoad() default false;

    boolean selectable() default true;

    boolean enableBar() default false;

    String value() default "";

    String width() default "32em";

    String height() default "25em";

    String frameStyle() default "";

    @NotNull
    String cmdList() default "none";

    String cmdFilter() default "";

    String codeType() default "js";

    String labelGap() default "4em";

    LabelPos labelPos() default LabelPos.none;

}
