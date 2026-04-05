/**
 * $RCSfile: Listener.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/01/23 16:29:55 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client;

import net.ooder.bpm.enums.event.ListenerTypeEnums;
import net.ooder.annotation.MethodChinaName;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 监听器客户端接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface ProcessListener extends JDSListener {

    /**
     * Getter method for listenerId
     * 
     * @return the value of listenerId
     */
    @MethodChinaName(cname = "监听器ID")
    public String getListenerId();

    /**
     * Getter method for listenername
     * 
     * @return the value of listenername
     */
    @MethodChinaName(cname = "监听器名称")
    public String getListenerName();

    /**
     * Getter method for listenerregistevent
     * 
     * @return the value of listenerregistevent
     */
    @MethodChinaName(cname = "监听器事件")
    public String getListenerEvent();

    /**
     * Getter method for realizeclass
     * 
     * @return the value of realizeclass
     */
    @MethodChinaName(cname = "执行类")
    public String getRealizeClass();

    @MethodChinaName(cname = "监听事件类型")
    public String getExpressionEventType();

    @MethodChinaName(cname = "监听器类型")
    public ListenerTypeEnums getExpressionListenerType();

    @MethodChinaName(cname = "执行表达式")
    public String getExpressionStr();

}