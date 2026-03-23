package net.ooder.scene.core.activation.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.core.activation.model.SceneKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * SQL场景密钥存储库实现
 * 
 * <p>基于SQLite/MySQL的场景密钥持久化存储</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SqlSceneKeyRepository implements SceneKeyRepository {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneKeyRepository.class);

    private static final String CREATE_KEY_TABLE = 
        "CREATE TABLE IF NOT EXISTS scene_keys (" +
        "key_id VARCHAR(255) PRIMARY KEY, " +
        "key VARCHAR(512), " +
        "key_type VARCHAR(50), " +
        "scene_id VARCHAR(255), " +
        "instance_id VARCHAR(255), " +
        "user_id VARCHAR(255), " +
        "role_id VARCHAR(255), " +
        "created_at BIGINT, " +
        "expires_at BIGINT, " +
        "last_used_at BIGINT, " +
        "active INTEGER, " +
        "permissions TEXT, " +
        "usage_count INTEGER, " +
        "max_usage_count INTEGER, " +
        "description TEXT" +
        ")";

    private static final String CREATE_INDEXES = 
        "CREATE INDEX IF NOT EXISTS idx_key_key ON scene_keys(key);" +
        "CREATE INDEX IF NOT EXISTS idx_key_scene_id ON scene_keys(scene_id);" +
        "CREATE INDEX IF NOT EXISTS idx_key_instance_id ON scene_keys(instance_id);" +
        "CREATE INDEX IF NOT EXISTS idx_key_user_id ON scene_keys(user_id);" +
        "CREATE INDEX IF NOT EXISTS idx_key_type ON scene_keys(key_type);" +
        "CREATE INDEX IF NOT EXISTS idx_key_active ON scene_keys(active)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneKeyRepository(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneKeyRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void initialize() {
        log.info("Initializing SqlSceneKeyRepository at: {}", jdbcUrl);

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
            log.info("SqlSceneKeyRepository initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneKeyRepository: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_KEY_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Database tables created/verified");
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneKeyRepository closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
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
    public SceneKey save(SceneKey key) {
        if (key == null || key.getKeyId() == null) {
            throw new IllegalArgumentException("Key and keyId must not be null");
        }

        String sql = "INSERT OR REPLACE INTO scene_keys " +
            "(key_id, key, key_type, scene_id, instance_id, user_id, role_id, " +
            "created_at, expires_at, last_used_at, active, permissions, usage_count, max_usage_count, description) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, key.getKeyId());
            pstmt.setString(2, key.getKey());
            pstmt.setString(3, key.getKeyType());
            pstmt.setString(4, key.getSceneId());
            pstmt.setString(5, key.getInstanceId());
            pstmt.setString(6, key.getUserId());
            pstmt.setString(7, key.getRoleId());
            pstmt.setLong(8, key.getCreatedAt());
            pstmt.setLong(9, key.getExpiresAt());
            pstmt.setLong(10, key.getLastUsedAt());
            pstmt.setInt(11, key.isActive() ? 1 : 0);
            pstmt.setString(12, serializeList(key.getPermissions()));
            pstmt.setInt(13, key.getUsageCount());
            pstmt.setInt(14, key.getMaxUsageCount());
            pstmt.setString(15, key.getDescription());
            pstmt.executeUpdate();

            log.debug("Saved key: {}", key.getKeyId());
            return key;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save key: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SceneKey> findById(String keyId) {
        if (keyId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM scene_keys WHERE key_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToKey(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find key: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<SceneKey> findByKey(String keyValue) {
        if (keyValue == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM scene_keys WHERE key = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyValue);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToKey(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find key by value: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<SceneKey> findBySceneId(String sceneId) {
        if (sceneId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_keys WHERE scene_id = ?";
        return queryKeys(sql, sceneId);
    }

    @Override
    public List<SceneKey> findByInstanceId(String instanceId) {
        if (instanceId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_keys WHERE instance_id = ?";
        return queryKeys(sql, instanceId);
    }

    @Override
    public List<SceneKey> findByUserId(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_keys WHERE user_id = ?";
        return queryKeys(sql, userId);
    }

    @Override
    public List<SceneKey> findByKeyType(String keyType) {
        if (keyType == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_keys WHERE key_type = ?";
        return queryKeys(sql, keyType);
    }

    @Override
    public List<SceneKey> findValidKeys(String sceneId) {
        if (sceneId == null) {
            return Collections.emptyList();
        }

        long now = System.currentTimeMillis();
        String sql = "SELECT * FROM scene_keys WHERE scene_id = ? AND active = 1 AND (expires_at = 0 OR expires_at > ?)";
        List<SceneKey> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneId);
            pstmt.setLong(2, now);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SceneKey key = mapResultSetToKey(rs);
                if (key.isValid()) {
                    result.add(key);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find valid keys: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public List<SceneKey> findAll() {
        String sql = "SELECT * FROM scene_keys";
        List<SceneKey> result = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSetToKey(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all keys: {}", e.getMessage());
        }
        return result;
    }

    private List<SceneKey> queryKeys(String sql, String param) {
        List<SceneKey> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, param);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToKey(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query keys: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteById(String keyId) {
        if (keyId == null) {
            return false;
        }

        String sql = "DELETE FROM scene_keys WHERE key_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.debug("Deleted key: {}", keyId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete key: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean revokeKey(String keyId) {
        if (keyId == null) {
            return false;
        }

        String sql = "UPDATE scene_keys SET active = 0 WHERE key_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to revoke key: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean incrementUsage(String keyId) {
        if (keyId == null) {
            return false;
        }

        String sql = "UPDATE scene_keys SET usage_count = usage_count + 1, last_used_at = ? WHERE key_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, keyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to increment usage: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM scene_keys";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count keys: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public long countBySceneId(String sceneId) {
        if (sceneId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM scene_keys WHERE scene_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count keys by sceneId: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean existsById(String keyId) {
        if (keyId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM scene_keys WHERE key_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check key existence: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int cleanupExpired() {
        long now = System.currentTimeMillis();
        String sql = "DELETE FROM scene_keys WHERE active = 0 OR (expires_at > 0 AND expires_at < ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, now);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Cleaned up {} expired keys", rows);
            }
            return rows;
        } catch (SQLException e) {
            log.error("Failed to cleanup expired keys: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public Optional<SceneKey> validateKey(String keyValue) {
        if (keyValue == null) {
            return Optional.empty();
        }

        Optional<SceneKey> keyOpt = findByKey(keyValue);
        if (!keyOpt.isPresent()) {
            return Optional.empty();
        }

        SceneKey key = keyOpt.get();
        if (!key.isValid()) {
            return Optional.empty();
        }

        incrementUsage(key.getKeyId());
        return Optional.of(key);
    }

    private SceneKey mapResultSetToKey(ResultSet rs) throws SQLException {
        SceneKey key = new SceneKey();
        key.setKeyId(rs.getString("key_id"));
        key.setKey(rs.getString("key"));
        key.setKeyType(rs.getString("key_type"));
        key.setSceneId(rs.getString("scene_id"));
        key.setInstanceId(rs.getString("instance_id"));
        key.setUserId(rs.getString("user_id"));
        key.setRoleId(rs.getString("role_id"));
        key.setCreatedAt(rs.getLong("created_at"));
        key.setExpiresAt(rs.getLong("expires_at"));
        key.setLastUsedAt(rs.getLong("last_used_at"));
        key.setActive(rs.getInt("active") == 1);
        key.setPermissions(deserializeList(rs.getString("permissions")));
        key.setUsageCount(rs.getInt("usage_count"));
        key.setMaxUsageCount(rs.getInt("max_usage_count"));
        key.setDescription(rs.getString("description"));

        return key;
    }

    private String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize list: {}", e.getMessage());
            return null;
        }
    }

    private List<String> deserializeList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize list: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
