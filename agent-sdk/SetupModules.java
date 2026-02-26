import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SetupModules {
    
    private static final String BASE_DIR = "E:/github/ooder-sdk/agent-sdk";
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== SDK模块化设置开始 ===\n");
        
        // 1. 创建agent-sdk-api模块
        setupAgentSdkApi();
        
        // 2. 创建llm-sdk模块
        setupLlmSdk();
        
        // 3. 更新父pom.xml
        updateParentPom();
        
        System.out.println("\n=== SDK模块化设置完成 ===");
        System.out.println("请在IDE中刷新Maven项目，然后修改引用");
    }
    
    private static void setupAgentSdkApi() throws Exception {
        System.out.println("【1】设置 agent-sdk-api 模块...");
        
        String moduleDir = BASE_DIR + "/agent-sdk-api";
        String sourceDir = BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        String targetDir = moduleDir + "/src/main/java/net/ooder/sdk";
        
        // 创建目录
        String[] dirs = {
            "/api/scene", "/api/scene/model", "/api/scene/store",
            "/api/protocol", "/api/command", "/api/capability",
            "/api/initializer", "/common/enums", "/common/constants"
        };
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(targetDir + dir));
        }
        System.out.println("  ✓ 目录结构创建完成");
        
        // 复制文件映射
        Map<String, String[]> filesMap = new LinkedHashMap<>();
        filesMap.put("/common/enums", new String[]{
            "MemberRole.java", "SceneType.java", "AgentType.java",
            "DiscoveryMethod.java", "SkillStatus.java"
        });
        filesMap.put("/common/constants", new String[]{
            "SDKConstants.java", "ErrorCodes.java", "Defaults.java"
        });
        filesMap.put("/api/scene", new String[]{
            "SceneGroup.java", "SceneMember.java", "SceneSnapshot.java",
            "SceneGroupKey.java", "CapabilityInvoker.java"
        });
        filesMap.put("/api/scene/model", new String[]{
            "SceneState.java", "SceneLifecycleStats.java", "SceneConfig.java"
        });
        filesMap.put("/api/scene/store", new String[]{
            "SceneStore.java", "GroupStore.java", "AgentStore.java",
            "SkillStore.java", "LinkStore.java", "DualStorage.java",
            "SyncListener.java", "StorageException.java", "StorageStatus.java",
            "ConflictInfo.java", "SyncEvent.java", "AgentRegistration.java",
            "SkillRegistration.java", "LinkConfig.java"
        });
        filesMap.put("/api/protocol", new String[]{
            "ProtocolHub.java", "ProtocolHandler.java"
        });
        filesMap.put("/api/command", new String[]{
            "CommandPacket.java", "CommandBuilder.java", "CommandDirection.java",
            "CommandResult.java", "CommandStatus.java", "BatchCommandResult.java",
            "CommandTrace.java"
        });
        filesMap.put("/api/capability", new String[]{
            "Capability.java", "CapAddress.java", "CapParameter.java",
            "CapabilityType.java", "CapabilityStatus.java"
        });
        filesMap.put("/api/initializer", new String[]{
            "NexusInitializer.java"
        });
        
        int copied = 0;
        for (Map.Entry<String, String[]> entry : filesMap.entrySet()) {
            String subDir = entry.getKey();
            for (String file : entry.getValue()) {
                Path source = Paths.get(sourceDir + subDir + "/" + file);
                Path target = Paths.get(targetDir + subDir + "/" + file);
                if (Files.exists(source)) {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                }
            }
        }
        System.out.println("  ✓ 复制了 " + copied + " 个文件");
        
        // 创建pom.xml
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
            "    <artifactId>agent-sdk-api</artifactId>\n" +
            "    <packaging>jar</packaging>\n\n" +
            "    <name>Ooder Agent SDK API</name>\n" +
            "    <description>Core API interfaces and models for Agent SDK</description>\n\n" +
            "    <dependencies>\n" +
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
        
        Files.write(Paths.get(moduleDir + "/pom.xml"), pomContent.getBytes());
        System.out.println("  ✓ pom.xml 创建完成");
    }
    
    private static void setupLlmSdk() throws Exception {
        System.out.println("\n【2】设置 llm-sdk 模块...");
        
        String moduleDir = BASE_DIR + "/llm-sdk";
        String sourceDir = BASE_DIR + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        String targetDir = moduleDir + "/src/main/java/net/ooder/llm";
        
        // 创建目录
        String[] dirs = {
            "/api", "/api/impl", "/service", "/service/impl",
            "/drivers", "/drivers/impl", "/config"
        };
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(targetDir + dir));
        }
        System.out.println("  ✓ 目录结构创建完成");
        
        // 复制LLM文件（从api/llm到llm-sdk/api）
        String[] llmFiles = {
            "LlmService.java", "ChatRequest.java", "LlmConfig.java",
            "FunctionDef.java", "TokenUsage.java"
        };
        
        int copied = 0;
        for (String file : llmFiles) {
            Path source = Paths.get(sourceDir + "/api/llm/" + file);
            Path target = Paths.get(targetDir + "/api/" + file);
            if (Files.exists(source)) {
                // 读取并修改包名
                String content = new String(Files.readAllBytes(source));
                content = content.replace("package net.ooder.sdk.api.llm;", "package net.ooder.llm.api;");
                content = content.replace("import net.ooder.sdk.api.llm.", "import net.ooder.llm.api.");
                Files.write(target, content.getBytes());
                copied++;
            }
        }
        
        // 复制impl文件
        Path implSource = Paths.get(sourceDir + "/api/llm/impl/LlmServiceImpl.java");
        Path implTarget = Paths.get(targetDir + "/api/impl/LlmServiceImpl.java");
        if (Files.exists(implSource)) {
            String content = new String(Files.readAllBytes(implSource));
            content = content.replace("package net.ooder.sdk.api.llm.impl;", "package net.ooder.llm.api.impl;");
            content = content.replace("import net.ooder.sdk.api.llm.", "import net.ooder.llm.api.");
            Files.write(implTarget, content.getBytes());
            copied++;
        }
        
        System.out.println("  ✓ 复制并修改了 " + copied + " 个文件");
        
        // 创建pom.xml
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
            "    <artifactId>llm-sdk</artifactId>\n" +
            "    <packaging>jar</packaging>\n\n" +
            "    <name>Ooder LLM SDK</name>\n" +
            "    <description>LLM integration SDK for Agent</description>\n\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>net.ooder</groupId>\n" +
            "            <artifactId>agent-sdk-api</artifactId>\n" +
            "            <version>${project.version}</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.apache.httpcomponents</groupId>\n" +
            "            <artifactId>httpclient</artifactId>\n" +
            "            <version>4.5.13</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.alibaba</groupId>\n" +
            "            <artifactId>fastjson</artifactId>\n" +
            "            <version>1.2.83</version>\n" +
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
        
        Files.write(Paths.get(moduleDir + "/pom.xml"), pomContent.getBytes());
        System.out.println("  ✓ pom.xml 创建完成");
    }
    
    private static void updateParentPom() throws Exception {
        System.out.println("\n【3】更新父pom.xml...");
        
        Path pomPath = Paths.get(BASE_DIR + "/pom.xml");
        String content = new String(Files.readAllBytes(pomPath));
        
        // 更新modules部分
        String oldModules = "<modules>\n" +
            "        <module>skills-framework</module>\n" +
            "        <module>scene-engine</module>\n" +
            "        <module>agent-sdk-core</module>\n" +
            "    </modules>";
        
        String newModules = "<modules>\n" +
            "        <module>agent-sdk-api</module>\n" +
            "        <module>llm-sdk</module>\n" +
            "        <module>skills-framework</module>\n" +
            "        <module>scene-engine</module>\n" +
            "        <module>agent-sdk-core</module>\n" +
            "    </modules>";
        
        content = content.replace(oldModules, newModules);
        Files.write(pomPath, content.getBytes());
        System.out.println("  ✓ 父pom.xml更新完成");
    }
}
