# Scene-Engine 与 Agent-SDK 协作说明

## 一、背景

Scene-Engine 项目已完成对 Agent-SDK 2.3 的依赖升级和重复代码清理工作。在迁移过程中，发现以下需要 SDK 团队协助的事项。

---

## 二、需要 SDK 增强的功能点

### 2.1 MemberRole 枚举缺少 MEMBER 值

**问题描述：**

| 项目 | 枚举值 |
|------|--------|
| Scene-Engine MemberRole | PRIMARY, BACKUP, OBSERVER, **MEMBER** |
| SDK MemberRole | PRIMARY, BACKUP, OBSERVER |

**影响：**
- Scene-Engine 中使用 `MemberRole.MEMBER` 表示普通成员角色
- 当前需要在 Scene-Engine 中保留 deprecated 的 MemberRole 类进行转换

**建议：**
```java
// SDK 中增加 MEMBER 枚举值
public enum MemberRole {
    PRIMARY("primary", "Primary agent with full control"),
    BACKUP("backup", "Backup agent ready for failover"),
    OBSERVER("observer", "Observer agent with read-only access"),
    MEMBER("member", "Regular member with limited access");  // 建议新增
}
```

---

### 2.2 SceneConfig 字段扩展

**问题描述：**

| 字段 | Scene-Engine 版本 | SDK 版本 |
|------|------------------|----------|
| sceneId | 无（使用 configId） | 有 |
| sceneName | 无 | 有 |
| configId | 有 | 无 |
| properties | 有 | 有 |
| interfaceBindings | 无 | 有 |
| autoStart | 无 | 有 |
| startupTimeout | 无 | 有 |
| shutdownTimeout | 无 | 有 |

**建议：**
- SDK SceneConfig 增加 `configId` 字段用于配置标识
- 或 Scene-Engine 直接使用 SDK 版本的 SceneConfig

---

### 2.3 CapRegistry 接口与实现分离

**当前状态：**
- SDK 提供 `CapRegistry` 接口和 `InMemoryCapRegistry` 实现
- Scene-Engine 有自己的简化版 `CapRegistry` 类

**建议：**
- Scene-Engine 将逐步迁移到使用 SDK 的 `InMemoryCapRegistry`
- SDK 确保 `InMemoryCapRegistry` 支持版本管理功能

---

## 三、已处理的重复类

以下类已在 Scene-Engine 中标记 `@Deprecated` 并实现 SDK 接口：

| 类名 | 处理方式 | SDK 替代类 |
|------|---------|-----------|
| `CapRegistry` | 实现 SDK 接口 | `net.ooder.sdk.api.capability.CapRegistry` |
| `CapabilityInfo` | 添加转换方法 | `net.ooder.sdk.api.capability.Capability` |
| `MemberRole` | 添加转换方法 | `net.ooder.sdk.common.enums.MemberRole` |
| `Driver` | 继承 SDK 类 | `net.ooder.sdk.api.driver.Driver` |
| `HealthStatus` | 继承 SDK 类 | `net.ooder.sdk.capability.model.HealthStatus` |
| `InterfaceDefinition` | 继承 SDK 类 | `net.ooder.sdk.core.InterfaceDefinition` |

---

## 四、Scene-Engine 内部重复类（待清理）

以下类在 Scene-Engine 内部存在重复定义，需要合并：

| 重复类 | 位置1 | 位置2 | 建议保留 |
|--------|-------|-------|---------|
| UserInfo | core/UserInfo.java | provider/model/user/UserInfo.java | provider 版本 |
| Permission | core/security/Permission.java | provider/model/user/Permission.java | provider 版本 |
| AuditLog | core/AuditLog.java | core/security/AuditLog.java | security 版本 |
| SecurityConfig | core/security/SecurityConfig.java | provider/model/config/SecurityConfig.java | provider 版本 |
| SystemStatus | provider/SystemStatus.java | provider/model/network/SystemStatus.java | provider 版本 |
| EngineStatus | core/EngineStatus.java | engine/EngineStatus.java | engine 版本 |
| SystemConfig | core/SystemConfig.java | provider/model/config/SystemConfig.java | provider 版本 |

---

## 五、依赖关系确认

**当前依赖：**
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>2.3</version>
</dependency>
```

**编译状态：** ✅ 通过

---

## 六、协作请求

### 优先级 P0（阻塞）
1. 确认 `MemberRole.MEMBER` 是否可以添加到 SDK

### 优先级 P1（重要）
2. 确认 `SceneConfig` 字段扩展方案
3. 确认 `InMemoryCapRegistry` 是否支持版本管理

### 优先级 P2（建议）
4. 考虑在 SDK 中提供统一的转换工具类
5. 考虑在 SDK 中提供 Builder 模式创建复杂对象

---

## 七、联系方式

- Scene-Engine 负责人：[请填写]
- SDK 负责人：[请填写]
- 文档日期：2025-02-27

---

## 附录：转换方法示例

```java
// MemberRole 转换
MemberRole localRole = MemberRole.PRIMARY;
net.ooder.sdk.common.enums.MemberRole sdkRole = localRole.toSdkMemberRole();

// 从 SDK 转换
MemberRole converted = MemberRole.fromSdkMemberRole(sdkRole);

// CapabilityInfo 转换
CapabilityInfo localInfo = new CapabilityInfo("cap-001", "test-cap");
net.ooder.sdk.api.capability.Capability sdkCap = localInfo.toSdkCapability();
```
