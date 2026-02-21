package net.ooder.agent.client.iot.json;

import java.io.Serializable;

public class CycleInfo implements Serializable{
	String id;
	String alarmid;
	String sensorId;
	String starttime;
	String endtime;
	String alertcontent;
	String comfort;
	String beyond;
	String lt;
	String gt;
	String between;
	String cycle;
	Integer status;
	public String getAlarmid() {
		return alarmid;
	}
	public void setAlarmid(String alarmid) {
		this.alarmid = alarmid;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
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
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getAlertcontent() {
		return alertcontent;
	}
	public void setAlertcontent(String alertcontent) {
		this.alertcontent = alertcontent;
	}
	public String getBetween() {
		return between;
	}
	public void setBetween(String between) {
		this.between = between;
	}
	public String getBeyond() {
		return beyond;
	}
	public void setBeyond(String beyond) {
		this.beyond = beyond;
	}
	public String getComfort() {
		return comfort;
	}
	public void setComfort(String comfort) {
		this.comfort = comfort;
	}
	public String getGt() {
		return gt;
	}
	public void setGt(String gt) {
		this.gt = gt;
	}
	public String getLt() {
		return lt;
	}
	public void setLt(String lt) {
		this.lt = lt;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	

}
