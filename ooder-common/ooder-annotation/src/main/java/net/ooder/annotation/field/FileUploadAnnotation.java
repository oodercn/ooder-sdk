package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.FILEUPLOAD)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface FileUploadAnnotation {

    String id() default "";

    @NotNull
    Dock dock() default Dock.fill;

    boolean selectable() default true;

    @NotNull
    String width() default "30.0em";

    @NotNull
    String height() default "30.0em";

    @NotNull
    String src() default "/plugins/fileupload/uploadgrid.html";

    Class bindClass() default Void.class;

    boolean prepareFormData() default true;

    String uploadUrl() default "";


}
