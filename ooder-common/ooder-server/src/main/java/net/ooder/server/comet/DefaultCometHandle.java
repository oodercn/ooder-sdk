
package net.ooder.server.comet;

import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;


public  class DefaultCometHandle extends AbstractCometHandle {

	public DefaultCometHandle(JDSClientService client, JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
		super(client,sessionHandle, systemCode);
	}

	@Override
	public void receive(String receiveStr) throws JDSException {
		logger.info("receive user:["+this.getConnectInfo().getLoginName()+"] ");
		logger.info(receiveStr);
	
	}

	@Override
	public boolean send(String msgString) throws JDSException {
		//logger.debug("send user:["+this.getConnectInfo().getLoginName()+"] ");
		logger.info(msgString);
		return super.send(msgString);
	}

	@Override
	public void disconnect() throws JDSException {
		//logger.info("user:"+connectInfo.getLoginName()+"["+sessionHandle.getSessionID()+"] cometLogout ");
		isClose=false;
		try {
			if (this.getClient()!=null){
			
				this.getClient().disconnect();
				
			}			
		} catch (JDSException e) {
			throw new JDSException(e);
		}
		
	}

	@Override
	public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
