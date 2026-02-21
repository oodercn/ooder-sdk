
/**
 * $RCSfile: ESBEventDispatcher.java,v $
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

import net.ooder.common.JDSException;

/**
 * <p>
 * Title: 总线管理系统
 * </p>
 * <p>
 * Description: 事件分发接口，实现此接口的类需要管理事件和监听器，将发送到达的时间分配给相应的事件监听器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author liwenzhang
 * @version 1.0
 */
public interface ESBEventDispatcher {

	/**
	 * 分发事件方法，实现此方法的类需要根据事件类型决定如何分发和处理此事件！
	 */
	public void dispatchEvent(ESBEvent event) throws JDSException;
}
