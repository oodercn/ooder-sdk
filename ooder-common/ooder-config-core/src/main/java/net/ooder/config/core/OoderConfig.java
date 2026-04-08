package net.ooder.config.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class OoderConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String description;
    
    private JdsConfig jds = new JdsConfig();
    private ServerConfig server = new ServerConfig();
    private ClusterConfig cluster = new ClusterConfig();
    private SessionConfig session = new SessionConfig();
    private CacheConfig cache = new CacheConfig();
    private UserConfig user = new UserConfig();
    
    private Map<String, Object> extensions = new ConcurrentHashMap<String, Object>();
    
    public OoderConfig() {
    }
    
    public static OoderConfigBuilder builder() {
        return new OoderConfigBuilder();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public JdsConfig getJds() {
        return jds;
    }
    
    public void setJds(JdsConfig jds) {
        this.jds = jds;
    }
    
    public ServerConfig getServer() {
        return server;
    }
    
    public void setServer(ServerConfig server) {
        this.server = server;
    }
    
    public ClusterConfig getCluster() {
        return cluster;
    }
    
    public void setCluster(ClusterConfig cluster) {
        this.cluster = cluster;
    }
    
    public SessionConfig getSession() {
        return session;
    }
    
    public void setSession(SessionConfig session) {
        this.session = session;
    }
    
    public CacheConfig getCache() {
        return cache;
    }
    
    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }
    
    public UserConfig getUser() {
        return user;
    }
    
    public void setUser(UserConfig user) {
        this.user = user;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    public String getValue(String key) {
        String[] parts = key.split("\\.", 2);
        if (parts.length == 2) {
            String prefix = parts[0];
            String suffix = parts[1];
            switch (prefix) {
                case "jds":
                    return jds.getValue(suffix);
                case "server":
                    return server.getValue(suffix);
                case "cluster":
                    return cluster.getValue(suffix);
                case "session":
                    return session.getValue(suffix);
                case "cache":
                    return cache.getValue(suffix);
                case "user":
                    return user.getValue(suffix);
                default:
                    Object value = extensions.get(key);
                    return value != null ? value.toString() : null;
            }
        }
        return null;
    }
    
    public Properties toProperties() {
        Properties props = new Properties();
        
        putIfNotNull(props, "jds.home", jds.getHome());
        putIfNotNull(props, "jds.configName", jds.getConfigName());
        
        putIfNotNull(props, "server.port", server.getPort() != null ? String.valueOf(server.getPort()) : null);
        putIfNotNull(props, "server.url", server.getUrl());
        putIfNotNull(props, "admin.StartAdminThread", String.valueOf(server.getAdmin().isEnabled()));
        putIfNotNull(props, "admin.port", server.getAdmin().getPort() != null ? String.valueOf(server.getAdmin().getPort()) : null);
        putIfNotNull(props, "admin.key", server.getAdmin().getKey());
        putIfNotNull(props, "admin.host", server.getAdmin().getHost());
        
        putIfNotNull(props, "cluster.enabled", String.valueOf(cluster.isEnabled()));
        putIfNotNull(props, "udpServer.enabled", String.valueOf(cluster.getUdp().isEnabled()));
        putIfNotNull(props, "udpServer.port", cluster.getUdp().getPort() != null ? String.valueOf(cluster.getUdp().getPort()) : null);
        putIfNotNull(props, "udpServer.code", cluster.getUdp().getCode());
        
        putIfNotNull(props, "session.enabled", String.valueOf(session.isEnabled()));
        putIfNotNull(props, "session.ExpireTime", String.valueOf(session.getExpireTime()));
        putIfNotNull(props, "session.CheckInterval", String.valueOf(session.getCheckInterval()));
        putIfNotNull(props, "singleLogin", String.valueOf(session.isSingleLogin()));
        
        putIfNotNull(props, "cache.enabled", String.valueOf(cache.isEnabled()));
        putIfNotNull(props, "server.dumpCache", String.valueOf(cache.isDumpCache()));
        
        putIfNotNull(props, "serverUrl", user.getServerUrl());
        putIfNotNull(props, "systemCode", user.getSystemCode());
        putIfNotNull(props, "configName", user.getConfigName());
        putIfNotNull(props, "username", user.getUsername());
        
        return props;
    }
    
    private void putIfNotNull(Properties props, String key, String value) {
        if (value != null) {
            props.setProperty(key, value);
        }
    }
    
    public static class OoderConfigBuilder {
        private OoderConfig config = new OoderConfig();
        
        public OoderConfigBuilder id(String id) {
            config.id = id;
            return this;
        }
        
        public OoderConfigBuilder name(String name) {
            config.name = name;
            return this;
        }
        
        public OoderConfigBuilder description(String description) {
            config.description = description;
            return this;
        }
        
        public OoderConfigBuilder jds(JdsConfig jds) {
            config.jds = jds;
            return this;
        }
        
        public OoderConfigBuilder server(ServerConfig server) {
            config.server = server;
            return this;
        }
        
        public OoderConfigBuilder cluster(ClusterConfig cluster) {
            config.cluster = cluster;
            return this;
        }
        
        public OoderConfigBuilder session(SessionConfig session) {
            config.session = session;
            return this;
        }
        
        public OoderConfigBuilder cache(CacheConfig cache) {
            config.cache = cache;
            return this;
        }
        
        public OoderConfigBuilder user(UserConfig user) {
            config.user = user;
            return this;
        }
        
        public OoderConfig build() {
            return config;
        }
    }
}
