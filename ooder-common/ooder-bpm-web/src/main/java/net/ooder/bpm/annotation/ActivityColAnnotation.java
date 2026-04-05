package net.ooder.bpm.annotation;

import net.ooder.bpm.enums.col.ActivityColType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface ActivityColAnnotation {

    String tempId() default "default";

    ActivityColType activityColType() ;

    Class sourceClass() default Void.class;

}
