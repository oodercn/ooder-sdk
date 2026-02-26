import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FixSceneEngine {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 修复Scene Engine编译错误 ===\n");
        
        // 1. 创建缺失的枚举和类
        createRuntimeCapabilityType();
        createRuntimeCapabilityStatus();
        createRuntimeCapabilityProvider();
        createRuntimeCapabilityConsumer();
        createEndpointInfo();
        
        // 2. 修复import语句
        fixImports();
        
        System.out.println("\n=== 修复完成 ===");
    }
    
    private static void createRuntimeCapabilityType() throws Exception {
        System.out.println("【1】创建RuntimeCapabilityType枚举...");
        
        String content = "package net.ooder.engine.scene.capability;\n\n" +
            "public enum RuntimeCapabilityType {\n" +
            "    COMPUTATION,\n" +
            "    STORAGE,\n" +
            "    NETWORK,\n" +
            "    SECURITY,\n" +
            "    UI,\n" +
            "    INTEGRATION\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/capability/RuntimeCapabilityType.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ RuntimeCapabilityType已创建");
    }
    
    private static void createRuntimeCapabilityStatus() throws Exception {
        System.out.println("【2】创建RuntimeCapabilityStatus枚举...");
        
        String content = "package net.ooder.engine.scene.capability;\n\n" +
            "public enum RuntimeCapabilityStatus {\n" +
            "    ACTIVE,\n" +
            "    INACTIVE,\n" +
            "    ERROR,\n" +
            "    INITIALIZING,\n" +
            "    SHUTTING_DOWN\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/capability/RuntimeCapabilityStatus.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ RuntimeCapabilityStatus已创建");
    }
    
    private static void createRuntimeCapabilityProvider() throws Exception {
        System.out.println("【3】创建RuntimeCapabilityProvider接口...");
        
        String content = "package net.ooder.engine.scene.capability;\n\n" +
            "import java.util.Map;\n\n" +
            "public interface RuntimeCapabilityProvider {\n" +
            "    String getProviderId();\n" +
            "    String getProviderType();\n" +
            "    Map<String, Object> getCapabilities();\n" +
            "    boolean isAvailable();\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/capability/RuntimeCapabilityProvider.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ RuntimeCapabilityProvider已创建");
    }
    
    private static void createRuntimeCapabilityConsumer() throws Exception {
        System.out.println("【4】创建RuntimeCapabilityConsumer接口...");
        
        String content = "package net.ooder.engine.scene.capability;\n\n" +
            "import java.util.Map;\n\n" +
            "public interface RuntimeCapabilityConsumer {\n" +
            "    String getConsumerId();\n" +
            "    String getConsumerType();\n" +
            "    Map<String, Object> getRequirements();\n" +
            "    void onCapabilityAvailable(RuntimeCapability capability);\n" +
            "    void onCapabilityUnavailable(String capId);\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/capability/RuntimeCapabilityConsumer.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ RuntimeCapabilityConsumer已创建");
    }
    
    private static void createEndpointInfo() throws Exception {
        System.out.println("【5】创建EndpointInfo类...");
        
        String content = "package net.ooder.engine.scene.endpoint;\n\n" +
            "public class EndpointInfo {\n" +
            "    private String endpointId;\n" +
            "    private String endpointType;\n" +
            "    private String host;\n" +
            "    private int port;\n" +
            "    private String protocol;\n" +
            "    private String status;\n\n" +
            "    public String getEndpointId() { return endpointId; }\n" +
            "    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }\n" +
            "    public String getEndpointType() { return endpointType; }\n" +
            "    public void setEndpointType(String endpointType) { this.endpointType = endpointType; }\n" +
            "    public String getHost() { return host; }\n" +
            "    public void setHost(String host) { this.host = host; }\n" +
            "    public int getPort() { return port; }\n" +
            "    public void setPort(int port) { this.port = port; }\n" +
            "    public String getProtocol() { return protocol; }\n" +
            "    public void setProtocol(String protocol) { this.protocol = protocol; }\n" +
            "    public String getStatus() { return status; }\n" +
            "    public void setStatus(String status) { this.status = status; }\n" +
            "}\n";
        
        Path target = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/endpoint/EndpointInfo.java");
        Files.write(target, content.getBytes("UTF-8"));
        System.out.println("  ✓ EndpointInfo已创建");
    }
    
    private static void fixImports() throws Exception {
        System.out.println("\n【6】修复import语句...");
        
        String sceneEngineDir = BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene";
        
        // 修复PersistentSceneManagerImpl.java
        Path persistentSceneManager = Paths.get(sceneEngineDir + "/core/PersistentSceneManagerImpl.java");
        if (Files.exists(persistentSceneManager)) {
            String content = new String(Files.readAllBytes(persistentSceneManager), "UTF-8");
            content = content.replace("import net.ooder.sdk.core.scene.model.SceneConfig;", 
                "import net.ooder.sdk.api.scene.model.SceneConfig;");
            Files.write(persistentSceneManager, content.getBytes("UTF-8"));
            System.out.println("  ✓ PersistentSceneManagerImpl import已修复");
        }
        
        // 修复SceneGroupManagerImpl.java
        Path sceneGroupManagerImpl = Paths.get(sceneEngineDir + "/core/SceneGroupManagerImpl.java");
        if (Files.exists(sceneGroupManagerImpl)) {
            String content = new String(Files.readAllBytes(sceneGroupManagerImpl), "UTF-8");
            content = content.replace("import net.ooder.sdk.infra.exception.SceneException;", 
                "// import net.ooder.sdk.infra.exception.SceneException;");
            content = content.replace("throw new SceneException", "throw new RuntimeException");
            Files.write(sceneGroupManagerImpl, content.getBytes("UTF-8"));
            System.out.println("  ✓ SceneGroupManagerImpl import已修复");
        }
        
        // 修复PersistentSceneGroupManagerImpl.java - 添加缺失的内部类引用
        Path persistentSceneGroupManager = Paths.get(sceneEngineDir + "/core/PersistentSceneGroupManagerImpl.java");
        if (Files.exists(persistentSceneGroupManager)) {
            String content = new String(Files.readAllBytes(persistentSceneGroupManager), "UTF-8");
            
            // 修复SceneGroupManager引用
            content = content.replace("implements SceneGroupManager", 
                "implements net.ooder.sdk.api.scene.SceneGroupManager");
            
            // 修复内部类引用
            content = content.replace("SceneGroupConfig", 
                "net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig");
            content = content.replace("FailoverStatus", 
                "net.ooder.sdk.api.scene.SceneGroupManager.FailoverStatus");
            content = content.replace("KeyShare", 
                "net.ooder.sdk.api.scene.SceneGroupManager.KeyShare");
            content = content.replace("VfsPermission", 
                "net.ooder.sdk.api.scene.SceneGroupManager.VfsPermission");
            
            Files.write(persistentSceneGroupManager, content.getBytes("UTF-8"));
            System.out.println("  ✓ PersistentSceneGroupManagerImpl import已修复");
        }
    }
}
