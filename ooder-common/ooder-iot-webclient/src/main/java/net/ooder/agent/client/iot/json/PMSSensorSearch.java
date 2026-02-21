package net.ooder.agent.client.iot.json;

import java.util.List;

public class PMSSensorSearch {
	
	
	List<String> lockIds;
	Integer pageIndex=0;
	Integer pageSize=10;
	Integer lockStatus=2;   // 2 - 全部; 1: 正常; 0: 离线
	Integer minpower=0;  // 最低电量, battery >= min_power	
	Integer maxpower=100; // 最高电量, battery <= max_power
	
	public List<String> getLockIds() {
		return lockIds;
	}
	public void setLockIds(List<String> lockIds) {
		this.lockIds = lockIds;
	}
	
	public Integer getMaxpower() {
		return maxpower;
	}
	public void setMaxpower(Integer maxpower) {
		this.maxpower = maxpower;
	}
	public Integer getMinpower() {
		return minpower;
	}
	public void setMinpower(Integer minpower) {
		this.minpower = minpower;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getLockStatus() {
		return lockStatus;
	}
	public void setLockStatus(Integer lockStatus) {
		this.lockStatus = lockStatus;
	}


}
