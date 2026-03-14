package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum DeviceZoneStatus implements Enumstype{
    
   

    OFF ("OFF",0 ,"解除报警"), 
    
    ON("ON",1, "报警"),



    BURGLAR("BURGLAR",2, "防盗报警"), 
    
    EMERGENCY("EMERGENCY",3,"紧急报警"),

    battery("battery",6, "低电压报警"),
    
    DEVICETROUBLE("DEVICETROUBLE",4,"设备故障"),
    
    FIRE("FIRE",5,"火警Fire");
  
    private String type;

    private String name;

    private Integer code;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    DeviceZoneStatus(String type,Integer code,String name) {
	this.type = type;
	this.name = name;
	this.code=code;

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
	return code.toString();
    }

    public static DeviceZoneStatus fromType(String typeName) {
	for (DeviceZoneStatus type : DeviceZoneStatus.values()) {
	    if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
		return type;
	    }
	}
	return null;
    }
    
    public static DeviceZoneStatus fromCode(Integer code) {
  	for (DeviceZoneStatus type : DeviceZoneStatus.values()) {
  	    if (type.getCode().equals(code)) {
  		return type;
  	    }
  	}
  	return null;
      }

   
}
