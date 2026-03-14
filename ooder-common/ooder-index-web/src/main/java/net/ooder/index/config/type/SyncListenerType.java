/**
 * $RCSfile: SyncListenerType.java,v $
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
import net.ooder.index.config.bean.JSyncListenerBean;
import net.ooder.index.listener.FileSyncListener;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ClassMappingAnnotation(clazz=JSyncListenerBean.class)
public @interface SyncListenerType {

    // VFS 云端VFS对应路径
    String vfsPath() default "";

    // 临时文件路径
    String tempPath() default "";
    
    
    // VFS 云端VFS对应路径
    String vfsRootPath() default "";

    // 临时文件路径
    String tempRootPath() default "";


    Class syncListener() default FileSyncListener.class;
    
    

}


