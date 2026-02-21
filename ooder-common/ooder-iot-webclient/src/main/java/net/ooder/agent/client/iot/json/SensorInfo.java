package net.ooder.agent.client.iot.json;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import  net.ooder.common.JDSException;
import  net.ooder.context.JDSActionContext;

public class SensorInfo implements Serializable {
    String id;

    String serialno;

    String alias;

    String battery;

    String typename;

    String typeno;

    String areaname;

    Integer status;

    Integer gwStatus;


    String gwIeee;


    String areaid;

    String placeid;

    String placename;

    String gatewayid;

    String gatewayname;

    Integer isShow = 0;

    Integer isStart = 0;

    String value;

    Integer islink = 0;

    String htmlValue;

    String icon;

    String color;

    public Integer getIslink() {
        return islink;
    }

    public void setIslink(Integer islink) {
        this.islink = islink;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public SensorInfo() {

    }

    public Integer getGwStatus() {
        return gwStatus;
    }

    public void setGwStatus(Integer gwStatus) {
        this.gwStatus = gwStatus;
    }

    public String getGwIeee() {
        return gwIeee;
    }

    public void setGwIeee(String gwIeee) {
        this.gwIeee = gwIeee;
    }

    public SensorInfo(ZNode znode) {

        ZNode gateway = znode.getParentNode();

        Map value = znode.getEndPoint().getCurrvalue();
        JDSActionContext.getActionContext().getContext().put("value", value);

        if (gateway != null) {
            this.gatewayname = gateway.getName();
        }

        if (gateway!=null && gateway.getEndPoint()!=null){
            gwStatus = gateway.getStatus().getCode();
            this.gwIeee = gateway.getEndPoint().getDevice().getSerialno();
        }


        this.gatewayid = znode.getParentid();
        Device device = znode.getEndPoint().getDevice();

        this.alias = znode.getEndPoint().getName();
        this.status = znode.getStatus().getCode();
        this.serialno = znode.getEndPoint().getIeeeaddress();
        this.id = znode.getZnodeid();

        Sensortype sensortype = znode.getEndPoint().getSensortype();
        this.typename = sensortype.getName();
        this.typeno = sensortype.getType().toString();

        if (device != null) {
            DeviceEndPoint endPoint = znode.getEndPoint();
            this.battery = znode.getEndPoint().getDevice().getBattery();
            if (!znode.getSensortype().equals(30) && false) {
                // 门锁处理简化业务
                this.placeid = znode.getEndPoint().getDevice().getPlaceid();
                Place place = null;
                try {
                    place = CtIotFactory.getCtIotService().getPlaceById(placeid);
                } catch (JDSException e) {
                    e.printStackTrace();
                }

                if (place != null) {
                    this.placename = place.getName();
                    this.isShow = 1;
                    if (place.getStart() != null && place.getStart().indexOf(znode.getZnodeid()) > -1) {
                        this.isStart = 0;
                    } else {
                        this.isStart = 1;
                    }

                }

                Area area = null;
                try {
                    area = CtIotFactory.getCtIotService().getAreaById(znode.getEndPoint().getDevice().getAreaid());
                } catch (JDSException e) {
                    e.printStackTrace();
                }
                if (area != null) {
                    this.areaname = area.getName();
                    this.areaid = area.getAreaid();
                }

            }
            this.htmlValue = sensortype.getHtmltemp();
            if (endPoint.getCurrvalue().get("imgUrl") == null) {
                this.icon = sensortype.getIcontemp();
            } else {
                this.icon = endPoint.getCurrvalue().get("imgUrl").toString();
            }

            this.color = sensortype.getColor();

        }

        if (value.get("Status") != null && !value.get("Status").toString().equals("0") && !value.get("Status").toString().equals("1")) {
            value.put("Status", this.getStatus());
        }

        this.value = JSONObject.toJSONString(value);

        JDSActionContext.getActionContext().getContext().remove("value");

    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getGatewayid() {
        return gatewayid;
    }

    public void setGatewayid(String gatewayid) {
        this.gatewayid = gatewayid;
    }

    public String getGatewayname() {
        return gatewayname;
    }

    public void setGatewayname(String gatewayname) {
        this.gatewayname = gatewayname;
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

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getTypeno() {
        return typeno;
    }

    public void setTypeno(String typeno) {
        this.typeno = typeno;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Integer getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart = isStart;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHtmlValue() {
        return htmlValue;
    }

    public void setHtmlValue(String htmlValue) {
        this.htmlValue = htmlValue;
    }

}
