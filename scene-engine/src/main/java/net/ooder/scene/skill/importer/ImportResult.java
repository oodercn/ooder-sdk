package net.ooder.scene.skill.importer;

import net.ooder.scene.skill.knowledge.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果
 *
 * @author ooder
 * @since 2.3
 */
public class ImportResult {
    
    private String taskId;
    private int totalCount;
    private int successCount;
    private int failedCount;
    private int skippedCount;
    private List<Document> documents = new ArrayList<>();
    private List<ImportError> errors = new ArrayList<>();
    private long duration;
    
    public ImportResult() {
    }
    
    public ImportResult(String taskId) {
        this.taskId = taskId;
    }
    
    public void addSuccess(Document doc) {
        documents.add(doc);
        successCount++;
    }
    
    public void addError(String fileName, String errorMessage) {
        errors.add(new ImportError(fileName, errorMessage));
        failedCount++;
    }
    
    public void addSkipped(String fileName, String reason) {
        errors.add(new ImportError(fileName, "跳过: " + reason));
        skippedCount++;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public int getFailedCount() {
        return failedCount;
    }
    
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
    
    public int getSkippedCount() {
        return skippedCount;
    }
    
    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public List<ImportError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public boolean isAllSuccess() {
        return failedCount == 0 && skippedCount == 0;
    }
    
    public static class ImportError {
        private String fileName;
        private String errorMessage;
        
        public ImportError(String fileName, String errorMessage) {
            this.fileName = fileName;
            this.errorMessage = errorMessage;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
