# LLM-CHAT 通用功能建设协作声明

## 一、协作背景

### 1.1 项目概述

| 项目 | 内容 |
|------|------|
| 项目名称 | LLM-CHAT 通用功能建设 |
| 版本 | v2.4.0 |
| 发起方 | SE (scene-engine) 团队 |
| 协作方 | Agent-SDK 团队、Skills 团队 |
| 创建日期 | 2026-03-10 |

### 1.2 协作目标

1. **统一 LLM 服务接口** - 提供 `LlmService` 统一调用入口
2. **SKILLS.MD 驱动配置** - 从说明书自动生成配置
3. **多级配置加载** - 系统/环境/应用/用户四级配置
4. **配置热加载** - 运行时配置更新无需重启
5. **LLM 辅助配置生成** - 首次调用自动生成，版本更新智能合并

---

## 二、协作分工

### 2.1 SE (scene-engine) 团队职责

| 模块 | 组件 | 说明 |
|------|------|------|
| **SKILLS.MD 解析** | `SkillsMdParser` | 解析 SKILLS.MD 文件 |
| **配置生成** | `LlmConfigGeneratorService` | LLM 辅助生成配置 |
| **热加载** | `ConfigHotReloadService` | 配置文件监听和热更新 |
| **版本管理** | `ConfigVersionManager` | 版本变更检测 |
| **多级加载** | `LayeredConfigLoader` | 四级配置加载 |
| **统一服务** | `LlmService` | LLM 调用统一接口 |

### 2.2 Agent-SDK 团队职责

| 模块 | 组件 | 说明 |
|------|------|------|
| **SkillPackage 扩展** | `SkillPackage` | 新增 form/sceneType 字段 |
| **注解支持** | `@LlmFunction/@LlmParam` | 可选的注解定义 |
| **Provider SPI** | `LlmProviderSpi` | Provider 扩展机制 |

### 2.3 Skills 团队职责

| 模块 | 组件 | 说明 |
|------|------|------|
| **SKILLS.MD 规范** | 模板和示例 | 标准化说明书格式 |
| **skill-index 更新** | 数据迁移 | 旧分类迁移到 v3.0 |
| **前端适配** | UI 更新 | 分类筛选界面 |

---

## 三、接口契约

### 3.1 SE 提供的接口

#### LlmService

```java
package net.ooder.scene.llm;

public interface LlmService {
    
    /**
     * 统一聊天接口
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 流式聊天
     */
    void chatStream(ChatRequest request, StreamHandler handler);
    
    /**
     * 获取可用的 Provider 列表
     */
    List<ProviderInfo> getProviders();
    
    /**
     * 获取指定 Provider 支持的模型
     */
    List<ModelInfo> getModels(String providerId);
    
    /**
     * 设置当前使用的模型
     */
    void setActiveModel(String providerId, String modelId);
}
```

#### SkillsMdParser

```java
package net.ooder.scene.llm.config;

public class SkillsMdParser {
    
    /**
     * 解析 SKILLS.MD 文件
     */
    public SkillsMdDocument parse(Path skillsMdPath) throws IOException;
    
    /**
     * 解析 SKILLS.MD 内容
     */
    public SkillsMdDocument parse(String content);
}
```

#### ConfigHotReloadService

```java
package net.ooder.scene.llm.config;

public class ConfigHotReloadService {
    
    /**
     * 注册配置目录监听
     */
    public void registerWatch(Path configDir, String skillId);
    
    /**
     * 添加配置变更监听器
     */
    public void addListener(ConfigChangeListener listener);
    
    /**
     * 启动监听
     */
    public void start();
    
    /**
     * 停止监听
     */
    public void stop();
}
```

### 3.2 Agent-SDK 需要提供的接口

#### SkillPackage 扩展

```java
package net.ooder.skills.api;

public class SkillPackage {
    
    // 现有字段...
    
    // v3.0 新增字段
    private SkillForm form;           // 技能形态
    private SceneType sceneType;      // 场景类型（仅 SCENE 时有效）
    private SkillCategory category;   // 技能分类（枚举）
    
    // 废弃字段（保留兼容）
    @Deprecated
    private String sceneId;           // 使用 form + sceneType 替代
    @Deprecated
    private String subCategory;       // v3.0 不再使用
}
```

### 3.3 Skills 需要提供的接口

#### SKILLS.MD 规范

```markdown
# 技能名称

> 版本: x.x.x | 作者: Team | 更新日期: yyyy-MM-dd

## 概述
技能描述...

## 能力列表
### capability-id: 能力名称
- **名称**: functionName
- **描述**: 能力描述
- **输入参数**:
  - `paramName` (type, required/optional): 参数描述
- **输出**: 返回类型描述

## 知识库
### 基础知识 (knowledge/basic.md)
### 高级知识 (knowledge/advanced.md)

## 配置建议
- 推荐模型: model-name
- Temperature: 0.7
```

