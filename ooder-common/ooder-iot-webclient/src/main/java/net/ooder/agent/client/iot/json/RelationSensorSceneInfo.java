package net.ooder.agent.client.iot.json;

public class RelationSensorSceneInfo {

	
	String id;
	String sensorId;
	Integer  lightValue;
	String rgbValue;
	String name;
	Integer status;
	
	
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

}
