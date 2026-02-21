package net.ooder.agent.client.home.udp;

import  net.ooder.common.JDSException;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.SensorMsg;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.common.ConfigCode;

public class SendAppMsg implements Runnable {
	private SensorMsg msg;

	private JDSSessionHandle handle;

	private RunableActionContextImpl autoruncontext;

	private boolean isSuccess = false;

	public SensorMsg getMsg() {
		return msg;
	}

	public void setMsg(SensorMsg msg) {
		this.msg = msg;

	}

	public SendAppMsg(SensorMsg msg, JDSSessionHandle handle, JDSContext context) {
		this.autoruncontext = new RunableActionContextImpl();
		this.handle = handle;
		this.msg = msg;

		JDSActionContext.setContext(context);
		autoruncontext.setParamMap(context.getContext());
		if (context.getSessionId() != null) {
			autoruncontext.setSessionId(context.getSessionId());
			autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
		}
		autoruncontext.setSessionMap(context.getSession());
	}

	public void send(Person toPerson, ConfigCode configCode) {
		try {
			if (!isSuccess) {
				JDSClientService client = JDSServer.getInstance().getJDSClientService(handle, configCode);
				if (client != null && client.getConnectInfo() != null) {
					AppClient appclient = HomeServer.getInstance().getAppClient(client);
					MsgType type = MsgType.fromType( msg.getType());

					if (type.equals(MsgType.ALARM)) {
						isSuccess = appclient.sendAlarmMsg(msg);
					} else if (type.equals(MsgType.SENSOR)) {
						isSuccess = appclient.sendDataMsg(msg);
					} else if (type.equals(MsgType.MSG)) {
						isSuccess = appclient.sendSystemMsg(msg);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// 发送消息失败则注销用户SESSION
			try {
				JDSServer.getInstance().disconnect(handle);
				// JDSServer.getInstance().getJDSClientService(handle,
				// msg.getSubSystemId()).disconnect();
			} catch (JDSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void run() {
		try {
			Person toPerson =OrgManagerFactory.getOrgManager().getPersonByID(msg.getReceiver());
			send(toPerson,autoruncontext.getConfigCode());
		} catch (PersonNotFoundException e) {
			e.printStackTrace();
		}

	}

}
