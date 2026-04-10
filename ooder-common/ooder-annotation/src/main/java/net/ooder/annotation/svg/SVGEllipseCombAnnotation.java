package net.ooder.annotation.svg;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ComponentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.SVGELLIPSECOMB)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SVGEllipseCombAnnotation {
    String svgTag() default "FlowChart:OnPageRefrence";
}
