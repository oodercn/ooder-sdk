package net.ooder.agent.client.iot.ct;

import net.ooder.agent.client.iot.Sensortype;
import net.ooder.agent.client.iot.enums.ZNodeZType;

public class CtSensortype implements Sensortype {
    String typeid;
    String deviceid;
    Integer type;
    String name;
    String icon;
    String color;
    String hisdataurl;
    String alarmurl;
    String datalisturl;
    String htmltemp;
    ZNodeZType znodetype;
    private String icontemp;


    @Override
    public String getIcontemp() {
        return icontemp;
    }

    @Override
    public void setIcontemp(String icontemp) {
        this.icontemp=icontemp;

    }
   public CtSensortype(){

    }

    public CtSensortype(Sensortype sensorType) {
        this.typeid = sensorType.getTypeid();
        this.deviceid = sensorType.getDeviceid();
        this.icontemp=sensorType.getIcontemp();
        this.type = sensorType.getType();
        this.name = sensorType.getName();
        this.color = sensorType.getColor();
        this.icon = sensorType.getIcon();
        this.hisdataurl = sensorType.getHisdataurl();
        this.htmltemp = sensorType.getHtmltemp();
        this.datalisturl = sensorType.getDatalisturl();
        this.znodetype=sensorType.getZnodetype();
        CtIotCacheManager.getInstance().sensorTypeCache.put(typeid,this);
    }


    public String getHtmltemp() {
        return htmltemp;
    }

    public void setHtmltemp(String htmltemp) {
        this.htmltemp = htmltemp;
    }
    @Override
    public String getTypeid() {
        return typeid;
    }

    @Override
    public void setTypeid(String typeid) {
       this.typeid=typeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDeviceid() {
        return deviceid;
    }

    @Override
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }


    public String getAlarmurl() {
        return alarmurl;
    }

    public void setAlarmurl(String alarmurl) {
        this.alarmurl = alarmurl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDatalisturl() {
        return datalisturl;
    }

    public void setDatalisturl(String datalisturl) {
        this.datalisturl = datalisturl;
    }

    @Override
    public void setType(Integer type) {

        this.type=type;
    }

    @Override
    public Integer getType() {
        return type;
    }

    @Override
    public void setZnodetype(ZNodeZType type) {

        this.znodetype=type;
    }

    @Override
    public ZNodeZType getZnodetype() {
        return znodetype;
    }

    public String getHisdataurl() {
        return hisdataurl;
    }

    public void setHisdataurl(String hisdataurl) {
        this.hisdataurl = hisdataurl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
