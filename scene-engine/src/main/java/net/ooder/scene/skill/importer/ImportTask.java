package net.ooder.scene.skill.importer;

import net.ooder.scene.skill.knowledge.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入任务
 *
 * @author ooder
 * @since 2.3
 */
public class ImportTask {
    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_CANCELLED = "cancelled";
    
    private String taskId;
    private String userId;
    private String kbId;
    private String source;
    private int totalCount;
    private int processedCount;
    private int successCount;
    private int failedCount;
    private String status;
    private long startTime;
    private long endTime;
    private String errorMessage;
    private List<String> processedFiles = new ArrayList<>();
    
    public ImportTask() {
    }
    
    public ImportTask(String taskId, String userId, String kbId) {
        this.taskId = taskId;
        this.userId = userId;
        this.kbId = kbId;
        this.status = STATUS_PENDING;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getProcessedCount() {
        return processedCount;
    }
    
    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public List<String> getProcessedFiles() {
        return processedFiles;
    }
    
    public void setProcessedFiles(List<String> processedFiles) {
        this.processedFiles = processedFiles;
    }
    
    public void incrementProcessed() {
        this.processedCount++;
    }
    
    public void incrementSuccess() {
        this.successCount++;
    }
    
    public void incrementFailed() {
        this.failedCount++;
    }
    
    public void addProcessedFile(String fileName) {
        this.processedFiles.add(fileName);
    }
    
    public boolean isFinished() {
        return STATUS_COMPLETED.equals(status) || 
               STATUS_FAILED.equals(status) || 
               STATUS_CANCELLED.equals(status);
    }
    
    public int getProgress() {
        if (totalCount == 0) {
            return 0;
        }
        return (int) ((processedCount * 100.0) / totalCount);
    }
    
    public void start() {
        this.status = STATUS_RUNNING;
        this.startTime = System.currentTimeMillis();
    }
    
    public void complete() {
        this.status = STATUS_COMPLETED;
        this.endTime = System.currentTimeMillis();
    }
    
    public void fail(String error) {
        this.status = STATUS_FAILED;
        this.errorMessage = error;
        this.endTime = System.currentTimeMillis();
    }
    
    public void cancel() {
        this.status = STATUS_CANCELLED;
        this.endTime = System.currentTimeMillis();
    }
}
