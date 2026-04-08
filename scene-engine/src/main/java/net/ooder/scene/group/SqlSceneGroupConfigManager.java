package net.ooder.scene.group;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL 场景组配置管理器实现
 *
 * <p>基于 SQLite/MySQL 的场景组扩展配置持久化存储。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneGroupConfigManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneGroupConfigManager.class);

    private static final String CREATE_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_group_config (" +
        "config_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "config_key VARCHAR(255) NOT NULL, " +
        "config_value TEXT, " +
        "config_type VARCHAR(50), " +
        "create_time TIMESTAMP, " +
        "update_time TIMESTAMP, " +
        "UNIQUE(scene_group_id, config_key)" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_config_scene_group ON scene_group_config(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_config_key ON scene_group_config(config_key)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneGroupConfigManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlSceneGroupConfigManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneGroupConfigManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneGroupConfigManager at: {}", jdbcUrl);

        try {
            if (jdbcUrl.contains("sqlite")) {
                Class.forName("org.sqlite.JDBC");
            } else if (jdbcUrl.contains("h2")) {
                Class.forName("org.h2.Driver");
            } else if (jdbcUrl.contains("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            if (username != null && password != null) {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } else {
                connection = DriverManager.getConnection(jdbcUrl);
            }

            createTables();
            initialized = true;
            log.info("SqlSceneGroupConfigManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneGroupConfigManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneGroupConfigManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_CONFIG_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Config tables created/verified");
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (username != null && password != null) {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } else {
                connection = DriverManager.getConnection(jdbcUrl);
            }
        }
        return connection;
    }

    /**
     * 设置配置项
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @param value 配置值
     * @return 是否成功
     */
    public boolean setConfig(String sceneGroupId, String key, Object value) {
        if (sceneGroupId == null || key == null) {
            return false;
        }

        String configId = sceneGroupId + ":" + key;
        String configValue = value != null ? JSON.toJSONString(value) : null;
        String configType = value != null ? value.getClass().getName() : null;
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT OR REPLACE INTO scene_group_config " +
            "(config_id, scene_group_id, config_key, config_value, config_type, create_time, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // MySQL 使用 INSERT ... ON DUPLICATE KEY UPDATE
        if (jdbcUrl.contains("mysql")) {
            sql = "INSERT INTO scene_group_config " +
                "(config_id, scene_group_id, config_key, config_value, config_type, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE config_value = VALUES(config_value), config_type = VALUES(config_type), update_time = VALUES(update_time)";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, configId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, key);
            pstmt.setString(4, configValue);
            pstmt.setString(5, configType);
            pstmt.setTimestamp(6, Timestamp.valueOf(now));
            pstmt.setTimestamp(7, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            log.debug("Config set: sceneGroupId={}, key={}", sceneGroupId, key);
            return true;

        } catch (SQLException e) {
            log.error("Failed to set config: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取配置项
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @return 配置值，不存在返回 null
     */
    public Object getConfig(String sceneGroupId, String key) {
        if (sceneGroupId == null || key == null) {
            return null;
        }

        String sql = "SELECT config_value, config_type FROM scene_group_config WHERE scene_group_id = ? AND config_key = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, key);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String configValue = rs.getString("config_value");
                String configType = rs.getString("config_type");
                return deserializeValue(configValue, configType);
            }

        } catch (SQLException e) {
            log.error("Failed to get config: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 获取指定类型的配置项
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @param type 配置类型
     * @return 配置值
     */
    public <T> T getConfig(String sceneGroupId, String key, Class<T> type) {
        Object value = getConfig(sceneGroupId, key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    /**
     * 获取场景组的所有配置
     *
     * @param sceneGroupId 场景组ID
     * @return 配置映射
     */
    public Map<String, Object> getAllConfig(String sceneGroupId) {
        Map<String, Object> result = new HashMap<>();

        if (sceneGroupId == null) {
            return result;
        }

        String sql = "SELECT config_key, config_value, config_type FROM scene_group_config WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String key = rs.getString("config_key");
                String configValue = rs.getString("config_value");
                String configType = rs.getString("config_type");
                Object value = deserializeValue(configValue, configType);
                if (value != null) {
                    result.put(key, value);
                }
            }

        } catch (SQLException e) {
            log.error("Failed to get all config: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 删除配置项
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @return 是否成功
     */
    public boolean removeConfig(String sceneGroupId, String key) {
        if (sceneGroupId == null || key == null) {
            return false;
        }

        String sql = "DELETE FROM scene_group_config WHERE scene_group_id = ? AND config_key = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, key);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.debug("Config removed: sceneGroupId={}, key={}", sceneGroupId, key);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to remove config: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 删除场景组的所有配置
     *
     * @param sceneGroupId 场景组ID
     * @return 删除的配置数量
     */
    public int removeAllConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "DELETE FROM scene_group_config WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            int rows = pstmt.executeUpdate();
            log.info("All config removed: sceneGroupId={}, count={}", sceneGroupId, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to remove all config: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 检查配置项是否存在
     *
     * @param sceneGroupId 场景组ID
     * @param key 配置键
     * @return 是否存在
     */
    public boolean hasConfig(String sceneGroupId, String key) {
        return getConfig(sceneGroupId, key) != null;
    }

    /**
     * 获取配置项数量
     *
     * @param sceneGroupId 场景组ID
     * @return 配置数量
     */
    public int getConfigCount(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM scene_group_config WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get config count: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 批量设置配置
     *
     * @param sceneGroupId 场景组ID
     * @param configs 配置映射
     * @return 成功设置的配置数量
     */
    public int setConfigs(String sceneGroupId, Map<String, Object> configs) {
        if (sceneGroupId == null || configs == null || configs.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            if (setConfig(sceneGroupId, entry.getKey(), entry.getValue())) {
                successCount++;
            }
        }

        log.info("Batch config set: sceneGroupId={}, success={}/total={}",
            sceneGroupId, successCount, configs.size());
        return successCount;
    }

    private Object deserializeValue(String configValue, String configType) {
        if (configValue == null) {
            return null;
        }

        try {
            // 尝试根据类型反序列化
            if (configType != null && !configType.isEmpty()) {
                Class<?> clazz = Class.forName(configType);
                return JSON.parseObject(configValue, clazz);
            }
            // 默认作为字符串返回
            return configValue;
        } catch (Exception e) {
            log.warn("Failed to deserialize config value: {}", e.getMessage());
            return configValue;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
