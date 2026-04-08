package net.ooder.scene.skill.knowledge.persistence;

import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.skill.knowledge.KnowledgeBindingManager;
import net.ooder.scene.skill.knowledge.KnowledgeChunk;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL 知识库绑定管理器实现
 *
 * <p>基于 SQLite/MySQL 的知识库绑定持久化存储，支持完整的绑定管理和知识检索。</p>
 *
 * <p><b>版本历史：</b></p>
 * <ul>
 *   <li>3.0.1 - 实现 searchKnowledge 和 crossLayerSearch 向量检索</li>
 *   <li>3.2.0 - 迁移到 net.ooder.scene.skill.knowledge.persistence 包</li>
 *   <li>统一使用 KnowledgeBinding 实体类</li>
 * </ul>
 *
 * <p>支持数据库：</p>
 * <ul>
 *   <li>SQLite: jdbc:sqlite:/path/to/scene.db</li>
 *   <li>MySQL: jdbc:mysql://host:3306/scene_db</li>
 *   <li>H2: jdbc:h2:mem:testdb</li>
 * </ul>
 *
 * @author ooder
 * @version 3.0.1
 * @since 3.1.0
 */
@Component
@ConditionalOnProperty(prefix = "scene.engine.knowledge", name = "storage.type", havingValue = "sql", matchIfMissing = true)
public class SqlKnowledgeBindingManager implements KnowledgeBindingManager {

    private static final Logger log = LoggerFactory.getLogger(SqlKnowledgeBindingManager.class);

    private static final String CREATE_BINDING_TABLE =
        "CREATE TABLE IF NOT EXISTS knowledge_bindings (" +
        "binding_id VARCHAR(255) PRIMARY KEY, " +
        "scene_group_id VARCHAR(255) NOT NULL, " +
        "knowledge_base_id VARCHAR(255) NOT NULL, " +
        "knowledge_base_name VARCHAR(500), " +
        "layer VARCHAR(50), " +
        "priority INTEGER DEFAULT 0, " +
        "bind_time BIGINT, " +
        "bound_by VARCHAR(255), " +
        "status VARCHAR(50), " +
        "config TEXT, " +
        "UNIQUE(scene_group_id, knowledge_base_id)" +
        ")";

    private static final String CREATE_INDEXES =
        "CREATE INDEX IF NOT EXISTS idx_kb_scene_group ON knowledge_bindings(scene_group_id);" +
        "CREATE INDEX IF NOT EXISTS idx_kb_kb_id ON knowledge_bindings(knowledge_base_id);" +
        "CREATE INDEX IF NOT EXISTS idx_kb_priority ON knowledge_bindings(priority);" +
        "CREATE INDEX IF NOT EXISTS idx_kb_layer ON knowledge_bindings(layer);" +
        "CREATE INDEX IF NOT EXISTS idx_kb_status ON knowledge_bindings(status)";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private Connection connection;
    private boolean initialized = false;

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Autowired(required = false)
    private SceneEmbeddingService embeddingService;

    public SqlKnowledgeBindingManager() {
        this("jdbc:sqlite:./data/scene-engine.db", null, null);
    }

    public SqlKnowledgeBindingManager(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlKnowledgeBindingManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void setVectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void setEmbeddingService(SceneEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SqlKnowledgeBindingManager at: {}", jdbcUrl);

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
            log.info("SqlKnowledgeBindingManager initialized successfully");

            if (vectorStore != null && embeddingService != null) {
                log.info("Vector search enabled: vectorStore={}, embeddingService={}",
                        vectorStore.getClass().getSimpleName(),
                        embeddingService.getClass().getSimpleName());
            } else {
                log.warn("Vector search disabled: vectorStore={}, embeddingService={}. " +
                        "searchKnowledge and crossLayerSearch will return empty results.",
                        vectorStore != null ? "available" : "null",
                        embeddingService != null ? "available" : "null");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlKnowledgeBindingManager: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlKnowledgeBindingManager closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_BINDING_TABLE);
            for (String indexSql : CREATE_INDEXES.split(";")) {
                if (!indexSql.trim().isEmpty()) {
                    stmt.execute(indexSql.trim());
                }
            }
            log.debug("Knowledge binding tables created/verified");
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
    public String bind(String sceneGroupId, KnowledgeBinding binding) {
        if (sceneGroupId == null || binding == null || binding.getKnowledgeBaseId() == null) {
            throw new IllegalArgumentException("sceneGroupId and binding.knowledgeBaseId are required");
        }

        binding.setSceneGroupId(sceneGroupId);
        binding.setBindTime(System.currentTimeMillis());

        if (binding.getBindingId() == null) {
            binding.setBindingId(java.util.UUID.randomUUID().toString().replace("-", ""));
        }

        String sql = "INSERT OR REPLACE INTO knowledge_bindings " +
            "(binding_id, scene_group_id, knowledge_base_id, knowledge_base_name, layer, priority, bind_time, bound_by, status, config) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, binding.getBindingId());
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, binding.getKnowledgeBaseId());
            pstmt.setString(4, binding.getKnowledgeBaseName());
            pstmt.setString(5, binding.getLayer());
            pstmt.setInt(6, binding.getPriority());
            pstmt.setLong(7, binding.getBindTime());
            pstmt.setString(8, binding.getBoundBy());
            pstmt.setString(9, binding.getStatus() != null ? binding.getStatus().name() : "ACTIVE");
            pstmt.setString(10, binding.getConfig() != null ? com.alibaba.fastjson2.JSON.toJSONString(binding.getConfig()) : null);
            pstmt.executeUpdate();

            log.info("Knowledge base bound: sceneGroupId={}, kbId={}, layer={}, priority={}",
                    sceneGroupId, binding.getKnowledgeBaseId(), binding.getLayer(), binding.getPriority());

            return binding.getBindingId();
        } catch (SQLException e) {
            log.error("Failed to bind knowledge base: {}", e.getMessage());
            throw new RuntimeException("Failed to bind knowledge base: " + e.getMessage(), e);
        }
    }

