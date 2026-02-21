package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.Sensortype;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import  net.ooder.common.JDSException;

import java.util.*;

public class CtDeviceEndPoint implements DeviceEndPoint {

    private String deviceId;

    private String endPointId;

    private Sensortype sensortype;

    private String name;

    private String ep;//

    private String profileid;

    private Map<DeviceDataTypeKey, String> value = new HashMap<DeviceDataTypeKey, String>();

    private String hadeviceid;// HA属性

    private String nwkAddress;// 短地址

    private String ieeeaddress;

    private Set<String> allZNodeIds = new LinkedHashSet<>();

    public CtDeviceEndPoint(DeviceEndPoint rmEndPoint) {
        this.ep = rmEndPoint.getEp();
        this.endPointId = rmEndPoint.getEndPointId();
        this.name = rmEndPoint.getName();
        this.deviceId = rmEndPoint.getDeviceId();
        this.sensortype = rmEndPoint.getSensortype();
        this.profileid = rmEndPoint.getProfileid();
        if (rmEndPoint.getCurrvalue() != null) {
            Map<DeviceDataTypeKey, String> valueMap = rmEndPoint.getCurrvalue();
            Iterator<DeviceDataTypeKey> iterator = valueMap.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                if (key instanceof DeviceDataTypeKey) {
                    Object valueObj = valueMap.get(key);
                    if (valueObj != null) {
                        value.put((DeviceDataTypeKey) key, valueObj.toString());
                    }

                } else {
                    Object valueObj = valueMap.get(key.toString());
                    if (valueObj != null) {
                        value.put(DeviceDataTypeKey.fromType(key.toString()), valueObj.toString());
                    }
                }


            }

        }
        this.hadeviceid = rmEndPoint.getHadeviceid();
        this.nwkAddress = rmEndPoint.getNwkAddress();
        this.ieeeaddress = rmEndPoint.getIeeeaddress();
        if (rmEndPoint.getAllZNodeIds() != null) {
            this.allZNodeIds = new LinkedHashSet(rmEndPoint.getAllZNodeIds());
        }

        CtIotCacheManager.getInstance().endpointCache.put(endPointId, this);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEp() {
        return ep;
    }

    public String getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(String endPointId) {
        this.endPointId = endPointId;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    @JSONField(serialize = false)
    public Sensortype getSensortype() {
        try {
            return CtIotFactory.getCtIotService().getSensorTypesByNo(this.sensortype.getType().toString());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSensortype(Sensortype sensortype) {
        this.sensortype = sensortype;
    }

    public String getProfileid() {
        return profileid;
    }

    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    @JSONField(serialize = false)

    public Map<DeviceDataTypeKey, String> getValue() {
        return value;
    }

    public void setValue(Map<DeviceDataTypeKey, String> value) {
        this.value = value;
    }

    public String getHadeviceid() {
        return hadeviceid;
    }

    public void setHadeviceid(String hadeviceid) {
        this.hadeviceid = hadeviceid;
    }

    public String getNwkAddress() {
        return nwkAddress;
    }

    public void setNwkAddress(String nwkAddress) {
        this.nwkAddress = nwkAddress;
    }

    public String getIeeeaddress() {
        return ieeeaddress;
    }

    public void setIeeeaddress(String ieeeaddress) {
        this.ieeeaddress = ieeeaddress;
    }

    public Set<String> getAllZNodeIds() {
        return allZNodeIds;
    }

    public void setAllZNodeIds(Set<String> allZNodeIds) {
        this.allZNodeIds = allZNodeIds;
    }

    @JSONField(serialize = false)

    public Device getDevice() {
        try {
            return CtIotCacheManager.getInstance().getDeviceById(this.getDeviceId());
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @JSONField(serialize = false)
    public List<ZNode> getAllZNodes() {
        List<ZNode> znodeList = new ArrayList<ZNode>();
        for (String znodeId : this.getAllZNodeIds()) {
            try {
                ZNode zNode = CtIotCacheManager.getInstance().getZNodeById(znodeId);
                if (zNode != null) {
                    znodeList.add(zNode);
                }

            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return znodeList;
    }

    @Override
    public String toString() {
        return (this.getName() == null || this.getName().equals("")) ? this.getIeeeaddress() : this.getName();
    }

    @Override
    public Map<DeviceDataTypeKey, String> getCurrvalue() {

        return this.value;
    }

    @Override
    public void setCurrvalue(Map<DeviceDataTypeKey, String> currvalue) {
        // TODO Auto-generated method stub

    }

    public void updateCurrvalue(DeviceDataTypeKey name, String currvalue) {
        CtIotCacheManager.getInstance().updateCurrvalue(endPointId, name.getType(), currvalue);
        value.put(name, currvalue);
    }
}
