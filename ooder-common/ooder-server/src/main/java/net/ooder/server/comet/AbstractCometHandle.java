package net.ooder.server.comet;

import com.alibaba.fastjson.JSONObject;
import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSCommand;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.annotation.Enumstype;
import net.ooder.msg.Msg;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.thread.JDSThreadFactory;
import net.sf.cglib.beans.BeanMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractCometHandle implements ConnectionHandle {
    protected JDSSessionHandle sessionHandle;

    protected ConnectInfo connectInfo;

    private String systemCode;

    public static final String HEARTKEY = "0";

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected JDSClientService client;

    public static Map<String, Long> commandMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "CommandCache",5 * 1024 * 1024, 60 * 60 * 1000);

    public static Cache<String, String> sessionMapUser = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "SessionMapUser",1 * 1024 * 1024, 60 * 60 * 1000);

    protected static Map<String, ScheduledExecutorService> commandServiceMap = new HashMap<String, ScheduledExecutorService>();

    public static Map<String, Long> checkHeart = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), "checkHeartCache",1 * 1024 * 1024, 60 * 60 * 1000);

    protected static Map<String, Long> checkCommandHeart = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), "checkCommandHeartCache",1 * 1024 * 1024, 60 * 60 * 1000);

    protected static Map<String, Long> checkTime = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), "checkTimeCache",1 * 1024 * 1024, 60 * 60 * 1000);

    protected static synchronized ScheduledExecutorService getCommandService(final String userId) {

        ScheduledExecutorService service = commandServiceMap.get(userId);
        if (service == null || service.isShutdown()) {
            service = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory("AbstractCometHandle.getCommandService"));
            commandServiceMap.put(userId, service);
        }
        return service;
    }

    protected class ConnectionServer implements Callable<JDSClientService> {

        private JDSClientService clientService;
        private ConnectInfo connInfo;

        public ConnectionServer(JDSClientService clientService, ConnectInfo connInfo) {
            this.connInfo = connInfo;
            this.clientService = clientService;
        }

        public JDSClientService call() throws JDSException {

            clientService.connect(connInfo);
            return clientService;
        }

    }

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ConnectionHandle.class);

    protected boolean isClose = true;
    boolean isStart = true;

    public AbstractCometHandle(JDSClientService client, JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
        this.client = client;
        this.systemCode = systemCode;
        this.sessionHandle = sessionHandle;

    }

    public void disconnect() throws JDSException {
        isClose = false;
        try {
            if (this.getClient() != null) {

                this.getClient().disconnect();

            }
        } catch (JDSException e) {
            throw new JDSException(e);
        }

    }

    public void reConnect() throws JDSException {
        this.disconnect();

        // 获取session
        JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext.getActionContext());
        JDSSessionHandle sessionHandle = factory.createSessionHandle();
        JDSActionContext.getActionContext().getContext().put(JDSActionContext.JSESSIONID, sessionHandle.getSessionID());
        request.setAttribute(JDSActionContext.JSESSIONID, sessionHandle.getSessionID());

        //   client = factory.newClientService(sessionHandle, JDSServer.getInstance().getAdminClient().getConfigCode());

        try {
            if (JDSServer.getInstance().getAdminClient() != null) {
                client = factory.newClientService(sessionHandle, JDSServer.getInstance().getAdminClient().getConfigCode());
            } else {
                client = factory.newClientService(sessionHandle, JDSServer.getInstance().getCurrServerBean().getConfigCode());
            }
        } catch (JDSException e) {
            client = factory.newClientService(sessionHandle, JDSServer.getInstance().getCurrServerBean().getConfigCode());
        }
        client.connect(connectInfo);

        logger.info("reConnect:" + connectInfo.getLoginName() + "[" + client.getContext().getSessionId() + "] reconnect success");
        isClose = false;

    }

    public void connect(JDSContext context) throws JDSException {
        //标记长链接位置

        logger.info("user:" + connectInfo.getLoginName() + "[" + client.getContext().getSessionId() + "] cometLogin success");
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
                if (lastHeartTime == null) {
                    lastHeartTime = System.currentTimeMillis();
                    checkCommandHeart.put(context.getSessionId(), lastHeartTime);
                }
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

                send("sessionId is  null,  place login frist!");
                this.disconnect();
            }
        }

    }

    public JDSClientService getClient() throws JDSException {
        return client;
    }

    public JDSSessionHandle getSessionHandle() {
        return sessionHandle;
    }

    public boolean isconnect() throws JDSException {
        if (response != null) {
            return true;
        }
        return false;
    }

    public boolean repeatCommand(JDSCommand command, JDSSessionHandle handle) throws JDSException {

        JDSClientService client = this.getClient();
        if (client != null && client.getConnectInfo() != null && client.getConnectionHandle().isconnect()) {
            logger.info("comet command [" + JSONObject.toJSONString(command) + "]");
            return client.getConnectionHandle().send(command);
        }
        return false;
    }

    public boolean repeatMsg(Msg msg, JDSSessionHandle handle) throws JDSException {

        // SendAppMsg msgRun = new SendAppMsg(msg, handle,JDSActionContext.getActionContext());
        // Executors.newSingleThreadExecutor().execute(msgRun);
        return true;
    }

    public synchronized boolean send(JDSCommand command) throws JDSException {
        String sessionId = "";
        if (client != null && client.getContext().getSessionId() != null) {
            sessionId = client.getContext().getSessionId();
        }
        String token = sessionId + "[" + command.getCommandId() + "]";
        logger.info("start " + token + " comet command [" + JSONObject.toJSONString(command) + "]");
        Boolean isSuccess = false;
        Map commandBeanmap = BeanMap.create(command);
        Enumstype commandCMD = (Enumstype) commandBeanmap.get("command");
        if (commandCMD == null) {
            isSuccess = false;
        } else {
            // 防止重复发送
            Long checkOutTime = commandMap.get(token);
            if (checkOutTime == null || checkOutTime - System.currentTimeMillis() > 2000) {
                logger.info("comet command [" + JSONObject.toJSONString(command) + "]");
                Iterator<String> keyit = commandBeanmap.keySet().iterator();
                // 过滤空值
                Map valueMap = new HashMap();
                for (; keyit.hasNext(); ) {
                    String key = keyit.next();
                    Object value = commandBeanmap.get(key);
                    if (value != null && !value.equals("")) {
                        if (key.equals("passVal1")) {
                            Integer pass = Integer.valueOf(value.toString());
                            valueMap.put("passVal1", pass);
                        } else {
                            valueMap.put(key, value);
                        }

                    }
                }

                String commandStr = JSONObject.toJSON(valueMap).toString();
                logger.info("send command [" + commandStr + "]");
                isSuccess = send(commandStr);

                if (isSuccess) {
                    commandMap.put(token, System.currentTimeMillis());
                }

            }

        }
        logger.info("end comet command [" + JSONObject.toJSONString(command) + "]");
        return isSuccess;
    }

    @Override
    public synchronized boolean send(String msgString) throws JDSException {
        if (response != null) {
            try {

                response.getWriter().println(msgString);
                response.getWriter().flush();
                response.flushBuffer();

                if (response.getWriter().checkError()) {
                    isClose = false;
                    disconnect();
                    return false;
                }

            } catch (Exception e) {
                // e.printStackTrace();

                logger.error("msg[" + msgString + "] send error");
                isClose = false;
                disconnect();
                return false;
            }
        } else {
            disconnect();
            return false;
        }

        return true;
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

    public static void main(String[] args) {
        String commandUrl = "http://smart.tujia.com:83/comet";
        String sessionid = "dddfffffffffffffff";
        String command = "{\"command\":\"CommandReConnect\",\"url\":\"" + commandUrl + "\",\"sessionid\":\"" + sessionid + "\"}";
        System.out.println(command + System.currentTimeMillis());

    }

    public static Map<String, Long> getCheckHeart() {
        return checkHeart;
    }

    public static void setCheckHeart(Map<String, Long> checkHeart) {
        AbstractCometHandle.checkHeart = checkHeart;
    }

}
