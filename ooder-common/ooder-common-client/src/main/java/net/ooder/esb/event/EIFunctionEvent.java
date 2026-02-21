
/**
 * $RCSfile: EIFunctionEvent.java,v $
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

import net.ooder.common.expression.function.Function;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心活动事件
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
public class EIFunctionEvent extends ESBEvent {

	
	public EIFunctionEvent(Function function, int eventID) {
		super(function,null);
		id = eventID;
	}


	/**
	 * 取得触发此流程事件的一个或多个活动实例
	 */
	public Function getFunction() {
		return (Function) getSource();
	}
	
	
}
