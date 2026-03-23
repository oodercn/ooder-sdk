package net.ooder.scene.skill.knowledge.persistence;

import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.IndexStatus;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存知识库仓库实现
 *
 * <p>基于内存的存储实现，适用于开发测试环境。</p>
 * <p>注意：数据不会持久化，重启后丢失。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class InMemoryKnowledgeRepository implements KnowledgeRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryKnowledgeRepository.class);

    private final Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Document>> documents = new ConcurrentHashMap<>();
    private final Map<String, IndexStatus> indexStatuses = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> permissions = new ConcurrentHashMap<>();

    private boolean initialized = false;

    @Override
    public void initialize() {
        log.info("InMemoryKnowledgeRepository initialized");
        initialized = true;
    }

    @Override
    public void close() {
        knowledgeBases.clear();
        documents.clear();
        indexStatuses.clear();
        permissions.clear();
        initialized = false;
        log.info("InMemoryKnowledgeRepository closed");
    }

    @Override
    public String getStorageType() {
        return RepositoryConfig.TYPE_MEMORY;
    }

    @Override
    public void saveKnowledgeBase(KnowledgeBase kb) {
        if (kb == null || kb.getKbId() == null) {
            throw new IllegalArgumentException("KnowledgeBase or kbId cannot be null");
        }
        knowledgeBases.put(kb.getKbId(), kb);
        documents.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
        permissions.computeIfAbsent(kb.getKbId(), k -> new ConcurrentHashMap<>());
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
            log.debug("Deleted knowledge base: {}", kbId);
        }
    }

    @Override
    public void saveDocument(Document document) {
        if (document == null || document.getKbId() == null || document.getDocId() == null) {
            throw new IllegalArgumentException("Document, kbId or docId cannot be null");
        }
        Map<String, Document> kbDocs = documents.computeIfAbsent(document.getKbId(), k -> new ConcurrentHashMap<>());
        kbDocs.put(document.getDocId(), document);
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
                log.debug("Deleted all documents from kb: {}", kbId);
            }
        }
    }

    @Override
    public void saveIndexStatus(IndexStatus status) {
        if (status != null && status.getKbId() != null) {
            indexStatuses.put(status.getKbId(), status);
            log.debug("Saved index status for kb: {}", status.getKbId());
        }
    }

    @Override
    public IndexStatus findIndexStatus(String kbId) {
        return kbId != null ? indexStatuses.get(kbId) : null;
    }

    @Override
    public void savePermission(String kbId, String userId, String permission) {
        if (kbId == null || userId == null || permission == null) {
            return;
        }
        Map<String, String> kbPerms = permissions.computeIfAbsent(kbId, k -> new ConcurrentHashMap<>());
        kbPerms.put(userId, permission);
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

    @Override
    public int countSceneBindings(String kbId) {
        return 0;
    }

    @Override
    public List<String> findBoundScenes(String kbId) {
        return new ArrayList<>();
    }
}
