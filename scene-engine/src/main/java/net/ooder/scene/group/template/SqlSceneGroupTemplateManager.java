package net.ooder.scene.group.template;

import com.alibaba.fastjson2.JSON;
import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SQL 场景组模板管理器实现
 *
 * <p>基于 SQLite/MySQL 的模板持久化存储，支持模板的 CRUD 和场景组创建。</p>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
@Component
public class SqlSceneGroupTemplateManager implements SceneGroupTemplateManager {

    private static final Logger log = LoggerFactory.getLogger(SqlSceneGroupTemplateManager.class);

    private static final String DEFAULT_JDBC_URL = "jdbc:sqlite:./data/scene-engine.db";

    private static final String CREATE_TEMPLATE_TABLE =
        "CREATE TABLE IF NOT EXISTS scene_group_templates (" +
        "template_id VARCHAR(255) PRIMARY KEY, " +
        "name VARCHAR(500) NOT NULL, " +
        "description TEXT, " +
        "category VARCHAR(100), " +
        "version INTEGER DEFAULT 1, " +
        "usage_count INTEGER DEFAULT 0, " +
        "template_data TEXT NOT NULL, " +
        "create_time TIMESTAMP, " +
        "update_time TIMESTAMP" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_template_category ON scene_group_templates(category);" +
        "CREATE INDEX IF NOT EXISTS idx_template_name ON scene_group_templates(name)";

    private String jdbcUrl;
    private String username;
    private String password;
    private Connection connection;
    private boolean initialized = false;

    private SceneGroupManager sceneGroupManager;

    /**
     * 默认构造函数 - 用于Spring自动装配
     */
    public SqlSceneGroupTemplateManager() {
        this.jdbcUrl = DEFAULT_JDBC_URL;
        this.username = null;
        this.password = null;
    }

    /**
     * 带SceneGroupManager的构造函数
     */
    public SqlSceneGroupTemplateManager(SceneGroupManager sceneGroupManager) {
        this(DEFAULT_JDBC_URL, null, null, sceneGroupManager);
    }

    public SqlSceneGroupTemplateManager(String jdbcUrl, SceneGroupManager sceneGroupManager) {
        this(jdbcUrl, null, null, sceneGroupManager);
    }

    public SqlSceneGroupTemplateManager(String jdbcUrl, String username, String password, SceneGroupManager sceneGroupManager) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.sceneGroupManager = sceneGroupManager;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlSceneGroupTemplateManager at: {}", jdbcUrl);

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
            log.info("SqlSceneGroupTemplateManager initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlSceneGroupTemplateManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlSceneGroupTemplateManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TEMPLATE_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Template tables created/verified");
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
    public SceneTemplate createTemplate(String templateId, String name, String description, String category) {
        if (templateId == null || name == null) {
            throw new IllegalArgumentException("templateId and name are required");
        }

        LocalDateTime now = LocalDateTime.now();
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId(templateId);
        template.setTemplateName(name);
        template.setDescription(description);
        template.setCategory(category);
        template.setVersion("1.0");

        String sql = "INSERT INTO scene_group_templates " +
            "(template_id, name, description, category, version, usage_count, template_data, create_time, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, templateId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, category);
            pstmt.setInt(5, 1);
            pstmt.setInt(6, 0);
            pstmt.setString(7, JSON.toJSONString(template));
            pstmt.setTimestamp(8, Timestamp.valueOf(now));
            pstmt.setTimestamp(9, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            log.info("Template created: templateId={}, name={}, category={}", templateId, name, category);
            return template;

        } catch (SQLException e) {
            log.error("Failed to create template: {}", e.getMessage());
            throw new RuntimeException("Failed to create template", e);
        }
    }

