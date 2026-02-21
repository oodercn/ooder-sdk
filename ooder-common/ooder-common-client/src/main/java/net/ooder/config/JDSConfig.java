/**
 * $RCSfile: JDSConfig.java,v $
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
package net.ooder.config;

import net.ooder.common.ConfigCode;
import net.ooder.common.property.ConfigFactory;
import net.ooder.common.property.Properties;
import net.ooder.common.property.XMLProperties;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.StringUtility;

import java.io.File;
import java.io.IOException;
import java.net.*;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: 引擎配置应用器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 4.0
 */
public class JDSConfig {

    private static final String CONFIG_FILENAME = "engine_config.xml";

    private static final String INIT_FILENAME = "jds_init.properties";

    private static final String SPRING_FILENAME = "application.properties";

    private static final String INIT_CLIENTFILENAME = "jdsclient_init.properties";

    public static final String JDSHomeName = "JDSHome";

    private static String jdsHome;

    private static XMLProperties xmlProperties;

    private static Properties props;

    public static final String THREAD_LOCK = "Thread Lock";

    public static String getServerHome() {
        initProps();
        File rootfile = new File(getAbsolutePath("", null));
        if (jdsHome == null) {
            synchronized (THREAD_LOCK) {
                jdsHome = System.getProperty(JDSHomeName);
                if (jdsHome == null) {
                    jdsHome = props.getProperty(JDSHomeName);
                }
                if (jdsHome == null) {
                    String mqhome = System.getProperty("activemq.home");
                    if (mqhome != null && !mqhome.equals("")) {
                        jdsHome = mqhome + File.separator + JDSHomeName;
                        if (jdsHome != null) {
                            System.out.println("  JDSHome[activemq.home]  path='" + jdsHome + "'");
                        }
                    }
                }

                if (jdsHome == null) {
                    File jdshomefile = new File(rootfile, JDSHomeName);
                    if (!jdshomefile.exists()) {
                        while (jdshomefile != null) {
                            System.out.println("  JDSHome[" + jdshomefile.getAbsolutePath() + "] loop path= " + jdshomefile.getAbsolutePath());
                            File homeFile = new File(jdshomefile, JDSHomeName);
                            if (homeFile.exists()) {
                                jdshomefile = homeFile;
                                break;
                            } else {
                                jdshomefile = jdshomefile.getParentFile();
                            }
                        }

                    }
                    if (jdshomefile == null) {
                        jdshomefile = new File(rootfile, JDSHomeName);
                    }
                    if (jdshomefile == null || !jdshomefile.exists()) {
                        jdshomefile.mkdirs();
                        System.out.println("  JDSHome[ ] error " + jdshomefile.getAbsolutePath());
                    }
                    jdsHome = jdshomefile.getAbsolutePath();
                }
                System.out.println("JDSHome path='" + jdsHome + "'");
            }


        }

        return jdsHome;
    }


    public static ConfigCode getConfigName() {
        ConfigCode currSystemCode = UserBean.getInstance().getConfigName();
        return currSystemCode;

    }

    public static void reLoad() {
        xmlProperties = null;
        props = null;
        init();
    }

    public static String getValue(String name) {
        init();
        if (xmlProperties != null && xmlProperties.getProperty(name) != null) {
            return xmlProperties.getProperty(name);
        } else {
            return props.getProperty(name);
        }

    }

    public static String[] getValues(String name) {
        init();
        if (xmlProperties != null && xmlProperties.getProperties(name) != null && xmlProperties.getProperties(name).length > 0) {
            return xmlProperties.getProperties(name);
        } else {
            if (props.get(name) != null) {
                String value = props.get(name).toString();
                if (value.indexOf(";") > -1) {
                    return StringUtility.split(value, ";");
                } else {
                    return new String[]{value};
                }
            } else {
                return new String[0];
            }
        }
    }

    public static void setValue(String name, String value) {
        init();
        if (xmlProperties != null)
            xmlProperties.setProperty(name, value);
        else
            props.setProperty(name, value);
    }

