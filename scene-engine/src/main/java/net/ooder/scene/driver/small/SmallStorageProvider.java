package net.ooder.scene.driver.small;

import com.alibaba.fastjson.JSON;
import net.ooder.scene.spi.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC 存储提供者 - Small 实现
 *
 * <p>基于 JDBC 数据库的存储实现，适用于小型生产环境</p>
 *
 * <p>配置项：</p>
 * <pre>
 * scene.engine.small.storage.table-prefix: scene_
 * </pre>
 *
 * <p>依赖：</p>
 * <ul>
 *   <li>MySQL 或 PostgreSQL 或 H2</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(StorageProvider.class)
@ConditionalOnProperty(prefix = "scene.engine", name = "driver", havingValue = "small")
public class SmallStorageProvider implements StorageProvider {

    private static final Logger log = LoggerFactory.getLogger(SmallStorageProvider.class);

    private final DataSource dataSource;
    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    @Value("${scene.engine.small.storage.table-prefix:scene_}")
    private String tablePrefix;

    @Autowired(required = false)
    public SmallStorageProvider(DataSource dataSource) {
        this.dataSource = dataSource;
        if (dataSource != null) {
            initTables();
        }
    }

    @Override
    public String getProviderType() {
        return "small";
    }

    private void initTables() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %sstorage (" +
                "collection VARCHAR(255) NOT NULL, " +
                "key VARCHAR(255) NOT NULL, " +
                "value TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (collection, key))"
            );
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                log.info("Initialized storage table: {}storage", tablePrefix);
            }
        } catch (SQLException e) {
            log.warn("Could not initialize storage table: {}", e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String collection, String key, Class<T> type) {
        Map<String, Object> collectionCache = cache.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        Object cached = collectionCache.get(key);
        if (cached != null) {
            return Optional.of(type.cast(cached));
        }

        if (dataSource == null) {
            return Optional.empty();
        }

        String sql = String.format(
            "SELECT value FROM %sstorage WHERE collection = ? AND key = ?",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("value");
                    T value = JSON.parseObject(json, type);
                    collectionCache.put(key, value);
                    return Optional.of(value);
                }
            }
        } catch (Exception e) {
            log.error("Failed to read from storage: {}/{}", collection, key, e);
        }

        return Optional.empty();
    }

    @Override
    public <T> void put(String collection, String key, T value) {
        Map<String, Object> collectionCache = cache.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        collectionCache.put(key, value);

        if (dataSource == null) {
            log.debug("No DataSource, cached only: {}/{}", collection, key);
            return;
        }

        String sql = String.format(
            "INSERT INTO %sstorage (collection, key, value) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE value = VALUES(value), updated_at = CURRENT_TIMESTAMP",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            ps.setString(2, key);
            ps.setString(3, JSON.toJSONString(value));
            ps.executeUpdate();
            log.debug("Stored: {}/{}", collection, key);
        } catch (Exception e) {
            log.error("Failed to write to storage: {}/{}", collection, key, e);
        }
    }

    @Override
    public void remove(String collection, String key) {
        Map<String, Object> collectionCache = cache.get(collection);
        if (collectionCache != null) {
            collectionCache.remove(key);
        }

        if (dataSource == null) {
            return;
        }

        String sql = String.format(
            "DELETE FROM %sstorage WHERE collection = ? AND key = ?",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            ps.setString(2, key);
            ps.executeUpdate();
            log.debug("Removed: {}/{}", collection, key);
        } catch (SQLException e) {
            log.error("Failed to remove from storage: {}/{}", collection, key, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String collection, Class<T> type) {
        Map<String, T> result = new HashMap<>();

        if (dataSource == null) {
            Map<String, Object> collectionCache = cache.get(collection);
            if (collectionCache != null) {
                collectionCache.forEach((k, v) -> result.put(k, type.cast(v)));
            }
            return result;
        }

        String sql = String.format(
            "SELECT key, value FROM %sstorage WHERE collection = ?",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("key");
                    String json = rs.getString("value");
                    T value = JSON.parseObject(json, type);
                    result.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("Failed to get all from storage: {}", collection, e);
        }

        return result;
    }

    @Override
    public boolean exists(String collection, String key) {
        if (dataSource == null) {
            Map<String, Object> collectionCache = cache.get(collection);
            return collectionCache != null && collectionCache.containsKey(key);
        }

        String sql = String.format(
            "SELECT COUNT(*) FROM %sstorage WHERE collection = ? AND key = ?",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            log.error("Failed to check existence: {}/{}", collection, key, e);
        }

        return false;
    }

    @Override
    public void clear(String collection) {
        cache.remove(collection);

        if (dataSource == null) {
            return;
        }

        String sql = String.format(
            "DELETE FROM %sstorage WHERE collection = ?",
            tablePrefix
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collection);
            ps.executeUpdate();
            log.info("Cleared collection: {}", collection);
        } catch (SQLException e) {
            log.error("Failed to clear: {}", collection, e);
        }
    }
}
