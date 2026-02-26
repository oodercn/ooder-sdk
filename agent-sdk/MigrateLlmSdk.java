import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MigrateLlmSdk {
    public static void main(String[] args) throws IOException {
        String baseDir = "E:/github/ooder-sdk/agent-sdk";
        String sourceDir = baseDir + "/agent-sdk-core/src/main/java/net/ooder/sdk";
        String targetDir = baseDir + "/llm-sdk/src/main/java/net/ooder/llm";

        // 创建目录结构
        String[] dirs = {
            "/api", "/api/impl", "/service", "/service/impl",
            "/drivers", "/drivers/impl", "/config"
        };

        for (String dir : dirs) {
            Path path = Paths.get(targetDir + dir);
            Files.createDirectories(path);
            System.out.println("Created: " + path);
        }

        // 定义要复制的文件 (源路径 -> 目标路径)
        Map<String, String> filesToCopy = new HashMap<>();

        // api
        filesToCopy.put("/api/llm/LlmService.java", "/api/LlmService.java");
        filesToCopy.put("/api/llm/ChatRequest.java", "/api/ChatRequest.java");
        filesToCopy.put("/api/llm/LlmConfig.java", "/api/LlmConfig.java");
        filesToCopy.put("/api/llm/FunctionDef.java", "/api/FunctionDef.java");
        filesToCopy.put("/api/llm/TokenUsage.java", "/api/TokenUsage.java");
        filesToCopy.put("/api/llm/impl/LlmServiceImpl.java", "/api/impl/LlmServiceImpl.java");

        // service
        filesToCopy.put("/service/llm/LlmService.java", "/service/LlmService.java");
        filesToCopy.put("/service/llm/LlmServiceImpl.java", "/service/LlmServiceImpl.java");
        filesToCopy.put("/service/llm/LlmClient.java", "/service/LlmClient.java");
        filesToCopy.put("/service/llm/LlmConfig.java", "/service/LlmConfig.java");

        // drivers
        filesToCopy.put("/drivers/llm/LlmDriver.java", "/drivers/LlmDriver.java");
        filesToCopy.put("/drivers/llm/impl/OpenAiLlmDriver.java", "/drivers/impl/OpenAiLlmDriver.java");
        filesToCopy.put("/drivers/llm/impl/LocalLlmDriver.java", "/drivers/impl/LocalLlmDriver.java");

        // 复制文件
        int copied = 0;
        for (Map.Entry<String, String> entry : filesToCopy.entrySet()) {
            String sourcePath = entry.getKey();
            String targetPath = entry.getValue();

            Path source = Paths.get(sourceDir + sourcePath);
            Path target = Paths.get(targetDir + targetPath);

            if (Files.exists(source)) {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied: " + sourcePath + " -> " + targetPath);
                copied++;
            } else {
                System.out.println("Not found: " + source);
            }
        }

        System.out.println("\nTotal files copied: " + copied);
    }
}
