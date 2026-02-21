
/**
 * $RCSfile: CommandListenerAdapter.java,v $
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

import net.ooder.msg.mqtt.JMQException;

public abstract class CommandListenerAdapter implements CommandListener {

    @Override
    public void commandSendIng(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandSended(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandSendFail(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandExecuteSuccess(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandExecuteFail(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandSendTimeOut(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandRouteing(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public void commandRouted(MQTTCommandEvent event) throws JMQException {

    }

    @Override
    public String getSystemCode() {
        return null;
    }
}


