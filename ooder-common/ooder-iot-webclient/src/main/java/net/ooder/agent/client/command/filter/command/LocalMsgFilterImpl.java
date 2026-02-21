package net.ooder.agent.client.command.filter.command;

import net.ooder.agent.client.command.filter.appmsg.*;
import net.ooder.agent.client.iot.HomeConstants;
import  net.ooder.cluster.ServerNode;
import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import  net.ooder.msg.Msg;
import  net.ooder.msg.filter.MsgFilter;
import  net.ooder.msg.filter.MsgFilterChain;
import  net.ooder.server.JDSServer;

/**
 * 
 * @author wenzhang
 * 
 */
public class LocalMsgFilterImpl implements MsgFilter {
	

	
	public LocalMsgFilterImpl(){
		
	}
	
	/**
	 * 应用应该实现的过滤方法。
	 * 
	 * @param msg
	 *            需要过滤的对象
	 * @return
	 */
	public boolean filterObject(Msg msg, JDSSessionHandle handle)  {

		ServerNode currServerBean=null;
		try {
			currServerBean = JDSServer.getInstance().getCurrServerBean();
			if (currServerBean.getId().equals(HomeConstants.SYSTEM_SERVER_TYPE_CONSOL)) {
				this.process( msg, handle);	
				return true;
			}else{
			  return false;
			}

		} catch (JDSException e) {
			e.printStackTrace();
		}
		return false;

	}
	


	private boolean process(Msg msg, JDSSessionHandle handle) throws JDSException {
		MsgFilterChain chain=new MsgFilterChain();
		chain.addFilter(new AlarmDataFilterImpl());
		chain.addFilter(new SensorDataFilterImpl());
		chain.addFilter(new LogDataFilterImpl());
		chain.addFilter(new ErrorReportDataFilterImpl());
		chain.addFilter(new SMSDataFilterImpl());
		chain.addFilter(new SystemMsgFilterImpl());
	
		return chain.filterObject(msg, handle);
	}


}
