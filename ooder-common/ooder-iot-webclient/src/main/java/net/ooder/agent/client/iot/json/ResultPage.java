package net.ooder.agent.client.iot.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultPage implements Serializable{

	int pagesize;
	int currentPage;
	List<SensorDataInfo> sensorDataList=new ArrayList<SensorDataInfo>();
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<SensorDataInfo> getSensorDataList() {
		return sensorDataList;
	}
	public void setSensorDataList(List<SensorDataInfo> sensorDataList) {
		this.sensorDataList = sensorDataList;
	}
}
