package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

import java.util.UUID;

@EsbBeanAnnotation(id = "Debug", dataType=ContextType.Command,name = "debug调试", expressionArr = "DebugCommand()", desc = "debug调试")
public class DebugCommand extends Command implements ADCommand{

    String gatewayAccount;
    String keyword;
    String mainServerUrl;
    String commandServerUrl;

    public DebugCommand() {
	super(CommandEnums.Debug);
        gatewayAccount= UUID.randomUUID().toString();
        keyword=UUID.randomUUID().toString();
        mainServerUrl="http://gw.itjds.net";
        commandServerUrl="http://comet.itjds.net";
    }

    public String getCommandServerUrl() {
	return commandServerUrl;
    }

    public void setCommandServerUrl(String commandServerUrl) {
	this.commandServerUrl = commandServerUrl;
    }

    public String getGatewayAccount() {
	return gatewayAccount;
    }

    public void setGatewayAccount(String gatewayAccount) {
	this.gatewayAccount = gatewayAccount;
    }

    public String getKeyword() {
	return keyword;
    }

    public void setKeyword(String keyword) {
	this.keyword = keyword;
    }

    public String getMainServerUrl() {
	return mainServerUrl;
    }

    public void setMainServerUrl(String mainServerUrl) {
	this.mainServerUrl = mainServerUrl;
    }

    @Override
    public String getFactory() {
        return "jds";
    }
}
