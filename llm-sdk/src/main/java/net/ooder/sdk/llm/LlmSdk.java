package net.ooder.sdk.llm;

import net.ooder.sdk.llm.adapter.MultiLlmAdapterApi;
import net.ooder.sdk.llm.capability.CapabilityRequestApi;
import net.ooder.sdk.llm.memory.MemoryBridgeApi;
import net.ooder.sdk.llm.monitoring.MonitoringApi;
import net.ooder.sdk.llm.nlp.NlpInteractionApi;
import net.ooder.sdk.llm.scheduling.SchedulingApi;
import net.ooder.sdk.llm.security.SecurityApi;

public interface LlmSdk {
    
    CapabilityRequestApi getCapabilityRequestApi();
    
    NlpInteractionApi getNlpInteractionApi();
    
    SchedulingApi getSchedulingApi();
    
    MemoryBridgeApi getMemoryBridgeApi();
    
    MultiLlmAdapterApi getMultiLlmAdapterApi();
    
    SecurityApi getSecurityApi();
    
    MonitoringApi getMonitoringApi();
    
    String getVersion();
    
    void shutdown();
}
