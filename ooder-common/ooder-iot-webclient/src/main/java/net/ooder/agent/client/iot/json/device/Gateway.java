package net.ooder.agent.client.iot.json.device;



public class Gateway {
	
	String serialno;
	
	String deviceId;
	
	String macno;
	
	String factory;
	
	String keyword;
	
	String version;
	
   
	String gwmServerUrl;
	
	String gatewayAccount;
	
	String mainServerUrl;
	
	String commandServerUrl;
	
	
	
	public Gateway(){
		
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



	public String getMacno() {
		return macno;
	}

	public void setMacno(String macno) {
		this.macno = macno;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}


	public String getCommandServerUrl() {
		return commandServerUrl;
	}

	public void setCommandServerUrl(String commandServerUrl) {
		this.commandServerUrl = commandServerUrl;
	}

	public String getMainServerUrl() {
		return mainServerUrl;
	}

	public void setMainServerUrl(String mainServerUrl) {
		this.mainServerUrl = mainServerUrl;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}






	public String getVersion() {
		return version;
	}






	public void setVersion(String version) {
		this.version = version;
	}






	public String getFactory() {
		return factory;
	}






	public void setFactory(String factory) {
		this.factory = factory;
	}






	public String getGwmServerUrl() {
		return gwmServerUrl;
	}






	public void setGwmServerUrl(String gwmServerUrl) {
		this.gwmServerUrl = gwmServerUrl;
	}








}
