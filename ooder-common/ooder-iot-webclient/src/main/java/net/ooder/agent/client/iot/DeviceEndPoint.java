package net.ooder.agent.client.iot;

import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wenzhang
 * 
 */
public interface DeviceEndPoint extends Serializable {

    public Device getDevice();

    public String getDeviceId();



    public String getEndPointId();

    public void setEndPointId(String endPointId);

    public String getEp();

    public void setEp(String ep);

    public String getHadeviceid();

    public void setHadeviceid(String hadeviceid);

    public String getIeeeaddress();

    public void setIeeeaddress(String ieeeaddress);

    public String getName();

    public void setName(String name);

    public String getNwkAddress();

    public void setNwkAddress(String nwkAddress);

    public String getProfileid();

    public Map<DeviceDataTypeKey, String> getCurrvalue();

    public void setProfileid(String profileid);

    public Sensortype getSensortype();

    public void setSensortype(Sensortype sensorType);

    public List<ZNode> getAllZNodes();

    public Set<String> getAllZNodeIds();
    
    public void setCurrvalue(Map<DeviceDataTypeKey, String> currvalue) ;

    public void updateCurrvalue(DeviceDataTypeKey name,  String currvalue) ;

}
