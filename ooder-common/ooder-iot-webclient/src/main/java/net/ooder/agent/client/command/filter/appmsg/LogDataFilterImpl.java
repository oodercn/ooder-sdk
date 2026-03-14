
package net.ooder.agent.client.command.filter.appmsg;

import  net.ooder.common.JDSException;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.filter.MsgFilter;

/**
 * 
 * @author wenzhang
 *
 */
public class LogDataFilterImpl implements MsgFilter {


	private JDSSessionHandle handle;



	public  boolean filterObject(Msg msg, JDSSessionHandle handle){

		MsgType type = MsgType.fromType( msg.getType());
		this.handle=handle;
		
		if (type.equals(MsgType.LOG)){
			try {
				sendMessage(msg);
			} catch (JDSException e) {
				e.printStackTrace();
			} 
			return true;
		}
	  
	   return false;
		
	}



	private void sendMessage(Msg msg) throws JDSException {
		MsgFactroy.getInstance().getClient(null,Msg.class).updateMsg(msg);
//		
	}




}
