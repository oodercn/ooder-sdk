/**
 * $RCSfile: ServiceConfig.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/04/17 11:40:18 $
 * <p>
 * Copyright (C) 2025 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ServiceConfig.java,v $
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
package net.ooder.esb.config.xml;

import net.ooder.common.JDSException;
import net.ooder.common.property.ConfigFactory;
import net.ooder.common.property.XMLProperties;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSConfig.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {

    private String fileName = "expressiontemplet_config.xml";

    private static final Map<String, XMLProperties> propertiesMap = new HashMap<String, XMLProperties>();
    private static final Map<String, ServiceConfig> serverConfigMap = new HashMap<String, ServiceConfig>();
    private XMLProperties propertie;

    public static void clear() {
        propertiesMap.clear();
        serverConfigMap.clear();
    }

    public static ServiceConfig getServiceConfig(String configFileName) throws JDSException {
        ServiceConfig serviceConfig = serverConfigMap.get(configFileName);
        if (serviceConfig == null) {
            serviceConfig = new ServiceConfig(configFileName);
            serverConfigMap.put(configFileName, serviceConfig);
        }
        return serviceConfig;
    }


    ServiceConfig(String fileName) {
        if (fileName != null) {
            this.fileName = fileName;
        }
        this.propertie = propertiesMap.get(fileName);
        if (propertie == null) {
            File engineConfigFile = new File(Config.configPath(), fileName);

            if (!engineConfigFile.exists()) {
                engineConfigFile = new File(Config.publicConfigPath(), fileName);
            }
            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath(File.separator);
                if (path == null) {
                    path = "JDSHome" + File.separator + "config" + File.separator;
                }
                engineConfigFile = new File(path + fileName);
            }

            if (engineConfigFile.exists()) {
                propertie = ConfigFactory.getXML(engineConfigFile.getAbsolutePath());
                propertiesMap.put(fileName, propertie);
            }

        }

    }


    public String getValue(String name) {
        String value = null;
        if (propertie != null) {
            value = propertie.getProperty(name);
        }

        if (value == null) {
            value = JDSConfig.getValue(name);
        }
        return value;
    }

    public String[] getValues(String name) {
        if (propertie != null && propertie.getProperties(name) != null && propertie.getProperties(name).length > 0) {
            return propertie.getProperties(name);
        } else {
            return JDSConfig.getValues(name);
        }
    }

}
