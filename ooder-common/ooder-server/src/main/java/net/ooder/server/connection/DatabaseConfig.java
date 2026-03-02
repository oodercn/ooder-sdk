package net.ooder.server.connection;

/**
 * 数据库配置
 * 定义数据库连接信息
 *
 * @author ooder
 * @since 2.3
 */
public class DatabaseConfig {
    
    /** 数据库类型 */
    private String type;        // mysql, postgresql, oracle, sqlserver
    
    /** 主机地址 */
    private String host;
    
    /** 端口号 */
    private int port;
    
    /** 数据库名称 */
    private String database;
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
    
    /** 连接超时(毫秒) */
    private int timeout;
    
    /**
     * 默认构造函数
     */
    public DatabaseConfig() {
        this.timeout = 5000; // 默认5秒超时
    }
    
    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    /**
     * 构建JDBC URL
     * @return JDBC URL
     */
    public String buildJdbcUrl() {
        switch (type.toLowerCase()) {
            case "mysql":
                return String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            case "postgresql":
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            case "oracle":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
            case "sqlserver":
                return String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, database);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }
}
