package net.ooder.server;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.udp.ClusterCommand;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.cluster.udp.HeardInfo;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSConfig;
import net.ooder.common.MsgStatus;
import net.ooder.server.udp.*;
import net.ooder.thread.JDSThreadFactory;
import net.ooder.web.ConnectionLogFactory;
import net.ooder.web.RemoteConnectionManager;
import net.ooder.web.RuntimeLog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * UDP服务器核心类
 * 提供基于UDP协议的消息推送、集群通信和心跳检测服务
 * 
 * @author ooder team
 * @version 2.0
 * @since 2025-08-25
 */
public class JDSUDPServer {
    public static final String SPLITOR = "|";

    public static JDSUDPServer JDSUDPServer;

    private static JDSUDPServer instance;

    private DatagramSocket pushMsgSocket;

    public static final String THREAD_LOCK = "Thread Lock";


    private String code;

    private Integer port;


    private static final String DEFAULT_UDPCODE = "utf-8";

    private static final int DEFAULT_UDPPORT = 8087;

    // 是否已启动
    public static boolean started = false;

    public static final String SUCCESS_KEY = "success";

    public static final String ERROR_KEY = "error";

    public static final String HIT_KEY = "0";

    public static final String START_KEY = "{";

    public static final String END_KEY = "}";

    public static final String STATUS_SPLIT_KEY = "&&";

    public static final String STATUS_START_KEY = "##||";

    public static final String STATUS_END_KEY = "||##";


    public static final String PERSON_SPLIT_KEY = "||||";
    public static final String PERSON_CLIENT_KEY = "||&&&&";

    public static final String LOGIN_KEY = "{\"event\":1001,\"msgStr\":\"session error\"}";

    public static final String SESSIONID = "sessionId";

