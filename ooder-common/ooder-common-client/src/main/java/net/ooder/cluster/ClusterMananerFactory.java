/**
 * $RCSfile: ClusterMananerFactory.java,v $
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
package net.ooder.cluster;

import net.ooder.common.ConfigCode;
import net.ooder.common.util.ClassUtility;
import net.ooder.server.JDSServer;


public class ClusterMananerFactory {



    private final static String defaultManagerClassName = "net.ooder.web.client.WebClusterManagerImpl";

    public static ClusterMananer getClusterManager(ConfigCode code) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ClusterMananer manager = null;
        String className  = JDSServer.getClusterClient().getServerNodeListByConfigCode(code).getClusterManagerClass();

        if (className != null && !className.equals("")) {
            manager = (ClusterMananer) ClassUtility.loadClass(className).newInstance();
        } else {
            manager = (ClusterMananer) ClassUtility.loadClass(defaultManagerClassName).newInstance();
        }
        return manager;
    }



}
