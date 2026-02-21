package net.ooder.agent.client.iot.json.device;

import com.alibaba.fastjson.JSON;

public class LockPassword {
	Integer modelNum;
	Long startTime;
	Long endTime;
	Integer type;
	String password;
	public String getPassword() {
		return password;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Integer getModelNum() {
		return modelNum;
	}
	public void setModelNum(Integer modelNum) {
		this.modelNum = modelNum;
	}
	

	public static void main(String[] args) {
		LockPassword password=JSON.parseObject("{'password':111}", LockPassword.class);
		System.out.println(JSON.toJSON(password));
	}
	
}
