
/**
 * $RCSfile: ServerServiceConfig.java,v $
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

import net.ooder.common.property.ConfigFactory;
import net.ooder.common.property.XMLProperties;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSConfig.Config;

import java.io.File;

public class ServerServiceConfig {

    private XMLProperties properties = null;
    private String fileName = "expressiontemplet_config.xml";
    private static ServerServiceConfig serviceConfig;

    public static ServerServiceConfig getServiceConfig(String fileName) {

        if (serviceConfig == null || !serviceConfig.fileName.equals(fileName)) {
            serviceConfig = new ServerServiceConfig(fileName);
        }
        return serviceConfig;
    }

    public void reLoad() {
        properties = null;
        this.init();
    }

    public ServerServiceConfig(String fileName) {
        if (fileName != null) {
            this.fileName = fileName;
        }
        properties = null;
    }


    public String getValue(String name) {
        init();
        if (properties != null)
            return properties.getProperty(name);
        else
            return null;
    }

    public String[] getValues(String name) {
        init();

        if (properties != null)
            return properties.getProperties(name);
        else
            return new String[0];
    }

    private void init() {


        if (properties == null) {
            File engineConfigFile = new File(fileName);
            if (!engineConfigFile.exists()) {
                engineConfigFile = new File(Config.configPath(), fileName);
            }
            if (!engineConfigFile.exists()) {
                engineConfigFile = new File(Config.publicConfigPath(), fileName);
            }

            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath(File.separator);
                engineConfigFile = new File(path + fileName);
            }
            properties = ConfigFactory.getXML(engineConfigFile.getAbsolutePath());
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
