package net.ooder.scene.snapshot;

import com.alibaba.fastjson2.JSON;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBindingManager;
import net.ooder.scene.llm.config.SceneLlmConfigInfo;
import net.ooder.scene.llm.config.SceneLlmConfigManager;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL 场景组快照管理器实现
 *
 * <p>基于 SQLite/MySQL 的快照持久化存储，支持快照的创建、恢复、删除和对比。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneSnapshotManager implements SceneSnapshotManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneSnapshotManager.class);

    private static final String CREATE_SNAPSHOT_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_snapshots (" +
        "snapshot_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "name VARCHAR(500), " +
        "description TEXT, " +
        "status VARCHAR(50), " +
        "create_time TIMESTAMP, " +
        "creator_id VARCHAR(255), " +
        "size BIGINT, " +
        "trigger_type VARCHAR(50), " +
        "snapshot_data TEXT, " +
        "UNIQUE(scene_group_id, snapshot_id)" +
        ")";

    private static final String CREATE_AUTO_SNAPSHOT_CONFIG_TABLE =
        "CREATE TABLE IF NOT EXISTS auto_snapshot_config (" +
        "config_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "trigger_type VARCHAR(50), " +
        "enabled BOOLEAN, " +
        "max_snapshots INTEGER, " +
        "retention_days INTEGER" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_snap_scene_group ON scene_snapshots(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_snap_status ON scene_snapshots(status);" +
        "CREATE INDEX IF NOT EXISTS idx_snap_create_time ON scene_snapshots(create_time)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    private final SceneGroupManager sceneGroupManager;
    private final KnowledgeBindingManager knowledgeBindingManager;
    private final SceneLlmConfigManager sceneLlmConfigManager;

    public SqlSceneSnapshotManager(SceneGroupManager sceneGroupManager,
                                   KnowledgeBindingManager knowledgeBindingManager,
                                   SceneLlmConfigManager sceneLlmConfigManager) {
        this("jdbc:sqlite:./data/scene-engine.db", null, null,
             sceneGroupManager, knowledgeBindingManager, sceneLlmConfigManager);
    }

    public SqlSceneSnapshotManager(String jdbcUrl,
                                   SceneGroupManager sceneGroupManager,
                                   KnowledgeBindingManager knowledgeBindingManager,
                                   SceneLlmConfigManager sceneLlmConfigManager) {
        this(jdbcUrl, null, null, sceneGroupManager, knowledgeBindingManager, sceneLlmConfigManager);
    }

    public SqlSceneSnapshotManager(String jdbcUrl, String username, String password,
                                   SceneGroupManager sceneGroupManager,
                                   KnowledgeBindingManager knowledgeBindingManager,
                                   SceneLlmConfigManager sceneLlmConfigManager) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.sceneGroupManager = sceneGroupManager;
        this.knowledgeBindingManager = knowledgeBindingManager;
        this.sceneLlmConfigManager = sceneLlmConfigManager;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneSnapshotManager at: {}", jdbcUrl);

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
            log.info("SqlSceneSnapshotManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneSnapshotManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneSnapshotManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_SNAPSHOT_TABLE);
            stmt.execute(CREATE_AUTO_SNAPSHOT_CONFIG_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Snapshot tables created/verified");
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
    public SceneSnapshot createSnapshot(String sceneGroupId, String name, String description) {
        return createSnapshot(sceneGroupId, name, description, SnapshotTrigger.MANUAL);
    }

    @Override
    public SceneSnapshot createSnapshot(String sceneGroupId, String name, String description, SnapshotTrigger trigger) {
        if (sceneGroupId == null) {
            throw new IllegalArgumentException("sceneGroupId is required");
        }

        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }

        String snapshotId = generateSnapshotId();
        SceneSnapshot snapshot = new SceneSnapshot();
        snapshot.setSnapshotId(snapshotId);
        snapshot.setSceneGroupId(sceneGroupId);
        snapshot.setName(name != null ? name : "Snapshot-" + snapshotId);
        snapshot.setDescription(description);
        snapshot.setStatus(SnapshotStatus.CREATING);
        snapshot.setCreateTime(LocalDateTime.now());
        snapshot.setTrigger(trigger);

        // 捕获场景组状态
        captureSceneGroupState(snapshot, group);

        // 计算大小
        long size = calculateSnapshotSize(snapshot);
        snapshot.setSize(size);

        // 持久化
        saveSnapshot(snapshot);

        // 更新状态为活跃
        snapshot.setStatus(SnapshotStatus.ACTIVE);
        updateSnapshotStatus(snapshotId, SnapshotStatus.ACTIVE);

        // 检查保留策略
        enforceRetentionPolicy(sceneGroupId);

        log.info("Snapshot created: sceneGroupId={}, snapshotId={}, name={}",
                sceneGroupId, snapshotId, snapshot.getName());
        return snapshot;
    }

    @Override
    public SceneSnapshot getSnapshot(String sceneGroupId, String snapshotId) {
        if (sceneGroupId == null || snapshotId == null) {
            return null;
        }

        String sql = "SELECT * FROM scene_snapshots WHERE scene_group_id = ? AND snapshot_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, snapshotId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSnapshot(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to get snapshot: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public List<SceneSnapshot> listSnapshots(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM scene_snapshots WHERE scene_group_id = ? ORDER BY create_time DESC";
        List<SceneSnapshot> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToSnapshot(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to list snapshots: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<SceneSnapshot> listActiveSnapshots(String sceneGroupId) {
        return listSnapshots(sceneGroupId).stream()
            .filter(s -> s.getStatus() == SnapshotStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    @Override
    public boolean restoreSnapshot(String sceneGroupId, String snapshotId) {
        SceneSnapshot snapshot = getSnapshot(sceneGroupId, snapshotId);
        if (snapshot == null || !snapshot.isAvailable()) {
            log.warn("Snapshot not available for restore: sceneGroupId={}, snapshotId={}",
                    sceneGroupId, snapshotId);
            return false;
        }

        updateSnapshotStatus(snapshotId, SnapshotStatus.RESTORING);

        try {
            SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
            if (group == null) {
                log.error("SceneGroup not found for restore: {}", sceneGroupId);
                return false;
            }

            // 恢复参与者
            restoreParticipants(group, snapshot.getParticipants());

            // 恢复知识库绑定
            restoreKnowledgeBindings(group, snapshot.getKnowledgeBases());

            // 恢复LLM配置
            restoreLlmConfig(group, snapshot.getLlmConfig());

            // 恢复扩展配置
            restoreExtendedConfig(group, snapshot.getExtendedConfig());

            updateSnapshotStatus(snapshotId, SnapshotStatus.ACTIVE);
            log.info("Snapshot restored: sceneGroupId={}, snapshotId={}", sceneGroupId, snapshotId);
            return true;

        } catch (Exception e) {
            log.error("Failed to restore snapshot: {}", e.getMessage(), e);
            updateSnapshotStatus(snapshotId, SnapshotStatus.ERROR);
            return false;
        }
    }

    @Override
    public boolean deleteSnapshot(String sceneGroupId, String snapshotId) {
        if (sceneGroupId == null || snapshotId == null) {
            return false;
        }

        updateSnapshotStatus(snapshotId, SnapshotStatus.DELETING);

        String sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND snapshot_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, snapshotId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Snapshot deleted: sceneGroupId={}, snapshotId={}", sceneGroupId, snapshotId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete snapshot: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public int deleteAllSnapshots(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            int rows = pstmt.executeUpdate();
            log.info("All snapshots deleted: sceneGroupId={}, count={}", sceneGroupId, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to delete all snapshots: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public SnapshotDiff compareSnapshots(String sceneGroupId, String snapshotId1, String snapshotId2) {
        SceneSnapshot snapshot1 = getSnapshot(sceneGroupId, snapshotId1);
        SceneSnapshot snapshot2 = getSnapshot(sceneGroupId, snapshotId2);

        if (snapshot1 == null || snapshot2 == null) {
            throw new IllegalArgumentException("One or both snapshots not found");
        }

        SnapshotDiff diff = new SnapshotDiff();
        diff.setSnapshotId1(snapshotId1);
        diff.setSnapshotId2(snapshotId2);

        // 对比参与者
        compareParticipants(diff, snapshot1.getParticipants(), snapshot2.getParticipants());

        // 对比能力
        compareCapabilities(diff, snapshot1.getCapabilities(), snapshot2.getCapabilities());

        // 对比知识库
        compareKnowledgeBases(diff, snapshot1.getKnowledgeBases(), snapshot2.getKnowledgeBases());

        // 对比LLM配置
        compareLlmConfig(diff, snapshot1.getLlmConfig(), snapshot2.getLlmConfig());

        return diff;
    }

    @Override
    public void setAutoSnapshot(String sceneGroupId, SnapshotTrigger trigger, boolean enabled) {
        String sql = "INSERT OR REPLACE INTO auto_snapshot_config " +
            "(config_id, scene_group_id, trigger_type, enabled) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            String configId = sceneGroupId + "-" + trigger.name();
            pstmt.setString(1, configId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, trigger.name());
            pstmt.setBoolean(4, enabled);
            pstmt.executeUpdate();
            log.info("Auto snapshot config updated: sceneGroupId={}, trigger={}, enabled={}",
                    sceneGroupId, trigger, enabled);
        } catch (SQLException e) {
            log.error("Failed to set auto snapshot config: {}", e.getMessage());
        }
    }

    @Override
    public void setSnapshotRetention(String sceneGroupId, int maxSnapshots, int retentionDays) {
        String sql = "INSERT OR REPLACE INTO auto_snapshot_config " +
            "(config_id, scene_group_id, max_snapshots, retention_days) " +
            "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            String configId = sceneGroupId + "-retention";
            pstmt.setString(1, configId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setInt(3, maxSnapshots);
            pstmt.setInt(4, retentionDays);
            pstmt.executeUpdate();
            log.info("Snapshot retention set: sceneGroupId={}, maxSnapshots={}, retentionDays={}",
                    sceneGroupId, maxSnapshots, retentionDays);
        } catch (SQLException e) {
            log.error("Failed to set snapshot retention: {}", e.getMessage());
        }
    }

    @Override
    public int getSnapshotCount(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM scene_snapshots WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get snapshot count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean exists(String sceneGroupId, String snapshotId) {
        return getSnapshot(sceneGroupId, snapshotId) != null;
    }

    // ===== 私有方法 =====

    private String generateSnapshotId() {
        return "snap-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void captureSceneGroupState(SceneSnapshot snapshot, SceneGroup group) {
        // 元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", group.getName());
        metadata.put("description", group.getDescription());
        metadata.put("status", group.getStatus());
        metadata.put("templateId", group.getTemplateId());
        snapshot.setMetadata(metadata);

        // 参与者
        List<ParticipantSnapshot> participantSnapshots = new ArrayList<>();
        for (Participant participant : group.getAllParticipants()) {
            ParticipantSnapshot ps = new ParticipantSnapshot();
            ps.setParticipantId(participant.getParticipantId());
            ps.setUserId(participant.getUserId());
            ps.setRole(participant.getRole().name());
            ps.setStatus(participant.getStatus().name());
            // joinTime 是 Instant 类型，转换为时间戳
            if (participant.getJoinTime() != null) {
                ps.setJoinTime(participant.getJoinTime().toEpochMilli());
            }
            participantSnapshots.add(ps);
        }
        snapshot.setParticipants(participantSnapshots);

        // 知识库绑定
        List<KnowledgeBindingSnapshot> kbSnapshots = new ArrayList<>();
        List<KnowledgeBinding> bindings = knowledgeBindingManager.getBindings(group.getSceneGroupId());
        for (KnowledgeBinding binding : bindings) {
            KnowledgeBindingSnapshot kbs = new KnowledgeBindingSnapshot();
            kbs.setKbId(binding.getKnowledgeBaseId());
            kbs.setKbName(binding.getKnowledgeBaseName());
            kbs.setScope(binding.getLayer());
            kbs.setPriority(binding.getPriority());
            kbs.setBindTime(binding.getBindTime());
            kbSnapshots.add(kbs);
        }
        snapshot.setKnowledgeBases(kbSnapshots);

        // LLM配置
        SceneLlmConfigInfo llmConfig = sceneLlmConfigManager.getLlmConfig(group.getSceneGroupId());
        if (llmConfig != null) {
            LlmConfigSnapshot lcs = new LlmConfigSnapshot();
            lcs.setProvider(llmConfig.getProvider());
            lcs.setModel(llmConfig.getModel());
            lcs.setTemperature(llmConfig.getTemperature());
            lcs.setMaxTokens(llmConfig.getMaxTokens());
            lcs.setTimeout(llmConfig.getTimeout());
            lcs.setExtensions(llmConfig.getExtensions());
            snapshot.setLlmConfig(lcs);
        }

        // 扩展配置
        snapshot.setExtendedConfig(new HashMap<>(group.getAllConfig()));
    }

    private long calculateSnapshotSize(SceneSnapshot snapshot) {
        try {
            String json = JSON.toJSONString(snapshot);
            return json.getBytes().length;
        } catch (Exception e) {
            return 0;
        }
    }

    private void saveSnapshot(SceneSnapshot snapshot) {
        String sql = "INSERT INTO scene_snapshots " +
            "(snapshot_id, scene_group_id, name, description, status, create_time, creator_id, size, trigger_type, snapshot_data) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, snapshot.getSnapshotId());
            pstmt.setString(2, snapshot.getSceneGroupId());
            pstmt.setString(3, snapshot.getName());
            pstmt.setString(4, snapshot.getDescription());
            pstmt.setString(5, snapshot.getStatus().name());
            pstmt.setTimestamp(6, Timestamp.valueOf(snapshot.getCreateTime()));
            pstmt.setString(7, snapshot.getCreatorId());
            pstmt.setLong(8, snapshot.getSize() != null ? snapshot.getSize() : 0);
            pstmt.setString(9, snapshot.getTrigger() != null ? snapshot.getTrigger().name() : null);
            pstmt.setString(10, JSON.toJSONString(snapshot));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to save snapshot: {}", e.getMessage());
            throw new RuntimeException("Failed to save snapshot", e);
        }
    }

    private void updateSnapshotStatus(String snapshotId, SnapshotStatus status) {
        String sql = "UPDATE scene_snapshots SET status = ? WHERE snapshot_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setString(2, snapshotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update snapshot status: {}", e.getMessage());
        }
    }

    private SceneSnapshot mapResultSetToSnapshot(ResultSet rs) throws SQLException {
        String snapshotData = rs.getString("snapshot_data");
        if (snapshotData != null && !snapshotData.isEmpty()) {
            try {
                return JSON.parseObject(snapshotData, SceneSnapshot.class);
            } catch (Exception e) {
                log.warn("Failed to parse snapshot data, using fallback: {}", e.getMessage());
            }
        }

        // Fallback to manual mapping
        SceneSnapshot snapshot = new SceneSnapshot();
        snapshot.setSnapshotId(rs.getString("snapshot_id"));
        snapshot.setSceneGroupId(rs.getString("scene_group_id"));
        snapshot.setName(rs.getString("name"));
        snapshot.setDescription(rs.getString("description"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            snapshot.setStatus(SnapshotStatus.valueOf(statusStr));
        }
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            snapshot.setCreateTime(createTime.toLocalDateTime());
        }
        snapshot.setCreatorId(rs.getString("creator_id"));
        snapshot.setSize(rs.getLong("size"));
        String triggerStr = rs.getString("trigger_type");
        if (triggerStr != null) {
            snapshot.setTrigger(SnapshotTrigger.valueOf(triggerStr));
        }
        return snapshot;
    }

    private void enforceRetentionPolicy(String sceneGroupId) {
        // 获取保留策略
        String sql = "SELECT max_snapshots, retention_days FROM auto_snapshot_config WHERE scene_group_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int maxSnapshots = rs.getInt("max_snapshots");
                int retentionDays = rs.getInt("retention_days");

                // 执行最大数量限制
                if (maxSnapshots > 0) {
                    enforceMaxSnapshots(sceneGroupId, maxSnapshots);
                }

                // 执行保留天数限制
                if (retentionDays > 0) {
                    enforceRetentionDays(sceneGroupId, retentionDays);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to enforce retention policy: {}", e.getMessage());
        }
    }

    private void enforceMaxSnapshots(String sceneGroupId, int maxSnapshots) {
        // 使用子查询删除超出限制的快照（保留最新的）
        String sql;
        if (jdbcUrl.contains("sqlite") || jdbcUrl.contains("h2")) {
            // SQLite/H2 语法
            sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND snapshot_id NOT IN " +
                "(SELECT snapshot_id FROM scene_snapshots WHERE scene_group_id = ? ORDER BY create_time DESC LIMIT ?)";
        } else {
            // MySQL 语法
            sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND snapshot_id NOT IN " +
                "(SELECT snapshot_id FROM (SELECT snapshot_id FROM scene_snapshots WHERE scene_group_id = ? ORDER BY create_time DESC LIMIT ?) AS tmp)";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setInt(3, maxSnapshots);
            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                log.info("Enforced max snapshots: sceneGroupId={}, maxSnapshots={}, deleted={}",
                    sceneGroupId, maxSnapshots, deleted);
            }
        } catch (SQLException e) {
            log.error("Failed to enforce max snapshots: {}", e.getMessage());
        }
    }

    private void enforceRetentionDays(String sceneGroupId, int retentionDays) {
        // 删除超过保留天数的快照
        String sql;
        if (jdbcUrl.contains("sqlite")) {
            // SQLite 语法
            sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND " +
                "datetime(create_time) < datetime('now', '-' || ? || ' days')";
        } else if (jdbcUrl.contains("h2")) {
            // H2 语法
            sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND " +
                "create_time < DATEADD('DAY', -?, CURRENT_TIMESTAMP)";
        } else {
            // MySQL 语法
            sql = "DELETE FROM scene_snapshots WHERE scene_group_id = ? AND " +
                "create_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setInt(2, retentionDays);
            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                log.info("Enforced retention days: sceneGroupId={}, retentionDays={}, deleted={}",
                    sceneGroupId, retentionDays, deleted);
            }
        } catch (SQLException e) {
            log.error("Failed to enforce retention days: {}", e.getMessage());
        }
    }

    // ===== 恢复方法 =====

    private void restoreParticipants(SceneGroup group, List<ParticipantSnapshot> participants) {
        // 注意：SceneGroup 没有提供清除所有参与者的方法
        // 在实际实现中，这里应该通过 SceneGroupManager 或其他方式处理

        // 恢复参与者
        for (ParticipantSnapshot ps : participants) {
            Participant participant = new Participant(ps.getParticipantId(), ps.getUserId(),
                ps.getUserId(), Participant.Type.USER);
            try {
                participant.setRole(Participant.Role.valueOf(ps.getRole()));
            } catch (IllegalArgumentException e) {
                participant.setRole(Participant.Role.EMPLOYEE);
            }
            // 状态通过 activate/join 方法设置
            if ("ACTIVE".equals(ps.getStatus())) {
                participant.activate();
            } else if ("JOINED".equals(ps.getStatus())) {
                participant.join();
            }
            group.addParticipant(participant);
        }
    }

    private void restoreKnowledgeBindings(SceneGroup group, List<KnowledgeBindingSnapshot> knowledgeBases) {
        // 清除现有绑定
        knowledgeBindingManager.clearAllBindings(group.getSceneGroupId());

        // 恢复绑定
        for (KnowledgeBindingSnapshot kbs : knowledgeBases) {
            try {
                KnowledgeBinding binding = new KnowledgeBinding();
                binding.setKnowledgeBaseId(kbs.getKbId());
                binding.setKnowledgeBaseName(kbs.getKbName());
                if (kbs.getScope() != null) {
                    binding.setLayer(kbs.getScope());
                }
                binding.setPriority(kbs.getPriority());
                binding.setBindTime(kbs.getBindTime());
                knowledgeBindingManager.bind(group.getSceneGroupId(), binding);
            } catch (Exception e) {
                log.warn("Failed to restore knowledge binding: {}", kbs.getKbId(), e);
            }
        }
    }

    private void restoreLlmConfig(SceneGroup group, LlmConfigSnapshot llmConfig) {
        if (llmConfig == null) {
            return;
        }

        SceneLlmConfigInfo config = new SceneLlmConfigInfo();
        config.setProvider(llmConfig.getProvider());
        config.setModel(llmConfig.getModel());
        config.setTemperature(llmConfig.getTemperature());
        config.setMaxTokens(llmConfig.getMaxTokens());
        config.setTimeout(llmConfig.getTimeout());
        config.setExtensions(llmConfig.getExtensions());

        sceneLlmConfigManager.setLlmConfig(group.getSceneGroupId(), config);
    }

    private void restoreExtendedConfig(SceneGroup group, Map<String, Object> extendedConfig) {
        if (extendedConfig == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : extendedConfig.entrySet()) {
            group.setConfig(entry.getKey(), entry.getValue());
        }
    }

    // ===== 对比方法 =====

    private void compareParticipants(SnapshotDiff diff, List<ParticipantSnapshot> list1, List<ParticipantSnapshot> list2) {
        Map<String, ParticipantSnapshot> map1 = list1.stream()
            .collect(Collectors.toMap(ParticipantSnapshot::getParticipantId, p -> p));
        Map<String, ParticipantSnapshot> map2 = list2.stream()
            .collect(Collectors.toMap(ParticipantSnapshot::getParticipantId, p -> p));

        // 新增的
        for (String id : map2.keySet()) {
            if (!map1.containsKey(id)) {
                diff.getAddedParticipants().add(map2.get(id));
            }
        }

        // 删除的
        for (String id : map1.keySet()) {
            if (!map2.containsKey(id)) {
                diff.getRemovedParticipants().add(map1.get(id));
            }
        }
    }

    private void compareCapabilities(SnapshotDiff diff, List<CapabilitySnapshot> list1, List<CapabilitySnapshot> list2) {
        Map<String, CapabilitySnapshot> map1 = list1.stream()
            .collect(Collectors.toMap(CapabilitySnapshot::getCapId, c -> c));
        Map<String, CapabilitySnapshot> map2 = list2.stream()
            .collect(Collectors.toMap(CapabilitySnapshot::getCapId, c -> c));

        for (String id : map2.keySet()) {
            if (!map1.containsKey(id)) {
                diff.getAddedCapabilities().add(map2.get(id));
            }
        }

        for (String id : map1.keySet()) {
            if (!map2.containsKey(id)) {
                diff.getRemovedCapabilities().add(map1.get(id));
            }
        }
    }

    private void compareKnowledgeBases(SnapshotDiff diff, List<KnowledgeBindingSnapshot> list1, List<KnowledgeBindingSnapshot> list2) {
        Map<String, KnowledgeBindingSnapshot> map1 = list1.stream()
            .collect(Collectors.toMap(KnowledgeBindingSnapshot::getKbId, k -> k));
        Map<String, KnowledgeBindingSnapshot> map2 = list2.stream()
            .collect(Collectors.toMap(KnowledgeBindingSnapshot::getKbId, k -> k));

        for (String id : map2.keySet()) {
            if (!map1.containsKey(id)) {
                diff.getAddedKnowledgeBases().add(map2.get(id));
            }
        }

        for (String id : map1.keySet()) {
            if (!map2.containsKey(id)) {
                diff.getRemovedKnowledgeBases().add(map1.get(id));
            }
        }
    }

    private void compareLlmConfig(SnapshotDiff diff, LlmConfigSnapshot config1, LlmConfigSnapshot config2) {
        if (config1 == null && config2 == null) {
            return;
        }

        if (config1 == null || config2 == null) {
            diff.setLlmConfigChanged(true);
            diff.setLlmConfig1(config1);
            diff.setLlmConfig2(config2);
            return;
        }

        boolean changed = !Objects.equals(config1.getProvider(), config2.getProvider())
            || !Objects.equals(config1.getModel(), config2.getModel())
            || config1.getTemperature() != config2.getTemperature()
            || config1.getMaxTokens() != config2.getMaxTokens();

        if (changed) {
            diff.setLlmConfigChanged(true);
            diff.setLlmConfig1(config1);
            diff.setLlmConfig2(config2);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
