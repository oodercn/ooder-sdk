/**
 * $RCSfile: UDPClient.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.cluster.udp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.cluster.event.ClusterEventControl;
import net.ooder.cluster.event.ServerEvent;
import net.ooder.cluster.service.ServerEventFactory;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.MsgStatus;
import net.ooder.common.ServerEventEnums;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.User;
import net.ooder.server.JDSServer;
import net.ooder.server.SubSystem;
import net.ooder.thread.JDSThreadFactory;
import net.ooder.web.ConnectionLogFactory;
import net.ooder.web.RuntimeLog;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UDPClient {

    private static UDPClient instance;

    private DatagramSocket clientSocket;

    public static final String THREAD_LOCK = "Thread Lock";

    private String code;

    public static final String SUCCESS_KEY = "success";

    public static final String ERROR_KEY = "error";

    public static final String HIT_KEY = "0";

    public static final String START_KEY = "{";

    public static final String END_KEY = "}";


    public static final String STATUS_SPLIT_KEY = "&&";

    public static final String STATUS_START_KEY = "##||";

    public static final String STATUS_END_KEY = "||##";

    public static final String SESSIONID = "sessionId";

    public static final String EVENTKEY = "event";

    public static final String SYSTEMCODE = "systemCode";

    private final static String USERNAME = "userName";

    private final static String PASSWORD = "password";

    Async async = Async.newInstance();

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, UDPClient.class);

    static Map<User, ClusterClient> clientMap = new HashMap<User, ClusterClient>();

    public static Cache<String, Long> msgCache = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "msgCache", 1024 * 1024, 60 * 1000);

    static ExecutorService cmdService = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory("UDPClient.cmdService"));

    static ExecutorService udpService = Executors.newFixedThreadPool(150, new JDSThreadFactory("UDPClient.udpService"));

    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    private static HeartThread hearttask;

    private User user;

    public boolean isLogin = false;


    public boolean isClient = false;

    public UDPClient() {

    }

    static UDPClient getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new UDPClient();
                }
            }
        }
        return instance;
    }


    public void stop() {
        cmdService.shutdownNow();
        //udpService.shutdownNow();
        clientMap.clear();

        isLogin = false;
        // instance = null;
        //   this.user = null;
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();

        }
        clientSocket = null;

    }

    public synchronized User login() {

        if (!isClient) {
            try {

                logger.info("*********************************Start UPD Connect******************************************");
                user = ajaxlogin();

                //初始化系统事件
                try {
                    ServerEventFactory.getInstance().initEvent(user);
                } catch (JDSException e) {
                    e.printStackTrace();
                }
                final String loginStr = JSONObject.toJSONString(user);
                isLogin = true;
                startHeart(user);
            } catch (final Exception e) {
                logger.error("loginException:", e);
            }

        } else {
            try {
                this.clientLogin(UserBean.getInstance());
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

        return user;

    }

    public synchronized User clientLogin(UserBean userBean) throws JDSException {


        this.isClient = true;
        logger.info("*********************************Start UPD Connect******************************************");
        user = clientAjaxLogin(userBean);
        //初始化系统事件
        try {
            ServerEventFactory.getInstance().initEvent(user);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        final String loginStr = JSONObject.toJSONString(user);
        isLogin = true;
        startHeart(user);
        return user;
    }

    /**
     * 客户端登录专用方法
     *
     * @return
     * @throws JDSException
     */
    private User clientAjaxLogin(UserBean userBean) throws JDSException {
        String newPWD = userBean.getUserpassword();
        newPWD = MD5.getHashString(newPWD);
        final String userName = userBean.getUsername();
        final String systemCode = userBean.getSystemCode();
        final Request request = Request.Post(userBean.getServerUrl() + userBean.getClitentLoginUrl())
                .bodyForm(Form.form().add(USERNAME, userName).add(JDSActionContext.JSESSIONID, "").add(PASSWORD, newPWD).build(), Charset.forName("utf-8"));

        String json = null;
        try {
            json = request.execute().returnContent().asString();
        } catch (Exception e2) {
            throw new JDSException("网络连接失败。");
        }

        logger.info("ajaxlogin-return-json:" + json);

        JSONObject jsonobj = JSONObject.parseObject(json);

        if (jsonobj.containsKey("requestStatus") && jsonobj.getInteger("requestStatus") != -1) {
            user = jsonobj.getObject("data", User.class);
            user.setPassword(newPWD);
            UserBean.getInstance().setConfigName(user.getConfigName());
            UserBean.getInstance().setSystemCode(user.getSystemCode());
            UserBean.getInstance().setPersonid(user.getId());

        } else {
            logger.error("ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode);
            String msg = "登录失败。";
            if (jsonobj.get("errdes") != null) {
                msg = jsonobj.get("errdes").toString();
            }
            throw new JDSException(msg);
        }
        logger.info("登陆用户信息:user=" + JSON.toJSONString(user));

        JDSServer.getInstance();

        return user;

    }

    public User ajaxlogin() throws ClientProtocolException, IOException, JDSException {

        String newPWD = UserBean.getInstance().getUserpassword();
        newPWD = MD5.getHashString(newPWD);

        final String userName = UserBean.getInstance().getUsername();
        final String systemCode = UserBean.getInstance().getSystemCode();
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + UserBean.getInstance().getLoginUrl())
                .bodyForm(Form.form().add(USERNAME, userName).add(SYSTEMCODE, systemCode).add(JDSActionContext.JSESSIONID, "").add(PASSWORD, newPWD).build(), Charset.forName("utf-8"));

        Future<Content> future = async.execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
                logger.info("content" + content);
            }

            public void cancelled() {
                logger.warn("ajaxlogin-cancelled" + "userName=" + userName + "systemCode=" + systemCode);
            }
        });

        String json = null;
        try {
            json = future.get().asString();
        } catch (InterruptedException e1) {
            logger.error("InterruptedException-ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode, e1);
        } catch (ExecutionException e2) {
            logger.error("ExecutionException-ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode, e2);
        }

        logger.info("ajaxlogin-return-json:" + json);

        JSONObject jsonobj = JSONObject.parseObject(json);

        /* 如果登陆失败,每隔10秒再尝试登陆一次*/
        while (json == null || jsonobj == null || !jsonobj.containsKey("data")) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error("sleepException", e);
            }
            try {
                future = async.execute(request);
                json = future.get().asString();
                jsonobj = JSONObject.parseObject(json);
            } catch (InterruptedException e1) {
                logger.error("InterruptedException-ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode, e1);
            } catch (ExecutionException e2) {
                logger.error("ExecutionException-ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode, e2);
            }
        }

        if (jsonobj.containsKey("requestStatus") && jsonobj.getInteger("requestStatus") != -1) {
            user = jsonobj.getObject("data", User.class);
            user.setPassword(newPWD);
            UserBean.getInstance().setPersonid(user.getId());
        } else {
            logger.error("ajaxlogin(登陆失败):" + "userName=" + userName + "systemCode=" + systemCode);
            throw new JDSException("登录失败。");
        }
        logger.info("登陆用户信息:user=" + JSON.toJSONString(user));
        JDSServer.getInstance();
        return user;
    }

    public void startHeart(User user) {

        if (hearttask != null && hearttask.getDs() != null && !hearttask.getDs().isClosed() && !cmdService.isShutdown()) {
            hearttask.setUser(user);
            hearttask.setDs(clientSocket);
        } else {
            hearttask = new HeartThread(clientSocket, user);
            if (cmdService.isShutdown()) {
                cmdService = Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory("UDPClient[" + user.getId() + "]"));
            }
            cmdService.submit(hearttask);

        }

//        Map evetntContext = new HashMap();
//        SubSystem system = null;
//        try {
//            system = JDSServer.getClusterClient().getSystem(JDSServer.getInstance().getCurrServerBean().getId());
//        } catch (JDSException e) {
//            e.printStackTrace();
//        }
        // fireSeverEvent(system, ServerEventEnums.serverStarted, evetntContext);
//

    }


    public boolean send(String msgString) {
        try {
            msgString = msgString.trim();
            if (msgString.startsWith("{") && msgString.endsWith("}")) {
                JSONObject jsonObject = JSONObject.parseObject(msgString);
                if (jsonObject.containsKey("msgId")) {
                    String msgId = jsonObject.getString("msgId");
                    msgCache.put(msgId, System.currentTimeMillis());
                    try {
                        RuntimeLog log = ConnectionLogFactory.getInstance().createLog(msgId, "UDP://" + user.getUdpIP() + ":[" + user.getSystemCode() + "]", "local send ", msgId);
                        // String eventStr = JSONObject.toJSONString(msgString);
                        log.setStartTime(System.currentTimeMillis());
                        log.setRequestJson(msgString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }

            }

            if (user != null && user.getUdpIP() != null && clientSocket != null) {
                final URL url = new URL("http://" + user.getUdpIP());
                // logger.info("local port:" + clientSocket.getPort() + " remote port:" + InetAddress.getByName(url.getHost()) + ":" + user.getUdpPort());
                msgString = java.net.URLEncoder.encode(msgString, "utf-8");
                final DatagramPacket hp = new DatagramPacket(msgString.getBytes(), msgString.length(), InetAddress.getByName(url.getHost()), user.getUdpPort());
                clientSocket.send(hp);
            }
        } catch (final UnsupportedEncodingException e2) {
            e2.printStackTrace();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        if (clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected()) {
            return false;
        }
        return true;
    }

    public void start() {
        if (clientSocket == null) {
            try {
                clientSocket = new DatagramSocket();
            } catch (final SocketException e1) {

                e1.printStackTrace();
            }
            if (clientSocket != null) {
                // 循环接收
                final byte[] buf = new byte[8192];
                final DatagramPacket rp = new DatagramPacket(buf, 8192);
                while (clientSocket != null && !clientSocket.isClosed()) {
                    try {
                        clientSocket.receive(rp);
                        // 取出信息
                        final String content = URLDecoder.decode(new String(rp.getData(), 0, rp.getLength()), "utf-8");

                        if (content.startsWith(ERROR_KEY)) {
                            JDSServer.getClusterClient().login(false);
                        } else if (content.startsWith(SUCCESS_KEY)) {
                            if (hearttask != null) {
                                hearttask.setLastTime(System.currentTimeMillis());
                            }
                            // 数据出现错误/为正确登录或者数据传输错误
                            // login();
                        } else if (content.startsWith(START_KEY) && content.endsWith(END_KEY)) {

                            if (hearttask != null) {
                                hearttask.setLastTime(System.currentTimeMillis());
                            }
                            final JSONObject jsonobj = JSONObject.parseObject(content);
                            // session 失效重新登录
                            if (jsonobj.containsKey(EVENTKEY) && jsonobj.get(EVENTKEY).equals(1001)) {
                                JDSServer.getClusterClient().reboot();

                            } else {
                                logger.info("Client &&&&&&&&&&&&&&&&&&&&&& json=" + content);
                                //    final JSONObject jsonobj = obj;
                                if (jsonobj.containsKey("token")) {
                                    String token = jsonobj.getString("token");
                                    if (!msgCache.containsKey(token)) {
                                        if (jsonobj.containsKey("expression")) {
                                            if (jsonobj.containsKey("commandJson")) {
                                                final ClusterCommand command = JSONObject.parseObject(content, ClusterCommand.class);
                                                udpService.submit(new ClusterExeCMDControl(command));
                                            } else if (jsonobj.containsKey("sourceJson")) {
                                                final ClusterEvent event = JSONObject.parseObject(content, ClusterEvent.class);
                                                udpService.submit(new ClusterExeEventControl(event));
                                            }
                                        }
                                    }
                                    this.updateCommandStatus(token, MsgStatus.READED);
                                }
                            }
                        } else {
                            logger.info("error " + "[" + content + "]");
                        }
                    } catch (Throwable thrown) {
                        thrown.printStackTrace();
                        //崩溃自动重启
                        new Thread() {
                            @Override
                            public void run() {
                                JDSServer.getClusterClient().reboot();
                            }
                        }.start();

                        //  JDSServer.getClusterClient().reboot();
                    }
                }
            }
        }
    }

    public void updateEventStatus(final String token, final MsgStatus status) {
        send(STATUS_START_KEY + token + STATUS_SPLIT_KEY + status.getType() + STATUS_END_KEY);
    }

    public void updateCommandStatus(final String token, final MsgStatus status) {
        send(STATUS_START_KEY + token + STATUS_SPLIT_KEY + status.getType() + STATUS_END_KEY);
    }


    public User getUser() {

//        if (user == null) {
//            user = new User();
//            user.setAccount(UserBean.getInstance().getUsername());
//
//        }

        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public DatagramSocket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(final DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void updateTaskStatus(final String msgId, final String status) {
        udpService.submit(new UDPControl(user, msgId, status));
    }


    private void fireSeverEvent(SubSystem server, ServerEventEnums eventID, Map eventContext) {
        try {
            ServerEvent event = new ServerEvent(server, eventID, JDSServer.getInstance().getCurrServerBean().getId());
            event.setContextMap(eventContext);
            ClusterEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {


    }

}
