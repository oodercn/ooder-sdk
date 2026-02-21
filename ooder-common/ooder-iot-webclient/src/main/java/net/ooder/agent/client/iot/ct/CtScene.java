package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.Scene;
import net.ooder.agent.client.iot.ZNode;

public class CtScene implements Scene {
    public CtScene(String sceneid) {
        this.sceneid = sceneid;
    }
    public CtScene(Scene scene) {
        this.sceneid = scene.getSceneid();
        this.sensorid = scene.getSensorid();
        this.intvalue = scene.getIntvalue();
        this.objvalue = scene.getObjvalue();
        this.name = scene.getName();
        this.status = scene.getStatus();
        CtIotCacheManager.getInstance().sceneCache.put(sceneid,this);
    }

    private String sceneid;
    private String sensorid;
    private Integer intvalue;
    private String objvalue;
    private String name;
    private Integer status;


    public String getSceneid() {
        return this.sceneid;
    }

    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }


    public Integer getIntvalue() {
        return this.intvalue;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public void setIntvalue(Integer intvalue) {
        this.intvalue = intvalue;
    }

    public String getObjvalue() {
        return this.objvalue;
    }

    public void setObjvalue(String objvalue) {
        this.objvalue = objvalue;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JSONField(serialize=false)
    
    public ZNode getZnode() {
        ZNode znode=null;
        try {
            znode= CtIotCacheManager.getInstance().getZNodeById(this.getSensorid());
        } catch (HomeException e) {
            e.printStackTrace();
        }
        return znode;
    }

}
