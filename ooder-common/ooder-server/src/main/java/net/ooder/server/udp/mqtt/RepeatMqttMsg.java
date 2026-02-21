package net.ooder.server.udp.mqtt;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSServer;
import net.ooder.server.JDSUDPServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.udp.UDPServerEventTask;
import net.ooder.web.RemoteConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RepeatMqttMsg implements Runnable {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatMqttMsg.class);

    private final String subsystemCode;
    private final ClusterEvent event;

    public RepeatMqttMsg(ClusterEvent event, final String subsystemCode) {
        this.subsystemCode = subsystemCode;
        this.event = event;
    }

    @Override
    public void run() {
        ServerNode remoteServerBean = JDSServer.getClusterClient().getServerNodeById(subsystemCode);
        ExecutorService service = RemoteConnectionManager.getConntctionService("RepeatSeverMsg[" + remoteServerBean.getId() + "]");
        try {
            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();
            if (event.getEventId() == null) {
                event.setMsgId(UUID.randomUUID().toString());
            }
            List<UDPServerEventTask> taskList = new ArrayList<UDPServerEventTask>();
            if (currServerBean.getType() != null && currServerBean.getType().equals("main")) {
                final List<ServerNode> systems = JDSServer.getClusterClient().getAllServer();
                for (final ServerNode node : systems) {
                    if (node.getAdminPersonId() != null) {
                        try {
                            Set<String> personIds = remoteServerBean.getAdminPersonIds();
                            for (String personId : personIds) {
                                Person toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(personId);
                                final ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
                                final Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
                                Set<String> serverKeys = JDSUDPServer.getInstance().getRepeatEventKey(node.getId());
                                if (serverKeys.contains(event.getExpression()) && sessionHandleList != null && sessionHandleList.size() > 0) {
                                    taskList.add(new UDPServerEventTask(node.getId(), event.clone()));
                                }
                            }
                        } catch (PersonNotFoundException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
            logger.info("taskList  =======size=" + taskList.size());
            List<Future<List<ClusterEvent>>> tasks = service.invokeAll(taskList);

            for (Future<List<ClusterEvent>> future : tasks) {
                List<ClusterEvent> events = future.get(5, TimeUnit.SECONDS);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

}
