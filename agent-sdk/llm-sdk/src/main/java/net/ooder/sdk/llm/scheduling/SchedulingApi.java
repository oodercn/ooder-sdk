package net.ooder.sdk.llm.scheduling;

import net.ooder.sdk.llm.scheduling.model.ExecutionStatus;
import net.ooder.sdk.llm.scheduling.model.LoadBalanceRequest;
import net.ooder.sdk.llm.scheduling.model.LoadBalanceResult;
import net.ooder.sdk.llm.scheduling.model.ResourceAssignment;
import net.ooder.sdk.llm.scheduling.model.ResourceRequest;
import net.ooder.sdk.llm.scheduling.model.ScaleRequest;
import net.ooder.sdk.llm.scheduling.model.ScaleResult;
import net.ooder.sdk.llm.scheduling.model.TaskRequest;
import net.ooder.sdk.llm.scheduling.model.TaskScheduleResult;

public interface SchedulingApi {
    
    ResourceAssignment assignLLMResource(ResourceRequest request);
    
    TaskScheduleResult scheduleTask(TaskRequest task);
    
    ExecutionStatus monitorExecution(String taskId);
    
    LoadBalanceResult loadBalance(LoadBalanceRequest request);
    
    ScaleResult scaleResource(ScaleRequest request);
}
