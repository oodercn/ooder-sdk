
package net.ooder.agent.client.home.event;

import  net.ooder.common.JDSEvent;
import  net.ooder.common.JDSListener;
import  net.ooder.server.JDSClientService;


/**
 * <p>
 * Title: GW管理系统
 * </p>
 * <p>
 * Description: GW所有事件的基类，继承自java.util.EventObject
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
public abstract class HomeEvent<T> extends JDSEvent<T> {

	private JDSListener listener;


	public HomeEvent(T source) {
		super(source);
		
	}
	public HomeEvent(T source,JDSListener listener) {
		super(source);
		this.listener=listener;		
	}


	protected String expression;
	
	

	protected JDSClientService client = null;


	@Override
	public T getSource() {
		return super.getSource();
	}

	/**
	 * 设置发生事件时的JDSClientService对象！
	 * 
	 * @param client
	 */
	protected void setClientService(JDSClientService client) {
		this.client = client;
	}

	/**
	 * 取得发生事件时的BSSClientService对象！
	 * 
	 * @return
	 */

	public JDSClientService getClientService() {
		
		return client;
	}


	
	
}
