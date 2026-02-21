/**
 * $RCSfile: CommandFilterImpl.java,v $
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
package net.ooder.msg.mqtt.command.filter;

import com.alibaba.fastjson.JSONObject;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.common.CommandEventEnums;
import net.ooder.msg.CommandMsg;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgFactroy;
import net.ooder.msg.MsgType;
import net.ooder.msg.filter.MsgFilter;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.filter.command.LocalCommandFilterImpl;
import net.ooder.msg.mqtt.command.filter.command.RemoteCommandFilterImpl;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;

/**
 * @author wenzhang
 */
public class CommandFilterImpl implements MsgFilter {

    /**
     * 应用应该实现的过滤方法。
     *
     * @param msg 需要过滤的对象
     * @return
     */
    public boolean filterObject(Msg msg, JDSSessionHandle handle) {

        MsgType type = MsgType.fromType(msg.getType());
        if (type != null && type.equals(MsgType.COMMAND)) {
            return process((CommandMsg) msg, handle);

        }
        return false;
    }

    private boolean process(CommandMsg msg, JDSSessionHandle sessionHandle) {
        Boolean isSuccess = false;
        String msgbody = msg.getBody();
        JSONObject jsonobj = JSONObject.parseObject(msgbody);
        MQTTCommand command = (MQTTCommand) JSONObject.parseObject(msgbody, MQTTCommandEnums.fromByName(jsonobj.getString("command")).getCommand());
        command.setCommandId(msg.getId());
        CommandFilterChain chain = new CommandFilterChain();
        chain.addFilter(new LocalCommandFilterImpl());
        chain.addFilter(new RemoteCommandFilterImpl());
        isSuccess = chain.filterObject(command, sessionHandle);

        if (isSuccess) {
            msg.setResultCode(CommandEventEnums.COMMANDROUTED);
            MsgFactroy.getInstance().getClient(null, CommandMsg.class).updateMsg(msg);
        }

        return isSuccess;
    }

}


