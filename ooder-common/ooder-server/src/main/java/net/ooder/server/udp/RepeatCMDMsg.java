package net.ooder.server.udp;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterCommand;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.Person;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.web.RemoteConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RepeatCMDMsg implements Runnable {

    private final String systemCode;
    private final ClusterCommand event;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatCMDMsg.class);

    public RepeatCMDMsg(ClusterCommand event, String systemCode) {
        this.systemCode = systemCode;
        this.event = event;
    }

    @Override
    public void run() {
        List<ClusterCommand> events = new ArrayList<ClusterCommand>();
        final ServerNode remoteServerBean = JDSServer.getClusterClient().getServerNodeById(systemCode);
        if (remoteServerBean != null) {
            try {
                Set<String> personIds = remoteServerBean.getAdminPersonIds();
                for (String personId : personIds) {
                    Person toPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(personId);
                    final ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
                    final Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
                    List<SendCommandMsgTask> taskList = new ArrayList<SendCommandMsgTask>();
                    logger.info("msg json =======sessionHandleList=" + sessionHandleList + "[" + sessionHandleList.size() + "]");
                    if (sessionHandleList != null && sessionHandleList.size() > 0) {
                        for (final JDSSessionHandle serverhandle : sessionHandleList) {
                            long currentTime = System.currentTimeMillis();
                            Long loginTime = (Long) JDSServer.getInstance().getConnectTimeCache().get(serverhandle.toString());
                            if (loginTime != null && ((currentTime - loginTime.longValue()) < 30 * 1000)) {
                                logger.info("msg json =======serverhandle=" + serverhandle);
                                ClusterCommand cevent = event.clone();
                                events.add(cevent);
                                SendCommandMsgTask task = new SendCommandMsgTask(serverhandle, cevent, systemCode);
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
                                    future.get(5, TimeUnit.SECONDS);
                                }
                                break;
                            case SEQUENCE:
                                for (SendCommandMsgTask task : taskList) {
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
    }


}
