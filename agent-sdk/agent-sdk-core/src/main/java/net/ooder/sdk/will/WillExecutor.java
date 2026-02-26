package net.ooder.sdk.will;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface WillExecutor {
    
    CompletableFuture<WillExecutionResult> execute(WillExpression will, WillTransformer.ExecutionPlan plan);
    
    void assignTasks(WillExpression will, List<Task> tasks);
    
    CompletableFuture<ExecutionStatus> monitorExecution(String willId);
    
    CompletableFuture<EffectEvaluation> evaluateEffect(String willId);
    
    void adjustStrategy(String willId, Feedback feedback);
    
    void cancel(String willId);
    
    class WillExecutionResult {
        private String willId;
        private String executionId;
        private boolean success;
        private String message;
        private Map<String, Object> data;
        private List<String> completedSteps;
        private List<String> failedSteps;
        private long executionTime;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getExecutionId() { return executionId; }
        public void setExecutionId(String executionId) { this.executionId = executionId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
        
        public List<String> getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(List<String> completedSteps) { this.completedSteps = completedSteps; }
        
        public List<String> getFailedSteps() { return failedSteps; }
        public void setFailedSteps(List<String> failedSteps) { this.failedSteps = failedSteps; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public static WillExecutionResult success(String willId, String executionId) {
            WillExecutionResult result = new WillExecutionResult();
            result.setWillId(willId);
            result.setExecutionId(executionId);
            result.setSuccess(true);
            result.setMessage("Execution completed successfully");
            return result;
        }
        
        public static WillExecutionResult failure(String willId, String message) {
            WillExecutionResult result = new WillExecutionResult();
            result.setWillId(willId);
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
    
    class Task {
        private String taskId;
        private String willId;
        private String name;
        private String description;
        private String assignee;
        private String status;
        private int priority;
        private long createdAt;
        private long completedAt;
        
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        
        public long getCompletedAt() { return completedAt; }
        public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
    }
    
    class ExecutionStatus {
        private String willId;
        private String status;
        private double progress;
        private List<String> completedSteps;
        private List<String> pendingSteps;
        private String currentStep;
        private Map<String, Object> metrics;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getProgress() { return progress; }
        public void setProgress(double progress) { this.progress = progress; }
        
        public List<String> getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(List<String> completedSteps) { this.completedSteps = completedSteps; }
        
        public List<String> getPendingSteps() { return pendingSteps; }
        public void setPendingSteps(List<String> pendingSteps) { this.pendingSteps = pendingSteps; }
        
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
    
    class EffectEvaluation {
        private String willId;
        private boolean goalAchieved;
        private double achievementRate;
        private List<String> achievements;
        private List<String> shortcomings;
        private String recommendation;
        private Map<String, Object> metrics;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public boolean isGoalAchieved() { return goalAchieved; }
        public void setGoalAchieved(boolean goalAchieved) { this.goalAchieved = goalAchieved; }
        
        public double getAchievementRate() { return achievementRate; }
        public void setAchievementRate(double achievementRate) { this.achievementRate = achievementRate; }
        
        public List<String> getAchievements() { return achievements; }
        public void setAchievements(List<String> achievements) { this.achievements = achievements; }
        
        public List<String> getShortcomings() { return shortcomings; }
        public void setShortcomings(List<String> shortcomings) { this.shortcomings = shortcomings; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }
    
    class Feedback {
        private String feedbackId;
        private String willId;
        private String type;
        private String content;
        private String source;
        private long timestamp;
        private int severity;
        
        public String getFeedbackId() { return feedbackId; }
        public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public int getSeverity() { return severity; }
        public void setSeverity(int severity) { this.severity = severity; }
    }
}
