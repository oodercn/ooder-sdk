package net.ooder.agent.client.iot.json.device;

import java.util.UUID;

import  net.ooder.context.JDSActionContext;
import  net.ooder.jds.core.User;


public class GWUser {

	String account;
	String keyword;
	String systemCode;
	String sessionId;
		

	public GWUser(){
		
	}
	public GWUser(User user){		
		this.account=user.getAccount();
		this.keyword=UUID.randomUUID().toString();	
		this.systemCode= JDSActionContext.getActionContext().getSystemCode();
		this.sessionId=JDSActionContext.getActionContext().getSessionId();
	}
	
	

	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	
}
