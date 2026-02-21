
package net.ooder.agent.client.command.filter.appmsg;

import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSException;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.SensorMsg;
import  net.ooder.msg.filter.MsgFilter;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;

/**
 * 
 * @author wenzhang
 *
 */
public class AlarmDataFilterImpl implements MsgFilter {

	private JDSSessionHandle handle;



	/**
	 * 应用应该实现的过滤方法。
	 * 
	 * @param msg
	 *            需要过滤的对象
	 * @return
	 */
	

	
	public  boolean filterObject(Msg msg, JDSSessionHandle handle){

		MsgType type = MsgType.fromType( msg.getType());
		this.handle=handle;
		
		if (type.equals(MsgType.ALARM)){
			try {
				sendMessage((SensorMsg)msg);
			} catch (JDSException e) {
				e.printStackTrace();
			} 
			return true;
		}
	  
	   return false;
		
	}



	private void sendMessage(SensorMsg msg) throws JDSException {

		String systemCode = JDSServer.getInstance().getSessionhandleSystemCodeCache().get(handle.getSessionID());
		if (systemCode != null) {
			ConfigCode configCode=JDSServer.getClusterClient().getSystem( msg.getSystemCode()).getConfigname();
			JDSClientService client = JDSServer.getInstance().getJDSClientService(handle,configCode);
			if (client != null && client.getConnectInfo() != null) {
				AppClient appclient = HomeServer.getInstance().getAppClient(client);
				appclient.sendAlarmMsg(msg);
			}
		}

	}




}
