package net.ooder.agent.client.home.engine;

import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.ClassUtility;
import  net.ooder.config.CApplication;
import  net.ooder.config.UserBean;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.msg.MsgClient;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.SubSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: JDS服务器。主要用于处理引擎的启动及初始化配置取得云平台服务接口实现等方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class HomeServer {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, HomeServer.class);

    // 计费服务的单例引用
    private static HomeServer instance;


    // 应用与消息引擎映射
    private static Map<ConfigCode, IOTDataEngine> msgEngines = new HashMap<ConfigCode, IOTDataEngine>();

    // 应用与消息接口映射
    public static Map<JDSSessionHandle, Map<String, MsgClient>> msgServiceMap = new HashMap<JDSSessionHandle, Map<String, MsgClient>>();


    // 应用与网关引擎映射
    private static Map<ConfigCode, CtIotCacheManager> appEngines = new HashMap<ConfigCode, CtIotCacheManager>();

    // 应用与数据引擎映射
    private static Map<ConfigCode, IOTDataEngine> dataEngines = new HashMap<ConfigCode, IOTDataEngine>();

    // 应用与网关链接引擎映射
    public static Map<JDSSessionHandle, Map<ConfigCode, GWClient>> gwServiceMap = new HashMap<JDSSessionHandle, Map<ConfigCode, GWClient>>();

    // 应用与手机APP链接引擎映射
    public static Map<JDSSessionHandle, Map<ConfigCode, AppClient>> appServiceMap = new HashMap<JDSSessionHandle, Map<ConfigCode, AppClient>>();

    private JDSServer jdsServcer;

    private Map<ConfigCode, CApplication> applicationMap;

    public static final String THREAD_LOCK = "Thread Lock";

    private HomeServer() throws HomeException {
        try {
            this.jdsServcer = JDSServer.getInstance();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.applicationMap = JDSServer.getClusterClient().getApplicationMap();
        init();
    }

    public static HomeServer getInstance() throws HomeException {
        if (instance == null) {
            synchronized ("Thread Lock") {
                if (instance == null) {
                    instance = new HomeServer();
                }
            }
        }

        return instance;
    }

    private void init() throws HomeException {
        logger.info("HomeServer starting ...");

        initSubSystem();

        logger.info("HomeServer end ...");
    }

    public void initSubSystem() throws HomeException {

        Map<ConfigCode, CApplication> applicationMap = JDSServer.getClusterClient().getApplicationMap();
        Iterator<ConfigCode> it = applicationMap.keySet().iterator();
        while (it.hasNext()) {
            ConfigCode code = it.next();
            CApplication appConfig = applicationMap.get(code);

            if ((appConfig != null) && (appConfig.getName() != null) && appConfig.getConfigCode().equals(UserBean.getInstance().getConfigName().getType())) {

                if (appConfig.getMsgEngine() != null) {
                    String msgEngineStr = appConfig.getMsgEngine().getImplementation();
                    try {
                        Class clazz = ClassUtility.loadClass(msgEngineStr);
                        Object[] parms = new Object[1];
                        parms[0] = ConfigCode.fromType(appConfig.getConfigCode());
                        Constructor constructor = clazz.getConstructor(new Class[]{ConfigCode.class});
                        IOTDataEngine msgEngine = (IOTDataEngine) constructor.newInstance(parms);
                        msgEngines.put(ConfigCode.fromType(appConfig.getConfigCode()), msgEngine);
                    } catch (ClassNotFoundException cnfe) {
                        throw new HomeException("MsgEngine class '" + msgEngineStr + "' not found.", cnfe, 25);
                    } catch (IllegalAccessException iae) {
                        throw new HomeException("", iae, 25);
                    } catch (InstantiationException ie) {
                        throw new HomeException("", ie, 25);
                    } catch (ClassCastException cce) {
                        throw new HomeException("FileEngine must implement FileEngine interface.", cce, 25);
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
                    CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(ConfigCode.fromType(appConfig.getConfigCode()));


                    msgEngines.put(ConfigCode.fromType(appConfig.getConfigCode()), msgEngine);
                }

            }
            // else {
            // throw new HomeException("dataEngine for application not configured.", 21);
            // }
        }
    }

    public static IOTDataEngine getDataEngine(String systemCode) throws HomeException {
        if (!JDSServer.started) {
            throw new HomeException("JDSServer not started!", 10);
        }
        if (systemCode == null) {
            systemCode = "org";
        }

        IOTDataEngine engine = (IOTDataEngine) dataEngines.get(systemCode);

        if (engine == null) {
            throw new HomeException("dataEngine for system code '" + systemCode + "' not found. Please check the configuration file 'JDS_config.xml'.", 21);
        }

        return engine;
    }

    public static IOTDataEngine getMsgEngine(ConfigCode systemCode) throws HomeException {
        if (!JDSServer.started) {
            throw new HomeException("JDSServer not started!", 10);
        }
        if (systemCode == null) {
            systemCode = ConfigCode.org;
        }
        IOTDataEngine engine = (IOTDataEngine) msgEngines.get(systemCode);

        if (engine == null) {
            engine = CtMsgDataEngine.getEngine(OrgConstants.CONFIG_KEY);
        }

        if (engine == null) {
            throw new HomeException("dataEngine for system code '" + systemCode + "' not found. Please check the configuration file 'JDS_config.xml'.", 21);
        }

        return engine;
    }

    public static CtIotCacheManager getAppEngine() throws HomeException {
        return getAppEngine(ConfigCode.org);
    }

    public static CtIotCacheManager getAppEngine(ConfigCode configCode) throws HomeException {
        if (!JDSServer.started) {
            throw new HomeException("JDSServer not started!", 10);
        }
        if (configCode == null) {
            configCode = ConfigCode.org;
        }

        CtIotCacheManager engine = (CtIotCacheManager) appEngines.get(configCode);

        if (engine == null) {
            engine = CtIotCacheManager.getInstance();
            appEngines.put(configCode, engine);
        }

        return engine;
    }


    public static HomeEventControl getHomeEventControl() throws HomeException {
        if (!JDSServer.started) {
            throw new HomeException("JDSServer not started!", 10);
        }
        return HomeEventControl.getInstance();
    }

    public MsgClient getMsgClient(JDSClientService client) throws HomeException {
        if (client == null) {
            throw new HomeException("Session invalid error!", 200);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        ConfigCode configCode = client.getConfigCode();
        MsgClient msgService = null;
        Map msgClients = (Map) msgServiceMap.get(sessionHandle);
        if (msgClients == null) {
            msgClients = new HashMap();
            msgServiceMap.put(sessionHandle, msgClients);
        }

        if ((msgService = (MsgClient) msgClients.get(configCode)) != null) {
            return msgService;
        }
        CApplication app = (CApplication) this.applicationMap.get(configCode);
        if (app == null) {
            throw new HomeException("The application config for '" + configCode + "' not found!");
        }
        String msgServiceClassName = null;
        if (app.getMsgService() != null) {
            msgServiceClassName = app.getMsgService().getImplementation();
        }

        if ((msgServiceClassName == null) || (msgServiceClassName.equals("")))
            return null;
        try {
            msgService = (MsgClient) ClassUtility.loadClass(msgServiceClassName).newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new HomeException("msgService class '" + msgServiceClassName + "' not found.", cnfe, 24);
        } catch (IllegalAccessException iae) {
            throw new HomeException("", iae, 24);
        } catch (InstantiationException ie) {
            throw new HomeException("", ie, 24);
        } catch (ClassCastException cce) {
            throw new HomeException("AdminService must implement AdminService interface.", cce, 24);
        }

        return msgService;
    }

    public GWClient getGWClient(JDSClientService client) throws HomeException {
        if (client == null) {
            throw new HomeException("Session invalid error!", 200);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        String systemCode = client.getSystemCode();

        GWClient gwService = null;
        Map gwClients = (Map) gwServiceMap.get(sessionHandle);
        if (gwClients == null) {
            gwClients = new HashMap();
            gwServiceMap.put(sessionHandle, gwClients);
        }
        SubSystem system = JDSServer.getClusterClient().getSystem(systemCode);
        if (gwClients.get(client.getConfigCode()) != null) {
            gwService = (GWClient) gwClients.get(client.getConfigCode());
        } else {
            CApplication app = (CApplication) this.applicationMap.get(system.getConfigname());
            if (app == null) {
                throw new HomeException("The application config for '" + client.getConfigCode() + "' not found!");
            }
            String gwServiceClassName = null;
            if (app.getGwService() != null) {
                gwServiceClassName = app.getGwService().getImplementation();
            }

            if ((gwServiceClassName == null) || (gwServiceClassName.equals("")))
                return null;
            try {
                gwService = (GWClient) ClassUtility.loadClass(gwServiceClassName).newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new HomeException("bssService class '" + gwServiceClassName + "' not found.", cnfe, 24);
            } catch (IllegalAccessException iae) {
                throw new HomeException("", iae, 24);
            } catch (InstantiationException ie) {
                throw new HomeException("", ie, 24);
            } catch (ClassCastException cce) {
                throw new HomeException("AdminService must implement AdminService interface.", cce, 24);
            }
        }

        if (gwService != null) {
            gwService.setClientService(client);

            try {
                gwService.connect(client.getConnectInfo());
            } catch (JDSException e) {
                throw new HomeException(e);
            }
            gwClients.put(client.getConfigCode(), gwService);
        }

        return gwService;
    }

    public AppClient getAppClient(JDSClientService client) throws HomeException {
        if (client == null) {
            throw new HomeException("Session invalid error!", 200);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        ConfigCode configCode = client.getConfigCode();
        AppClient appService = null;

        Map appClients = (Map) appServiceMap.get(sessionHandle);
        if (appClients == null) {
            appClients = new HashMap();
            appServiceMap.put(sessionHandle, appClients);
        }

        if ((appService = (AppClient) appClients.get(configCode)) != null) {
            return appService;
        }
        CApplication app = (CApplication) this.applicationMap.get(configCode);
        if (app == null) {
            throw new HomeException("The application config for '" + configCode + "' not found!");
        }
        String appServiceClassName = null;
        if (app.getAppService() != null) {
            appServiceClassName = app.getAppService().getImplementation();
        }

        if ((appServiceClassName == null) || (appServiceClassName.equals(""))) {
            return null;
        }
        try {
            appService = (AppClient) ClassUtility.loadClass(appServiceClassName).newInstance();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new HomeException("AppService class '" + appServiceClassName + "' not found.", cnfe, 24);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new HomeException("", iae, 24);

        } catch (InstantiationException ie) {
            ie.printStackTrace();
            throw new HomeException("", ie, 24);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            throw new HomeException(appServiceClassName + " must implement AppService interface.", cce, 24);
        }

        if (appService != null) {

            appService.setClientService(client);
            try {
                appService.connect(client.getConnectInfo());
            } catch (JDSException e) {
                e.printStackTrace();
                throw new HomeException(e);
            }
            appClients.put(configCode, appService);
        }

        return appService;
    }
}