package net.ooder.bpm.annotation;

import net.ooder.bpm.enums.col.ProcessColType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface ProcessColAnnotation {

    String tempId() default "default";

    ProcessColType processColType() ;

    Class sourceClass() default Void.class;


}
