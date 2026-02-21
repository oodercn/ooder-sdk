/**
 * $RCSfile: ExecScriptCommand.java,v $
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
package net.ooder.msg.mqtt.command.cmd;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;


@EsbBeanAnnotation(id = "ExecScript", name = "执行脚本", expressionArr = "ExecScriptCommand()", desc = "执行脚本")
public class ExecScriptCommand extends MQTTCommand {

    String script;

    public ExecScriptCommand() {
        super(MQTTCommandEnums.ExecScript);

    }

    public ExecScriptCommand(String script) {
        super(MQTTCommandEnums.ExecScript);
        this.script = script;

    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}


