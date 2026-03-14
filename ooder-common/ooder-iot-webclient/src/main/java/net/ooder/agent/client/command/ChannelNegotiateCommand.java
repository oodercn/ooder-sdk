package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ChannelNegotiate",dataType=ContextType.Command, name = "修改WIFI信息", expressionArr = "ChannelNegotiateCommand()", desc = "修改WIFI信息")

public class ChannelNegotiateCommand extends Command {
    WifiInfo wifi=new WifiInfo();
    WlanInfo wlan=new WlanInfo();
    public ChannelNegotiateCommand() {
	super(CommandEnums.ChannelNegotiate);

    }



    public WifiInfo getWifi() {
	return wifi;
    }

    public void setWifi(WifiInfo wifi) {
	this.wifi = wifi;
    }

    public WlanInfo getWlan() {
	return wlan;
    }

    public void setWlan(WlanInfo wlan) {
	this.wlan = wlan;
    }
}
