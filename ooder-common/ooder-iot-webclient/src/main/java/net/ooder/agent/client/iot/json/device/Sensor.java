package net.ooder.agent.client.iot.json.device;

import java.util.ArrayList;
import java.util.List;


public class Sensor {
	
	String serialno;
	
	Integer battery=100;
	
	Integer sensorType=100;
	
	String zigbeekey;
	//兼容																																																																																																																																																																																																																														
	String deviceName;
   
	String currVersion;

	String status;
	
	String value;
	
	
	
	List<EndPoint> epList=new ArrayList<EndPoint>();
	
	List<BindInfo> bindList=new ArrayList<BindInfo>();
	
	
	public List<BindInfo> getBindList() {
		return bindList;
	}


	public void setBindList(List<BindInfo> bindList) {
		this.bindList = bindList;
	}


	public List<EndPoint> getEpList() {
		return epList;
	}


	public void setEpList(List<EndPoint> epList) {
		this.epList = epList;
	}


	public Sensor( ){}
	

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getZigbeekey() {
		return zigbeekey;
	}

	public void setZigbeekey(String zigbeekey) {
		this.zigbeekey = zigbeekey;
	}

	
	public Integer getSensorType() {
		return sensorType;
	}

	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
	}

	public String getCurrVersion() {
		return currVersion;
	}

	public void setCurrVersion(String currVersion) {
		this.currVersion = currVersion;
	}

	public Integer getBattery() {
		return battery;
	}
	public void setBattery(Integer battery) {
		this.battery = battery;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	

}
