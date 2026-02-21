package net.ooder.agent.client.iot.json.device;

import java.util.ArrayList;
import java.util.List;

public class SensorListInfo {

	List<Sensor> sensors=new ArrayList<Sensor>();
	
	public SensorListInfo(){
		
	}
	
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

}
