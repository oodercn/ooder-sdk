import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CleanupAgentSdkCore {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 清理agent-sdk-core开始 ===\n");
        
        String coreDir = BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        
        // 定义要删除的文件列表
        List<String> filesToDelete = new ArrayList<>();
        
        // api/scene 下的模型类 (接口保留在api模块)
        filesToDelete.add("/api/scene/SceneGroup.java");
        filesToDelete.add("/api/scene/SceneMember.java");
        filesToDelete.add("/api/scene/SceneSnapshot.java");
        filesToDelete.add("/api/scene/SceneGroupKey.java");
        filesToDelete.add("/api/scene/CapabilityInvoker.java");
        
        // api/scene/model
        filesToDelete.add("/api/scene/model/SceneState.java");
        filesToDelete.add("/api/scene/model/SceneLifecycleStats.java");
        filesToDelete.add("/api/scene/model/SceneConfig.java");
        
        // api/scene/store 下的接口
        filesToDelete.add("/api/scene/store/SceneStore.java");
        filesToDelete.add("/api/scene/store/GroupStore.java");
        filesToDelete.add("/api/scene/store/AgentStore.java");
        filesToDelete.add("/api/scene/store/SkillStore.java");
        filesToDelete.add("/api/scene/store/LinkStore.java");
        filesToDelete.add("/api/scene/store/DualStorage.java");
        filesToDelete.add("/api/scene/store/SyncListener.java");
        filesToDelete.add("/api/scene/store/StorageException.java");
        filesToDelete.add("/api/scene/store/StorageStatus.java");
        filesToDelete.add("/api/scene/store/ConflictInfo.java");
        filesToDelete.add("/api/scene/store/SyncEvent.java");
        filesToDelete.add("/api/scene/store/AgentRegistration.java");
        filesToDelete.add("/api/scene/store/SkillRegistration.java");
        filesToDelete.add("/api/scene/store/LinkConfig.java");
        
        // api/protocol
        filesToDelete.add("/api/protocol/ProtocolHub.java");
        filesToDelete.add("/api/protocol/ProtocolHandler.java");
        
        // api/command
        filesToDelete.add("/api/command/CommandPacket.java");
        filesToDelete.add("/api/command/CommandBuilder.java");
        filesToDelete.add("/api/command/CommandDirection.java");
        filesToDelete.add("/api/command/CommandResult.java");
        filesToDelete.add("/api/command/CommandStatus.java");
        filesToDelete.add("/api/command/BatchCommandResult.java");
        filesToDelete.add("/api/command/CommandTrace.java");
        
        // api/capability
        filesToDelete.add("/api/capability/Capability.java");
        filesToDelete.add("/api/capability/CapAddress.java");
        filesToDelete.add("/api/capability/CapParameter.java");
        filesToDelete.add("/api/capability/CapabilityType.java");
        filesToDelete.add("/api/capability/CapabilityStatus.java");
        
        // api/initializer
        filesToDelete.add("/api/initializer/NexusInitializer.java");
        
        // api/llm (已迁移到llm-sdk)
        filesToDelete.add("/api/llm/LlmService.java");
        filesToDelete.add("/api/llm/ChatRequest.java");
        filesToDelete.add("/api/llm/LlmConfig.java");
        filesToDelete.add("/api/llm/FunctionDef.java");
        filesToDelete.add("/api/llm/TokenUsage.java");
        filesToDelete.add("/api/llm/impl/LlmServiceImpl.java");
        
        // common/enums
        filesToDelete.add("/common/enums/MemberRole.java");
        filesToDelete.add("/common/enums/SceneType.java");
        filesToDelete.add("/common/enums/AgentType.java");
        filesToDelete.add("/common/enums/DiscoveryMethod.java");
        filesToDelete.add("/common/enums/SkillStatus.java");
        
        // common/constants
        filesToDelete.add("/common/constants/SDKConstants.java");
        filesToDelete.add("/common/constants/ErrorCodes.java");
        filesToDelete.add("/common/constants/Defaults.java");
        
        // core/scene/impl (已迁移到scene-engine)
        filesToDelete.add("/core/scene/impl/SceneManagerImpl.java");
        filesToDelete.add("/core/scene/impl/PersistentSceneManagerImpl.java");
        filesToDelete.add("/core/scene/impl/SceneGroupManagerImpl.java");
        filesToDelete.add("/core/scene/impl/PersistentSceneGroupManagerImpl.java");
        
        // core/scene/failover (已迁移到scene-engine)
        filesToDelete.add("/core/scene/failover/FailoverManager.java");
        filesToDelete.add("/core/scene/failover/HeartbeatManager.java");
        filesToDelete.add("/core/scene/failover/RoleManager.java");
        
        // core/scene/group (已迁移到scene-engine)
        filesToDelete.add("/core/scene/group/KeyShareManager.java");
        filesToDelete.add("/core/scene/group/MemberManager.java");
        
        // core/scene/store (已迁移到scene-engine)
        filesToDelete.add("/core/scene/store/LocalSceneStore.java");
        filesToDelete.add("/core/scene/store/DualSceneStore.java");
        filesToDelete.add("/core/scene/store/AbstractSceneStore.java");
        
        // core/scene/endpoint (已迁移到scene-engine)
        filesToDelete.add("/core/scene/endpoint/EndpointAllocator.java");
        
        // core/scene/capability (已迁移到scene-engine)
        filesToDelete.add("/core/scene/capability/RuntimeCapabilityRegistry.java");
        filesToDelete.add("/core/scene/capability/RuntimeCapability.java");
        
        // service/scene (已迁移到scene-engine)
        filesToDelete.add("/service/scene/SceneService.java");
        
        int deleted = 0;
        int notFound = 0;
        
        for (String file : filesToDelete) {
            Path path = Paths.get(coreDir + file);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("  ✓ 删除: " + file);
                deleted++;
            } else {
                notFound++;
            }
        }
        
        System.out.println("\n  统计: 成功删除 " + deleted + " 个, 未找到 " + notFound + " 个");
        
        // 清理空目录
        cleanupEmptyDirectories(coreDir);
        
        System.out.println("\n=== 清理完成 ===");
    }
    
    private static void cleanupEmptyDirectories(String baseDir) throws Exception {
        System.out.println("\n【清理空目录】");
        
        String[] dirsToCheck = {
            "/api/scene/model",
            "/api/scene/store",
            "/api/scene",
            "/api/protocol",
            "/api/command",
            "/api/capability",
            "/api/initializer",
            "/api/llm/impl",
            "/api/llm",
            "/common/enums",
            "/common/constants",
            "/common",
            "/core/scene/impl",
            "/core/scene/failover",
            "/core/scene/group",
            "/core/scene/store",
            "/core/scene/endpoint",
            "/core/scene/capability",
            "/core/scene",
            "/service/scene",
            "/service"
        };
        
        for (String dir : dirsToCheck) {
            Path path = Paths.get(baseDir + dir);
            if (Files.exists(path) && Files.isDirectory(path)) {
                try {
                    Files.delete(path);
                    System.out.println("  ✓ 删除空目录: " + dir);
                } catch (DirectoryNotEmptyException e) {
                    // 目录不为空，保留
                }
            }
        }
    }
}
