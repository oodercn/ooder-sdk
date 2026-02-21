
/**
 * $RCSfile: MsgFilter.java,v $
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
package net.ooder.msg.filter;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.engine.event.Listener;
import net.ooder.msg.Msg;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 结果过滤器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 2.0
 */
public interface MsgFilter{
	public static final Log logger = LogFactory.getLog(
			JDSConstants.CONFIG_KEY, MsgFilter.class);


	/**
 * 应用应该实现的过滤方法
 * @param msg 需要过滤的消息对象
 * @param handle 会话句柄
 * @return 是否通过过滤
 */
	public   boolean filterObject(Msg msg, JDSSessionHandle handle);
}


