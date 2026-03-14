package net.ooder.agent.client.command.task;

import net.ooder.agent.client.iot.HomeException;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.msg.Msg;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.common.ConfigCode;

import java.util.concurrent.Callable;

public class ClearPasswordCommandTask implements Callable<Msg> {

	private String serialno;

	private MinServerActionContextImpl autoruncontext;

	private ConfigCode configCode;

	private String gatewayieee;

	public ClearPasswordCommandTask(String gatewayieee,String serialno, String systemCode) {
		JDSContext context = JDSActionContext.getActionContext();
		this.gatewayieee=gatewayieee;
		this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
		autoruncontext.setParamMap(context.getContext());
		this.configCode = autoruncontext.getConfigCode();
		if (context.getSessionId() != null) {
			autoruncontext.setSessionId(context.getSessionId());
			autoruncontext.getSession().put("sessionHandle",
					context.getSession().get("sessionHandle"));
		}
		autoruncontext.setSessionMap(context.getSession());

		this.serialno = serialno;

	}

	public Msg call() throws Exception {
		Msg msg = null;

		try {
			JDSActionContext.setContext(autoruncontext);

			CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);
			JDSSessionHandle handle=(JDSSessionHandle) autoruncontext.getSession().get("sessionHandle");
			 JDSActionContext.setContext(new RunableActionContextImpl());
			
			 JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, configCode);
			 JDSClientService client=JDSServer.getInstance().getJDSClientService(handle, configCode);

			 CommandClient commandClient= msgEngine.getCommandClientByieee(gatewayieee);
			 commandClient.sendClearPasswordCommand(serialno).get();
		} catch (HomeException e) {
			e.printStackTrace();
		}
		return msg;
	}

}
