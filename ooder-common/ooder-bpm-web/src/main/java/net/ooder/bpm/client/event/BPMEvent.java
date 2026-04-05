
package net.ooder.bpm.client.event;

import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.JDSEvent;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS平台
 * </p>
 * <p>
 * Description: JDS内所有事件的基类，继承自java.util.EventObject
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 3.0
 */

public abstract class BPMEvent<T> extends JDSEvent {


	public BPMEvent(T source) {		
		super(source);
		
	}
	public BPMEvent(T source,JDSListener listener) {
		super(source);
		this.listener=listener;		
	}


	
	protected String expression;
	

	protected WorkflowClientService client = null;

	

	/**
	 * 设置发生事件时的WorkflowClientService对象！
	 * 
	 * @param client
	 */
	public void setClientService(WorkflowClientService client) {
		this.client = client;
	}

	/**
	 * 取得发生事件时的WorkflowClientService对象！
	 * 
	 * @return
	 */
	public WorkflowClientService getClientService() {
		return client;
	}

	
	
	
	
}
