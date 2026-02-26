import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MigrateSceneEngine {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Scene实现类迁移开始 ===\n");
        
        // 1. 创建scene-engine的目录结构
        createDirectoryStructure();
        
        // 2. 迁移Scene实现类
        migrateSceneImpl();
        
        // 3. 更新scene-engine的pom.xml
        updateSceneEnginePom();
        
        System.out.println("\n=== Scene实现类迁移完成 ===");
    }
    
    private static void createDirectoryStructure() throws Exception {
        System.out.println("【1】创建scene-engine目录结构...");
        
        String baseDir = BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene";
        String[] dirs = {
            "/core", "/failover", "/group", "/store", 
            "/endpoint", "/capability", "/service"
        };
        
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(baseDir + dir));
        }
        System.out.println("  ✓ 目录结构创建完成");
    }
    
    private static void migrateSceneImpl() throws Exception {
        System.out.println("\n【2】迁移Scene实现类...");
        
        String sourceBase = BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        String targetBase = BASE_DIR + "/scene-engine/src/main/java/net/ooder/engine/scene";
        
        // 定义迁移映射: 源路径 -> 目标路径, 包名修改
        Map<String, MigrationInfo> migrations = new LinkedHashMap<>();
        
        // core/scene/impl -> scene-engine/core
        migrations.put("/core/scene/impl/SceneManagerImpl.java", 
            new MigrationInfo("/core/SceneManagerImpl.java", "net.ooder.sdk.core.scene.impl", "net.ooder.engine.scene.core"));
        migrations.put("/core/scene/impl/PersistentSceneManagerImpl.java", 
            new MigrationInfo("/core/PersistentSceneManagerImpl.java", "net.ooder.sdk.core.scene.impl", "net.ooder.engine.scene.core"));
        migrations.put("/core/scene/impl/SceneGroupManagerImpl.java", 
            new MigrationInfo("/core/SceneGroupManagerImpl.java", "net.ooder.sdk.core.scene.impl", "net.ooder.engine.scene.core"));
        migrations.put("/core/scene/impl/PersistentSceneGroupManagerImpl.java", 
            new MigrationInfo("/core/PersistentSceneGroupManagerImpl.java", "net.ooder.sdk.core.scene.impl", "net.ooder.engine.scene.core"));
        
        // core/scene/failover -> scene-engine/failover
        migrations.put("/core/scene/failover/FailoverManager.java", 
            new MigrationInfo("/failover/FailoverManager.java", "net.ooder.sdk.core.scene.failover", "net.ooder.engine.scene.failover"));
        migrations.put("/core/scene/failover/HeartbeatManager.java", 
            new MigrationInfo("/failover/HeartbeatManager.java", "net.ooder.sdk.core.scene.failover", "net.ooder.engine.scene.failover"));
        migrations.put("/core/scene/failover/RoleManager.java", 
            new MigrationInfo("/failover/RoleManager.java", "net.ooder.sdk.core.scene.failover", "net.ooder.engine.scene.failover"));
        
        // core/scene/group -> scene-engine/group
        migrations.put("/core/scene/group/KeyShareManager.java", 
            new MigrationInfo("/group/KeyShareManager.java", "net.ooder.sdk.core.scene.group", "net.ooder.engine.scene.group"));
        migrations.put("/core/scene/group/MemberManager.java", 
            new MigrationInfo("/group/MemberManager.java", "net.ooder.sdk.core.scene.group", "net.ooder.engine.scene.group"));
        
        // core/scene/store -> scene-engine/store
        migrations.put("/core/scene/store/LocalSceneStore.java", 
            new MigrationInfo("/store/LocalSceneStore.java", "net.ooder.sdk.core.scene.store", "net.ooder.engine.scene.store"));
        migrations.put("/core/scene/store/DualSceneStore.java", 
            new MigrationInfo("/store/DualSceneStore.java", "net.ooder.sdk.core.scene.store", "net.ooder.engine.scene.store"));
        migrations.put("/core/scene/store/AbstractSceneStore.java", 
            new MigrationInfo("/store/AbstractSceneStore.java", "net.ooder.sdk.core.scene.store", "net.ooder.engine.scene.store"));
        
        // core/scene/endpoint -> scene-engine/endpoint
        migrations.put("/core/scene/endpoint/EndpointAllocator.java", 
            new MigrationInfo("/endpoint/EndpointAllocator.java", "net.ooder.sdk.core.scene.endpoint", "net.ooder.engine.scene.endpoint"));
        
        // core/scene/capability -> scene-engine/capability
        migrations.put("/core/scene/capability/RuntimeCapabilityRegistry.java", 
            new MigrationInfo("/capability/RuntimeCapabilityRegistry.java", "net.ooder.sdk.core.scene.capability", "net.ooder.engine.scene.capability"));
        migrations.put("/core/scene/capability/RuntimeCapability.java", 
            new MigrationInfo("/capability/RuntimeCapability.java", "net.ooder.sdk.core.scene.capability", "net.ooder.engine.scene.capability"));
        
        // service/scene -> scene-engine/service
        migrations.put("/service/scene/SceneService.java", 
            new MigrationInfo("/service/SceneService.java", "net.ooder.sdk.service.scene", "net.ooder.engine.scene.service"));
        
        int copied = 0;
        int notFound = 0;
        
        for (Map.Entry<String, MigrationInfo> entry : migrations.entrySet()) {
            String sourcePath = entry.getKey();
            MigrationInfo info = entry.getValue();
            
            Path source = Paths.get(sourceBase + sourcePath);
            Path target = Paths.get(targetBase + info.targetPath);
            
            if (Files.exists(source)) {
                // 读取文件内容
                String content = new String(Files.readAllBytes(source), "UTF-8");
                
                // 修改包名
                content = content.replace("package " + info.oldPackage + ";", "package " + info.newPackage + ";");
                
                // 修改import语句中的旧包引用
                content = content.replace("import " + info.oldPackage + ".", "import " + info.newPackage + ".");
                content = content.replace("import net.ooder.sdk.core.scene.impl.", "import net.ooder.engine.scene.core.");
                content = content.replace("import net.ooder.sdk.core.scene.failover.", "import net.ooder.engine.scene.failover.");
                content = content.replace("import net.ooder.sdk.core.scene.group.", "import net.ooder.engine.scene.group.");
                content = content.replace("import net.ooder.sdk.core.scene.store.", "import net.ooder.engine.scene.store.");
                content = content.replace("import net.ooder.sdk.core.scene.endpoint.", "import net.ooder.engine.scene.endpoint.");
                content = content.replace("import net.ooder.sdk.core.scene.capability.", "import net.ooder.engine.scene.capability.");
                content = content.replace("import net.ooder.sdk.service.scene.", "import net.ooder.engine.scene.service.");
                
                // 确保目标目录存在
                Files.createDirectories(target.getParent());
                
                // 写入文件
                Files.write(target, content.getBytes("UTF-8"));
                System.out.println("  ✓ 迁移: " + sourcePath + " -> " + info.targetPath);
                copied++;
            } else {
                System.out.println("  ⚠ 未找到: " + sourcePath);
                notFound++;
            }
        }
        
        System.out.println("\n  统计: 成功 " + copied + " 个, 未找到 " + notFound + " 个");
    }
    
    private static void updateSceneEnginePom() throws Exception {
        System.out.println("\n【3】更新scene-engine的pom.xml...");
        
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n\n" +
            "    <parent>\n" +
            "        <groupId>net.ooder</groupId>\n" +
            "        <artifactId>agent-sdk</artifactId>\n" +
            "        <version>2.3</version>\n" +
            "    </parent>\n\n" +
            "    <artifactId>scene-engine</artifactId>\n" +
            "    <packaging>jar</packaging>\n\n" +
            "    <name>Ooder Scene Engine</name>\n" +
            "    <description>Scene management and orchestration engine</description>\n\n" +
            "    <dependencies>\n" +
            "        <!-- API层 -->\n" +
            "        <dependency>\n" +
            "            <groupId>net.ooder</groupId>\n" +
            "            <artifactId>agent-sdk-api</artifactId>\n" +
            "            <version>${project.version}</version>\n" +
            "        </dependency>\n" +
            "        <!-- Skills框架 -->\n" +
            "        <dependency>\n" +
            "            <groupId>net.ooder</groupId>\n" +
            "            <artifactId>skills-framework</artifactId>\n" +
            "            <version>${project.version}</version>\n" +
            "        </dependency>\n" +
            "        <!-- Spring Boot -->\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.slf4j</groupId>\n" +
            "            <artifactId>slf4j-api</artifactId>\n" +
            "            <version>1.7.36</version>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n\n" +
            "    <build>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.apache.maven.plugins</groupId>\n" +
            "                <artifactId>maven-compiler-plugin</artifactId>\n" +
            "                <version>3.8.1</version>\n" +
            "                <configuration>\n" +
            "                    <source>1.8</source>\n" +
            "                    <target>1.8</target>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>\n";
        
        Files.write(Paths.get(BASE_DIR + "/scene-engine/pom.xml"), pomContent.getBytes("UTF-8"));
        System.out.println("  ✓ scene-engine/pom.xml 更新完成");
    }
    
    static class MigrationInfo {
        String targetPath;
        String oldPackage;
        String newPackage;
        
        MigrationInfo(String targetPath, String oldPackage, String newPackage) {
            this.targetPath = targetPath;
            this.oldPackage = oldPackage;
            this.newPackage = newPackage;
        }
    }
}
