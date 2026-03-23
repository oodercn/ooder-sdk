# SDK 扩展需求清单

> **目标**: 为 scene-engine 2.3.1 版本提供必要的底层能力支持
> **提交日期**: 2026-03-21
> **优先级**: P0（阻塞编译）

---

## 一、架构层次分工

```
┌─────────────────────────────────────────────────────────────────────┐
│                        应用层 (scene-engine)                         │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ MultiLevelContextManager                                     │   │
│  │ SkillPromptProvider / SkillPromptRagProvider                 │   │
│  │ SkillInstallProcessor                                        │   │
│  │ JsonContextStore                                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                │                                    │
│                                ▼                                    │
├─────────────────────────────────────────────────────────────────────┤
│                        能力层 (skills-framework)                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ SkillPackage.getResource()        ← 需要扩展                  │   │
│  │ SkillPackage.resourcePath         ← 需要新增                  │   │
│  │ SkillManifest.prompts             ← 需要新增                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                │                                    │
│                                ▼                                    │
├─────────────────────────────────────────────────────────────────────┤
│                        基础层 (skills-api)                           │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │ ResourceAccessor 接口              ← 需要新增                  │   │
│  │ PromptConfig 模型                  ← 需要新增                  │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 二、扩展需求详情

### 需求 1: SkillPackage 资源访问能力 (P0 - 阻塞)

**问题**: `SkillPackage` 缺少资源文件访问方法，导致无法读取技能包内的提示词模板等资源。

**影响文件**:
- `SkillPromptProviderImpl.java` (第79行)
- `SkillInstallProcessorImpl.java` (第301行)

**当前错误**:
```
找不到符号: 方法 getResource(java.lang.String)
位置: 类型为net.ooder.skills.api.SkillPackage的变量 skillPackage
```

**扩展方案**:

#### 方案 A: 直接在 SkillPackage 添加方法（推荐）

```java
// 文件: net.ooder.skills.api.SkillPackage

