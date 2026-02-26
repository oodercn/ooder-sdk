package net.ooder.sdk.orchestration;

import net.ooder.sdk.story.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WillTransformerImpl implements WillTransformer {
    
    private static final Logger log = LoggerFactory.getLogger(WillTransformerImpl.class);
    
    @Override
    public UserStory transform(WillExpression will) {
        if (will == null) {
            throw new IllegalArgumentException("Will cannot be null");
        }
        
        String actor = extractActor(will);
        String goal = extractGoal(will);
        String benefit = extractBenefit(will);
        
        UserStoryBuilder builder = UserStory.builder()
            .storyId("story-" + will.getWillId())
            .title(generateTitle(will))
            .description(will.getRawText())
            .actor(actor)
            .goal(goal)
            .benefit(benefit)
            .status(UserStory.StoryStatus.DRAFT);
        
        List<StoryStep> steps = generateStepsFromWill(will);
        for (StoryStep step : steps) {
            builder.addStep(step);
        }
        
        List<String> capabilities = identifyRequiredCapabilitiesFromWill(will);
        for (String cap : capabilities) {
            builder.addCapability(cap);
        }
        
        Map<String, Object> variables = extractVariables(will);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            builder.addContext(entry.getKey(), entry.getValue());
        }
        
        return builder.build();
    }
    
    @Override
    public List<UserStory> transformToStories(WillExpression will) {
        List<UserStory> stories = new ArrayList<>();
        stories.add(transform(will));
        return stories;
    }
    
    @Override
    public StoryContext buildContext(UserStory story) {
        StoryContext context = StoryContext.create(story.getStoryId());
        
        for (Map.Entry<String, Object> entry : story.getContext().entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        
        for (String capId : story.getRequiredCapabilities()) {
            context.getAvailableCapabilities().add(capId);
        }
        
        return context;
    }
    
    @Override
    public List<StoryStep> generateSteps(UserStory story) {
        List<StoryStep> steps = new ArrayList<>();
        
        StoryStep initStep = StoryStep.builder()
            .stepId("step-init")
            .name("Initialize")
            .description("Initialize execution context")
            .type(StoryStep.StepType.ACTION)
            .build();
        steps.add(initStep);
        
        for (String capId : story.getRequiredCapabilities()) {
            StoryStep capStep = StoryStep.builder()
                .stepId("step-" + capId)
                .name("Execute " + capId)
                .description("Execute capability: " + capId)
                .type(StoryStep.StepType.ACTION)
                .capabilityId(capId)
                .addDependency("step-init")
                .build();
            steps.add(capStep);
        }
        
        StoryStepBuilder verifyBuilder = StoryStep.builder()
            .stepId("step-verify")
            .name("Verify Results")
            .description("Verify execution results")
            .type(StoryStep.StepType.QUERY);
        
        if (!steps.isEmpty()) {
            StoryStep lastStep = steps.get(steps.size() - 1);
            verifyBuilder.addDependency(lastStep.getStepId());
        }
        steps.add(verifyBuilder.build());
        
        return steps;
    }
    
    @Override
    public List<String> identifyRequiredCapabilities(UserStory story) {
        List<String> capabilities = new ArrayList<>();
        
        String intent = story.getGoal();
        if (intent != null) {
            intent = intent.toLowerCase();
            
            if (intent.contains("query") || intent.contains("search") || intent.contains("find")) {
                capabilities.add("cap.query");
            }
            if (intent.contains("create") || intent.contains("add") || intent.contains("new")) {
                capabilities.add("cap.create");
            }
            if (intent.contains("update") || intent.contains("modify") || intent.contains("change")) {
                capabilities.add("cap.update");
            }
            if (intent.contains("delete") || intent.contains("remove")) {
                capabilities.add("cap.delete");
            }
            if (intent.contains("execute") || intent.contains("run") || intent.contains("start")) {
                capabilities.add("cap.execute");
            }
            if (intent.contains("monitor") || intent.contains("watch") || intent.contains("observe")) {
                capabilities.add("cap.monitor");
            }
        }
        
        if (capabilities.isEmpty()) {
            capabilities.add("cap.default");
        }
        
        return capabilities;
    }
    
    @Override
    public Map<String, Object> extractVariables(WillExpression will) {
        Map<String, Object> variables = new HashMap<>();
        
        if (will.getEntities() != null) {
            for (int i = 0; i < will.getEntities().size(); i++) {
                variables.put("entity_" + i, will.getEntities().get(i));
            }
        }
        
        if (will.getDomain() != null) {
            variables.put("domain", will.getDomain());
        }
        
        if (will.getIntent() != null) {
            variables.put("intent", will.getIntent());
        }
        
        variables.put("priority", will.getPriority());
        
        return variables;
    }
    
    private List<StoryStep> generateStepsFromWill(WillExpression will) {
        UserStory tempStory = UserStory.builder()
            .storyId("temp")
            .goal(will.getIntent())
            .build();
        return generateSteps(tempStory);
    }
    
    private List<String> identifyRequiredCapabilitiesFromWill(WillExpression will) {
        UserStory tempStory = UserStory.builder()
            .storyId("temp")
            .goal(will.getIntent())
            .build();
        return identifyRequiredCapabilities(tempStory);
    }
    
    private String extractActor(WillExpression will) {
        if (will.getMetadata() != null && will.getMetadata().containsKey("actor")) {
            return (String) will.getMetadata().get("actor");
        }
        return "user";
    }
    
    private String extractGoal(WillExpression will) {
        if (will.getIntent() != null) {
            return will.getIntent();
        }
        return will.getRawText();
    }
    
    private String extractBenefit(WillExpression will) {
        return "Achieve user intent: " + (will.getIntent() != null ? will.getIntent() : "unknown");
    }
    
    private String generateTitle(WillExpression will) {
        String intent = will.getIntent();
        if (intent != null && !intent.isEmpty()) {
            return "Story: " + intent;
        }
        return "Story from Will: " + will.getWillId();
    }
}
