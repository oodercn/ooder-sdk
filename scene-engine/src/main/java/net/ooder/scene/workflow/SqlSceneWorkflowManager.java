package net.ooder.scene.workflow;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SQL 场景组工作流管理器实现
 *
 * <p>基于 SQLite/MySQL 的工作流持久化存储，支持工作流定义、执行和监控。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneWorkflowManager implements SceneWorkflowManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneWorkflowManager.class);

    private static final String CREATE_WORKFLOW_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_workflows (" +
        "workflow_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "name VARCHAR(500) NOT NULL, " +
        "description TEXT, " +
        "status VARCHAR(50), " +
        "trigger_type VARCHAR(50), " +
        "trigger_config TEXT, " +
        "trigger_enabled BOOLEAN DEFAULT FALSE, " +
        "variables TEXT, " +
        "create_time TIMESTAMP, " +
        "update_time TIMESTAMP, " +
        "creator_id VARCHAR(255), " +
        "version INTEGER DEFAULT 1, " +
        "auto_start BOOLEAN DEFAULT FALSE" +
        ")";

    private static final String CREATE_WORKFLOW_STEP_TABLE =
        "CREATE TABLE IF NOT EXISTS workflow_steps (" +
        "step_id VARCHAR(255) PRIMARY KEY, " +
        "workflow_id VARCHAR(255) NOT NULL, " +
        "name VARCHAR(500), " +
        "description TEXT, " +
        "sequence INTEGER NOT NULL, " +
        "step_type VARCHAR(100), " +
        "config TEXT, " +
        "UNIQUE(workflow_id, sequence)" +
        ")";

    private static final String CREATE_WORKFLOW_EXECUTION_TABLE =
        "CREATE TABLE IF NOT EXISTS workflow_executions (" +
        "execution_id VARCHAR(255) PRIMARY KEY, " +
        "workflow_id VARCHAR(255) NOT NULL, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "status VARCHAR(50), " +
        "start_time TIMESTAMP, " +
        "end_time TIMESTAMP, " +
        "trigger_type VARCHAR(50), " +
        "trigger_source VARCHAR(255), " +
        "input_data TEXT, " +
        "output_data TEXT, " +
        "result TEXT, " +
        "error_message TEXT, " +
        "current_step_index INTEGER DEFAULT 0, " +
        "executor_id VARCHAR(255)" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_wf_scene_group ON scene_workflows(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_wf_status ON scene_workflows(status);" +
        "CREATE INDEX IF NOT EXISTS idx_step_workflow ON workflow_steps(workflow_id);" +
        "CREATE INDEX IF NOT EXISTS idx_exec_workflow ON workflow_executions(workflow_id);" +
        "CREATE INDEX IF NOT EXISTS idx_exec_scene_group ON workflow_executions(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_exec_status ON workflow_executions(status);" +
        "CREATE INDEX IF NOT EXISTS idx_exec_start_time ON workflow_executions(start_time)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    public SqlSceneWorkflowManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlSceneWorkflowManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlSceneWorkflowManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneWorkflowManager at: {}", jdbcUrl);

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
            log.info("SqlSceneWorkflowManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneWorkflowManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneWorkflowManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_WORKFLOW_TABLE);
            stmt.execute(CREATE_WORKFLOW_STEP_TABLE);
            stmt.execute(CREATE_WORKFLOW_EXECUTION_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Workflow tables created/verified");
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
    public SceneWorkflow createWorkflow(String sceneGroupId, String name, String description) {
        if (sceneGroupId == null || name == null) {
            throw new IllegalArgumentException("sceneGroupId and name are required");
        }

        String workflowId = generateWorkflowId();
        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO scene_workflows " +
            "(workflow_id, scene_group_id, name, description, status, create_time, update_time, version) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, name);
            pstmt.setString(4, description);
            pstmt.setString(5, WorkflowStatus.DRAFT.name());
            pstmt.setTimestamp(6, Timestamp.valueOf(now));
            pstmt.setTimestamp(7, Timestamp.valueOf(now));
            pstmt.setInt(8, 1);
            pstmt.executeUpdate();

            SceneWorkflow workflow = new SceneWorkflow();
            workflow.setWorkflowId(workflowId);
            workflow.setSceneGroupId(sceneGroupId);
            workflow.setName(name);
            workflow.setDescription(description);
            workflow.setStatus(WorkflowStatus.DRAFT);
            workflow.setCreateTime(now);
            workflow.setUpdateTime(now);
            workflow.setVersion(1);

            log.info("Workflow created: sceneGroupId={}, workflowId={}, name={}",
                    sceneGroupId, workflowId, name);
            return workflow;

        } catch (SQLException e) {
            log.error("Failed to create workflow: {}", e.getMessage());
            throw new RuntimeException("Failed to create workflow", e);
        }
    }

    @Override
    public SceneWorkflow getWorkflow(String workflowId) {
        if (workflowId == null) {
            return null;
        }

        String sql = "SELECT * FROM scene_workflows WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                SceneWorkflow workflow = mapResultSetToWorkflow(rs);
                // 加载步骤
                workflow.setSteps(loadWorkflowSteps(workflowId));
                return workflow;
            }
        } catch (SQLException e) {
            log.error("Failed to get workflow: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public List<SceneWorkflow> listWorkflows(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM scene_workflows WHERE scene_group_id = ? ORDER BY create_time DESC";
        List<SceneWorkflow> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SceneWorkflow workflow = mapResultSetToWorkflow(rs);
                workflow.setSteps(loadWorkflowSteps(workflow.getWorkflowId()));
                result.add(workflow);
            }
        } catch (SQLException e) {
            log.error("Failed to list workflows: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<SceneWorkflow> listActiveWorkflows(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM scene_workflows WHERE scene_group_id = ? AND status = ? ORDER BY create_time DESC";
        List<SceneWorkflow> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, WorkflowStatus.ACTIVE.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SceneWorkflow workflow = mapResultSetToWorkflow(rs);
                workflow.setSteps(loadWorkflowSteps(workflow.getWorkflowId()));
                result.add(workflow);
            }
        } catch (SQLException e) {
            log.error("Failed to list active workflows: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean updateWorkflow(SceneWorkflow workflow) {
        if (workflow == null || workflow.getWorkflowId() == null) {
            return false;
        }

        // 自动递增版本号
        int newVersion = workflow.getVersion() + 1;
        workflow.setVersion(newVersion);

        String sql = "UPDATE scene_workflows SET " +
            "name = ?, description = ?, status = ?, trigger_type = ?, trigger_config = ?, " +
            "trigger_enabled = ?, variables = ?, update_time = ?, version = ?, auto_start = ? " +
            "WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflow.getName());
            pstmt.setString(2, workflow.getDescription());
            pstmt.setString(3, workflow.getStatus() != null ? workflow.getStatus().name() : null);
            pstmt.setString(4, workflow.getTriggerType() != null ? workflow.getTriggerType().name() : null);
            pstmt.setString(5, workflow.getTriggerConfig());
            pstmt.setBoolean(6, workflow.isTriggerEnabled());
            pstmt.setString(7, workflow.getVariables() != null ? JSON.toJSONString(workflow.getVariables()) : null);
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(9, newVersion);
            pstmt.setBoolean(10, workflow.isAutoStart());
            pstmt.setString(11, workflow.getWorkflowId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow updated: workflowId={}, newVersion={}", workflow.getWorkflowId(), newVersion);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update workflow: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteWorkflow(String workflowId) {
        if (workflowId == null) {
            return false;
        }

        // 检查是否有正在执行的记录
        if (hasRunningExecutions(workflowId)) {
            log.warn("Cannot delete workflow with running executions: workflowId={}", workflowId);
            throw new IllegalStateException("Cannot delete workflow with running executions. Please cancel them first.");
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 先删除步骤
                String deleteStepsSql = "DELETE FROM workflow_steps WHERE workflow_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteStepsSql)) {
                    pstmt.setString(1, workflowId);
                    pstmt.executeUpdate();
                }

                // 删除执行记录（只删除已完成的）
                String deleteExecSql = "DELETE FROM workflow_executions WHERE workflow_id = ? AND status != ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteExecSql)) {
                    pstmt.setString(1, workflowId);
                    pstmt.setString(2, WorkflowStatus.RUNNING.name());
                    pstmt.executeUpdate();
                }

                // 删除工作流
                String deleteWorkflowSql = "DELETE FROM scene_workflows WHERE workflow_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteWorkflowSql)) {
                    pstmt.setString(1, workflowId);
                    int rows = pstmt.executeUpdate();
                    conn.commit();

                    if (rows > 0) {
                        log.info("Workflow deleted: workflowId={}", workflowId);
                        return true;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("Failed to delete workflow: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean activateWorkflow(String workflowId) {
        return updateWorkflowStatus(workflowId, WorkflowStatus.ACTIVE);
    }

    @Override
    public boolean pauseWorkflow(String workflowId) {
        return updateWorkflowStatus(workflowId, WorkflowStatus.PAUSED);
    }

    @Override
    public boolean archiveWorkflow(String workflowId) {
        return updateWorkflowStatus(workflowId, WorkflowStatus.ARCHIVED);
    }

    private boolean updateWorkflowStatus(String workflowId, WorkflowStatus status) {
        if (workflowId == null || status == null) {
            return false;
        }

        String sql = "UPDATE scene_workflows SET status = ?, update_time = ? WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, workflowId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow status updated: workflowId={}, status={}", workflowId, status);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update workflow status: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean addWorkflowStep(String workflowId, WorkflowStep step) {
        if (workflowId == null || step == null || step.getStepId() == null) {
            return false;
        }

        // 获取当前最大序号
        int nextSequence = getNextStepSequence(workflowId);
        if (step.getSequence() <= 0) {
            step.setSequence(nextSequence);
        }

        String sql = "INSERT INTO workflow_steps " +
            "(step_id, workflow_id, name, description, sequence, step_type, config) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, step.getStepId());
            pstmt.setString(2, workflowId);
            pstmt.setString(3, step.getName());
            pstmt.setString(4, step.getDescription());
            pstmt.setInt(5, step.getSequence());
            pstmt.setString(6, step.getStepType());
            pstmt.setString(7, step.getConfig() != null ? JSON.toJSONString(step.getConfig()) : null);

            pstmt.executeUpdate();
            log.info("Workflow step added: workflowId={}, stepId={}, sequence={}",
                    workflowId, step.getStepId(), step.getSequence());
            return true;

        } catch (SQLException e) {
            log.error("Failed to add workflow step: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateWorkflowStep(String workflowId, WorkflowStep step) {
        if (workflowId == null || step == null || step.getStepId() == null) {
            return false;
        }

        String sql = "UPDATE workflow_steps SET " +
            "name = ?, description = ?, sequence = ?, step_type = ?, config = ? " +
            "WHERE step_id = ? AND workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, step.getName());
            pstmt.setString(2, step.getDescription());
            pstmt.setInt(3, step.getSequence());
            pstmt.setString(4, step.getStepType());
            pstmt.setString(5, step.getConfig() != null ? JSON.toJSONString(step.getConfig()) : null);
            pstmt.setString(6, step.getStepId());
            pstmt.setString(7, workflowId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow step updated: workflowId={}, stepId={}", workflowId, step.getStepId());
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update workflow step: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteWorkflowStep(String workflowId, String stepId) {
        if (workflowId == null || stepId == null) {
            return false;
        }

        String sql = "DELETE FROM workflow_steps WHERE workflow_id = ? AND step_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, stepId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // 重新排序剩余步骤
                reorderStepsAfterDeletion(workflowId);
                log.info("Workflow step deleted: workflowId={}, stepId={}", workflowId, stepId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete workflow step: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean reorderSteps(String workflowId, List<String> stepIds) {
        if (workflowId == null || stepIds == null || stepIds.isEmpty()) {
            return false;
        }

        String sql = "UPDATE workflow_steps SET sequence = ? WHERE step_id = ? AND workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < stepIds.size(); i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setString(2, stepIds.get(i));
                pstmt.setString(3, workflowId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            log.info("Workflow steps reordered: workflowId={}, count={}", workflowId, stepIds.size());
            return true;
        } catch (SQLException e) {
            log.error("Failed to reorder workflow steps: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public WorkflowExecution executeWorkflow(String workflowId, Map<String, Object> inputData) {
        return executeWorkflow(workflowId, inputData, null);
    }

    @Override
    public WorkflowExecution executeWorkflow(String workflowId, Map<String, Object> inputData, String executorId) {
        if (workflowId == null) {
            throw new IllegalArgumentException("workflowId is required");
        }

        SceneWorkflow workflow = getWorkflow(workflowId);
        if (workflow == null) {
            throw new IllegalArgumentException("Workflow not found: " + workflowId);
        }

        if (!workflow.isExecutable()) {
            throw new IllegalStateException("Workflow is not executable: " + workflowId);
        }

        // 并发控制：检查是否已有正在执行的实例（如果工作流不允许并发执行）
        if (!workflow.isAutoStart() && hasRunningExecutions(workflowId)) {
            log.warn("Workflow is already running: workflowId={}", workflowId);
            throw new IllegalStateException("Workflow is already running. Please wait for it to complete or cancel it.");
        }

        String executionId = generateExecutionId();
        LocalDateTime now = LocalDateTime.now();

        WorkflowExecution execution = new WorkflowExecution();
        execution.setExecutionId(executionId);
        execution.setWorkflowId(workflowId);
        execution.setSceneGroupId(workflow.getSceneGroupId());
        execution.setStatus(WorkflowStatus.RUNNING);
        execution.setStartTime(now);
        execution.setTriggerType(workflow.getTriggerType() != null ? workflow.getTriggerType().name() : "MANUAL");
        execution.setInputData(inputData != null ? inputData : new HashMap<>());
        execution.setExecutorId(executorId);
        execution.setCurrentStepIndex(0);

        // 保存执行记录
        saveExecution(execution);

        log.info("Workflow execution started: workflowId={}, executionId={}", workflowId, executionId);
        return execution;
    }

    @Override
    public WorkflowExecution getExecution(String executionId) {
        if (executionId == null) {
            return null;
        }

        String sql = "SELECT * FROM workflow_executions WHERE execution_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, executionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToExecution(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to get execution: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public List<WorkflowExecution> listExecutions(String workflowId) {
        if (workflowId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM workflow_executions WHERE workflow_id = ? ORDER BY start_time DESC";
        List<WorkflowExecution> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToExecution(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to list executions: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<WorkflowExecution> listExecutionsBySceneGroup(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM workflow_executions WHERE scene_group_id = ? ORDER BY start_time DESC";
        List<WorkflowExecution> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToExecution(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to list executions by scene group: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean cancelExecution(String executionId) {
        if (executionId == null) {
            return false;
        }

        String sql = "UPDATE workflow_executions SET status = ?, end_time = ? WHERE execution_id = ? AND status = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, WorkflowStatus.CANCELLED.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, executionId);
            pstmt.setString(4, WorkflowStatus.RUNNING.name());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow execution cancelled: executionId={}", executionId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to cancel execution: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public WorkflowExecution retryExecution(String executionId) {
        WorkflowExecution originalExecution = getExecution(executionId);
        if (originalExecution == null) {
            throw new IllegalArgumentException("Execution not found: " + executionId);
        }

        if (originalExecution.getStatus() != WorkflowStatus.ERROR &&
            originalExecution.getStatus() != WorkflowStatus.CANCELLED) {
            throw new IllegalStateException("Only failed or cancelled executions can be retried");
        }

        return executeWorkflow(
            originalExecution.getWorkflowId(),
            originalExecution.getInputData(),
            originalExecution.getExecutorId()
        );
    }

    @Override
    public boolean setTrigger(String workflowId, WorkflowTriggerType triggerType, String triggerConfig) {
        if (workflowId == null) {
            return false;
        }

        // 设置触发器时默认启用
        String sql = "UPDATE scene_workflows SET trigger_type = ?, trigger_config = ?, trigger_enabled = ?, update_time = ? WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, triggerType != null ? triggerType.name() : null);
            pstmt.setString(2, triggerConfig);
            pstmt.setBoolean(3, triggerType != null); // 设置触发器时默认启用
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(5, workflowId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow trigger set: workflowId={}, triggerType={}, enabled={}", workflowId, triggerType, triggerType != null);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to set workflow trigger: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean setTriggerEnabled(String workflowId, boolean enabled) {
        if (workflowId == null) {
            return false;
        }

        String sql = "UPDATE scene_workflows SET trigger_enabled = ?, update_time = ? WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setBoolean(1, enabled);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, workflowId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow trigger enabled: workflowId={}, enabled={}", workflowId, enabled);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to set trigger enabled: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public int getWorkflowCount(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM scene_workflows WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get workflow count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public int getExecutionCount(String workflowId) {
        if (workflowId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM workflow_executions WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get execution count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public int getSuccessExecutionCount(String workflowId) {
        if (workflowId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM workflow_executions WHERE workflow_id = ? AND status = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, WorkflowStatus.COMPLETED.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get success execution count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public long getAverageExecutionDuration(String workflowId) {
        if (workflowId == null) {
            return 0;
        }

        String sql;
        if (jdbcUrl.contains("sqlite")) {
            sql = "SELECT AVG(CAST((julianday(end_time) - julianday(start_time)) * 86400000 AS INTEGER)) " +
                "FROM workflow_executions WHERE workflow_id = ? AND end_time IS NOT NULL";
        } else if (jdbcUrl.contains("h2")) {
            sql = "SELECT AVG(DATEDIFF('MILLISECOND', start_time, end_time)) " +
                "FROM workflow_executions WHERE workflow_id = ? AND end_time IS NOT NULL";
        } else {
            sql = "SELECT AVG(TIMESTAMPDIFF(MICROSECOND, start_time, end_time) / 1000) " +
                "FROM workflow_executions WHERE workflow_id = ? AND end_time IS NOT NULL";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get average execution duration: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean exists(String workflowId) {
        return getWorkflow(workflowId) != null;
    }

    // ========== 执行状态管理 ==========

    /**
     * 更新执行状态为完成
     *
     * @param executionId 执行ID
     * @param outputData 输出数据
     * @param result 执行结果
     * @return 是否成功
     */
    public boolean completeExecution(String executionId, Map<String, Object> outputData, String result) {
        if (executionId == null) {
            return false;
        }

        String sql = "UPDATE workflow_executions SET status = ?, end_time = ?, output_data = ?, result = ? " +
            "WHERE execution_id = ? AND status = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, WorkflowStatus.COMPLETED.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, outputData != null ? JSON.toJSONString(outputData) : null);
            pstmt.setString(4, result);
            pstmt.setString(5, executionId);
            pstmt.setString(6, WorkflowStatus.RUNNING.name());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow execution completed: executionId={}", executionId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to complete execution: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 更新执行状态为失败
     *
     * @param executionId 执行ID
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    public boolean failExecution(String executionId, String errorMessage) {
        if (executionId == null) {
            return false;
        }

        String sql = "UPDATE workflow_executions SET status = ?, end_time = ?, error_message = ? " +
            "WHERE execution_id = ? AND status = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, WorkflowStatus.ERROR.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, errorMessage);
            pstmt.setString(4, executionId);
            pstmt.setString(5, WorkflowStatus.RUNNING.name());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Workflow execution failed: executionId={}, error={}", executionId, errorMessage);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update execution status: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 更新当前步骤索引
     *
     * @param executionId 执行ID
     * @param stepIndex 步骤索引
     * @return 是否成功
     */
    public boolean updateExecutionStepIndex(String executionId, int stepIndex) {
        if (executionId == null) {
            return false;
        }

        String sql = "UPDATE workflow_executions SET current_step_index = ? WHERE execution_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, stepIndex);
            pstmt.setString(2, executionId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.debug("Execution step index updated: executionId={}, stepIndex={}", executionId, stepIndex);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update execution step index: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 检查是否有正在执行的记录
     *
     * @param workflowId 工作流ID
     * @return 是否有正在执行的记录
     */
    public boolean hasRunningExecutions(String workflowId) {
        if (workflowId == null) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM workflow_executions WHERE workflow_id = ? AND status = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            pstmt.setString(2, WorkflowStatus.RUNNING.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.error("Failed to check running executions: {}", e.getMessage());
        }

        return false;
    }

    // ===== 私有方法 =====

    private String generateWorkflowId() {
        return "wf-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateExecutionId() {
        return "exec-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private SceneWorkflow mapResultSetToWorkflow(ResultSet rs) throws SQLException {
        SceneWorkflow workflow = new SceneWorkflow();
        workflow.setWorkflowId(rs.getString("workflow_id"));
        workflow.setSceneGroupId(rs.getString("scene_group_id"));
        workflow.setName(rs.getString("name"));
        workflow.setDescription(rs.getString("description"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            workflow.setStatus(WorkflowStatus.valueOf(statusStr));
        }

        String triggerTypeStr = rs.getString("trigger_type");
        if (triggerTypeStr != null) {
            workflow.setTriggerType(WorkflowTriggerType.valueOf(triggerTypeStr));
        }

        workflow.setTriggerConfig(rs.getString("trigger_config"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            workflow.setCreateTime(createTime.toLocalDateTime());
        }

        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            workflow.setUpdateTime(updateTime.toLocalDateTime());
        }

        workflow.setCreatorId(rs.getString("creator_id"));
        workflow.setVersion(rs.getInt("version"));
        workflow.setAutoStart(rs.getBoolean("auto_start"));

        String variablesJson = rs.getString("variables");
        if (variablesJson != null && !variablesJson.isEmpty()) {
            try {
                workflow.setVariables(JSON.parseObject(variablesJson, Map.class));
            } catch (Exception e) {
                workflow.setVariables(new HashMap<>());
            }
        }

        return workflow;
    }

    private List<WorkflowStep> loadWorkflowSteps(String workflowId) {
        String sql = "SELECT * FROM workflow_steps WHERE workflow_id = ? ORDER BY sequence";
        List<WorkflowStep> steps = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                steps.add(mapResultSetToStep(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to load workflow steps: {}", e.getMessage());
        }

        return steps;
    }

    private WorkflowStep mapResultSetToStep(ResultSet rs) throws SQLException {
        WorkflowStep step = new WorkflowStep();
        step.setStepId(rs.getString("step_id"));
        step.setName(rs.getString("name"));
        step.setDescription(rs.getString("description"));
        step.setSequence(rs.getInt("sequence"));
        step.setStepType(rs.getString("step_type"));

        String configJson = rs.getString("config");
        if (configJson != null && !configJson.isEmpty()) {
            try {
                step.setConfig(JSON.parseObject(configJson, Map.class));
            } catch (Exception e) {
                step.setConfig(new HashMap<>());
            }
        }

        return step;
    }

    private WorkflowExecution mapResultSetToExecution(ResultSet rs) throws SQLException {
        WorkflowExecution execution = new WorkflowExecution();
        execution.setExecutionId(rs.getString("execution_id"));
        execution.setWorkflowId(rs.getString("workflow_id"));
        execution.setSceneGroupId(rs.getString("scene_group_id"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            execution.setStatus(WorkflowStatus.valueOf(statusStr));
        }

        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            execution.setStartTime(startTime.toLocalDateTime());
        }

        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            execution.setEndTime(endTime.toLocalDateTime());
        }

        execution.setTriggerType(rs.getString("trigger_type"));
        execution.setTriggerSource(rs.getString("trigger_source"));

        String inputDataJson = rs.getString("input_data");
        if (inputDataJson != null && !inputDataJson.isEmpty()) {
            try {
                execution.setInputData(JSON.parseObject(inputDataJson, Map.class));
            } catch (Exception e) {
                execution.setInputData(new HashMap<>());
            }
        }

        String outputDataJson = rs.getString("output_data");
        if (outputDataJson != null && !outputDataJson.isEmpty()) {
            try {
                execution.setOutputData(JSON.parseObject(outputDataJson, Map.class));
            } catch (Exception e) {
                execution.setOutputData(new HashMap<>());
            }
        }

        execution.setResult(rs.getString("result"));
        execution.setErrorMessage(rs.getString("error_message"));
        execution.setCurrentStepIndex(rs.getInt("current_step_index"));
        execution.setExecutorId(rs.getString("executor_id"));

        return execution;
    }

    private void saveExecution(WorkflowExecution execution) {
        String sql = "INSERT INTO workflow_executions " +
            "(execution_id, workflow_id, scene_group_id, status, start_time, trigger_type, " +
            "input_data, executor_id, current_step_index) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, execution.getExecutionId());
            pstmt.setString(2, execution.getWorkflowId());
            pstmt.setString(3, execution.getSceneGroupId());
            pstmt.setString(4, execution.getStatus().name());
            pstmt.setTimestamp(5, Timestamp.valueOf(execution.getStartTime()));
            pstmt.setString(6, execution.getTriggerType());
            pstmt.setString(7, execution.getInputData() != null ? JSON.toJSONString(execution.getInputData()) : null);
            pstmt.setString(8, execution.getExecutorId());
            pstmt.setInt(9, execution.getCurrentStepIndex());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to save execution: {}", e.getMessage());
            throw new RuntimeException("Failed to save execution", e);
        }
    }

    private int getNextStepSequence(String workflowId) {
        String sql = "SELECT MAX(sequence) FROM workflow_steps WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            log.error("Failed to get next step sequence: {}", e.getMessage());
        }

        return 1;
    }

    private void reorderStepsAfterDeletion(String workflowId) {
        String sql = "SELECT step_id FROM workflow_steps WHERE workflow_id = ? ORDER BY sequence";
        List<String> stepIds = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stepIds.add(rs.getString("step_id"));
            }
        } catch (SQLException e) {
            log.error("Failed to get steps for reordering: {}", e.getMessage());
            return;
        }

        // 清理无效依赖：更新所有步骤的 config，移除对已删除步骤的依赖
        cleanupInvalidDependencies(workflowId, stepIds);

        reorderSteps(workflowId, stepIds);
    }

    /**
     * 清理步骤中对已删除步骤的依赖引用
     *
     * @param workflowId 工作流ID
     * @param validStepIds 有效的步骤ID列表
     */
    private void cleanupInvalidDependencies(String workflowId, List<String> validStepIds) {
        String sql = "SELECT step_id, config FROM workflow_steps WHERE workflow_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, workflowId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String stepId = rs.getString("step_id");
                String configJson = rs.getString("config");

                if (configJson != null && !configJson.isEmpty()) {
                    try {
                        Map<String, Object> config = JSON.parseObject(configJson, Map.class);
                        if (config != null && config.containsKey("dependsOn")) {
                            Object dependsOnObj = config.get("dependsOn");
                            if (dependsOnObj instanceof List) {
                                @SuppressWarnings("unchecked")
                                List<String> dependsOn = (List<String>) dependsOnObj;
                                // 过滤掉无效的依赖
                                List<String> validDepends = dependsOn.stream()
                                    .filter(validStepIds::contains)
                                    .toList();

                                if (validDepends.size() != dependsOn.size()) {
                                    config.put("dependsOn", validDepends);
                                    // 更新步骤配置
                                    updateStepConfig(stepId, config);
                                    log.debug("Cleaned up dependencies for step: {}, removed: {}",
                                        stepId, dependsOn.size() - validDepends.size());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse config for step: {}", stepId, e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to cleanup invalid dependencies: {}", e.getMessage());
        }
    }

    /**
     * 更新步骤配置
     *
     * @param stepId 步骤ID
     * @param config 新配置
     */
    private void updateStepConfig(String stepId, Map<String, Object> config) {
        String sql = "UPDATE workflow_steps SET config = ? WHERE step_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, JSON.toJSONString(config));
            pstmt.setString(2, stepId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update step config: {}", e.getMessage());
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
