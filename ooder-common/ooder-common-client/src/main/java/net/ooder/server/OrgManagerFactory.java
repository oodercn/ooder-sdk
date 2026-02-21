/**
 * $RCSfile: OrgManagerFactory.java,v $
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

import net.ooder.app.AppManager;
import net.ooder.common.CommonConfig;
import net.ooder.common.ConfigCode;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.OrgManager;
import net.ooder.annotation.Permissions;
import net.ooder.org.Person;
import net.ooder.org.conf.OrgConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;


public abstract class OrgManagerFactory {
    protected static Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), OrgManagerFactory.class);
    private static OrgManagerFactory factory = null;

    private static Map<ConfigCode, AppManager> appManagerMap = new HashMap<ConfigCode, AppManager>();

    public Map<String, SubSystem> systemMap = new HashMap<String, SubSystem>();// CacheManagerFactory.getCache(OrgConstants.CONFIG_KEY,

    public static ConcurrentMap<ConfigCode, OrgManager> orgManagerMap = new ConcurrentHashMap<ConfigCode, OrgManager>();

    private static Map<String, ScheduledExecutorService> threadPoolMap = new HashMap<String, ScheduledExecutorService>();

    public static final String THREAD_LOCK = "Thread Lock";


    private static ConfigCode DEFAULTSYSCODE = ConfigCode.org;


    public OrgManagerFactory() {
        if (systemMap.isEmpty()) {
            getSystems();
        }

    }

    /**
     * 取得组织机构工厂类的实例
     *
     * @return
     */
    public synchronized static OrgManagerFactory getInstance() {

        if (factory == null) {
            synchronized (THREAD_LOCK) {
                if (!UserBean.getInstance().getConfigName().equals(OrgConstants.CONFIG_KEY) && !UserBean.getInstance().getConfigName().equals(OrgConstants.CLUSTERCONFIG_KEY)) {
                    factory = new LocalOrgManagerFactory();
                } else {
                    try {
                        Class c = ClassUtility.loadClass(CommonConfig.getValue(OrgConstants.ORGMANAGERFACTORYCLASSNAME_KEY));
                        factory = (OrgManagerFactory) c.newInstance();
                    } catch (Exception ex) {
                        //  ex.printStackTrace();
                    }
                    if (factory == null) {
                        try {
                            Class c = ClassUtility.loadClass("net.ooder.common.org.impl.database.DbOrgManagerFactory");
                            factory = (OrgManagerFactory) c.newInstance();
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }


                }
            }
        }


        return factory;
    }

    public synchronized void reLoadSystem() {
        this.systemMap.clear();
        List<SubSystem> systems = getSystems();

    }

    public final static OrgManager getOrgManager() {
        ConfigCode subSystemId = JDSActionContext.getActionContext().getConfigCode();
        if (subSystemId == null) {
            subSystemId = DEFAULTSYSCODE;
        }
        return getOrgManager(subSystemId);
    }


    public final AppManager getAppManager(ConfigCode configCode) {


        AppManager appManager = appManagerMap.get(configCode);
        if (appManager == null) {
            appManager = EsbUtil.parExpression( AppManager.class);
            appManager.init(configCode);
            appManagerMap.put(configCode, appManager);
        }
        return appManager;
    }


    /**
     * 取得组织机构管理器
     *
     * @return
     */
    public final static OrgManager getClientOrgManager(ConfigCode configCode) {
        synchronized (configCode) {

            OrgManager orgManager = null;
            try {

                String className = JDSConfig.getValue(OrgConstants.ORGMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY);
                if (className == null) {
                    className = OrgConstants.DEFAULTCTORGCLASS;
                }

                Class c = ClassUtility.loadClass(className);
                orgManager = (OrgManager) c.newInstance();
            } catch (Exception ex1) {
                log.error("", ex1);
            }
            orgManager.init(configCode);

            return orgManager;
        }
    }

    /**
     * 取得组织机构管理器
     *
     * @return
     */
    public final static OrgManager getOrgManager(ConfigCode subSystemId) {
        synchronized (subSystemId) {
            if (subSystemId == null) {
                subSystemId = JDSActionContext.getActionContext().getConfigCode();
            }

            if (subSystemId == null || subSystemId.equals("") || subSystemId.equals("\"\"")) {
                subSystemId = DEFAULTSYSCODE;
            }
            OrgManager orgManager = orgManagerMap.get(subSystemId);

            if (orgManager == null) {
                if (UserBean.getInstance().getConfigName().equals(ConfigCode.org)) {
                    Class c = null;
                    try {
                        c = ClassUtility.loadClass("net.ooder.common.org.impl.database.DbOrgManagerImpl");
                        orgManager = (OrgManager) c.newInstance();
                    } catch (Exception ex1) {
                        log.error("", ex1);
                    }

                } else {
                    try {
                        String className = CommonConfig.getValue(DEFAULTSYSCODE.getType() + "." + OrgConstants.ORGMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY);
                        if (className != null) {
                            Class c = ClassUtility.loadClass(className);
                            orgManager = (OrgManager) c.newInstance();
                        }

                    } catch (Exception ex) {
                        log.warn("Can't find OrgManager config in common_config!");
                        log.info("Try client config ...");

                    }
                }

                if (orgManager == null) {
                    try {
                        String className = JDSConfig.getValue(OrgConstants.ORGMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY);
                        if (className == null) {
                            className = OrgConstants.DEFAULTCTORGCLASS;
                        }

                        Class c = ClassUtility.loadClass(className);
                        orgManager = (OrgManager) c.newInstance();
                    } catch (Exception ex1) {
                        log.error("", ex1);
                    }
                }


                if (orgManager != null) {
                    try {
                        orgManager.init(subSystemId);
                        orgManagerMap.put(subSystemId, orgManager);
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                }
            }


            return orgManager;
        }


    }

    /**
     * 取得组织机构管理器
     *
     * @return
     */
    public OrgManager getOrgManagerByName(String name) {
        // 超时重新装载
        SubSystem system = systemMap.get(name);
        if (system == null) {
            return null;
        }

        return getOrgManager(system.getConfigname());
    }

    /**
     * 取得组织机构管理器
     *
     * @return
     */
    public OrgManager getOrgManagerByCode(String name) {
        if (systemMap.isEmpty() || systemMap.get(name) == null) {
            this.reLoadSystem();
        }
        SubSystem system = systemMap.get(name);
        return getOrgManager(system.getConfigname());
    }

    public abstract SubSystem getSystemById(String key);

    public Map<String, SubSystem> getSystemMap() {
        return systemMap;
    }


    public abstract List<SubSystem> getSystems();

    /**
     * 取得该人员的一些特殊权限
     *
     * @param person 人员对象
     * @return 权限
     */
    public Permissions getPermissions(Person person) {
        return new Permissions();
    }
}