    @Override
    public SceneTemplate getTemplate(String templateId) {
        if (templateId == null) {
            return null;
        }

        String sql = "SELECT * FROM scene_group_templates WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, templateId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTemplate(rs);
            }

        } catch (SQLException e) {
            log.error("Failed to get template: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public List<SceneTemplate> getAllTemplates() {
        String sql = "SELECT * FROM scene_group_templates ORDER BY create_time DESC";
        List<SceneTemplate> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToTemplate(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to get all templates: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public List<SceneTemplate> getTemplatesByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM scene_group_templates WHERE category = ? ORDER BY create_time DESC";
        List<SceneTemplate> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToTemplate(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to get templates by category: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean updateTemplate(String templateId, String name, String description) {
        if (templateId == null) {
            return false;
        }

        String sql = "UPDATE scene_group_templates SET name = ?, description = ?, update_time = ? WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, templateId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Template updated: templateId={}", templateId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to update template: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteTemplate(String templateId) {
        if (templateId == null) {
            return false;
        }

        String sql = "DELETE FROM scene_group_templates WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, templateId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Template deleted: templateId={}", templateId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to delete template: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean setTemplateConfig(String templateId, String key, Object value) {
        SceneTemplate template = getTemplate(templateId);
        if (template == null) {
            return false;
        }

        Map<String, Object> metadata = template.getMetadata();
        if (metadata == null) {
            metadata = new java.util.HashMap<>();
            template.setMetadata(metadata);
        }
        metadata.put(key, value);

        return updateTemplateData(templateId, template);
    }

    @Override
    public Object getTemplateConfig(String templateId, String key) {
        SceneTemplate template = getTemplate(templateId);
        if (template == null || template.getMetadata() == null) {
            return null;
        }
        return template.getMetadata().get(key);
    }

    @Override
    public String createSceneGroupFromTemplate(String sceneGroupId, String templateId, String creatorId, String name) {
        SceneTemplate template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        if (sceneGroupManager == null) {
            throw new IllegalStateException("SceneGroupManager is not available");
        }

        // 使用模板创建场景组
        String newSceneGroupId = sceneGroupId != null ? sceneGroupId : UUID.randomUUID().toString();
        SceneGroup.CreatorType creatorType = SceneGroup.CreatorType.USER;
        if (creatorId != null && creatorId.startsWith("system:")) {
            creatorType = SceneGroup.CreatorType.SYSTEM;
        }
        SceneGroup group = sceneGroupManager.createSceneGroup(
            newSceneGroupId,
            template.getTemplateId(),
            creatorId,
            creatorType
        );
        group.setName(name != null ? name : template.getTemplateName());
        group.setDescription(template.getDescription());

        if (group == null) {
            throw new RuntimeException("Failed to create scene group from template");
        }

        // 增加模板使用次数
        incrementUsageCount(templateId);

        log.info("Scene group created from template: templateId={}, sceneGroupId={}", templateId, group.getSceneGroupId());
        return group.getSceneGroupId();
    }

    @Override
    public int getTemplateUsageCount(String templateId) {
        if (templateId == null) {
            return 0;
        }

        String sql = "SELECT usage_count FROM scene_group_templates WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, templateId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("usage_count");
            }
        } catch (SQLException e) {
            log.error("Failed to get template usage count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public List<String> getAllCategories() {
        String sql = "SELECT DISTINCT category FROM scene_group_templates WHERE category IS NOT NULL ORDER BY category";
        List<String> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            log.error("Failed to get all categories: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public boolean exists(String templateId) {
        return getTemplate(templateId) != null;
    }

    @Override
    public String cloneTemplate(String sourceTemplateId, String newTemplateId, String newName) {
        SceneTemplate source = getTemplate(sourceTemplateId);
        if (source == null) {
            throw new IllegalArgumentException("Source template not found: " + sourceTemplateId);
        }

        // 克隆模板数据
        String json = JSON.toJSONString(source);
        SceneTemplate clone = JSON.parseObject(json, SceneTemplate.class);
        clone.setTemplateId(newTemplateId);
        clone.setTemplateName(newName);
        clone.setVersion("1.0");

        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT INTO scene_group_templates " +
            "(template_id, name, description, category, version, usage_count, template_data, create_time, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newTemplateId);
            pstmt.setString(2, newName);
            pstmt.setString(3, source.getDescription());
            pstmt.setString(4, source.getCategory());
            pstmt.setInt(5, 1);
            pstmt.setInt(6, 0);
            pstmt.setString(7, JSON.toJSONString(clone));
            pstmt.setTimestamp(8, Timestamp.valueOf(now));
            pstmt.setTimestamp(9, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            log.info("Template cloned: sourceId={}, newId={}, newName={}", sourceTemplateId, newTemplateId, newName);
            return newTemplateId;

        } catch (SQLException e) {
            log.error("Failed to clone template: {}", e.getMessage());
            throw new RuntimeException("Failed to clone template", e);
        }
    }

    @Override
    public String exportTemplate(String templateId) {
        SceneTemplate template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }
        return JSON.toJSONString(template);
    }

    @Override
    public String importTemplate(String json, String templateId) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("Template JSON is required");
        }

        SceneTemplate template = JSON.parseObject(json, SceneTemplate.class);
        if (templateId != null) {
            template.setTemplateId(templateId);
        }

        LocalDateTime now = LocalDateTime.now();

        String sql = "INSERT OR REPLACE INTO scene_group_templates " +
            "(template_id, name, description, category, version, usage_count, template_data, create_time, update_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (jdbcUrl.contains("mysql")) {
            sql = "INSERT INTO scene_group_templates " +
                "(template_id, name, description, category, version, usage_count, template_data, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), " +
                "category = VALUES(category), template_data = VALUES(template_data), update_time = VALUES(update_time)";
        }

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, template.getTemplateId());
            pstmt.setString(2, template.getTemplateName());
            pstmt.setString(3, template.getDescription());
            pstmt.setString(4, template.getCategory());
            pstmt.setString(5, template.getVersion());
            pstmt.setInt(6, 0);
            pstmt.setString(7, JSON.toJSONString(template));
            pstmt.setTimestamp(8, Timestamp.valueOf(now));
            pstmt.setTimestamp(9, Timestamp.valueOf(now));

            pstmt.executeUpdate();
            log.info("Template imported: templateId={}", template.getTemplateId());
            return template.getTemplateId();

        } catch (SQLException e) {
            log.error("Failed to import template: {}", e.getMessage());
            throw new RuntimeException("Failed to import template", e);
        }
    }

    private boolean updateTemplateData(String templateId, SceneTemplate template) {
        String sql = "UPDATE scene_group_templates SET template_data = ?, update_time = ? WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, JSON.toJSONString(template));
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, templateId);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            log.error("Failed to update template data: {}", e.getMessage());
            return false;
        }
    }

    private void incrementUsageCount(String templateId) {
        String sql = "UPDATE scene_group_templates SET usage_count = usage_count + 1 WHERE template_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, templateId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to increment usage count: {}", e.getMessage());
        }
    }

    private SceneTemplate mapResultSetToTemplate(ResultSet rs) throws SQLException {
        String templateData = rs.getString("template_data");
        if (templateData != null && !templateData.isEmpty()) {
            try {
                return JSON.parseObject(templateData, SceneTemplate.class);
            } catch (Exception e) {
                log.warn("Failed to parse template data, using fallback: {}", e.getMessage());
            }
        }

        // Fallback to manual mapping
        SceneTemplate template = new SceneTemplate();
        template.setTemplateId(rs.getString("template_id"));
        template.setTemplateName(rs.getString("name"));
        template.setDescription(rs.getString("description"));
        template.setCategory(rs.getString("category"));
        template.setVersion(String.valueOf(rs.getInt("version")));
        return template;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 设置SceneGroupManager - 用于Spring依赖注入
     */
    public void setSceneGroupManager(SceneGroupManager sceneGroupManager) {
        this.sceneGroupManager = sceneGroupManager;
    }
}
