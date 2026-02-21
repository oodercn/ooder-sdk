/**
 * $RCSfile: MQTTCommandEnums.java,v $
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
package net.ooder.msg.mqtt.enums;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Enumstype;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.cmd.*;

public enum MQTTCommandEnums implements Enumstype {


    SubscriptTopic("订阅主题", "SubscriptTopic", SubscriptTopicCommand.class),

    UnSubscriptTopic("取消订阅主题", "UnSubscriptTopic", UnSubscriptTopicCommand.class),

    ExecScript("执行脚本", "ExecScript", ExecScriptCommand.class),

    FireEvent("触发事件", "FireEvent", FireEventCommand.class),

    CreateTopic("新建主题", "CreateTopic", CreateTopicCommand.class),

    ClearTopic("清空主题", "ClearTopic", ClearTopicCommand.class);


    private String name;
    private Class<? extends MQTTCommand> command;
    private String type;

    MQTTCommandEnums(String name, String type, Class<? extends MQTTCommand> command) {

        this.name = name;
        this.type = type;
        this.command = command;
    }

    public static MQTTCommandEnums fromByName(String typename) {
        for (MQTTCommandEnums type : MQTTCommandEnums.values()) {
            if (type.getType().toUpperCase().equals(typename.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
    @Override
    public String getType() {
        return type;
    }

    @JSONField
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type;
    }

    public Class<? extends MQTTCommand> getCommand() {
        return command;
    }

    public void setCommand(Class<? extends MQTTCommand> command) {
        this.command = command;
    }

    public void setName(String name) {
        this.name = name;
    }

}


