package net.ooder.agent.client.command.task;

import net.ooder.agent.client.command.Command;
import  net.ooder.common.ConfigCode;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.context.MinServerActionContextImpl;

import java.util.concurrent.Callable;

public class LightCommandTask implements Callable<Command>  {
	private Integer value;

	private String sensorieee;

	private MinServerActionContextImpl autoruncontext;

	private ConfigCode configCode;

	private String gatewayieee;

	public LightCommandTask(String gatewayieee,String sensorieee, Integer value,String systemCode) {
		this.gatewayieee=gatewayieee;
		JDSContext context=JDSActionContext.getActionContext();
		this.autoruncontext=new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());

		autoruncontext.setParamMap(context.getContext());
		this.configCode=autoruncontext.getConfigCode();
		if (context.getSessionId() != null) {
			autoruncontext.setSessionId(context.getSessionId());
			autoruncontext.getSession().put("sessionHandle",context.getSession().get("sessionHandle"));
		}
		autoruncontext.setSessionMap(context.getSession());		
		this.value = value;
		this.sensorieee = sensorieee;
		
	}

	

	public Command call() throws Exception {
       JDSActionContext.setContext(autoruncontext);
		
		CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);
		JDSSessionHandle handle=(JDSSessionHandle) autoruncontext.getSession().get("sessionHandle");
		 JDSActionContext.setContext(new RunableActionContextImpl());
		
		 JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, configCode);
		 JDSClientService client=JDSServer.getInstance().getJDSClientService(handle, configCode);

		CommandClient commandClient = msgEngine.getCommandClientByieee(gatewayieee);
		return commandClient.sendLightCommand(sensorieee, value).get();
	
	}

}
