package net.ooder.config.core;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class PropertiesConfigReader {
    
    private static final String[] CONFIG_FILE_NAMES = {
        "application.properties",
        "jds_init.properties",
        "jdsclient_init.properties"
    };
    
    public OoderConfig read() {
        for (String fileName : CONFIG_FILE_NAMES) {
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                return read(resource);
            }
        }
        return new OoderConfig();
    }
    
    public OoderConfig read(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return read(file);
        }
        return new OoderConfig();
    }
    
    public OoderConfig read(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return read(is);
        } catch (IOException e) {
            throw new ConfigException("Failed to read config file: " + file.getPath(), e);
        }
    }
    
    public OoderConfig read(URL url) {
        try (InputStream is = url.openStream()) {
            return read(is);
        } catch (IOException e) {
            throw new ConfigException("Failed to read config from URL: " + url, e);
        }
    }
    
    public OoderConfig read(InputStream inputStream) {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new ConfigException("Failed to load properties", e);
        }
        return propertiesToConfig(props);
    }
    
    public OoderConfig read(Properties props) {
        return propertiesToConfig(props);
    }
    
    private OoderConfig propertiesToConfig(Properties props) {
        OoderConfig config = new OoderConfig();
        
        JdsConfig jds = config.getJds();
        jds.setHome(props.getProperty("jds.home", props.getProperty("JDSHome")));
        jds.setConfigName(props.getProperty("jds.configName", props.getProperty("configName")));
        
        ServerConfig server = config.getServer();
        String portStr = props.getProperty("server.port");
        if (portStr != null) {
            try {
                server.setPort(Integer.parseInt(portStr));
            } catch (NumberFormatException e) {
            }
        }
        server.setUrl(props.getProperty("server.url", props.getProperty("serverUrl")));
        
        ServerConfig.AdminConfig admin = server.getAdmin();
        admin.setEnabled(Boolean.parseBoolean(props.getProperty("admin.StartAdminThread", "true")));
        String adminPortStr = props.getProperty("admin.port");
        if (adminPortStr != null) {
            try {
                admin.setPort(Integer.parseInt(adminPortStr));
            } catch (NumberFormatException e) {
            }
        }
        admin.setKey(props.getProperty("admin.key", "jds-admin"));
        admin.setHost(props.getProperty("admin.host", "127.0.0.1"));
        
        ClusterConfig cluster = config.getCluster();
        cluster.setEnabled(Boolean.parseBoolean(props.getProperty("cluster.enabled", "false")));
        
        ClusterConfig.UdpConfig udp = cluster.getUdp();
        udp.setEnabled(Boolean.parseBoolean(props.getProperty("udpServer.enabled", "true")));
        String udpPortStr = props.getProperty("udpServer.port");
        if (udpPortStr != null) {
            try {
                udp.setPort(Integer.parseInt(udpPortStr));
            } catch (NumberFormatException e) {
            }
        }
        udp.setCode(props.getProperty("udpServer.code", "utf-8"));
        udp.setServerIP(props.getProperty("udpServer.serverIP"));
        udp.setSelf(Boolean.parseBoolean(props.getProperty("udpServer.self", "true")));
        
        SessionConfig session = config.getSession();
        session.setEnabled(Boolean.parseBoolean(props.getProperty("session.enabled", "true")));
        String expireTimeStr = props.getProperty("session.ExpireTime");
        if (expireTimeStr != null) {
            try {
                session.setExpireTime(Long.parseLong(expireTimeStr));
            } catch (NumberFormatException e) {
            }
        }
        String checkIntervalStr = props.getProperty("session.CheckInterval");
        if (checkIntervalStr != null) {
            try {
                session.setCheckInterval(Long.parseLong(checkIntervalStr));
            } catch (NumberFormatException e) {
            }
        }
        session.setSingleLogin(Boolean.parseBoolean(props.getProperty("singleLogin", "true")));
        
        CacheConfig cache = config.getCache();
        cache.setEnabled(Boolean.parseBoolean(props.getProperty("hsql.cacheEnabled", "true")));
        cache.setDumpCache(Boolean.parseBoolean(props.getProperty("server.dumpCache", "true")));
        cache.setDbUser(props.getProperty("server.cacheDbUser", props.getProperty("hsql.cacheDbUser", "sa")));
        cache.setDbPassword(props.getProperty("server.cacheDbPassword", props.getProperty("hsql.cacheDbPassword", "")));
        cache.setDbUrl(props.getProperty("server.cacheDbURL", props.getProperty("hsql.url")));
        cache.setDataPath(props.getProperty("hsql.dataPath"));
        cache.setDbName(props.getProperty("hsql.dbName", "cache"));
        
        UserConfig user = config.getUser();
        user.setServerUrl(props.getProperty("serverUrl"));
        user.setSystemCode(props.getProperty("systemCode"));
        user.setConfigName(props.getProperty("configName"));
        user.setUsername(props.getProperty("username"));
        user.setPassword(props.getProperty("password", props.getProperty("userpassword")));
        user.setPersonId(props.getProperty("personid"));
        user.setIndex(props.getProperty("index"));
        user.setProxyHost(props.getProperty("proxyHost", "http://127.0.0.1"));
        String proxyPortStr = props.getProperty("proxyPort");
        if (proxyPortStr != null) {
            try {
                user.setProxyPort(Integer.parseInt(proxyPortStr));
            } catch (NumberFormatException e) {
            }
        }
        user.setLoginUrl(props.getProperty("loginUrl", "/api/sys/syslogin"));
        user.setAutoLogin(Boolean.parseBoolean(props.getProperty("autoLogin", "false")));
        user.setSavePassword(Boolean.parseBoolean(props.getProperty("savePassword", "false")));
        user.setOffline(Boolean.parseBoolean(props.getProperty("OffLine", props.getProperty("offline", "true"))));
        String msgPortStr = props.getProperty("msgport");
        if (msgPortStr != null) {
            try {
                user.setMsgPort(Integer.parseInt(msgPortStr));
            } catch (NumberFormatException e) {
            }
        }
        user.setUdpUrl(props.getProperty("udpUrl"));
        
        return config;
    }
}
