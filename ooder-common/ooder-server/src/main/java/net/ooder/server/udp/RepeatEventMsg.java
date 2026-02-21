package net.ooder.server.udp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSServer;
import net.ooder.server.JDSUDPServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.web.RemoteConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RepeatEventMsg implements Runnable {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatEventMsg.class);

    private final String subsystemCode;
    private final ClusterEvent event;

    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }
    public RepeatEventMsg(ClusterEvent event, final String subsystemCode) {
        this.subsystemCode = subsystemCode;
        this.event = event;
    }

    @Override
    public void run() {
        ServerNode remoteServerBean = JDSServer.getClusterClient().getServerNodeById(subsystemCode);
        if (remoteServerBean == null) {
            try {
                remoteServerBean = JDSServer.getInstance().getCurrServerBean();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        ExecutorService service = RemoteConnectionManager.getConntctionService("RepeatSeverMsg[" + remoteServerBean.getId() + "]");
        try {
            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();
            if (event.getEventId() == null) {
                event.setMsgId(UUID.randomUUID().toString());
            }
            List<UDPServerEventTask> taskList = new ArrayList<UDPServerEventTask>();


            // if (currServerBean.getType() != null && currServerBean.getType().equals("main")) {
            logger.info("currServerBeanInfo  " + JSON.toJSONString(currServerBean,config));
            final List<ServerNode> systems = JDSServer.getClusterClient().getAllServer();
            for (final ServerNode node : systems) {
                if (node.getAdminPersonId() != null && !node.getId().equals(OrgConstants.UDPCONFIG_KEY)) {
                    Set<String> personIds = node.getAdminPersonIds();
                    //添加开发者账号
                    Set<String> personSetIds = JDSUDPServer.getInstance().getRepeatPersonEventKey(node.getId(), event.getExpression());
                    personIds.addAll(personSetIds);
                    for (String personId : personIds) {
                        if (personId != null) {
                            try {
                                Person toPerson = OrgManagerFactory.getOrgManager().getPersonByID(personId);
                                final ConnectInfo connInfo = new ConnectInfo(toPerson.getID(), toPerson.getAccount(), toPerson.getPassword());
                                final Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
                                //if (event.getSystemCode() != null && !event.getSystemCode().equals(node.getId())) {
                                Set<String> serverKeys = JDSUDPServer.getInstance().getRepeatEventKey(node.getId());
                                logger.info("node.getId()=  " + node.getId() + "serverKeys=" + serverKeys);
                                logger.info("sessionHandleList=  " + sessionHandleList + "event.getExpression()=" + event.getExpression());
                                if (serverKeys.contains(event.getExpression()) && sessionHandleList != null && sessionHandleList.size() > 0) {
                                    taskList.add(new UDPServerEventTask(node.getId(), event.clone()));
                                }
                            } catch (PersonNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                //   }
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
