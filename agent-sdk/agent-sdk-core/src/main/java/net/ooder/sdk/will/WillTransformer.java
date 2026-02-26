package net.ooder.sdk.will;

import java.util.List;
import java.util.Map;

public interface WillTransformer {
    
    WillTransformResult transform(WillExpression will);
    
    List<String> decomposeGoals(WillExpression will);
    
    List<ExecutionPlan> generatePlans(WillExpression will);
    
    ResourcePlan planResources(WillExpression will);
    
    RiskAssessment assessRisks(WillExpression will);
    
    class WillTransformResult {
        private String willId;
        private List<String> subGoals;
        private List<ExecutionPlan> plans;
        private ResourcePlan resourcePlan;
        private RiskAssessment riskAssessment;
        private boolean success;
        private String errorMessage;
        
        public String getWillId() { return willId; }
        public void setWillId(String willId) { this.willId = willId; }
        
        public List<String> getSubGoals() { return subGoals; }
        public void setSubGoals(List<String> subGoals) { this.subGoals = subGoals; }
        
        public List<ExecutionPlan> getPlans() { return plans; }
        public void setPlans(List<ExecutionPlan> plans) { this.plans = plans; }
        
        public ResourcePlan getResourcePlan() { return resourcePlan; }
        public void setResourcePlan(ResourcePlan resourcePlan) { this.resourcePlan = resourcePlan; }
        
        public RiskAssessment getRiskAssessment() { return riskAssessment; }
        public void setRiskAssessment(RiskAssessment riskAssessment) { this.riskAssessment = riskAssessment; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public static WillTransformResult success() {
            WillTransformResult result = new WillTransformResult();
            result.setSuccess(true);
            return result;
        }
        
        public static WillTransformResult failure(String errorMessage) {
            WillTransformResult result = new WillTransformResult();
            result.setSuccess(false);
            result.setErrorMessage(errorMessage);
            return result;
        }
    }
    
    class ExecutionPlan {
        private String planId;
        private String name;
        private String description;
        private List<ExecutionStep> steps;
        private Map<String, Object> parameters;
        private int estimatedDuration;
        private int priority;
        
        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<ExecutionStep> getSteps() { return steps; }
        public void setSteps(List<ExecutionStep> steps) { this.steps = steps; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public int getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(int estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
    
    class ExecutionStep {
        private String stepId;
        private String name;
        private String description;
        private String action;
        private String target;
        private List<String> dependencies;
        private Map<String, Object> params;
        
        public String getStepId() { return stepId; }
        public void setStepId(String stepId) { this.stepId = stepId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    class ResourcePlan {
        private List<ResourceRequirement> resources;
        private int totalCost;
        private String availability;
        
        public List<ResourceRequirement> getResources() { return resources; }
        public void setResources(List<ResourceRequirement> resources) { this.resources = resources; }
        
        public int getTotalCost() { return totalCost; }
        public void setTotalCost(int totalCost) { this.totalCost = totalCost; }
        
        public String getAvailability() { return availability; }
        public void setAvailability(String availability) { this.availability = availability; }
    }
    
    class ResourceRequirement {
        private String resourceId;
        private String type;
        private String name;
        private int quantity;
        private String unit;
        
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
    
    class RiskAssessment {
        private List<RiskItem> risks;
        private int overallRiskLevel;
        private String recommendation;
        
        public List<RiskItem> getRisks() { return risks; }
        public void setRisks(List<RiskItem> risks) { this.risks = risks; }
        
        public int getOverallRiskLevel() { return overallRiskLevel; }
        public void setOverallRiskLevel(int overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    class RiskItem {
        private String riskId;
        private String name;
        private String description;
        private int probability;
        private int impact;
        private String mitigation;
        
        public String getRiskId() { return riskId; }
        public void setRiskId(String riskId) { this.riskId = riskId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public int getProbability() { return probability; }
        public void setProbability(int probability) { this.probability = probability; }
        
        public int getImpact() { return impact; }
        public void setImpact(int impact) { this.impact = impact; }
        
        public String getMitigation() { return mitigation; }
        public void setMitigation(String mitigation) { this.mitigation = mitigation; }
    }
}
