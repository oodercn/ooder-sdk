package net.ooder.server.comet;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.context.JDSCometContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GWLongCometHandle extends AbstractCometHandle {

    public GWLongCometHandle(JDSClientService client, JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
	super(client, sessionHandle, systemCode);

    }

    public void receive(String receiveStr) throws JDSException {
	logger.info("receive user:[" + getConnectInfo().getLoginName() + "] ");
	logger.info(receiveStr);
    }

    public boolean send(String msgString) throws JDSException {
	logger.info(msgString);
	return super.send(msgString);
    }

    class ClearSession implements Runnable {
	private ConnectionHandle sessionHandle;

	private ConnectInfo connectInfo;

	private String systemCode;

	ClearSession(ConnectInfo connectInfo, ConnectionHandle handle, String systemCode) {
	    this.sessionHandle = handle;
	    this.connectInfo = connectInfo;
	    this.systemCode = systemCode;
	}

	public void run() {
	    try {
		Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connectInfo);
			ConfigCode configCode=JDSServer.getClusterClient().getSystem(systemCode).getConfigname();
		for (JDSSessionHandle handle : sessionHandleList) {
		    if (!handle.toString().equals(sessionHandle.toString())) {
			JDSClientService client = JDSServer.getInstance().getJDSClientService(handle, configCode);
			if (client != null && client.getConnectInfo() != null) {
			    client.getConnectionHandle().disconnect();
			}
		    }

		}
	    } catch (JDSException e) {
		e.printStackTrace();
	    }

	}

    }

    public void connect(JDSContext context) throws JDSException

    {

	if (context instanceof JDSCometContext) {
	    JDSCometContext cometContext = (JDSCometContext) context;
	    this.request = (HttpServletRequest) cometContext.getHttpRequest();
	    this.response = (HttpServletResponse) cometContext.getHttpResponse();
	} else {
	    throw new JDSException("用户未登录！", JDSException.PROCESSDEFINITIONERROR);
	}

	if (this.connectInfo == null) {
	    connectInfo = this.getClient().getConnectInfo();
	}

	if (this.connectInfo == null) {
	    throw new JDSException("用户未登录！", JDSException.PROCESSDEFINITIONERROR);
	}

	Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connectInfo);
	// 注销掉多余的SESSION
	List<JDSSessionHandle> newsessionHandleList = new ArrayList<JDSSessionHandle>();

	if (sessionHandleList != null) {
	    newsessionHandleList.addAll(sessionHandleList);
	    for (JDSSessionHandle handle : newsessionHandleList) {
		if (handle != null && !handle.getSessionID().toString().equals(context.getSessionId())) {
		    if (JDSServer.getInstance().getConnectTimeCache().get(handle) != null) {
			JDSServer.getInstance().disconnect(handle);
		    }

		}

	    }

	}

	client.connect(connectInfo);
	logger.info("user:" + connectInfo.getLoginName() + "[" + sessionHandle.getSessionID() + "] cometLogin success");
	int k = 0;
	while (isClose) {
	    JDSClientService client = this.getClient();
	    ConnectInfo connectionInfo = client.getConnectInfo();
	    if (client != null && connectionInfo != null) {

		Long lastLoginTime = checkTime.get(context.getSessionId());

		Long lastCommandTime = checkHeart.get("Herat" + context.getSessionId() + "");

		if (lastCommandTime == null) {
		    lastCommandTime = System.currentTimeMillis();
		    checkHeart.put("Herat" + context.getSessionId() + "", lastCommandTime);

		}

		Long lastHeartTime = checkCommandHeart.get(context.getSessionId());

		// 每300秒激活一次在线时间
		if (lastLoginTime == null) {
		    lastLoginTime = System.currentTimeMillis();
		    checkTime.put(context.getSessionId(), lastLoginTime);
		    ConnectionServer connectTask = new ConnectionServer(client, connectInfo);
		    getCommandService(connectInfo.getUserID()).submit(connectTask);

		}

		if (System.currentTimeMillis() - lastLoginTime > 300 * 1000) {
		    checkTime.put(context.getSessionId(), System.currentTimeMillis());
		    ConnectionServer connectTask = new ConnectionServer(client, connectInfo);
		    getCommandService(connectInfo.getUserID()).submit(connectTask);
		}

		// 每300秒激活一次在线时间
		if (lastHeartTime == null) {
		    lastHeartTime = System.currentTimeMillis();
		    checkCommandHeart.put(context.getSessionId(), lastHeartTime);
		}

		// // //发送心跳
		// if (System.currentTimeMillis() - lastHeartTime > 30 * 1000) {
		//
		// checkCommandHeart.put(context.getSessionId(), System.currentTimeMillis());
		// SycnCommand command = new SycnCommand();
		// command.setCommandId("Herat" + context.getSessionId() + "");
		// command.setCommand("SyncTime");
		// String time = Long.valueOf(System.currentTimeMillis()).toString();
		// command.setValue(time.substring(0, time.length() - 3));
		// String commandStr = JSONObject.toJSON(command).toString();
		// this.send(commandStr);
		// }

		if (System.currentTimeMillis() - lastCommandTime < 45 * 1000) {
		    send(HEARTKEY);
		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e) {
			isClose = false;
			send(e.getMessage());

		    }
		    k = k + 1;

		} else {
		    isClose = false;
		    client.getConnectionHandle().disconnect();

		}

	    } else {
		isClose = false;

		send("sessionId is  null,  place login frist!");
		this.disconnect();
	    }
	}

    }

    public void disconnect() throws JDSException {
	try {
	    // CommandWebService gwclient=(CommandWebService)
	    // EsbUtil.parExpression("$CommandWebService");
	    // Gateway gateway=new Gateway();
	    // gateway.setDeviceId(connectInfo.getLoginName());
	    // gateway.setGatewayAccount(connectInfo.getLoginName());
	    // gwclient.gatewayOffLine(gateway);
	    // GWAccountWebService accountclient=(GWAccountWebService)
	    // EsbUtil.parExpression("$GWAccountWebService");
	    // accountclient.logout();

	} catch (Exception localException) {
	}

	super.disconnect();
    }

    @Override
    public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {
	// TODO Auto-generated method stub
	return false;
    }
}
