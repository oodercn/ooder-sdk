package net.ooder.scene.bridge;

import net.ooder.scene.capability.CapabilityBinding;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupEvent;
import net.ooder.scene.knowledge.KnowledgeBindingInfo;
import net.ooder.scene.participant.Participant;
import net.ooder.scene.todo.TodoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景组完整状态
 * 
 * <p>包含场景组的完整状态信息，用于桥接层同步。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneGroupFullState {
    
    private SceneGroup sceneGroup;
    private List<Participant> participants = new ArrayList<>();
    private List<CapabilityBinding> capabilityBindings = new ArrayList<>();
    private List<KnowledgeBindingInfo> knowledgeBindings = new ArrayList<>();
    private List<TodoDTO> pendingTodos = new ArrayList<>();
    private List<SceneGroupEvent> recentEvents = new ArrayList<>();
    private Map<String, Object> businessContext = new HashMap<>();
    private Map<String, Object> workflowState = new HashMap<>();
    
    public SceneGroupFullState() {}
    
    public SceneGroupFullState(SceneGroup sceneGroup) {
        this.sceneGroup = sceneGroup;
        if (sceneGroup != null) {
            this.participants = sceneGroup.getAllParticipants();
            this.capabilityBindings = sceneGroup.getAllCapabilityBindings();
        }
    }
    
    public SceneGroup getSceneGroup() {
        return sceneGroup;
    }
    
    public void setSceneGroup(SceneGroup sceneGroup) {
        this.sceneGroup = sceneGroup;
    }
    
    public List<Participant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<Participant> participants) {
        this.participants = participants != null ? participants : new ArrayList<>();
    }
    
    public List<CapabilityBinding> getCapabilityBindings() {
        return capabilityBindings;
    }
    
    public void setCapabilityBindings(List<CapabilityBinding> capabilityBindings) {
        this.capabilityBindings = capabilityBindings != null ? capabilityBindings : new ArrayList<>();
    }
    
    public List<KnowledgeBindingInfo> getKnowledgeBindings() {
        return knowledgeBindings;
    }
    
    public void setKnowledgeBindings(List<KnowledgeBindingInfo> knowledgeBindings) {
        this.knowledgeBindings = knowledgeBindings != null ? knowledgeBindings : new ArrayList<>();
    }
    
    public List<TodoDTO> getPendingTodos() {
        return pendingTodos;
    }
    
    public void setPendingTodos(List<TodoDTO> pendingTodos) {
        this.pendingTodos = pendingTodos != null ? pendingTodos : new ArrayList<>();
    }
    
    public List<SceneGroupEvent> getRecentEvents() {
        return recentEvents;
    }
    
    public void setRecentEvents(List<SceneGroupEvent> recentEvents) {
        this.recentEvents = recentEvents != null ? recentEvents : new ArrayList<>();
    }
    
    public Map<String, Object> getBusinessContext() {
        return businessContext;
    }
    
    public void setBusinessContext(Map<String, Object> businessContext) {
        this.businessContext = businessContext != null ? businessContext : new HashMap<>();
    }
    
    public Map<String, Object> getWorkflowState() {
        return workflowState;
    }
    
    public void setWorkflowState(Map<String, Object> workflowState) {
        this.workflowState = workflowState != null ? workflowState : new HashMap<>();
    }
    
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }
    
    public void addCapabilityBinding(CapabilityBinding binding) {
        this.capabilityBindings.add(binding);
    }
    
    public void addKnowledgeBinding(KnowledgeBindingInfo binding) {
        this.knowledgeBindings.add(binding);
    }
    
    public void addPendingTodo(TodoDTO todo) {
        this.pendingTodos.add(todo);
    }
    
    public void addRecentEvent(SceneGroupEvent event) {
        this.recentEvents.add(event);
    }
    
    public void addBusinessContext(String key, Object value) {
        this.businessContext.put(key, value);
    }
    
    public void addWorkflowState(String key, Object value) {
        this.workflowState.put(key, value);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final SceneGroupFullState state = new SceneGroupFullState();
        
        public Builder sceneGroup(SceneGroup sceneGroup) {
            state.setSceneGroup(sceneGroup);
            return this;
        }
        
        public Builder participants(List<Participant> participants) {
            state.setParticipants(participants);
            return this;
        }
        
        public Builder addParticipant(Participant participant) {
            state.addParticipant(participant);
            return this;
        }
        
        public Builder capabilityBindings(List<CapabilityBinding> capabilityBindings) {
            state.setCapabilityBindings(capabilityBindings);
            return this;
        }
        
        public Builder addCapabilityBinding(CapabilityBinding binding) {
            state.addCapabilityBinding(binding);
            return this;
        }
        
        public Builder knowledgeBindings(List<KnowledgeBindingInfo> knowledgeBindings) {
            state.setKnowledgeBindings(knowledgeBindings);
            return this;
        }
        
        public Builder addKnowledgeBinding(KnowledgeBindingInfo binding) {
            state.addKnowledgeBinding(binding);
            return this;
        }
        
        public Builder pendingTodos(List<TodoDTO> pendingTodos) {
            state.setPendingTodos(pendingTodos);
            return this;
        }
        
        public Builder addPendingTodo(TodoDTO todo) {
            state.addPendingTodo(todo);
            return this;
        }
        
        public Builder recentEvents(List<SceneGroupEvent> recentEvents) {
            state.setRecentEvents(recentEvents);
            return this;
        }
        
        public Builder addRecentEvent(SceneGroupEvent event) {
            state.addRecentEvent(event);
            return this;
        }
        
        public Builder businessContext(Map<String, Object> businessContext) {
            state.setBusinessContext(businessContext);
            return this;
        }
        
        public Builder addBusinessContext(String key, Object value) {
            state.addBusinessContext(key, value);
            return this;
        }
        
        public Builder workflowState(Map<String, Object> workflowState) {
            state.setWorkflowState(workflowState);
            return this;
        }
        
        public Builder addWorkflowState(String key, Object value) {
            state.addWorkflowState(key, value);
            return this;
        }
        
        public SceneGroupFullState build() {
            return state;
        }
    }
    
    @Override
    public String toString() {
        return "SceneGroupFullState{" +
                "sceneGroupId='" + (sceneGroup != null ? sceneGroup.getSceneGroupId() : "null") + '\'' +
                ", participantCount=" + participants.size() +
                ", capabilityBindingCount=" + capabilityBindings.size() +
                ", knowledgeBindingCount=" + knowledgeBindings.size() +
                ", pendingTodoCount=" + pendingTodos.size() +
                ", recentEventCount=" + recentEvents.size() +
                '}';
    }
}
