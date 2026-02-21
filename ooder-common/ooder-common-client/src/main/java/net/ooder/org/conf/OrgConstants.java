/**
 * $RCSfile: OrgConstants.java,v $
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
package net.ooder.org.conf;


import net.ooder.common.ConfigCode;

/**
 * Contains constant values representing various objects in Jive.
 */
public class OrgConstants {
    public static final ConfigCode CONFIG_KEY = ConfigCode.org;

    public static final ConfigCode CLUSTERCONFIG_KEY = ConfigCode.cluster;

    public static final ConfigCode UDPCONFIG_KEY = ConfigCode.udp;

    public static final ConfigCode VFSCONFIG_KEY = ConfigCode.vfs;

    public static final String VFSCONFIG_KEY_DISK = "disk";

    public static final String VFSCONFIG_KEY_STORE = "store";

    public static final String IOTCONFIG_KEY = "iot";

    public static final String CONFIG_CACHE_KEY = "JDS";

    public static final String ORGMANAGERCLASSNAME_KEY =
            CONFIG_KEY.getType() + "." + "OrgManager.className";

    public static final String ORGCACHEMANAGERCLASSNAME_KEY =
            CONFIG_KEY.getType() + "." + "OrgManager.cacheManagerClassName";

    public static final String ORGMANAGERFACTORYCLASSNAMEWITHNOCONFIGKEY_KEY =
            "OrgManager.factoryClassName";

    public static final String ORGMANAGERFACTORYCLASSNAME_KEY =
            CONFIG_KEY.getType() + "." + ORGMANAGERFACTORYCLASSNAMEWITHNOCONFIGKEY_KEY;

    public static final String ORGMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY = "OrgManager.implClassName";

    public static final String DEFAULTCTORGCLASS = "net.ooder.common.org.CtOrgManager";
    // 获取组织机构管理器工厂实现类名


    public static final String CMAILROOTPATH = "/cmailroot/";


    public static final String DISKROOTPATH = "/cdiskroot/";


    public static final String EDISKROOTPATH = "/cediskroot/";


    public static final String ORGMANAGERIMPLCLASSNAME_KEY =
            CONFIG_KEY.getType() + "." + ORGMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY;


    public static final String ORGADMINMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY = "OrgAdminManager.implClassName";
    public static final String ORGADMIN_MANAGERIMPLCLASSNAME_KEY =
            CONFIG_KEY.getType() + "." + ORGADMINMANAGERIMPLCLASSNAMEWITHNOCONFIGKEY_KEY;


}
