/**
 * $RCSfile: ExcuteObj.java,v $
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
package net.ooder.jds.core.esb.task;

import net.ooder.context.JDSActionContext;

import java.util.Map;

public class ExcuteObj<T, K> {

    Class<T> clazz;
    String expression;
    Map<String, Object> context;
    K source;
    T obj;

    public ExcuteObj(String expression) {
        this.expression = expression;
    }

    public ExcuteObj(String expression, Class<T> clazz, K source) {

        this.clazz = clazz;
        this.expression = expression;
        this.context = JDSActionContext.getActionContext().getContext();
        this.source = source;
        this.obj = obj;
    }


    public ExcuteObj(String expression, Class<T> clazz, Map<String, Object> context, K source) {

        this.clazz = clazz;
        this.expression = expression;
        this.context = context;
        this.source = source;
    }

    public Map getContext() {
        return context;
    }

    public void setContext(Map context) {
        this.context = context;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public K getSource() {
        return source;
    }

    public void setSource(K source) {
        this.source = source;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }


}
