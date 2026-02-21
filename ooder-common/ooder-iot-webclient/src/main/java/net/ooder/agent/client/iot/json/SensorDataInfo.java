package net.ooder.agent.client.iot.json;

import java.io.Serializable;

public class SensorDataInfo implements Serializable{
	String id;
	String sensorId;	
	String battery ;
	String value;
	
	String iconTemp;
	String htmlValue;
	String datetime;
	
	public SensorDataInfo(){
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getHtmlValue() {
		return htmlValue;
	}

	public void setHtmlValue(String htmlValue) {
		this.htmlValue = htmlValue;
	}



	public String getIconTemp() {
		return iconTemp;
	}

	public void setIconTemp(String iconTemp) {
		this.iconTemp = iconTemp;
	}

	
	


}
