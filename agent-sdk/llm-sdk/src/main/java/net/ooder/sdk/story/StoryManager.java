package net.ooder.sdk.story;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 故事管理器接口
 * 管理用户故事的完整生命周期
 */
public interface StoryManager {
    
    /**
     * 创建故事
     * @param title 标题
     * @param description 描述
     * @param actor 参与者
     * @param goal 目标
     * @param benefit 收益
     * @return 创建的故事
     */
    UserStory createStory(String title, String description, String actor, String goal, String benefit);
    
    /**
     * 根据ID获取故事
     * @param storyId 故事ID
     * @return 故事对象
     */
    Optional<UserStory> getStory(String storyId);
    
    /**
     * 更新故事
     * @param storyId 故事ID
     * @param story 更新后的故事
     * @return 是否成功
     */
    boolean updateStory(String storyId, UserStory story);
    
    /**
     * 删除故事
     * @param storyId 故事ID
     * @return 是否成功
     */
    boolean deleteStory(String storyId);
    
    /**
     * 获取所有故事
     * @return 故事列表
     */
    List<UserStory> getAllStories();
    
    /**
     * 根据状态获取故事
     * @param status 状态
     * @return 故事列表
     */
    List<UserStory> getStoriesByStatus(UserStory.StoryStatus status);
    
    /**
     * 启动故事执行
     * @param storyId 故事ID
     * @return 执行结果
     */
    CompletableFuture<StoryExecutionResult> executeStory(String storyId);
    
    /**
     * 暂停故事执行
     * @param storyId 故事ID
     * @return 是否成功
     */
    boolean pauseStory(String storyId);
    
    /**
     * 恢复故事执行
     * @param storyId 故事ID
     * @return 是否成功
     */
    boolean resumeStory(String storyId);
    
    /**
     * 取消故事执行
     * @param storyId 故事ID
     * @return 是否成功
     */
    boolean cancelStory(String storyId);
    
    /**
     * 添加故事监听器
     * @param listener 监听器
     */
    void addStoryListener(StoryListener listener);
    
    /**
     * 移除故事监听器
     * @param listener 监听器
     */
    void removeStoryListener(StoryListener listener);
    
    /**
     * 获取活跃故事数量
     * @return 数量
     */
    int getActiveStoryCount();
    
    /**
     * 获取故事统计信息
     * @return 统计信息
     */
    StoryStatistics getStatistics();
    
    /**
     * 清空所有故事
     */
    void clear();
    
    /**
     * 故事执行结果
     */
    class StoryExecutionResult {
        private String storyId;
        private boolean success;
        private String message;
        private long executionTime;
        private List<String> completedSteps;
        private List<String> failedSteps;
        
        public String getStoryId() { return storyId; }
        public void setStoryId(String storyId) { this.storyId = storyId; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public List<String> getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(List<String> completedSteps) { this.completedSteps = completedSteps; }
        
        public List<String> getFailedSteps() { return failedSteps; }
        public void setFailedSteps(List<String> failedSteps) { this.failedSteps = failedSteps; }
        
        public static StoryExecutionResult success(String storyId, String message) {
            StoryExecutionResult result = new StoryExecutionResult();
            result.setStoryId(storyId);
            result.setSuccess(true);
            result.setMessage(message);
            return result;
        }
        
        public static StoryExecutionResult failure(String storyId, String message) {
            StoryExecutionResult result = new StoryExecutionResult();
            result.setStoryId(storyId);
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
    
    /**
     * 故事统计信息
     */
    class StoryStatistics {
        private int totalStories;
        private int draftCount;
        private int readyCount;
        private int inProgressCount;
        private int completedCount;
        private int failedCount;
        private int cancelledCount;
        
        public int getTotalStories() { return totalStories; }
        public void setTotalStories(int totalStories) { this.totalStories = totalStories; }
        
        public int getDraftCount() { return draftCount; }
        public void setDraftCount(int draftCount) { this.draftCount = draftCount; }
        
        public int getReadyCount() { return readyCount; }
        public void setReadyCount(int readyCount) { this.readyCount = readyCount; }
        
        public int getInProgressCount() { return inProgressCount; }
        public void setInProgressCount(int inProgressCount) { this.inProgressCount = inProgressCount; }
        
        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
        
        public int getFailedCount() { return failedCount; }
        public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
        
        public int getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(int cancelledCount) { this.cancelledCount = cancelledCount; }
    }
}
