package net.ooder.agent.client.command.task;

import net.ooder.agent.client.iot.HomeException;
import  net.ooder.common.JDSException;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.common.ConfigCode;

public class BindingCommandTask implements Runnable {

	private String sensorieee;

	private String destieee;

	private boolean bing;

	private ConfigCode configCode;

	private MinServerActionContextImpl autoruncontext;

	private String gatewayieee;

	public BindingCommandTask(String gatewayieee,String sensorieee, String destieee, boolean bing,String systemCode) {
		this.gatewayieee=gatewayieee;
		this.sensorieee = sensorieee;
		this.destieee = destieee;
		this.bing = bing;

		JDSContext context = JDSActionContext.getActionContext();
		this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
		autoruncontext.setParamMap(context.getContext());
		this.configCode=autoruncontext.getConfigCode();
		if (context.getSessionId() != null) {
			autoruncontext.setSessionId(context.getSessionId());
			autoruncontext.getSession().put("sessionHandle",
					context.getSession().get("sessionHandle"));
		}
		autoruncontext.setSessionMap(context.getSession());

	}

	public void run() {

		try {
			JDSActionContext.setContext(autoruncontext);
		
			CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);
			JDSSessionHandle handle=(JDSSessionHandle) autoruncontext.getSession().get("sessionHandle");
			 JDSActionContext.setContext(new RunableActionContextImpl());
			
			 JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, configCode);
			 JDSClientService client=JDSServer.getInstance().getJDSClientService(handle, autoruncontext.getConfigCode());

			 CommandClient commandClient= msgEngine.getCommandClientByieee(gatewayieee);
			if (bing) {
				commandClient
						.sendBindDeviceCommand(sensorieee, destieee);
			} else {
				commandClient.sendUNBindDeviceCommand(sensorieee, destieee);
			}

		} catch (HomeException e) {
			e.printStackTrace();
		} catch (JDSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
