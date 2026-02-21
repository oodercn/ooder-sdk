package net.ooder.agent.client.command.filter;

import net.ooder.agent.client.command.filter.command.LocalMsgFilterImpl;
import net.ooder.agent.client.command.filter.command.RemoteMsgFilterImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.filter.MsgFilter;
import  net.ooder.msg.filter.MsgFilterChain;

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
		chain.addFilter(new RemoteMsgFilterImpl());
		return chain.filterObject(msg, handle);
	}



}
