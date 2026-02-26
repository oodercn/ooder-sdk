package net.ooder.sdk.will;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WillTransformerImpl implements WillTransformer {
    
    private static final Logger log = LoggerFactory.getLogger(WillTransformerImpl.class);
    
    @Override
    public WillTransformResult transform(WillExpression will) {
        if (will == null) {
            return WillTransformResult.failure("Will cannot be null");
        }
        
        WillTransformResult result = WillTransformResult.success();
        result.setWillId(will.getWillId());
        
        try {
            result.setSubGoals(decomposeGoals(will));
            result.setPlans(generatePlans(will));
            result.setResourcePlan(planResources(will));
            result.setRiskAssessment(assessRisks(will));
            
            log.info("Will transformed: {} -> {} sub-goals, {} plans", 
                will.getWillId(), result.getSubGoals().size(), result.getPlans().size());
            
        } catch (Exception e) {
            log.error("Failed to transform will: {}", will.getWillId(), e);
            return WillTransformResult.failure("Transform error: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<String> decomposeGoals(WillExpression will) {
        List<String> subGoals = new ArrayList<>();
        String goal = will.getGoal();
        
        if (goal == null || goal.isEmpty()) {
            return subGoals;
        }
        
        switch (will.getType()) {
            case STRATEGIC:
                subGoals.addAll(decomposeStrategicGoal(goal));
                break;
            case TACTICAL:
                subGoals.addAll(decomposeTacticalGoal(goal));
                break;
            case EXECUTION:
                subGoals.addAll(decomposeExecutionGoal(goal));
                break;
        }
        
        return subGoals;
    }
    
    private List<String> decomposeStrategicGoal(String goal) {
        List<String> subGoals = new ArrayList<>();
        
        subGoals.add("分析现状和目标差距");
        subGoals.add("制定实施路线图");
        subGoals.add("分配资源和责任");
        subGoals.add("建立监控机制");
        subGoals.add("阶段性评估和调整");
        
        if (goal.contains("效率")) {
            subGoals.add("识别效率瓶颈");
            subGoals.add("优化关键流程");
        }
        if (goal.contains("成本")) {
            subGoals.add("成本结构分析");
            subGoals.add("成本优化方案");
        }
        
        return subGoals;
    }
    
    private List<String> decomposeTacticalGoal(String goal) {
        List<String> subGoals = new ArrayList<>();
        
        subGoals.add("明确具体目标");
        subGoals.add("识别关键步骤");
        subGoals.add("确定资源需求");
        subGoals.add("制定时间计划");
        subGoals.add("执行和监控");
        
        return subGoals;
    }
    
    private List<String> decomposeExecutionGoal(String goal) {
        List<String> subGoals = new ArrayList<>();
        
        subGoals.add("准备执行环境");
        subGoals.add("执行具体操作");
        subGoals.add("验证执行结果");
        
        return subGoals;
    }
    
    @Override
    public List<ExecutionPlan> generatePlans(WillExpression will) {
        List<ExecutionPlan> plans = new ArrayList<>();
        
        ExecutionPlan plan = new ExecutionPlan();
        plan.setPlanId("plan-" + will.getWillId());
        plan.setName("执行计划: " + (will.getGoal() != null ? will.getGoal() : will.getAction()));
        plan.setDescription("为意志 " + will.getWillId() + " 生成的执行计划");
        plan.setPriority(will.getPriority());
        plan.setEstimatedDuration(estimateDuration(will));
        plan.setSteps(generateSteps(will));
        plan.setParameters(new HashMap<>());
        
        plans.add(plan);
        
        if (will.getType() == WillExpression.WillType.STRATEGIC) {
            ExecutionPlan backupPlan = new ExecutionPlan();
            backupPlan.setPlanId("plan-" + will.getWillId() + "-backup");
            backupPlan.setName("备选方案");
            backupPlan.setDescription("备选执行方案");
            backupPlan.setPriority(will.getPriority() - 1);
            backupPlan.setSteps(new ArrayList<>());
            plans.add(backupPlan);
        }
        
        return plans;
    }
    
    private List<ExecutionStep> generateSteps(WillExpression will) {
        List<ExecutionStep> steps = new ArrayList<>();
        int stepNum = 1;
        
        ExecutionStep initStep = new ExecutionStep();
        initStep.setStepId("step-" + stepNum++);
        initStep.setName("初始化");
        initStep.setDescription("准备执行环境和资源");
        initStep.setAction("initialize");
        initStep.setDependencies(new ArrayList<>());
        steps.add(initStep);
        
        if (will.getAction() != null) {
            ExecutionStep actionStep = new ExecutionStep();
            actionStep.setStepId("step-" + stepNum++);
            actionStep.setName("执行操作");
            actionStep.setDescription("执行: " + will.getAction());
            actionStep.setAction(will.getAction());
            actionStep.setTarget(will.getObject());
            actionStep.setDependencies(Collections.singletonList(initStep.getStepId()));
            steps.add(actionStep);
        }
        
        ExecutionStep verifyStep = new ExecutionStep();
        verifyStep.setStepId("step-" + stepNum);
        verifyStep.setName("验证结果");
        verifyStep.setDescription("验证执行结果是否符合预期");
        verifyStep.setAction("verify");
        verifyStep.setDependencies(Collections.singletonList("step-" + (stepNum - 1)));
        steps.add(verifyStep);
        
        return steps;
    }
    
    private int estimateDuration(WillExpression will) {
        switch (will.getType()) {
            case STRATEGIC:
                return 30 * 24 * 60;
            case TACTICAL:
                return 7 * 24 * 60;
            case EXECUTION:
            default:
                return 24 * 60;
        }
    }
    
    @Override
    public ResourcePlan planResources(WillExpression will) {
        ResourcePlan plan = new ResourcePlan();
        List<ResourceRequirement> resources = new ArrayList<>();
        
        ResourceRequirement humanResource = new ResourceRequirement();
        humanResource.setResourceId("res-human-1");
        humanResource.setType("HUMAN");
        humanResource.setName("执行人员");
        humanResource.setQuantity(1);
        humanResource.setUnit("人");
        resources.add(humanResource);
        
        ResourceRequirement timeResource = new ResourceRequirement();
        timeResource.setResourceId("res-time-1");
        timeResource.setType("TIME");
        timeResource.setName("执行时间");
        timeResource.setQuantity(estimateDuration(will));
        timeResource.setUnit("分钟");
        resources.add(timeResource);
        
        if (will.getType() == WillExpression.WillType.STRATEGIC) {
            ResourceRequirement budgetResource = new ResourceRequirement();
            budgetResource.setResourceId("res-budget-1");
            budgetResource.setType("BUDGET");
            budgetResource.setName("预算资源");
            budgetResource.setQuantity(10000);
            budgetResource.setUnit("元");
            resources.add(budgetResource);
        }
        
        plan.setResources(resources);
        plan.setAvailability("资源可用");
        plan.setTotalCost(calculateTotalCost(resources));
        
        return plan;
    }
    
    private int calculateTotalCost(List<ResourceRequirement> resources) {
        return resources.stream()
            .filter(r -> "BUDGET".equals(r.getType()))
            .mapToInt(ResourceRequirement::getQuantity)
            .sum();
    }
    
    @Override
    public RiskAssessment assessRisks(WillExpression will) {
        RiskAssessment assessment = new RiskAssessment();
        List<RiskItem> risks = new ArrayList<>();
        
        RiskItem timeRisk = new RiskItem();
        timeRisk.setRiskId("risk-1");
        timeRisk.setName("时间风险");
        timeRisk.setDescription("可能无法在规定时间内完成");
        timeRisk.setProbability(3);
        timeRisk.setImpact(4);
        timeRisk.setMitigation("制定详细时间计划，预留缓冲时间");
        risks.add(timeRisk);
        
        RiskItem resourceRisk = new RiskItem();
        resourceRisk.setRiskId("risk-2");
        resourceRisk.setName("资源风险");
        resourceRisk.setDescription("资源可能不足或不可用");
        resourceRisk.setProbability(2);
        resourceRisk.setImpact(3);
        resourceRisk.setMitigation("提前确认资源可用性，准备备选方案");
        risks.add(resourceRisk);
        
        if (will.getType() == WillExpression.WillType.STRATEGIC) {
            RiskItem scopeRisk = new RiskItem();
            scopeRisk.setRiskId("risk-3");
            scopeRisk.setName("范围蔓延风险");
            scopeRisk.setDescription("战略目标可能在执行过程中扩大");
            scopeRisk.setProbability(4);
            scopeRisk.setImpact(3);
            scopeRisk.setMitigation("明确目标边界，建立变更控制机制");
            risks.add(scopeRisk);
        }
        
        assessment.setRisks(risks);
        assessment.setOverallRiskLevel(calculateOverallRisk(risks));
        assessment.setRecommendation("建议采取风险缓解措施后执行");
        
        return assessment;
    }
    
    private int calculateOverallRisk(List<RiskItem> risks) {
        return risks.stream()
            .mapToInt(r -> r.getProbability() * r.getImpact())
            .max()
            .orElse(0);
    }
}
