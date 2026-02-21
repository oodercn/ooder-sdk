package net.ooder.server.udp.mqtt;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.Person;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.web.RemoteConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MqttEventTask implements Callable<List<ClusterEvent>> {


    private final String systemCode;
    private final ClusterEvent event;

    public MqttEventTask(String systemCode, ClusterEvent event) {
        this.systemCode = systemCode;
        this.event = event;
    }


    @Override
    public List<ClusterEvent> call() throws Exception {
        List<ClusterEvent> events = new ArrayList<ClusterEvent>();
        final ServerNode remoteServerBean = JDSServer.getClusterClient().getServerNodeById(systemCode);
        if (remoteServerBean != null) {

            try {
                Set<String> personIds = remoteServerBean.getAdminPersonIds();
                for (String personId : personIds) {
                    Person toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(personId);
                    final ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
                    final Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
                    if (sessionHandleList != null && sessionHandleList.size() > 0) {
                        List<SendMqttMsgTask> taskList = new ArrayList<SendMqttMsgTask>();
                        for (final JDSSessionHandle serverhandle : sessionHandleList) {
                            long currentTime = System.currentTimeMillis();
                            Long loginTime = (Long) JDSServer.getInstance().getConnectTimeCache().get(serverhandle.toString());
                            if (loginTime != null && ((currentTime - loginTime.longValue()) < 30 * 1000)) {
                                ClusterEvent cevent = event.clone();
                                events.add(cevent);
                                SendMqttMsgTask task = new SendMqttMsgTask(serverhandle, cevent, systemCode);
                                taskList.add(task);
                            }
                        }
                        ExecutorService service = RemoteConnectionManager.getConntctionService(remoteServerBean.getUrl());
                        switch (event.getSequence()) {
                            case FIRST:
                                service.submit(taskList.get(0)).get();
                                break;
                            case MEANWHILE:
                                List<Future<Boolean>> tasks = service.invokeAll(taskList);
                                for (Future<Boolean> future : tasks) {
                                    future.get();
                                }
                                break;
                            case SEQUENCE:
                                for (SendMqttMsgTask task : taskList) {
                                    Boolean result = service.submit(task).get();
                                    if (result == null || !result) {
                                        break;
                                    }
                                }
                            default:
                                break;
                        }
                    }

                }


            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return events;
    }
}
