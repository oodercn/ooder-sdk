package net.ooder.agent.client.iot.json.device;



public class GatewayErrorReport {
	
	
    String serialno;
	
	String deviceId;
	
	String macno;
	
	String systemCode;
	
	String keyword;
	
	String currVersion;
	
	
	String gatewayAccount;
	
	String mainServerUrl;
	
	String commandServerUrl;
	
	String sessionId;
	
	Integer errorCode;
	
	
	
	public String getCommandServerUrl() {
		return commandServerUrl;
	}

	public void setCommandServerUrl(String commandServerUrl) {
		this.commandServerUrl = commandServerUrl;
	}

	public String getCurrVersion() {
		return currVersion;
	}

	public void setCurrVersion(String currVersion) {
		this.currVersion = currVersion;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getGatewayAccount() {
		return gatewayAccount;
	}

	public void setGatewayAccount(String gatewayAccount) {
		this.gatewayAccount = gatewayAccount;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getMacno() {
		return macno;
	}

	public void setMacno(String macno) {
		this.macno = macno;
	}

	public String getMainServerUrl() {
		return mainServerUrl;
	}

	public void setMainServerUrl(String mainServerUrl) {
		this.mainServerUrl = mainServerUrl;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
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



}