    public static final String SYSTEMCODE = "systemCode";

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, JDSUDPServer.class);


    ExecutorService heartService = Executors.newFixedThreadPool(150, new JDSThreadFactory("HeartService"));


    private Map<String, Set<String>> repeatEventKeyMap = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "RepeatEventKeyMap");

    private Map<String, Set<String>> msg2personEventMap = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "Msg2personEventMap");


    static Map<String, ScheduledExecutorService> heartServiceMap = new HashMap<String, ScheduledExecutorService>();


    private static JDSUDPServer udpServer;

    public Set<String> getRepeatEventKey(String systemId) {

        Set<String> eventKeys = repeatEventKeyMap.get(systemId);
        if (eventKeys == null) {
            eventKeys = new HashSet<String>();
            repeatEventKeyMap.put(systemId, eventKeys);
        }
        //  logger.info("start getRepeatEventKey systemId=" + systemId + " serviceKey=" + JSONObject.toJSONString(eventKeys));
        return eventKeys;
    }

    /**
     * 系统定义 消息
     *
     * @return
     */
    public Map<String, Set<String>> getAllRepeatEventKey() {
        Map<String, Set<String>> allRepeatEventKeyMap = new HashMap<String, Set<String>>();

        Set<String> keySet = repeatEventKeyMap.keySet();
        for (String key : keySet) {
            if (key != null) {
                allRepeatEventKeyMap.put(key, repeatEventKeyMap.get(key));
            }
        }
        return allRepeatEventKeyMap;
    }


    /**
     * 开发者订阅消息
     *
     * @return
     */
    public Map<String, Set<String>> getAllPersonRepeatEventKey() {
        Map<String, Set<String>> allRepeatEventKeyMap = new HashMap<String, Set<String>>();

        Set<String> keySet = msg2personEventMap.keySet();
        for (String key : keySet) {
            if (key != null) {
                allRepeatEventKeyMap.put(key, msg2personEventMap.get(key));
            }
        }
        return allRepeatEventKeyMap;
    }


    public void removeRepeatEventKey(String systemId, String serviceKey, String personId) {
        Set<String> eventKeys = this.getRepeatEventKey(systemId);
        eventKeys.remove(serviceKey);
        repeatEventKeyMap.put(systemId, eventKeys);

        Set<String> devPersonEventKeys = this.getRepeatPersonEventKey(systemId, serviceKey);
        devPersonEventKeys.remove(personId);
        msg2personEventMap.put(systemId + "|" + serviceKey, devPersonEventKeys);
    }


    public Set<String> getRepeatPersonEventKey(String systemId, String serviceKey) {
        String eventRepeartKey = systemId + "|" + serviceKey;
        Set<String> personIds = msg2personEventMap.get(eventRepeartKey);
        if (personIds == null) {
            personIds = new LinkedHashSet<String>();
            msg2personEventMap.put(eventRepeartKey, personIds);
        }
        Set<String> personIdSet = new HashSet<String>();
        for (String personId : personIds) {
            if (personId != null && !personId.equals("")) {
                personIdSet.add(personId);
            }

        }

        return personIdSet;
    }

    public void clearEventKey(String systemId) {
        repeatEventKeyMap.put(systemId, new HashSet<>());
    }


    public void addRepeatEventKey(String systemId, String serviceKey, String personId) {
        synchronized (systemId + serviceKey) {
            logger.info("start addRepeatEventKey  personId +" + personId + " systemId=" + systemId + " serviceKey=" + serviceKey);
            Set<String> eventKeys = this.getRepeatEventKey(systemId);
            eventKeys.add(serviceKey);
            Set<String> personIds = getRepeatPersonEventKey(systemId, serviceKey);
            if (!personIds.contains(personId)) {
                personIds.add(personId);
                String eventRepeartKey = systemId + "|" + serviceKey;
                msg2personEventMap.put(eventRepeartKey, personIds);
            }
            repeatEventKeyMap.put(systemId, eventKeys);
        }
    }


    private JDSUDPServer(Integer port, String code) {
        this.code = code;
        this.port = port;
        msg2personEventMap.clear();
        repeatEventKeyMap.clear();

    }

    public boolean started() {
        return started;
    }

    //
    public static JDSUDPServer getInstance() throws JDSException {
        String portStr = JDSConfig.getValue("udpServer.port");
        int port = DEFAULT_UDPPORT;

        if (portStr != null) {
            try {
                port = (Integer) Integer.parseInt(portStr);
            } catch (NumberFormatException nfe) {
                port = DEFAULT_UDPPORT;
            }
        }

        String code = JDSConfig.getValue("udpServer.code");

        if (code == null) {
            code = DEFAULT_UDPCODE;
        }

        try {
            udpServer = getInstance(port, code);
        } catch (JDSException e) {
            logger.error("Failed to get UDP server instance", e);
        }

        return udpServer;

    }

    ;


    /**
     * 取得JDSUDPServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    static JDSUDPServer getInstance(Integer port, String code) throws JDSException {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new JDSUDPServer(port, code);
                    String enable = JDSConfig.getValue("udpServer.enabled");
                    logger.info("************************************************");
                    logger.info(" udpServer enable:" + enable);
                    if (enable != null && Boolean.valueOf(enable)) {
                        Executors.newSingleThreadExecutor(new JDSThreadFactory("JDSServer.startUDPServer")).execute(new Runnable() {
                            public void run() {
                                logger.info("start clearCache ");
                                udpServer.start();
                            }
                        });

                    }
                }
            }
        }
        return instance;
    }

    // 开始P2P交换服务(程序起点)
    public void start() {
        try {
            pushMsgSocket = new DatagramSocket(port);
            startPushMsgSocket();
            started = true;
        } catch (SocketException e) {
            logger.error("Failed to start UDP server on port " + port, e);
            try {
                Thread.sleep(5000);
                this.start();
            } catch (InterruptedException e1) {
                logger.error("Thread interrupted during UDP server restart", e1);
            }

        }

    }

    public synchronized boolean sendHeart(String ip, Integer port) throws JDSException {
        DatagramSocket socket = getInstance().getPushMsgSocket();
        DatagramPacket dp = null;
        try {
            dp = new DatagramPacket((START_KEY + END_KEY).getBytes(), (START_KEY + END_KEY).getBytes().length, InetAddress.getByName(ip), port);
            socket.send(dp);

        } catch (Exception e) {
            logger.error("Failed to send heartbeat to " + ip + ":" + port, e);
            return false;
        }

        if (!socket.isClosed() && socket.isConnected()) {
            return false;
        }
        return true;
    }

    public synchronized boolean send(String msgString, String ip, Integer port) throws JDSException {
        JDSUDPServer udpServer = getInstance();
        DatagramSocket socket = getInstance().getPushMsgSocket();
        //logger.info("msg send=" + ip + port + msgString);
        try {
            msgString = URLEncoder.encode(msgString, udpServer.getCode());
        } catch (UnsupportedEncodingException e2) {
            logger.error("Failed to encode message", e2);
        }
        DatagramPacket dp = null;
        try {
            dp = new DatagramPacket(msgString.getBytes(), msgString.getBytes().length, InetAddress.getByName(ip), port);
            socket.send(dp);

        } catch (Exception e) {
            logger.error("Failed to send message to " + ip + ":" + port, e);
            return false;
        }

        if (!socket.isClosed() && socket.isConnected()) {
            return false;
        }
        return true;
    }

    String encode(String msgString) {
        try {
            msgString = URLEncoder.encode(msgString, this.getCode());
        } catch (UnsupportedEncodingException e2) {
            logger.error("Failed to encode message", e2);
        }
        return msgString;
    }

    private void startPushMsgSocket() {
        JDSUDPServer udpServer = null;
        try {
            udpServer = getInstance();
        } catch (JDSException e1) {
            logger.error("Failed to get UDP server instance", e1);
        }
        byte[] buf = new byte[8192];
        DatagramPacket p = new DatagramPacket(buf, 8192);
        boolean isEnd = false;
        while (!isEnd) {
            try {
                pushMsgSocket.receive(p);
                String content = new String(p.getData(), 0, p.getLength());
                String address = p.getAddress().getHostAddress();
                Integer port = p.getPort();
                String key = address + ":" + port;
                DatagramPacket sendPacket = new DatagramPacket(SUCCESS_KEY.getBytes(), SUCCESS_KEY.getBytes().length, p.getAddress(), port);
                ExecutorService cmdService = RemoteConnectionManager.getConntctionService(key);
                ExecutorService eventService = RemoteConnectionManager.getConntctionService(key + ":event");
                if (content.startsWith(URLEncoder.encode(START_KEY, udpServer.getCode())) && content.endsWith(URLEncoder.encode(END_KEY, udpServer.getCode()))) {
                    content = URLDecoder.decode(content, udpServer.getCode());
                    logger.info("content==========" + content);
                    JSONObject jsonobj = JSONObject.parseObject(content);
                    String sessionId = null;
                    String systemCode = null;

                    if (jsonobj.containsKey(SESSIONID)) {
                        sessionId = jsonobj.getString(SESSIONID);
                    } else {
                        sessionId = jsonobj.getJSONObject("userinfo").getString(SESSIONID);
                    }
                    if (jsonobj.containsKey(SYSTEMCODE)) {
                        systemCode = jsonobj.getString(SYSTEMCODE);
                    } else {
                        if (jsonobj.getJSONObject("userinfo") != null) {
                            systemCode = jsonobj.getJSONObject("userinfo").getString(SYSTEMCODE);
                        } else {
                            throw new JDSException("systemCode not in udpcontent=" + content);
                        }

                    }
                    if (sessionId != null && systemCode != null) {

                        if (jsonobj.containsKey("commandJson")) {
                            logger.info("msg json =======key=" + key + "[" + URLDecoder.decode(content, udpServer.getCode()) + "]");
                            final ClusterCommand command = JSONObject.parseObject(content, ClusterCommand.class);
                            cmdService.execute(new RepeatCMDMsg(command, systemCode));
                        } else if (jsonobj.containsKey("sourceJson")) {
                            logger.info("msg json =======key=" + key + "[" + URLDecoder.decode(content, udpServer.getCode()) + "]");
                            final ClusterEvent event = JSONObject.parseObject(content, ClusterEvent.class);
                            event.setSendTime(System.currentTimeMillis());
                            RepeatEventMsg eventRepeat = new RepeatEventMsg(event, systemCode);
                            eventService.execute(eventRepeat);
                        } else {
                            HeardInfo heardInfo = JSONObject.parseObject(content, HeardInfo.class);

                            heartService.execute(new HeartCommand(heardInfo, p.getAddress(), port));


                        }
                    }
                } else if (content.startsWith(URLEncoder.encode(STATUS_START_KEY, udpServer.getCode())) && content.endsWith(URLEncoder.encode(STATUS_END_KEY, udpServer.getCode()))) {
                    String contentStr = URLDecoder.decode(content, udpServer.getCode());
                    String body = contentStr.substring(STATUS_START_KEY.length(), contentStr.length() - STATUS_END_KEY.length());
                    MsgStatus msgStatus = MsgStatus.READED;
                    String msgId = body;
                    if (body.indexOf(STATUS_SPLIT_KEY) > -1) {
                        String[] strIdArr = body.split(STATUS_SPLIT_KEY);
                        msgId = strIdArr[0];
                        msgStatus = MsgStatus.fromType(strIdArr[1]);
                        RuntimeLog log = ConnectionLogFactory.getInstance().getLog(msgId);
                        if (log != null) {
                            if (msgStatus.equals(MsgStatus.READED)) {
                                log.setArrivedTime(System.currentTimeMillis());
                                log.setTime(log.getArrivedTime() - log.getStartTime());
                            } else {
                                log.setEndTime(System.currentTimeMillis());
                                if (log.getArrivedTime() > 0) {
                                    log.setExetime(log.getEndTime() - log.getArrivedTime());
                                } else {
                                    log.setExetime(log.getEndTime() - log.getStartTime());
                                }

                            }
                            log.setStatus(msgStatus);
                        }

                    }
                } else {

                    // String ucontent = URLDecoder.decode(content, udpServer.getCode());
                    //content.startsWith(URLEncoder.encode(PERSON_CLIENT_KEY, udpServer.getCode()))
                    if (content.indexOf(URLEncoder.encode(PERSON_CLIENT_KEY, udpServer.getCode())) > -1) {
                        RepeatPersonClientMsg command = new RepeatPersonClientMsg(URLDecoder.decode(content, udpServer.getCode()), this.SYSTEMCODE);
                        cmdService.execute(command);
                    } else if (content.indexOf(URLEncoder.encode(PERSON_CLIENT_KEY, udpServer.getCode())) > -1) {
                        RepeatClientMsg command = new RepeatClientMsg(URLDecoder.decode(content, udpServer.getCode()));
                        cmdService.execute(command);
                    } else {

                        //  String ucontent = URLDecoder.decode(content, udpServer.getCode());
                        this.logger.error(content);
                        sendPacket = new DatagramPacket(ERROR_KEY.getBytes(), ERROR_KEY.getBytes().length, p.getAddress(), port);
                        pushMsgSocket.send(sendPacket);

                    }

                }

            } catch (Exception e) {
                logger.error("Error processing UDP message", e);
            }

        }
        pushMsgSocket.close();

    }

    public DatagramSocket getPushMsgSocket() {

        return pushMsgSocket;
    }

    public void setPushMsgSocket(DatagramSocket pushMsgSocket) {
        this.pushMsgSocket = pushMsgSocket;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServerIP() {

        String serverIP = JDSConfig.getValue("udpServer.serverIP");

        return serverIP;

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @param args
     * @throws IOException
     * @throws IOException
     */
}
