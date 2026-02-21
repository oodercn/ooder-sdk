package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;

import java.lang.annotation.*;

@Inherited
@CustomClass( moduleType = ModuleViewType.GALLERYCONFIG, viewType = CustomViewType.COMPONENT, componentType = ComponentType.GALLERY)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface GalleryFieldAnnotation {

    String bgimg() default "";

    @NotNull
    Class<? extends Enum> enumClass() default Enum.class;

    String backgroundColor() default "transparent";


    Class[] customService() default {};

    @NotNull
    BorderType borderType() default BorderType.none;


}
