
package net.ooder.server.udp;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterClient;
import net.ooder.common.JDSException;
import net.ooder.context.JDSContext;
import net.ooder.context.JDSUDPContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.msg.Msg;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

import java.util.List;
import java.util.Set;


public  class DefaultUDPHandle extends AbstractUDPHandle {

	public DefaultUDPHandle(JDSClientService client,JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
		super(client,sessionHandle, systemCode);
	}



	private Boolean sendServerMessage(String eventStr, String systemCode) throws JDSException {

		logger.debug("start sendEvent  eventStr [" + systemCode + "]" + eventStr);
		ServerNode remoteServerBean = JDSServer.getClusterClient().getServerNodeById(systemCode);
		if (remoteServerBean != null) {
			Person toPerson;
			try {
				toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(remoteServerBean.getId() + remoteServerBean.getId());

				ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
				Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
				for (JDSSessionHandle serverhandle : sessionHandleList) {
					JDSClientService client = JDSServer.getInstance().getJDSClientService(serverhandle, OrgConstants.CONFIG_KEY);
					if (client.getConnectInfo() != null) {
						logger.info("end sendEvent to  [" + client.getConnectionHandle() + "]");
						client.getConnectionHandle().send(eventStr);
					}


				}

				// logger.info("end subSystem eventStr [" + systemCode + "]" + eventStr);
				return true;
			} catch (PersonNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (JDSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;

	}

	@Override
	public void receive(String receiveStr) throws JDSException {
		JSONObject jsonobj = JSONObject.parseObject(receiveStr);
		if (jsonobj.containsKey("expression")) {
			if (jsonobj.containsKey("commandJson")) {

			} else if (jsonobj.containsKey("sourceJson")) {

				ClusterClient client=JDSServer.getClusterClient();

				ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();

				if (currServerBean.getType() != null && currServerBean.getType().equals("main")) {
					List<ServerNode> systems = client.getAllServer();
					for (ServerNode node : systems) {
						if (client.getSystem(node.getId()).getConfigname().equals("app")) {
							sendServerMessage(receiveStr, node.getId());
						}
					}
				}

			}

		}

	}

	@Override
	public boolean send(String msgString) throws JDSException {

		if (this.getConnectInfo() != null) {
			logger.info("send user:[" + this.getConnectInfo().getLoginName() + "] ");
			logger.debug(msgString);
		}
		Boolean canSend = false;
		if (this.getIp() != null && this.getPort() != null) {
			canSend = this.getUdpServer().send(msgString, this.getIp(), this.getPort());
		}

		return canSend;

	}


	@Override
	public void connect(JDSContext context) throws JDSException {
		super.connect(context);
		JDSUDPContext updContext = (JDSUDPContext) context;

		this.setIp(updContext.getIpAddr());
		this.setPort(updContext.getPort());
		JDSUDPContext jdsUDPContext = (JDSUDPContext) context;
		Person eiperson = null;
		try {
			eiperson = OrgManagerFactory.getOrgManager().getPersonByID(this.getClient().getConnectInfo().getUserID());
		} catch (PersonNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public String toString(){
		return this.getIp()+":"+this.getPort()+"["+this.getSessionHandle()+"]";
	};

	@Override
	public void disconnect() throws JDSException {
		super.disconnect();
	}

	@Override
	public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {

		return true;
	}



}
