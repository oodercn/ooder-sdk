/**
 * $RCSfile: P2PListener.java,v $
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
import net.ooder.msg.Msg;
import net.ooder.msg.mqtt.JMQException;

/**
 * <p>
 * Title: Msg管理系统
 * </p>
 * <p>
 * Description: 核心事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 2.0
 */


public interface P2PListener extends java.util.EventListener {


    @MethodChinaName("指定用户")
    public void send2Person(P2PEvent<Msg> event) throws JMQException;


    @MethodChinaName("指定客户端消息")
    public void send2Client(P2PEvent<Msg> event) throws JMQException;


    @MethodChinaName("指定用户群消息")
    public void send2PersonMsg(P2PEvent<Msg> event) throws JMQException;




    /**
     * 得到系统Code
     *
     * @return
     */
    public String getSystemCode();

}


