package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Area;

import java.io.Serializable;

public class AreaInfo implements Serializable{
	String id;
	String name;
	String placeId;
	
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public AreaInfo(){
		
	}
	public AreaInfo(Area
							area){
		this.id=area.getAreaid();
		this.name=area.getName();
		this.placeId=area.getPlaceid();
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
	

}
