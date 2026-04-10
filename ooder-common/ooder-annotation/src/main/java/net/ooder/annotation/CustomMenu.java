package net.ooder.annotation;



import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface CustomMenu {

    String type();

    String caption();

    IconColorEnum iconColor() default IconColorEnum.YELLOW;

    ItemColorEnum itemColor() default ItemColorEnum.YELLOW;

    FontColorEnum fontColor() default FontColorEnum.YELLOW;

    TagCmdsAlign tagCmdsAlign() default TagCmdsAlign.floatright;

    ComboInputType itemType() default ComboInputType.button;

    String imageClass() default "";

    String expression() default "";

    CustomAction[] actions() default {};


}
