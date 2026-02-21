package net.ooder.annotation;

import net.ooder.annotation.menu.CustomFormMenu;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NavMenuBarAnnotation {

    CustomFormMenu[] customMenu() default {};

    Class[] customService() default {};

}
