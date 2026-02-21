/**
 * $RCSfile: EntityBean.java,v $
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
package net.ooder.web;

import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.annotation.ESDEntity;
import net.ooder.web.util.AnnotationUtil;

@AnnotationType(clazz = ESDEntity.class)
public class EntityBean implements CustomBean {
    Class sourceClass;

    Class serviceClass;

    Class rootClass;


    public EntityBean() {

    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Class getRootClass() {
        return rootClass;
    }

    public void setRootClass(Class rootClass) {
        this.rootClass = rootClass;
    }

    public EntityBean(ESDEntity annotation) {
        fillData(annotation);
    }

    public EntityBean fillData(ESDEntity annotation) {
        return AnnotationUtil.fillBean(annotation, this);
    }

    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }

}
