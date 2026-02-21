package net.ooder.agent.client.command.task;

import com.alibaba.fastjson.JSON;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.HomeException;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.common.ConfigCode;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class AddPasswordCommandTask implements Callable<Command> {

    private MinServerActionContextImpl autoruncontext;

    private AppLockPassword lockPassword;


	private ConfigCode configCode;

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, AddPasswordCommandTask.class);

    public AddPasswordCommandTask(AppLockPassword lockPassword, String sysCode) {
	JDSContext context = JDSActionContext.getActionContext();
	this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
	autoruncontext.setParamMap(context.getContext());

	if (context.getSessionId() != null) {
	    autoruncontext.setSessionId(context.getSessionId());
	    autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
	}
	autoruncontext.setSessionMap(context.getSession());
		configCode=autoruncontext.getConfigCode();
	this.lockPassword = lockPassword;

    }

    public Command call() {
	Command command = null;
	try {
	    JDSActionContext.setContext(autoruncontext);
	    CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);
	    JDSSessionHandle handle = (JDSSessionHandle) autoruncontext.getSession().get("sessionHandle");
	    JDSActionContext.setContext(new RunableActionContextImpl());
	    JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, configCode);
	    CommandClient commandClient = msgEngine.getCommandClientByieee(lockPassword.getGwserialno());

	    if (commandClient != null && commandClient.getGWClient().isOnLine()) {
		logger.info("reSend success:" + commandClient + JSON.toJSONString(lockPassword));
			command = commandClient.sendAddLockPasswordCommand(lockPassword).get();

	    } else {
		logger.info("reSend false:" + commandClient + JSON.toJSONString(lockPassword));
	    }

	} catch (HomeException e) {
	    e.printStackTrace();
	} catch (JDSException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return command;
    }

}
