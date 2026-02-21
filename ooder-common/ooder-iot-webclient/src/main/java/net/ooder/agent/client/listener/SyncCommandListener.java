package net.ooder.agent.client.listener;

import  net.ooder.agent.client.home.event.CommandEvent;
import  net.ooder.agent.client.home.event.CommandListener;
import net.ooder.agent.client.iot.HomeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
//@EsbBeanAnnotation(id = "SyncCommandListener", name = "同步命令状态", expressionArr = "SyncCommandListener()", flowType = EsbFlowType.listener,desc = "同步命令状态")
public class SyncCommandListener implements CommandListener {

    private final Logger logger = LoggerFactory.getLogger(SyncCommandListener.class);
    ;


    public SyncCommandListener() {

    }

    @Override
    public void commandSendIng(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSended(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSendFail(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandExecuteSuccess(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandExecuteFail(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandSendTimeOut(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandRouteing(CommandEvent event) throws HomeException {

    }

    @Override
    public void commandRouted(CommandEvent event) throws HomeException {

    }

    @Override
    public String getSystemCode() {
        return null;
    }



}