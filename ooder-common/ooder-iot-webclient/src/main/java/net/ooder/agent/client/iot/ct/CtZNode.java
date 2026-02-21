package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;
import  net.ooder.common.JDSException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtZNode implements ZNode, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String znodeid;

    private String deviceid;

    private String endPointid;

    private String parentid;
    private String panid;
    private String macaddress;
    private ZNodeZType ztype;

    private String profilepath;
    private String masterkey;
    private String name;
    private DeviceStatus status;
    private Integer channel;
    private Integer sensortype;
    private String zmoduleid;
    private String createuiserid;
    private Set<String> childNodeIdList = new LinkedHashSet<String>();

    public CtZNode(ZNode znode) {

        this.znodeid = znode.getZnodeid();

        this.deviceid = znode.getDeviceid();

        this.endPointid = znode.getEndPointid();

        this.parentid = znode.getParentid();
        this.panid = znode.getPanid();
        this.macaddress = znode.getMacaddress();
        this.ztype = znode.getZtype();
        this.profilepath = znode.getProfilepath();
        this.masterkey = znode.getMasterkey();
        this.name = znode.getName();

        this.channel = znode.getChannel();
        this.sensortype = znode.getSensortype();

        this.zmoduleid = znode.getZmoduleid();
        this.status = znode.getStatus();


        if (znode.getChildNodeIdList() != null) {
            this.childNodeIdList = znode.getChildNodeIdList();
        }
        this.createuiserid = znode.getCreateuiserid();

        CtIotCacheManager.getInstance().znodeCache.put(znodeid, this);

    }

    public String getZnodeid() {
        return znodeid;
    }

    public void setZnodeid(String znodeid) {
        this.znodeid = znodeid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getPanid() {
        return panid;
    }

    public void setPanid(String panid) {
        this.panid = panid;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getProfilepath() {
        return profilepath;
    }

    public void setProfilepath(String profilepath) {
        this.profilepath = profilepath;
    }

    public String getMasterkey() {
        return masterkey;
    }

    public void setMasterkey(String masterkey) {
        this.masterkey = masterkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSensortype() {
        return sensortype;
    }

    public void setSensortype(Integer sensortype) {
        this.sensortype = sensortype;
    }


    public String getZmoduleid() {
        return zmoduleid;
    }

    public void setZmoduleid(String zmoduleid) {
        this.zmoduleid = zmoduleid;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }


    public void setCreateuiserid(String createuiserid) {
        this.createuiserid = createuiserid;
    }


    @JSONField(serialize = false)
    public List<ZNode> getChildNodeList() {
        List<ZNode> childNodes = new ArrayList<ZNode>();

        Set<String> ichildNodeIds = this.getChildNodeIdList();

        try {
            childNodes = CtIotCacheManager.getInstance().loadAllZNode(ichildNodeIds);
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return childNodes;
    }

    @JSONField(serialize = false)

    public Device getDevice() {
        Device device = null;
        try {
            device = CtIotCacheManager.getInstance().getDeviceById(this.getDeviceid());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return device;
    }

    @JSONField(serialize = false)

    public ZNode getParentNode() {
        ZNode znode = null;
        try {
            znode = CtIotCacheManager.getInstance().getZNodeById(this.getParentid());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return znode;
    }

    public Integer getChannel() {
        return channel;
    }


    public String getCreateuiserid() {
        if (this.getEndPoint() != null && getParentNode() != null && this.getEndPoint().getSensortype() != null && !this.getEndPoint().getSensortype().getType().equals(0)) {
            this.createuiserid = getParentNode().getCreateuiserid();
        }
        return createuiserid;
    }

    @Override
    public String toString() {
        return name;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object != null && object instanceof ZNode) {
            return this.getZnodeid() == ((ZNode) object).getZnodeid();
        } else {
            return false;
        }
    }

    public DeviceEndPoint getEndPoint() {
        DeviceEndPoint endPoint = null;
        try {
            endPoint = CtIotCacheManager.getInstance().getEndPointById(this.getEndPointid());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return endPoint;
    }

    public ZNodeZType getZtype() {
        if (ztype == null || ztype.equals(ZNodeZType.Other)) {
            ztype = this.getEndPoint().getSensortype().getZnodetype();
        }
        return ztype;
    }

    public void setZtype(ZNodeZType ztype) {
        this.ztype = ztype;
    }

    public DeviceStatus getStatus() {
//        CommandClient commandClient = CtIotFactory.getCommandClient(this.getDevice().getRootDevice().getSerialno());
        if (this.getDevice().getRootDevice().getStates().equals(DeviceStatus.OFFLINE) && this.getEndPoint().getDevice().getStates().equals(DeviceStatus.ONLINE)) {
            return DeviceStatus.OFFLINE;
        }
        return this.getEndPoint().getDevice().getStates();
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public String getEndPointid() {
        return endPointid;
    }

    public void setEndPointid(String endPointid) {
        this.endPointid = endPointid;
    }

    public Set<String> getChildNodeIdList() {
        Set<String> childNodeIds = new LinkedHashSet<>();
        if (getZtype().equals(ZNodeZType.GATEWAY)) {
            Device rootDevice = this.getDevice();
            List<ZNode> znodeList = new ArrayList<ZNode>();
            for (Device device : rootDevice.getChildDevices()) {
                znodeList.addAll(device.getAllZNodes());
            }
            for (ZNode zNode : znodeList) {
                if (zNode.getParentid().equals(this.znodeid)) {
                    childNodeIds.add(zNode.getZnodeid());
                }
            }
        }
        return childNodeIds;
    }

    public void setChildNodeIdList(Set<String> childNodeIdList) {
        this.childNodeIdList = childNodeIdList;
    }


}
