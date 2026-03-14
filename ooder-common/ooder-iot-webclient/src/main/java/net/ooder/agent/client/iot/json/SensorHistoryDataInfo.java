package net.ooder.agent.client.iot.json;

import net.ooder.agent.client.iot.Area;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import  net.ooder.common.JDSException;
import  net.ooder.common.util.DateUtility;
import  net.ooder.msg.SensorMsg;

import java.io.Serializable;
import java.util.Date;


public class SensorHistoryDataInfo implements Serializable {
    String value;
    String msgId;
    String datetime;
    String areaname;
    String htmlValue;
    String sensorId;
    String sensorName;

    public SensorHistoryDataInfo() {

    }

    public SensorHistoryDataInfo(SensorMsg msg) {

        this.msgId = msg.getId();
        this.sensorId = msg.getSensorId();


        if (msg.getEventTime() != null) {
            this.setDatetime(DateUtility.formatDate(new Date(msg.getEventTime()), "yyyy-MM-dd HH:mm:ss"));
        } else {
            this.setDatetime(DateUtility.formatDate(new Date(msg.getReceiveTime()), "yyyy-MM-dd HH:mm:ss"));
        }

        this.setHtmlValue(msg.getTitle());
        try {
            ZNode zode = CtIotFactory.getCtIotService().getZNodeById(sensorId);
            if (zode != null) {
                DeviceEndPoint endPoint = zode.getEndPoint();
                this.sensorId = endPoint.getIeeeaddress();
                Area area = null;
                try {
                    area = CtIotFactory.getCtIotService().getAreaById(endPoint.getDevice().getAreaid());
                } catch (JDSException e) {
                    e.printStackTrace();
                }
                if (area != null) {
                    this.areaname = area.getName();
                }
                this.sensorName = (endPoint.getName() == null || endPoint.getName().equals("")) ? endPoint.getSensortype().getName() : endPoint.getName();
            } else {
                this.sensorId = "0000000000";
                this.sensorName = "已删除设备";
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }



//
//        if (msg.getBody().startsWith("{") && msg.getBody().endsWith("}")) {
//            if (msg.getBody() != null) {
//                if (msg.getBody().startsWith("{") && msg.getBody().endsWith("}")) {
//
//
//                }
//            }
//        } else {
//            this.sensorId = msg.getSensorId();
//            this.htmlValue = msg.getBody();
//            this.sensorName = "格式错误";
//        }
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }


    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getHtmlValue() {
        return htmlValue;
    }

    public void setHtmlValue(String htmlValue) {
        this.htmlValue = htmlValue;
    }


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
