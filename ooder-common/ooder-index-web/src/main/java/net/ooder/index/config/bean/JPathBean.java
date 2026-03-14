/**
 * $RCSfile: JPathBean.java,v $
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
package net.ooder.index.config.bean;

import net.ooder.index.config.type.PathElementType;

public class JPathBean implements JPath {

    String expression;

    String absolutePath;

    String path;

    Class clazz;

    String id;

    // 索引属性
    PathElementType type;

    @Override
    public String getExpression() {
	return expression;
    }

    public void setExpression(String expression) {
	this.expression = expression;
    }

    @Override
    public String getAbsolutePath() {
	return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
	this.absolutePath = absolutePath;
    }

    @Override
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public void setId(String id) {
	this.id = id;
    }

    @Override
    public PathElementType getType() {
	return type;
    }

    public void setType(PathElementType type) {
	this.type = type;
    }

    @Override
    public void setClazz(Class clazz) {
	this.clazz = clazz;
    }

    @Override
    public Class getClazz() {
	return clazz;
    }

}


