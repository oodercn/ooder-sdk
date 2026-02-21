package net.ooder.agent.client.iot.udp;

import net.ooder.agent.client.command.filter.SendMsgChain;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgAdapter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EsbBeanAnnotation(id="GHUDPMsgAdapter",
		name="消息分发适配器",
		expressionArr="GHUDPMsgAdapter(\"systemCode\",size)",
		desc="UDP发送适配器")
public class GHUDPMsgAdapter implements MsgAdapter {
	
	private static ScheduledExecutorService commandpool= Executors.newScheduledThreadPool(20);

	private String systemCode;
		
	public GHUDPMsgAdapter(String systemCode,Integer _threadPoolSize){

		this.systemCode=systemCode;

		
	} 
	
	

	
	public void submit(Msg msg) {

		SendMsgChain msgRun = new SendMsgChain(msg);
		try {
			commandpool.submit(msgRun).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}




	public void stop(Msg msg) {
		 commandpool.shutdownNow();
	}

}