package net.ooder.skills.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SkillPackage {
    
    // ... 现有字段 ...
    
    /**
     * 技能资源根路径（本地文件系统路径或资源前缀）
     */
    private String resourcePath;
    
    // ... 现有 getter/setter ...
    
    public String getResourcePath() {
        return resourcePath;
    }
    
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    
    /**
     * 获取技能资源输入流
     *
     * @param relativePath 相对于技能根目录的资源路径
     * @return 资源输入流，如果资源不存在返回 null
     */
    public InputStream getResource(String relativePath) {
        if (resourcePath == null || relativePath == null) {
            return null;
        }
        
        try {
            Path fullPath = Paths.get(resourcePath, relativePath);
            if (Files.exists(fullPath)) {
                return new FileInputStream(fullPath.toFile());
            }
            
            // 尝试从 classpath 加载
            String classpathResource = "/" + skillId + "/" + relativePath;
            InputStream is = getClass().getResourceAsStream(classpathResource);
            if (is != null) {
                return is;
            }
            
            // 尝试从当前线程的 ClassLoader 加载
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.getResourceAsStream(skillId + "/" + relativePath);
            
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * 检查资源是否存在
     *
     * @param relativePath 相对于技能根目录的资源路径
     * @return 是否存在
     */
    public boolean hasResource(String relativePath) {
        if (resourcePath == null || relativePath == null) {
            return false;
        }
        
        Path fullPath = Paths.get(resourcePath, relativePath);
        if (Files.exists(fullPath)) {
            return true;
        }
        
        try (InputStream is = getResource(relativePath)) {
            return is != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 列出目录下的资源文件
     *
     * @param directory 相对于技能根目录的目录路径
     * @return 资源文件名列表
     */
    public List<String> listResources(String directory) {
        if (resourcePath == null || directory == null) {
            return java.util.Collections.emptyList();
        }
        
        try {
            Path dirPath = Paths.get(resourcePath, directory);
            if (Files.isDirectory(dirPath)) {
                return Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .collect(java.util.stream.Collectors.toList());
            }
        } catch (IOException e) {
            // ignore
        }
        
        return java.util.Collections.emptyList();
    }
}
```

#### 方案 B: 创建扩展接口（备选）

```java
// 文件: net.ooder.skills.api.ResourceAccessor

package net.ooder.skills.api;

import java.io.InputStream;
import java.util.List;

/**
 * 资源访问器接口
 */
public interface ResourceAccessor {
    
    InputStream getResource(String relativePath);
    
    boolean hasResource(String relativePath);
    
    List<String> listResources(String directory);
    
    String getRootPath();
}

// 文件: net.ooder.skills.api.SkillPackage (修改)

public class SkillPackage {
    // ... 现有字段 ...
    
    private ResourceAccessor resourceAccessor;
    
    public ResourceAccessor getResourceAccessor() {
        return resourceAccessor;
    }
    
    public void setResourceAccessor(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }
    
    // 便捷方法
    public InputStream getResource(String relativePath) {
        return resourceAccessor != null ? resourceAccessor.getResource(relativePath) : null;
    }
}
```

**建议**: 采用方案 A，直接在 `SkillPackage` 添加方法，减少接口复杂度。

---

### 需求 2: SkillManifest 提示词配置支持 (P1 - 增强)

**问题**: `SkillManifest` 缺少提示词相关配置字段，无法在技能清单中声明提示词文件。

**扩展方案**:

```java
// 文件: net.ooder.skills.api.SkillManifest

public class SkillManifest {
    
    // ... 现有字段 ...
    
    /**
     * 提示词配置
     */
    private PromptConfig prompt;
    
    /**
     * LLM 配置
     */
    private LlmConfig llmConfig;
    
    public PromptConfig getPrompt() {
        return prompt;
    }
    
    public void setPrompt(PromptConfig prompt) {
        this.prompt = prompt;
    }
    
    public LlmConfig getLlmConfig() {
        return llmConfig;
    }
    
    public void setLlmConfig(LlmConfig llmConfig) {
        this.llmConfig = llmConfig;
    }
    
    /**
     * 提示词配置
     */
    public static class PromptConfig {
        private String systemPromptFile;
        private String rolePromptsDir;
        private List<String> contextFiles;
        private Map<String, String> variables;
        
        public String getSystemPromptFile() {
            return systemPromptFile;
        }
        
        public void setSystemPromptFile(String systemPromptFile) {
            this.systemPromptFile = systemPromptFile;
        }
        
        public String getRolePromptsDir() {
            return rolePromptsDir;
        }
        
        public void setRolePromptsDir(String rolePromptsDir) {
            this.rolePromptsDir = rolePromptsDir;
        }
        
        public List<String> getContextFiles() {
            return contextFiles;
        }
        
        public void setContextFiles(List<String> contextFiles) {
            this.contextFiles = contextFiles;
        }
        
        public Map<String, String> getVariables() {
            return variables;
        }
        
        public void setVariables(Map<String, String> variables) {
            this.variables = variables;
        }
    }
    
    /**
     * LLM 配置
     */
    public static class LlmConfig {
        private String systemPrompt;
        private Double temperature;
        private Integer maxTokens;
        private List<FunctionConfig> functions;
        
        public String getSystemPrompt() {
            return systemPrompt;
        }
        
        public void setSystemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
        }
        
        public Double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Integer getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }
        
        public List<FunctionConfig> getFunctions() {
            return functions;
        }
        
        public void setFunctions(List<FunctionConfig> functions) {
            this.functions = functions;
        }
    }
    
    /**
     * 函数配置
     */
    public static class FunctionConfig {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private String capability;
        
        // getter/setter ...
    }
}
```

---

### 需求 3: SkillPackageLoader 资源路径注入 (P1 - 增强)

**问题**: 技能加载时需要设置 `resourcePath` 字段。

**扩展方案**:

```java
// 文件: net.ooder.skills.core.loader.SkillPackageLoader (或相关实现类)

public SkillPackage loadSkill(String skillId) {
    SkillPackage pkg = new SkillPackage();
    // ... 现有加载逻辑 ...
    
    // 设置资源路径
    String resourcePath = resolveResourcePath(skillId);
    pkg.setResourcePath(resourcePath);
    
    return pkg;
}

private String resolveResourcePath(String skillId) {
    // 1. 检查本地技能目录
    String localPath = skillsDir + "/" + skillId;
    if (new File(localPath).exists()) {
        return localPath;
    }
    
    // 2. 检查 classpath
    // 3. 检查其他来源
    
    return null;
}
```

---

## 三、skill.yaml 配置示例

扩展后支持的配置格式：

```yaml
skillId: recruitment-skill
name: 招聘助手
version: 1.0.0

# 提示词配置
prompt:
  systemPromptFile: prompts/system.md
  rolePromptsDir: prompts/roles
  contextFiles:
    - prompts/context/company.md
    - prompts/context/department.md
  variables:
    companyName: "Ooder科技"
    department: "人力资源部"

# LLM 配置
llmConfig:
  systemPrompt: "你是招聘场景的智能助手..."
  temperature: 0.7
  maxTokens: 2000
  functions:
    - name: scan_resume
      description: 扫描并解析简历
      parameters:
        type: object
        properties:
          resumeId:
            type: string
            description: 简历ID
      capability: resume_scan

# 能力定义
capabilities:
  - capId: resume_scan
    name: 简历扫描
    type: executor
```

---

## 四、验证清单

SDK 团队完成扩展后，请确认以下验证点：

| 验证项 | 验证方法 | 预期结果 |
|--------|----------|----------|
| `getResource()` 方法可用 | `skillPackage.getResource("prompts/system.md")` | 返回非空 InputStream |
| `hasResource()` 方法可用 | `skillPackage.hasResource("prompts/system.md")` | 返回 true/false |
| `resourcePath` 字段可设置 | `skillPackage.setResourcePath("/path/to/skill")` | 无异常 |
| `PromptConfig` 可解析 | 解析包含 prompt 字段的 skill.yaml | 正确映射到对象 |
| `LlmConfig` 可解析 | 解析包含 llmConfig 字段的 skill.yaml | 正确映射到对象 |

---

## 五、影响范围

### scene-engine 中受影响的文件

| 文件 | 使用方式 | 状态 |
|------|----------|------|
| `SkillPromptProviderImpl.java` | `skillPackage.getResource(promptFile)` | 编译失败 |
| `SkillInstallProcessorImpl.java` | `skillPackage.getResource(promptFile)` | 编译失败 |
| `SkillPromptRagProviderImpl.java` | 间接依赖 | 待验证 |

### 临时解决方案

在 SDK 扩展完成前，scene-engine 已创建 `SkillResourceAccessor` 接口作为适配层：

```java
// 文件: net.ooder.scene.skill.resource.SkillResourceAccessor
public interface SkillResourceAccessor {
    InputStream getResource(SkillPackage skillPackage, String resourcePath);
    boolean exists(SkillPackage skillPackage, String resourcePath);
    String getSkillRootPath(SkillPackage skillPackage);
    List<String> listResources(SkillPackage skillPackage, String directory);
}
```

SDK 扩展完成后，可直接使用 `SkillPackage.getResource()` 方法，移除适配层。

---

## 六、时间线

| 阶段 | 时间 | 负责方 |
|------|------|--------|
| 需求确认 | 2026-03-21 | scene-engine 团队 |
| SDK 扩展开发 | 2026-03-22 ~ 2026-03-23 | SDK 团队 |
| 联调验证 | 2026-03-24 | 双方 |
| 发布 | 2026-03-25 | SDK 团队 |

---

**联系人**: scene-engine 团队
**文档版本**: v1.0
**最后更新**: 2026-03-21
