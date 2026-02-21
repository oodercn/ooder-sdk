package net.ooder.agent.client.iot.json;

import java.io.Serializable;

public class AlarmMessageInfo implements Serializable{

	String id;
	String title;
	String message;
	String sensorId;
	String time;

	



	public AlarmMessageInfo(){
		
	}

	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
