/**
 * $RCSfile: LocalMsgFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter.command;

import net.ooder.cluster.ServerNode;
import net.ooder.common.JDSException;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.msg.filter.MsgFilter;
import net.ooder.msg.filter.MsgFilterChain;
import net.ooder.msg.mqtt.command.filter.appmsg.LogDataFilterImpl;
import net.ooder.msg.mqtt.command.filter.appmsg.SMSDataFilterImpl;
import net.ooder.msg.mqtt.command.filter.appmsg.SystemMsgFilterImpl;
import net.ooder.server.JDSServer;

/**
 * @author wenzhang
 */
public class LocalMsgFilterImpl implements MsgFilter {


    public LocalMsgFilterImpl() {

    }

    /**
     * 应用应该实现的过滤方法。
     *
     * @param msg 需要过滤的对象
     * @return
     */
    public boolean filterObject(Msg msg, JDSSessionHandle handle) {

        ServerNode currServerBean = null;
        try {
            currServerBean = JDSServer.getInstance().getCurrServerBean();

            this.process(msg, handle);
            return true;


        } catch (JDSException e) {
            e.printStackTrace();
        }
        return false;

    }


    private boolean process(Msg msg, JDSSessionHandle handle) throws JDSException {
        MsgFilterChain chain = new MsgFilterChain();
        chain.addFilter(new LogDataFilterImpl());
        chain.addFilter(new SMSDataFilterImpl());
        chain.addFilter(new SystemMsgFilterImpl());

        return chain.filterObject(msg, handle);
    }


}


