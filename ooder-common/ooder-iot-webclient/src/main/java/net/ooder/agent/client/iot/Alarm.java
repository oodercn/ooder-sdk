package net.ooder.agent.client.iot;



/**
 * Alarm entity. @author MyEclipse Persistence Tools
 */

public interface Alarm extends java.io.Serializable {

	
	public String getAlarmid();

	public String getStarttime();

	public void setStarttime(String starttime);

	public String getEndtime();

	public void setEndtime(String endtime);

	public String getCycle();

	public void setCycle(String cycle);

	public String getAlertcontent();

	public void setAlertcontent(String alertcontent);

	public String getComfort();

	public void setComfort(String comfort);

	public Integer getIstart();

	public void setIstart(Integer istart);

	public Integer getOperatestatus();

	public void setOperatestatus(Integer operatestatus);

	public Integer getDevicestatus();

	public void setDevicestatus(Integer devicestatus);

	public Integer getDelaytime();

	public void setDelaytime(Integer delaytime);
	

	public String getName();

	public void setName(String name);
	

	public Scene getScene() ;
	
	public String getSceneid();

	public void setSceneid(String sceneid);

	public String getSensorid();

	public void setSensorid(String sensorid);

	public Alarm clone(String alarmId);


}