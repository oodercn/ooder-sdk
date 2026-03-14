package net.ooder.agent.client.iot;

import net.ooder.agent.client.iot.enums.ZNodeZType;

/**
 * HaSensortype entity. @author MyEclipse Persistence Tools
 */

public interface Sensortype extends java.io.Serializable {

    public String getTypeid();

    public void setTypeid(String typeid);

    public String getName();

    public void setName(String name);

    public String getDeviceid();

    public void setDeviceid(String deviceid);

    public String getIcon();

    public void setIcon(String icon);

    public String getColor();

    public void setColor(String color);

    public String getHisdataurl();

    public void setHisdataurl(String hisdataurl);

    public String getAlarmurl();

    public void setAlarmurl(String alarmurl);

    public String getDatalisturl();

    public void setDatalisturl(String datalisturl);

    public void setType(Integer type);

    public Integer getType();

    public void setZnodetype(ZNodeZType type);

    public ZNodeZType getZnodetype();

    public String getHtmltemp();

    public void setHtmltemp(String htmltemp);

    public String getIcontemp();

    public void setIcontemp(String icontemp);

}