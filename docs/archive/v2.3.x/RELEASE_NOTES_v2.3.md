# Ooder SDK v2.3 版本发布说明

**发布日期**: 2026-02-27  
**版本号**: 2.3  
**状态**: 正式发布

---

## 一、版本概述

Ooder SDK v2.3 是一个重要的架构升级版本。本版本主要聚焦于:

1. **架构重构**: agent-sdk 拆分为 api/core/skills-framework/llm-sdk 模块
2. **模块精简**: 移除冗余模块(vfs-skill, org-skill, skills等)
3. **版本统一**: 所有模块版本统一为2.3,简化依赖管理
4. **文档完善**: 补充核心接口和类的注释

---

## 二、主要变更

### 2.1 模块结构调整

#### 删除的模块
- `vfs-skill` - 迁移到 ooder-skills 仓库
- `org-skill` - 迁移到 ooder-skills 仓库
- `skills` - 功能合并到 agent-sdk
- `agent-sdk/scene-engine-core` - 功能合并到 scene-engine
- `ooder-common/ooder-database` - 暂时移除
- `ooder-common/ooder-index-web` - 暂时移除
- `ooder-common/ooder-iot-webclient` - 暂时移除
- `ooder-infra-core` - 移除
- `ooder-infra-driver` - 移除
- `ooder-codegen` - 移除
- `ooder-codegen-cli` - 移除

#### agent-sdk 新结构
```
agent-sdk/
├── agent-sdk-api/          # API接口层
├── agent-sdk-core/         # 核心实现层
├── skills-framework/       # 技能框架
├── llm-sdk-api/            # LLM轻量级API
└── llm-sdk/                # LLM完整实现
```

### 2.2 版本统一

所有模块版本统一为 **2.3**:

| 模块 | 旧版本 | 新版本 |
|------|--------|--------|
| agent-sdk-api | 0.7.3 | 2.3 |
| agent-sdk-core | 0.7.3 | 2.3 |
| skills-framework | 0.7.3 | 2.3 |
| llm-sdk-api | 0.7.3 | 2.3 |
| llm-sdk | 0.7.3 | 2.3 |
| scene-engine | 0.7.3 | 2.3 |
| ooder-annotation | 2.2 | 2.3 |
| ooder-common | 2.1 | 2.3 |

### 2.3 注释补充

为核心接口和类补充了 JavaDoc 注释:
- `Capability` / `CapabilityStatus` / `CapabilityType`
- `SceneManager` / `SceneDefinition` / `SceneGroupManager`
- `AgentType` / `MemberRole` / `SceneType`
- `SkillService` / `SessionInfo`

---

## 三、兼容性说明

### 3.1 向后兼容

- **API兼容**: 核心API保持不变
- **依赖变更**: 需要更新Maven依赖坐标

### 3.2 迁移指南

#### 更新Maven依赖

**变更前**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>vfs-skill</artifactId>
    <version>2.2</version>
</dependency>
```

**变更后**:
```xml
<!-- 移除 vfs-skill 依赖，使用 scene-engine 提供的 VFS 能力 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>
```

---

## 四、系统要求

- **Java版本**: Java 8+
- **Spring Boot**: 2.7.0+
- **Maven**: 3.6+

---

## 五、依赖引用

### Maven

```xml
<!-- scene-engine -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>2.3</version>
</dependency>

<!-- agent-sdk-core (完整SDK) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>

<!-- agent-sdk-api (仅API接口) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-api</artifactId>
    <version>2.3</version>
</dependency>

<!-- llm-sdk (LLM完整实现) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>llm-sdk</artifactId>
    <version>2.3</version>
</dependency>

<!-- skills-framework (技能框架) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skills-framework</artifactId>
    <version>2.3</version>
</dependency>

<!-- ooder-annotation (注解定义) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-annotation</artifactId>
    <version>2.3</version>
</dependency>

<!-- ooder-common子模块 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.3</version>
</dependency>
```

### 依赖关系说明

```
上层应用
    ↓ 依赖
scene-engine
    ↓ 依赖
agent-sdk-core
    ↓ 依赖
agent-sdk-api + skills-framework + llm-sdk-api + llm-sdk
    ↓ 依赖
ooder-common-* (ooder-config, ooder-common-client, ooder-server等)
    ↓ 依赖
ooder-api + ooder-util + ooder-annotation
```

**注意**: `agent-sdk` 是父工程(pom类型),不能直接作为依赖使用。请依赖具体的子模块如 `agent-sdk-core`。

---

## 六、已知问题

暂无

---

## 七、问题反馈

如有问题,请联系:
- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues
- **邮箱**: team@ooder.net

---

## 八、致谢

感谢所有贡献者和用户的支持!

---

**发布日期**: 2026-02-27  
**文档版本**: 1.0
