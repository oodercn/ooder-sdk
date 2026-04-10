package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFlex;
import net.ooder.annotation.ui.css.CSLayout;

import java.lang.annotation.*;

/**
 * TreeGrid组件CSS样式注解
 * 
 * TreeGrid组件包含多种虚拟DOM类型：
 * - LIST: 列表容器
 * - HEADER: 表头区域
 * - ROW: 表格行
 * - CELL: 单元格
 * - SELECTED: 选中行
 * - HOVER: 悬停行
 * - ODD/EVEN: 奇偶行
 * 
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TreeGridStyle {
    
    CSLayout list() default @CSLayout;
    
    CSBorder header() default @CSBorder;
    
    CSBorder row() default @CSBorder;
    
    CSBorder cell() default @CSBorder;
    
    CSBorder selected() default @CSBorder;
    
    CSBorder hover() default @CSBorder;
    
    CSBorder odd() default @CSBorder;
    
    CSBorder even() default @CSBorder;
    
    CSBorder focus() default @CSBorder;
    
    CSLayout scroll() default @CSLayout;
    
    CSFlex flex() default @CSFlex;
}
