package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Sensortype;

import java.io.Serializable;
public class SensorTypeInfo implements Serializable{
	String id ;
	String gatewayid;
	String typeno;
	String name;
	String icon;
	String color;
	String hisdataurl;
	String alarmurl;
	String datalisturl;
	String htmltemp;
	
	
	public String getHtmltemp() {
		return htmltemp;
	}

	public void setHtmltemp(String htmltemp) {
		this.htmltemp = htmltemp;
	}

	public SensorTypeInfo(){
		
	}
	
	public SensorTypeInfo(Sensortype sensorType){
		this.id=sensorType.getTypeid();
		this.gatewayid=sensorType.getDeviceid();
		this.typeno=sensorType.getType().toString();
		this.name=sensorType.getName();
		this.color=sensorType.getColor();
		this.icon=sensorType.getIcon();
		this.hisdataurl=sensorType.getHisdataurl();
		this.htmltemp=sensorType.getHtmltemp();
		this.datalisturl=sensorType.getDatalisturl();
	}
	
	public String getGatewayid() {
		return gatewayid;
	}
	public void setGatewayid(String gatewayid) {
		this.gatewayid = gatewayid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeno() {
		return typeno;
	}
	public void setTypeno(String typeno) {
		this.typeno = typeno;
	}

	public String getAlarmurl() {
		return alarmurl;
	}

	public void setAlarmurl(String alarmurl) {
		this.alarmurl = alarmurl;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDatalisturl() {
		return datalisturl;
	}

	public void setDatalisturl(String datalisturl) {
		this.datalisturl = datalisturl;
	}

	public String getHisdataurl() {
		return hisdataurl;
	}

	public void setHisdataurl(String hisdataurl) {
		this.hisdataurl = hisdataurl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
