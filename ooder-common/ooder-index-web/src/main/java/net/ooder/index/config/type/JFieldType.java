/**
 * $RCSfile: JFieldType.java,v $
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
import net.ooder.index.config.bean.JFieldBean;
import org.apache.lucene.document.Field.Store;

import java.lang.annotation.*;

/**
 * luecene  index 注解
 *
 * @author wenzhang
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ClassMappingAnnotation(clazz = JFieldBean.class)
public @interface JFieldType {
    //索引名称
    String name() default "";

    //唯一标识
    String id() default "";

    boolean highlighter() default false;

    String converter() default "";


    Store store() default Store.NO;

}