    public static String[] getChildrenProperties(String name) {
        init();
        if (xmlProperties != null && xmlProperties.getChildrenProperties(name) != null && xmlProperties.getChildrenProperties(name).length > 0) {
            return xmlProperties.getChildrenProperties(name);
        } else {
            return new String[0];
        }

    }

    private static void initProps() {
        try {
            if (props == null) {
                if (ClassUtility.loadResource(SPRING_FILENAME) != null) {
                    props = new Properties();
                    props.load(ClassUtility.loadResource(SPRING_FILENAME));
                } else if (ClassUtility.loadResource(INIT_FILENAME) != null) {
                    props = new Properties();
                    props.load(ClassUtility.loadResource(INIT_FILENAME));
                } else if (ClassUtility.loadResource(INIT_CLIENTFILENAME) != null) {
                    props = new Properties();
                    props.load(ClassUtility.loadResource(INIT_CLIENTFILENAME));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        initProps();
        if (xmlProperties == null) {
            File engineConfigFile = new File(Config.configPath(), CONFIG_FILENAME);
            if (!engineConfigFile.exists()) {
                engineConfigFile = new File(Config.publicConfigPath(), CONFIG_FILENAME);
            }
            if (!engineConfigFile.exists()) {
                String path = JDSConfig.getAbsolutePath(File.separator);
                engineConfigFile = new File(path + CONFIG_FILENAME);
            }
            if (engineConfigFile.exists()) {
                xmlProperties = ConfigFactory.getXML(engineConfigFile.getAbsolutePath());
            }
        }

    }

    public static String getJDSHomeAbsolutePath(String url) {
        return getAbsolutePath(".." + File.separator + "..") + url;
    }

    public static String getAbsoluteLibPath() {
        return JDSConfig.getAbsolutePath(".." + File.separator + "lib").substring(1);
    }

    public static String getAbsolutePath(String relativePath) {
        return getAbsolutePath(relativePath, null);

    }


    public static class Config {

        public static File rootServerHome() {
            String serverHomeStr = JDSConfig.getServerHome();
            if (serverHomeStr == null) {
                serverHomeStr = "JDSHome";
            }
            File serverHome = new File(serverHomeStr);
            return serverHome;
        }

        public static File publicConfigPath() {
            File configPath = new File(rootServerHome().getAbsoluteFile() + File.separator + "config");
            if (!configPath.exists() || !configPath.isDirectory()) {
                configPath.mkdirs();
                System.out.println(" Config path '" + configPath.getAbsolutePath() + "' does not exists!");
            }
            return configPath;
        }

        public static File applicationHome() {
            File jdsHome = rootServerHome();
            File applicationHome = new File(jdsHome.getAbsoluteFile() + File.separator + "application");
            if (!applicationHome.exists() || !applicationHome.isDirectory()) {
                System.out.println("Application home '" + applicationHome.getAbsolutePath() + "' does not exits!");
            }
            return applicationHome;
        }

        public static File sourcePath() {

            File sourcePath = new File(currServerHome().getAbsoluteFile() + File.separator + "classes" + File.separator);
            if (!sourcePath.exists() || !sourcePath.isDirectory()) {
                sourcePath.mkdirs();
            }
            return sourcePath;
        }

        public static File currServerHome() {
            // File serverHome = new File(applicationHome().getAbsolutePath() + File.separator + getConfigName());
            File serverHome = new File(applicationHome().getAbsolutePath() + File.separator + getConfigName().getType());
            if (!serverHome.exists() || !serverHome.isDirectory()) {
                System.out.println("JDSHome '" + serverHome.getAbsolutePath() + "' does not exists!");
            }
            return serverHome;
        }


        public static File configPath() {
            File configPath = new File(currServerHome().getAbsoluteFile() + File.separator + "config");
            if (!configPath.exists() || !configPath.isDirectory()) {
                configPath.mkdirs();
                // System.out.println("Config path '" + configPath.getAbsolutePath() + "' does not exists!");
            }
            return configPath;
        }

        public static File libPath() {
            File libPath = new File(currServerHome().getAbsoluteFile() + File.separator + "lib");
            if (!libPath.exists() || !libPath.isDirectory()) {
                libPath.mkdirs();
                // System.out.println("Lib path '" + serverHome().getAbsoluteFile() + File.separator + "lib' does not
                // exits!");
            }
            return libPath;
        }

        public static File dataPath() {
            return new File(currServerHome().getAbsoluteFile() + File.separator + "data");
        }

        public static File tempPath() {
            return new File(currServerHome().getAbsoluteFile() + File.separator + "temp");
        }

        public static boolean connectionProfile() {
            try {
                return new Boolean(JDSConfig.getValue("connectionProfile")).booleanValue();
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean startAdminThread() {
            try {
                return new Boolean(JDSConfig.getValue("admin.StartAdminThread")).booleanValue();
            } catch (Exception e) {
                return false;
            }
        }

        // 是否一个帐号只能登陆一次
        public static boolean singleLogin() {
            try {
                return new Boolean(JDSConfig.getValue("singleLogin")).booleanValue();
            } catch (Exception e) {
                return false;
            }
        }

        public static InetAddress adminAddress() {
            // get the admin server info
            try {
                return InetAddress.getByName(JDSConfig.getValue("admin.host"));
            } catch (Exception e) {
                try {
                    return InetAddress.getLocalHost();
                } catch (UnknownHostException uhe) {
                    return null; // never happended
                }
            }
        }

        public static int adminPort() {
            // parse the port number
            try {
                return Integer.parseInt(JDSConfig.getValue("admin.port"));
            } catch (Exception e) {
                return 10523;
            }
        }

        public static String adminKey() {
            // set the admin key
            String adminKey = JDSConfig.getValue("admin.key");
            if (adminKey == null) {
                adminKey = "NA";
            }
            return adminKey;
        }

        public static boolean dumpCache() {
            try {
                return new Boolean(JDSConfig.getValue("server.dumpCache")).booleanValue();
            } catch (Exception e) {
                return true;
            }
        }

        public static String cacheDbUser() {
            String cacheDbUser = JDSConfig.getValue("server.cacheDbUser");
            if (cacheDbUser == null)
                cacheDbUser = "sa";
            return cacheDbUser;
        }

        public static String cacheDbPassword() {
            String cacheDbPassword = JDSConfig.getValue("server.cacheDbPassword");
            if (cacheDbPassword == null)
                cacheDbPassword = "";
            return cacheDbPassword;
        }

        public static String cacheDbURL() {
            String cacheDbURL = JDSConfig.getValue("server.cacheDbURL");
            if (cacheDbURL == null)
                cacheDbURL = "jdbc:hsqldb:hsql://localhost";
            return cacheDbURL;
        }

        public static String cacheDbServerProps() {
            String cacheDbServerProps = JDSConfig.getValue("server.cacheDbServerProps");
            if (cacheDbServerProps == null) {
                try {
                    cacheDbServerProps = "database.0=" + new File(dataPath().getAbsoluteFile() + File.separator + "cache").toURL();
                } catch (MalformedURLException mue) {
                    System.out.print(mue);
                }
            }
            return cacheDbServerProps;
        }

    }

    public static String getAbsolutePath(String relativePath, Class classzz) {
        initProps();
        String classPath = props.getProperty("classPath");
        if (classPath == null || classPath.trim().equals("")) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(relativePath);
            if (url == null) {
                try {
                    if (classzz == null) {
                        try {
                            classzz = ClassUtility.loadClass("net.ooder.JDSInit");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    if (classzz == null) {
                        classzz = JDSConfig.class;
                    }

                    if (classzz.getResource(relativePath) != null && classzz.getResource(relativePath).toURI() != null) {
                        classPath = classzz.getResource(relativePath).toURI().getPath();
                    } else if (Thread.currentThread().getContextClassLoader().getResource(relativePath) != null) {
                        classPath = Thread.currentThread().getContextClassLoader().getResource(relativePath).getPath();
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    classPath = Thread.currentThread().getContextClassLoader().getResource(relativePath).getPath();

                }
            } else {
                try {
                    classPath = url.toURI().getPath();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    classPath = url.getPath();
                }
            }
        }

        if (classPath == null) {
            classPath = File.separator;
        }

        return classPath;
    }

}
