/**
 * $RCSfile: AggregationBean.java,v $
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

import net.ooder.annotation.*;
import net.ooder.annotation.CustomAction;
import net.ooder.web.util.AnnotationUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@AnnotationType(clazz = Aggregation.class)
public class AggregationBean implements CustomBean {


    String domainId;

    String moduleName;

    AggregationType type;

    Set<UserSpace> userSpace;

    Set<Class<? extends CustomAction>> implActions;

    Class entityClass;

    Class sourceClass;

    Class rootClass;

    public AggregationBean() {

    }

    public AggregationBean(Aggregation annotation) {
        fillData(annotation);
    }

    public void addUserSpace(UserSpace... spaces) {
        if (userSpace == null) {
            userSpace = new HashSet<UserSpace>();
        }
        userSpace.addAll(Arrays.asList(spaces));
    }

    public Set<Class<? extends CustomAction>> getImplActions() {
        return implActions;
    }

    public void setImplActions(Set<Class<? extends CustomAction>> implActions) {
        this.implActions = implActions;
    }

    public Set<UserSpace> getUserSpace() {
        return userSpace;
    }

    public void setUserSpace(Set<UserSpace> userSpace) {
        this.userSpace = userSpace;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public AggregationType getType() {
        return type;
    }

    public void setType(AggregationType type) {
        this.type = type;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Class getRootClass() {
        return rootClass;
    }

    public void setRootClass(Class rootClass) {
        this.rootClass = rootClass;
    }


    public AggregationBean fillData(Aggregation annotation) {
        return AnnotationUtil.fillBean(annotation, this);
    }

    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }

}
