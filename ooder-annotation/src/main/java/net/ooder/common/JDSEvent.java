package net.ooder.common;

import net.ooder.annotation.EventEnums;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: JDS平台
 * </p>
 * <p>
 * Description: JDS内所有事件的基类，继承自java.util.EventObject
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: ooder
 * </p>
 *
 * @author ooder
 * @version 3.0
 */
public abstract class JDSEvent<T> extends java.util.EventObject {

    public JDSListener listener;

    public JDSEvent(T source) {
        super(source);

    }

    public JDSEvent(T source, JDSListener listener) {

        super(source);
        this.listener = listener;
    }

    public static final int AFTERWEBINIT = 8001;

    public static final int BEFORUPDATE = 8002;

    protected EventEnums id;

    protected String systemCode;


    protected boolean consumed = false;

    protected String expression;

    protected Map context = new HashMap();

    /**
     * 返回事件是否已经被消耗，如果是则不需要继续传递 如果事件可以被其中一个事件处理中止传递，则需要将此方法公开(public)
     */
    protected boolean isConsumed() {
        return consumed;
    }

    /**
     * 消耗掉当前事件，阻止事件继续在事件处理链中继续传递 如果事件可以被其中一个事件处理中止传递，则需要将此方法公开(public)
     */
    protected void consume() {
        consumed = true;
    }

    /**
     * 取得当前事件的ID
     */
    public abstract EventEnums getID();

    /**
     * 取得事件的上下文参数
     *
     * @param key 上下文参数的键
     * @return 上下文参数的值
     */
    public Object getEventContext(String key) {
        if (context == null) {
            return null;
        }
        return context.get(key);
    }

    public void setContextMap(Map context) {
        this.context = context;
    }

    public Map getContextMap() {
        return context;
    }

    public JDSListener getListener() {

        return listener;
    }

    public void setListener(JDSListener listener) {
        this.listener = listener;
    }

    public T getSource() {
        return (T) super.getSource();
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

}
