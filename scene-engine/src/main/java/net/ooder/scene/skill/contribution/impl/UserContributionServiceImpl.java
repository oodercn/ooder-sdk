package net.ooder.scene.skill.contribution.impl;

import net.ooder.scene.skill.contribution.*;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.DocumentCreateRequest;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户知识贡献服务实现
 *
 * <p>提供用户向知识库贡献知识的完整能力</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class UserContributionServiceImpl implements UserContributionService {

    private final KnowledgeBaseService knowledgeBaseService;
    
    // 用户贡献统计缓存
    private final Map<String, ContributionStats> statsCache = new ConcurrentHashMap<>();
    
    public UserContributionServiceImpl(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @Override
    public Document uploadFile(String userId, String kbId, FileUploadRequest request) {
        if (request == null || request.getInputStream() == null) {
            throw new IllegalArgumentException("File upload request or input stream is null");
        }
        
        try {
            // 读取文件内容
            String content = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
            
            // 创建文档请求
            DocumentCreateRequest docRequest = new DocumentCreateRequest();
            docRequest.setTitle(request.getTitle() != null ? request.getTitle() : request.getFileName());
            docRequest.setContent(content);
            docRequest.setSource(Document.SOURCE_UPLOAD);
            docRequest.setTags(request.getTags());
            docRequest.setMimeType(request.getMimeType());
            docRequest.setFileSize(request.getFileSize());
            
            // 构建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("filename", request.getFileName());
            metadata.put("fileSize", request.getFileSize());
            metadata.put("mimeType", request.getMimeType());
            metadata.put("contributorId", userId);
            if (request.getMetadata() != null) {
                metadata.putAll(request.getMetadata());
            }
            docRequest.setMetadata(metadata);
            
            // 添加到知识库
            Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
            
            // 更新统计
            updateStats(userId, "file");
            
            return doc;
        } catch (Exception e) {
            throw new ContributionException("Failed to upload file: " + request.getFileName(), e);
        }
    }

    @Override
    public Document inputText(String userId, String kbId, TextKnowledgeRequest request) {
        if (request == null || request.getContent() == null) {
            throw new IllegalArgumentException("Text knowledge request or content is null");
        }
        
        try {
            // 创建文档请求
            DocumentCreateRequest docRequest = new DocumentCreateRequest();
            docRequest.setTitle(request.getTitle());
            docRequest.setContent(request.getContent());
            docRequest.setSource(Document.SOURCE_TEXT);
            docRequest.setTags(request.getTags());
            
            // 构建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("contributorId", userId);
            if (request.getMetadata() != null) {
                metadata.putAll(request.getMetadata());
            }
            docRequest.setMetadata(metadata);
            
            // 添加到知识库
            Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
            
            // 更新统计
            updateStats(userId, "text");
            
            return doc;
        } catch (Exception e) {
            throw new ContributionException("Failed to input text knowledge", e);
        }
    }

    @Override
    public Document importFromUrl(String userId, String kbId, UrlImportRequest request) {
        if (request == null || request.getUrl() == null) {
            throw new IllegalArgumentException("URL import request or URL is null");
        }
        
        HttpURLConnection connection = null;
        try {
            URL url = new URL(request.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(request.getTimeout());
            connection.setReadTimeout(request.getTimeout());
            connection.setInstanceFollowRedirects(request.isFollowRedirects());
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ContributionException("Failed to fetch URL: HTTP " + responseCode);
            }
            
            // 检查内容长度
            int contentLength = connection.getContentLength();
            if (contentLength > request.getMaxContentLength()) {
                throw new ContributionException("Content too large: " + contentLength + " bytes");
            }
            
            // 读取内容
            String content = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
            
            // 创建文档请求
            DocumentCreateRequest docRequest = new DocumentCreateRequest();
            docRequest.setTitle(request.getTitle() != null ? request.getTitle() : extractTitleFromUrl(request.getUrl()));
            docRequest.setContent(content);
            docRequest.setSource(Document.SOURCE_URL);
            docRequest.setTags(request.getTags());
            
            // 构建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("sourceUrl", request.getUrl());
            metadata.put("contributorId", userId);
            if (request.getMetadata() != null) {
                metadata.putAll(request.getMetadata());
            }
            docRequest.setMetadata(metadata);
            
            // 添加到知识库
            Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
            
            // 更新统计
            updateStats(userId, "url");
            
            return doc;
        } catch (Exception e) {
            throw new ContributionException("Failed to import from URL: " + request.getUrl(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public BatchImportResult batchUpload(String userId, String kbId, List<FileUploadRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new BatchImportResult(0);
        }
        
        BatchImportResult result = new BatchImportResult(requests.size());
        
        for (FileUploadRequest request : requests) {
            try {
                Document doc = uploadFile(userId, kbId, request);
                result.addSuccess(doc);
            } catch (Exception e) {
                result.addError(request.getFileName(), e.getMessage());
            }
        }
        
        return result;
    }

    @Override
    public ContributionStats getStats(String userId) {
        return statsCache.getOrDefault(userId, createEmptyStats(userId));
    }
    
    /**
     * 更新用户贡献统计
     */
    private void updateStats(String userId, String type) {
        ContributionStats stats = statsCache.computeIfAbsent(userId, this::createEmptyStats);
        stats.setTotalContributions(stats.getTotalContributions() + 1);
        
        Map<String, Long> typeCounts = stats.getTypeCounts();
        if (typeCounts == null) {
            typeCounts = new HashMap<>();
            stats.setTypeCounts(typeCounts);
        }
        typeCounts.put(type, typeCounts.getOrDefault(type, 0L) + 1);
        
        // 计算积分（简单规则：每次贡献10分）
        stats.setTotalPoints(stats.getTotalPoints() + 10);
        
        // 计算等级
        stats.setLevel(calculateLevel(stats.getTotalPoints()));
    }
    
    /**
     * 创建空统计
     */
    private ContributionStats createEmptyStats(String userId) {
        ContributionStats stats = new ContributionStats();
        stats.setUserId(userId);
        stats.setTotalContributions(0);
        stats.setTotalPoints(0);
        stats.setLevel(1);
        stats.setTypeCounts(new HashMap<>());
        return stats;
    }
    
    /**
     * 计算等级
     */
    private int calculateLevel(long points) {
        if (points >= 1000) return 5;
        if (points >= 500) return 4;
        if (points >= 200) return 3;
        if (points >= 50) return 2;
        return 1;
    }
    
    /**
     * 从URL提取标题
     */
    private String extractTitleFromUrl(String url) {
        try {
            String path = new URL(url).getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            return fileName.isEmpty() ? "Imported from URL" : fileName;
        } catch (Exception e) {
            return "Imported from URL";
        }
    }
}
