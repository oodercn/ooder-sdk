package net.ooder.sdk.story;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface UserStory {
    
    String getStoryId();
    
    String getTitle();
    
    String getDescription();
    
    StoryStatus getStatus();
    
    void setStatus(StoryStatus status);
    
    String getActor();
    
    String getGoal();
    
    String getBenefit();
    
    List<StoryStep> getSteps();
    
    Map<String, Object> getContext();
    
    List<String> getRequiredCapabilities();
    
    Instant getCreatedAt();
    
    Instant getCompletedAt();
    
    enum StoryStatus {
        DRAFT,
        READY,
        IN_PROGRESS,
        BLOCKED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    static UserStoryBuilder builder() {
        return new UserStoryBuilder();
    }
}
