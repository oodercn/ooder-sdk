package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Alarm;

import java.io.Serializable;


public class AlarmInfo implements Serializable {

	String id;

	String starttime;

	String name;

	String endtime;

	String cycle;

	String sensorId;

	String alertcontent;

	String comfort;

	Integer operateStatus;

	Integer isStart;

	String sceneid;

	String scenename;

	Integer deviceStatus;

	Integer delayTime;

	public AlarmInfo() {

	}

	public AlarmInfo(Alarm alarm) {
		this.id = alarm.getAlarmid();
		this.alertcontent = alarm.getAlertcontent();
		this.starttime = alarm.getStarttime();
		this.endtime = alarm.getEndtime();
		this.cycle = alarm.getCycle();
		this.comfort = alarm.getComfort();
		this.operateStatus = alarm.getOperatestatus();
		this.isStart = alarm.getIstart();
		this.deviceStatus = alarm.getDevicestatus();
		this.delayTime = alarm.getDelaytime();
		this.sensorId = alarm.getSensorid();
		if (alarm.getName() == null) {
			this.name = "默认报警";
		} else {
			this.name = alarm.getName();
		}
		this.sceneid = alarm.getSceneid();
		if (this.sceneid != null && alarm.getScene() != null) {
			this.scenename = alarm.getScene().getName();
		}

	}

	public String getScenename() {
		return scenename;
	}

	public String getAlertcontent() {
		return alertcontent;
	}

	public void setAlertcontent(String alertcontent) {
		this.alertcontent = alertcontent;
	}

	public String getComfort() {
		return comfort;
	}

	public void setComfort(String comfort) {
		this.comfort = comfort;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public Integer getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(Integer delayTime) {
		this.delayTime = delayTime;
	}

	public Integer getDeviceStatus() {
		return deviceStatus;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIsStart() {
		return isStart;
	}

	public void setIsStart(Integer isStart) {
		this.isStart = isStart;
	}

	public Integer getOperateStatus() {
		return operateStatus;
	}

	public void setOperateStatus(Integer operateStatus) {
		this.operateStatus = operateStatus;
	}

	public String getStarttime() {
		return starttime;
	}

	public String getSceneid() {
		return sceneid;
	}

	public void setSceneid(String sceneid) {
		this.sceneid = sceneid;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public void setDeviceStatus(Integer deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScenename(String scenename) {
		this.scenename = scenename;
	}

}