    @Override
    public String bind(String sceneGroupId, String kbId, String layer, String boundBy) {
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setKbId(kbId);
        binding.setLayer(layer);
        binding.setBoundBy(boundBy);
        return bind(sceneGroupId, binding);
    }

    @Override
    public boolean unbind(String sceneGroupId, String kbId) {
        if (sceneGroupId == null || kbId == null) {
            return false;
        }

        String sql = "DELETE FROM knowledge_bindings WHERE scene_group_id = ? AND knowledge_base_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, kbId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Knowledge base unbound: sceneGroupId={}, kbId={}", sceneGroupId, kbId);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to unbind knowledge base: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public List<KnowledgeBinding> getBindings(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM knowledge_bindings WHERE scene_group_id = ? ORDER BY priority DESC";
        List<KnowledgeBinding> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToBinding(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to get knowledge bindings: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public KnowledgeBinding getBinding(String sceneGroupId, String kbId) {
        if (sceneGroupId == null || kbId == null) {
            return null;
        }

        String sql = "SELECT * FROM knowledge_bindings WHERE scene_group_id = ? AND knowledge_base_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, kbId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBinding(rs);
            }
        } catch (SQLException e) {
            log.error("Failed to get knowledge binding: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public boolean hasBinding(String sceneGroupId, String kbId) {
        if (sceneGroupId == null || kbId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM knowledge_bindings WHERE scene_group_id = ? AND knowledge_base_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            pstmt.setString(2, kbId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check knowledge binding: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setPriority(String sceneGroupId, String kbId, int priority) {
        if (sceneGroupId == null || kbId == null) {
            return false;
        }

        String sql = "UPDATE knowledge_bindings SET priority = ? WHERE scene_group_id = ? AND knowledge_base_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, priority);
            pstmt.setString(2, sceneGroupId);
            pstmt.setString(3, kbId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                log.info("Binding priority updated: sceneGroupId={}, kbId={}, priority={}",
                        sceneGroupId, kbId, priority);
                return true;
            }
        } catch (SQLException e) {
            log.error("Failed to set binding priority: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public int clearAllBindings(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "DELETE FROM knowledge_bindings WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            int rows = pstmt.executeUpdate();
            log.info("All bindings cleared: sceneGroupId={}, count={}", sceneGroupId, rows);
            return rows;
        } catch (SQLException e) {
            log.error("Failed to clear all bindings: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public List<KnowledgeChunk> searchKnowledge(String sceneGroupId, String query, int topK) {
        if (!isVectorSearchAvailable()) {
            log.debug("Vector search not available for searchKnowledge. sceneGroupId={}", sceneGroupId);
            return new ArrayList<>();
        }

        if (sceneGroupId == null || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            log.debug("searchKnowledge: sceneGroupId={}, query={}, topK={}", sceneGroupId, query, topK);

            float[] queryVector = embeddingService.embed(query);

            List<KnowledgeBinding> bindings = getBindings(sceneGroupId);
            if (bindings.isEmpty()) {
                log.debug("No bindings found for sceneGroupId={}", sceneGroupId);
                return new ArrayList<>();
            }

            List<String> kbIds = bindings.stream()
                    .map(KnowledgeBinding::getKnowledgeBaseId)
                    .collect(Collectors.toList());

            Map<String, Object> filters = new HashMap<>();
            filters.put("sceneGroupId", sceneGroupId);
            filters.put("kbId", kbIds);

            List<SearchResult> searchResults = vectorStore.search(queryVector, topK, filters);

            List<KnowledgeChunk> chunks = new ArrayList<>();
            for (SearchResult sr : searchResults) {
                KnowledgeChunk chunk = convertToKnowledgeChunk(sr);
                chunks.add(chunk);
            }

            log.debug("searchKnowledge returned {} results for sceneGroupId={}", chunks.size(), sceneGroupId);
            return chunks;

        } catch (Exception e) {
            log.error("Failed to search knowledge: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<KnowledgeChunk> crossLayerSearch(String sceneGroupId, String query, List<String> layers, int topK) {
        if (!isVectorSearchAvailable()) {
            log.debug("Vector search not available for crossLayerSearch. sceneGroupId={}", sceneGroupId);
            return new ArrayList<>();
        }

        if (sceneGroupId == null || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        if (layers == null || layers.isEmpty()) {
            return searchKnowledge(sceneGroupId, query, topK);
        }

        try {
            log.debug("crossLayerSearch: sceneGroupId={}, query={}, layers={}, topK={}",
                    sceneGroupId, query, layers, topK);

            float[] queryVector = embeddingService.embed(query);

            Map<String, Object> filters = new HashMap<>();
            filters.put("sceneGroupId", sceneGroupId);
            filters.put("layer", layers);

            List<SearchResult> searchResults = vectorStore.search(queryVector, topK, filters);

            List<KnowledgeChunk> chunks = new ArrayList<>();
            for (SearchResult sr : searchResults) {
                KnowledgeChunk chunk = convertToKnowledgeChunk(sr);
                chunks.add(chunk);
            }

            log.debug("crossLayerSearch returned {} results for sceneGroupId={}, layers={}",
                    chunks.size(), sceneGroupId, layers);
            return chunks;

        } catch (Exception e) {
            log.error("Failed to cross-layer search knowledge: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private boolean isVectorSearchAvailable() {
        return vectorStore != null && embeddingService != null;
    }

    private KnowledgeChunk convertToKnowledgeChunk(SearchResult sr) {
        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setChunkId(sr.getId());
        chunk.setContent(sr.getContent());
        chunk.setScore(sr.getScore());

        Map<String, Object> metadata = sr.getMetadata();
        if (metadata != null) {
            chunk.setKbId(getStringFromMetadata(metadata, "kbId"));
            chunk.setLayer(getStringFromMetadata(metadata, "layer"));
            chunk.setSource(getStringFromMetadata(metadata, "source"));

            Object chunkIndex = metadata.get("chunkIndex");
            if (chunkIndex instanceof Number) {
                chunk.setStartIndex(((Number) chunkIndex).intValue());
            }
        }

        return chunk;
    }

    private String getStringFromMetadata(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public int getBindingCount(String sceneGroupId) {
        if (sceneGroupId == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM knowledge_bindings WHERE scene_group_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sceneGroupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get binding count: {}", e.getMessage());
        }

        return 0;
    }

    @Override
    public long getTotalBindingCount() {
        String sql = "SELECT COUNT(*) FROM knowledge_bindings";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get total binding count: {}", e.getMessage());
        }

        return 0;
    }

    public List<String> getSceneGroupsByKnowledgeBase(String knowledgeBaseId) {
        if (knowledgeBaseId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT scene_group_id FROM knowledge_bindings WHERE knowledge_base_id = ?";
        List<String> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, knowledgeBaseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("scene_group_id"));
            }
        } catch (SQLException e) {
            log.error("Failed to get scene groups by knowledge base: {}", e.getMessage());
        }

        return result;
    }

    private KnowledgeBinding mapResultSetToBinding(ResultSet rs) throws SQLException {
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setBindingId(rs.getString("binding_id"));
        binding.setSceneGroupId(rs.getString("scene_group_id"));
        binding.setKnowledgeBaseId(rs.getString("knowledge_base_id"));
        binding.setKnowledgeBaseName(rs.getString("knowledge_base_name"));
        binding.setLayer(rs.getString("layer"));
        binding.setPriority(rs.getInt("priority"));
        binding.setBindTime(rs.getLong("bind_time"));
        binding.setBoundBy(rs.getString("bound_by"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                binding.setStatus(KnowledgeBinding.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                binding.setStatus(KnowledgeBinding.Status.ACTIVE);
            }
        }

        return binding;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
