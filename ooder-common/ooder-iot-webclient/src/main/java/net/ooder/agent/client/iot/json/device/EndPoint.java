package net.ooder.agent.client.iot.json.device;


import net.ooder.agent.client.command.HAAttribute;

import java.util.ArrayList;
import java.util.List;

public class EndPoint {
	String ieee;//长地址
		
	Integer sensorType=0;
	
	String ep;//
	
	String profileid;
	
	String deviceid;//HA属性
	
	String nwkAddress;//短地址
	
	String name;//名称

	List<HAAttribute> attributes=new ArrayList<HAAttribute>();

	public String getDeviceid() {
		return deviceid;
	}



	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}



	public String getEp() {
		return ep;
	}



	public void setEp(String ep) {
		this.ep = ep;
	}



	public String getIeee() {
		return ieee;
	}



	public void setIeee(String ieee) {
		this.ieee = ieee;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getNwkAddress() {
		return nwkAddress;
	}



	public void setNwkAddress(String nwkAddress) {
		this.nwkAddress = nwkAddress;
	}



	public String getProfileid() {
		return profileid;
	}



	public void setProfileid(String profileid) {
		this.profileid = profileid;
	}



	public Integer getSensorType() {
		return sensorType;
	}



	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
	}



	public List<HAAttribute> getAttributes() {
		return attributes;
	}



	public void setAttributes(List<HAAttribute> attributes) {
		this.attributes = attributes;
	}





	
}
