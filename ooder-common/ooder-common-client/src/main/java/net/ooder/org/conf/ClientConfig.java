/**
 * $RCSfile: ClientConfig.java,v $
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

import net.ooder.common.property.ConfigFactory;
import net.ooder.common.property.XMLProperties;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSConfig.Config;

import java.io.File;
import java.net.URISyntaxException;

public class ClientConfig {
    public static final String CONFIG_FILENAME = "org_client_config.xml";
    private static XMLProperties properties = null;

    public static String getValue(String name) {
        init();
        if (properties != null)
            return properties.getProperty(name);
        else
            return null;
    }

    public void reLoad() {
        properties = null;
        this.init();
    }

    public static String[] getValues(String name) {
        init();
        if (properties != null) {
            return properties.getProperties(name);
        } else {
            return new String[0];
        }
    }

    public static void setValue(String name, String value) {
        init();
        if (properties != null) {
            try {
                // modified by andy do not save changes
                properties.setProperty(name, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void init() {

        if (properties == null) {

            File engineConfigFile = new File(Config.configPath(), CONFIG_FILENAME);
            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath(File.separator);
                engineConfigFile = new File(Config.publicConfigPath(), CONFIG_FILENAME);
            }
            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath(File.separator);
                engineConfigFile = new File(path, CONFIG_FILENAME);
                // engineConfigFile = new File(path+CONFIG_FILENAME);
            }
            if (engineConfigFile.exists()) {
                properties = ConfigFactory.getXML(engineConfigFile.getAbsolutePath());
            }

        }
    }

}
