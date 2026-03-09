# Scene-Engine 包结构重构建议报告

> **编写日期**: 2026-03-09

---

## 一、重复类名分析

### 1.1 发现的重复类

| 类名 | 包路径1 | 包路径2 | 风险等级 |
|------|---------|---------|----------|
| **AuditLog** | `net.ooder.scene.core` | `net.ooder.scene.core.security` | 🟡 中 |
| **AuditService** | `net.ooder.scene.audit` | `net.ooder.scene.core.security` | 🟡 中 |
| **UnifiedSceneService** | `net.ooder.scene.service` | `net.ooder.scene.core.service` | 🟡 中 |
| **HealthStatus** | `net.ooder.scene.core` | `net.ooder.scene.provider` | 🟡 中 |
| **SystemStatus** | `net.ooder.scene.provider` | - | 仅一处 |
| **SecurityStatus** | `net.ooder.scene.provider` | - | 仅一处 |

---

## 二、重构建议

### 2.1 AuditLog 重复

**现状**:
```
net.ooder.scene.core.AuditLog                    # 通用审计日志
net.ooder.scene.core.security.AuditLog           # 安全审计日志
```

**建议**: 合并为单一 AuditLog

```
net.ooder.scene.core.AuditLog
├── type: AuditType (GENERAL, SECURITY)
├── targetType: String
└── ...
```

**工作量**: 0.5人天

---

### 2.2 AuditService 重复

**现状**:
```
net.ooder.scene.audit.AuditService              # 审计服务
net.ooder.scene.core.security.AuditService       # 安全审计服务
```

**建议**: 合并为单一 AuditService

```
net.ooder.scene.core.AuditService
├── log(AuditLog)                              # 通用方法
├── logSecurity(SecurityAuditLog)              # 安全专用方法
└── ...
```

**工作量**: 0.5人天

---

### 2.3 UnifiedSceneService 重复

**现状**:
```
net.ooder.scene.service.UnifiedSceneService     # 应用层统一服务
net.ooder.scene.core.service.UnifiedSceneService # 核心层统一服务
```

**建议**: 分离职责

```
# 核心层 - 纯业务逻辑，无状态
net.ooder.scene.core.service.UnifiedSceneService

# 应用层 - 事务编排、缓存
net.ooder.scene.service.UnifiedSceneFacade
```

**工作量**: 1人天

---

### 2.4 HealthStatus 重复

**现状**:
```
net.ooder.scene.core.HealthStatus              # 核心层健康状态
net.ooder.scene.provider.HealthStatus          # 提供者健康状态
```

**建议**: 分离为不同类型

```
# 核心层 - 引擎健康状态
net.ooder.scene.core.EngineHealthStatus

# 提供者层 - 服务健康状态
net.ooder.scene.provider.HealthStatus
```

**工作量**: 0.5人天

---

## 三、包结构优化建议

### 3.1 当前包结构

```
net.ooder.scene/
├── core/                    # 核心接口
├── service/                 # 业务服务
├── skill/                   # 技能层
├── discovery/               # 发现层
├── event/                   # 事件层
├── llm/                     # LLM层
├── provider/                # 提供者层
├── session/                 # 会话层
├── monitor/                 # 监控层
├── protocol/                # 协议层
├── asset/                   # 资产层
├── audit/                   # 审计层
└── engine/                  # 引擎层
```

### 3.2 建议包结构（按分层原则）

```
net.ooder.scene/
├── api/                     # 外部API入口
│   ├── SceneEngine.java
│   ├── SceneClient.java
│   └── AdminClient.java
│
├── core/                    # 核心领域
│   ├── lifecycle/          # 生命周期管理
│   ├── context/             # 上下文管理
│   ├── activation/          # 激活流程
│   ├── decision/            # 决策引擎
│   └── security/            # 安全服务
│
├── service/                 # 业务服务层
│   ├── push/                # 推送服务
│   ├── reminder/            # 提醒服务
│   └── journal/            # 日志服务
│
├── skill/                   # 技能管理
│   ├── registry/            # 注册发现
│   ├── runtime/             # 运行时
│   ├── knowledge/           # 知识能力
│   └── vector/              # 向量存储
│
├── infrastructure/           # 基础设施层
│   ├── provider/            # 提供者
│   ├── session/             # 会话
│   ├── monitor/             # 监控
│   ├── protocol/            # 协议
│   ├── event/               # 事件
│   └── persistence/          # 持久化
│
└── config/                   # 配置
```

---

## 四、优先级建议

| 优先级 | 任务 | 工作量 | 收益 |
|--------|------|--------|------|
| **P0** | 统一 AuditLog/AuditService | 1人天 | 消除歧义 |
| **P1** | 分离 UnifiedSceneService | 1人天 | 职责清晰 |
| **P2** | HealthStatus 重命名 | 0.5人天 | 减少重复 |
| **P3** | 包结构重组 | 3人天 | 长期可维护 |

---

## 五、总结

### 当前状态

- 总类数: 约 200+ 个
- 重复类: 4 组
- 包层级: 3 层

### 重构收益

- 消除命名歧义
- 明确职责边界
- 提升可维护性
- 便于模块拆分

### 风险控制

1. 保持向后兼容
2. 逐步迁移而非一次性重构
3. 添加 @Deprecated 注解标记旧接口

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-09
