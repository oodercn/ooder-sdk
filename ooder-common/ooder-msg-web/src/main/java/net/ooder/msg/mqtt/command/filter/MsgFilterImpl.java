/**
 * $RCSfile: MsgFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter;

import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgType;
import net.ooder.msg.filter.MsgFilter;
import net.ooder.msg.filter.MsgFilterChain;
import net.ooder.msg.mqtt.command.filter.command.LocalMsgFilterImpl;

/**
 * 
 * @author wenzhang
 * 
 */
public class MsgFilterImpl implements MsgFilter {

	/**
	 * 应用应该实现的过滤方法。
	 *
	 *            需要过滤的对象
	 * @return
	 */
	
	public MsgFilterImpl(){
		
	}
	@Override
	public boolean filterObject(Msg msg, JDSSessionHandle handle) {

		MsgType type = MsgType.fromType( msg.getType());
		if (type!=null && !type.equals(MsgType.COMMAND) ) {
			return process(msg, handle);

		}
		return false;

	}

	private boolean process(Msg msg, JDSSessionHandle handle) {
		MsgFilterChain chain = new MsgFilterChain();
		chain.addFilter(new LocalMsgFilterImpl());
		return chain.filterObject(msg, handle);
	}



}


