package net.ooder.agent.client.home.udp;

import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.msg.SensorMsg;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.OrgManagerFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class SendAlarmSMSMsg implements Runnable{
	private SensorMsg msg;

	public SensorMsg getMsg() {
		return msg;
	}

	public void setMsg(SensorMsg msg) {
		this.msg = msg;
		
	}

	public SendAlarmSMSMsg(SensorMsg msg){
	
		this.msg=msg;
	
	}
	
	public void send(Person toPerson, String systemCode) {
			try {
				
				ZNode znode=CtIotCacheManager.getInstance().getZNodeById(msg.getGatewayId());
				ZNode cznode=CtIotCacheManager.getInstance().getZNodeById(msg.getSensorId());
			
				Device device= CtIotCacheManager.getInstance().getDeviceById(znode.getDeviceid());
				String mobile=toPerson.getAccount();
				if (device.getBindingaccount().equals(toPerson)){
					mobile=device.getAppaccount();
				}
				String msgStr="[{ \"gatewayId\":\""+device.getSerialno()+"\", \"message\": \""+cznode.getName()+"设备【" +msg.getTitle()+"】\", \"mobiles\":\""+mobile+"\"}]";
				Request request = Request.Post("https://api.tujia.com/lock/PushSensorAlarms").bodyString(msgStr,ContentType.APPLICATION_JSON);
				try {
				Content content=	request.execute().returnContent();
				String ruest=content.toString();
				
				System.out.println(msgStr+"  状态["+ruest+"]");
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
		} catch (Exception e) {
			e.printStackTrace();
			//发送消息失败则注销用户SESSION
			
		}

	}
	


	public void run() {
		try {
			Person toPerson = (Person)OrgManagerFactory.getOrgManager()
					.getPersonByID(msg.getReceiver());
			send(toPerson, msg.getSystemCode());
		} catch (PersonNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		String msgStr="[{ \"gatewayId\": \"sdf544444\", \"message\": \"wenzhang\", \"mobiles\": \"112123123;1212312312\"}]";
		Request request = Request.Post("https://api.fvt.tujia.com/lock/PushSensorAlarms").bodyString(msgStr,ContentType.APPLICATION_JSON);
		try {
		Content content=	request.execute().returnContent();
		String ruest=content.toString();
		System.out.println(ruest);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
