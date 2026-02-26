import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MigrateSdk {
    public static void main(String[] args) throws IOException {
        String baseDir = "E:/github/ooder-sdk/agent-sdk";
        String sourceDir = baseDir + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        String targetDir = baseDir + "/agent-sdk-api/src/main/java/net/ooder/sdk";

        // 创建目录结构
        String[] dirs = {
            "/api/scene", "/api/scene/model", "/api/scene/store",
            "/api/protocol", "/api/command", "/api/capability",
            "/api/initializer", "/common/enums", "/common/constants"
        };

        for (String dir : dirs) {
            Path path = Paths.get(targetDir + dir);
            Files.createDirectories(path);
            System.out.println("Created: " + path);
        }

        // 定义要复制的文件
        Map<String, String[]> filesToCopy = new HashMap<>();

        // common enums
        filesToCopy.put("/common/enums", new String[]{
            "MemberRole.java", "SceneType.java", "AgentType.java",
            "DiscoveryMethod.java", "SkillStatus.java"
        });

        // scene
        filesToCopy.put("/api/scene", new String[]{
            "SceneGroup.java", "SceneMember.java", "SceneSnapshot.java",
            "SceneGroupKey.java", "CapabilityInvoker.java"
        });

        // scene model
        filesToCopy.put("/api/scene/model", new String[]{
            "SceneState.java", "SceneLifecycleStats.java", "SceneConfig.java"
        });

        // scene store
        filesToCopy.put("/api/scene/store", new String[]{
            "SceneStore.java", "GroupStore.java", "AgentStore.java",
            "SkillStore.java", "LinkStore.java", "DualStorage.java",
            "SyncListener.java", "StorageException.java", "StorageStatus.java",
            "ConflictInfo.java", "SyncEvent.java", "AgentRegistration.java",
            "SkillRegistration.java", "LinkConfig.java"
        });

        // protocol
        filesToCopy.put("/api/protocol", new String[]{
            "ProtocolHub.java", "ProtocolHandler.java"
        });

        // command
        filesToCopy.put("/api/command", new String[]{
            "CommandPacket.java", "CommandBuilder.java", "CommandDirection.java",
            "CommandResult.java", "CommandStatus.java", "BatchCommandResult.java",
            "CommandTrace.java"
        });

        // capability
        filesToCopy.put("/api/capability", new String[]{
            "Capability.java", "CapAddress.java", "CapParameter.java",
            "CapabilityType.java", "CapabilityStatus.java"
        });

        // initializer
        filesToCopy.put("/api/initializer", new String[]{
            "NexusInitializer.java"
        });

        // 复制文件
        int copied = 0;
        for (Map.Entry<String, String[]> entry : filesToCopy.entrySet()) {
            String subDir = entry.getKey();
            String[] files = entry.getValue();

            for (String file : files) {
                Path source = Paths.get(sourceDir + subDir + "/" + file);
                Path target = Paths.get(targetDir + subDir + "/" + file);

                if (Files.exists(source)) {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied: " + file);
                    copied++;
                } else {
                    System.out.println("Not found: " + source);
                }
            }
        }

        System.out.println("\nTotal files copied: " + copied);
    }
}
