package net.ooder.annotation.view;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface EChartViewAnnotation {


    String JSONUrl() default "";

    String XMLUrl() default "";

    String categoriesUrl() default "";

    String datasetUrl() default "";

    String dataUrl() default "";

    String trendlinesUrl() default "";


}
