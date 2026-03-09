package net.ooder.scene.skill.importer.impl;

import net.ooder.scene.skill.contribution.FileUploadRequest;
import net.ooder.scene.skill.contribution.UrlImportRequest;
import net.ooder.scene.skill.contribution.UserContributionService;
import net.ooder.scene.skill.importer.*;
import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.permission.Permission;
import net.ooder.scene.skill.permission.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 批量导入服务实现
 *
 * <p>提供知识库批量导入的完整能力实现。</p>
 *
 * <p>架构层次：应用层 - 批量导入实现</p>
 *
 * @author ooder
 * @since 2.3
 */
public class BatchImportServiceImpl implements BatchImportService {
    
    private static final Logger log = LoggerFactory.getLogger(BatchImportServiceImpl.class);
    
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "md", "json", "xml", "csv", "html", "htm", "pdf", "doc", "docx"
    ));
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final PermissionService permissionService;
    private final UserContributionService contributionService;
    private final Map<String, ImportTask> taskStore = new ConcurrentHashMap<>();
    private final Map<String, ImportResult> resultStore = new ConcurrentHashMap<>();
    
    public BatchImportServiceImpl(KnowledgeBaseService knowledgeBaseService,
                                   PermissionService permissionService,
                                   UserContributionService contributionService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.permissionService = permissionService;
        this.contributionService = contributionService;
    }
    
    @Override
    public ImportTask importFromArchive(String userId, String kbId, ArchiveImportRequest request) {
        log.info("User {} importing archive to kb {}", userId, kbId);
        
        validatePermission(userId, kbId, Permission.WRITE);
        
        String taskId = generateId();
        ImportTask task = new ImportTask(taskId, userId, kbId);
        task.setSource(request.getFileName());
        taskStore.put(taskId, task);
        
        processArchiveAsync(task, request);
        
        return task;
    }
    
    @Override
    public ImportTask importFromUrls(String userId, String kbId, List<String> urls) {
        log.info("User {} importing {} URLs to kb {}", userId, urls.size(), kbId);
        
        validatePermission(userId, kbId, Permission.WRITE);
        
        String taskId = generateId();
        ImportTask task = new ImportTask(taskId, userId, kbId);
        task.setSource("urls");
        task.setTotalCount(urls.size());
        taskStore.put(taskId, task);
        
        processUrlsAsync(task, urls);
        
        return task;
    }
    
    @Override
    public ImportTask getTask(String taskId) {
        return taskStore.get(taskId);
    }
    
    @Override
    public void cancelTask(String taskId, String userId) {
        ImportTask task = taskStore.get(taskId);
        if (task != null && task.getUserId().equals(userId)) {
            task.cancel();
            log.info("Task cancelled: {}", taskId);
        }
    }
    
    @Override
    public List<ImportTask> listUserTasks(String userId) {
        List<ImportTask> result = new ArrayList<>();
        for (ImportTask task : taskStore.values()) {
            if (task.getUserId().equals(userId)) {
                result.add(task);
            }
        }
        return result;
    }
    
    @Override
    public ImportResult getResult(String taskId) {
        return resultStore.get(taskId);
    }
    
    private void validatePermission(String userId, String kbId, Permission permission) {
        KnowledgeBase kb = knowledgeBaseService.get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        if (!permissionService.hasPermission(kbId, userId, permission)) {
            throw new SecurityException("No " + permission.getCode() + " permission for kb: " + kbId);
        }
    }
    
    private void processArchiveAsync(ImportTask task, ArchiveImportRequest request) {
        task.start();
        
        try {
            List<ArchiveEntry> entries = listArchiveEntries(request);
            task.setTotalCount(entries.size());
            
            ImportResult result = new ImportResult(task.getTaskId());
            result.setTotalCount(entries.size());
            long startTime = System.currentTimeMillis();
            
            for (ArchiveEntry entry : entries) {
                if (task.isFinished()) {
                    break;
                }
                
                if (!shouldProcess(entry.name, request)) {
                    result.addSkipped(entry.name, "文件类型不支持或被排除");
                    task.incrementProcessed();
                    continue;
                }
                
                if (entry.size > request.getMaxFileSize()) {
                    result.addSkipped(entry.name, "文件过大");
                    task.incrementProcessed();
                    continue;
                }
                
                try {
                    String content = readEntryContent(entry);
                    
                    FileUploadRequest uploadRequest = new FileUploadRequest();
                    uploadRequest.setFileName(entry.name);
                    uploadRequest.setFileSize(entry.size);
                    uploadRequest.setTitle(extractTitle(entry.name));
                    uploadRequest.setTags(request.getTags());
                    uploadRequest.setMetadata(request.getMetadata());
                    uploadRequest.setInputStream(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
                    
                    Document doc = contributionService.uploadFile(task.getUserId(), task.getKbId(), uploadRequest);
                    result.addSuccess(doc);
                    task.incrementSuccess();
                    
                } catch (Exception e) {
                    log.error("Failed to import file: {}", entry.name, e);
                    result.addError(entry.name, e.getMessage());
                    task.incrementFailed();
                }
                
                task.incrementProcessed();
                task.addProcessedFile(entry.name);
            }
            
            result.setDuration(System.currentTimeMillis() - startTime);
            resultStore.put(task.getTaskId(), result);
            task.complete();
            
            log.info("Archive import completed: taskId={}, success={}, failed={}", 
                    task.getTaskId(), result.getSuccessCount(), result.getFailedCount());
            
        } catch (Exception e) {
            log.error("Archive import failed: taskId={}", task.getTaskId(), e);
            task.fail(e.getMessage());
        }
    }
    
    private void processUrlsAsync(ImportTask task, List<String> urls) {
        task.start();
        
        ImportResult result = new ImportResult(task.getTaskId());
        result.setTotalCount(urls.size());
        long startTime = System.currentTimeMillis();
        
        for (String url : urls) {
            if (task.isFinished()) {
                break;
            }
            
            try {
                UrlImportRequest importRequest = new UrlImportRequest(url);
                Document doc = contributionService.importFromUrl(task.getUserId(), task.getKbId(), importRequest);
                result.addSuccess(doc);
                task.incrementSuccess();
                
            } catch (Exception e) {
                log.error("Failed to import URL: {}", url, e);
                result.addError(url, e.getMessage());
                task.incrementFailed();
            }
            
            task.incrementProcessed();
        }
        
        result.setDuration(System.currentTimeMillis() - startTime);
        resultStore.put(task.getTaskId(), result);
        task.complete();
        
        log.info("URL import completed: taskId={}, success={}, failed={}", 
                task.getTaskId(), result.getSuccessCount(), result.getFailedCount());
    }
    
    private List<ArchiveEntry> listArchiveEntries(ArchiveImportRequest request) throws IOException {
        List<ArchiveEntry> entries = new ArrayList<>();
        
        try (ZipInputStream zis = new ZipInputStream(request.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    ArchiveEntry ae = new ArchiveEntry();
                    ae.name = entry.getName();
                    ae.size = entry.getSize();
                    ae.inputStream = copyStream(zis);
                    entries.add(ae);
                }
                zis.closeEntry();
            }
        }
        
        return entries;
    }
    
    private boolean shouldProcess(String fileName, ArchiveImportRequest request) {
        String ext = getFileExtension(fileName);
        if (!SUPPORTED_EXTENSIONS.contains(ext.toLowerCase())) {
            return false;
        }
        
        if (request.getExcludePatterns() != null) {
            for (String pattern : request.getExcludePatterns()) {
                if (Pattern.matches(pattern, fileName)) {
                    return false;
                }
            }
        }
        
        if (request.getIncludePatterns() != null && !request.getIncludePatterns().isEmpty()) {
            for (String pattern : request.getIncludePatterns()) {
                if (Pattern.matches(pattern, fileName)) {
                    return true;
                }
            }
            return false;
        }
        
        return true;
    }
    
    private String readEntryContent(ArchiveEntry entry) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(entry.inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private InputStream copyStream(ZipInputStream zis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            baos.write(buffer, 0, len);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    private String extractTitle(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
    
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
    
    private String generateId() {
        return "task_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private static class ArchiveEntry {
        String name;
        long size;
        InputStream inputStream;
    }
}
