/**
 * 
 */

package net.ooder.agent.client.iot.json;

/**
 * @author wenzhang
 *
 */
public class ScoketInfoJson {

	Long startTime ;
	
	String sensorId;
	
	Long endTime;
	
	Integer number=0;
	
	public ScoketInfoJson(){
		
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
	
	
	
}
