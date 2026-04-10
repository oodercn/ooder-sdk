package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.event.CustomFieldEvent;
import net.ooder.annotation.event.CustomHotKeyEvent;
import net.ooder.annotation.event.FieldEvent;
import net.ooder.annotation.event.FieldHotKeyEvent;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.HAlignType;
import net.ooder.annotation.ui.PositionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FieldAnnotation {

    boolean required() default false;

    boolean dirtyMark() default false;

    boolean dynCheck() default false;

    boolean removed() default false;

    int rawRow() default 0;

    int rawColSpan() default 1;

    int rawRowSpan() default 1;

    int manualHeight() default 25;

    int manualWidth() default 150;

    int rawCol() default 0;

    @NotNull
    int colSpan() default 1;

    String expression() default "";

    @NotNull
    int rowSpan() default 1;

    int tabindex() default 0;

    PositionType innerPosition() default PositionType.inner;

    HAlignType textAlign() default HAlignType.center;

    @NotNull
    boolean serialize() default true;

    @NotNull
    boolean haslabel() default true;

    ComponentType componentType() default ComponentType.INPUT;

    Class serviceClass() default Void.class;

    @NotNull
    Class customContextMenuService() default Void.class;

    CustomFieldEvent[] customFieldEvent() default {};

    CustomHotKeyEvent[] customHotKeyEvent() default {};

    FieldEvent[] event() default {};

    FieldHotKeyEvent[] hotKeyEvent() default {};

    ComponentType[] bindTypes() default {ComponentType.INPUT, ComponentType.COMBOINPUT, ComponentType.CHECKBOX, ComponentType.CODEEDITOR, ComponentType.RICHEDITOR, ComponentType.RADIOBOX};


}
