package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.Sensortype;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import  net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtDevice implements Device {

    private String deviceid;

    private Integer devicetype;
    private String serialno;

    private DeviceStatus states;
    private String macaddress;
    private String batch;
    private String areaid;
    private String placeid;

    private String factory;

    private String bindingaccount;
    private String appaccount;
    private String channel;
    private Long addtime;
    private String name;
    private String battery;
    private String subsyscode;

    private Long lastlogintime;

    private Set<String> childDeviceIds = new LinkedHashSet<>();

    private Set<String> deviceEndPointIds = new LinkedHashSet<>();


    public CtDevice(Device rmDevice) {
        this.deviceid = rmDevice.getDeviceid();

        this.devicetype = rmDevice.getDevicetype();
        this.serialno = rmDevice.getSerialno();
        this.addtime = rmDevice.getAddtime();

        this.subsyscode = rmDevice.getSubsyscode();
        this.lastlogintime = rmDevice.getLastlogintime();


        this.states = rmDevice.getStates();
        this.macaddress = rmDevice.getMacaddress();
        this.batch = rmDevice.getBatch();
        this.areaid = rmDevice.getAreaid();
        this.placeid = rmDevice.getPlaceid();

        this.factory = rmDevice.getFactory();

        this.bindingaccount = rmDevice.getBindingaccount();
        this.appaccount = rmDevice.getAppaccount();
        this.channel = rmDevice.getChannel();

        this.name = rmDevice.getName();
        this.battery = rmDevice.getBattery();

        if (rmDevice.getChildDeviceIds() != null) {
            this.childDeviceIds = new LinkedHashSet<>(rmDevice.getChildDeviceIds());
        }

        if (deviceEndPointIds != null) {
            this.deviceEndPointIds = new LinkedHashSet<>(rmDevice.getDeviceEndPointIds());
        }


        CtIotCacheManager.getInstance().deviceCache.put(deviceid, this);

    }


    @Override
    public Long getLastlogintime() {
        return lastlogintime;
    }

    @Override
    public void setLastlogintime(Long lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public Long getAddtime() {
        return addtime;
    }

    public void setAddtime(Long addtime) {
        this.addtime = addtime;
    }

    public String getBindingaccount() {
        return bindingaccount;
    }

    public void setBindingaccount(String bindingaccount) {
        this.bindingaccount = bindingaccount;
    }

    public String getAppaccount() {
        return appaccount;
    }

    public void setAppaccount(String appaccount) {
        this.appaccount = appaccount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getSubsyscode() {
        return subsyscode;
    }

    @JSONField(serialize = false)

    public List<DeviceEndPoint> getDeviceEndPoints() {

        List<DeviceEndPoint> endPoints = new ArrayList<DeviceEndPoint>();
        Set<String> childIds = this.getDeviceEndPointIds();

        for (String endpointId : childIds) {
            try {
                DeviceEndPoint endPoint = CtIotFactory.getCtIotService().getEndPointById(endpointId);
                if (endPoint != null) {
                    endPoints.add(endPoint);
                }

            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return endPoints;

    }

    @JSONField(serialize = false)

    public Sensortype getSensortype() {
        Sensortype sensortype = null;
        try {
            sensortype = CtIotFactory.getCtIotService().getSensorTypesByNo(this.getDevicetype().toString());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sensortype;

    }

    @JSONField(serialize = false)
    public List<ZNode> getAllZNodesRecursivelys() {
        List<ZNode> znodeList = new ArrayList<ZNode>();
        if (!this.getDeviceid().equals(this.getBindingaccount())) {
            List<DeviceEndPoint> endPoints = this.getDeviceEndPoints();
            for (DeviceEndPoint endPoint : endPoints) {
                List<ZNode> nodes = endPoint.getAllZNodes();
                znodeList.addAll(nodes);
            }
        } else {
            for (Device device : this.getChildDevices()) {
                znodeList.addAll(device.getAllZNodes());
            }
        }
        return znodeList;
    }

    @JSONField(serialize = false)
    public List<ZNode> getAllZNodes() {
        List<ZNode> znodeList = new ArrayList<ZNode>();
        List<DeviceEndPoint> endPoints = this.getDeviceEndPoints();
        for (DeviceEndPoint endPoint : endPoints) {
            List<ZNode> nodes = endPoint.getAllZNodes();
            znodeList.addAll(nodes);
        }

        return znodeList;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if ((object != null) && ((object instanceof Device))) {
            return getDeviceid().equals(((Device) object).getDeviceid());
        }
        return false;
    }

    @JSONField(serialize = false)

    public List<Device> getSub() {

        if (this.getChildDevices().size() > 0) {
            return this.getChildDevices();
        } else {
            return null;
        }

    }

    @JSONField(serialize = false)

    public List<Device> getChildDevices() {
        List<Device> devices = new ArrayList<Device>();
        Set<String> childIds = this.getChildDeviceIds();

        for (String deviceId : childIds) {
            try {
                if (!deviceId.equals(this.getBindingaccount())) {
                    Device device = CtIotCacheManager.getInstance().getDeviceById(deviceId);
                    if (device != null && !device.getStates().equals(DeviceStatus.DELETE)) {
                        devices.add(device);
                    }

                }
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return devices;
    }

    @JSONField(serialize = false)

    public Device getRootDevice() {
        Device rootDevice = null;
        if (this.getBindingaccount().equals(this.getDeviceid())) {
            rootDevice = this;
        } else {
            try {
                rootDevice = CtIotCacheManager.getInstance().getDeviceById(this.getBindingaccount());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return rootDevice;

    }


    public Set<String> getChildDeviceIds() {
        return childDeviceIds;
    }

    public void setChildDeviceIds(Set<String> childDeviceIds) {
        this.childDeviceIds = childDeviceIds;
    }

    public DeviceStatus getStates() {
        return states;
    }

    public Integer getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(Integer devicetype) {
        this.devicetype = devicetype;
    }

    public Set<String> getDeviceEndPointIds() {
        return deviceEndPointIds;
    }

    public void setDeviceEndPointIds(Set<String> deviceEndPointIds) {
        this.deviceEndPointIds = deviceEndPointIds;
    }

    public void setStates(DeviceStatus states) {
        this.states = states;
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

    public void setSubsyscode(String subsyscode) {
        this.subsyscode = subsyscode;
    }

}
