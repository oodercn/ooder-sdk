package net.ooder.agent.client.iot;

import java.util.List;
import java.util.Set;

/**
 * Area entity. @author MyEclipse Persistence Tools
 */

public interface Area extends java.io.Serializable {
    
    
    public Set<String> getSensorIds();
    
    public List<ZNode> getSensors();


    public String getAreaid();

    public void setAreaid(String areaid);

    public String getPlaceid();

    public void setPlaceid(String placeid);

    public String getName();

    public void setName(String name);

    public String getMemo();

    public void setMemo(String memo);

    public Place getPlace();

}