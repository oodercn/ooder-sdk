package net.ooder.sdk.llm.memory;

import net.ooder.sdk.llm.memory.model.BridgeResult;
import net.ooder.sdk.llm.memory.model.MemoryContent;
import net.ooder.sdk.llm.memory.model.MemoryQuery;
import net.ooder.sdk.llm.memory.model.MemoryUpdate;
import net.ooder.sdk.llm.memory.model.ShareRequest;
import net.ooder.sdk.llm.memory.model.ShareResult;
import net.ooder.sdk.llm.memory.model.SyncRequest;
import net.ooder.sdk.llm.memory.model.SyncResult;
import net.ooder.sdk.llm.memory.model.UpdateResult;

public interface MemoryBridgeApi {
    
    BridgeResult bridgeToAgentMemory(String agentId);
    
    SyncResult syncMemoryContext(SyncRequest request);
    
    ShareResult shareMemoryAcrossAgents(ShareRequest request);
    
    MemoryContent queryMemory(MemoryQuery query);
    
    UpdateResult updateMemory(MemoryUpdate update);
}
