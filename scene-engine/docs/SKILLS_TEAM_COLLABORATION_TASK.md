# Skills Team 协同任务：Skill Provider 实现

## 文档信息

| 项目 | 内容 |
|------|------|
| 文档版本 | 1.0.0 |
| 创建日期 | 2026-02-21 |
| 发起方 | SEC Team |
| 接收方 | Skills Team |

---

## 一、背景

SEC 0.7.3 已完成 Skill Provider 接口定义和 Mock 实现。Skills Team 需要实现具体的 Provider 接入实际 SDK。

### 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│  SEC (scene-engine 0.7.3)                                    │
│  ├── skill/                                                  │
│  │   ├── LlmProvider.java        (接口)                     │
│  │   ├── StorageProvider.java    (接口)                     │
│  │   ├── SchedulerProvider.java  (接口)                     │
│  │   ├── HttpClientProvider.java (接口)                     │
│  │   └── SkillProviderRegistry.java (注册器)               │
│  │                                                          │
│  └── Mock 实现（默认）                                       │
│      ├── MockLlmProvider.java                               │
│      ├── MockStorageProvider.java                           │
│      ├── MockSchedulerProvider.java                         │
│      └── MockHttpClientProvider.java                        │
└─────────────────────────────────────────────────────────────┘
                              ↓ ServiceLoader
┌─────────────────────────────────────────────────────────────┐
│  Skills Team 实现 (ooder-skills 仓库)                        │
│  ├── skill-llm-openai/                                       │
│  │   └── OpenAiLlmProvider implements LlmProvider           │
│  ├── skill-llm-ollama/                                       │
│  │   └── OllamaLlmProvider implements LlmProvider           │
│  ├── skill-storage-minio/                                    │
│  │   └── MinioStorageProvider implements StorageProvider    │
│  ├── skill-storage-s3/                                       │
│  │   └── S3StorageProvider implements StorageProvider       │
│  ├── skill-scheduler-quartz/                                 │
│  │   └── QuartzSchedulerProvider implements SchedulerProvider│
│  └── skill-httpclient-okhttp/                                │
│      └── OkHttpProvider implements HttpClientProvider        │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、任务清单

### 2.1 LlmProvider 实现

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| OpenAiLlmProvider | 1天 | P1 | 接入 OpenAI API |
| OllamaLlmProvider | 1天 | P2 | 接入 Ollama 本地模型 |
| AzureOpenAiLlmProvider | 1天 | P3 | 接入 Azure OpenAI |

**接口文件**：`net.ooder.scene.skill.LlmProvider`

```java
public interface LlmProvider {
    String getProviderType();
    List<String> getSupportedModels();
    Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options);
    String complete(String model, String prompt, Map<String, Object> options);
    List<double[]> embed(String model, List<String> texts);
    String translate(String model, String text, String targetLanguage, String sourceLanguage);
    String summarize(String model, String text, int maxLength);
}
```

### 2.2 StorageProvider 实现

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| MinioStorageProvider | 1天 | P1 | 接入 MinIO 对象存储 |
| S3StorageProvider | 1天 | P2 | 接入 AWS S3 |
| NasStorageProvider | 1天 | P3 | 接入 NAS 文件存储 |

**接口文件**：`net.ooder.scene.skill.StorageProvider`

```java
public interface StorageProvider {
    String getProviderType();
    byte[] readFile(String filePath);
    boolean writeFile(String filePath, byte[] content, boolean overwrite);
    boolean deleteFile(String filePath, boolean recursive);
    List<FileInfo> listFiles(String directoryPath, String pattern, boolean recursive);
    boolean fileExists(String filePath);
    boolean createDirectory(String directoryPath);
    FileInfo getFileInfo(String filePath);
    boolean copyFile(String sourcePath, String targetPath);
    boolean moveFile(String sourcePath, String targetPath);
    StorageQuota getQuota();
}
```

### 2.3 SchedulerProvider 实现

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| QuartzSchedulerProvider | 1天 | P1 | 接入 Quartz 调度器 |
| SpringSchedulerProvider | 0.5天 | P2 | 接入 Spring Scheduler |
| RedisSchedulerProvider | 1天 | P3 | 接入 Redis 分布式调度 |

**接口文件**：`net.ooder.scene.skill.SchedulerProvider`

