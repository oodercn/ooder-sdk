package net.ooder.agent.client.iot.json.device;



public class SensorData {
	
	String serialno;
		
	String attributename;//Current:电流  Voltate:电压  Power:电能  Energy:功率  Temperature:温度  Humidity:湿度

	String status;
	
	String time;
	
	String modeid;
	
	String value;
	
	
	
	public SensorData( ){
		
	}


	public String getStatus() {
		return status;
	}

	

	public void setStatus(String status) {
		this.status = status;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public String getAttributename() {
		return attributename;
	}

	public void setAttributename(String attributename) {
		this.attributename = attributename;
	}
	public String getSerialno() {
		return serialno;
	}
	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}
	
	public String getModeid() {
		return modeid;
	}
	public void setModeid(String modeid) {
		this.modeid = modeid;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


}
