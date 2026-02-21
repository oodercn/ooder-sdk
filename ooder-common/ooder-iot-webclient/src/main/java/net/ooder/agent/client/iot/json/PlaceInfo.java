package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Place;

import java.io.Serializable;


public class PlaceInfo implements Serializable {
    String id;
    String account;
    String name;
    String sensorIds;
    String isstarts;

    public PlaceInfo() {

    }

    public PlaceInfo(Place place) {
	this.id = place.getPlaceid();
	this.account = place.getUserid();
	this.name = place.getName();
	this.sensorIds = place.getMemo();
	this.isstarts = place.getStart();

    }

    public String getSensorIds() {
	return sensorIds;
    }

    public void setSensorIds(String sensorIds) {
	this.sensorIds = sensorIds;
    }

    public String getAccount() {
	return account;
    }

    public void setAccount(String account) {
	this.account = account;
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

    public String getIsstarts() {
	return isstarts;
    }

    public void setIsstarts(String isstarts) {
	this.isstarts = isstarts;
    }


}
