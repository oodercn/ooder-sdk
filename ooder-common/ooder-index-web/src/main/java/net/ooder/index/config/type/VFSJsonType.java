/**
 * $RCSfile: VFSJsonType.java,v $
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
import net.ooder.index.config.bean.VFSJsonBean;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ClassMappingAnnotation(clazz = VFSJsonBean.class)
public @interface VFSJsonType {

    String fileName() default "index.json";

    String id() default "";

    // VFS 云端VFS对应路径
    String vfsPath() default "json/";

    //相对路径
    String path() default "data/";


}


