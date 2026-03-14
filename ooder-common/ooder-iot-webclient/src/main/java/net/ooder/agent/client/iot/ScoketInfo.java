/**
 * 
 */
package net.ooder.agent.client.iot;

/**
 * @author wenzhang
 *
 */
public class ScoketInfo {

	String endReading="0";
	
	String StartReading="0";
	
	Integer number=0;
	
	Integer Status=1;
	
	public ScoketInfo(){
		
	}

	public String getEndReading() {
		return endReading;
	}

	public void setEndReading(String endReading) {
		this.endReading = endReading;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getStartReading() {
		return StartReading;
	}

	public void setStartReading(String startReading) {
		StartReading = startReading;
	}

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}
	
	
}
