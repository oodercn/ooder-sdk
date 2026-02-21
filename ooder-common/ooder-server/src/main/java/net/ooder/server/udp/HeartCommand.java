package net.ooder.server.udp;

import net.ooder.client.JDSSessionFactory;
import net.ooder.cluster.udp.HeardInfo;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.JDSUDPServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.context.UDPActionContextImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

public class HeartCommand implements Runnable {

    private HeardInfo heardInfo;
    private InetAddress address;

    public int port;
    private String sessionId;
    private ConfigCode configCode;
    private String systemCode;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, HeartCommand.class);

    public HeartCommand(HeardInfo heardInfo, InetAddress address, int port) {
        this.address = address;
        this.heardInfo = heardInfo;
        this.port = port;
        this.sessionId = heardInfo.getSessionId();
        this.configCode = ConfigCode.app;
        this.systemCode = heardInfo.getSystemCode();

    }

    private String login(String systemCode, String address, Integer port, JDSSessionHandle sessionHandle) {
        String sendPacket = JDSUDPServer.SUCCESS_KEY;
        try {
            JDSClientService client = this.getClient(sessionHandle.getSessionID(), systemCode, configCode, address, port);
            if (client != null) {
                client.getConnectionHandle().connect(client.getContext());
                //   JDSUDPServer.getInstance().getConnectUDPSocketCache().put(address + ":" + port, client.getSessionHandle());
            } else {
                sendPacket = JDSUDPServer.LOGIN_KEY;
            }
        } catch (JDSException e) {
            sendPacket = JDSUDPServer.LOGIN_KEY;
        }

        return sendPacket;
    }

    private JDSClientService getClient(String sessionId, String systemCode, ConfigCode configCode, String ip, Integer port) throws JDSException {


        UDPActionContextImpl context = new UDPActionContextImpl(ip, port, systemCode, configCode);
        JDSClientService appClient = null;
        context.getContext().put(context.SYSCODE, configCode);
        context.setSessionId(sessionId);
        JDSSessionFactory factory = new JDSSessionFactory(context);
        JDSSessionHandle handle = factory.getSessionHandleBySessionId(sessionId);

        if (handle != null && ip != null && (port != null && port > 0)) {
            appClient = factory.getJDSClientBySessionId(sessionId, configCode);
            AbstractUDPHandle udp = (AbstractUDPHandle) appClient.getConnectionHandle();
            if (udp != null) {
                udp.setIp(ip);
                udp.setPort(port);
                appClient.setConnectionHandle(udp);
            }

            ConnectInfo connectInfo = JDSServer.getInstance().getConnectInfo(handle);
            if (appClient != null) {
                appClient.setContext(context);
                appClient.connect(connectInfo);
            }
            // 只有当前SESSION有效的时候才进行新的用户client获取
            if (appClient == null && connectInfo != null) {
                appClient = JDSServer.getInstance().newJDSClientService(handle, configCode);
                appClient.connect(connectInfo);
            }

        }

        return appClient;
    }

    @Override
    public void run() {
        String msgStr = JDSUDPServer.SUCCESS_KEY;
        try {
            ConnectInfo connectionInfo = null;
            JDSClientService appClient = getClient(sessionId, systemCode, configCode, address.getHostAddress(), port);
            if (appClient == null) {
                msgStr = "{\"event\":1001,\"msgStr\":\"appClient is null\"}";
                logger.error(" appClient is null userId:[" + heardInfo.getUserid() + "] sessionid:[" + heardInfo.getSessionId() + "]");
            } else {
                JDSSessionHandle sessionhandle = JDSServer.getInstance().getSessionHandleCache().get(sessionId);
                ConnectionHandle handle = appClient.getConnectionHandle();
                connectionInfo = appClient.getConnectInfo();
                if (connectionInfo == null || !connectionInfo.getUserID().equals(this.heardInfo.getUserid())) {
                    Person person = OrgManagerFactory.getOrgManager().getPersonByID(this.heardInfo.getUserid());
                    connectionInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
                    appClient.connect(connectionInfo);
                }

                if (sessionhandle != null) {
                    JDSServer.getInstance().activeSession(sessionhandle);
                } else {
                    Set<JDSSessionHandle> handles = JDSServer.getInstance().getSessionHandleList(connectionInfo);
                    for (JDSSessionHandle ohandle : handles) {
                        if (ohandle != null) {
                            if (ohandle.getSessionID().equals(sessionId) || ohandle.getSessionID().equals(handle.getClient().getSessionHandle().getSessionID())) {
                                sessionhandle = ohandle;
                                sessionhandle.setIp(address.getHostAddress());
                                sessionhandle.setPort(port);
                                JDSServer.getInstance().activeSession(sessionhandle);
                            }
                        }
                    }
                    // 避免SESSION失效后的继续登录
                    //先做一次尝试登陆
                    if (sessionhandle == null) {
                        OrgManager orgManager = OrgManagerFactory.getOrgManager(configCode);
                        msgStr = "{\"event\":1001,\"msgStr\":\"sessionhandle=is null or systemStatus is -1\"}";
                        logger.error(msgStr + " sessionhandle=" + handle.getClient().getConnectionHandle());
                    }

                }


                if (getSystemStatus(connectionInfo) == -1) {
                    msgStr = "{\"event\":1001,\"msgStr\":\"connectionInfo=" + connectionInfo + " systemStatus is -1\"}";
                    logger.error(msgStr);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error(e);
            msgStr = "{\"event\":1001,\"msgStr\":\"sessionhandle is null\"}";
        }
        DatagramPacket sendPacket = new DatagramPacket(msgStr.getBytes(), msgStr.getBytes().length, address, port);
        try {
            DatagramSocket socket = JDSUDPServer.getInstance().getPushMsgSocket();
            socket.send(sendPacket);
        } catch (IOException | JDSException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    public Integer getSystemStatus(ConnectInfo connectInfo) {
        try {

            Set<JDSSessionHandle> handles = JDSServer.getInstance().getSessionHandleList(connectInfo);
            long expireTime = 15 * 1000;
//            for (JDSSessionHandle handle : handles) {
//                if (JDSServer.getInstance().getConnectTimeCache().get(handle.getSessionID()) != null) {
//                    long currentTime = System.currentTimeMillis();
//                    Long loginTime = (Long) JDSServer.getInstance().getConnectTimeCache().get(handle.getSessionID());
//                    if ((currentTime - loginTime.longValue()) < 60 * 1000) {
//                        logger.info("user[" + connectInfo.getLoginName() + "]handles.size=" + handles.size() + "  getSessionID()=" + handle.getSessionID() + "time=" + (currentTime - loginTime.longValue()));
//                    }
//                }
//            }

            for (JDSSessionHandle handle : handles) {
                if (JDSServer.getInstance().getConnectTimeCache().get(handle.getSessionID()) != null) {
                    long currentTime = System.currentTimeMillis();
                    Long loginTime = (Long) JDSServer.getInstance().getConnectTimeCache().get(handle.getSessionID());
                    if ((currentTime - loginTime.longValue()) < expireTime) {
                        logger.info("user[" + connectInfo.getLoginName() + "]handles.size=" + handles.size() + "  getSessionID()=" + handle.getSessionID() + "time=" + (currentTime - loginTime.longValue()));
                        return 0;
                    }
                }
            }

        } catch (JDSException e) {

        }
        return -1;// 脱机
    }

}
