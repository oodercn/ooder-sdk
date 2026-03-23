package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.*;
import net.ooder.scene.skill.knowledge.persistence.KnowledgeRepository;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
    
    private final KnowledgeRepository repository;
    private final DocumentChunker chunker;
    private final SceneEmbeddingService embeddingService;
    private final VectorStore vectorStore;
    
    public KnowledgeBaseServiceImpl(KnowledgeRepository repository,
                                     DocumentChunker chunker, 
                                     SceneEmbeddingService embeddingService,
                                     VectorStore vectorStore) {
        this.repository = repository;
        this.chunker = chunker;
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    public KnowledgeBaseServiceImpl(DocumentChunker chunker, 
                                     SceneEmbeddingService embeddingService,
                                     VectorStore vectorStore) {
        this(null, chunker, embeddingService, vectorStore);
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
        
        if (repository != null) {
            repository.saveKnowledgeBase(kb);
            IndexStatus status = new IndexStatus(kbId);
            repository.saveIndexStatus(status);
            repository.savePermission(kbId, request.getOwnerId(), "admin");
        }
        
        log.info("Knowledge base created: {}", kbId);
        return kb;
    }
    
    @Override
    public boolean exists(String kbId) {
        if (repository != null) {
            return repository.existsKnowledgeBase(kbId);
        }
        return false;
    }
    
    @Override
    public KnowledgeBase get(String kbId) {
        if (repository != null) {
            return repository.findKnowledgeBaseById(kbId);
        }
        return null;
    }
    
    @Override
    public KnowledgeBase update(String kbId, KnowledgeBaseUpdateRequest request) {
        KnowledgeBase kb = get(kbId);
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
        
        if (repository != null) {
            repository.saveKnowledgeBase(kb);
        }
        
        log.info("Knowledge base updated: {}", kbId);
        return kb;
    }
    
    @Override
    public void delete(String kbId) {
        log.info("Deleting knowledge base: {}", kbId);
        
        if (repository != null) {
            repository.deleteDocumentsByKnowledgeBase(kbId);
            repository.deletePermissionsByKnowledgeBase(kbId);
            repository.deleteKnowledgeBase(kbId);
        }
        
        Map<String, Object> deleteFilter = new HashMap<>();
        deleteFilter.put("kbId", kbId);
        vectorStore.deleteByMetadata(deleteFilter);
        
        log.info("Knowledge base deleted: {}", kbId);
    }
    
    @Override
    public List<KnowledgeBase> listByOwner(String ownerId) {
        if (repository != null) {
            return repository.findKnowledgeBasesByOwner(ownerId);
        }
        return new ArrayList<>();
    }
    
    @Override
    public List<KnowledgeBase> listPublic() {
        if (repository != null) {
            return repository.findAllKnowledgeBases().stream()
                    .filter(KnowledgeBase::isPublic)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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
        
        if (repository != null) {
            repository.saveDocument(doc);
            kb.incrementDocumentCount();
            kb.setTotalSize(kb.getTotalSize() + doc.getFileSize());
            repository.saveKnowledgeBase(kb);
        }
        
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
        if (repository != null) {
            return repository.findDocumentById(kbId, docId);
        }
        return null;
    }
    
    @Override
    public void deleteDocument(String kbId, String docId) {
        log.info("Deleting document: {} from kb: {}", docId, kbId);
        
        Document doc = getDocument(kbId, docId);
        if (doc != null) {
            KnowledgeBase kb = get(kbId);
            if (kb != null) {
                kb.decrementDocumentCount();
                kb.setTotalSize(kb.getTotalSize() - doc.getFileSize());
                if (repository != null) {
                    repository.saveKnowledgeBase(kb);
                }
            }
            
            if (repository != null) {
                repository.deleteDocument(kbId, docId);
            }
            
            Map<String, Object> docFilter = new HashMap<>();
            docFilter.put("docId", docId);
            vectorStore.deleteByMetadata(docFilter);
        }
    }
    
    @Override
    public List<Document> listDocuments(String kbId) {
        if (repository != null) {
            return repository.findDocumentsByKnowledgeBase(kbId);
        }
        return new ArrayList<>();
    }
    
    @Override
    public List<KnowledgeSearchResult> search(String kbId, KnowledgeSearchRequest request) {
        log.debug("Searching knowledge base: {}", kbId);
        
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        float[] queryVector = embeddingService.embed(request.getQuery());
        
        List<SearchResult> vectorResults = vectorStore.search(
            queryVector,
            request.getTopK(),
            buildFilters(request)
        );
        
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
        
        IndexStatus status = getIndexStatus(kbId);
        List<Document> docs = listDocuments(kbId);
        
        status.start(docs.size(), kb.getTotalSize());
        
        try {
            Map<String, Object> rebuildFilter = new HashMap<>();
            rebuildFilter.put("kbId", kbId);
            vectorStore.deleteByMetadata(rebuildFilter);
            
            int totalChunks = 0;
            int indexedDocs = 0;
            
            for (Document doc : docs) {
                doc.setStatus(Document.STATUS_PROCESSING);
                
                List<DocumentChunk> chunks = indexDocument(kb, doc);
                totalChunks += chunks.size();
                indexedDocs++;
                
                doc.markIndexed(chunks.size());
                if (repository != null) {
                    repository.saveDocument(doc);
                }
                status.updateProgress(indexedDocs, totalChunks, totalChunks);
            }
            
            status.complete();
            kb.setIndexStatus(IndexStatus.INDEXED);
            
            if (repository != null) {
                repository.saveIndexStatus(status);
                repository.saveKnowledgeBase(kb);
            }
            
            log.info("Index rebuilt for kb: {}, total chunks: {}", kbId, totalChunks);
            
        } catch (Exception e) {
            status.fail(e.getMessage());
            if (repository != null) {
                repository.saveIndexStatus(status);
            }
            log.error("Failed to rebuild index for kb: {}", kbId, e);
            throw new RuntimeException("Failed to rebuild index: " + e.getMessage(), e);
        }
    }
    
    @Override
    public IndexStatus getIndexStatus(String kbId) {
        if (repository != null) {
            IndexStatus status = repository.findIndexStatus(kbId);
            if (status == null) {
                status = new IndexStatus(kbId);
                repository.saveIndexStatus(status);
            }
            return status;
        }
        return new IndexStatus(kbId);
    }
    
    // ========== 权限管理 ==========
    
    @Override
    public boolean hasPermission(String kbId, String userId, String permission) {
        KnowledgeBase kb = get(kbId);
        if (kb == null) {
            return false;
        }
        
        if (userId.equals(kb.getOwnerId())) {
            return true;
        }
        
        if (kb.isPublic() && "read".equals(permission)) {
            return true;
        }
        
        if (repository != null) {
            String userPerm = repository.findPermission(kbId, userId);
            if (userPerm != null) {
                return comparePermissions(userPerm, permission) >= 0;
            }
        }
        
        return false;
    }
    
    @Override
    public void grantPermission(String kbId, String userId, String permission) {
        if (repository != null) {
            repository.savePermission(kbId, userId, permission);
            log.info("Permission granted: kb={}, user={}, perm={}", kbId, userId, permission);
        }
    }
    
    @Override
    public void revokePermission(String kbId, String userId) {
        if (repository != null) {
            repository.deletePermission(kbId, userId);
            log.info("Permission revoked: kb={}, user={}", kbId, userId);
        }
    }
    
    // ========== 统计聚合 ==========
    
    @Override
    public KnowledgeBaseStats getStats() {
        log.debug("Getting knowledge base statistics");
        
        KnowledgeBaseStats stats = new KnowledgeBaseStats();
        
        List<KnowledgeBase> allKbs = listAll();
        stats.setTotalKb(allKbs.size());
        
        int totalDocs = 0;
        long totalSize = 0;
        int totalChunks = 0;
        Map<String, Integer> layerStats = new HashMap<>();
        Map<String, Integer> visibilityStats = new HashMap<>();
        Map<String, Integer> statusStats = new HashMap<>();
        String defaultEmbeddingModel = null;
        
        for (KnowledgeBase kb : allKbs) {
            totalDocs += kb.getDocumentCount();
            totalSize += kb.getTotalSize();
            
            if (kb.getEmbeddingModel() != null && defaultEmbeddingModel == null) {
                defaultEmbeddingModel = kb.getEmbeddingModel();
            }
            
            String visibility = kb.getVisibility();
            visibilityStats.merge(visibility, 1, Integer::sum);
            
            String indexStatus = kb.getIndexStatus();
            if (indexStatus != null) {
                statusStats.merge(indexStatus, 1, Integer::sum);
            }
            
            if (repository != null) {
                List<Document> docs = repository.findDocumentsByKnowledgeBase(kb.getKbId());
                for (Document doc : docs) {
                    totalChunks += doc.getChunkCount();
                }
            }
        }
        
        stats.setTotalDocs(totalDocs);
        stats.setTotalSize(totalSize);
        stats.setTotalChunks(totalChunks);
        stats.setEmbeddingModel(defaultEmbeddingModel != null ? defaultEmbeddingModel : embeddingService.getModel());
        stats.setVisibilityStats(visibilityStats);
        stats.setStatusStats(statusStats);
        
        layerStats.put("GENERAL", 0);
        layerStats.put("PROFESSIONAL", 0);
        layerStats.put("SCENE", 0);
        stats.setLayerStats(layerStats);
        
        int totalBindings = 0;
        for (KnowledgeBase kb : allKbs) {
            totalBindings += getBindingCount(kb.getKbId());
        }
        stats.setTotalBindings(totalBindings);
        
        log.debug("Knowledge base statistics: totalKb={}, totalDocs={}, totalBindings={}", 
                  stats.getTotalKb(), stats.getTotalDocs(), stats.getTotalBindings());
        
        return stats;
    }
    
    @Override
    public List<KnowledgeBase> listAll() {
        if (repository != null) {
            return repository.findAllKnowledgeBases();
        }
        return new ArrayList<>();
    }
    
    @Override
    public int getBindingCount(String kbId) {
        if (repository != null) {
            return repository.countSceneBindings(kbId);
        }
        return 0;
    }
    
    // ========== 私有方法 ==========
    
    private void indexDocumentAsync(KnowledgeBase kb, Document doc) {
        doc.markProcessing();
        
        try {
            List<DocumentChunk> chunks = indexDocument(kb, doc);
            doc.markIndexed(chunks.size());
            if (repository != null) {
                repository.saveDocument(doc);
            }
        } catch (Exception e) {
            doc.markFailed(e.getMessage());
            if (repository != null) {
                repository.saveDocument(doc);
            }
            log.error("Failed to index document: {}", doc.getDocId(), e);
        }
    }
    
    private List<DocumentChunk> indexDocument(KnowledgeBase kb, Document doc) {
        List<DocumentChunk> chunks = chunker.chunk(doc, kb);
        
        if (chunks.isEmpty()) {
            return chunks;
        }
        
        List<String> texts = new ArrayList<>();
        for (DocumentChunk chunk : chunks) {
            texts.add(chunk.getContent());
        }
        
        List<float[]> vectors = embeddingService.embedBatch(texts);
        
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
