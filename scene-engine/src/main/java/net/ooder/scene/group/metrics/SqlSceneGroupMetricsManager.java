package net.ooder.scene.group.metrics;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 场景组监控指标管理器实现
 *
 * <p>基于 SQLite/MySQL 的监控指标持久化存储，支持历史指标数据的存储和查询。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneGroupMetricsManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneGroupMetricsManager.class);

    private static final String CREATE_METRICS_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_group_metrics_history (" +
        "metric_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "metric_type VARCHAR(50) NOT NULL, " +
        "metric_value BIGINT NOT NULL, " +
        "metric_label VARCHAR(100), " +
        "record_time TIMESTAMP NOT NULL, " +
        "extra_data TEXT" +
        ")";

    private static final String CREATE_SNAPSHOT_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_group_metrics_snapshots (" +
        "snapshot_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "snapshot_data TEXT NOT NULL, " +
        "record_time TIMESTAMP NOT NULL" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_metrics_scene_group ON scene_group_metrics_history(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_metrics_type ON scene_group_metrics_history(metric_type);" +
        "CREATE INDEX IF NOT EXISTS idx_metrics_time ON scene_group_metrics_history(record_time);" +
        "CREATE INDEX IF NOT EXISTS idx_snapshot_scene_group ON scene_group_metrics_snapshots(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_snapshot_time ON scene_group_metrics_snapshots(record_time)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    // 内存缓存当前指标
    private final Map<String, SceneGroupMetrics> metricsCache = new HashMap<>();

    public SqlSceneGroupMetricsManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlSceneGroupMetricsManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneGroupMetricsManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneGroupMetricsManager at: {}", jdbcUrl);

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
            log.info("SqlSceneGroupMetricsManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneGroupMetricsManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneGroupMetricsManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_METRICS_TABLE);
            stmt.execute(CREATE_SNAPSHOT_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Metrics tables created/verified");
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
     * 获取或创建场景组的指标对象
     *
     * @param sceneGroupId 场景组ID
     * @return 指标对象
     */
    public SceneGroupMetrics getMetrics(String sceneGroupId) {
        return metricsCache.computeIfAbsent(sceneGroupId, SceneGroupMetrics::new);
    }

    /**
     * 记录指标值
     *
     * @param sceneGroupId 场景组ID
     * @param metricType 指标类型
     * @param value 指标值
     * @param label 标签
     * @return 是否成功
     */
    public boolean recordMetric(String sceneGroupId, String metricType, long value, String label) {
        if (sceneGroupId == null || metricType == null) {
            return false;
        }

        String metricId = sceneGroupId + ":" + metricType + ":" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO scene_group_metrics_history " +
            "(metric_id, scene_group_id, metric_type, metric_value, metric_label, record_time) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, metricId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, metricType);
            pstmt.setLong(4, value);
            pstmt.setString(5, label);
            pstmt.setTimestamp(6, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            log.error("Failed to record metric: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 保存指标快照
     *
     * @param sceneGroupId 场景组ID
     * @return 是否成功
     */
    public boolean saveMetricsSnapshot(String sceneGroupId) {
        SceneGroupMetrics metrics = getMetrics(sceneGroupId);
        if (metrics == null) {
            return false;
        }

        String snapshotId = sceneGroupId + ":" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        // 构建快照数据
        Map<String, Object> snapshotData = new HashMap<>();
        snapshotData.put("currentParticipants", metrics.getCurrentParticipants());
        snapshotData.put("totalParticipantsJoined", metrics.getTotalParticipantsJoined());
        snapshotData.put("totalParticipantsLeft", metrics.getTotalParticipantsLeft());
        snapshotData.put("totalMessages", metrics.getTotalMessages());
        snapshotData.put("totalLlmCalls", metrics.getTotalLlmCalls());
        snapshotData.put("totalLlmTokens", metrics.getTotalLlmTokens());
        snapshotData.put("totalKnowledgeQueries", metrics.getTotalKnowledgeQueries());
        snapshotData.put("totalKnowledgeResults", metrics.getTotalKnowledgeResults());
        snapshotData.put("totalErrors", metrics.getTotalErrors());
        snapshotData.put("averageResponseTimeMs", metrics.getAverageResponseTimeMs());
        snapshotData.put("errorRate", metrics.getErrorRate());
        snapshotData.put("timestamp", Instant.now().toString());

        String sql = "INSERT INTO scene_group_metrics_snapshots " +
            "(snapshot_id, scene_group_id, snapshot_data, record_time) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, snapshotId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, JSON.toJSONString(snapshotData));
            pstmt.setTimestamp(4, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            log.debug("Metrics snapshot saved: sceneGroupId={}", sceneGroupId);
            return true;

        } catch (SQLException e) {
            log.error("Failed to save metrics snapshot: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 查询历史指标
     *
     * @param sceneGroupId 场景组ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标记录列表
     */
    public List<MetricRecord> queryMetrics(String sceneGroupId, String metricType, 
                                           LocalDateTime startTime, LocalDateTime endTime) {
        List<MetricRecord> result = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM scene_group_metrics_history WHERE scene_group_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(sceneGroupId);

        if (metricType != null) {
            sqlBuilder.append(" AND metric_type = ?");
            params.add(metricType);
        }
        if (startTime != null) {
            sqlBuilder.append(" AND record_time >= ?");
            params.add(Timestamp.valueOf(startTime));
        }
        if (endTime != null) {
            sqlBuilder.append(" AND record_time <= ?");
            params.add(Timestamp.valueOf(endTime));
        }

        sqlBuilder.append(" ORDER BY record_time DESC");

        try (PreparedStatement pstmt = getConnection().prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToMetricRecord(rs));
            }

        } catch (SQLException e) {
            log.error("Failed to query metrics: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 获取最新的指标快照
     *
     * @param sceneGroupId 场景组ID
     * @return 快照数据
     */
    public Map<String, Object> getLatestSnapshot(String sceneGroupId) {
        String sql = "SELECT snapshot_data FROM scene_group_metrics_snapshots " +
            "WHERE scene_group_id = ? ORDER BY record_time DESC LIMIT 1";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String data = rs.getString("snapshot_data");
                return JSON.parseObject(data, Map.class);
            }

        } catch (SQLException e) {
            log.error("Failed to get latest snapshot: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 获取指标统计
     *
     * @param sceneGroupId 场景组ID
     * @param metricType 指标类型
     * @return 统计值
     */
    public MetricStatistics getMetricStatistics(String sceneGroupId, String metricType) {
        String sql;
        if (jdbcUrl.contains("sqlite")) {
            sql = "SELECT COUNT(*) as count, AVG(metric_value) as avg, " +
                "MIN(metric_value) as min, MAX(metric_value) as max, " +
                "SUM(metric_value) as sum FROM scene_group_metrics_history " +
                "WHERE scene_group_id = ? AND metric_type = ?";
        } else {
            sql = "SELECT COUNT(*) as count, AVG(metric_value) as avg, " +
                "MIN(metric_value) as min, MAX(metric_value) as max, " +
                "SUM(metric_value) as sum FROM scene_group_metrics_history " +
                "WHERE scene_group_id = ? AND metric_type = ?";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, metricType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MetricStatistics stats = new MetricStatistics();
                stats.setCount(rs.getLong("count"));
                stats.setAverage(rs.getDouble("avg"));
                stats.setMin(rs.getLong("min"));
                stats.setMax(rs.getLong("max"));
                stats.setSum(rs.getLong("sum"));
                return stats;
            }

        } catch (SQLException e) {
            log.error("Failed to get metric statistics: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 清理过期指标数据
     *
     * @param retentionDays 保留天数
     * @return 删除的记录数
     */
    public int cleanupOldMetrics(int retentionDays) {
        String sql;
        if (jdbcUrl.contains("sqlite")) {
            sql = "DELETE FROM scene_group_metrics_history WHERE " +
                "datetime(record_time) < datetime('now', '-' || ? || ' days')";
        } else if (jdbcUrl.contains("h2")) {
            sql = "DELETE FROM scene_group_metrics_history WHERE " +
                "record_time < DATEADD('DAY', -?, CURRENT_TIMESTAMP)";
        } else {
            sql = "DELETE FROM scene_group_metrics_history WHERE " +
                "record_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, retentionDays);
            int rows = pstmt.executeUpdate();
            log.info("Old metrics cleaned up: retentionDays={}, deleted={}", retentionDays, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to cleanup old metrics: {}", e.getMessage());
        }

        return 0;
    }

    /**
     * 删除场景组的所有指标数据
     *
     * @param sceneGroupId 场景组ID
     * @return 删除的记录数
     */
    public int deleteAllMetrics(String sceneGroupId) {
        int totalDeleted = 0;

        // 删除历史指标
        String sql1 = "DELETE FROM scene_group_metrics_history WHERE scene_group_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql1)) {
            pstmt.setString(1, sceneGroupId);
            totalDeleted += pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete metrics history: {}", e.getMessage());
        }

        // 删除快照
        String sql2 = "DELETE FROM scene_group_metrics_snapshots WHERE scene_group_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql2)) {
            pstmt.setString(1, sceneGroupId);
            totalDeleted += pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete metrics snapshots: {}", e.getMessage());
        }

        // 清理缓存
        metricsCache.remove(sceneGroupId);

        log.info("All metrics deleted: sceneGroupId={}, totalDeleted={}", sceneGroupId, totalDeleted);
        return totalDeleted;
    }

    private MetricRecord mapResultSetToMetricRecord(ResultSet rs) throws SQLException {
        MetricRecord record = new MetricRecord();
        record.setMetricId(rs.getString("metric_id"));
        record.setSceneGroupId(rs.getString("scene_group_id"));
        record.setMetricType(rs.getString("metric_type"));
        record.setValue(rs.getLong("metric_value"));
        record.setLabel(rs.getString("metric_label"));
        record.setRecordTime(rs.getTimestamp("record_time").toLocalDateTime());
        return record;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 指标记录
     */
    public static class MetricRecord {
        private String metricId;
        private String sceneGroupId;
        private String metricType;
        private long value;
        private String label;
        private LocalDateTime recordTime;

        public String getMetricId() { return metricId; }
        public void setMetricId(String metricId) { this.metricId = metricId; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getMetricType() { return metricType; }
        public void setMetricType(String metricType) { this.metricType = metricType; }
        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public LocalDateTime getRecordTime() { return recordTime; }
        public void setRecordTime(LocalDateTime recordTime) { this.recordTime = recordTime; }
    }

    /**
     * 指标统计
     */
    public static class MetricStatistics {
        private long count;
        private double average;
        private long min;
        private long max;
        private long sum;

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public double getAverage() { return average; }
        public void setAverage(double average) { this.average = average; }
        public long getMin() { return min; }
        public void setMin(long min) { this.min = min; }
        public long getMax() { return max; }
        public void setMax(long max) { this.max = max; }
        public long getSum() { return sum; }
        public void setSum(long sum) { this.sum = sum; }
    }
}
