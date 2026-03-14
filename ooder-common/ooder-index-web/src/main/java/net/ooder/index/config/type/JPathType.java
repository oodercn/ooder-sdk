/**
 * $RCSfile: JPathType.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.index.config.type;

import net.ooder.annotation.ClassMappingAnnotation;
import net.ooder.index.config.bean.JPathBean;

import java.lang.annotation.*;

/**
 * luecene  index 注解
 * @author wenzhang
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ClassMappingAnnotation(clazz=JPathBean.class)
public @interface JPathType{
    
        //动态表达式
        String expression() default "";
        
        //决对路径
        String absolutePath() default "";
        
	//相对路径
        String path() default "";	
	
	//作用类型
	PathElementType type() default PathElementType.ALL;	
	
}


