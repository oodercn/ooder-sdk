package net.ooder.config.scene.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumDocument {
    
    String name();
    
    String description();
    
    String installGuide() default "";
    
    String startupGuide() default "";
    
    String configExample() default "";
    
    String version() default "1.0.0";
    
    String author() default "";
}
