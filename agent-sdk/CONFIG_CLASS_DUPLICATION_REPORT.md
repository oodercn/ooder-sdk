# 配置类重复检查报告

**检查时间**: 2026-03-09  
**检查范围**: 所有 *Config.java 类  
**版本**: 2.3.1

---

## 一、严重重复 - 需要立即处理

### 1. LlmConfig 重复 ⚠️ 严重

| 类名 | 位置 | 说明 |
|------|------|------|
| `LlmConfig` | `net.ooder.sdk.service.llm` | 简单配置，只有基本字段 |
| `LlmConfig` (内部类) | `net.ooder.sdk.drivers.llm.LlmDriver` | 完整配置，包含更多字段和方法 |

**字段对比**:

| 字段 | service.llm | drivers.llm |
|------|-------------|-------------|
| endpoint | ✅ | ✅ |
| apiKey | ✅ | ✅ |
| model | ✅ | ✅ |
| maxTokens | ✅ | ✅ |
| temperature | ✅ | ✅ |
| timeout | ✅ | ✅ |
| apiSecret | ❌ | ✅ |
| appId | ❌ | ✅ |
| baseUrl | ❌ | ✅ |
| defaultModel | ❌ | ✅ |
| topP | ❌ | ✅ |
| maxRetries | ❌ | ✅ |
| simulationMode | ❌ | ✅ |
| properties | ❌ | ✅ |

**建议**: 删除 `net.ooder.sdk.service.llm.LlmConfig`，统一使用 `LlmDriver.LlmConfig`

---

### 2. SceneConfig 重复 ⚠️ 严重

| 类名 | 位置 | 说明 |
|------|------|------|
| `SceneConfig` | `net.ooder.skills.api` | skills-framework 的配置 |
| `SceneConfig` (内部类) | `net.ooder.skills.api.SceneTemplate` | SceneTemplate 的配置 |
| `SceneConfig` (内部类) | `net.ooder.sdk.llm.integration.SceneEngineIntegration` | LLM集成的配置 |
| `SceneConfig<P>` | `net.ooder.sdk.api.scene.model` | agent-sdk-core 的泛型配置 |
| `SceneConfiguration` | `net.ooder.sdk.infra.config.scene` | 基础设施配置 |

**建议**: 统一使用 `net.ooder.skills.api.SceneConfig` 或 `net.ooder.sdk.api.scene.model.SceneConfig`

---

### 3. MainFirstConfig 重复 ⚠️ 严重

| 类名 | 位置 |
|------|------|
| `MainFirstConfig` (内部类) | `net.ooder.skills.api.SkillManifest` |
| `MainFirstConfig` (内部类) | `net.ooder.skills.api.SceneTemplate` |
| `MainFirstConfig` (内部类) | `net.ooder.skills.api.Capability` |

**建议**: 提取为独立的公共类

---

### 4. SelfDriveConfig 重复 ⚠️ 严重

| 类名 | 位置 |
|------|------|
| `SelfDriveConfig` (内部类) | `net.ooder.skills.api.SkillManifest` |
| `SelfDriveConfig` (内部类) | `net.ooder.skills.api.Capability` |

**建议**: 提取为独立的公共类

---

### 5. CollaborativeConfig 重复 ⚠️ 严重

| 类名 | 位置 |
|------|------|
| `CollaborativeConfig` (内部类) | `net.ooder.skills.api.SceneGroupManager` |
| `CollaborativeConfig` (内部类) | `net.ooder.skills.api.MainFirstService` |

**建议**: 提取为独立的公共类

---

### 6. ShareConfig 重复 ⚠️ 严重

| 类名 | 位置 |
|------|------|
| `ShareConfig` | `net.ooder.sdk.nexus.resource.model` |
| `ShareConfig` | `net.ooder.sdk.api.share.model` |
| `ShareConfig` (内部类) | `net.ooder.sdk.api.capability.ShareableCapability` |

**建议**: 统一使用 `net.ooder.sdk.api.share.model.ShareConfig`

---

## 二、中等重复 - 需要关注

### 7. ObservationConfig 重复

| 类名 | 位置 |
|------|------|
| `ObservationConfig` | `net.ooder.sdk.southbound.adapter.model` |
| `ObservationConfig` | `net.ooder.sdk.northbound.protocol.model` |

---

## 三、Config 类统计

### 总计发现 67 个 Config 相关定义

| 模块 | Config 类数量 |
|------|--------------|
| agent-sdk-core | ~35 个 |
| llm-sdk | ~10 个 |
| skills-framework | ~15 个 |
| docs | ~7 个 |

---

## 四、建议处理方案

### 短期 (v2.3.2)

1. **合并 LlmConfig**
   - 删除 `net.ooder.sdk.service.llm.LlmConfig`
   - 所有引用改为 `LlmDriver.LlmConfig`

2. **统一 SceneConfig**
   - 保留 `net.ooder.skills.api.SceneConfig`
   - 删除其他 SceneConfig 或改为继承

### 中期 (v2.4)

3. **提取公共 Config**
   - 创建 `net.ooder.sdk.common.config` 包
   - 将 MainFirstConfig, SelfDriveConfig, CollaborativeConfig 等提取为公共类

4. **清理重复**
   - 删除所有内部类 Config
   - 统一使用公共 Config 类

### 长期 (v2.5)

5. **配置中心**
   - 建立统一的配置管理中心
   - 支持配置的热更新和动态加载

---

## 五、影响评估

| 重复类 | 影响范围 | 处理难度 |
|--------|---------|---------|
| LlmConfig | 高 | 中 |
| SceneConfig | 高 | 高 |
| MainFirstConfig | 中 | 低 |
| SelfDriveConfig | 中 | 低 |
| CollaborativeConfig | 中 | 低 |
| ShareConfig | 中 | 中 |

---

## 六、总结

### 问题严重性: ⚠️ 高

- **严重重复**: 6 组
- **中等重复**: 1 组
- **总计影响**: 约 20 个文件

### 建议优先级

1. **P0**: LlmConfig 合并
2. **P1**: SceneConfig 统一
3. **P2**: 提取公共 Config 类
4. **P3**: 建立配置中心

---

**报告结束**

*本报告由 Agent-SDK 团队生成*  
*日期: 2026-03-09*
