package net.ooder.sdk.story;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class UserStoryImpl implements UserStory {
    
    private final String storyId;
    private final String title;
    private final String description;
    private StoryStatus status;
    private final String actor;
    private final String goal;
    private final String benefit;
    private final List<StoryStep> steps;
    private final Map<String, Object> context;
    private final List<String> requiredCapabilities;
    private final Instant createdAt;
    private final Instant completedAt;
    
    UserStoryImpl(UserStoryBuilder builder) {
        this.storyId = builder.storyId;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
        this.actor = builder.actor;
        this.goal = builder.goal;
        this.benefit = builder.benefit;
        this.steps = Collections.unmodifiableList(builder.steps);
        this.context = Collections.unmodifiableMap(builder.context);
        this.requiredCapabilities = Collections.unmodifiableList(builder.requiredCapabilities);
        this.createdAt = builder.createdAt != null ? Instant.ofEpochMilli(builder.createdAt) : Instant.now();
        this.completedAt = builder.completedAt != null ? Instant.ofEpochMilli(builder.completedAt) : null;
    }
    
    @Override public String getStoryId() { return storyId; }
    @Override public String getTitle() { return title; }
    @Override public String getDescription() { return description; }
    @Override public StoryStatus getStatus() { return status; }
    @Override public void setStatus(StoryStatus status) { this.status = status; }
    @Override public String getActor() { return actor; }
    @Override public String getGoal() { return goal; }
    @Override public String getBenefit() { return benefit; }
    @Override public List<StoryStep> getSteps() { return steps; }
    @Override public Map<String, Object> getContext() { return context; }
    @Override public List<String> getRequiredCapabilities() { return requiredCapabilities; }
    @Override public Instant getCreatedAt() { return createdAt; }
    @Override public Instant getCompletedAt() { return completedAt; }
    
    @Override
    public String toString() {
        return "UserStory{" +
            "storyId='" + storyId + '\'' +
            ", title='" + title + '\'' +
            ", status=" + status +
            ", actor='" + actor + '\'' +
            ", goal='" + goal + '\'' +
            '}';
    }
}
