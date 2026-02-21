package net.ooder.agent.client.iot;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Scene entity. @author MyEclipse Persistence Tools
 */

public interface Scene extends  java.io.Serializable {

	
	public String getSceneid();

	public Integer getIntvalue();

	public String getSensorid();

	public void setSensorid(String sensorid);

	public void setIntvalue(Integer intvalue);

	public String getObjvalue();

	public void setObjvalue(String objvalue);

	public String getName();

	public void setName(String name);

	public Integer getStatus();

	public void setStatus(Integer status);

	public ZNode getZnode();

	public void setSceneid(String id);

	

}