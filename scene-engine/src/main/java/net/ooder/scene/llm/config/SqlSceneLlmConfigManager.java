package net.ooder.scene.llm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL LLM 配置管理器实现
 *
 * <p>基于 SQLite/MySQL 的 LLM 配置持久化存储，替代内存存储实现。</p>
 * <p>支持：</p>
 * <ul>
 *   <li>SQLite: jdbc:sqlite:/path/to/scene.db</li>
 *   <li>MySQL: jdbc:mysql://host:3306/scene_db</li>
 *   <li>H2: jdbc:h2:mem:testdb</li>
 * </ul>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
@ConditionalOnProperty(prefix = "scene.engine.llm", name = "storage.type", havingValue = "sql", matchIfMissing = true)
public class SqlSceneLlmConfigManager implements SceneLlmConfigManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneLlmConfigManager.class);

    private static final String CREATE_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_llm_configs (" +
        "config_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL UNIQUE, " +
        "provider VARCHAR(100), " +
        "model VARCHAR(100), " +
        "temperature DOUBLE DEFAULT 0.7, " +
        "max_tokens INTEGER DEFAULT 2048, " +
        "timeout BIGINT DEFAULT 60000, " +
        "extensions TEXT, " +
        "created_at BIGINT, " +
        "updated_at BIGINT" +
        ")";

    private static final String CREATE_DEFAULT_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS default_llm_config (" +
        "id INTEGER PRIMARY KEY CHECK (id = 1), " +
        "config_id VARCHAR(255), " +
        "provider VARCHAR(100), " +
        "model VARCHAR(100), " +
        "temperature DOUBLE DEFAULT 0.7, " +
        "max_tokens INTEGER DEFAULT 2048, " +
        "timeout BIGINT DEFAULT 60000, " +
        "extensions TEXT, " +
        "updated_at BIGINT" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_llm_scene_group ON scene_llm_configs(scene_group_id)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneLlmConfigManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlSceneLlmConfigManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneLlmConfigManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneLlmConfigManager at: {}", jdbcUrl);

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
            initializeDefaultConfig();
            initialized = true;
            log.info("SqlSceneLlmConfigManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneLlmConfigManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneLlmConfigManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_CONFIG_TABLE);
            stmt.execute(CREATE_DEFAULT_CONFIG_TABLE);
            stmt.execute(CREATE_INDEXES);
            log.debug("LLM config tables created/verified");
        }
    }

    private void initializeDefaultConfig() {
        String sql = "INSERT OR IGNORE INTO default_llm_config (id, config_id, provider, model, temperature, max_tokens, timeout, updated_at) " +
            "VALUES (1, 'default-llm-config', 'openai', 'gpt-4', 0.7, 2048, 60000, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.warn("Failed to initialize default config: {}", e.getMessage());
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

    @Override
    public SceneLlmConfigInfo getLlmConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return getDefaultConfig();
        }

        String sql = "SELECT * FROM scene_llm_configs WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToConfig(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to get LLM config: {}", e.getMessage());
        }

        log.debug("No custom config for sceneGroup: {}, using default", sceneGroupId);
        return getDefaultConfig();
    }

    @Override
    public void setLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        if (sceneGroupId == null) {
            throw new IllegalArgumentException("sceneGroupId is required");
        }

        if (config == null) {
            resetLlmConfig(sceneGroupId);
            return;
        }

        config.setSceneGroupId(sceneGroupId);
        if (config.getConfigId() == null) {
            config.setConfigId(java.util.UUID.randomUUID().toString().replace("-", ""));
        }

        String sql = "INSERT OR REPLACE INTO scene_llm_configs " +
            "(config_id, scene_group_id, provider, model, temperature, max_tokens, timeout, extensions, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, config.getConfigId());
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, config.getProvider());
            pstmt.setString(4, config.getModel());
            pstmt.setDouble(5, config.getTemperature());
            pstmt.setInt(6, config.getMaxTokens());
            pstmt.setLong(7, config.getTimeout());
            pstmt.setString(8, serializeExtensions(config.getExtensions()));
            pstmt.setLong(9, System.currentTimeMillis());
            pstmt.setLong(10, System.currentTimeMillis());
            pstmt.executeUpdate();

            log.info("LLM config set: sceneGroupId={}, provider={}, model={}",
                    sceneGroupId, config.getProvider(), config.getModel());
        } catch (SQLException e) {
            log.error("Failed to set LLM config: {}", e.getMessage());
            throw new RuntimeException("Failed to set LLM config: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateLlmConfig(String sceneGroupId, SceneLlmConfigInfo config) {
        if (sceneGroupId == null || config == null) {
            return;
        }

        SceneLlmConfigInfo existing = getLlmConfig(sceneGroupId);
        if (existing == null || !hasCustomConfig(sceneGroupId)) {
            setLlmConfig(sceneGroupId, config);
            return;
        }

        StringBuilder sqlBuilder = new StringBuilder("UPDATE scene_llm_configs SET ");
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (config.getProvider() != null) {
            sqlBuilder.append("provider = ?, ");
            params.add(config.getProvider());
        }
        if (config.getModel() != null) {
            sqlBuilder.append("model = ?, ");
            params.add(config.getModel());
        }
        if (config.getTemperature() > 0) {
            sqlBuilder.append("temperature = ?, ");
            params.add(config.getTemperature());
        }
        if (config.getMaxTokens() > 0) {
            sqlBuilder.append("max_tokens = ?, ");
            params.add(config.getMaxTokens());
        }
        if (config.getTimeout() > 0) {
            sqlBuilder.append("timeout = ?, ");
            params.add(config.getTimeout());
        }

        sqlBuilder.append("updated_at = ? WHERE scene_group_id = ?");
        params.add(System.currentTimeMillis());
        params.add(sceneGroupId);

        try (PreparedStatement pstmt = getConnection().prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.executeUpdate();
            log.info("LLM config updated: sceneGroupId={}", sceneGroupId);
        } catch (SQLException e) {
            log.error("Failed to update LLM config: {}", e.getMessage());
        }
    }

    @Override
    public void resetLlmConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return;
        }

        String sql = "DELETE FROM scene_llm_configs WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("LLM config reset: sceneGroupId={}", sceneGroupId);
            }
        } catch (SQLException e) {
            log.error("Failed to reset LLM config: {}", e.getMessage());
        }
    }

    @Override
    public boolean hasCustomConfig(String sceneGroupId) {
        if (sceneGroupId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM scene_llm_configs WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check custom config: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public SceneLlmConfigInfo getDefaultConfig() {
        String sql = "SELECT * FROM default_llm_config WHERE id = 1";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return mapResultSetToDefaultConfig(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to get default config: {}", e.getMessage());
        }

        // 返回硬编码默认值
        return createFallbackDefaultConfig();
    }

    @Override
    public void setDefaultConfig(SceneLlmConfigInfo defaultConfig) {
        if (defaultConfig == null) {
            return;
        }

        String sql = "INSERT OR REPLACE INTO default_llm_config " +
            "(id, config_id, provider, model, temperature, max_tokens, timeout, extensions, updated_at) " +
            "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, defaultConfig.getConfigId());
            pstmt.setString(2, defaultConfig.getProvider());
            pstmt.setString(3, defaultConfig.getModel());
            pstmt.setDouble(4, defaultConfig.getTemperature());
            pstmt.setInt(5, defaultConfig.getMaxTokens());
            pstmt.setLong(6, defaultConfig.getTimeout());
            pstmt.setString(7, serializeExtensions(defaultConfig.getExtensions()));
            pstmt.setLong(8, System.currentTimeMillis());
            pstmt.executeUpdate();

            log.info("Default LLM config updated: provider={}, model={}",
                    defaultConfig.getProvider(), defaultConfig.getModel());
        } catch (SQLException e) {
            log.error("Failed to set default config: {}", e.getMessage());
        }
    }

    /**
     * 获取配置数量
     */
    public int getConfigCount() {
        String sql = "SELECT COUNT(*) FROM scene_llm_configs";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get config count: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 清除所有配置
     */
    public void clearAllConfigs() {
        String sql = "DELETE FROM scene_llm_configs";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(sql);
            log.info("All LLM configs cleared");
        } catch (SQLException e) {
            log.error("Failed to clear all configs: {}", e.getMessage());
        }
    }

    private SceneLlmConfigInfo mapResultSetToConfig(ResultSet rs) throws SQLException {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setConfigId(rs.getString("config_id"));
        config.setSceneGroupId(rs.getString("scene_group_id"));
        config.setProvider(rs.getString("provider"));
        config.setModel(rs.getString("model"));
        config.setTemperature(rs.getDouble("temperature"));
        config.setMaxTokens(rs.getInt("max_tokens"));
        config.setTimeout(rs.getLong("timeout"));
        config.setExtensions(deserializeExtensions(rs.getString("extensions")));
        return config;
    }

    private SceneLlmConfigInfo mapResultSetToDefaultConfig(ResultSet rs) throws SQLException {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setConfigId(rs.getString("config_id"));
        config.setProvider(rs.getString("provider"));
        config.setModel(rs.getString("model"));
        config.setTemperature(rs.getDouble("temperature"));
        config.setMaxTokens(rs.getInt("max_tokens"));
        config.setTimeout(rs.getLong("timeout"));
        config.setExtensions(deserializeExtensions(rs.getString("extensions")));
        return config;
    }

    private SceneLlmConfigInfo createFallbackDefaultConfig() {
        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setConfigId("default-llm-config");
        config.setProvider("openai");
        config.setModel("gpt-4");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTimeout(60000);
        return config;
    }

    private String serializeExtensions(Map<String, Object> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return null;
        }
        try {
            return com.alibaba.fastjson2.JSON.toJSONString(extensions);
        } catch (Exception e) {
            log.warn("Failed to serialize extensions: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, Object> deserializeExtensions(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return com.alibaba.fastjson2.JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize extensions: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
