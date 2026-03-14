package net.ooder.agent.client.iot;

import net.ooder.agent.client.iot.enums.DeviceStatus;

import java.util.List;
import java.util.Set;

/**
 * Device entity. @author MyEclipse Persistence Tools
 */

public interface Device extends java.io.Serializable {

    public String getDeviceid();

    public  void setDeviceid(String deviceid);

    public Device getRootDevice();

    public Integer getDevicetype();

    public void setDevicetype(Integer devicetype);

    public String getSerialno();

    public void setSerialno(String serialno);

    public DeviceStatus getStates();

    public void setStates(DeviceStatus states);

    public String getMacaddress();

    public void setMacaddress(String macaddress);

    public String getBatch();

    public void setBatch(String batch);

    public String getFactory();

    public void setFactory(String factory);

    public Long getAddtime();

    public void setAddtime(Long addtime);

    public String getBindingaccount();

    public void setBindingaccount(String bindingaccount);

    public String getAreaid();

    public void setAreaid(String areaid);

    public String getPlaceid();

    public void setPlaceid(String placeid);

    public String getChannel();

    public void setChannel(String channel);

    public String getSubsyscode();

    public void setSubsyscode(String subsyscode);

    public String getName();

    public void setName(String name);

    public String getBattery();

    public void setBattery(String battery);

    public List<DeviceEndPoint> getDeviceEndPoints();

    public Set<String> getDeviceEndPointIds();

    public void setDeviceEndPointIds(Set<String> endpointIds);

    public List<ZNode> getAllZNodes();


    public List<Device> getChildDevices();

    public void setChildDeviceIds(Set<String> childDeviceIds);

    public Set<String> getChildDeviceIds();

    public String getAppaccount();

    public void setAppaccount(String appaccount);

    public Sensortype getSensortype();

    public Long getLastlogintime() ;

    public void setLastlogintime(Long lastlogintime) ;

}