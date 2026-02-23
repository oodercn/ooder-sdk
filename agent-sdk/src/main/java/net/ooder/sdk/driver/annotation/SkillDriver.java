package net.ooder.sdk.driver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkillDriver {
    
    String interfaceId();
    
    String skillId() default "";
    
    int priority() default 0;
    
    boolean singleton() default true;
    
    boolean fallback() default false;
    
    String description() default "";
}
