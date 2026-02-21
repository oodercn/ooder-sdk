package net.ooder.agent.client.home.udp;


import net.ooder.agent.client.iot.json.AlarmMessageInfo;
import net.ooder.agent.client.iot.json.SensorDataInfo;
import net.ooder.agent.client.iot.json.UserInfo;

public class UDPData {
	
	public static final int SYSTEMCODE=1;
	
	public static final int ALARMCODE=2;
	
	public static final int SENSORCODE=3;
	
	public static final int SYSTEMLOGIN=1001;
	
	public static final int SYSTEMLOGOUT=1002;
	
	public static final int ALARMADD=2001;
	
	public static final int SENSOROFFLINE=2002;
	
	public static final int SENSORONLINE=2003;
	
	public static final int SENSORADDFAIL=2004;
	
	public static final int SENSORUPDATEVALUE=3001;
	
	String systemCode;
	String sessionId;
	String msgStr;
	
	Integer commond;//SYSTEM -1  ALARM-3  SENSOR-3
	Integer event;//[SYSTEM-LOGIN  LOGOUT ] [ALARM -ADD] [SENSOR-UPDATEVALUE ]
	SensorDataInfo sensorinfo;
	UserInfo userinfo;
	AlarmMessageInfo alarmMessageInfo;

	
	public AlarmMessageInfo getAlarmMessageInfo() {
		return alarmMessageInfo;
	}
	public void setAlarmMessageInfo(AlarmMessageInfo alarmMessageInfo) {
		this.alarmMessageInfo = alarmMessageInfo;
	}
	public SensorDataInfo getSensorinfo() {
		return sensorinfo;
	}
	public void setSensorinfo(SensorDataInfo sensorinfo) {
		this.sensorinfo = sensorinfo;
	}
	public UserInfo getUserinfo() {
		return userinfo;
	}
	public void setUserinfo(UserInfo userinfo) {
		this.userinfo = userinfo;
	}
	
	public Integer getCommond() {
		return commond;
	}
	public void setCommond(Integer commond) {
		this.commond = commond;
	}
	public Integer getEvent() {
		return event;
	}
	public void setEvent(Integer event) {
		this.event = event;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getMsgStr() {
		return msgStr;
	}
	public void setMsgStr(String msgStr) {
		this.msgStr = msgStr;
	}
	
}
