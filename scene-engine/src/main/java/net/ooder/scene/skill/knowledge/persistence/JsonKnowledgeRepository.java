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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JSON文件知识库仓库实现
 *
 * <p>基于JSON文件的持久化存储，适用于生产环境。</p>
 * <p>支持自动保存和数据恢复。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class JsonKnowledgeRepository implements KnowledgeRepository {

    private static final Logger log = LoggerFactory.getLogger(JsonKnowledgeRepository.class);

    private static final String KNOWLEDGE_BASES_FILE = "knowledge_bases.json";
    private static final String DOCUMENTS_DIR = "documents";
    private static final String INDEX_STATUS_FILE = "index_status.json";
    private static final String PERMISSIONS_FILE = "permissions.json";

    private final String basePath;
    private final ObjectMapper objectMapper;
    private final boolean autoSave;
    private final long saveIntervalMs;

    private final Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Document>> documents = new ConcurrentHashMap<>();
    private final Map<String, IndexStatus> indexStatuses = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> permissions = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;
    private volatile boolean dirty = false;
    private boolean initialized = false;

    public JsonKnowledgeRepository(String basePath) {
        this(basePath, true, 5000);
    }

    public JsonKnowledgeRepository(String basePath, boolean autoSave, long saveIntervalMs) {
        this.basePath = basePath;
        this.autoSave = autoSave;
        this.saveIntervalMs = saveIntervalMs;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void initialize() {
        log.info("Initializing JsonKnowledgeRepository at: {}", basePath);

        try {
            Path baseDir = Paths.get(basePath);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                log.info("Created base directory: {}", basePath);
            }

            Path docsDir = baseDir.resolve(DOCUMENTS_DIR);
            if (!Files.exists(docsDir)) {
                Files.createDirectories(docsDir);
            }

            loadData();

            if (autoSave) {
                startAutoSave();
            }

            initialized = true;
            log.info("JsonKnowledgeRepository initialized. Loaded {} knowledge bases", knowledgeBases.size());

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize JsonKnowledgeRepository: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (dirty) {
            saveAllData();
        }

        initialized = false;
        log.info("JsonKnowledgeRepository closed");
    }

    @Override
    public String getStorageType() {
        return RepositoryConfig.TYPE_JSON;
    }

    private void loadData() {
        loadKnowledgeBases();
        loadDocuments();
        loadIndexStatuses();
        loadPermissions();
    }

    private void loadKnowledgeBases() {
        File file = Paths.get(basePath, KNOWLEDGE_BASES_FILE).toFile();
        if (!file.exists()) {
            return;
        }

        try {
            List<KnowledgeBase> list = objectMapper.readValue(file, new TypeReference<List<KnowledgeBase>>() {});
            for (KnowledgeBase kb : list) {
                knowledgeBases.put(kb.getKbId(), kb);
                documents.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
                permissions.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
            }
            log.debug("Loaded {} knowledge bases", knowledgeBases.size());
        } catch (IOException e) {
            log.error("Failed to load knowledge bases: {}", e.getMessage(), e);
        }
    }

    private void loadDocuments() {
        Path docsDir = Paths.get(basePath, DOCUMENTS_DIR);
        if (!Files.exists(docsDir)) {
            return;
        }

        try {
            Files.list(docsDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(this::loadDocumentFile);
        } catch (IOException e) {
            log.error("Failed to list document files: {}", e.getMessage(), e);
        }
    }

    private void loadDocumentFile(Path file) {
        try {
            String kbId = file.getFileName().toString().replace(".json", "");
            List<Document> docList = objectMapper.readValue(file.toFile(), new TypeReference<List<Document>>() {});
            Map<String, Document> kbDocs = documents.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>());
            for (Document doc : docList) {
                kbDocs.put(doc.getDocId(), doc);
            }
            log.debug("Loaded {} documents for kb: {}", docList.size(), kbId);
        } catch (IOException e) {
            log.error("Failed to load documents from {}: {}", file, e.getMessage());
        }
    }

    private void loadIndexStatuses() {
        File file = Paths.get(basePath, INDEX_STATUS_FILE).toFile();
        if (!file.exists()) {
            return;
        }

        try {
            List<IndexStatus> list = objectMapper.readValue(file, new TypeReference<List<IndexStatus>>() {});
            for (IndexStatus status : list) {
                indexStatuses.put(status.getKbId(), status);
            }
            log.debug("Loaded {} index statuses", indexStatuses.size());
        } catch (IOException e) {
            log.error("Failed to load index statuses: {}", e.getMessage(), e);
        }
    }

    private void loadPermissions() {
        File file = Paths.get(basePath, PERMISSIONS_FILE).toFile();
        if (!file.exists()) {
            return;
        }

        try {
            Map<String, Map<String, String>> loaded = objectMapper.readValue(file, new TypeReference<Map<String, Map<String, String>>>() {});
            permissions.putAll(loaded);
            log.debug("Loaded permissions for {} knowledge bases", permissions.size());
        } catch (IOException e) {
            log.error("Failed to load permissions: {}", e.getMessage(), e);
        }
    }

    private synchronized void saveAllData() {
        try {
            saveKnowledgeBases();
            saveAllDocuments();
            saveIndexStatuses();
            savePermissions();
            dirty = false;
            log.debug("All data saved successfully");
        } catch (Exception e) {
            log.error("Failed to save all data: {}", e.getMessage(), e);
        }
    }

    private void saveKnowledgeBases() throws IOException {
        File file = Paths.get(basePath, KNOWLEDGE_BASES_FILE).toFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<>(knowledgeBases.values()));
    }

    private void saveAllDocuments() throws IOException {
        for (Map.Entry<String, Map<String, Document>> entry : documents.entrySet()) {
            saveDocumentsForKb(entry.getKey(), entry.getValue());
        }
    }

    private void saveDocumentsForKb(String kbId, Map<String, Document> kbDocs) throws IOException {
        if (kbDocs.isEmpty()) {
            return;
        }
        File file = Paths.get(basePath, DOCUMENTS_DIR, kbId + ".json").toFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<>(kbDocs.values()));
    }

    private void saveIndexStatuses() throws IOException {
        File file = Paths.get(basePath, INDEX_STATUS_FILE).toFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<>(indexStatuses.values()));
    }

    private void savePermissions() throws IOException {
        File file = Paths.get(basePath, PERMISSIONS_FILE).toFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, permissions);
    }

    private void startAutoSave() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "json-repo-autosave");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() -> {
            if (dirty) {
                saveAllData();
            }
        }, saveIntervalMs, saveIntervalMs, TimeUnit.MILLISECONDS);
        log.debug("Auto-save started with interval: {}ms", saveIntervalMs);
    }

    private void markDirty() {
        dirty = true;
    }

    // ========== 知识库操作 ==========

    @Override
    public void saveKnowledgeBase(KnowledgeBase kb) {
        if (kb == null || kb.getKbId() == null) {
            throw new IllegalArgumentException("KnowledgeBase or kbId cannot be null");
        }
        knowledgeBases.put(kb.getKbId(), kb);
        documents.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
        permissions.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
        markDirty();
        log.debug("Saved knowledge base: {}", kb.getKbId());
    }

    @Override
    public KnowledgeBase findKnowledgeBaseById(String kbId) {
        return kbId != null ? knowledgeBases.get(kbId) : null;
    }

    @Override
    public List<KnowledgeBase> findAllKnowledgeBases() {
        return new ArrayList<>(knowledgeBases.values());
    }

    @Override
    public List<KnowledgeBase> findKnowledgeBasesByOwner(String ownerId) {
        if (ownerId == null) {
            return new ArrayList<>();
        }
        return knowledgeBases.values().stream()
                .filter(kb -> ownerId.equals(kb.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsKnowledgeBase(String kbId) {
        return kbId != null && knowledgeBases.containsKey(kbId);
    }

    @Override
    public void deleteKnowledgeBase(String kbId) {
        if (kbId != null) {
            knowledgeBases.remove(kbId);
            documents.remove(kbId);
            indexStatuses.remove(kbId);
            permissions.remove(kbId);

            File docFile = Paths.get(basePath, DOCUMENTS_DIR, kbId + ".json").toFile();
            if (docFile.exists()) {
                docFile.delete();
            }

            markDirty();
            log.debug("Deleted knowledge base: {}", kbId);
        }
    }

    // ========== 文档操作 ==========

    @Override
    public void saveDocument(Document document) {
        if (document == null || document.getKbId() == null || document.getDocId() == null) {
            throw new IllegalArgumentException("Document, kbId or docId cannot be null");
        }
        Map<String, Document> kbDocs = documents.computeIfAbsent(document.getKbId(), k -> new ConcurrentHashMap<>());
        kbDocs.put(document.getDocId(), document);
        markDirty();
        log.debug("Saved document: {} in kb: {}", document.getDocId(), document.getKbId());
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
        Map<String, Document> kbDocs = documents.get(kbId);
        return kbDocs != null ? kbDocs.get(docId) : null;
    }

    @Override
    public List<Document> findDocumentsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return new ArrayList<>();
        }
        Map<String, Document> kbDocs = documents.get(kbId);
        return kbDocs != null ? new ArrayList<>(kbDocs.values()) : new ArrayList<>();
    }

    @Override
    public void deleteDocument(String kbId, String docId) {
        if (kbId != null && docId != null) {
            Map<String, Document> kbDocs = documents.get(kbId);
            if (kbDocs != null) {
                kbDocs.remove(docId);
                markDirty();
                log.debug("Deleted document: {} from kb: {}", docId, kbId);
            }
        }
    }

    @Override
    public void deleteDocumentsByKnowledgeBase(String kbId) {
        if (kbId != null) {
            Map<String, Document> kbDocs = documents.get(kbId);
            if (kbDocs != null) {
                kbDocs.clear();
                markDirty();
                log.debug("Deleted all documents from kb: {}", kbId);
            }
        }
    }

    // ========== 索引状态操作 ==========

    @Override
    public void saveIndexStatus(IndexStatus status) {
        if (status != null && status.getKbId() != null) {
            indexStatuses.put(status.getKbId(), status);
            markDirty();
            log.debug("Saved index status for kb: {}", status.getKbId());
        }
    }

    @Override
    public IndexStatus findIndexStatus(String kbId) {
        return kbId != null ? indexStatuses.get(kbId) : null;
    }

    // ========== 权限操作 ==========

    @Override
    public void savePermission(String kbId, String userId, String permission) {
        if (kbId == null || userId == null || permission == null) {
            return;
        }
        Map<String, String> kbPerms = permissions.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>());
        kbPerms.put(userId, permission);
        markDirty();
        log.debug("Saved permission: kb={}, user={}, perm={}", kbId, userId, permission);
    }

    @Override
    public String findPermission(String kbId, String userId) {
        if (kbId == null || userId == null) {
            return null;
        }
        Map<String, String> kbPerms = permissions.get(kbId);
        return kbPerms != null ? kbPerms.get(userId) : null;
    }

    @Override
    public Map<String, String> findPermissionsByKnowledgeBase(String kbId) {
        if (kbId == null) {
            return new HashMap<>();
        }
        Map<String, String> kbPerms = permissions.get(kbId);
        return kbPerms != null ? new HashMap<>(kbPerms) : new HashMap<>();
    }

    @Override
    public void deletePermission(String kbId, String userId) {
        if (kbId != null && userId != null) {
            Map<String, String> kbPerms = permissions.get(kbId);
            if (kbPerms != null) {
                kbPerms.remove(userId);
                markDirty();
                log.debug("Deleted permission: kb={}, user={}", kbId, userId);
            }
        }
    }

    @Override
    public void deletePermissionsByKnowledgeBase(String kbId) {
        if (kbId != null) {
            Map<String, String> kbPerms = permissions.get(kbId);
            if (kbPerms != null) {
                kbPerms.clear();
                markDirty();
                log.debug("Deleted all permissions for kb: {}", kbId);
            }
        }
    }

    public int getKnowledgeBaseCount() {
        return knowledgeBases.size();
    }

    public int getDocumentCount(String kbId) {
        Map<String, Document> kbDocs = documents.get(kbId);
        return kbDocs != null ? kbDocs.size() : 0;
    }

    public void forceSave() {
        saveAllData();
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
