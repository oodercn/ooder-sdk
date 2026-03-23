package net.ooder.scene.core.activation.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.core.activation.model.ActivationProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * SQL激活流程存储库实现
 * 
 * <p>基于SQLite/MySQL的激活流程持久化存储</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SqlActivationProcessRepository implements ActivationProcessRepository {

    private static final Logger log = LoggerFactory.getLogger(SqlActivationProcessRepository.class);

    private static final String CREATE_PROCESS_TABLE = 
        "CREATE TABLE IF NOT EXISTS activation_processes (" +
        "process_id VARCHAR(255) PRIMARY KEY, " +
        "activation_id VARCHAR(255), " +
        "template_id VARCHAR(255), " +
        "scene_group_id VARCHAR(255), " +
        "user_id VARCHAR(255), " +
        "role_id VARCHAR(255), " +
        "status VARCHAR(50), " +
        "steps TEXT, " +
        "created_at BIGINT, " +
        "started_at BIGINT, " +
        "completed_at BIGINT, " +
        "error_message TEXT" +
        ")";

    private static final String CREATE_INDEXES = 
        "CREATE INDEX IF NOT EXISTS idx_process_activation_id ON activation_processes(activation_id);" +
        "CREATE INDEX IF NOT EXISTS idx_process_template_id ON activation_processes(template_id);" +
        "CREATE INDEX IF NOT EXISTS idx_process_user_id ON activation_processes(user_id);" +
        "CREATE INDEX IF NOT EXISTS idx_process_status ON activation_processes(status)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private boolean initialized = false;

    public SqlActivationProcessRepository(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlActivationProcessRepository(String jdbcUrl, String username, String password) {
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
        log.info("Initializing SqlActivationProcessRepository at: {}", jdbcUrl);

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
            log.info("SqlActivationProcessRepository initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlActivationProcessRepository: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_PROCESS_TABLE);
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
                log.info("SqlActivationProcessRepository closed");
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
    public ActivationProcess save(ActivationProcess process) {
        if (process == null || process.getProcessId() == null) {
            throw new IllegalArgumentException("Process and processId must not be null");
        }

        String sql = "INSERT OR REPLACE INTO activation_processes " +
            "(process_id, activation_id, template_id, scene_group_id, user_id, role_id, " +
            "status, steps, created_at, started_at, completed_at, error_message) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, process.getProcessId());
            pstmt.setString(2, process.getProcessId());
            pstmt.setString(3, process.getTemplateId());
            pstmt.setString(4, process.getSceneGroupId());
            pstmt.setString(5, process.getUserId());
            pstmt.setString(6, process.getRoleId());
            pstmt.setString(7, process.getStatus() != null ? process.getStatus().name() : null);
            pstmt.setString(8, serializeSteps(process.getSteps()));
            pstmt.setLong(9, process.getCreatedAt());
            pstmt.setLong(10, process.getStartedAt());
            pstmt.setLong(11, process.getCompletedAt());
            pstmt.setString(12, process.getErrorMessage());
            pstmt.executeUpdate();

            log.debug("Saved process: {}", process.getProcessId());
            return process;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save process: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ActivationProcess> findById(String processId) {
        if (processId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM activation_processes WHERE process_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, processId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProcess(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find process: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ActivationProcess> findByActivationId(String activationId) {
        if (activationId == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM activation_processes WHERE activation_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, activationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProcess(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find process by activationId: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ActivationProcess> findByTemplateId(String templateId) {
        if (templateId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM activation_processes WHERE template_id = ?";
        return queryProcesses(sql, templateId);
    }

    @Override
    public List<ActivationProcess> findByUserId(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM activation_processes WHERE user_id = ?";
        return queryProcesses(sql, userId);
    }

    @Override
    public List<ActivationProcess> findByStatus(ActivationProcess.ProcessStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM activation_processes WHERE status = ?";
        return queryProcesses(sql, status.name());
    }

    @Override
    public List<ActivationProcess> findByUserIdAndStatus(String userId, ActivationProcess.ProcessStatus status) {
        if (userId == null || status == null) {
            return Collections.emptyList();
        }

        String sql = "SELECT * FROM activation_processes WHERE user_id = ? AND status = ?";
        List<ActivationProcess> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, status.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToProcess(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query processes: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public List<ActivationProcess> findAll() {
        String sql = "SELECT * FROM activation_processes";
        List<ActivationProcess> result = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSetToProcess(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all processes: {}", e.getMessage());
        }
        return result;
    }

    private List<ActivationProcess> queryProcesses(String sql, String param) {
        List<ActivationProcess> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, param);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToProcess(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query processes: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteById(String processId) {
        if (processId == null) {
            return false;
        }

        String sql = "DELETE FROM activation_processes WHERE process_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, processId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.debug("Deleted process: {}", processId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete process: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateStatus(String processId, ActivationProcess.ProcessStatus status) {
        if (processId == null || status == null) {
            return false;
        }

        String sql = "UPDATE activation_processes SET status = ? WHERE process_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setString(2, processId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Failed to update status: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addStepExecution(String processId, ActivationProcess.StepExecution stepExecution) {
        Optional<ActivationProcess> processOpt = findById(processId);
        if (!processOpt.isPresent()) {
            return false;
        }

        ActivationProcess process = processOpt.get();
        process.addStepExecution(stepExecution);
        save(process);
        return true;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM activation_processes";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count processes: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public long countByStatus(ActivationProcess.ProcessStatus status) {
        if (status == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM activation_processes WHERE status = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to count processes by status: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean existsById(String processId) {
        if (processId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM activation_processes WHERE process_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, processId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check process existence: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int cleanupExpired(long beforeTime) {
        String sql = "DELETE FROM activation_processes WHERE created_at < ? AND status IN ('COMPLETED', 'FAILED', 'CANCELLED')";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, beforeTime);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Cleaned up {} expired processes", rows);
            }
            return rows;
        } catch (SQLException e) {
            log.error("Failed to cleanup expired processes: {}", e.getMessage());
            return 0;
        }
    }

    private ActivationProcess mapResultSetToProcess(ResultSet rs) throws SQLException {
        ActivationProcess process = new ActivationProcess();
        process.setProcessId(rs.getString("process_id"));
        process.setTemplateId(rs.getString("template_id"));
        process.setSceneGroupId(rs.getString("scene_group_id"));
        process.setUserId(rs.getString("user_id"));
        process.setRoleId(rs.getString("role_id"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                process.setStatus(ActivationProcess.ProcessStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                log.warn("Unknown status: {}", statusStr);
            }
        }

        process.setSteps(deserializeSteps(rs.getString("steps")));
        process.setCreatedAt(rs.getLong("created_at"));
        process.setStartedAt(rs.getLong("started_at"));
        process.setCompletedAt(rs.getLong("completed_at"));
        process.setErrorMessage(rs.getString("error_message"));

        return process;
    }

    private String serializeSteps(List<ActivationProcess.StepExecution> steps) {
        if (steps == null || steps.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(steps);
        } catch (Exception e) {
            log.warn("Failed to serialize steps: {}", e.getMessage());
            return null;
        }
    }

    private List<ActivationProcess.StepExecution> deserializeSteps(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ActivationProcess.StepExecution>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize steps: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
