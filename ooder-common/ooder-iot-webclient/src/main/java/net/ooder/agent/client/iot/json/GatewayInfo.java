package net.ooder.agent.client.iot.json;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeConstants;
import net.ooder.agent.client.iot.Place;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import  net.ooder.common.JDSException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GatewayInfo implements Serializable {
       String id;

    String serialno;

    String alias;

    String wbaccount;

    DeviceStatus status;

    String placeid;

    String placename;

    String sensornum;

    String userId;


    private NetworkInfo networkInfo;


    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public GatewayInfo() {

    }

    public GatewayInfo(ZNode znode) {
        if (znode != null) {
            this.setAlias(znode.getName());

            this.userId = znode.getCreateuiserid();
            this.id = znode.getZnodeid();
            this.serialno = znode.getEndPoint().getDevice().getSerialno();
            this.status = znode.getStatus();

            this.wbaccount = znode.getEndPoint().getDevice().getBindingaccount();
            this.sensornum = znode.getChildNodeList().size() + "";

            DeviceEndPoint endPoint = znode.getEndPoint();

            String json = (String) endPoint.getCurrvalue().get(
                    HomeConstants.GATEWAYNETWORKINFO);

            if (json != null) {
                Map map = new HashMap();
                map.put("dhcp", DHCPInfo.class);
                networkInfo = (NetworkInfo) JSONObject
                        .parseObject(json, NetworkInfo.class);
            }

            placeid = znode.getEndPoint().getDevice().getPlaceid();

            if (placeid != null) {
                Place place = null;
                try {
                    place = CtIotFactory.getCtIotService().getPlaceById(placeid);
                } catch (JDSException e) {
                    e.printStackTrace();
                }


                if (place != null) {
                    this.placename = place.getName();
                }

            }

        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getSensornum() {
        return sensornum;
    }

    public void setSensornum(String sensornum) {
        this.sensornum = sensornum;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public String getWbaccount() {
        return wbaccount;
    }

    public void setWbaccount(String wbaccount) {
        this.wbaccount = wbaccount;
    }

}
