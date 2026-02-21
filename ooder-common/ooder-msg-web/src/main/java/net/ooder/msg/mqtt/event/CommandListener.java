/**
 * $RCSfile: CommandListener.java,v $
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
package net.ooder.msg.mqtt.event;

import net.ooder.annotation.MethodChinaName;
import net.ooder.msg.mqtt.JMQException;

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


    /**
     * 1001
     * 命令开始发送
     *
     * @param event
     * @throws JMQException
     */
    @MethodChinaName("命令开始发送")
    public void commandSendIng(MQTTCommandEvent event) throws JMQException;


    /**
     * 发送结束,等待服务端结束
     *
     * @param event
     * @throws JMQException
     */
    public void commandSended(MQTTCommandEvent event) throws JMQException;


    /**
     * 命令未到达2001/
     *
     * @param event
     * @throws JMQException
     */
    public void commandSendFail(MQTTCommandEvent event) throws JMQException;

    /**
     * 1000
     * 命令执行成功
     *
     * @param event
     * @throws JMQException
     */
    public void commandExecuteSuccess(MQTTCommandEvent event) throws JMQException;


    /**
     * 命令执行失败
     *
     * @param event
     * @throws JMQException
     */
    public void commandExecuteFail(MQTTCommandEvent event) throws JMQException;


    /**
     * 发送超时
     *
     * @param event
     * @throws JMQException
     */
    public void commandSendTimeOut(MQTTCommandEvent event) throws JMQException;


    /**
     * 开始路由（引擎内部方法）
     *
     * @param event
     * @throws JMQException
     */
    public void commandRouteing(MQTTCommandEvent event) throws JMQException;


    /**
     * 路由结束（引擎内部方法）
     *
     * @param event
     * @throws JMQException
     */
    public void commandRouted(MQTTCommandEvent event) throws JMQException;


    /**
     * 得到系统Code
     *
     * @return
     */
    public String getSystemCode();

}


