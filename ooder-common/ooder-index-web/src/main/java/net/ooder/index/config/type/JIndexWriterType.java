/**
 * $RCSfile: JIndexWriterType.java,v $
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
import net.ooder.index.config.bean.JIndexWriterBean;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ClassMappingAnnotation(clazz=JIndexWriterBean.class)
public @interface JIndexWriterType {
    
    String id() default "";
    
    //索引名称
   IndexWriterConfig.OpenMode openMode() default IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
    
    Class analyzer() default StandardAnalyzer.class;
    
   
	

}


