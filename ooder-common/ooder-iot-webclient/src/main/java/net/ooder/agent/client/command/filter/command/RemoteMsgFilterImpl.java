package net.ooder.agent.client.command.filter.command;

import net.ooder.agent.client.iot.HomeConstants;
import  net.ooder.cluster.ServerNode;
import  net.ooder.common.JDSException;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.udp.SendRemoteAppMsg;
import  net.ooder.msg.Msg;
import  net.ooder.msg.filter.MsgFilter;
import  net.ooder.server.JDSServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 
 * @author wenzhang
 * 
 */
public class RemoteMsgFilterImpl implements MsgFilter {
	

	static	Map<String ,ScheduledExecutorService >  remoteServiceMap = new HashMap<String ,ScheduledExecutorService >();
	
	public RemoteMsgFilterImpl(){
		
	}
	
    static synchronized ScheduledExecutorService getRemoteService(String personId){	
		 ScheduledExecutorService service=remoteServiceMap.get(personId);
		  if (service==null||service.isShutdown()){
			  service=Executors.newSingleThreadScheduledExecutor();  
			  remoteServiceMap.put(personId, service);
			}
			return service;
		}
	
	public boolean filterObject(Msg msg, JDSSessionHandle handle) {
		ServerNode currServerBean=null;
			try {
				currServerBean = JDSServer.getInstance().getCurrServerBean();
				if (!currServerBean.getId().equals(HomeConstants.SYSTEM_SERVER_TYPE_CONSOL)) {
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
		SendRemoteAppMsg msgRun = new SendRemoteAppMsg(msg);
		getRemoteService(msg.getReceiver()).submit(msgRun);

		return true;
	}


}
