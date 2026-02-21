/**
 * $RCSfile: Listener.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:02 $
 *
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 *
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: Listener.java,v $
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
package net.ooder.engine.event;


import net.ooder.annotation.MethodChinaName;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 监听器客户端接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface Listener {

	public static final String PROCESS_LISTENER_EVENT = "PROCESS";

	public static final String ACTIVITY_LISTENER_EVENT = "ACTIVITY";

	public static final String ACTIVITY_MAPDAO_LISTENER_EVENT = "MAPDAO";

	public static final String EXPRESSIONLISENTERTYPE_EXPRESSION = "EXPRESSION";

	public static final String EXPRESSIONLISENTERTYPE_LISTENER = "LISTENER";

	/**
	 * Getter method for listenerId
	 *
	 * @return the value of listenerId
	 */
	@MethodChinaName(cname = "监听器ID")
	public String getListenerId();

	/**
	 * Getter method for listenername
	 *
	 * @return the value of listenername
	 */
	@MethodChinaName(cname = "监听器名称")
	public String getListenerName();

	/**
	 * Getter method for listenerregistevent
	 *
	 * @return the value of listenerregistevent
	 */
	@MethodChinaName(cname = "监听器事件")
	public String getListenerEvent();

	/**
	 * Getter method for realizeclass
	 *
	 * @return the value of realizeclass
	 */
	@MethodChinaName(cname = "执行类")
	public String getRealizeClass();

	@MethodChinaName(cname = "监听事件类型")
	public String getExpressionEventType();

	@MethodChinaName(cname = "监听器类型")
	public String getExpressionListenerType();

	@MethodChinaName(cname = "执行表达式")
	public String getExpressionStr();

}