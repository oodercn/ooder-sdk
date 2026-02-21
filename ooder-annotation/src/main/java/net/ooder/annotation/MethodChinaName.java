package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface MethodChinaName {


    String value() default "";

    public String cname() default "无";

    public String imageClass() default "";

    public int logLevel() default 0;

    public String returnStr() default "";

    public boolean display() default true;
}