```java
public interface SchedulerProvider {
    String getProviderType();
    String schedule(String taskName, String cronExpression, Object taskData, Map<String, Object> options);
    boolean cancel(String taskId);
    boolean pause(String taskId);
    boolean resume(String taskId);
    TaskInfo getTask(String taskId);
    TaskListResult listTasks(String status, int page, int pageSize);
    boolean triggerNow(String taskId);
    boolean updateCron(String taskId, String cronExpression);
    List<TaskExecution> getExecutionHistory(String taskId, int limit);
}
```

### 2.4 HttpClientProvider 实现

| 任务 | 工作量 | 优先级 | 说明 |
|------|--------|--------|------|
| OkHttpProvider | 0.5天 | P1 | 接入 OkHttp |
| ApacheHttpProvider | 0.5天 | P2 | 接入 Apache HttpClient |
| JavaHttpProvider | 0.5天 | P3 | 接入 Java 11+ HttpClient |

**接口文件**：`net.ooder.scene.skill.HttpClientProvider`

```java
public interface HttpClientProvider {
    String getProviderType();
    HttpResponse get(String url, Map<String, String> headers, Map<String, Object> options);
    HttpResponse post(String url, Object body, Map<String, String> headers, Map<String, Object> options);
    HttpResponse put(String url, Object body, Map<String, String> headers, Map<String, Object> options);
    HttpResponse delete(String url, Map<String, String> headers, Map<String, Object> options);
    HttpResponse head(String url, Map<String, String> headers, Map<String, Object> options);
    HttpResponse patch(String url, Object body, Map<String, String> headers, Map<String, Object> options);
}
```

---

## 三、实现指南

### 3.1 项目结构

```
skill-llm-openai/
├── pom.xml
└── src/main/
    ├── java/net/ooder/skills/llm/openai/
    │   ├── OpenAiLlmProvider.java
    │   └── OpenAiClient.java
    └── resources/
        └── META-INF/services/
            └── net.ooder.scene.skill.LlmProvider
```

### 3.2 pom.xml 模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>net.ooder.skills</groupId>
    <artifactId>skill-llm-openai</artifactId>
    <version>0.7.3</version>
    <packaging>jar</packaging>

    <name>skill-llm-openai</name>
    <description>OpenAI LLM Provider Implementation</description>

    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>scene-engine</artifactId>
            <version>0.7.3</version>
        </dependency>
        <!-- OpenAI SDK -->
        <dependency>
            <groupId>com.theokanning.openai-gpt3-java</groupId>
            <artifactId>service</artifactId>
            <version>0.18.2</version>
        </dependency>
    </dependencies>
</project>
```

### 3.3 ServiceLoader 注册

```
# META-INF/services/net.ooder.scene.skill.LlmProvider
net.ooder.skills.llm.openai.OpenAiLlmProvider
```

### 3.4 实现示例

```java
package net.ooder.skills.llm.openai;

import net.ooder.scene.skill.LlmProvider;
import java.util.*;

public class OpenAiLlmProvider implements LlmProvider {

    private OpenAiClient client;

    public OpenAiLlmProvider() {
        this.client = new OpenAiClient(System.getenv("OPENAI_API_KEY"));
    }

    @Override
    public String getProviderType() {
        return "openai";
    }

    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList("gpt-4", "gpt-4-turbo", "gpt-3.5-turbo");
    }

    @Override
    public Map<String, Object> chat(String model, List<Map<String, Object>> messages, Map<String, Object> options) {
        return client.createChatCompletion(model, messages, options);
    }

    // ... 其他方法实现
}
```

---

## 四、验收标准

### 4.1 功能验收

- [ ] 实现接口所有方法
- [ ] ServiceLoader 正确注册
- [ ] 单元测试覆盖核心功能
- [ ] 异常处理完善

### 4.2 质量验收

- [ ] 代码符合 Java 8 规范
- [ ] 无编译警告
- [ ] 有 Javadoc 注释

### 4.3 集成验收

- [ ] 可通过 SkillProviderRegistry 获取实例
- [ ] Mock 实现可被替换

---

## 五、交付物

| 交付物 | 说明 |
|--------|------|
| Provider 实现类 | 实现对应接口 |
| ServiceLoader 配置 | META-INF/services/... |
| 单元测试 | 覆盖核心功能 |
| README.md | 使用说明 |

---

## 六、联系方式

| 角色 | 联系方式 |
|------|----------|
| SEC Team | scene-engine 仓库 |
| Skills Team | ooder-skills 仓库 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-02-21  
**维护团队**: SEC Team