---

## 四、数据契约

### 4.1 配置文件格式

#### skill-config.yaml

```yaml
apiVersion: ooder.net/v1
kind: SkillConfig

metadata:
  skillId: document-skill
  version: 2.3.1
  generatedAt: 2026-03-10T10:00:00Z
  generator: LlmConfigGenerator

spec:
  llm:
    provider: deepseek
    model: deepseek-chat
    temperature: 0.7
    maxTokens: 4096
  
  functions:
    - name: search_documents
      description: 搜索文档
      capability: documentSearch
      parameters:
        query:
          type: string
          description: 搜索关键词
          required: true
  
  prompts:
    system: |
      你是一个文档管理助手...
  
  rules:
    - id: route_search
      type: ROUTING
      condition: "query.contains('搜索')"
      action: "{capability: 'documentSearch'}"
```

### 4.2 多级配置路径

```
Level 1: 系统默认
  classpath:llm-config-default.yaml

Level 2: 环境配置
  config/env/{env}/llm-config.yaml

Level 3: 应用配置
  config/app/llm-config.yaml

Level 4: 用户配置
  ~/.ooder/llm-config.yaml
```

---

## 五、协作时间线

### 5.1 Phase 1: 核心能力（Week 1-2）

| 任务 | 负责方 | 依赖 | 交付物 |
|------|--------|------|--------|
| SkillsMdParser | SE | 无 | 解析器代码 |
| SkillsMdDocument | SE | 无 | 文档模型 |
| LlmConfigGeneratorService | SE | SkillsMdParser | 配置生成服务 |
| ConfigHotReloadService | SE | 无 | 热加载服务 |
| ConfigVersionManager | SE | 无 | 版本管理 |
| LlmService | SE | 无 | 统一服务接口 |

### 5.2 Phase 2: 增强能力（Week 3-4）

| 任务 | 负责方 | 依赖 | 交付物 |
|------|--------|------|--------|
| LayeredConfigLoader | SE | 无 | 多级加载器 |
| ConfigDiffCalculator | SE | 无 | 差异计算器 |
| SkillPackage 扩展 | Agent-SDK | 无 | 扩展字段 |
| SKILLS.MD 规范 | Skills | 无 | 规范文档 |

### 5.3 Phase 3: 集成测试（Week 5-6）

| 任务 | 负责方 | 依赖 | 交付物 |
|------|--------|------|--------|
| 集成测试 | SE + Agent-SDK | Phase 1-2 | 测试报告 |
| 前端适配 | Skills | Phase 1-2 | UI 更新 |
| 文档完善 | All | Phase 1-2 | 使用文档 |

---

## 六、验收标准

### 6.1 SE 交付验收

| 验收项 | 标准 |
|--------|------|
| SkillsMdParser | 能正确解析标准 SKILLS.MD 文件 |
| LlmConfigGeneratorService | 能生成有效的 skill-config.yaml |
| ConfigHotReloadService | 配置修改后 1 秒内生效 |
| LlmService | 统一接口可调用所有 Provider |

### 6.2 Agent-SDK 交付验收

| 验收项 | 标准 |
|--------|------|
| SkillPackage 扩展 | 新增字段向后兼容 |
| 废弃字段 | 标记 @Deprecated 但仍可用 |

### 6.3 Skills 交付验收

| 验收项 | 标准 |
|--------|------|
| SKILLS.MD 规范 | 所有 Skill 都有符合规范的 SKILLS.MD |
| 前端适配 | 分类筛选使用新的枚举类型 |

---

## 七、沟通机制

### 7.1 定期会议

| 会议 | 频率 | 参与方 |
|------|------|--------|
| 周例会 | 每周一 | SE + Agent-SDK + Skills |
| 技术评审 | 每周五 | 核心开发人员 |

### 7.2 文档协作

| 文档 | 维护方 | 位置 |
|------|--------|------|
| 协作声明 | SE | scene-engine/docs/ |
| 接口文档 | 各负责方 | 各自仓库 |
| 集成指南 | SE | scene-engine/docs/ |

---

## 八、风险与缓解

| 风险 | 等级 | 缓解措施 |
|------|------|----------|
| 接口变更影响 | 中 | 保持向后兼容，废弃字段保留 |
| 配置迁移复杂 | 中 | 提供迁移脚本和文档 |
| 热加载性能 | 低 | 防抖处理，增量更新 |
| LLM 生成质量 | 中 | 人工审核 + 配置校验 |

---

## 九、签署确认

| 团队 | 确认人 | 确认日期 | 状态 |
|------|--------|----------|------|
| SE (scene-engine) | ________ | ________ | ⏳ 待确认 |
| Agent-SDK | ________ | ________ | ⏳ 待确认 |
| Skills | ________ | ________ | ⏳ 待确认 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**创建人**: SE Team
