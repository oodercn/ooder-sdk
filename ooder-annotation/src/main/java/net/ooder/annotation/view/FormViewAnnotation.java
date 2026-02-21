package net.ooder.annotation.view;

import net.ooder.annotation.event.BindMenuItem;
import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.CustomMenuItem;
import net.ooder.annotation.ui.RequestPathEnum;
import net.ooder.annotation.ui.ResponsePathEnum;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FormViewAnnotation {

    String expression() default "";


    @BindMenuItem(bindItems = CustomMenuItem.SAVE, requestParams = {RequestPathEnum.CURRFORM, RequestPathEnum.CTX})
    String saveUrl() default "";

    @BindMenuItem(bindFormEvent = CustomFormEvent.RESET, requestParams = {RequestPathEnum.CURRFORM, RequestPathEnum.CTX})
    String reSetUrl() default "";

    @BindMenuItem(bindItems = CustomMenuItem.DATAURL,
            requestParams = {RequestPathEnum.CURRFORM, RequestPathEnum.CTX},
            responsePath = {ResponsePathEnum.FORM, ResponsePathEnum.CTX})
    String dataUrl() default "";

    @BindMenuItem(bindItems = CustomMenuItem.SEARCH,
            requestParams = {RequestPathEnum.CURRFORM, RequestPathEnum.CTX}
    )
    String searchUrl() default "";

    String caption() default "";

    boolean autoSave() default false;


    ;


}
