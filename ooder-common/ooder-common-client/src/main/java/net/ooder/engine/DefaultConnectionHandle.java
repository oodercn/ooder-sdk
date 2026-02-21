/**
 * $RCSfile: DefaultConnectionHandle.java,v $
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
package net.ooder.engine;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.context.JDSContext;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;
/**
 * 默认实现
 * @author wenzhang
 *
 */
public class DefaultConnectionHandle implements ConnectionHandle{

	

	  public DefaultConnectionHandle(JDSClientService client, JDSSessionHandle sessionHandle, ConfigCode configCode)
	    
	  {
	    
	  }
	  

	@Override
	public JDSClientService getClient() throws JDSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectInfo getConnectInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect(JDSContext context) throws JDSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isconnect() throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() throws JDSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive(String receiveStr) throws JDSException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean send(String msgStr) throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean repeatMsg(Msg msg, JDSSessionHandle handle)
			throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean repeatCommand(JDSCommand command, JDSSessionHandle handle)
			throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean send(JDSCommand command) throws JDSException {
		// TODO Auto-generated method stub
		return false;
	}

}
