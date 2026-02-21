/**
 * $RCSfile: ConnectionHandle.java,v $
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

import net.ooder.common.JDSCommand;
import net.ooder.common.JDSException;
import net.ooder.context.JDSContext;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;

import java.io.Serializable;

public interface ConnectionHandle extends Serializable{

	/**
	 * 获取应用客户端
	 * @return
	 * @throws JDSException
	 */
	public JDSClientService getClient() throws JDSException;
	
	/**
	 * 获取链接用户信息
	 * @return
	 */
	public ConnectInfo getConnectInfo();
	
	/**
	 * 挂接链接
	 * @param context
	 * @throws JDSException
	 */
	public void connect(JDSContext context)throws JDSException;
	
	/**
	 * 是否已连接
	 * @return
	 * @throws JDSException
	 */
	public boolean isconnect()throws JDSException;
	
	/**
	 * 注销
	 * @throws JDSException
	 */
	public void disconnect()throws JDSException;
	
	/**
	 *  接收返回值
	 * @throws JDSException
	 */
	public void receive(String receiveStr)throws JDSException;
	
	/**
	 * 发送消息
	 * @param msgStr
	 * @return
	 * @throws JDSException
	 */
	public boolean send(String msgStr)throws JDSException;
	

	/**
	 * 转发消息
	 * @param msg
	 * @param handle
	 * @return
	 * @throws JDSException
	 */
	public boolean repeatMsg(Msg msg, JDSSessionHandle handle)throws JDSException;
	
	/**
	 * 转发命令
	 * @param command
	 * @param handle
	 * @return
	 * @throws JDSException
	 */
	public boolean repeatCommand(JDSCommand command, JDSSessionHandle handle)throws JDSException;

	/**
	 * 发送消息
	 * @param command
	 * @return
	 * @throws JDSException
	 */
	public boolean send(JDSCommand command)throws JDSException;
}
