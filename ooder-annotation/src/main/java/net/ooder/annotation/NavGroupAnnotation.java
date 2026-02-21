package net.ooder.annotation;

import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NavGroupAnnotation {


    String expression() default "";

    @NotNull
    Dock dock() default Dock.none;

    boolean noFrame() default false;

    @NotNull
    boolean toggleBtn() default true;

    CustomFormMenu[] customMenu() default {};

    CustomFormMenu[] bottombarMenu() default {};

    CustomFormEvent[] event() default {};

    ComponentType[] bindTypes() default {ComponentType.FORMLAYOUT, ComponentType.TREEVIEW, ComponentType.PANEL, ComponentType.GROUP, ComponentType.DIALOG};

    Class[] customService() default {};


}
