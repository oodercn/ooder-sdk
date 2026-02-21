package net.ooder.agent.client.iot.json;


import net.ooder.agent.client.iot.enums.DeviceStatus;

import java.io.Serializable;

public class ShareUserInfo implements Serializable{
	
	String account;
	
	String mainaccount;

	String name;

	String gatewayid;

	String gatewayname;

	DeviceStatus status;// 0停用  1启用
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getGatewayid() {
		return gatewayid;
	}

	public void setGatewayid(String gatewayid) {
		this.gatewayid = gatewayid;
	}

	public String getGatewayname() {
		return gatewayname;
	}

	public void setGatewayname(String gatewayname) {
		this.gatewayname = gatewayname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DeviceStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceStatus status) {
		this.status = status;
	}

	public String getMainaccount() {
		return mainaccount;
	}

	public void setMainaccount(String mainaccount) {
		this.mainaccount = mainaccount;
	}


}
