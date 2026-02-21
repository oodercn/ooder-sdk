/**
 * $RCSfile: ClusterClientImpl.java,v $
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerBeanManager;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.ServerNodeList;
import net.ooder.cluster.event.ClusterEventControl;
import net.ooder.cluster.event.ServerEvent;
import net.ooder.common.*;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.AppConfig;
import net.ooder.config.CApplication;
import net.ooder.config.JDSConfig;
import net.ooder.config.UserBean;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.ServerStatus;
import net.ooder.server.SubSystem;
import net.ooder.server.ct.CtSubSystem;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class ClusterClientImpl implements ClusterClient {

    private UDPClient udpClient;

    private static final String SYSCODE = "SYSCODE";


    private Map<String, SubSystem> systemInfoCache = new HashMap<String, SubSystem>();

    private List<String> systemIds;

    // 注册的应用和ServerBean的Map
    private static Map<ConfigCode, ServerNodeList> serverNodeListMap = new HashMap<ConfigCode, ServerNodeList>();

    public static final String APPLICATION_GETALLSYSTEMURL = "/api/sys/GetAllSystemInfo";

    public static final String APPLICATION_GETSYSTEMSTATUS = "/api/sys/getAllSystemStatus";

    public static final String APPLICATION_GETSERVERS = "/api/sys/GetAllSystemBeanList";

    public static final String APPLICATION_GETAPPCONFIG = "/api/sys/GetAppLications";

    public static final String APPLICATION_CONFIGFILE_NAME = "application_config.xml";

    public static final String APPLICATION_SERVERCONFIG_NAME = "server_config.xml";

    public static final String GetSubSystemInfoURL = "/api/sys/getSubSystemInfo";

    private Long lasterUpdateTime;

    Map<String, ServerStatus> statusMap = new HashMap<String, ServerStatus>();

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ClusterClientImpl.class);

    // 注册的应用及其配置信息的Map
    private static ConcurrentMap<ConfigCode, CApplication> applicationMap = new ConcurrentHashMap<ConfigCode, CApplication>();

    private static ConcurrentMap<String, CApplication> sysCodeApplicationMap = new ConcurrentHashMap<String, CApplication>();


    public ClusterClientImpl() {
        udpClient = UDPClient.getInstance();
    }

    @Override
    public void start() {
        if (!UserBean.getInstance().getConfigName().equals(ConfigCode.client)) {
            try {
                initCluster();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            new Thread() {
                @Override
                public void run() {
                    udpClient.start();
                }
            }.start();
        }
    }

    @Override
    public UDPClient getUDPClient() {
        return UDPClient.getInstance();
    }

    synchronized void initCluster() throws JDSException {
        this.systemIds = new ArrayList<String>();
        if (!UserBean.getInstance().getConfigName().equals(OrgConstants.CLUSTERCONFIG_KEY)) {
            systemIds = this.getRemoteAllSystem();
            List<CApplication> applications = this.getRemoteAppLications();
            for (CApplication application : applications) {
                applicationMap.put(ConfigCode.fromType(application.getConfigCode()), application);
                ServerNodeList beanList = this.getRemoteServerNodeList(ConfigCode.fromType(application.getConfigCode()));
                if (beanList != null) {
                    serverNodeListMap.put(ConfigCode.fromType(application.getConfigCode()), this.getRemoteServerNodeList(ConfigCode.fromType(application.getConfigCode())));
                }
            }
        } else {
            List<SubSystem> eisystems = OrgManagerFactory.getInstance().getSystems();
            for (SubSystem subSystem : eisystems) {

                if (subSystem != null && subSystem.getSysId() != null) {
                    systemInfoCache.put(subSystem.getSysId(), new CtSubSystem(subSystem));
                    systemIds.add(subSystem.getSysId());
                }

            }
        }
        for (String systemId : systemIds) {
            SubSystem system = this.getRemoteSystem(systemId);
            try {
                if (system != null) {
                    logger.info("initSubSystem cluster configname[" + system.getName() + "]");
                    Map eventContext = new HashMap();
                    eventContext.put(SYSCODE, system.getSysId());
                    //如果是本地应用则总本地装载配额制
                    if (UserBean.getInstance().getConfigName().equals(OrgConstants.CLUSTERCONFIG_KEY)) {
                        CApplication application = applicationMap.get(system.getConfigname());
                        if (application == null) {
                            application = initSubSystem(system);
                            if (application != null) {
                                applicationMap.put(system.getConfigname(), application);
                            } else {
                                throw new JDSException("Application '" + system.getConfigname() + "' not found!", JDSException.ACTIVITYDEFINITIONERROR);
                            }
                        } else {
                            initSubSystem(system);
                        }
                        sysCodeApplicationMap.put(system.getSysId(), application);
                    } else {
                        initRemoteSystemConfig(system.getConfigname(), system);
                        CApplication application = applicationMap.get(system.getConfigname());
                        if (application != null) {
                            sysCodeApplicationMap.put(system.getSysId(), applicationMap.get(system.getConfigname()));
                        }
                    }
                    systemInfoCache.put(system.getSysId(), system);
                } else {
                    logger.error("getRemoteSystem error systemId[" + systemId + "]");
                }

            } catch (JDSException e) {
                logger.error("initSubSystem error configname[" + system.getConfigname() + "]");
            }

        }

    }

    /**
     * 获取指定应用的配置文件字符串
     *
     * @param sysCode 应用代码
     * @return 配置文件字符串
     */
    public String getApplicationConfig(String sysCode) throws JDSException {
        File appHome = new File(JDSConfig.Config.applicationHome().getAbsolutePath() + File.separator + sysCode);
        if (appHome.exists()) {
            File appConfigFile = new File(appHome, APPLICATION_CONFIGFILE_NAME);

            if (appConfigFile.exists()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(appConfigFile), "utf-8"));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    String xml = sb.toString();

                    return xml;
                } catch (IOException fnfe) {
                    logger.error("Load config file for application " + sysCode + " fail.", fnfe);
                    // 获取应用的配置文件时发生错误。
                    throw new JDSException("ErrorCannotGetAppConfig");
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException ioe) {
                            // ignore it
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<CApplication> getRemoteAppLications() throws JDSException {
        String url = APPLICATION_GETAPPCONFIG;
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url);
        Future<Content> future = Async.newInstance().execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {
            }
        });

        List<CApplication> applications = new ArrayList<CApplication>();
        String json = "";
        try {
            json = future.get().asString();

            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                applications = JSONArray.parseArray(data, CApplication.class);
            } else {
                throw new JDSException("无法获取应用配置信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取应用配置信息");
        }

        return applications;
    }


    private List<String> getRemoteAllSystem() throws JDSException {
        String url = APPLICATION_GETALLSYSTEMURL;
        logger.info("getRemoteAllSystem:" + UserBean.getInstance().getServerUrl() + url);
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url);
        Future<Content> future = Async.newInstance().execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {

            }
        });

        List<String> systems = new ArrayList<String>();
        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                List<CtSubSystem> eisystems = JSONArray.parseArray(data, CtSubSystem.class);
                for (SubSystem subSystem : eisystems) {
                    systemInfoCache.put(subSystem.getSysId(), subSystem);
                    systems.add(subSystem.getSysId());
                }
            } else {
                throw new JDSException("无法获取系统信息");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new JDSException("无法获取系统信息" + UserBean.getInstance().getServerUrl() + url);
        }

        return systems;
    }

    void initRemoteSystemConfig(ConfigCode appCode, SubSystem system) throws JDSException {
        ServerNodeList serverBeanList = serverNodeListMap.get(appCode);
        if (serverBeanList == null) {
            serverBeanList = this.getRemoteServerNodeList(appCode);
            serverNodeListMap.put(appCode, serverBeanList);
        }
        if (serverBeanList != null) {
            Set<String> it = serverBeanList.getEsbBeanMap().keySet();
            if (!it.contains(system.getSysId())) {
                ServerNode serverBean = new ServerNode(system);
                serverBeanList.getEsbBeanMap().put(system.getSysId(), serverBean);
            }

        }

    }

    public void login(final Boolean init) {
        if (init) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    udpClient.login();
                }
            }.start();
        } else {
            udpClient.login();
        }


    }

    public void login() {
        login(true);
    }

    public void reboot() {
        udpClient.stop();
        new Thread() {
            @Override
            public void run() {
                //  reLoadServer();
                ClusterClientImpl.this.start();
                udpClient.login();
            }
        }.start();


    }


    public void reLoadConfig() {
        try {
            initCluster();
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }


    public List<ServerNode> getAllServer() {
        List<ServerNode> servers = new ArrayList<ServerNode>();
        Map<String, ServerNode> besnmap = getAllServerMap();
        Set<String> keyset = besnmap.keySet();
        for (String key : keyset) {
            ServerNode severBean = besnmap.get(key);
            if (severBean != null) {
                servers.add(besnmap.get(key));
            }

        }
        return servers;
    }

    public ServerNode getServerNodeById(String nodeId) {

        ServerNode node = getAllServerMap().get(nodeId);
        if (node == null) {
            SubSystem subSystem = this.getSystem(nodeId);
            if (subSystem != null) {
                node = new ServerNode(subSystem);
                getAllServerMap().put(nodeId, node);
            }
        }

        return node;
    }

    public ServerNodeList getServerNodeListByConfigCode(ConfigCode code) {
        return serverNodeListMap.get(code);
    }


    private static ServerNodeList getRemoteServerNodeList(ConfigCode code) throws JDSException {
        String url = APPLICATION_GETSERVERS;
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url + "?code=" + code.getType());
        Future<Content> future = Async.newInstance().execute(request, new FutureCallback<Content>() {
            public void failed(final Exception ex) {
                ex.printStackTrace();
            }

            public void completed(final Content content) {
            }

            public void cancelled() {

            }
        });

        ServerNodeList serverBeanList = new ServerNodeList();
        String json = "";
        try {
            json = future.get().asString();

            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                serverBeanList = JSONObject.parseObject(data, ServerNodeList.class);
            } else {
                throw new JDSException("无法获取应用配置信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取应用配置信息");
        }

        return serverBeanList;
    }

    private SubSystem loadRemoteSystem(String systemCode) throws JDSException {
        String url = GetSubSystemInfoURL;
        final Request request = Request.Post(UserBean.getInstance().getServerUrl() + url + "?systemCode=" + systemCode);
        Future<Content> future = Async.newInstance().execute(request);
        SubSystem system = null;
        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                system = JSONObject.parseObject(data, SubSystem.class);

            } else {
                throw new JDSException("无法获取系统信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取系统信息");
        }

        return system;
    }


    public List<SubSystem> getRemoteSystems() {
        List<SubSystem> subSystems = new ArrayList<SubSystem>();
        for (String systemId : this.systemIds) {
            subSystems.add(this.getRemoteSystem(systemId));
        }
        return subSystems;
    }

    public SubSystem getRemoteSystem(String systemId) {
        SubSystem system = this.systemInfoCache.get(systemId);
        if (system == null) {
            try {
                SubSystem remoteSystem = loadRemoteSystem(systemId);
                if (remoteSystem != null) {
                    system = new CtSubSystem(remoteSystem);
                    systemInfoCache.put(systemId, system);
                }

            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return system;
    }


    public CApplication initSubSystem(SubSystem system) throws JDSException {
        ConfigCode syscode = system.getConfigname();
        CApplication app = null;
        if (syscode == null) {
            syscode = ConfigCode.fromType(JDSConstants.CONFIG_KEY);
        }
        String path = JDSConfig.Config.applicationHome().getAbsolutePath() + File.separator + syscode.getType();
        File applicationPath = new File(path);
        if (applicationPath.isDirectory()) {
            File applicationConfigFile = new File(applicationPath, APPLICATION_CONFIGFILE_NAME);
            if (applicationConfigFile.exists() && applicationConfigFile.isFile()) {
                try {
                    // Create a Reader to the file to unmarshal from
                    Reader reader = new InputStreamReader(new FileInputStream(applicationConfigFile), "utf-8");
                    InputSource is = new InputSource(reader);
                    AppConfig appConfig = new AppConfig(is);
                    reader.close();
                    app = appConfig.getApplication();
                    app.setConfigPath(system.getConfigname().getType());
                    if (app != null && app.getName() != null) {
                        logger.info("Application " + app.getName() + " loaded.");
                        File applicationServerConfigFile = new File(applicationPath, APPLICATION_SERVERCONFIG_NAME);
                        if (applicationServerConfigFile.exists() && applicationServerConfigFile.isFile()) {
                            ServerNodeList serverBeanList = ServerBeanManager.getEsbBeanList(applicationServerConfigFile.getAbsolutePath(), system);
                            serverNodeListMap.put(ConfigCode.fromType(app.getConfigCode()), serverBeanList);
                            Set<String> it = serverBeanList.getEsbBeanMap().keySet();
                            if (!it.contains(system.getSysId())) {
                                ServerNode serverBean = new ServerNode(system);
                                serverBeanList.getEsbBeanMap().put(system.getSysId(), serverBean);
                            }
                        }
                    }

                } catch (Exception me) {
                    throw new JDSException("Failed to load config file " + applicationConfigFile.getAbsolutePath(), me, JDSException.LOADAPPLICATIONCONFIGERROR);
                }
            }
        }
        return app;
    }


    public Map<String, ServerNode> getAllServerMap() {
        Map<String, ServerNode> serverMap = new HashMap<String, ServerNode>();
        Set<ConfigCode> ketSet = serverNodeListMap.keySet();
        for (ConfigCode code : ketSet) {
            ServerNodeList beanList = serverNodeListMap.get(code);
            if (beanList != null) {
                Map<String, ServerNode> beanmap = beanList.getEsbBeanMap();
                serverMap.putAll(beanmap);
            }
        }
        return serverMap;
    }

    @Override
    public CApplication getApplication(ConfigCode systemCode) {
        return this.applicationMap.get(systemCode);
    }

    @Override
    public Map<ConfigCode, CApplication> getApplicationMap() {
        return applicationMap;
    }

    @Override
    public List<CApplication> getApplications() {
        List<CApplication> applications = new ArrayList<CApplication>();
        Map<ConfigCode, CApplication> besnmap = applicationMap;
        Set<ConfigCode> keyset = besnmap.keySet();
        for (ConfigCode key : keyset) {
            CApplication application = besnmap.get(key);
            if (application != null) {
                applications.add(besnmap.get(key));
            }
        }
        return applications;
    }

    @Override
    public SubSystem getSystem(String systemCode) {
        return this.getRemoteSystem(systemCode);
    }

    @Override
    public List<SubSystem> getAllSystem() {
        return getRemoteSystems();
    }

    @Override
    public synchronized SystemStatus getSystemStatus(String systemCode) {
        try {
            String url = APPLICATION_GETSYSTEMSTATUS;
            if (lasterUpdateTime == null || System.currentTimeMillis() - lasterUpdateTime > 60 * 1000) {
                statusMap.clear();
                Request request = Request.Post(UserBean.getInstance().getServerUrl() + url + "?sysCode=" + systemCode);
                Future<Content> future = Async.newInstance().execute(request);
                String json = future.get().asString();
                JSONObject jsonObj = JSONObject.parseObject(json);
                Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
                if (status == 0) {
                    String data = jsonObj.getString("data");
                    List<ServerStatus> serverStatusList = JSONArray.parseArray(data, ServerStatus.class);
                    for (ServerStatus serverStatus : serverStatusList) {
                        statusMap.put(serverStatus.getId(), serverStatus);
                    }
                }
                lasterUpdateTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            logger.error("getSystemStatus  sysCoxde");
            e.printStackTrace();
        }
        ServerStatus serverStatus = statusMap.get(systemCode);
        if (serverStatus != null) {
            return serverStatus.getStatus();
        }


        return SystemStatus.OFFLINE;// 脱机
    }

    @Override
    public Person getAdminPerson(String systemCode) {
        try {
            Person person = null;
            UserBean userBean = UserBean.getInstance();
            if (systemCode == null || systemCode.equals(UserBean.getInstance().getSystemCode()) || !isLogin()) {
                person = new LocalPerson(userBean.getUsername(), userBean.getUsername());
            } else if (this.getSystem(systemCode) != null) {
                String adminId = this.getSystem(systemCode).getAdminId();
                if (adminId != null) {
                    person = OrgManagerFactory.getOrgManager().getPersonByAccount(adminId);
                } else {
                    person = OrgManagerFactory.getOrgManager().getPersonByAccount(this.getSystem(systemCode).getSysId() + this.getSystem(systemCode).getName());
                }
            }
            return person;
        } catch (PersonNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定应用的服务器配置文件字符串
     *
     * @param sysCode 应用代码
     * @return 配置文件字符串
     */
    public String getServerConfig(String sysCode) throws JDSException {
        File appHome = new File(JDSConfig.Config.applicationHome().getAbsolutePath() + File.separator + sysCode);
        File appConfigFile = new File(appHome, APPLICATION_SERVERCONFIG_NAME);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(appConfigFile), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            String xml = sb.toString();
            return xml;
        } catch (IOException fnfe) {
            logger.error("Load config file for application " + sysCode + " fail.", fnfe);
            // 获取应用的配置文件时发生错误。
            throw new JDSException("ErrorCannotGetAppConfig");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                    // ignore it
                }
            }
        }
    }

    public void updateTaskStatus(String id, String readed) {


    }


    public boolean isLogin() {
        if (udpClient != null && udpClient.isLogin) {
            return true;
        }
        return false;
    }


    public boolean send(String msgStr) {
        return udpClient.send(msgStr);
    }

    @Override
    public void stop() {
        udpClient = UDPClient.getInstance();
        new Thread() {
            @Override
            public void run() {
                udpClient.stop();
            }
        }.start();

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

}
