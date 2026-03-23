package net.ooder.scene.skill.knowledge.persistence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.IndexStatus;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class SqlKnowledgeRepository implements KnowledgeRepository {

    private static final Logger log = LoggerFactory.getLogger(SqlKnowledgeRepository.class);

    private static final String CREATE_KB_TABLE = 
        "CREATE TABLE IF NOT EXISTS knowledge_bases (" +
        "kb_id VARCHAR(255) PRIMARY KEY, " +
        "name VARCHAR(500) NOT NULL, " +
        "description TEXT, " +
        "owner_id VARCHAR(255), " +
        "embedding_model VARCHAR(255), " +
        "created_at BIGINT, " +
        "updated_at BIGINT, " +
        "metadata TEXT" +
        ")";

    private static final String CREATE_DOC_TABLE = 
        "CREATE TABLE IF NOT EXISTS documents (" +
        "doc_id VARCHAR(255) NOT NULL, " +
        "kb_id VARCHAR(255) NOT NULL, " +
        "title VARCHAR(1000), " +
        "content TEXT, " +
        "source VARCHAR(1000), " +
        "doc_type VARCHAR(100), " +
        "created_at BIGINT, " +
        "updated_at BIGINT, " +
        "metadata TEXT, " +
        "PRIMARY KEY (doc_id, kb_id), " +
        "FOREIGN KEY (kb_id) REFERENCES knowledge_bases(kb_id) ON DELETE CASCADE" +
        ")";

    private static final String CREATE_INDEX_STATUS_TABLE = 
        "CREATE TABLE IF NOT EXISTS index_status (" +
        "kb_id VARCHAR(255) PRIMARY KEY, " +
        "status VARCHAR(50), " +
        "total_docs INTEGER, " +
        "indexed_docs INTEGER, " +
        "last_index_time BIGINT, " +
        "error_message TEXT, " +
        "FOREIGN KEY (kb_id) REFERENCES knowledge_bases(kb_id) ON DELETE CASCADE" +
        ")";

    private static final String CREATE_PERMISSION_TABLE = 
        "CREATE TABLE IF NOT EXISTS permissions (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "kb_id VARCHAR(255) NOT NULL, " +
        "user_id VARCHAR(255) NOT NULL, " +
        "permission VARCHAR(50) NOT NULL, " +
        "UNIQUE(kb_id, user_id), " +
        "FOREIGN KEY (kb_id) REFERENCES knowledge_bases(kb_id) ON DELETE CASCADE" +
        ")";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private boolean initialized = false;

    public SqlKnowledgeRepository(String jdbcUrl) {
        this(jdbcUrl, null, null);
    }

    public SqlKnowledgeRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void initialize() {
        log.info("Initializing SqlKnowledgeRepository at: {}", jdbcUrl);

        try {
            if (jdbcUrl.contains("sqlite")) {
                Class.forName("org.sqlite.JDBC");
            } else if (jdbcUrl.contains("h2")) {
                Class.forName("org.h2.Driver");
            }

            if (username != null && password != null) {
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            } else {
                connection = DriverManager.getConnection(jdbcUrl);
            }

            createTables();
            initialized = true;
            log.info("SqlKnowledgeRepository initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SqlKnowledgeRepository: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_KB_TABLE);
            stmt.execute(CREATE_DOC_TABLE);
            stmt.execute(CREATE_INDEX_STATUS_TABLE);
            stmt.execute(CREATE_PERMISSION_TABLE);
            log.debug("Database tables created/verified");
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("SqlKnowledgeRepository closed");
            } catch (SQLException e) {
                log.error("Error closing database connection: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    @Override
    public String getStorageType() {
        return RepositoryConfig.TYPE_SQL;
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
    public void saveKnowledgeBase(KnowledgeBase kb) {
        if (kb == null || kb.getKbId() == null) {
            throw new IllegalArgumentException("KnowledgeBase or kbId cannot be null");
        }

        String sql = "INSERT OR REPLACE INTO knowledge_bases " +
            "(kb_id, name, description, owner_id, embedding_model, created_at, updated_at, metadata) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kb.getKbId());
            pstmt.setString(2, kb.getName());
            pstmt.setString(3, kb.getDescription());
            pstmt.setString(4, kb.getOwnerId());
            pstmt.setString(5, kb.getEmbeddingModel());
            pstmt.setLong(6, kb.getCreatedAt());
            pstmt.setLong(7, kb.getUpdatedAt());
            pstmt.setString(8, serializeMap(kb.getMetadata()));
            pstmt.executeUpdate();
            log.debug("Saved knowledge base: {}", kb.getKbId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save knowledge base: " + e.getMessage(), e);
        }
    }

    @Override
    public KnowledgeBase findKnowledgeBaseById(String kbId) {
        if (kbId == null) {
            return null;
        }

        String sql = "SELECT * FROM knowledge_bases WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToKnowledgeBase(rs);
            }
            return null;
        } catch (SQLException e) {
            log.error("Failed to find knowledge base: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<KnowledgeBase> findAllKnowledgeBases() {
        String sql = "SELECT * FROM knowledge_bases";
        List<KnowledgeBase> result = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapResultSetToKnowledgeBase(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find all knowledge bases: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public List<KnowledgeBase> findKnowledgeBasesByOwner(String ownerId) {
        if (ownerId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM knowledge_bases WHERE owner_id = ?";
        List<KnowledgeBase> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, ownerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToKnowledgeBase(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find knowledge bases by owner: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public boolean existsKnowledgeBase(String kbId) {
        if (kbId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM knowledge_bases WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            log.error("Failed to check knowledge base existence: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteKnowledgeBase(String kbId) {
        if (kbId == null) {
            return;
        }

        String sql = "DELETE FROM knowledge_bases WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.executeUpdate();
            log.debug("Deleted knowledge base: {}", kbId);
        } catch (SQLException e) {
            log.error("Failed to delete knowledge base: {}", e.getMessage());
        }
    }

    @Override
    public void saveDocument(Document document) {
        if (document == null || document.getKbId() == null || document.getDocId() == null) {
            throw new IllegalArgumentException("Document, kbId or docId cannot be null");
        }

        String sql = "INSERT OR REPLACE INTO documents " +
            "(doc_id, kb_id, title, content, source, doc_type, created_at, updated_at, metadata) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, document.getDocId());
            pstmt.setString(2, document.getKbId());
            pstmt.setString(3, document.getTitle());
            pstmt.setString(4, document.getContent());
            pstmt.setString(5, document.getSource());
            pstmt.setString(6, document.getType());
            pstmt.setLong(7, document.getCreatedAt());
            pstmt.setLong(8, document.getUpdatedAt());
            pstmt.setString(9, serializeMap(document.getMetadata()));
            pstmt.executeUpdate();
            log.debug("Saved document: {} in kb: {}", document.getDocId(), document.getKbId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save document: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveDocuments(List<Document> documentList) {
        if (documentList != null) {
            documentList.forEach(this::saveDocument);
        }
    }

    @Override
    public Document findDocumentById(String kbId, String docId) {
        if (kbId == null || docId == null) {
            return null;
        }

        String sql = "SELECT * FROM documents WHERE kb_id = ? AND doc_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.setString(2, docId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDocument(rs);
            }
            return null;
        } catch (SQLException e) {
            log.error("Failed to find document: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Document> findDocumentsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM documents WHERE kb_id = ?";
        List<Document> result = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find documents by knowledge base: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public void deleteDocument(String kbId, String docId) {
        if (kbId == null || docId == null) {
            return;
        }

        String sql = "DELETE FROM documents WHERE kb_id = ? AND doc_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.setString(2, docId);
            pstmt.executeUpdate();
            log.debug("Deleted document: {} from kb: {}", docId, kbId);
        } catch (SQLException e) {
            log.error("Failed to delete document: {}", e.getMessage());
        }
    }

    @Override
    public void deleteDocumentsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return;
        }

        String sql = "DELETE FROM documents WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.executeUpdate();
            log.debug("Deleted all documents from kb: {}", kbId);
        } catch (SQLException e) {
            log.error("Failed to delete documents by knowledge base: {}", e.getMessage());
        }
    }

    @Override
    public void saveIndexStatus(IndexStatus status) {
        if (status == null || status.getKbId() == null) {
            return;
        }

        String sql = "INSERT OR REPLACE INTO index_status " +
            "(kb_id, status, total_docs, indexed_docs, last_index_time, error_message) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status.getKbId());
            pstmt.setString(2, status.getStatus());
            pstmt.setInt(3, status.getTotalDocuments());
            pstmt.setInt(4, status.getIndexedDocuments());
            pstmt.setLong(5, status.getLastUpdated());
            pstmt.setString(6, status.getErrorMessage());
            pstmt.executeUpdate();
            log.debug("Saved index status for kb: {}", status.getKbId());
        } catch (SQLException e) {
            log.error("Failed to save index status: {}", e.getMessage());
        }
    }

    @Override
    public IndexStatus findIndexStatus(String kbId) {
        if (kbId == null) {
            return null;
        }

        String sql = "SELECT * FROM index_status WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                IndexStatus status = new IndexStatus();
                status.setKbId(rs.getString("kb_id"));
                status.setStatus(rs.getString("status"));
                status.setTotalDocuments(rs.getInt("total_docs"));
                status.setIndexedDocuments(rs.getInt("indexed_docs"));
                status.setLastUpdated(rs.getLong("last_index_time"));
                status.setErrorMessage(rs.getString("error_message"));
                return status;
            }
            return null;
        } catch (SQLException e) {
            log.error("Failed to find index status: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void savePermission(String kbId, String userId, String permission) {
        if (kbId == null || userId == null || permission == null) {
            return;
        }

        String sql = "INSERT OR REPLACE INTO permissions (kb_id, user_id, permission) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.setString(2, userId);
            pstmt.setString(3, permission);
            pstmt.executeUpdate();
            log.debug("Saved permission: kb={}, user={}, perm={}", kbId, userId, permission);
        } catch (SQLException e) {
            log.error("Failed to save permission: {}", e.getMessage());
        }
    }

    @Override
    public String findPermission(String kbId, String userId) {
        if (kbId == null || userId == null) {
            return null;
        }

        String sql = "SELECT permission FROM permissions WHERE kb_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.setString(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("permission");
            }
            return null;
        } catch (SQLException e) {
            log.error("Failed to find permission: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, String> findPermissionsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return new HashMap<>();
        }

        String sql = "SELECT user_id, permission FROM permissions WHERE kb_id = ?";
        Map<String, String> result = new HashMap<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("user_id"), rs.getString("permission"));
            }
        } catch (SQLException e) {
            log.error("Failed to find permissions by knowledge base: {}", e.getMessage());
        }
        return result;
    }

    @Override
    public void deletePermission(String kbId, String userId) {
        if (kbId == null || userId == null) {
            return;
        }

        String sql = "DELETE FROM permissions WHERE kb_id = ? AND user_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
            log.debug("Deleted permission: kb={}, user={}", kbId, userId);
        } catch (SQLException e) {
            log.error("Failed to delete permission: {}", e.getMessage());
        }
    }

    @Override
    public void deletePermissionsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return;
        }

        String sql = "DELETE FROM permissions WHERE kb_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            pstmt.executeUpdate();
            log.debug("Deleted all permissions for kb: {}", kbId);
        } catch (SQLException e) {
            log.error("Failed to delete permissions by knowledge base: {}", e.getMessage());
        }
    }

    private KnowledgeBase mapResultSetToKnowledgeBase(ResultSet rs) throws SQLException {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setKbId(rs.getString("kb_id"));
        kb.setName(rs.getString("name"));
        kb.setDescription(rs.getString("description"));
        kb.setOwnerId(rs.getString("owner_id"));
        kb.setEmbeddingModel(rs.getString("embedding_model"));
        kb.setCreatedAt(rs.getLong("created_at"));
        kb.setUpdatedAt(rs.getLong("updated_at"));
        kb.setMetadata(deserializeMap(rs.getString("metadata")));
        return kb;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();
        doc.setDocId(rs.getString("doc_id"));
        doc.setKbId(rs.getString("kb_id"));
        doc.setTitle(rs.getString("title"));
        doc.setContent(rs.getString("content"));
        doc.setSource(rs.getString("source"));
        doc.setType(rs.getString("doc_type"));
        doc.setCreatedAt(rs.getLong("created_at"));
        doc.setUpdatedAt(rs.getLong("updated_at"));
        doc.setMetadata(deserializeMap(rs.getString("metadata")));
        return doc;
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

    @SuppressWarnings("unchecked")
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

    public int getKnowledgeBaseCount() {
        String sql = "SELECT COUNT(*) FROM knowledge_bases";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get knowledge base count: {}", e.getMessage());
        }
        return 0;
    }

    public int getDocumentCount(String kbId) {
        String sql = "SELECT COUNT(*) FROM documents WHERE kb_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, kbId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Failed to get document count: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public int countSceneBindings(String kbId) {
        return 0;
    }

    @Override
    public List<String> findBoundScenes(String kbId) {
        return new ArrayList<>();
    }
}
