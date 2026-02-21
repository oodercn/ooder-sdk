/**
 * $RCSfile: JDSServer.java,v $
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
package net.ooder.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.client.JDSSessionFactory;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.event.ClusterEventControl;
import net.ooder.cluster.event.ServerEvent;
import net.ooder.cluster.udp.ClusterClient;
import net.ooder.cluster.udp.ClusterClientImpl;
import net.ooder.common.*;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManager;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.cache.MemCacheManager;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;
import net.ooder.config.CApplication;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSConfig.Config;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.DefaultConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.engine.event.EIServerEvent;
import net.ooder.engine.event.EventControl;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.jds.core.User;
import net.ooder.org.conf.OrgConstants;
import net.ooder.thread.JDSThreadFactory;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: JDS服务器。主要用于处理引擎的启动及初始化配置、登陆、注销、取得JDS客户端服务接口实现等方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: raddev.cnjd
 * </p>
 * hoem
 *
 * @author wenzhang li
 * @version 6.0
 */
public class JDSServer implements Runnable {

    private static final Classpath classPath = new Classpath(System.getProperty("java.class.path"));
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, JDSServer.class);

    static {
        // load the config director
        classPath.addComponent(Config.libPath());
        if (Config.libPath().exists() && Config.libPath().isDirectory()) {
            loadLibs(Config.libPath());
        }
        initPoolClassPah(Config.rootServerHome());
        System.setProperty("java.class.path", classPath.toString());
    }

    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }


    static ExecutorService clearSensorservice = Executors.newFixedThreadPool(50, new JDSThreadFactory("JDSServer.clearSensorservice"));

    public static final ReadWriteLock lock = new ReentrantReadWriteLock(false);

    public static final String PRODUCT_NAME = "JDS Server";

    public static final String PRODUCT_VERSION = "V6.0b";

    public static final String PRODUCT_COPYRIGHT = "Copyright(c)2002 - 2020 ooder.net, All Rights Reserved";

    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");

    private static final long DEFAULT_SESSIONCHECKINTERVAL = 5 * Constants.MINUTE;

    private static final long DEFAULT_SESSIONEXPIRETIME = 30 * Constants.MINUTE;


    private static final int DEFAULT_UDPPORT = 8087;

    private static final String DEFAULT_UDPCODE = "utf-8";

    public static final String START_COMMAND = "START";

    public static final String STOP_COMMAND = "STOP";

    public static final String RESTART_COMMAND = "RESTART";

    public static final String STATUS_COMMAND = "STATUS";

    public static final String COMMAND_SUCCESS = "OK";

    public static final String COMMAND_FAIL = "FAIL";


    public static final String APPLICATION_ESBSYS_URL = "/api/sys/getClusterService";

    public static final String THREAD_LOCK = "Thread Lock";


    // JDS服务的单例引用
    private static JDSServer instance;

    private static ClusterClient clusterClient;


    // 是否已启动
    public static boolean started;


    // SessionHandle和ClientService的Map
    private static ConcurrentMap<String, ConcurrentMap<ConfigCode, JDSClientService>> clientServiceMap = new ConcurrentHashMap<String, ConcurrentMap<ConfigCode, JDSClientService>>();

    // SessionHandle和ConnectInfo的Map
    private static Cache<String, ConnectInfo> sessionhandleConnectInfoCache;

    private static Cache<String, String> sessionhandleSystemCodeCache;

    private static Cache<String, JDSSessionHandle> sessionHandleCache;

    // SessionHandle 的Map，key为用户的ConnectInfo，value为SessionHandle的列表
    private static Cache<String, String> connectHandleCache;

    // SessionHandle和最近连接时间的Map
    protected static Cache<String, Long> connectTimeCache;

    private JDSClientService adminClient;

    private ServerSocket serverSocket;

    private Thread serverThread;

    protected boolean serverRunning = true;

    long expireTime = 0;


    private JDSServer() throws JDSException {
        init();
        this.getClusterClient().start();
        _start();


    }

    /**
     * 取得JDSServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    public static JDSServer getInstance() throws JDSException {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new JDSServer();
                }
            }
        }


        return instance;
    }

    public List<ExpressionTempBean> getClusterSevice() throws JDSException {
        List<ExpressionTempBean> clusterSeviceList = new ArrayList<ExpressionTempBean>();

        if (!UserBean.getInstance().getConfigName().equals(OrgConstants.CLUSTERCONFIG_KEY)) {
            clusterSeviceList = this.getRemoteClusterSevice();
        } else {
            List<EsbBean> esbBeanList = EsbBeanFactory.getInstance().getEsbBeanList();
            for (EsbBean esbBean : esbBeanList) {
                if (esbBean.getEsbtype() != null && esbBean.getEsbtype().equals(EsbBeanType.Cluster)) {
                    List<? extends ServiceBean> list = EsbBeanFactory.getInstance().getAllServiceBeanByEsbKey(esbBean.getId());
                    for (ServiceBean bean : list) {
                        if (bean instanceof ExpressionTempBean) {
                            if (bean.getFlowType() != null) {
                                ((ExpressionTempBean) bean).setFlowType(EsbFlowType.remoteAction);
                            }
                            clusterSeviceList.add((ExpressionTempBean) bean);
                        }
                    }
                }

            }

        }

        return clusterSeviceList;
    }

    ;

    private List<ExpressionTempBean> getRemoteClusterSevice() throws JDSException {
        String url = APPLICATION_ESBSYS_URL;
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

        List<ExpressionTempBean> tempBeanList = new ArrayList<ExpressionTempBean>();

        String json = "";
        try {
            json = future.get().asString();
            JSONObject jsonObj = JSONObject.parseObject(json);
            Integer status = Integer.valueOf(jsonObj.get("requestStatus").toString());
            if (status == 0) {
                String data = jsonObj.getString("data");
                tempBeanList = JSONArray.parseArray(data, ExpressionTempBean.class);
                for (ExpressionTempBean tempBean : tempBeanList) {
                    if (tempBean.getFlowType() != null) {
                        ((ExpressionTempBean) tempBean).setFlowType(EsbFlowType.remoteAction);
                    }
                }
            } else {
                throw new JDSException("无法获取系统信息");
            }

        } catch (Exception e) {
            throw new JDSException("无法获取系统信息");
        }

        return tempBeanList;
    }


    void startUDPServer() {

        String enable = JDSConfig.getValue("udpServer.enabled");

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


        logger.info("************************************************");
        logger.info("canable udpServer :" + enable);

        if (enable != null && Boolean.valueOf(enable)) {

            Executors.newSingleThreadExecutor(new JDSThreadFactory("JDSServer.startUDPServer")).execute(new Runnable() {
                public void run() {
                    logger.info("start clearCache ");
                    clearCache();
                }
            });
        }

    }

    /**
     * JDS服务器初始化方法
     *
     * @throws JDSException
     */
    private void init() throws JDSException {


        String expireTimeStr = JDSConfig.getValue("session.ExpireTime");
        if (expireTimeStr == null) {
            expireTime = DEFAULT_SESSIONEXPIRETIME;
        } else {
            try {
                expireTime = (long) (Double.parseDouble(expireTimeStr) * Constants.MINUTE);
            } catch (NumberFormatException nfe) {
                expireTime = DEFAULT_SESSIONEXPIRETIME;
            }
        }

        sessionhandleConnectInfoCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "SessionhandleConnectInfo", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
        sessionhandleSystemCodeCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "SessionhandleSystemCodeCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
        sessionHandleCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "SessionHandle", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
        connectHandleCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "ConnectHandleCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
        connectTimeCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "ConnectTimeCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        long startTime = System.currentTimeMillis();
        logger.info(PRODUCT_NAME + " - " + PRODUCT_VERSION);
        logger.info(PRODUCT_COPYRIGHT);
        logger.info("************************************************");
        logger.info("-------- JDSServer Initialization ---------");
        logger.info("************************************************");
        logger.info("JDS Home: " + Config.currServerHome().getAbsolutePath());

        // Startup session-validate check task
        boolean isloaddb = true;
        try {
            isloaddb = new Boolean(JDSConfig.getValue("loaddb")).booleanValue();
        } catch (Exception e) {

        }


        // Load core event listeners
        EventControl ec = EventControl.getInstance();
        for (int i = 0; i < ec.coreServerEventListeners.size(); i++) {
            logger.info("Load ServerEventListener: " + ec.coreServerEventListeners.get(i).getClass().getName());
        }

        // Startup session-validate check task
        boolean sessionCheckEnabled = true;
        try {
            sessionCheckEnabled = new Boolean(JDSConfig.getValue("session.enabled")).booleanValue();
        } catch (Exception e) {
        }

        if (sessionCheckEnabled) {
            long checkInterval;

            String checkIntervalStr = JDSConfig.getValue("session.CheckInterval");
            if (checkIntervalStr == null) {
                checkInterval = DEFAULT_SESSIONCHECKINTERVAL;
            } else {
                try {
                    checkInterval = (long) (Double.parseDouble(checkIntervalStr) * Constants.MINUTE);
                } catch (NumberFormatException nfe) {
                    checkInterval = DEFAULT_SESSIONCHECKINTERVAL;
                }
            }

            SessionCheckTask sessionCheckTask = new SessionCheckTask(expireTime);
            Executors.newSingleThreadScheduledExecutor(new JDSThreadFactory("JDSServer.sessionCheckTask")).scheduleWithFixedDelay(sessionCheckTask, 15000, checkInterval, TimeUnit.MILLISECONDS);
        }

        // Start admin thread
        if (Config.startAdminThread()) {
            try {
                // Start admin server
                startListenerThread();
                // Add shutdownHook
                setShutdownHook();
            } catch (IOException ioe) {
                throw new JDSException(" admin thread start failed.", ioe, JDSException.SERVERSTARTERROR);
            }
        }

        long timeCost = System.currentTimeMillis() - startTime;
        logger.info("************************************************");
        logger.info("----- DataBase Initialized in " + timeCost / Constants.SECOND + "s:" + timeCost % Constants.SECOND + " -----");

        startTime = System.currentTimeMillis();

        startUDPServer();

        long timeLoadUDP = System.currentTimeMillis() - startTime;
        logger.info("----- UDPServer Initialized in " + timeLoadUDP / Constants.SECOND + "s:" + timeLoadUDP % Constants.SECOND + " -----");

        startTime = System.currentTimeMillis();

        long timeLoadOrg = System.currentTimeMillis() - startTime;
        logger.info("----- Load Org in " + timeLoadOrg / Constants.SECOND + "s:" + timeLoadOrg % Constants.SECOND + " -----");
        logger.info("************************************************");

    }

    /**
     * jds Server destroy process and it will be invoked while the JVM down.
     */
    private void destroy() {

    }


    /***


     /**
     * JDS服务器启动方法
     *
     * @throws JDSException
     */
    private void _start() throws JDSException {

        if (!started) {
            logger.info("JDS Server starting ...");

            // Dispatch server starting event
            EIServerEvent serverStartingEvent = new EIServerEvent(ServerEventEnums.serverStarting);
            EventControl.getInstance().dispatchEvent(serverStartingEvent);


            logger.info("JDS Server loadLibs ...");

            // load the application directory
            loadLibs(Config.applicationHome());
            // set the classpath/classloader
            logger.info("JDS Server setProperty ...");
            System.setProperty("java.class.path", classPath.toString());


            String currSystemCode = UserBean.getInstance().getSystemCode();

            logger.info("Classpath: " + classPath);

            logger.info("JDS Server opened for business.");
            started = true;

            // Dispatch server started event
            EIServerEvent serverStartedEvent = new EIServerEvent(ServerEventEnums.serverStarted);
            EventControl.getInstance().dispatchEvent(serverStartedEvent);

        }

    }

    private void _stop() throws JDSException {
        if (started) {

            logger.info("JDS Server stopping ...");
            // Dispatch server stopping event
            EIServerEvent serverStoppingEvent = new EIServerEvent(ServerEventEnums.serverStopped);
            EventControl.getInstance().dispatchEvent(serverStoppingEvent);

            Map evetntContext = new HashMap();
            SubSystem system = getClusterClient().getSystem(this.getCurrServerBean().getId());
            fireSeverEvent(system, ServerEventEnums.serverStarting, evetntContext);

            Iterator<String> ite = connectTimeCache.keySet().iterator();
            while (ite.hasNext()) {
                JDSSessionHandle handle = getSessionHandleCache().get(ite.next());
                invalidateSession(handle, false);
            }

        }


        // Clear cache
        Map<String, CacheManager> cacheManagerMap = CacheManagerFactory.getInstance().getCacheManagerMap();
        Iterator<String> it = cacheManagerMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String key = it.next();
            CacheManager cacheManager = cacheManagerMap.get(key);
            if (cacheManager.isCacheEnabled() && (cacheManager instanceof MemCacheManager)) {
                Map cacheMap = cacheManager.getAllCache();
                Iterator cacheNameIte = cacheMap.keySet().iterator();
                while (cacheNameIte.hasNext()) {
                    String cacheName = (String) cacheNameIte.next();
                    Cache cache = (Cache) cacheMap.get(cacheName);
                    cache.clear();
                }
                logger.info("Cache destroyed");
            }
        }
        logger.info("JDS Server start stopped.");

        started = false;
        // Dispatch server stopped event
        EIServerEvent serverStoppedEvent = new EIServerEvent(ServerEventEnums.serverStopped);
        EventControl.getInstance().dispatchEvent(serverStoppedEvent);
        Map evetntContext = new HashMap();
        SubSystem system = getClusterClient().getSystem(this.getCurrServerBean().getId());
        fireSeverEvent(system, ServerEventEnums.serverStopped, evetntContext);


        logger.info("JDS Server stopped.");

    }

    private void _restart() throws JDSException {
        _stop();
        _start();
    }

    private DatabaseMetaData getDatabaseMetaData(Connection connection) {

        if (connection == null) {
            return null;
        }

        DatabaseMetaData dbData = null;
        try {
            dbData = connection.getMetaData();
        } catch (SQLException sqle) {
            logger.error("Unable to get database meta data... Error was:" + sqle.toString());
            return null;
        }

        if (dbData == null) {
            logger.error("Unable to get database meta data; method returned null");
        }

        return dbData;
    }

    private void printDbMiscData(DatabaseMetaData dbData) {
        if (dbData == null) {
            return;
        }
        // Database Info
        if (logger.isInfoEnabled()) {
            try {
                logger.info("Database Product Name is " + dbData.getDatabaseProductName());
                logger.info("Database Product Version is " + dbData.getDatabaseProductVersion());
            } catch (SQLException sqle) {
                logger.warn("Unable to get Database name & version information");
            }
        }
        // JDBC Driver Info
        if (logger.isInfoEnabled()) {
            try {
                logger.info("Database Driver Name is " + dbData.getDriverName());
                logger.info("Database Driver Version is " + dbData.getDriverVersion());
            } catch (SQLException sqle) {
                logger.warn("Unable to get Driver name & version information");
            }
        }
        // Db/Driver support settings
        if (logger.isInfoEnabled()) {
            try {
                logger.info("- supports transactions    [" + dbData.supportsTransactions() + "]*");
                logger.info("- isolation None           [" + dbData.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE) + "]");
                logger.info("- isolation ReadCommitted  [" + dbData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED) + "]");
                logger.info("- isolation ReadUncommitted[" + dbData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED) + "]");
                logger.info("- isolation RepeatableRead [" + dbData.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ) + "]");
                logger.info("- isolation Serializable   [" + dbData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE) + "]");
                logger.info("- is case sensitive        [" + dbData.supportsMixedCaseIdentifiers() + "]");
                logger.info("- stores LowerCase         [" + dbData.storesLowerCaseIdentifiers() + "]");
                logger.info("- stores MixedCase         [" + dbData.storesMixedCaseIdentifiers() + "]");
                logger.info("- stores UpperCase         [" + dbData.storesUpperCaseIdentifiers() + "]");
                logger.info("- max table name length    [" + dbData.getMaxTableNameLength() + "]");
                logger.info("- max column name length   [" + dbData.getMaxColumnNameLength() + "]");
                logger.info("- max schema name length   [" + dbData.getMaxSchemaNameLength() + "]");
                logger.info("- concurrent connections   [" + dbData.getMaxConnections() + "]");
                logger.info("- concurrent statements    [" + dbData.getMaxStatements() + "]");
                logger.info("- ANSI SQL92 Entry         [" + dbData.supportsANSI92EntryLevelSQL() + "]");
                logger.info("- ANSI SQL92 Itermediate   [" + dbData.supportsANSI92IntermediateSQL() + "]");
                logger.info("- ANSI SQL92 Full          [" + dbData.supportsANSI92FullSQL() + "]");
                logger.info("- ODBC SQL Grammar Core    [" + dbData.supportsCoreSQLGrammar() + "]");
                logger.info("- ODBC SQL Grammar Extended[" + dbData.supportsExtendedSQLGrammar() + "]");
                logger.info("- ODBC SQL Grammar Minimum [" + dbData.supportsMinimumSQLGrammar() + "]");
                logger.info("- outer joins              [" + dbData.supportsOuterJoins() + "]*");
                logger.info("- limited outer joins      [" + dbData.supportsLimitedOuterJoins() + "]");
                logger.info("- full outer joins         [" + dbData.supportsFullOuterJoins() + "]");
                logger.info("- group by                 [" + dbData.supportsGroupBy() + "]*");
                logger.info("- group by not in select   [" + dbData.supportsGroupByUnrelated() + "]");
                logger.info("- column aliasing          [" + dbData.supportsColumnAliasing() + "]");
                logger.info("- order by not in select   [" + dbData.supportsOrderByUnrelated() + "]");

                logger.info("- alter table add column   [" + dbData.supportsAlterTableWithAddColumn() + "]*");
                logger.info("- non-nullable column      [" + dbData.supportsNonNullableColumns() + "]*");
            } catch (Exception e) {
                logger.warn("Unable to get misc. support/setting information");
            }
        }
    }

    /**
     * 用于新建jdsClientService实例的方法。
     *
     * @return jdsClientService的实例
     */
    public JDSClientService newJDSClientService(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {

        if (!started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }

        if (configCode == null) {
            throw new JDSException("systemCode is null!", JDSException.SERVERNOTSTARTEDERROR);
        }

        JDSClientService jdsService = null;
        ConnectionHandle handle = null;
        CApplication app = this.getClusterClient().getApplication(configCode);
        if (app == null) {
            jdsService = new JDSClientServiceImpl(sessionHandle, configCode);
            if (jdsService.getConnectInfo() != null) {
                this.connect(jdsService);
            }
            // throw new JDSException("The application config for '" + configCode + "' not found!");
        } else {
            if (app.getJdsService() != null) {
                String jdsServiceStr = app.getJdsService().getImplementation();
                try {
                    Class clazz = (Class) ClassUtility.loadClass(jdsServiceStr);
                    Object[] parms = new Object[2];
                    ConnectInfo connectInfo = this.getConnectInfo(sessionHandle);
                    parms[0] = connectInfo;
                    parms[1] = configCode;
                    Constructor constructor = clazz.getConstructor(new Class[]{ConnectInfo.class, ConfigCode.class});
                    JDSClientService clientServiec = (JDSClientService) constructor.newInstance(parms);

                } catch (ClassNotFoundException cnfe) {
                    throw new JDSException("JDSClientService class '" + jdsServiceStr + "' not found.", cnfe, JDSException.LOADRIGHTENGINEERROR);
                } catch (IllegalAccessException iae) {
                    throw new JDSException("", iae, JDSException.LOADRIGHTENGINEERROR);
                } catch (InstantiationException ie) {
                    throw new JDSException("", ie, JDSException.LOADRIGHTENGINEERROR);
                } catch (ClassCastException cce) {
                    throw new JDSException(jdsServiceStr + " must implement " + JDSClientService.class + " interface.", cce, JDSException.LOADRIGHTENGINEERROR);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                jdsService = new JDSClientServiceImpl(sessionHandle, configCode);
                ConnectInfo connectInfo = this.getConnectInfo(sessionHandle);
                // jdsService.connect(connectInfo);
                if (jdsService.getConnectInfo() != null) {
                    this.connect(jdsService);
                }
            }

            if (app.getConnectionHandle() != null) {
                String connectionHandleStr = app.getConnectionHandle().getImplementation();
                try {
                    Class clazz = (Class) ClassUtility.loadClass(connectionHandleStr);
                    Object[] parms = new Object[3];
                    parms[0] = jdsService;
                    parms[1] = sessionHandle;
                    parms[2] = jdsService.getSystemCode();

                    Constructor constructor = clazz.getConstructor(new Class[]{JDSClientService.class, JDSSessionHandle.class, String.class});
                    handle = (ConnectionHandle) constructor.newInstance(parms);

                } catch (Exception e) {
                    handle = new DefaultConnectionHandle(jdsService, sessionHandle, configCode);
                    // e.printStackTrace();
                }
            } else {
                handle = new DefaultConnectionHandle(jdsService, sessionHandle, configCode);

            }
        }

        jdsService.setConnectionHandle(handle);
        return jdsService;
    }

    public boolean isCometServer() {

        return EsbBeanFactory.getInstance().getIdMap().containsKey("RepeatCommand");
    }


    /**
     * 用于根据连接的Session信息以及应用的标识取得JDSClientService实例的方法。
     *
     * @param sessionHandle Session连接信息
     * @return JClientService的已有实例。例如：HA代表home系统服务，wf代表wf系统服务等等。
     */
    public JDSClientService getJDSClientService(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        if (!started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        if (sessionHandle == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        ConcurrentMap<ConfigCode, JDSClientService> clients = clientServiceMap.get(sessionHandle.getSessionID());
        JDSClientService clientService = null;

        if (clients == null) {
            clients = new ConcurrentHashMap();
        }
        if (configCode == null) {
            configCode = OrgConstants.CLUSTERCONFIG_KEY;
        }
        if (!clients.containsKey(configCode)) {
            clientService = this.newJDSClientService(sessionHandle, configCode);

            if (configCode.getType().equals(this.getCurrServerBean().getId())) {
                try {
                    this.connect(clientService);
                } catch (JDSException e) {

                }
            }
            clients.put(configCode, clientService);
            clientServiceMap.put(sessionHandle.getSessionID(), clients);
        } else {
            clientService = clients.get(configCode);
        }
        // clientService.setOrgManager(this.orgManagerMap.get(systemCode));
        return clientService;
    }


    // =====================================Session管理相关方法

    public static void activeSession(JDSSessionHandle sessionHandle) {
        // Long connectTime = (Long) connectTimeCache.get(sessionHandle);
        // if (connectTime != null) {
        connectTimeCache.put(sessionHandle.getSessionID(), System.currentTimeMillis());
        sessionHandleCache.put(sessionHandle.getSessionID(), sessionHandle);
        // }s
    }

    public void invalidateSession(List<JDSSessionHandle> sessionHandleList) {

        for (int i = 0; i < sessionHandleList.size(); i++) {
            final JDSSessionHandle sessionHandle = sessionHandleList.get(i);
            clearSensorservice.submit(new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().setName("disconnect " + sessionHandle);
                        JDSServer.getInstance().disconnect(sessionHandle);
                    } catch (JDSException e) {
                        invalidateSession(sessionHandle, true);
                    }
                }

            });
        }

    }

    private void invalidateSession(JDSSessionHandle sessionHandle, boolean msgClient) {
        if (sessionHandle == null)
            return;

        clientServiceMap.remove(sessionHandle.getSessionID());
        connectTimeCache.remove(sessionHandle.getSessionID());
        sessionHandleCache.remove(sessionHandle.getSessionID());

        ConnectInfo connectInfo = sessionhandleConnectInfoCache.get(sessionHandle.getSessionID());
        if (connectInfo == null)
            return;
        // lock.writeLock().lock();
        Set<JDSSessionHandle> sessionHandleList = getJDSSessionHandlist(connectInfo);
        if (sessionHandleList != null && sessionHandleList.contains(sessionHandle)) {
            sessionHandleList.remove(sessionHandle);
            // 提交分布式CACHE
            updateHandle(connectInfo, sessionHandleList);
        }
        // lock.writeLock().unlock();
        sessionhandleConnectInfoCache.remove(sessionHandle.getSessionID());
        logger.debug("Invalidate Session for user '" + connectInfo.getLoginName() + "'");
    }

    public Set<JDSSessionHandle> getJDSSessionHandlist(ConnectInfo connectInfo) {
        String handleList = connectHandleCache.get(connectInfo.getUserID());
        Set<JDSSessionHandle> realSessionHandle = new LinkedHashSet<>();
        List<JDSSessionHandle> sessionHandleList = null;
        if (handleList != null) {
            sessionHandleList = JSONArray.parseArray(handleList, JDSSessionHandle.class);
            for (JDSSessionHandle handle : sessionHandleList) {
                if (handle != null && this.getSessionHandleCache().get(handle.getSessionID()) != null) {
                    realSessionHandle.add(this.getSessionHandleCache().get(handle.getSessionID()));
                }
            }
        }
        return realSessionHandle;
    }

    public void updateHandle(ConnectInfo connectInfo, Set<JDSSessionHandle> sessionHandleList) {

        String json = JSONArray.toJSONString(sessionHandleList, config);
        connectHandleCache.put(connectInfo.getUserID(), json);
        // 仅作本地缓存处理，请勿使用集群属性；

    }

    // =====================================处理服务器连接相关方法

    /**
     * 处理登陆JDS服务器的方法
     *
     * @param clientService 连接信息，包括登陆名和密码
     * @return
     * @throws JDSException
     */
    public JDSSessionHandle connect(JDSClientService clientService) throws JDSException {

        if (!started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }

        JDSSessionHandle sessionHandle = clientService.getSessionHandle();
        ConnectInfo connectInfo = clientService.getConnectInfo();

        if (sessionHandle == null) {
            throw new JDSException("sessionHandle  is null", JDSException.NOTLOGINEDERROR);
        }

        if (connectInfo == null) {
            connectInfo = sessionhandleConnectInfoCache.get(sessionHandle.getSessionID());
            if (connectInfo != null) {
                clientService.connect(connectInfo);
            }
        }

        if (connectInfo == null && clientService.getConnectionHandle() != null) {
            connectInfo = clientService.getConnectionHandle().getConnectInfo();
            if (connectInfo != null) {
                clientService.connect(connectInfo);
            }
        }

        if (connectInfo == null) {
            throw new JDSException("connectInfo  is null", JDSException.NOTLOGINEDERROR);
        }

        Set<JDSSessionHandle> sessionHandleList = getJDSSessionHandlist(connectInfo);

        if (Config.singleLogin()) {
            // if (connectInfoMap.values().contains(connectInfo)) {
            if (sessionHandleList != null && sessionHandleList.size() != 0) {
                // 当前用户在未登陆过的Session登陆
                if (!sessionHandleList.contains(sessionHandle))
                    throw new JDSException("User '" + connectInfo.getLoginName() + "' already logined!", JDSException.ALREADYLOGINEDERROR);
            }
        }

        ConfigCode configCode = clientService.getConfigCode();
        // 第一次登录
        if (sessionHandleList == null) {
            sessionHandleList = new LinkedHashSet<JDSSessionHandle>();

        }
        // 同一个用户在已登陆过的Session登陆
        if (sessionHandleList.contains(sessionHandle)) {
            Map<ConfigCode, JDSClientService> clients = clientServiceMap.get(sessionHandle.getSessionID());
            if (clients == null) {
                clients = new ConcurrentHashMap<ConfigCode, JDSClientService>();
                clientServiceMap.put(sessionHandle.getSessionID(), (ConcurrentMap<ConfigCode, JDSClientService>) clients);
            }
            clients.put(configCode, clientService);
        } else { // 当前用户在未登陆过的Session登陆

            // 如果该Session已存在，需要先invalidate该Session
            invalidateSession(sessionHandle, false);
            sessionHandleList.add(sessionHandle);
            ConcurrentMap<ConfigCode, JDSClientService> clients = new ConcurrentHashMap<ConfigCode, JDSClientService>();
            clients.put(configCode, clientService);
            clientServiceMap.put(sessionHandle.getSessionID(), clients);
            // 刷新分布式CACHE
            this.updateHandle(connectInfo, sessionHandleList);
        }
        //修改且集群可读取
        sessionHandleCache.put(sessionHandle.getSessionID(), sessionHandle);
        sessionhandleConnectInfoCache.put(sessionHandle.getSessionID(), connectInfo);

        // 更新用户权限信息
        JDSActionContext.getActionContext().getSession().put(JDSContext.JDSUSERID, connectInfo.getUserID());

        if (JDSActionContext.getActionContext().getSessionId() == null) {
            JDSActionContext.getActionContext().getSession().put(JDSActionContext.JSESSIONID, clientService.getSessionHandle().getSessionID());
        }
        long currentTime = System.currentTimeMillis();
        connectTimeCache.put(sessionHandle.getSessionID(), currentTime);
        if (isCometServer()) {
            JDSServer.getSessionhandleSystemCodeCache().put(sessionHandle.getSessionID(), JDSServer.getInstance().getCurrServerBean().getId());
        }

        return sessionHandle;
    }

    /**
     * 处理注销JDS服务器的方法
     *
     * @param sessionHandle
     * @throws JDSException
     */
    public void disconnect(JDSSessionHandle sessionHandle) throws JDSException {
        if (!started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        ConnectInfo connectInfo = getConnectInfo(sessionHandle);

//        // 注销集群链接
//        String systemCode = this.getSessionhandleSystemCodeCache().get(sessionHandle.getSessionID());
//
//        if (systemCode != null) {
//            Cache cache = this.getSystemConnectTimeCache(systemCode);
//            cache.remove(sessionHandle.getSessionID());
//            getSessionhandleSystemCodeCache().remove(sessionHandle.getSessionID());
//        }

        // 使Session实效
        invalidateSession(sessionHandle, true);

        long currentTime = System.currentTimeMillis();

        if (connectInfo != null) {
            logger.info("User '" + connectInfo.getLoginName() + "' logout at " + dateFormat.format(new Date(currentTime)) + ".");
        }

    }

    // =============================================JDS服务器管理相关方法

    public static void start() throws JDSException {
        try {
            sendSocketCommand(JDSServer.START_COMMAND);
        } catch (IOException ioe) {
            throw new JDSException("jds Server START command error.", ioe, JDSException.SERVERSTARTCOMMANDERROR);
        }

    }

    public static String status() throws JDSException {
        try {
            return sendSocketCommand(JDSServer.STATUS_COMMAND);
        } catch (ConnectException e) {
            return "Connect to server failed.";
        } catch (IOException e) {
            throw new JDSException("jds Server STATUS command error.", e, JDSException.SERVERSTATUSCOMMANDERROR);
        }
    }

    public static String stop() throws JDSException {
        try {
            return sendSocketCommand(JDSServer.STOP_COMMAND);
        } catch (IOException ioe) {
            throw new JDSException("jds Server STOP command error.", ioe, JDSException.SERVERSTOPCOMMANDERROR);
        }
    }

    public static String restart() throws JDSException {
        try {
            return sendSocketCommand(JDSServer.RESTART_COMMAND);
        } catch (IOException ioe) {
            throw new JDSException("JDS Server RESTART command error.", ioe, JDSException.SERVERRESTARTCOMMANDERROR);
        }
    }

    public boolean started() {
        return started;
    }

    public void run() {
        while (serverRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Received connection from - " + clientSocket.getInetAddress() + " : " + clientSocket.getPort());
                processClientRequest(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    private void startListenerThread() throws IOException {
        try {

            this.serverSocket = new ServerSocket(Config.adminPort(), 1, Config.adminAddress());
            this.serverThread = new Thread(this, this.toString());
            this.serverThread.setDaemon(false);
            this.serverThread.start();
            logger.info("Startup admin listener thread [" + Config.adminAddress().getHostName() + ":" + Config.adminPort() + "]");
        } catch (BindException be) {
            // ignore it
        }
    }

    private void processClientRequest(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String request = reader.readLine();

        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        writer.println(processRequest(request, client));
        writer.flush();

        writer.close();
        reader.close();
    }

    private String processRequest(String request, Socket client) {
        if (request != null) {
            String key = request.substring(0, request.indexOf(':'));
            String command = request.substring(request.indexOf(':') + 1);
            if (!key.equals(Config.adminKey())) {
                return "Admin key ERROR";
            } else {
                logger.trace("Command '" + command + "' issued from: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                if (command.equals(JDSServer.START_COMMAND)) {
                    try {
                        _start();
                        return COMMAND_SUCCESS;
                    } catch (JDSException e) {
                        logger.error("Command '" + command + "' failed.", e);
                        return COMMAND_FAIL;
                    }
                } else if (command.equals(JDSServer.STOP_COMMAND)) {
                    try {
                        _stop();
                        return COMMAND_SUCCESS;
                    } catch (JDSException e) {
                        logger.error("Command '" + command + "' failed.", e);
                        return COMMAND_FAIL;
                    }
                } else if (command.equals(JDSServer.RESTART_COMMAND)) {
                    try {
                        _restart();
                        return COMMAND_SUCCESS;
                    } catch (JDSException e) {
                        logger.error("Command '" + command + "' failed.", e);
                        return COMMAND_FAIL;
                    }
                } else if (command.equals(JDSServer.STATUS_COMMAND)) {
                    return started ? "Running" : "Stopped";
                } else {
                    return "Unknown Command";
                }
            }
        } else {
            return COMMAND_FAIL;
        }
    }

    private static void loadLibs(File libDir) {
        if (libDir.exists() && libDir.isDirectory()) {

            File files[] = libDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    if (files[i].isDirectory()) {
                        loadLibs(files[i]);
                    } else if (fileName.endsWith(".jar") || fileName.endsWith(".zip") || fileName.endsWith(".class")) {
                        classPath.addComponent(files[i]);

                    }
                }
            }

        }
    }

    private static void initPoolClassPah(File libDir) {
        try {
            ClassPool pool = ClassPool.getDefault();
            String absolutePath = JDSConfig.getAbsolutePath("", null);
            if (absolutePath != null) {
                pool.appendClassPath(absolutePath);
            }
            logger.info("absolutePath= " + absolutePath);

            File jarFile = new File(absolutePath + File.separator + ".." + File.separator + ".." + File.separator + "WEB-INF" + File.separator + "lib");

            logger.info("initPoolClassPah =" + jarFile.getAbsolutePath());
            if (!jarFile.exists() && libDir.exists() && libDir.getParentFile() != null && libDir.getParentFile().getParentFile() != null) {
                jarFile = new File(libDir.getParentFile().getParentFile().getAbsolutePath() + File.separator + "WEB-INF" + File.separator + "lib");
            }


            if (jarFile.exists() && jarFile.isDirectory()) {
                File files[] = jarFile.listFiles();
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                        pool.appendClassPath(files[i].getPath());
                    }
                }
            }


        } catch (NotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private static String sendSocketCommand(String command) throws IOException, ConnectException {
        Socket socket = new Socket(Config.adminAddress(), Config.adminPort());

        // send the command
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(Config.adminKey() + ":" + command);

        // read the reply
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = null;

        try {
            int tryCounts = 0;
            while (!reader.ready() && tryCounts < 5) {
                tryCounts++;
                Thread.sleep(50);
            }
        } catch (InterruptedException ie) {
        }

        if (reader.ready()) {
            response = reader.readLine();
        }

        reader.close();

        // close the socket
        writer.close();
        socket.close();

        return response;
    }

    private void setShutdownHook() {
        try {
            Method shutdownHook = java.lang.Runtime.class.getMethod("addShutdownHook", new Class[]{java.lang.Thread.class});
            Thread hook = new Thread() {

                public void run() {
                    setName("JDS_Shutdown_Hook");

                    serverRunning = false;
                    JDSServer.this.destroy();

                    // Try to avoid JVM crash
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            shutdownHook.invoke(Runtime.getRuntime(), new Object[]{hook});
        } catch (Exception e) {
            // VM Does not support shutdown hook
            e.printStackTrace();
        }
    }
//

    /**
     * 保证每次取得的列表是最新的
     *
     * @param connectInfo
     * @return
     */
    public Set<JDSSessionHandle> getSessionHandleList(ConnectInfo connectInfo) {
        Set<JDSSessionHandle> sessionHandleList = new LinkedHashSet<>();
        Set<JDSSessionHandle> serverHandleList = getJDSSessionHandlist(connectInfo);
        if (serverHandleList != null) {
            for (JDSSessionHandle handle : serverHandleList) {
                if (expireTime > 0) {
                    long currentTime = System.currentTimeMillis();
                    Long loginTime = (Long) getConnectTimeCache().get(handle.getSessionID());
                    // int expireTime = 30 * 1000;
                    if (loginTime != null && ((currentTime - loginTime.longValue()) < expireTime)) {
                        sessionHandleList.add(handle);
                    }
//                    else {
//                        connectTimeCache.put(handle.getSessionID(), System.currentTimeMillis());
//                        sessionHandleList.add(handle);
//                    }
                } else {
                    sessionHandleList.add(handle);
                }
            }
        }
        return sessionHandleList;

    }

    public Map<String, Map<ConfigCode, JDSClientService>> getClientServiceMap() {
        Map<String, Map<ConfigCode, JDSClientService>> clientMap = new HashMap<String, Map<ConfigCode, JDSClientService>>();
        clientMap.putAll(clientServiceMap);
        return clientMap;
    }

    public Cache<String, Long> getConnectTimeCache() {
        return connectTimeCache;
    }

    public ConnectInfo getConnectInfo(JDSSessionHandle handle) {
        ConnectInfo connectInfo = sessionhandleConnectInfoCache.get(handle.getSessionID());
        return connectInfo;
    }


    /**
     * 非缓存方法小心调用
     *
     * @return
     */
    public static List<ConnectInfo> getAllConnectInfo() {
        List<ConnectInfo> infoList = new ArrayList<ConnectInfo>();
        List<ConnectInfo> connectInfos = (List<ConnectInfo>) sessionhandleConnectInfoCache.values();
        for (ConnectInfo connectInfo : connectInfos) {
            if (!infoList.contains(connectInfo)) {
                infoList.add(connectInfo);
            }
        }

        return infoList;
    }

    //

    public Cache<String, JDSSessionHandle> getSessionHandleCache() {
        return sessionHandleCache;
    }

    public static ClusterClient getClusterClient() {
        if (clusterClient == null) {
            synchronized (THREAD_LOCK) {
                if (clusterClient == null) {
                    clusterClient = new ClusterClientImpl();
                }
            }
        }

        return clusterClient;
    }


    public static Cache<String, String> getSessionhandleSystemCodeCache() {
        return sessionhandleSystemCodeCache;
    }


    public User getAdminUser() {
        return getClusterClient().getUDPClient().getUser();
    }

    public JDSClientService getAdminClient() throws JDSException {
        if (adminClient == null) {
            User user = getAdminUser();
            String adminId = user.getSessionId();
            JDSSessionHandle handle = getSessionHandleCache().get(adminId);
            if (handle == null) {
                handle = JDSSessionFactory.newSessionHandle(adminId);
            }
            ConfigCode configCode = user.getConfigName();
            if (configCode == null && user.getSystemCode() != null) {
                configCode = ConfigCode.fromType(user.getSystemCode());
            }

            adminClient = JDSServer.getInstance().getJDSClientService(handle, configCode);
            if (adminClient.getConnectInfo() == null || !adminClient.getConnectInfo().getLoginName().equals(user.getAccount())) {
                adminClient.connect(new ConnectInfo(user.getId(), user.getAccount(), user.getPassword()));
            }
        }

        return adminClient;
    }

    public ServerNode getCurrServerBean() {
        String currSystemCode = UserBean.getInstance().getSystemCode();
        ServerNode currServerBean = null;
        if (getClusterClient().isLogin()) {
            currServerBean = getClusterClient().getServerNodeById(currSystemCode);
            if (currServerBean == null) {
                logger.error("************************************************");
                logger.error("System error!");
                logger.error("System error! CurrSystem [" + currSystemCode + "] not registers in cluster!");
                logger.error("custerServer URL is " + UserBean.getInstance().getServerUrl());
                logger.error("place register  is frist!");
                logger.error("************************************************");
            }
        } else {
            currServerBean = new ServerNode(UserBean.getInstance());
        }


        return currServerBean;
    }


    public CApplication getCurrApplication() {
        String currSystemCode = UserBean.getInstance().getSystemCode();
        ServerNode currServerBean = getClusterClient().getServerNodeById(currSystemCode);

        SubSystem system = JDSServer.getClusterClient().getSystem(currServerBean.getId());

        CApplication application = JDSServer.getClusterClient().getApplication(system.getConfigname());
        application.setSysId(system.getSysId());

        if (application == null) {
            logger.error("************************************************");
            logger.error("Confit error!");
            logger.error("System error! CurrSystem [" + currSystemCode + "] not registers in cluster!");
            logger.error("custerServer URL is " + UserBean.getInstance().getServerUrl());
            logger.error("place register  is frist!");
            logger.error("************************************************");
        }

        return application;
    }


//
//    public Cache<String, Long> getSystemConnectTimeCache(String systemCode) {
//        Cache<String, Long> defauleCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "ConnectTimeCache[org]");
//        return defauleCache;
//    }

    public void clearCache() {

        sessionhandleConnectInfoCache.clear();
        sessionhandleSystemCodeCache.clear();
        sessionHandleCache.clear();
        connectHandleCache.clear();
        connectTimeCache.clear();
//        Map<String, Cache> cacheMap = new HashMap<String, Cache>();
//        Map<String, CacheManager> cacheManagerMap = CacheManagerFactory.getInstance().getCacheManagerMap();
//        Iterator<String> cacheit = cacheManagerMap.keySet().iterator();
//        CacheManager subCacheManager = cacheManagerMap.get(JDSConstants.CONFIG_KEY);
//        if (subCacheManager != null) {
//            cacheMap.putAll(subCacheManager.getAllCache());
//            Set<String> keySet = cacheMap.keySet();
//            for (String key : keySet) {
//                logger.info(key);
//                Cache cache = (Cache) cacheMap.get(key);
//                if (cache != null) {
//                    cache.clear();
//                }
//            }
//        }


    }


    private void fireSeverEvent(SubSystem server, ServerEventEnums eventID, Map eventContext) {
        try {

            ServerEvent event = new ServerEvent(server, eventID, getCurrServerBean().getId());
            event.setContextMap(eventContext);
            ClusterEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
