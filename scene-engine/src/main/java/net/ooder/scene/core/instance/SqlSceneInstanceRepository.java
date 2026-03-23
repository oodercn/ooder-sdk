package net.ooder.scene.core.instance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * SQL场景实例存储库实现
 * 
 * <p>基于SQLite/MySQL的场景实例持久化存储，支持：</p>
 * <ul>
 *   <li>SQLite: jdbc:sqlite:/path/to/scene.db</li>
 *   <li>MySQL: jdbc:mysql://host:3306/scene_db</li>
 *   <li>H2: jdbc:h2:mem:testdb</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SqlSceneInstanceRepository implements SceneInstanceRepository {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneInstanceRepository.class);

    private static final String CREATE_INSTANCE_TABLE = 
        "CREATE TABLE IF NOT EXISTS scene_instances (" +
        "instance_id VARCHAR(255) PRIMARY KEY, " +
        "scene_id VARCHAR(255), " +
        "template_id VARCHAR(255), " +
        "template_name VARCHAR(500), " +
        "skill_id VARCHAR(255), " +
        "skill_name VARCHAR(500), " +
        "state VARCHAR(50), " +
        "visibility VARCHAR(50), " +
        "category VARCHAR(100), " +
        "config TEXT, " +
        "runtime_data TEXT, " +
        "context TEXT, " +
        "tags TEXT, " +
        "metadata TEXT, " +
        "created_at BIGINT, " +
        "updated_at BIGINT, " +
        "activated_at BIGINT, " +
        "deactivated_at BIGINT, " +
        "created_by VARCHAR(255), " +
        "updated_by VARCHAR(255)" +
        ")";

    private static final String CREATE_PARTICIPANT_TABLE = 
        "CREATE TABLE IF NOT EXISTS scene_participants (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "instance_id VARCHAR(255) NOT NULL, " +
        "user_id VARCHAR(255) NOT NULL, " +
        "user_name VARCHAR(500), " +
        "role_id VARCHAR(255), " +
        "role_name VARCHAR(500), " +
        "status VARCHAR(50), " +
        "config TEXT, " +
        "permissions TEXT, " +
        "joined_at BIGINT, " +
        "left_at BIGINT, " +
        "UNIQUE(instance_id, user_id), " +
        "FOREIGN KEY (instance_id) REFERENCES scene_instances(instance_id) ON DELETE CASCADE" +
        ")";

    private static final String CREATE_ACTIVATION_RECORD_TABLE = 
        "CREATE TABLE IF NOT EXISTS scene_activation_records (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "instance_id VARCHAR(255) NOT NULL, " +
        "record_id VARCHAR(255), " +
        "user_id VARCHAR(255), " +
        "role_id VARCHAR(255), " +
        "step_id VARCHAR(255), " +
        "step_name VARCHAR(500), " +
        "action VARCHAR(255), " +
        "success INTEGER, " +
        "message TEXT, " +
        "input TEXT, " +
        "output TEXT, " +
        "timestamp BIGINT, " +
        "FOREIGN KEY (instance_id) REFERENCES scene_instances(instance_id) ON DELETE CASCADE" +
        ")";

    private static final String CREATE_INDEXES = 
        "CREATE INDEX IF NOT EXISTS idx_instance_scene_id ON scene_instances(scene_id);" +
        "CREATE INDEX IF NOT EXISTS idx_instance_template_id ON scene_instances(template_id);" +
        "CREATE INDEX IF NOT EXISTS idx_instance_state ON scene_instances(state);" +
        "CREATE INDEX IF NOT EXISTS idx_participant_user_id ON scene_participants(user_id);" +
        "CREATE INDEX IF NOT EXISTS idx_participant_role_id ON scene_participants(role_id)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneInstanceRepository(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneInstanceRepository(String jdbcUrl, String username, String password) {
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
        log.info("Initializing SqlSceneInstanceRepository at: {}", jdbcUrl);

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
            log.info("SqlSceneInstanceRepository initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneInstanceRepository: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_INSTANCE_TABLE);
            stmt.execute(CREATE_PARTICIPANT_TABLE);
            stmt.execute(CREATE_ACTIVATION_RECORD_TABLE);
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
                log.info("SqlSceneInstanceRepository closed");
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
    public SceneInstance save(SceneInstance instance) {
        if (instance == null || instance.getInstanceId() == null) {
            throw new IllegalArgumentException("Instance and instanceId must not be null");
        }

        String sql = "INSERT OR REPLACE INTO scene_instances " +
            "(instance_id, scene_id, template_id, template_name, skill_id, skill_name, " +
            "state, visibility, category, config, runtime_data, context, tags, metadata, " +
            "created_at, updated_at, activated_at, deactivated_at, created_by, updated_by) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instance.getInstanceId());
            pstmt.setString(2, instance.getSceneId());
            pstmt.setString(3, instance.getTemplateId());
            pstmt.setString(4, instance.getTemplateName());
            pstmt.setString(5, instance.getSkillId());
            pstmt.setString(6, instance.getSkillName());
            pstmt.setString(7, instance.getState() != null ? instance.getState().name() : null);
            pstmt.setString(8, instance.getVisibility());
            pstmt.setString(9, instance.getCategory());
            pstmt.setString(10, serializeMap(instance.getConfig()));
            pstmt.setString(11, serializeMap(instance.getRuntimeData()));
            pstmt.setString(12, serializeMap(instance.getContext()));
            pstmt.setString(13, serializeList(instance.getTags()));
            pstmt.setString(14, serializeStringMap(instance.getMetadata()));
            pstmt.setLong(15, instance.getCreatedAt());
            pstmt.setLong(16, instance.getUpdatedAt());
            pstmt.setLong(17, instance.getActivatedAt());
            pstmt.setLong(18, instance.getDeactivatedAt());
            pstmt.setString(19, instance.getCreatedBy());
            pstmt.setString(20, instance.getUpdatedBy());
            pstmt.executeUpdate();

            saveParticipants(instance.getInstanceId(), instance.getParticipants());
            saveActivationRecords(instance.getInstanceId(), instance.getActivationHistory());

            log.debug("Saved instance: {}", instance.getInstanceId());
            return instance;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save instance: " + e.getMessage(), e);
        }
    }

    private void saveParticipants(String instanceId, Map<String, SceneInstance.ParticipantInfo> participants) throws SQLException {
        String deleteSql = "DELETE FROM scene_participants WHERE instance_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(deleteSql)) {
            pstmt.setString(1, instanceId);
            pstmt.executeUpdate();
        }

        if (participants == null || participants.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO scene_participants " +
            "(instance_id, user_id, user_name, role_id, role_name, status, config, permissions, joined_at, left_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (SceneInstance.ParticipantInfo p : participants.values()) {
            try (PreparedStatement pstmt = getConnection().prepareStatement(insertSql)) {
                pstmt.setString(1, instanceId);
                pstmt.setString(2, p.getUserId());
                pstmt.setString(3, p.getUserName());
                pstmt.setString(4, p.getRoleId());
                pstmt.setString(5, p.getRoleName());
                pstmt.setString(6, p.getStatus() != null ? p.getStatus().name() : null);
                pstmt.setString(7, serializeMap(p.getConfig()));
                pstmt.setString(8, serializeList(p.getPermissions()));
                pstmt.setLong(9, p.getJoinedAt());
                pstmt.setLong(10, p.getLeftAt());
                pstmt.executeUpdate();
            }
        }
    }

    private void saveActivationRecords(String instanceId, List<SceneInstance.ActivationRecord> records) throws SQLException {
        String deleteSql = "DELETE FROM scene_activation_records WHERE instance_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(deleteSql)) {
            pstmt.setString(1, instanceId);
            pstmt.executeUpdate();
        }

        if (records == null || records.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO scene_activation_records " +
            "(instance_id, record_id, user_id, role_id, step_id, step_name, action, success, message, input, output, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (SceneInstance.ActivationRecord r : records) {
            try (PreparedStatement pstmt = getConnection().prepareStatement(insertSql)) {
                pstmt.setString(1, instanceId);
                pstmt.setString(2, r.getRecordId());
                pstmt.setString(3, r.getUserId());
                pstmt.setString(4, r.getRoleId());
                pstmt.setString(5, r.getStepId());
                pstmt.setString(6, r.getStepName());
                pstmt.setString(7, r.getAction());
                pstmt.setInt(8, r.isSuccess() ? 1 : 0);
                pstmt.setString(9, r.getMessage());
                pstmt.setString(10, serializeMap(r.getInput()));
                pstmt.setString(11, serializeMap(r.getOutput()));
                pstmt.setLong(12, r.getTimestamp());
                pstmt.executeUpdate();
            }
        }
    }

    @Override
    public Optional<SceneInstance> findById(String instanceId) {
        if (instanceId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM scene_instances WHERE instance_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToInstance(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find instance: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<SceneInstance> findBySceneId(String sceneId) {
        if (sceneId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_instances WHERE scene_id = ?";
        return queryInstances(sql, sceneId);
    }

    @Override
    public List<SceneInstance> findByTemplateId(String templateId) {
        if (templateId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_instances WHERE template_id = ?";
        return queryInstances(sql, templateId);
    }

    @Override
    public List<SceneInstance> findByUserId(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT DISTINCT si.* FROM scene_instances si " +
            "INNER JOIN scene_participants sp ON si.instance_id = sp.instance_id " +
            "WHERE sp.user_id = ?";
        return queryInstances(sql, userId);
    }

    @Override
    public List<SceneInstance> findByUserIdAndRoleId(String userId, String roleId) {
        if (userId == null || roleId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT DISTINCT si.* FROM scene_instances si " +
            "INNER JOIN scene_participants sp ON si.instance_id = sp.instance_id " +
            "WHERE sp.user_id = ? AND sp.role_id = ?";
        return queryInstancesTwoParams(sql, userId, roleId);
    }

    @Override
    public List<SceneInstance> findByState(String state) {
        if (state == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM scene_instances WHERE state = ?";
        return queryInstances(sql, state);
    }

    @Override
    public List<SceneInstance> findAll() {
        String sql = "SELECT * FROM scene_instances";
        List<SceneInstance> result = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSetToInstance(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all instances: {}", e.getMessage());
        }
        return result;
    }

    private List<SceneInstance> queryInstances(String sql, String param) {
        List<SceneInstance> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, param);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToInstance(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query instances: {}", e.getMessage());
        }
        return result;
    }

    private List<SceneInstance> queryInstancesTwoParams(String sql, String param1, String param2) {
        List<SceneInstance> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, param1);
            pstmt.setString(2, param2);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToInstance(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query instances: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteById(String instanceId) {
        if (instanceId == null) {
            return false;
        }

        String sql = "DELETE FROM scene_instances WHERE instance_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.debug("Deleted instance: {}", instanceId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete instance: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateState(String instanceId, String state) {
        if (instanceId == null || state == null) {
            return false;
        }

        String sql = "UPDATE scene_instances SET state = ?, updated_at = ? WHERE instance_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, state);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, instanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update state: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addParticipant(String instanceId, SceneInstance.ParticipantInfo participant) {
        if (instanceId == null || participant == null) {
            return false;
        }

        String sql = "INSERT OR REPLACE INTO scene_participants " +
            "(instance_id, user_id, user_name, role_id, role_name, status, config, permissions, joined_at, left_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            pstmt.setString(2, participant.getUserId());
            pstmt.setString(3, participant.getUserName());
            pstmt.setString(4, participant.getRoleId());
            pstmt.setString(5, participant.getRoleName());
            pstmt.setString(6, participant.getStatus() != null ? participant.getStatus().name() : null);
            pstmt.setString(7, serializeMap(participant.getConfig()));
            pstmt.setString(8, serializeList(participant.getPermissions()));
            pstmt.setLong(9, participant.getJoinedAt());
            pstmt.setLong(10, participant.getLeftAt());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error("Failed to add participant: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeParticipant(String instanceId, String userId) {
        if (instanceId == null || userId == null) {
            return false;
        }

        String sql = "DELETE FROM scene_participants WHERE instance_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to remove participant: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateConfig(String instanceId, Map<String, Object> config) {
        if (instanceId == null) {
            return false;
        }

        String sql = "UPDATE scene_instances SET config = ?, updated_at = ? WHERE instance_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, serializeMap(config));
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, instanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update config: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addActivationRecord(String instanceId, SceneInstance.ActivationRecord record) {
        if (instanceId == null || record == null) {
            return false;
        }

        String sql = "INSERT INTO scene_activation_records " +
            "(instance_id, record_id, user_id, role_id, step_id, step_name, action, success, message, input, output, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            pstmt.setString(2, record.getRecordId());
            pstmt.setString(3, record.getUserId());
            pstmt.setString(4, record.getRoleId());
            pstmt.setString(5, record.getStepId());
            pstmt.setString(6, record.getStepName());
            pstmt.setString(7, record.getAction());
            pstmt.setInt(8, record.isSuccess() ? 1 : 0);
            pstmt.setString(9, record.getMessage());
            pstmt.setString(10, serializeMap(record.getInput()));
            pstmt.setString(11, serializeMap(record.getOutput()));
            pstmt.setLong(12, record.getTimestamp());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.error("Failed to add activation record: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM scene_instances";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count instances: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public long countBySceneId(String sceneId) {
        if (sceneId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM scene_instances WHERE scene_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count instances by sceneId: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean existsById(String instanceId) {
        if (instanceId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM scene_instances WHERE instance_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instanceId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check instance existence: {}", e.getMessage());
            return false;
        }
    }

    private SceneInstance mapResultSetToInstance(ResultSet rs) throws SQLException {
        SceneInstance instance = new SceneInstance();
        instance.setInstanceId(rs.getString("instance_id"));
        instance.setSceneId(rs.getString("scene_id"));
        instance.setTemplateId(rs.getString("template_id"));
        instance.setTemplateName(rs.getString("template_name"));
        instance.setSkillId(rs.getString("skill_id"));
        instance.setSkillName(rs.getString("skill_name"));

        String stateStr = rs.getString("state");
        if (stateStr != null) {
            try {
                instance.setState(SkillLifecycleState.valueOf(stateStr));
            } catch (IllegalArgumentException e) {
                log.warn("Unknown state: {}", stateStr);
            }
        }

        instance.setVisibility(rs.getString("visibility"));
        instance.setCategory(rs.getString("category"));
        instance.setConfig(deserializeMap(rs.getString("config")));
        instance.setRuntimeData(deserializeMap(rs.getString("runtime_data")));
        instance.setContext(deserializeMap(rs.getString("context")));
        instance.setTags(deserializeList(rs.getString("tags")));
        instance.setMetadata(deserializeStringMap(rs.getString("metadata")));
        instance.setCreatedAt(rs.getLong("created_at"));
        instance.setUpdatedAt(rs.getLong("updated_at"));
        instance.setActivatedAt(rs.getLong("activated_at"));
        instance.setDeactivatedAt(rs.getLong("deactivated_at"));
        instance.setCreatedBy(rs.getString("created_by"));
        instance.setUpdatedBy(rs.getString("updated_by"));

        loadParticipants(instance);
        loadActivationRecords(instance);

        return instance;
    }

    private void loadParticipants(SceneInstance instance) throws SQLException {
        String sql = "SELECT * FROM scene_participants WHERE instance_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instance.getInstanceId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SceneInstance.ParticipantInfo p = new SceneInstance.ParticipantInfo();
                p.setUserId(rs.getString("user_id"));
                p.setUserName(rs.getString("user_name"));
                p.setRoleId(rs.getString("role_id"));
                p.setRoleName(rs.getString("role_name"));

                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    try {
                        p.setStatus(SceneInstance.ParticipantInfo.ParticipantStatus.valueOf(statusStr));
                    } catch (IllegalArgumentException e) {
                        log.warn("Unknown participant status: {}", statusStr);
                    }
                }

                p.setConfig(deserializeMap(rs.getString("config")));
                p.setPermissions(deserializeList(rs.getString("permissions")));
                p.setJoinedAt(rs.getLong("joined_at"));
                p.setLeftAt(rs.getLong("left_at"));

                instance.addParticipant(p);
            }
        }
    }

    private void loadActivationRecords(SceneInstance instance) throws SQLException {
        String sql = "SELECT * FROM scene_activation_records WHERE instance_id = ? ORDER BY timestamp";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, instance.getInstanceId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SceneInstance.ActivationRecord r = new SceneInstance.ActivationRecord();
                r.setRecordId(rs.getString("record_id"));
                r.setUserId(rs.getString("user_id"));
                r.setRoleId(rs.getString("role_id"));
                r.setStepId(rs.getString("step_id"));
                r.setStepName(rs.getString("step_name"));
                r.setAction(rs.getString("action"));
                r.setSuccess(rs.getInt("success") == 1);
                r.setMessage(rs.getString("message"));
                r.setInput(deserializeMap(rs.getString("input")));
                r.setOutput(deserializeMap(rs.getString("output")));
                r.setTimestamp(rs.getLong("timestamp"));

                instance.addActivationRecord(r);
            }
        }
    }

    private String serializeMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.warn("Failed to serialize map: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, Object> deserializeMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize map: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String serializeStringMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.warn("Failed to serialize string map: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, String> deserializeStringMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize string map: {}", e.getMessage());
            return new HashMap<>();
        }
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
