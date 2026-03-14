/**
 * $RCSfile: FSDirectoryType.java,v $
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
import net.ooder.index.config.bean.JFSDirectoryBean;
import org.apache.lucene.store.SimpleFSDirectory;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ClassMappingAnnotation(clazz=JFSDirectoryBean.class)
public @interface FSDirectoryType {
    
    

    Class fsDirectoryClass() default SimpleFSDirectory.class;
    

    SyncListenerType syncListener() default @SyncListenerType;
   
    String id() default "";
    
    // VFS 云端VFS对应路径
    String vfsPath() default "/index/";
    
    //延时执行时间
    long syncDelayTime() default 15000;
   
    

    int maxTaskSize() default 1;


    // 临时文件路径
    String tempPath() default "/index/temp";

    //相对路径
    String path() default "/index/";
 

}


