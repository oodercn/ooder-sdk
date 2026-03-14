package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.Area;
import net.ooder.agent.client.iot.Place;
import net.ooder.agent.client.iot.ZNode;
import  net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtArea implements Area {

    private String areaid;
    private String placeid;
    private String name;
    private String memo;
    private Set<String> sensorids;

    public CtArea(Area rmArea) {
	this.areaid = rmArea.getAreaid();
	this.placeid = rmArea.getPlaceid();
	this.name = rmArea.getName();
	this.memo = rmArea.getMemo();
	this.sensorids = rmArea.getSensorIds();
		CtIotCacheManager.getInstance().areaCache.put(areaid,this);

    }

    public String getAreaid() {
	return areaid;
    }

    public void setAreaid(String areaid) {
	this.areaid = areaid;
    }

    public String getPlaceid() {
	return placeid;
    }

    public void setPlaceid(String placeid) {
	this.placeid = placeid;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getMemo() {
	return memo;
    }

    public void setMemo(String memo) {
	this.memo = memo;
    }

	@JSONField(serialize=false)
	
    public Place getPlace() {
	Place place = null;
	try {
	    place = CtIotCacheManager.getInstance().getPlaceById(this.getPlaceid());
	} catch (JDSException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return place;
    }

    @Override
    public Set<String> getSensorIds() {
	if (sensorids == null) {
	    sensorids = new LinkedHashSet<>();
	}

	return sensorids;
    }

	@JSONField(serialize=false)
	
    public List<ZNode> getSensors() {
		Set<String> znodeids = this.getSensorIds();
	List<ZNode> znodes = new ArrayList<ZNode>();
	for (String znodeid : znodeids) {
	    try {
		znodes.add(CtIotCacheManager.getInstance().getZNodeById(znodeid));
	    } catch (JDSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return znodes;
    }

}
