package net.ooder.agent.client.home.listener;

import  net.ooder.agent.client.home.event.CommandEvent;
import  net.ooder.agent.client.home.event.CommandListener;
import net.ooder.agent.client.iot.HomeException;

public class DefalutCommandListener implements CommandListener {

    @Override
    public void commandSendIng(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandSended(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandSendFail(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandExecuteSuccess(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandExecuteFail(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandSendTimeOut(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandRouteing(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public void commandRouted(CommandEvent event) throws HomeException {
	// TODO Auto-generated method stub

    }

    @Override
    public String getSystemCode() {
	// TODO Auto-generated method stub
	return null;
    }

}
