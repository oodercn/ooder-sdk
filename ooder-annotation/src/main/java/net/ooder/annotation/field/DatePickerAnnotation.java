package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.DATEPICKER)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface DatePickerAnnotation {
    String id() default "";

    boolean timeInput() default false;

    String value() default "";

    boolean closeBtn() default false;

    int firstDayOfWeek() default 0;

    String offDays() default "60";

    boolean hideWeekLabels() default false;

    String dateInputFormat() default "yyyy-mm-dd";// listbox:["yyyy-mm-dd","mm-dd-yyyy","dd-mm-yyyy"],


}
