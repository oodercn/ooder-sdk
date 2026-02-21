/**
 * $RCSfile: ViewBean.java,v $
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
import net.ooder.annotation.View;
import net.ooder.web.util.AnnotationUtil;

/**
 * 视图Bean类
 * 封装视图注解信息，用于前端视图渲染和数据绑定
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
@AnnotationType(clazz = View.class)
public class ViewBean implements CustomBean {

    String viewInstId;

    String domainId;

    String imageClass;

    Class aggClass;

    Class entityClass;

    public ViewBean() {

    }

    public String getViewInstId() {
        return viewInstId;
    }

    public void setViewInstId(String viewInstId) {
        this.viewInstId = viewInstId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getImageClass() {
        return imageClass;
    }

    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    public Class getAggClass() {
        return aggClass;
    }

    public void setAggClass(Class aggClass) {
        this.aggClass = aggClass;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public ViewBean(View annotation) {
        fillData(annotation);
    }

    public ViewBean fillData(View annotation) {
        return AnnotationUtil.fillBean(annotation, this);
    }

    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }

}
