package net.ooder.sdk.llm.capability;

import net.ooder.sdk.llm.capability.model.BatchCapabilityResponse;
import net.ooder.sdk.llm.capability.model.CapabilityRequest;
import net.ooder.sdk.llm.capability.model.CapabilityResponse;
import net.ooder.sdk.llm.capability.model.CapabilityStatus;
import net.ooder.sdk.llm.capability.model.ReleaseResponse;
import net.ooder.sdk.llm.capability.model.ScheduleRequest;
import net.ooder.sdk.llm.capability.model.ScheduleResponse;

import java.util.List;

public interface CapabilityRequestApi {
    
    CapabilityResponse requestLLMCapability(CapabilityRequest request);
    
    CapabilityStatus queryCapabilityStatus(String capabilityId);
    
    ReleaseResponse releaseCapability(String capabilityId);
    
    BatchCapabilityResponse batchRequestCapability(List<CapabilityRequest> requests);
    
    ScheduleResponse scheduleCapability(ScheduleRequest request);
}
