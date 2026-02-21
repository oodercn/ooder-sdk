package net.ooder.server.udp;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.udp.ClusterCommand;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.common.MsgStatus;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.web.ConnectionLogFactory;
import net.ooder.web.RuntimeLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

//延期异步执行
class SendCommandMsgTask implements Callable<Boolean> {
    private final JDSSessionHandle serverhandle;
    private final ClusterCommand cloneevent;
    private final String sysCode;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SendCommandMsgTask.class);

    SendCommandMsgTask(JDSSessionHandle serverhandle, ClusterCommand cloneevent, String sysCode) {
        this.serverhandle = serverhandle;
        this.cloneevent = cloneevent;
        this.sysCode = sysCode;
    }


    @Override
    public Boolean call() {
        final List<String> portList = new ArrayList<String>();
        final JDSClientService client;
        try {
            client = JDSServer.getInstance().getJDSClientService(serverhandle, ConfigCode.app);
            ConnectionHandle cnnectionHandle = client.getConnectionHandle();

            if (cnnectionHandle instanceof AbstractUDPHandle) {
                AbstractUDPHandle udpCnnectionHandle = (AbstractUDPHandle) client.getConnectionHandle();
                final String key = udpCnnectionHandle.getIp() + ":" + udpCnnectionHandle.getPort();
                logger.info("msg cnnectionHandle =======key=" + "[" + key + "]" + (cnnectionHandle instanceof AbstractUDPHandle));
                if (client.getConnectInfo() != null && !portList.contains(key) && udpCnnectionHandle.getIp() != null) {
                    String msgId = cloneevent.getMsgId();
                    String token = cloneevent.getToken();

                    RuntimeLog log = ConnectionLogFactory.getInstance().createLog(token, "UDP://" + key + ":[" + sysCode + "]", cloneevent.getCommand() + "[" + cloneevent.getEventId() + "]", msgId);
                    String eventStr = JSONObject.toJSONString(cloneevent);
                    log.setStartTime(System.currentTimeMillis());
                    log.setRequestJson(eventStr);
                    client.getConnectionHandle().send(eventStr);
                    portList.add(key);
                    int times = 1;
                    int k = 0;
                    //判断超时
                    while (log.getStatus().equals(MsgStatus.NORMAL) && k < cloneevent.getTimeout()) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        k = k + k * 2;
                    }

                    if (log.getStatus().equals(MsgStatus.NORMAL)) {
                        switch (cloneevent.getDeadLine()) {
                            case GOON:
                                return true;
                            case STOP:
                                return false;
                            case DELAY:
                                while (log.getStatus().equals(MsgStatus.NORMAL) && times < cloneevent.getMaxtimes()) {
                                    client.getConnectionHandle().send(eventStr);
                                    times = times + 1;
                                    try {
                                        Thread.sleep(cloneevent.getTimeout());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            default:
                                return true;
                        }
                    } else {
                        return true;
                    }

                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
