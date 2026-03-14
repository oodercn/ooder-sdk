package net.ooder.agent.client.iot.json;


import net.ooder.agent.client.iot.ZNode;

public class PMSSensorInfo extends SensorInfo{
	Integer gatewayStatus=0;
	String gwsn;
	
	Integer signal=100;
	

	PMSSensorInfo(){
		
	}
	
	public PMSSensorInfo(ZNode znode){
		super(znode);
		if (znode!=null && znode.getParentNode() != null ){
			gatewayStatus=znode.getParentNode().getStatus().getCode();
			
			gwsn=znode.getEndPoint().getDevice().getRootDevice().getSerialno();
		}
		String lqi=(String) znode.getEndPoint().getCurrvalue().get("lqi");
		if (lqi!=null){
			if (znode.getEndPoint().getDevice().getFactory().equals("huohe")){
				signal=Integer.valueOf(lqi)*50;
			}else{
				signal=Integer.valueOf(lqi);
			}
		}
	}

	public Integer getGatewayStatus() {
		return gatewayStatus;
	}

	public void setGatewayStatus(Integer gatewayStatus) {
		this.gatewayStatus = gatewayStatus;
	}

	public Integer getSignal() {
		return signal;
	}

	public void setSignal(Integer signal) {
		this.signal = signal;
	}
	
	public String getGwsn() {
	    return gwsn;
	}

	public void setGwsn(String gwsn) {
	    this.gwsn = gwsn;
	}

	
}
