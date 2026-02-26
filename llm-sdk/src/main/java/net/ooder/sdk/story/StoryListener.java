package net.ooder.sdk.story;

/**
 * 故事监听器接口
 * 用于监听故事生命周期事件
 */
public interface StoryListener {
    
    /**
     * 故事创建时触发
     * @param story 创建的故事
     */
    void onStoryCreated(UserStory story);
    
    /**
     * 故事更新时触发
     * @param story 更新后的故事
     */
    void onStoryUpdated(UserStory story);
    
    /**
     * 故事删除时触发
     * @param storyId 删除的故事ID
     */
    void onStoryDeleted(String storyId);
    
    /**
     * 故事开始执行时触发
     * @param story 执行的故事
     */
    void onStoryStarted(UserStory story);
    
    /**
     * 故事步骤完成时触发
     * @param story 故事
     * @param stepId 完成的步骤ID
     */
    void onStoryStepCompleted(UserStory story, String stepId);
    
    /**
     * 故事步骤失败时触发
     * @param story 故事
     * @param stepId 失败的步骤ID
     * @param error 错误信息
     */
    void onStoryStepFailed(UserStory story, String stepId, Throwable error);
    
    /**
     * 故事完成时触发
     * @param story 完成的故事
     * @param result 执行结果
     */
    void onStoryCompleted(UserStory story, StoryManager.StoryExecutionResult result);
    
    /**
     * 故事失败时触发
     * @param story 失败的故事
     * @param error 错误信息
     */
    void onStoryFailed(UserStory story, Throwable error);
    
    /**
     * 故事取消时触发
     * @param story 取消的故事
     */
    void onStoryCancelled(UserStory story);
    
    /**
     * 故事暂停时触发
     * @param story 暂停的故事
     */
    void onStoryPaused(UserStory story);
    
    /**
     * 故事恢复时触发
     * @param story 恢复的故事
     */
    void onStoryResumed(UserStory story);
}
