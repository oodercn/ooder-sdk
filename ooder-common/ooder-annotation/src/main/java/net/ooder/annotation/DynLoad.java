package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface DynLoad {
    
    String viewInstId() default "";
    
    Class sourceClass() default Void.class;
    
    Class rootClass() default Void.class;

}
