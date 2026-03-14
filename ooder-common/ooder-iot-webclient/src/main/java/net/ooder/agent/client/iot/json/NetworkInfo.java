package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.command.WifiInfo;
import net.ooder.agent.client.command.WlanInfo;

import java.util.List;

public class NetworkInfo {
	
   
	String gwieee;
	
	WifiInfo wifi;

	WlanInfo wlan;

	List<DHCPInfo> dhcp;

	public List<DHCPInfo> getDhcp() {
		return dhcp;
	}

	public void setDhcp(List<DHCPInfo> dhcp) {
		this.dhcp = dhcp;
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

	public String getGwieee() {
		return gwieee;
	}

	public void setGwieee(String gwieee) {
		this.gwieee = gwieee;
	}

	
}
