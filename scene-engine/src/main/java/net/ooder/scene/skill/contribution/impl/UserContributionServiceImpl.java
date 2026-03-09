package net.ooder.scene.skill.contribution.impl;

import net.ooder.scene.skill.contribution.*;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.DocumentCreateRequest;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户知识贡献服务实现
 *
 * <p>提供用户向知识库贡献知识的完整能力实现。</p>
 *
 * <p>架构层次：应用层 - 用户知识贡献实现</p>
 *
 * @author ooder
 * @since 2.3
 */
public class UserContributionServiceImpl implements UserContributionService {
    
    private static final Logger log = LoggerFactory.getLogger(UserContributionServiceImpl.class);
    
    private static final Pattern TITLE_PATTERN = Pattern.compile("<title[^>]*>([^<]+)</title>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final int MAX_CONTENT_SIZE = 10 * 1024 * 1024;
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final Map<String, ContributionStats> statsMap = new ConcurrentHashMap<>();
    
    public UserContributionServiceImpl(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }
    
    @Override
    public Document uploadFile(String userId, String kbId, FileUploadRequest request) {
        log.info("User {} uploading file {} to kb {}", userId, request.getFileName(), kbId);
        
        validatePermission(userId, kbId, "write");
        
        String content = extractContent(request.getInputStream(), request.getMimeType());
        
        DocumentCreateRequest docRequest = new DocumentCreateRequest();
        docRequest.setTitle(request.getTitle() != null ? request.getTitle() : request.getFileName());
        docRequest.setContent(content);
        docRequest.setSource(Document.SOURCE_UPLOAD);
        docRequest.setFilePath(request.getFileName());
        docRequest.setFileSize(request.getFileSize());
        docRequest.setMimeType(request.getMimeType());
        docRequest.setTags(request.getTags());
        docRequest.setMetadata(request.getMetadata());
        
        Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
        
        updateStats(userId, "file", request.getFileSize());
        
        log.info("File uploaded successfully: docId={}", doc.getDocId());
        return doc;
    }
    
    @Override
    public Document inputText(String userId, String kbId, TextKnowledgeRequest request) {
        log.info("User {} inputting text to kb {}", userId, kbId);
        
        validatePermission(userId, kbId, "write");
        
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }
        
        DocumentCreateRequest docRequest = new DocumentCreateRequest();
        docRequest.setTitle(request.getTitle());
        docRequest.setContent(request.getContent());
        docRequest.setSource(Document.SOURCE_TEXT);
        docRequest.setFileSize((long) request.getContent().getBytes(StandardCharsets.UTF_8).length);
        docRequest.setTags(request.getTags());
        docRequest.setMetadata(request.getMetadata());
        
        Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
        
        updateStats(userId, "text", doc.getFileSize());
        
        log.info("Text input successfully: docId={}", doc.getDocId());
        return doc;
    }
    
    @Override
    public Document importFromUrl(String userId, String kbId, UrlImportRequest request) {
        log.info("User {} importing from URL {} to kb {}", userId, request.getUrl(), kbId);
        
        validatePermission(userId, kbId, "write");
        
        String url = request.getUrl();
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL is required");
        }
        
        UrlContent fetched = fetchUrlContent(url, request);
        
        DocumentCreateRequest docRequest = new DocumentCreateRequest();
        docRequest.setTitle(request.getTitle() != null ? request.getTitle() : fetched.title);
        docRequest.setContent(fetched.content);
        docRequest.setSource(Document.SOURCE_URL);
        docRequest.setSourceUrl(url);
        docRequest.setFileSize((long) fetched.content.getBytes(StandardCharsets.UTF_8).length);
        docRequest.setTags(request.getTags());
        docRequest.setMetadata(request.getMetadata());
        
        Document doc = knowledgeBaseService.addDocument(kbId, docRequest);
        
        updateStats(userId, "url", doc.getFileSize());
        
        log.info("URL imported successfully: docId={}", doc.getDocId());
        return doc;
    }
    
    @Override
    public BatchImportResult batchUpload(String userId, String kbId, List<FileUploadRequest> requests) {
        log.info("User {} batch uploading {} files to kb {}", userId, requests.size(), kbId);
        
        validatePermission(userId, kbId, "write");
        
        BatchImportResult result = new BatchImportResult(requests.size());
        
        for (FileUploadRequest request : requests) {
            try {
                Document doc = uploadFile(userId, kbId, request);
                result.addSuccess(doc);
            } catch (Exception e) {
                log.error("Failed to upload file: {}", request.getFileName(), e);
                result.addError(request.getFileName(), e.getMessage());
            }
        }
        
        log.info("Batch upload completed: success={}, failed={}", result.getSuccessCount(), result.getFailedCount());
        return result;
    }
    
    @Override
    public ContributionStats getStats(String userId) {
        return statsMap.getOrDefault(userId, new ContributionStats(userId));
    }
    
    private void validatePermission(String userId, String kbId, String permission) {
        KnowledgeBase kb = knowledgeBaseService.get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        if (!knowledgeBaseService.hasPermission(kbId, userId, permission)) {
            throw new SecurityException("User does not have " + permission + " permission for kb: " + kbId);
        }
    }
    
    private String extractContent(InputStream inputStream, String mimeType) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input stream", e);
        }
    }
    
    private UrlContent fetchUrlContent(String urlStr, UrlImportRequest request) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(request.getTimeout());
            connection.setReadTimeout(request.getTimeout());
            connection.setInstanceFollowRedirects(request.isFollowRedirects());
            
            connection.setRequestProperty("User-Agent", "OoderKnowledgeBot/1.0");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,text/plain");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP error: " + responseCode);
            }
            
            String contentType = connection.getContentType();
            long contentLength = connection.getContentLengthLong();
            
            if (contentLength > request.getMaxContentLength()) {
                throw new RuntimeException("Content too large: " + contentLength + " bytes");
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (content.length() + line.length() > request.getMaxContentLength()) {
                        break;
                    }
                    content.append(line).append("\n");
                }
            }
            
            String rawContent = content.toString();
            String title = extractTitle(rawContent);
            String cleanContent = cleanHtml(rawContent, contentType);
            
            return new UrlContent(title, cleanContent);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch URL: " + urlStr, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private String extractTitle(String html) {
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Untitled";
    }
    
    private String cleanHtml(String content, String contentType) {
        if (contentType != null && contentType.contains("text/html")) {
            content = HTML_TAG_PATTERN.matcher(content).replaceAll(" ");
            content = content.replaceAll("\\s+", " ").trim();
        }
        return content;
    }
    
    private void updateStats(String userId, String type, long size) {
        ContributionStats stats = statsMap.computeIfAbsent(userId, ContributionStats::new);
        stats.setLastContributionTime(System.currentTimeMillis());
        stats.addSize(size);
        
        switch (type) {
            case "file":
                stats.incrementFiles();
                break;
            case "text":
                stats.incrementTexts();
                break;
            case "url":
                stats.incrementUrls();
                break;
        }
    }
    
    private static class UrlContent {
        String title;
        String content;
        
        UrlContent(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }
}
