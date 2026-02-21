/**
 * $RCSfile: CommonConfig.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:16 $
 * <p>
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CommonConfig.java,v $
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
package net.ooder.common;

import net.ooder.common.property.ConfigFactory;
import net.ooder.common.property.Properties;
import net.ooder.common.property.XMLProperties;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;
import net.ooder.config.JDSConfig.Config;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Title: 常用代码打包
 * </p>
 * <p>
 * Description: Get configuration properties used by this common package. The config file "common_config.xml" lied in
 * CLASSPATH will be used.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class CommonConfig {

    public static final String CONFIG_FILENAME = "common_config.xml";


    private static final String SPRING_FILENAME = "application.properties";

    private static Properties props;

    private static XMLProperties properties = null;

    public static String getValue(String name) {
        init();
        if (properties != null) {
            return properties.getProperty(name);
        } else {
            return props.getProperty(name);
        }

    }

    public static String[] getValues(String name) {
        init();
        if (properties != null) {
            return properties.getProperties(name);
        } else {
            return new String[]{props.getProperty(name)};
        }

    }

    public static void setValue(String name, String value) {
        init();
        if (properties != null)
            properties.setProperty(name, value);
        else
            props.setProperty(name, value);
    }

    public static void reLoad() {
        properties = null;
        props = null;
        init();
    }

//    public static String[] getValues(String name) {
//        init();
//        if (properties != null)
//            return properties.getProperties(name);
//        else
//            return new String[0];
//    }


    private static void initProps() {

    }

    private static void init() {
        try {
            if (props == null) {
                if (ClassUtility.loadResource(SPRING_FILENAME) != null) {
                    props = new Properties();
                    props.load(ClassUtility.loadResource(SPRING_FILENAME));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (properties == null) {
            File engineConfigFile = new File(Config.configPath(), CONFIG_FILENAME);
            if (!engineConfigFile.exists()) {
                engineConfigFile = new File(Config.publicConfigPath(), CONFIG_FILENAME);
            }

            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath("/");
                engineConfigFile = new File(path, CONFIG_FILENAME);
            }
            if (engineConfigFile.exists()) {
                properties = ConfigFactory.getXML(engineConfigFile.getAbsolutePath());
            }
        }

    }

}
