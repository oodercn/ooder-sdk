import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FixMissingFiles {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 修复缺失文件 ===\n");
        
        // 1. 创建简化的Capability接口在agent-sdk-api中
        createCapabilityInterface();
        
        // 2. 迁移SceneManager接口
        migrateSceneManager();
        
        // 3. 迁移SceneGroupManager接口
        migrateSceneGroupManager();
        
        // 4. 迁移SceneDefinition类
        migrateSceneDefinition();
        
        // 5. 创建SceneLifecycleListener接口
        createSceneLifecycleListener();
        
        // 6. 创建InternalSceneState类
        createInternalSceneState();
        
        System.out.println("\n=== 修复完成 ===");
    }
    
    private static void createCapabilityInterface() throws Exception {
        System.out.println("【1】创建Capability接口...");
        
        String content = "package net.ooder.sdk.api.capability;\n\n" +
            "import java.util.Map;\n\n" +
            "public interface Capability {\n" +
            "    String getCapId();\n" +
            "    void setCapId(String capId);\n" +
            "    String getName();\n" +
            "    void setName(String name);\n" +
            "    String getType();\n" +
            "    void setType(String type);\n" +
            "    String getVersion();\n" +
            "    void setVersion(String version);\n" +
            "    Map<String, Object> getConfig();\n" +
            "    void setConfig(Map<String, Object> config);\n" +
            "    String getStatus();\n" +
            "    void setStatus(String status);\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/agent-sdk-api/src/main/java/net/ooder/sdk/api/capability/Capability.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ Capability接口已创建");
    }
    
    private static void migrateSceneManager() throws Exception {
        System.out.println("\n【2】迁移SceneManager接口...");
        
        Path source = Paths.get(BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk/api/scene/SceneManager.java");
        Path target = Paths.get(BASE_DIR + "/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneManager.java");
        
        if (Files.exists(source)) {
            String content = new String(Files.readAllBytes(source), "UTF-8");
            // 修改import，使用本地的Capability
            content = content.replace("import net.ooder.skills.api.Capability;", "import net.ooder.sdk.api.capability.Capability;");
            Files.write(target, content.getBytes("UTF-8"));
            Files.delete(source);
            System.out.println("  ✓ SceneManager已迁移");
        }
    }
    
    private static void migrateSceneGroupManager() throws Exception {
        System.out.println("\n【3】迁移SceneGroupManager接口...");
        
        Path source = Paths.get(BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk/api/scene/SceneGroupManager.java");
        Path target = Paths.get(BASE_DIR + "/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneGroupManager.java");
        
        if (Files.exists(source)) {
            String content = new String(Files.readAllBytes(source), "UTF-8");
            Files.write(target, content.getBytes("UTF-8"));
            Files.delete(source);
            System.out.println("  ✓ SceneGroupManager已迁移");
        } else {
            System.out.println("  ⚠ SceneGroupManager未找到，创建新接口...");
            String content = "package net.ooder.sdk.api.scene;\n\n" +
                "import java.util.List;\n" +
                "import java.util.concurrent.CompletableFuture;\n\n" +
                "public interface SceneGroupManager {\n" +
                "    CompletableFuture<SceneGroup> createGroup(String sceneId, String groupId, SceneGroup group);\n" +
                "    CompletableFuture<Void> deleteGroup(String sceneId, String groupId);\n" +
                "    CompletableFuture<SceneGroup> getGroup(String sceneId, String groupId);\n" +
                "    CompletableFuture<List<SceneGroup>> listGroups(String sceneId);\n" +
                "    CompletableFuture<Void> addMember(String sceneId, String groupId, SceneMember member);\n" +
                "    CompletableFuture<Void> removeMember(String sceneId, String groupId, String memberId);\n" +
                "    CompletableFuture<List<SceneMember>> listMembers(String sceneId, String groupId);\n" +
                "    CompletableFuture<Void> activateGroup(String sceneId, String groupId);\n" +
                "    CompletableFuture<Void> deactivateGroup(String sceneId, String groupId);\n" +
                "}\n";
            Files.write(target, content.getBytes("UTF-8"));
            System.out.println("  ✓ SceneGroupManager已创建");
        }
    }
    
    private static void migrateSceneDefinition() throws Exception {
        System.out.println("\n【4】迁移SceneDefinition类...");
        
        Path source = Paths.get(BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk/api/scene/SceneDefinition.java");
        Path target = Paths.get(BASE_DIR + "/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneDefinition.java");
        
        if (Files.exists(source)) {
            String content = new String(Files.readAllBytes(source), "UTF-8");
            // 修改import
            content = content.replace("import net.ooder.skills.api.Capability;", "import net.ooder.sdk.api.capability.Capability;");
            Files.write(target, content.getBytes("UTF-8"));
            Files.delete(source);
            System.out.println("  ✓ SceneDefinition已迁移");
        }
    }
    
    private static void createSceneLifecycleListener() throws Exception {
        System.out.println("\n【5】创建SceneLifecycleListener接口...");
        
        String content = "package net.ooder.sdk.api.scene;\n\n" +
            "public interface SceneLifecycleListener {\n" +
            "    void onSceneCreated(String sceneId, SceneDefinition definition);\n" +
            "    void onSceneActivated(String sceneId);\n" +
            "    void onSceneDeactivated(String sceneId);\n" +
            "    void onSceneDeleted(String sceneId);\n" +
            "    void onSceneError(String sceneId, Throwable error);\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/agent-sdk-api/src/main/java/net/ooder/sdk/api/scene/SceneLifecycleListener.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ SceneLifecycleListener已创建");
    }
    
    private static void createInternalSceneState() throws Exception {
        System.out.println("\n【6】创建InternalSceneState类...");
        
        String content = "package net.ooder.engine.scene.core;\n\n" +
            "import net.ooder.sdk.api.scene.SceneDefinition;\n" +
            "import net.ooder.sdk.api.scene.model.SceneState;\n\n" +
            "import java.util.Map;\n" +
            "import java.util.concurrent.ConcurrentHashMap;\n\n" +
            "public class InternalSceneState {\n" +
            "    private String sceneId;\n" +
            "    private SceneDefinition definition;\n" +
            "    private SceneState state;\n" +
            "    private Map<String, Object> runtimeData;\n" +
            "    private long createdAt;\n" +
            "    private long activatedAt;\n\n" +
            "    public InternalSceneState(String sceneId, SceneDefinition definition) {\n" +
            "        this.sceneId = sceneId;\n" +
            "        this.definition = definition;\n" +
            "        this.state = SceneState.CREATED;\n" +
            "        this.runtimeData = new ConcurrentHashMap<>();\n" +
            "        this.createdAt = System.currentTimeMillis();\n" +
            "    }\n\n" +
            "    public String getSceneId() { return sceneId; }\n" +
            "    public void setSceneId(String sceneId) { this.sceneId = sceneId; }\n" +
            "    public SceneDefinition getDefinition() { return definition; }\n" +
            "    public void setDefinition(SceneDefinition definition) { this.definition = definition; }\n" +
            "    public SceneState getState() { return state; }\n" +
            "    public void setState(SceneState state) { this.state = state; }\n" +
            "    public Map<String, Object> getRuntimeData() { return runtimeData; }\n" +
            "    public void setRuntimeData(Map<String, Object> runtimeData) { this.runtimeData = runtimeData; }\n" +
            "    public long getCreatedAt() { return createdAt; }\n" +
            "    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }\n" +
            "    public long getActivatedAt() { return activatedAt; }\n" +
            "    public void setActivatedAt(long activatedAt) { this.activatedAt = activatedAt; }\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/core/InternalSceneState.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ InternalSceneState已创建");
    }
}
