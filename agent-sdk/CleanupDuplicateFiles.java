import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CleanupDuplicateFiles {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 清理agent-sdk-core中的重复文件 ===\n");
        
        String coreDir = BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        
        // 删除已迁移到scene-engine的文件
        String[] filesToDelete = {
            // scene/model
            "/core/scene/model/InternalSceneState.java",
            // scene/impl
            "/core/scene/impl/CapabilityInvokerImpl.java",
            // scene/capability
            "/core/scene/capability/RuntimeCapabilityException.java",
            "/core/scene/capability/RuntimeCapabilityConsumer.java",
            "/core/scene/capability/RuntimeCapabilityProvider.java",
            "/core/scene/capability/RuntimeCapabilityStatus.java",
            "/core/scene/capability/RuntimeCapabilityType.java",
            // scene/endpoint
            "/core/scene/endpoint/DefaultEndpointAllocator.java",
            "/core/scene/endpoint/EndpointInfo.java"
        };
        
        int deleted = 0;
        for (String file : filesToDelete) {
            Path path = Paths.get(coreDir + file);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("  ✓ 删除: " + file);
                deleted++;
            }
        }
        
        System.out.println("\n  统计: 删除 " + deleted + " 个文件");
        
        // 清理空目录
        cleanupEmptyDirectories(coreDir);
        
        System.out.println("\n=== 清理完成 ===");
    }
    
    private static void cleanupEmptyDirectories(String baseDir) throws Exception {
        System.out.println("\n【清理空目录】");
        
        String[] dirsToCheck = {
            "/core/scene/model",
            "/core/scene/impl",
            "/core/scene/capability",
            "/core/scene/endpoint",
            "/core/scene"
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
