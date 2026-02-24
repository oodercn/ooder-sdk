package net.ooder.sdk.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStoryBuilder {
    
    String storyId;
    String title;
    String description;
    UserStory.StoryStatus status = UserStory.StoryStatus.DRAFT;
    String actor;
    String goal;
    String benefit;
    List<StoryStep> steps = new ArrayList<>();
    Map<String, Object> context = new HashMap<>();
    List<String> requiredCapabilities = new ArrayList<>();
    Long createdAt;
    Long completedAt;
    
    public UserStoryBuilder storyId(String storyId) { this.storyId = storyId; return this; }
    public UserStoryBuilder title(String title) { this.title = title; return this; }
    public UserStoryBuilder description(String description) { this.description = description; return this; }
    public UserStoryBuilder status(UserStory.StoryStatus status) { this.status = status; return this; }
    public UserStoryBuilder actor(String actor) { this.actor = actor; return this; }
    public UserStoryBuilder goal(String goal) { this.goal = goal; return this; }
    public UserStoryBuilder benefit(String benefit) { this.benefit = benefit; return this; }
    public UserStoryBuilder steps(List<StoryStep> steps) { this.steps = steps; return this; }
    public UserStoryBuilder addStep(StoryStep step) { this.steps.add(step); return this; }
    public UserStoryBuilder context(Map<String, Object> context) { this.context = context; return this; }
    public UserStoryBuilder addContext(String key, Object value) { this.context.put(key, value); return this; }
    public UserStoryBuilder requiredCapabilities(List<String> capabilities) { this.requiredCapabilities = capabilities; return this; }
    public UserStoryBuilder addCapability(String capability) { this.requiredCapabilities.add(capability); return this; }
    public UserStoryBuilder createdAt(Long createdAt) { this.createdAt = createdAt; return this; }
    public UserStoryBuilder completedAt(Long completedAt) { this.completedAt = completedAt; return this; }
    
    public UserStory build() {
        if (storyId == null) storyId = "story-" + System.currentTimeMillis();
        if (createdAt == null) createdAt = System.currentTimeMillis();
        return new UserStoryImpl(this);
    }
}
