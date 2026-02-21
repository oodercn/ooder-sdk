
/**
 * $RCSfile: LogDataFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter.appmsg;

import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgFactroy;
import net.ooder.msg.MsgType;
import net.ooder.msg.filter.MsgFilter;

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


