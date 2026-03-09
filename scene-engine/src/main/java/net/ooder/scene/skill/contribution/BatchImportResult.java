package net.ooder.scene.skill.contribution;

import net.ooder.scene.skill.knowledge.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量导入结果
 *
 * @author ooder
 * @since 2.3
 */
public class BatchImportResult {
    
    private int totalCount;
    private int successCount;
    private int failedCount;
    private List<Document> successDocuments = new ArrayList<>();
    private List<ImportError> errors = new ArrayList<>();
    
    public BatchImportResult() {
    }
    
    public BatchImportResult(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public void addSuccess(Document doc) {
        successDocuments.add(doc);
        successCount++;
    }
    
    public void addError(String fileName, String errorMessage) {
        errors.add(new ImportError(fileName, errorMessage));
        failedCount++;
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
    
    public List<Document> getSuccessDocuments() {
        return successDocuments;
    }
    
    public void setSuccessDocuments(List<Document> successDocuments) {
        this.successDocuments = successDocuments;
    }
    
    public List<ImportError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }
    
    public boolean isAllSuccess() {
        return failedCount == 0;
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
