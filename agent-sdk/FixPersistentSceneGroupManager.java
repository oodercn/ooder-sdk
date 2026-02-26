import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FixPersistentSceneGroupManager {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== 修复PersistentSceneGroupManagerImpl ===\n");
        
        Path file = Paths.get(BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene/core/PersistentSceneGroupManagerImpl.java");
        
        String content = new String(Files.readAllBytes(file), "UTF-8");
        
        // 1. 修复import语句
        content = content.replace(
            "import net.ooder.engine.scene.group.net.ooder.sdk.api.scene.SceneGroupManager.KeyShareManager;",
            "import net.ooder.engine.scene.group.KeyShareManager;");
        
        // 2. 修复字段声明
        content = content.replace(
            "private final net.ooder.sdk.api.scene.SceneGroupManager.KeyShareManager keyShareManager;",
            "private final KeyShareManager keyShareManager;");
        
        // 3. 修复构造函数中的实例化
        content = content.replace(
            "this.keyShareManager = new net.ooder.sdk.api.scene.SceneGroupManager.KeyShareManager();",
            "this.keyShareManager = new KeyShareManager();");
        
        // 4. 修复方法参数类型
        content = content.replace(
            "net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig",
            "SceneGroupConfig");
        
        // 5. 修复方法名 - 这是最关键的
        content = content.replace(
            "getnet.ooder.sdk.api.scene.SceneGroupManager.FailoverStatus",
            "getFailoverStatus");
        content = content.replace(
            "distributenet.ooder.sdk.api.scene.SceneGroupManager.KeyShares",
            "distributeKeyShares");
        content = content.replace(
            "getnet.ooder.sdk.api.scene.SceneGroupManager.VfsPermission",
            "getVfsPermission");
        
        // 6. 修复内部类使用
        content = content.replace(
            "new net.ooder.sdk.api.scene.SceneGroupManager.FailoverStatus()",
            "new FailoverStatus()");
        content = content.replace(
            "new net.ooder.sdk.api.scene.SceneGroupManager.VfsPermission()",
            "new VfsPermission()");
        
        // 7. 修复List参数类型
        content = content.replace(
            "List<net.ooder.sdk.api.scene.SceneGroupManager.KeyShare>",
            "List<KeyShare>");
        
        // 8. 修复for循环中的类型
        content = content.replace(
            "for (net.ooder.sdk.api.scene.SceneGroupManager.KeyShare share : shares)",
            "for (KeyShare share : shares)");
        
        // 9. 修复SceneGroupKey.KeyShare引用
        content = content.replace(
            "SceneGroupKey.net.ooder.sdk.api.scene.SceneGroupManager.KeyShare",
            "SceneGroupKey.KeyShare");
        
        // 10. 添加内部类导入
        String importSection = "import net.ooder.sdk.common.enums.MemberRole;";
        String newImports = importSection + "\nimport net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig;" +
            "\nimport net.ooder.sdk.api.scene.SceneGroupManager.FailoverStatus;" +
            "\nimport net.ooder.sdk.api.scene.SceneGroupManager.KeyShare;" +
            "\nimport net.ooder.sdk.api.scene.SceneGroupManager.VfsPermission;";
        content = content.replace(importSection, newImports);
        
        Files.write(file, content.getBytes("UTF-8"));
        System.out.println("  ✓ PersistentSceneGroupManagerImpl已修复");
    }
}
