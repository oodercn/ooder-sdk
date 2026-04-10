# skill-common 2.3.1 变更说明

**发布日期**: 2026-03-20  
**版本**: 2.3.1  
**影响范围**: 所有依赖 skill-common 的 MVP 项目

---

## 一、变更概述

本次版本对 `skill-common` 进行了重大重构，移除了与 `scene-engine 2.3.1` 重复的实现，统一使用 scene-engine 提供的核心类，减少代码冗余，提高维护效率。

---

## 二、依赖变更

### 新增依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3.1</version>
</dependency>
```

> **注意**: 所有使用 skill-common 2.3.1 的项目将自动获得 scene-engine 2.3.1 的传递依赖。

---

## 三、移除的类

以下类已被移除，请使用 scene-engine 中的对应实现：

| 移除的类 | 替代方案 | 说明 |
|---------|---------|------|
| `net.ooder.skill.common.Result` | `net.ooder.scene.core.Result` | 统一返回结果类 |
| `net.ooder.skill.common.SkillContext` | `agent-sdk` 中的实现 | Skill 上下文类 |

### 迁移指南

#### Result 类迁移

```java
// 旧代码 (已移除)
import net.ooder.skill.common.Result;

// 新代码
import net.ooder.scene.core.Result;

// 使用方式基本一致
Result.success(data);
Result.error("错误信息");
Result.error(500, "错误信息");
```

#### SkillContext 类迁移

```java
// 旧代码 (已移除)
import net.ooder.skill.common.SkillContext;

// 新代码 - 使用 agent-sdk 提供的实现
import net.ooder.agent.sdk.context.SkillContext;
```

---

## 四、保留的类 (无变更)

以下类虽然与 scene-engine 存在相似实现，但因业务差异保留：

| 类名 | 保留原因 |
|-----|---------|
| `LoginRequest` | 本地版本包含 `role` 字段用于角色选择，scene-engine 版本字段不同 |
| `UserSession` | 包含 `roleType`、`departmentId`、`departmentName` 等业务字段 |
| `ResultModel` | 包含 `requestId`、`timestamp` 等额外字段，用于 API 响应 |
| `JsonStorage` | 通用 JSON 文件存储工具 |
| `JsonStorageService` | 通用存储服务，scene-engine 的 `ContextStorageService` 专门用于上下文存储 |
| `DiscoveryResult` | 用于技能发现，与 scene-engine 的 `DiscoveryProvider` 用途不同 |
| `CapabilityDTO` | 技能发现数据传输对象 |

---

## 五、升级步骤

### 1. 更新依赖版本

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 2. 检查并替换已移除的类

搜索项目中以下 import 语句并替换：

```
net.ooder.skill.common.Result → net.ooder.scene.core.Result
net.ooder.skill.common.SkillContext → net.ooder.agent.sdk.context.SkillContext
```

### 3. 验证编译

```bash
mvn clean compile
```

---

## 六、兼容性说明

| 兼容性项 | 状态 |
|---------|------|
| 二进制兼容 | ❌ 不兼容 (移除了类) |
| 源码兼容 | ❌ 不兼容 (需要修改 import) |
| 行为兼容 | ✅ 兼容 (替代类行为一致) |

---

## 七、FAQ

### Q1: 为什么要移除这些类？

**A**: scene-engine 2.3.1 已经提供了这些核心类的完整实现，保留重复实现会增加维护成本，且容易导致版本不一致问题。

### Q2: 如果我的项目已经依赖 scene-engine 怎么办？

**A**: 没有冲突。skill-common 2.3.1 现在显式依赖 scene-engine 2.3.1，Maven 会自动处理依赖关系。

### Q3: Result 和 ResultModel 有什么区别？

**A**: 
- `Result` (scene-engine): 简洁的通用返回结果，适用于 Provider 接口
- `ResultModel` (skill-common): 包含更多元数据 (requestId, timestamp)，适用于 REST API 响应

### Q4: 遇到编译错误怎么办？

**A**: 
1. 确保本地 Maven 仓库有 scene-engine 2.3.1
2. 执行 `mvn clean install -U` 强制更新依赖
3. 检查是否有直接 import 已移除的类

---

## 八、联系方式

如有问题，请联系：
- **技术支持**: ooder-team@ooder.net
- **问题反馈**: GitHub Issues

---

*本变更说明由 ooder 团队发布*
