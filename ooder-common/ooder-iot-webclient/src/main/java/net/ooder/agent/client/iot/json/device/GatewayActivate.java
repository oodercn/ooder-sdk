package net.ooder.agent.client.iot.json.device;



public class GatewayActivate {
	
	
    String keyword;
	
	String gatewayAccount;
	
	String mainServerUrl;
	
   
	String gwmServerUrl;
	
	String commandServerUrl;
	
	
	public GatewayActivate(){
		
	}
	
			
	public GatewayActivate(Gateway gateway){
		this.keyword=gateway.getKeyword();
		this.gatewayAccount=gateway.getGatewayAccount();
		this.mainServerUrl=gateway.getMainServerUrl();
		this.commandServerUrl=gateway.getCommandServerUrl();
		this.gwmServerUrl=gateway.getGwmServerUrl();
	
	}


	public String getCommandServerUrl() {
		return commandServerUrl;
	}


	public void setCommandServerUrl(String commandServerUrl) {
		this.commandServerUrl = commandServerUrl;
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


	public String getMainServerUrl() {
		return mainServerUrl;
	}


	public void setMainServerUrl(String mainServerUrl) {
		this.mainServerUrl = mainServerUrl;
	}


	public String getGwmServerUrl() {
		return gwmServerUrl;
	}


	public void setGwmServerUrl(String gwmServerUrl) {
		this.gwmServerUrl = gwmServerUrl;
	}








}
