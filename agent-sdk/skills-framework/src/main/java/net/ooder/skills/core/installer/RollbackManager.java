
package net.ooder.skills.core.installer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollbackManager {
    
    private static final Logger log = LoggerFactory.getLogger(RollbackManager.class);
    
    private final Map<String, Stack<RollbackAction>> rollbackStacks;
    private final Map<String, Set<String>> skillContextMap;
    
    public RollbackManager() {
        this.rollbackStacks = new HashMap<>();
        this.skillContextMap = new HashMap<>();
    }
    
    public void registerAction(String contextId, String skillId, String description, Runnable action) {
        Stack<RollbackAction> stack = rollbackStacks.computeIfAbsent(contextId, k -> new Stack<>());
        stack.push(new RollbackAction(description, action));
        
        Set<String> contexts = skillContextMap.computeIfAbsent(skillId, k -> new HashSet<>());
        contexts.add(contextId);
        
        log.debug("Registered rollback action: {} for context: {} (skill: {})", description, contextId, skillId);
    }
    
    public void registerAction(String contextId, String description, Runnable action) {
        Stack<RollbackAction> stack = rollbackStacks.computeIfAbsent(contextId, k -> new Stack<>());
        stack.push(new RollbackAction(description, action));
        log.debug("Registered rollback action: {} for context: {}", description, contextId);
    }
    
    public void rollback(InstallContext context) {
        String contextId = context.getContextId();
        Stack<RollbackAction> stack = rollbackStacks.get(contextId);
        
        if (stack == null || stack.isEmpty()) {
            log.debug("No rollback actions for context: {}", contextId);
            return;
        }
        
        log.info("Starting rollback for context: {}", contextId);
        
        while (!stack.isEmpty()) {
            RollbackAction action = stack.pop();
            try {
                log.debug("Executing rollback: {}", action.getDescription());
                action.execute();
            } catch (Exception e) {
                log.error("Rollback action failed: {}", action.getDescription(), e);
            }
        }
        
        rollbackStacks.remove(contextId);
        skillContextMap.values().forEach(contexts -> contexts.remove(contextId));
        log.info("Rollback completed for context: {}", contextId);
    }
    
    public void cleanup(String skillId) {
        Set<String> contexts = skillContextMap.remove(skillId);
        if (contexts != null) {
            for (String contextId : contexts) {
                rollbackStacks.remove(contextId);
            }
            log.debug("Cleaned up rollback data for skill: {}, removed {} contexts", skillId, contexts.size());
        } else {
            log.debug("No rollback data found for skill: {}", skillId);
        }
    }
    
    public void clearContext(String contextId) {
        rollbackStacks.remove(contextId);
        skillContextMap.values().forEach(contexts -> contexts.remove(contextId));
    }
    
    private static class RollbackAction {
        private final String description;
        private final Runnable action;
        
        public RollbackAction(String description, Runnable action) {
            this.description = description;
            this.action = action;
        }
        
        public String getDescription() { return description; }
        
        public void execute() {
            action.run();
        }
    }
}
