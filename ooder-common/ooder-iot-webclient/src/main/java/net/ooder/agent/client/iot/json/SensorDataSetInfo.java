package net.ooder.agent.client.iot.json;

import java.io.Serializable;
import java.util.List;

public class SensorDataSetInfo implements Serializable {

	Long stime;

	Long etime;

	String timeunit;

	List<String> values;

	String unit;

	public Long getEtime() {
		return etime;
	}

	public void setEtime(Long etime) {
		this.etime = etime;
	}

	public Long getStime() {
		return stime;
	}

	public void setStime(Long stime) {
		this.stime = stime;
	}

	public String getTimeunit() {
		return timeunit;
	}

	public void setTimeunit(String timeunit) {
		this.timeunit = timeunit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
