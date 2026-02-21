package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SensorSceneInfo implements Serializable{
	String id;
	String sensorId;
	Integer lightValue;
	String rgbValue;
	String name;
	String sensorName;
	Integer status;
	List<String> refSensorIds=new ArrayList<String>();
	
	public SensorSceneInfo(){
		
	}
	
	public SensorSceneInfo(Scene scene){
		this.id=scene.getSceneid();
		this.sensorId=scene.getSensorid();
		this.lightValue=scene.getIntvalue();
		this.rgbValue=scene.getObjvalue();
		this.status=scene.getStatus();
		this.sensorName=scene.getZnode().getName();
	    this.name=scene.getName();
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getLightValue() {
		return lightValue;
	}
	public void setLightValue(Integer lightValue) {
		this.lightValue = lightValue;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRgbValue() {
		return rgbValue;
	}
	public void setRgbValue(String rgbValue) {
		this.rgbValue = rgbValue;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<String> getRefSensorIds() {
		return refSensorIds;
	}

	public void setRefSensorIds(List<String> refSensorIds) {
		this.refSensorIds = refSensorIds;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

}
