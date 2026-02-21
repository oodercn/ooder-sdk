package net.ooder.agent.client.iot;

import java.util.List;
import java.util.Set;

/**
 * Place entity. @author MyEclipse Persistence Tools
 */

public interface Place extends java.io.Serializable {

    public List<Area> getAreas();

    public Set<String> getAreaIds();

    public Set<String> getChildIds();

    public List<Place> getChilders();

    public List<ZNode> getSensors();

    public List<ZNode> getGateways();

    public Set<String> getGatewayIds();

    public String getParentId();

    public void setParentId(String parentid);

    public Place getParent();

    public String getPlaceid();



    public void setPlaceid(String placeid);

    public String getName();

    public void setName(String name);

    public String getUserid();

    public String getOrgid();

    public void setOrgid(String orgid);

    public void setUserid(String userid);

    public List<ZNode> getIndexSensorList();

    public Set<String> getIndexSensorIdList();

    public void setMemo(String memo);

    public void setStart(String start);

    public String getMemo();

    public String getStart();

}