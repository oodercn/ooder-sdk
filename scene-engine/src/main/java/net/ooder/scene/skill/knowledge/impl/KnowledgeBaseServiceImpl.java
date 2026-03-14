package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 知识库管理服务实现
 *
 * <p>提供知识库的完整生命周期管理。</p>
 *
 * <p>架构层次：知识增强层 - 知识库管理实现</p>
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseServiceImpl.class);
    
    private final Map<String, KnowledgeBase> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Document>> documents = new ConcurrentHashMap<>();
    private final Map<String, IndexStatus> indexStatuses = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> permissions = new ConcurrentHashMap<>();
    
    private final DocumentChunker chunker;
    private final SceneEmbeddingService embeddingService;
    private final VectorStore vectorStore;
    
    public KnowledgeBaseServiceImpl(DocumentChunker chunker, 
                                     SceneEmbeddingService embeddingService,
                                     VectorStore vectorStore) {
        this.chunker = chunker;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }
    
    // ========== 知识库管理 ==========
    
    @Override
    public KnowledgeBase create(KnowledgeBaseCreateRequest request) {
        log.info("Creating knowledge base: {}", request.getName());
        
        String kbId = generateId("kb");
        KnowledgeBase kb = new KnowledgeBase(kbId, request.getName(), request.getOwnerId());
        kb.setDescription(request.getDescription());
        kb.setVisibility(request.getVisibility() != null ? request.getVisibility() : KnowledgeBase.VISIBILITY_PRIVATE);
        kb.setEmbeddingModel(request.getEmbeddingModel());
        kb.setChunkSize(request.getChunkSize() > 0 ? request.getChunkSize() : 500);
        kb.setChunkOverlap(request.getChunkOverlap() > 0 ? request.getChunkOverlap() : 50);
        kb.setTags(request.getTags());
        kb.setMetadata(request.getMetadata());
        
        knowledgeBases.put(kbId, kb);
        documents.put(kbId, new ConcurrentHashMap<>());
        permissions.put(kbId, new ConcurrentHashMap<>());
        indexStatuses.put(kbId, new IndexStatus(kbId));
        
        // 授予所有者管理员权限
        grantPermission(kbId, request.getOwnerId(), "admin");
        
        log.info("Knowledge base created: {}", kbId);
        return kb;
    }
    
    @Override
    public boolean exists(String kbId) {
        return knowledgeBases.containsKey(kbId);
    }
    
    @Override
    public KnowledgeBase get(String kbId) {
        return knowledgeBases.get(kbId);
    }
    
    @Override
    public KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request) {
        KnowledgeBase kb = knowledgeBases.get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        if (request.getName() != null) {
            kb.setName(request.getName());
        }
        if (request.getDescription() != null) {
            kb.setDescription(request.getDescription());
        }
        if (request.getVisibility() != null) {
            kb.setVisibility(request.getVisibility());
        }
        if (request.getChunkSize() != null) {
            kb.setChunkSize(request.getChunkSize());
        }
        if (request.getChunkOverlap() != null) {
            kb.setChunkOverlap(request.getChunkOverlap());
        }
        if (request.getTags() != null) {
            kb.setTags(request.getTags());
        }
        if (request.getMetadata() != null) {
            kb.setMetadata(request.getMetadata());
        }
        
        kb.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Knowledge base updated: {}", kbId);
        return kb;
    }
    
    @Override
    public void delete(String kbId) {
        log.info("Deleting knowledge base: {}", kbId);
        
        KnowledgeBase kb = knowledgeBases.remove(kbId);
        if (kb != null) {
            // 删除所有文档
            Map<String, Document> kbDocs = documents.remove(kbId);
            if (kbDocs != null) {
                for (String docId : kbDocs.keySet()) {
                    vectorStore.deleteByMetadata("kbId", kbId);
                }
            }
            
            // 删除权限
            permissions.remove(kbId);
            indexStatuses.remove(kbId);
            
            // 从向量库删除
            vectorStore.deleteByMetadata("kbId", kbId);
        }
        
        log.info("Knowledge base deleted: {}", kbId);
    }
    
    @Override
    public List<KnowledgeBase> listByOwner(String ownerId) {
        List<KnowledgeBase> result = new ArrayList<>();
        for (KnowledgeBase kb : knowledgeBases.values()) {
            if (ownerId.equals(kb.getOwnerId())) {
                result.add(kb);
            }
        }
        return result;
    }
    
    @Override
    public List<KnowledgeBase> listPublic() {
        List<KnowledgeBase> result = new ArrayList<>();
        for (KnowledgeBase kb : knowledgeBases.values()) {
            if (kb.isPublic()) {
                result.add(kb);
            }
        }
        return result;
    }
    
    // ========== 文档管理 ==========
    
    @Override
    public Document addDocument(String kbId, DocumentCreateRequest request) {
        log.info("Adding document to knowledge base: {}", kbId);
        
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        String docId = generateId("doc");
        Document doc = new Document(docId, kbId, request.getTitle(), request.getContent());
        doc.setSource(request.getSource());
        doc.setSourceUrl(request.getSourceUrl());
        doc.setFilePath(request.getFilePath());
        doc.setMimeType(request.getMimeType());
        doc.setFileSize(request.getFileSize());
        doc.setTags(request.getTags());
        doc.setMetadata(request.getMetadata());
        
        documents.get(kbId).put(docId, doc);
        kb.incrementDocumentCount();
        kb.setTotalSize(kb.getTotalSize() + doc.getFileSize());
        
        // 异步索引文档
        indexDocumentAsync(kb, doc);
        
        log.info("Document added: {}", docId);
        return doc;
    }
    
    @Override
    public List<Document> addDocuments(String kbId, List<DocumentCreateRequest> requests) {
        List<Document> docs = new ArrayList<>();
        for (DocumentCreateRequest request : requests) {
            docs.add(addDocument(kbId, request));
        }
        return docs;
    }
    
    @Override
    public Document getDocument(String kbId, String docId) {
        Map<String, Document> kbDocs = documents.get(kbId);
        return kbDocs != null ? kbDocs.get(docId) : null;
    }
    
    @Override
    public void deleteDocument(String kbId, String docId) {
        log.info("Deleting document: {} from kb: {}", docId, kbId);
        
        Map<String, Document> kbDocs = documents.get(kbId);
        if (kbDocs != null) {
            Document doc = kbDocs.remove(docId);
            if (doc != null) {
                KnowledgeBase kb = get(kbId);
                if (kb != null) {
                    kb.decrementDocumentCount();
                    kb.setTotalSize(kb.getTotalSize() - doc.getFileSize());
                }
                
                // 从向量库删除
                vectorStore.deleteByMetadata("docId", docId);
            }
        }
    }
    
    @Override
    public List<Document> listDocuments(String kbId) {
        Map<String, Document> kbDocs = documents.get(kbId);
        return kbDocs != null ? new ArrayList<>(kbDocs.values()) : new ArrayList<>();
    }
    
    @Override
    public List<KnowledgeSearchResult> search(String kbId, KnowledgeSearchRequest request) {
        log.debug("Searching knowledge base: {}", kbId);
        
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        // 1. 向量化查询
        float[] queryVector = embeddingService.embed(request.getQuery());
        
        // 2. 向量检索
        List<SearchResult> vectorResults = vectorStore.search(
            queryVector,
            request.getTopK(),
            buildFilters(request)
        );
        
        // 3. 构建结果
        List<KnowledgeSearchResult> results = new ArrayList<>();
        for (SearchResult vr : vectorResults) {
            if (vr.getScore() < request.getThreshold()) {
                continue;
            }
            
            String docId = (String) vr.getMetadata().get("docId");
            Document doc = getDocument(kbId, docId);
            
            if (doc != null) {
                KnowledgeSearchResult result = new KnowledgeSearchResult(doc, vr.getContent(), vr.getScore());
                result.setChunkId((String) vr.getMetadata().get("chunkId"));
                results.add(result);
            }
        }
        
        log.debug("Search returned {} results", results.size());
        return results;
    }
    
    // ========== 索引管理 ==========
    
    @Override
    public void rebuildIndex(String kbId) {
        log.info("Rebuilding index for knowledge base: {}", kbId);
        
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        IndexStatus status = indexStatuses.get(kbId);
        List<Document> docs = listDocuments(kbId);
        
        status.start(docs.size(), kb.getTotalSize());
        
        try {
            // 删除旧索引
            vectorStore.deleteByMetadata("kbId", kbId);
            
            int totalChunks = 0;
            int indexedDocs = 0;
            
            for (Document doc : docs) {
                doc.setStatus(Document.STATUS_PROCESSING);
                
                List<DocumentChunk> chunks = indexDocument(kb, doc);
                totalChunks += chunks.size();
                indexedDocs++;
                
                doc.markIndexed(chunks.size());
                status.updateProgress(indexedDocs, totalChunks, totalChunks);
            }
            
            status.complete();
            kb.setIndexStatus(IndexStatus.INDEXED);
            
            log.info("Index rebuilt for kb: {}, total chunks: {}", kbId, totalChunks);
            
        } catch (Exception e) {
            status.fail(e.getMessage());
            log.error("Failed to rebuild index for kb: {}", kbId, e);
            throw new RuntimeException("Failed to rebuild index: " + e.getMessage(), e);
        }
    }
    
    @Override
    public IndexStatus getIndexStatus(String kbId) {
        return indexStatuses.get(kbId);
    }
    
    // ========== 权限管理 ==========
    
    @Override
    public boolean hasPermission(String kbId, String userId, String permission) {
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            return false;
        }
        
        // 所有者拥有所有权限
        if (userId.equals(kb.getOwnerId())) {
            return true;
        }
        
        // 公开知识库，所有人有读权限
        if (kb.isPublic() && "read".equals(permission)) {
            return true;
        }
        
        // 检查显式权限
        Map<String, String> kbPerms = permissions.get(kbId);
        if (kbPerms != null) {
            String userPerm = kbPerms.get(userId);
            if (userPerm != null) {
                return comparePermissions(userPerm, permission) >= 0;
            }
        }
        
        return false;
    }
    
    @Override
    public void grantPermission(String kbId, String userId, String permission) {
        Map<String, String> kbPerms = permissions.get(kbId);
        if (kbPerms != null) {
            kbPerms.put(userId, permission);
            log.info("Permission granted: kb={}, user={}, perm={}", kbId, userId, permission);
        }
    }
    
    @Override
    public void revokePermission(String kbId, String userId) {
        Map<String, String> kbPerms = permissions.get(kbId);
        if (kbPerms != null) {
            kbPerms.remove(userId);
            log.info("Permission revoked: kb={}, user={}", kbId, userId);
        }
    }
    
    // ========== 私有方法 ==========
    
    private void indexDocumentAsync(KnowledgeBase kb, Document doc) {
        doc.markProcessing();
        
        // 简化实现：同步索引
        try {
            List<DocumentChunk> chunks = indexDocument(kb, doc);
            doc.markIndexed(chunks.size());
        } catch (Exception e) {
            doc.markFailed(e.getMessage());
            log.error("Failed to index document: {}", doc.getDocId(), e);
        }
    }
    
    private List<DocumentChunk> indexDocument(KnowledgeBase kb, Document doc) {
        // 1. 分块
        List<DocumentChunk> chunks = chunker.chunk(doc, kb);
        
        if (chunks.isEmpty()) {
            return chunks;
        }
        
        // 2. 向量化
        List<String> texts = new ArrayList<>();
        for (DocumentChunk chunk : chunks) {
            texts.add(chunk.getContent());
        }
        
        List<float[]> vectors = embeddingService.embedBatch(texts);
        
        // 3. 存储到向量库
        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = chunks.get(i);
            float[] vector = vectors.get(i);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("kbId", kb.getKbId());
            metadata.put("docId", doc.getDocId());
            metadata.put("chunkId", chunk.getChunkId());
            metadata.put("chunkIndex", chunk.getChunkIndex());
            
            vectorStore.insert(chunk.getChunkId(), vector, metadata);
            chunk.setVectorId(chunk.getChunkId());
        }
        
        return chunks;
    }
    
    private Map<String, Object> buildFilters(KnowledgeSearchRequest request) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("kbId", request.getKbId());
        
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            filters.put("tags", request.getTags());
        }
        
        filters.putAll(request.getFilters());
        return filters;
    }
    
    private int comparePermissions(String p1, String p2) {
        Map<String, Integer> weights = new HashMap<>();
        weights.put("read", 1);
        weights.put("write", 2);
        weights.put("admin", 3);
        
        return Integer.compare(
            weights.getOrDefault(p1, 0),
            weights.getOrDefault(p2, 0)
        );
    }
    
    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
