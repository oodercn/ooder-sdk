package net.ooder.agent.client.iot.json.device;



public class GatewayRegister {
	
	
	String deviceId;
	
	String macno;
	
	String gatewayAccount;
	
	public GatewayRegister(){
		
	}
	
			
	public GatewayRegister(Gateway gateway){
		this.macno=gateway.getSerialno();
		this.deviceId=gateway.getDeviceId();
	    this.gatewayAccount=gateway.getGatewayAccount();
	}

	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	public String getMacno() {
		return macno;
	}

	public void setMacno(String macno) {
		this.macno = macno;
	}


	public String getGatewayAccount() {
		return gatewayAccount;
	}


	public void setGatewayAccount(String gatewayAccount) {
		this.gatewayAccount = gatewayAccount;
	}

	

}
