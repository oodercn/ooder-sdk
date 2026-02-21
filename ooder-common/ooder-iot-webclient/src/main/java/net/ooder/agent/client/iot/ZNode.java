package net.ooder.agent.client.iot;

import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;

import java.util.List;
import java.util.Set;

/**
 * Znode entity. @author MyEclipse Persistence Tools
 */

public interface ZNode extends java.io.Serializable {

    public String getZnodeid();

    public void setZnodeid(String znodeid);

    public String getEndPointid();

    public void setEndPointid(String znodeid);

    public String getDeviceid();

    public void setDeviceid(String deviceid);

    public String getParentid();

    public void setParentid(String parentid);

    public String getPanid();

    public void setPanid(String panid);

    public String getMacaddress();

    public void setMacaddress(String macaddress);

    public ZNodeZType getZtype();

    public void setZtype(ZNodeZType ztype);

    public String getProfilepath();

    public void setProfilepath(String profilepath);

    public String getMasterkey();

    public void setMasterkey(String masterkey);

    public String getName();

    public void setName(String name);

    public Integer getChannel();

    public void setChannel(Integer channel);

    public Integer getSensortype();

    public void setSensortype(Integer sensortype);

    public String getZmoduleid();

    public void setZmoduleid(String zmoduleid);

    public String getCreateuiserid();

    public void setCreateuiserid(String createuiserid);

    public DeviceStatus getStatus();

    public void setStatus(DeviceStatus status);

    public List<ZNode> getChildNodeList();

    public Set<String> getChildNodeIdList();

    public DeviceEndPoint getEndPoint();

    public ZNode getParentNode();


}