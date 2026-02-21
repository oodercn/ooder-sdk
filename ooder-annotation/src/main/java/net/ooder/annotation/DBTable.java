package net.ooder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DBTable {
    String tableName();

    String cname() default "无";

    String configKey() default "console";

    String url() default "console";

    String titleKey() default "";

    String primaryKey();
}
