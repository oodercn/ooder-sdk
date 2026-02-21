
/**
 * $RCSfile: MQTTEvent.java,v $
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
package net.ooder.msg.mqtt.event;

import net.ooder.common.JDSEvent;
import net.ooder.common.JDSListener;
import net.ooder.server.JDSClientService;


/**
 * <p>
 * Title: im管理系统
 * </p>
 * <p>
 * Description: IM所有事件的基类，继承自java.util.EventObject
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
public abstract class MQTTEvent<T> extends JDSEvent<T> {

	private JDSListener listener;




	abstract String getExpression();

	public MQTTEvent(T source) {
		super(source);
		
	}
	public MQTTEvent(T source,JDSListener listener) {
		super(source);
		this.listener=listener;		
	}


	

	protected JDSClientService client = null;


	@Override
	public T getSource() {
		return super.getSource();
	}

	/**
	 * 设置发生事件时的JDSClientService对象。
	 * 
	 * @param client
	 */
	protected void setClientService(JDSClientService client) {
		this.client = client;
	}

	/**
	 * 取得发生事件时的BSSClientService对象。
	 * 
	 * @return
	 */

	public JDSClientService getClientService() {
		
		return client;
	}


	
	
}


