
package net.ooder.agent.client.command.filter.appmsg;

import net.ooder.agent.client.iot.HomeConstants;
import  net.ooder.common.JDSException;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.SensorMsg;
import  net.ooder.msg.filter.MsgFilter;

/**
 * 
 * @author wenzhang
 *
 */
public class ErrorReportDataFilterImpl implements MsgFilter {


	private JDSSessionHandle handle;



	public  boolean filterObject(Msg msg, JDSSessionHandle handle){

		MsgType type = MsgType.fromType( msg.getType());
		this.handle=handle;
		
		if (type.equals(HomeConstants.DEVICE_EVENTTPE_ERRORREPORT)){
			try {
				sendMessage((SensorMsg) msg);
			} catch (JDSException e) {
				e.printStackTrace();
			} 
			return true;
		}
	  
	   return false;
		
	}



	private void sendMessage(SensorMsg msg) throws JDSException {

		MsgFactroy.getInstance().getClient(null,SensorMsg.class).updateMsg(msg);
	}


	
	
}
