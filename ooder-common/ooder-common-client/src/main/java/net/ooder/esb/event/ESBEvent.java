/**
 * $RCSfile: ESBEvent.java,v $
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
package net.ooder.esb.event;

import java.util.Map;

import net.ooder.engine.event.Listener;
import net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: 总线事件
 * </p>
 * <p>
 * Description: 总线所有事件的基类，继承自java.util.EventObject
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 1.0
 */
public abstract class ESBEvent extends java.util.EventObject {

	private Listener listener;

	public ESBEvent(Object source) {		
		super(source);
	}
	
	public ESBEvent(Object source,Listener listener) {		
		super(source);
		this.listener=listener;		
	}

	/**
	 * 活动初始化完毕，进入inactive状态
	 */
	public static final int AFTERWEBINIT = 8001;

	/**
	 * 活动开始执行路由操作
	 */
	public static final int BEFORUPDATE = 8002;
	
	protected int id = -1;
	protected boolean consumed = false;
	protected String expression;
	protected JDSClientService client = null;
	protected Map context = null;

	/**
	 * 返回事件是否已经被消耗，如果是则不需要继续传递。如果事件可以被其中一个事件处理中止传递，则需要将此方法公开(public)
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * 消耗掉当前事件，阻止事件继续在事件处理链中继续传递。如果事件可以被其中一个事件处理中止传递，则需要将此方法公开(public)
	 */
	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	/**
	 * 取得当前事件的ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * 设置发生事件时的WorkflowClientService对象实例
	 */
	public void setWorkflowClientService(Object workflowClientService) {
		this.client = (JDSClientService) workflowClientService;
	}

	/**
	 * 取得发生事件时的WorkflowClientService对象实例
	 */
	public Object getWorkflowClientService() {
		return client;
	}

	/**
	 * 取得事件的上下文参数
	 * 
	 * @param key
	 * @return
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

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
}