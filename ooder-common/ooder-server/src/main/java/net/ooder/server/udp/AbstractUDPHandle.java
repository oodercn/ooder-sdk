
package net.ooder.server.udp;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.cluster.udp.ClusterCommand;
import net.ooder.common.JDSCommand;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSContext;
import net.ooder.context.JDSUDPContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.Enumstype;
import net.ooder.msg.Msg;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSUDPServer;
import net.ooder.thread.JDSThreadFactory;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractUDPHandle implements ConnectionHandle {
    private JDSSessionHandle sessionHandle;
    private String ip;
    private Integer port;
    private ConnectInfo connectInfo;
    public String systemCode;
    private JDSClientService client;
    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ConnectionHandle.class);

    public JDSUDPServer getUdpServer() {
        JDSUDPServer server = null;
        try {
            server = JDSUDPServer.getInstance();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return server;
    }

    public boolean send(JDSCommand command) throws JDSException {
        Map commandBeanmap = BeanMap.create(command);
        String commandCMD = (String) commandBeanmap.get("command");

        if (commandCMD == null || commandCMD.equals("")) {
            return false;
        }
        ClusterCommand clusterCommand = new ClusterCommand();
        clusterCommand.setCommand(commandCMD);
        clusterCommand.setCommandJson(JSONObject.toJSONString(command,config));
        clusterCommand.setExpression("$RepeatCommand");
        clusterCommand.setSessionId(this.getSessionHandle().getSessionID());
        clusterCommand.setSessionHandle(this.sessionHandle);
        clusterCommand.setSystemCode(this.getSystemCode());

        String commandStr = JSONObject.toJSON(clusterCommand,config).toString();
        logger.info("satrt commandStr " + commandStr);
        boolean isSend = send(commandStr);
        logger.info("start send [" + isSend + "]" + commandStr);

        return send(commandStr);
    }

    static Map<String, ScheduledExecutorService> heartServiceMap = new HashMap<String, ScheduledExecutorService>();

    public static synchronized ScheduledExecutorService getHandleService(String ipport) {
        ScheduledExecutorService service = heartServiceMap.get(ipport);
        if (service == null) {
            service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory("AbstractUDPHandle.getHandleService"));
            heartServiceMap.put(ipport, service);
        }
        return service;
    }

    public AbstractUDPHandle(JDSClientService client, JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
        this.client = client;
        this.systemCode = systemCode;
        this.sessionHandle = sessionHandle;

    }

    public void disconnect() throws JDSException {
        logger.info("user:" + connectInfo.getLoginName() + "[" + sessionHandle.getSessionID() + "," + ip + ":" + port + "] udpLogout ");

        try {
            this.getClient().disconnect();
        } catch (JDSException e) {
            throw new JDSException(e);
        }
    }

    public void connect(JDSContext context) throws JDSException {
        if (context instanceof JDSUDPContext) {
            JDSUDPContext updContext = (JDSUDPContext) context;

            this.ip = updContext.getIpAddr();
            this.port = updContext.getPort();
        }

        if (this.connectInfo == null) {
            connectInfo = this.getClient().getConnectInfo();

        }
        client.connect(connectInfo);
        if (connectInfo != null) {
            // logger.info("user:"+connectInfo.getLoginName()+"["+sessionHandle.getSessionID()+","+ip+":"+port+"]
            // udpLogin success");

        }

    }


    public boolean repeatCommand(JDSCommand command, JDSSessionHandle handle) throws JDSException {

        JDSClientService client = this.getClient();
        if (client != null && client.getConnectInfo() != null && client.getConnectionHandle().isconnect()) {
            logger.info("comet command [" + JSONObject.toJSONString(command,config) + "]");
            Map commandBeanmap = BeanMap.create(command);
            Enumstype commandCMD = (Enumstype) commandBeanmap.get("command");
            if (commandCMD == null || commandCMD.equals("")) {
                return false;
            }

            ClusterCommand clusterCommand = new ClusterCommand();
            clusterCommand.setCommand(commandCMD.getType());
            clusterCommand.setCommandJson(JSONObject.toJSONString(command,config));
            clusterCommand.setExpression("$RepeatCommand");
            clusterCommand.setSessionId(handle.getSessionID());
            clusterCommand.setSessionHandle(handle);
            clusterCommand.setSystemCode(command.getSystemCode());
            String commandStr = JSONObject.toJSON(clusterCommand,config).toString();
            logger.info("satrt commandStr " + commandStr);
            boolean isSend = send(commandStr);
            logger.info("end repeatCommand [" + isSend + "]" + commandStr);

            return isSend;
        }
        return false;
    }

    public abstract boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException;

    public boolean isconnect() throws JDSException {
        if (this.ip != null && this.port != null) {
            return true;
        }
        return false;
    }

    public JDSClientService getClient() throws JDSException {
        return client;
    }

    public JDSSessionHandle getSessionHandle() {
        return sessionHandle;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(ConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

}
