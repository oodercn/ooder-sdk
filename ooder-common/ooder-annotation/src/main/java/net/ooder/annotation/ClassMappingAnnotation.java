package net.ooder.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ClassMappingAnnotation {
	Class clazz() ;
}
