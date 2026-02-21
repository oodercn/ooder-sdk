package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.Alarm;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.Scene;

public class CtAlarm implements Alarm {

    private String alarmid;
    private String sensorid;
    private String starttime;
    private String endtime;
    private String cycle;
    private String alertcontent;
    private String comfort;
    private Integer istart;
    private Integer operatestatus;
    private Integer devicestatus;
    private Integer delaytime;
    private String name;
    private String sceneid;

     CtAlarm(String  alarmid){
         this.alarmid = alarmid;
    }
    public CtAlarm(Alarm alarm) {
        this.alarmid = alarm.getAlarmid();
        this.sensorid = alarm.getSensorid();
        this.starttime = alarm.getStarttime();
        this.endtime = alarm.getEndtime();
        this.cycle = alarm.getCycle();
        this.alertcontent = alarm.getAlertcontent();
        this.comfort = alarm.getComfort();
        this.istart = alarm.getIstart();
        this.operatestatus = alarm.getOperatestatus();
        this.devicestatus = alarm.getDevicestatus();
        this.sceneid=alarm.getSceneid();
        this.delaytime = alarm.getDelaytime();
        this.name=alarm.getName();
        CtIotCacheManager.getInstance().alarmCache.put(alarmid,this);
    }


    public String getAlarmid() {
        return this.alarmid;
    }

    public void setAlarmid(String alarmid) {
        this.alarmid = alarmid;
    }



    public String getStarttime() {
        return this.starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return this.endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCycle() {
        return this.cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getAlertcontent() {
        return this.alertcontent;
    }

    public void setAlertcontent(String alertcontent) {
        this.alertcontent = alertcontent;
    }

    public String getComfort() {
        return this.comfort;
    }

    public void setComfort(String comfort) {
        this.comfort = comfort;
    }

    public Integer getIstart() {
        return this.istart;
    }

    public void setIstart(Integer istart) {
        this.istart = istart;
    }

    public Integer getOperatestatus() {
        return this.operatestatus;
    }

    public void setOperatestatus(Integer operatestatus) {
        this.operatestatus = operatestatus;
    }

    public Integer getDevicestatus() {
        return this.devicestatus;
    }

    public void setDevicestatus(Integer devicestatus) {
        this.devicestatus = devicestatus;
    }



    public Integer getDelaytime() {
        return this.delaytime;
    }

    public void setDelaytime(Integer delaytime) {
        this.delaytime = delaytime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(serialize=false)
 
    public Scene getScene() {
        try {
            return  CtIotCacheManager.getInstance().getSceneById(this.getSceneid());
        } catch (HomeException e) {
         e.printStackTrace();
        }
        return null;

    }


    public String getSceneid() {
        return sceneid;
    }

    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    @Override
    public Alarm clone(String alarmId) {
        Alarm alarm=new CtAlarm(alarmId);
        alarm.setAlertcontent(alertcontent);
        alarm.setComfort(comfort);
        alarm.setCycle(cycle);
        alarm.setDelaytime(delaytime);
        alarm.setEndtime(endtime);
        alarm.setDevicestatus(devicestatus);
        alarm.setIstart(istart);
        alarm.setOperatestatus(operatestatus);
        alarm.setSceneid(sceneid);
        alarm.setStarttime(starttime);
        alarm.setSensorid(sensorid);
        alarm.setName(name);
        return alarm;
    }

}
