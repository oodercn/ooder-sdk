# Ooder SDK v2.3.1 发布说明

**发布日期**: 2026-03-10  
**版本号**: 2.3.1  
**状态**: 正式发布

---

## 概述

Ooder SDK v2.3.1 是 v2.3 的维护版本，主要包含 bug 修复、稳定性改进和性能优化。此版本保持与 v2.3 的 API 兼容性，建议所有用户升级。

---

## 主要变更

### 🔧 Bug 修复

#### 1. JDSConfig 配置问题修复
- **问题**: `currServerHome()` 方法在 `JDSHome` 未设置时抛出 NullPointerException
- **修复**: 增加默认值 `./JDSHome`，自动创建目录结构
- **影响模块**: `ooder-common-client`

#### 2. JDSInit 类加载异常处理优化
- **问题**: `net.ooder.JDSInit` 类不存在时打印完整堆栈信息
- **修复**: 改为警告日志输出，避免噪音
- **影响模块**: `ooder-common-client`

#### 3. LLM 代理层连接池稳定性改进
- **问题**: 相同 LLM 配置的 Agent 未能正确共享连接池
- **修复**: 优化 `LlmConnectionPoolKey` 的 equals/hashCode 实现
- **影响模块**: `scene-engine`

### ⚡ 性能优化

#### 1. Maven 编译速度优化
- **优化**: 在 `local` profile 中禁用 Javadoc 插件
- **效果**: 编译时间从 7分56秒减少到 3分35秒（提升约55%）
- **涉及文件**: 根 `pom.xml` 及多个子模块 `pom.xml`

#### 2. LLM 连接池引用计数优化
- **优化**: 改进连接池生命周期管理，无引用时自动关闭
- **效果**: 减少资源泄漏风险
- **影响模块**: `scene-engine`

### 📚 文档更新

#### 1. Skills 规范配置文档
- **新增**: 三闭环检查要求（生命周期、数据实体、按钮API）
- **新增**: 字典表规范与使用指南
- **新增**: API 响应格式规范
- **新增**: 命名规范和开发流程规范
- **位置**: `agent-sdk/docs/SDK_INJECTION_SECONDARY_DEVELOPMENT_GUIDE.md`

#### 2. LLM 代理层架构文档
- **新增**: 分层架构设计说明（用户-Agent-连接三层隔离）
- **新增**: 连接池共享机制说明
- **新增**: 配额管理设计文档
- **位置**: `scene-engine/docs/LAYERED_ARCHITECTURE_DESIGN.md`

---

## 新功能

### 🆕 SceneEngine LLM 代理层 (Beta)

**功能描述**: 提供用户-Agent-连接三层隔离的 LLM 服务架构

**核心组件**:
- `LlmConnectionManager` - 连接管理层，支持连接池共享
- `AgentSessionManager` - Agent 会话管理层，四级缓存设计
- `UserLlmSessionManager` - 用户会话和配额管理层
- `SceneEngineLlmProxy` - 统一入口类

**特性**:
- ✅ 相同 LLM 配置的 Agent 共享连接池
- ✅ Agent 级配额管理（Token/对话数）
- ✅ 用户级配额管理（Agent数/对话数/Token）
- ✅ 对话内存隔离
- ✅ 生命周期监听机制
- ✅ 监控和统计功能

**使用示例**:
```java
// 创建代理层
SceneEngineLlmProxy proxy = new SceneEngineLlmProxy();
proxy.start();

// 创建 Agent
AgentLlmSessionHandle agent = proxy.createAgent(
    sessionId, "assistant", llmConfig, 
    AgentCreationOptions.defaults()
        .dailyTokenLimit(100000)
        .maxConversations(10)
);

// 对话
String response = proxy.chatWithAgent(agent.getAgentId(), "Hello!");
```

**状态**: Beta 版本，欢迎反馈问题

---

## 兼容性

### 向后兼容
- ✅ 与 v2.3 完全 API 兼容
- ✅ 数据库结构无变化
- ✅ 配置文件格式无变化

### 依赖要求
- Java 8+
- Maven 3.6+
- Spring Boot 2.7.x (可选)

---

## 升级指南

### Maven 依赖升级

```xml
<!-- 父 POM -->
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk-parent</artifactId>
    <version>2.3.1</version>
</parent>

<!-- 或具体模块 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>2.3.1</version>
</dependency>
```

### 本地安装

```bash
# 下载源码后本地安装
git clone https://github.com/oodercn/ooder-sdk.git
cd ooder-sdk
git checkout v2.3.1
mvn clean install -DskipTests
```

---

## 已知问题

### 🐛 已知限制

1. **SceneEngine LLM 代理层**: 目前仅支持同步对话，流式对话 API 待完善
2. **连接池监控**: 监控数据持久化功能待实现
3. **配额重置**: 每日配额重置需手动触发或配置定时任务

### 📝 计划修复

- v2.3.2: 完善 LLM 代理层流式对话支持
- v2.3.2: 增加连接池监控数据持久化
- v2.4.0: 引入自动配额重置机制

---

## 贡献者

感谢以下贡献者对本版本的贡献：

- **ENGINE Team**: JDSConfig 修复和稳定性改进
- **SDK Team**: LLM 代理层架构设计和实现
- **QA Team**: 测试和问题反馈

---

## 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 快速开始 | `docs/guides/QUICK_START.md` | 快速入门指南 |
| 架构指南 | `ARCHITECTURE_GUIDE.md` | 整体架构说明 |
| 二次开发手册 | `DEVELOPMENT_GUIDE.md` | 二次开发指南 |
| Skills 协作 | `SKILLS_COLLABORATION.md` | Skills 开发协作规范 |
| 变更日志 | `CHANGELOG.md` | 详细变更历史 |
| v2.3 发布说明 | `RELEASE_NOTES_v2.3.md` | 上一版本说明 |

---

## 反馈与支持

- **GitHub Issues**: https://github.com/oodercn/ooder-sdk/issues
- **文档反馈**: docs@ooder.net
- **技术支持**: support@ooder.net

---

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

---

**Ooder Team**  
2026-03-10
