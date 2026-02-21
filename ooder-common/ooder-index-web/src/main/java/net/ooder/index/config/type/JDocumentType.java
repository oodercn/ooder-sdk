/**
 * $RCSfile: JDocumentType.java,v $
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
import net.ooder.index.config.bean.JDocumentBean;

import java.lang.annotation.*;

/**
 * luecene  index 注解
 * @author wenzhang
 *
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ClassMappingAnnotation(clazz=JDocumentBean.class)
public @interface JDocumentType{
    
        String id() default "";
    
        //索引名称
        String name() default "";
        
        boolean vfsValid() default true;
        
        boolean indexValid() default true;;
	

	FSDirectoryType fsDirectory() default @FSDirectoryType;
	
	//索引配置
	JIndexWriterType indexWriter() default @JIndexWriterType;
	
	//配置
	VFSJsonType vfsJson() default @VFSJsonType;
	
	
}


