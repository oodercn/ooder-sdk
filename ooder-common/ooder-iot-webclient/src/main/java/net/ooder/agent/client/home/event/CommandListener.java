package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.HomeException;
import  net.ooder.annotation.MethodChinaName;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */


public interface CommandListener extends java.util.EventListener {
	

	

	
	/** 1001
	 * 命令开始发送
	 * @param event
	 * @throws HomeException
	 */
	@MethodChinaName(cname="命令开始发送")
	public void commandSendIng(CommandEvent event)throws HomeException;
	
	
	/**
	 * 发送结束/等待服务端结果
	 * @param event
	 * @throws HomeException
	 */
	public void commandSended(CommandEvent event)throws HomeException;
	
	
	/**
	 * 命令未到达 2001/
	 * @param event
	 * @throws HomeException
	 */
	public void commandSendFail(CommandEvent event)throws HomeException;
	
	/**
	 * 1000
	 * 命令执行成功
	 * @param event
	 * @throws HomeException
	 */
	public void commandExecuteSuccess(CommandEvent event)throws HomeException;
	
	
	/**
	 * 命令执行失败
	 * @param event
	 * @throws HomeException
	 */
	public void commandExecuteFail(CommandEvent event)throws HomeException;
	
	

	/**
	 * 发送超时
	 * @param event
	 * @throws HomeException
	 */
	public void commandSendTimeOut(CommandEvent event)throws HomeException;
	
	
	
	/**
	 * 开始路由（引擎内部方法）
	 * @param event
	 * @throws HomeException
	 */
	public void commandRouteing(CommandEvent event)throws HomeException;
	
	

	/**
	 * 路由结束（引擎内部方法）
	 * @param event
	 * @throws HomeException
	 */
	public void commandRouted(CommandEvent event)throws HomeException;
	
	
	
	
	
	
	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}
