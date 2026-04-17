package net.ooder.skill.knowledge.controller;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.vector.EmbeddingModel;
import net.ooder.scene.skill.vector.VectorStore;
import net.ooder.scene.skill.knowledge.FileParseService;
import net.ooder.skill.knowledge.dto.KnowledgeBaseDTO;
import net.ooder.skill.knowledge.dto.KnowledgeDocumentDTO;
import net.ooder.skill.knowledge.dto.TextDocumentRequestDTO;
import net.ooder.skill.knowledge.dto.UrlImportRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/knowledge-bases")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class KnowledgeBaseController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);

    @Autowired(required = false)
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Autowired(required = false)
    private EmbeddingModel embeddingModel;

    @Autowired(required = false)
    private FileParseService fileParseService;

    private final Map<String, KnowledgeBaseDTO> knowledgeBases = new ConcurrentHashMap<>();
    private final Map<String, List<KnowledgeDocumentDTO>> documents = new ConcurrentHashMap<>();

    @GetMapping
    public ResultModel<List<KnowledgeBaseDTO>> listKnowledgeBases() {
        log.info("[listKnowledgeBases] Listing all knowledge bases");
        return ResultModel.success(new ArrayList<>(knowledgeBases.values()));
    }

    @PostMapping
    public ResultModel<KnowledgeBaseDTO> createKnowledgeBase(@RequestBody KnowledgeBaseDTO request) {
        log.info("[createKnowledgeBase] Creating knowledge base: {}", request.getName());
        
        String kbId = request.getKbId() != null ? request.getKbId() : "kb-" + System.currentTimeMillis();
        
        KnowledgeBaseDTO kb = new KnowledgeBaseDTO();
        kb.setKbId(kbId);
        kb.setName(request.getName());
        kb.setDescription(request.getDescription());
        kb.setVisibility(request.getVisibility() != null ? request.getVisibility() : "private");
        kb.setEmbeddingModel(request.getEmbeddingModel() != null ? request.getEmbeddingModel() : "text-embedding-ada-002");
        kb.setChunkSize(request.getChunkSize() > 0 ? request.getChunkSize() : 500);
        kb.setChunkOverlap(request.getChunkOverlap() > 0 ? request.getChunkOverlap() : 50);
        kb.setDocumentCount(0);
        kb.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        kb.setCreateTime(System.currentTimeMillis());
        kb.setUpdateTime(System.currentTimeMillis());
        
        knowledgeBases.put(kbId, kb);
        documents.put(kbId, new ArrayList<>());
        
        return ResultModel.success(kb);
    }

    @GetMapping("/{kbId}")
    public ResultModel<KnowledgeBaseDTO> getKnowledgeBase(@PathVariable String kbId) {
        log.info("[getKnowledgeBase] Getting knowledge base: {}", kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        return ResultModel.success(kb);
    }

    @PutMapping("/{kbId}")
    public ResultModel<KnowledgeBaseDTO> updateKnowledgeBase(
            @PathVariable String kbId,
            @RequestBody KnowledgeBaseDTO request) {
        
        log.info("[updateKnowledgeBase] Updating knowledge base: {}", kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
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
        if (request.getTags() != null) {
            kb.setTags(request.getTags());
        }
        kb.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(kb);
    }

    @DeleteMapping("/{kbId}")
    public ResultModel<Boolean> deleteKnowledgeBase(@PathVariable String kbId) {
        log.info("[deleteKnowledgeBase] Deleting knowledge base: {}", kbId);
        
        KnowledgeBaseDTO removed = knowledgeBases.remove(kbId);
        if (removed == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        documents.remove(kbId);
        
        return ResultModel.success(true);
    }

    @GetMapping("/{kbId}/documents")
    public ResultModel<Map<String, Object>> listDocuments(
            @PathVariable String kbId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        log.info("[listDocuments] Listing documents for kb: {}, page: {}, size: {}", kbId, pageNum, pageSize);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        List<KnowledgeDocumentDTO> allDocs = documents.getOrDefault(kbId, new ArrayList<>());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allDocs.size());
        List<KnowledgeDocumentDTO> pageDocs = start < allDocs.size() ? allDocs.subList(start, end) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageDocs);
        result.put("total", allDocs.size());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return ResultModel.success(result);
    }

    @PostMapping("/{kbId}/documents/text")
    public ResultModel<KnowledgeDocumentDTO> addTextDocument(
            @PathVariable String kbId,
            @RequestBody TextDocumentRequestDTO request) {
        
        log.info("[addTextDocument] Adding text document to kb: {}", kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        String docId = "doc-" + System.currentTimeMillis();
        KnowledgeDocumentDTO doc = new KnowledgeDocumentDTO();
        doc.setDocId(docId);
        doc.setKbId(kbId);
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        doc.setSource("text");
        doc.setCreateTime(System.currentTimeMillis());
        doc.setUpdateTime(System.currentTimeMillis());
        
        documents.computeIfAbsent(kbId, k -> new ArrayList<>()).add(doc);
        kb.setDocumentCount(documents.get(kbId).size());
        kb.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(doc);
    }

    @PostMapping("/{kbId}/documents/upload")
    public ResultModel<KnowledgeDocumentDTO> uploadDocument(
            @PathVariable String kbId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) {
        
        log.info("[uploadDocument] Uploading document to kb: {}, file: {}", kbId, file.getOriginalFilename());
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        String docId = "doc-" + System.currentTimeMillis();
        KnowledgeDocumentDTO doc = new KnowledgeDocumentDTO();
        doc.setDocId(docId);
        doc.setKbId(kbId);
        doc.setTitle(title != null ? title : file.getOriginalFilename());
        doc.setSource("upload");
        doc.setFileSize(file.getSize());
        doc.setCreateTime(System.currentTimeMillis());
        doc.setUpdateTime(System.currentTimeMillis());
        
        documents.computeIfAbsent(kbId, k -> new ArrayList<>()).add(doc);
        kb.setDocumentCount(documents.get(kbId).size());
        kb.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(doc);
    }

    @PostMapping("/{kbId}/documents/url")
    public ResultModel<KnowledgeDocumentDTO> importFromUrl(
            @PathVariable String kbId,
            @RequestBody UrlImportRequestDTO request) {
        
        log.info("[importFromUrl] Importing from URL to kb: {}, url: {}", kbId, request.getUrl());
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        String docId = "doc-" + System.currentTimeMillis();
        KnowledgeDocumentDTO doc = new KnowledgeDocumentDTO();
        doc.setDocId(docId);
        doc.setKbId(kbId);
        doc.setTitle(request.getTitle() != null ? request.getTitle() : request.getUrl());
        doc.setSourceUrl(request.getUrl());
        doc.setSource("url");
        doc.setCreateTime(System.currentTimeMillis());
        doc.setUpdateTime(System.currentTimeMillis());
        
        documents.computeIfAbsent(kbId, k -> new ArrayList<>()).add(doc);
        kb.setDocumentCount(documents.get(kbId).size());
        kb.setUpdateTime(System.currentTimeMillis());
        
        return ResultModel.success(doc);
    }

    @DeleteMapping("/{kbId}/documents/{docId}")
    public ResultModel<Boolean> deleteDocument(
            @PathVariable String kbId,
            @PathVariable String docId) {
        
        log.info("[deleteDocument] Deleting document: {} from kb: {}", docId, kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        List<KnowledgeDocumentDTO> docs = documents.get(kbId);
        if (docs != null) {
            docs.removeIf(d -> d.getDocId().equals(docId));
            kb.setDocumentCount(docs.size());
            kb.setUpdateTime(System.currentTimeMillis());
        }
        
        return ResultModel.success(true);
    }

    @PostMapping("/{kbId}/rebuild-index")
    public ResultModel<Map<String, Object>> rebuildIndex(@PathVariable String kbId) {
        log.info("[rebuildIndex] Rebuilding index for kb: {}", kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("kbId", kbId);
        result.put("status", "indexing");
        result.put("message", "索引重建已启动");
        
        return ResultModel.success(result);
    }

    @PostMapping("/{kbId}/documents/{docId}/reindex")
    public ResultModel<Boolean> reindexDocument(
            @PathVariable String kbId,
            @PathVariable String docId) {
        
        log.info("[reindexDocument] Reindexing document: {} in kb: {}", docId, kbId);
        
        KnowledgeBaseDTO kb = knowledgeBases.get(kbId);
        if (kb == null) {
            return ResultModel.error(404, "知识库不存在: " + kbId);
        }
        
        List<KnowledgeDocumentDTO> docs = documents.get(kbId);
        if (docs != null) {
            for (KnowledgeDocumentDTO doc : docs) {
                if (doc.getDocId().equals(docId)) {
                    doc.setUpdateTime(System.currentTimeMillis());
                    break;
                }
            }
        }
        
        return ResultModel.success(true);
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;
        private String requestId;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
            this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> error(int code, String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(code);
            result.setStatus("error");
            result.setMessage(message);
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
    }
}
