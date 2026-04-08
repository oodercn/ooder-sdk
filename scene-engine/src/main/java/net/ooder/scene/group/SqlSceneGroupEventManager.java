package net.ooder.scene.group;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL 场景组事件管理器实现
 *
 * <p>基于 SQLite/MySQL 的事件日志持久化存储，支持事件记录和查询。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneGroupEventManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneGroupEventManager.class);

    private static final String CREATE_EVENT_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_group_events (" +
        "event_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "event_type VARCHAR(50) NOT NULL, " +
        "related_id VARCHAR(255), " +
        "description TEXT, " +
        "user_id VARCHAR(255), " +
        "agent_id VARCHAR(255), " +
        "timestamp BIGINT NOT NULL, " +
        "extra_data TEXT" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_event_scene_group ON scene_group_events(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_event_type ON scene_group_events(event_type);" +
        "CREATE INDEX IF NOT EXISTS idx_event_timestamp ON scene_group_events(timestamp);" +
        "CREATE INDEX IF NOT EXISTS idx_event_related ON scene_group_events(related_id)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneGroupEventManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlSceneGroupEventManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneGroupEventManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneGroupEventManager at: {}", jdbcUrl);

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
            log.info("SqlSceneGroupEventManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneGroupEventManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneGroupEventManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_EVENT_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Event tables created/verified");
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
     * 记录事件
     *
     * @param event 事件
     * @return 是否成功
     */
    public boolean recordEvent(SceneGroupEvent event) {
        if (event == null) {
            return false;
        }

        String sql = "INSERT INTO scene_group_events " +
            "(event_id, scene_group_id, event_type, related_id, description, user_id, agent_id, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, event.getEventId());
            pstmt.setString(2, event.getSceneGroupId());
            pstmt.setString(3, event.getType().name());
            pstmt.setString(4, event.getRelatedId());
            pstmt.setString(5, event.getDescription());
            pstmt.setString(6, event.getUserId());
            pstmt.setString(7, event.getAgentId());
            pstmt.setLong(8, event.getTimestamp());

            pstmt.executeUpdate();
            log.debug("Event recorded: eventId={}, type={}", event.getEventId(), event.getType());
            return true;

        } catch (SQLException e) {
            log.error("Failed to record event: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据查询条件获取事件
     *
     * @param sceneGroupId 场景组ID
     * @param query 查询条件
     * @return 事件列表
     */
    public List<SceneGroupEvent> queryEvents(String sceneGroupId, EventLogQuery query) {
        List<SceneGroupEvent> result = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM scene_group_events WHERE scene_group_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(sceneGroupId);

        // 事件类型过滤
        if (query != null && query.getEventTypes() != null && !query.getEventTypes().isEmpty()) {
            sqlBuilder.append(" AND event_type IN (");
            for (int i = 0; i < query.getEventTypes().size(); i++) {
                if (i > 0) sqlBuilder.append(",");
                sqlBuilder.append("?");
                params.add(query.getEventTypes().get(i));
            }
            sqlBuilder.append(")");
        }

        // 时间范围过滤
        if (query != null && query.getStartTime() != null) {
            sqlBuilder.append(" AND timestamp >= ?");
            params.add(query.getStartTime().toEpochMilli());
        }
        if (query != null && query.getEndTime() != null) {
            sqlBuilder.append(" AND timestamp <= ?");
            params.add(query.getEndTime().toEpochMilli());
        }

        // 参与者/相关ID过滤
        if (query != null && query.getParticipantId() != null) {
            sqlBuilder.append(" AND (related_id = ? OR user_id = ?)");
            params.add(query.getParticipantId());
            params.add(query.getParticipantId());
        }
        if (query != null && query.getRelatedId() != null) {
            sqlBuilder.append(" AND related_id = ?");
            params.add(query.getRelatedId());
        }

        // 关键词过滤
        if (query != null && query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            sqlBuilder.append(" AND description LIKE ?");
            params.add("%" + query.getKeyword() + "%");
        }

        // 排序
        sqlBuilder.append(" ORDER BY timestamp ");
        if (query == null || query.isDescending()) {
            sqlBuilder.append("DESC");
        } else {
            sqlBuilder.append("ASC");
        }

        // 分页
        int limit = (query != null) ? query.getLimit() : 100;
        int offset = (query != null) ? query.getOffset() : 0;

        if (limit > 0) {
            if (jdbcUrl.contains("sqlite") || jdbcUrl.contains("h2")) {
                sqlBuilder.append(" LIMIT ? OFFSET ?");
                params.add(limit);
                params.add(offset);
            } else {
                sqlBuilder.append(" LIMIT ? OFFSET ?");
                params.add(limit);
                params.add(offset);
            }
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToEvent(rs));
            }

        } catch (SQLException e) {
            log.error("Failed to query events: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 获取场景组的所有事件
     *
     * @param sceneGroupId 场景组ID
     * @return 事件列表
     */
    public List<SceneGroupEvent> getAllEvents(String sceneGroupId) {
        return queryEvents(sceneGroupId, null);
    }

    /**
     * 获取事件数量
     *
     * @param sceneGroupId 场景组ID
     * @return 事件数量
     */
    public int getEventCount(String sceneGroupId) {
        String sql = "SELECT COUNT(*) FROM scene_group_events WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get event count: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 删除场景组的所有事件
     *
     * @param sceneGroupId 场景组ID
     * @return 删除的事件数量
     */
    public int deleteAllEvents(String sceneGroupId) {
        String sql = "DELETE FROM scene_group_events WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            int rows = pstmt.executeUpdate();
            log.info("All events deleted: sceneGroupId={}, count={}", sceneGroupId, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to delete events: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 清理过期事件
     *
     * @param retentionDays 保留天数
     * @return 删除的事件数量
     */
    public int cleanupOldEvents(int retentionDays) {
        String sql;
        if (jdbcUrl.contains("sqlite")) {
            sql = "DELETE FROM scene_group_events WHERE " +
                "datetime(timestamp/1000, 'unixepoch') < datetime('now', '-' || ? || ' days')";
        } else if (jdbcUrl.contains("h2")) {
            sql = "DELETE FROM scene_group_events WHERE " +
                "timestamp < DATEADD('DAY', -?, CURRENT_TIMESTAMP)";
        } else {
            sql = "DELETE FROM scene_group_events WHERE " +
                "timestamp < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL ? DAY)) * 1000";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, retentionDays);
            int rows = pstmt.executeUpdate();
            log.info("Old events cleaned up: retentionDays={}, deleted={}", retentionDays, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to cleanup old events: {}", e.getMessage());
        }

        return 0;
    }

    private SceneGroupEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        String sceneGroupId = rs.getString("scene_group_id");
        String eventTypeStr = rs.getString("event_type");
        String relatedId = rs.getString("related_id");
        String description = rs.getString("description");

        SceneGroupEvent.Type type = SceneGroupEvent.Type.valueOf(eventTypeStr);
        SceneGroupEvent event = new SceneGroupEvent(sceneGroupId, type, relatedId, description);

        // 使用反射设置其他字段（因为 SceneGroupEvent 的字段是 final 的）
        try {
            java.lang.reflect.Field userIdField = SceneGroupEvent.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(event, rs.getString("user_id"));

            java.lang.reflect.Field agentIdField = SceneGroupEvent.class.getDeclaredField("agentId");
            agentIdField.setAccessible(true);
            agentIdField.set(event, rs.getString("agent_id"));
        } catch (Exception e) {
            log.warn("Failed to set event fields: {}", e.getMessage());
        }

        return event;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
