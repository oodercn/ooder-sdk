package net.ooder.annotation.event;

import net.ooder.annotation.ui.CustomMenuItem;
import net.ooder.annotation.ui.RequestPathEnum;
import net.ooder.annotation.ui.ResponsePathEnum;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface BindMenuItem {

    CustomMenuItem[] bindItems() default {};

    RequestPathEnum[] requestParams() default {};

    ResponsePathEnum[] responsePath() default {};

    public CustomFieldEvent[] bindFieldEvent() default {};

    public CustomGalleryEvent[] bindGalleryEvent() default {};

    public CustomTitleBlockEvent[] bindTitleBlockEvent() default {};

    public CustomTreeGridEvent[] bindTreeGridEvent() default {};

    public CustomTreeEvent[] bindTreeEvent() default {};

    public CustomFormEvent[] bindFormEvent() default {};

    public CustomTabsEvent[] bindTabsEvent() default {};

    public CustomHotKeyEvent[] bindHotKeyEvent() default {};

}
