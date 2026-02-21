
/**
 * $RCSfile: MsgFilterChain.java,v $
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

import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.filter.AbstractMsgFilter;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 结果过滤器的链调用实现类
 * </p>
 * 此实现类本身不做任何判断和过滤，仅仅提供一个过滤器链的载体
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * d
 * @author wenzhang li
 * @version 1.0
 */
public class MsgFilterChain extends AbstractMsgFilter {
	
	
	public MsgFilterChain(){
		
	}

	/**
	 * 过滤器
	 *
	 * @return
	 */
	public boolean filterObject(Msg msg, JDSSessionHandle handle) {
		return processChildFilter(msg,handle);
	}
	
}


