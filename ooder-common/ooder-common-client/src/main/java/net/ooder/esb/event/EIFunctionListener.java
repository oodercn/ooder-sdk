/**
 * $RCSfile: EIFunctionListener.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:06 $
 *
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 *
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: EIFunctionListener.java,v $
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
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 核心活动事件监听器
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
public interface EIFunctionListener extends java.util.EventListener {

	/**
	 * 活动初始化完毕，进入inactive状态
	 */
	public void activityInited(EIFunctionEvent event) throws JDSException;

	/**
	 * 活动开始执行路由操作
	 */
	public void activityRouting(EIFunctionEvent event) throws JDSException;

	
}
