# ooderAgent 企业版 2.0 开发包 MIT 协议发布声明

**发布日期**: 2026年2月8日  
**版本号**: 2.0  
**开源协议**: MIT License  
**GitHub仓库**: https://github.com/ooder-net/ooder-common  
**Maven中央仓库**: https://central.sonatype.com/artifact/net.ooder

---

## 一、发布概述

ooderAgent 企业版 2.1 开发套包（以下简称"本开发包"）正式以 **MIT 开源协议** 面向全球企业开发者发布。本开发包是 ooder 技术团队历经多年企业级项目实践沉淀的核心基础框架，旨在为企业数字化转型提供稳定、高效、可扩展的技术底座。

本版本 2.1 是在 2.0 基础上的稳定维护版本，主要修复了模块配置问题并优化了依赖管理。

---

## 二、发布目的

### 2.1 面向企业开发者

本开发包专为企业级应用场景设计，提供以下核心能力：

- **数据库管理**: 企业级数据库连接池管理、DAO工厂模式、SQL解析与元数据管理
- **缓存管理**: Redis分布式缓存、连接池优化、集群模式支持
- **文件存储**: VFS虚拟文件系统、分布式存储访问、版本控制与协作
- **组织架构**: 企业组织机构管理、人员权限体系、部门岗位配置
- **消息通信**: MQTT协议支持、消息队列管理、IoT设备消息处理
- **向量检索**: 非结构化数据存储、相似度检索、全文检索支持
- **微服务支撑**: 服务注册发现、集群管理、HTTP代理网关
- **物联网**: 北向协议转换、设备管理、数据采集与远程控制

### 2.2 完全自主可控

- **100%开源**: 所有源代码完全开放，无任何闭源组件
- **自主可控**: 企业可自由修改、定制、扩展，无需担心供应商锁定
- **合规保障**: 严格遵循开源规范，通过Maven Central完整合规检测

---

## 三、企业版 2.0 完整组成与能力

### 3.1 ooderAgent 北向（集中式）协议实现

ooderAgent 企业版 2.0 提供完整的北向协议实现，构建企业级 AI 能力中台：

| 组件 | 功能说明 |
|------|----------|
| **数据中心 (VFS)** | 分布式虚拟文件系统，支持企业级数据存储、版本控制、协作共享 |
| **安全认证中心** | 统一身份认证与权限管理，支持多租户、多组织、细粒度权限控制 |
| **Agent 管理中心** | Agent 全生命周期管理，包括注册、监控、调度、扩缩容 |
| **能力管理中心 (SkillsCenter)** | Skill 能力注册、发现、编排、版本管理，实现能力复用 |
| **Skillflow 流程调度中心** | 可视化流程编排引擎，支持复杂业务流程的自动化执行 |
| **oodAI (大脑)** | 核心 AI 推理引擎，支持多模型接入、智能决策、知识推理 |

### 3.2 三端开发依赖包

企业版 2.1 开发套包涵盖 **客户端 Agent SDK**、**企业端服务套件**、**ooderAI 大脑端** 三端的完整开发依赖：

```
┌─────────────────────────────────────────────────────────────┐
│                    ooderAgent 企业版 2.1                     │
├─────────────────┬─────────────────┬─────────────────────────┤
│   客户端 Agent   │   企业端服务     │      ooderAI 大脑端      │
│     SDK         │     套件         │                         │
├─────────────────┼─────────────────┼─────────────────────────┤
│ • ooder-config  │ • ooder-server  │ • ooder-index-web       │
│ • ooder-common- │ • ooder-org-web │   (向量检索)             │
│   client        │ • ooder-msg-web │ • ooder-iot-webclient   │
│ • ooder-database│ • ooder-vfs-web │   (IoT协议转换)          │
│                 │                 │                         │
│ 功能：设备接入   │ 功能：服务支撑   │ 功能：AI能力支撑         │
│ 数据采集        │ 组织管理        │ 智能推理                 │
│ 边缘计算        │ 消息通信        │ 知识管理                 │
│ 本地缓存        │ 文件存储        │ 流程调度                 │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### 3.3 二次开发与深度集成

企业用户可通过本套件提供的基础 API，更深层次地完成对 ooderAgent 的二次开发：

- **深度定制**: 基于开源代码自由修改，深度集成企业自身业务特点
- **能力扩展**: 通过 SkillsCenter 扩展自定义能力，构建企业专属 AI 能力库
- **流程编排**: 利用 Skillflow 可视化编排复杂业务流程，实现业务自动化
- **数据打通**: 通过 VFS 数据中心实现企业数据资产的统一管理与智能利用
- **安全集成**: 对接企业现有 IAM 体系，实现无缝安全集成

---

## 四、版本号统一说明

### 4.1 为什么从 2.0 开始

自本版本起，**ooderAgent 企业版与内部版本号完全统一**，不再区分内外部版本。这一决策基于以下考量：

1. **版本一致性**: 消除内外部版本差异带来的混淆，统一版本认知
2. **全开源战略**: 标志着 ooderAgent 正式进入全开源阶段，内部与外部版本完全一致
3. **生态共建**: 便于企业开发者与核心团队基于同一版本进行协作和贡献
4. **透明治理**: 版本演进过程完全公开，接受社区监督

当前版本 **2.1** 是在 2.0 基础上的稳定维护版本。

### 4.2 与 ooderAgent 的关系

本开发包是 **ooderAgent 企业级基础框架** 的核心组成部分：

- **ooderAgent**: 面向AI Agent和自动化流程的SuperAgent协议实现
- **ooder-common (企业版)**: 面向企业应用开发的基础技术底座

两者共享同一版本号体系，确保技术栈的一致性和兼容性。

---

## 四、代码仓库与分发

### 4.1 GitHub 仓库

本开发包唯一官方源码托管平台为 **GitHub**：

- **仓库地址**: https://github.com/ooder-net/ooder-common
- **协议**: HTTPS / SSH
- **许可证**: MIT License (详见 [LICENSE](LICENSE) 文件)

> **注意**: 企业版仅提供 GitHub 官方仓库，请认准 `ooder-net` 组织下的官方仓库，谨防钓鱼仓库。

### 4.2 Maven 中央仓库

本开发包已完整发布至 **Maven Central** 官方中央仓库：

```xml
<!-- 基础配置包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.1</version>
</dependency>

<!-- 核心客户端包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>2.1</version>
</dependency>

<!-- 数据库管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-database</artifactId>
    <version>2.1</version>
</dependency>

<!-- VFS存储管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-vfs-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 组织机构管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-org-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 消息通信管理包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 向量检索包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-index-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- 微服务支撑包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-server</artifactId>
    <version>2.1</version>
</dependency>

<!-- IoT北向协议转换包 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-iot-webclient</artifactId>
    <version>2.1</version>
</dependency>
```

**Maven Central 搜索**: https://search.maven.org/search?q=g:net.ooder

---

## 五、企业版与个人版区别

### 5.1 企业版定位

**核心目标**: 兼容性和完全自主可控

| 特性 | 企业版 2.1 | 说明 |
|------|-----------|------|
| **JDK版本** | Java 8+ | 保持向后兼容，支持传统企业环境 |
| **依赖策略** | 适度依赖成熟开源组件 | 优先选用经过验证的稳定依赖 |
| **发布节奏** | 稳定迭代，严格测试 | 每个版本经过完整回归测试 |
| **开源规范** | 100%符合Maven Central规范 | 源码、Javadoc、GPG签名齐全 |
| **发展方向** | 以实用企业开发插件为主线横向扩展 | 覆盖更多企业应用场景 |

### 5.2 个人版定位

**核心目标**: 开箱即用和快速迭代

| 特性 | 个人版 | 说明 |
|------|--------|------|
| **JDK版本** | Java 17+ | 采用最新LTS版本，享受新特性 |
| **依赖策略** | 尽可能减少外部依赖 | 轻量级，降低依赖冲突风险 |
| **发布节奏** | 快速发版，敏捷迭代 | 新特性快速上线 |
| **使用方式** | 开箱即用 | 简化配置，降低使用门槛 |
| **发展方向** | 精简核心，快速验证 | 适合个人项目和小型应用 |

### 5.3 版本选择建议

- **企业级应用 / 大型项目**: 推荐使用 **企业版 2.1**，注重稳定性和兼容性
- **个人学习 / 小型项目**: 推荐使用 **个人版**，享受最新技术特性
- **长期维护项目**: 推荐使用 **企业版 2.1**，版本稳定，文档完善

---

## 六、MIT 协议声明

本开发包采用 **MIT License** 开源协议，协议全文如下：

```
MIT License

Copyright (c) 2026 ooder

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### 6.1 协议要点

- **自由使用**: 可自由用于商业或非商业项目
- **自由修改**: 可修改源码以满足特定需求
- **自由分发**: 可重新分发原始或修改后的版本
- **自由闭源**: 基于本开发包的衍生作品可以闭源
- **保留声明**: 需保留原始版权声明和许可声明

---

## 七、技术支持与社区

### 7.1 官方渠道

- **官方网站**: https://ooder.net
- **GitHub仓库**: https://github.com/oodercn/ocommon
- **问题反馈**: https://github.com/oodercn/common/issues
- **联系邮箱**: team@ooder.net

### 7.2 参与贡献

我们欢迎社区贡献！请通过以下方式参与：

1. **提交 Issue**: 报告Bug或提出功能建议
2. **提交 PR**: 修复问题或实现新功能
3. **完善文档**: 补充使用文档和示例代码
4. **分享经验**: 在社区分享使用心得和最佳实践

---

## 八、免责声明

本开发包按 **"原样"** 提供，不提供任何形式的明示或暗示担保，包括但不限于对适销性、特定用途适用性和非侵权性的担保。在任何情况下，作者或版权持有人均不对任何索赔、损害或其他责任负责，无论是在合同诉讼、侵权诉讼或其他诉讼中，还是与本开发包或本开发包的使用或其他交易有关。

---

## 九、致谢

感谢所有为 ooderAgent 项目做出贡献的开发者！

特别感谢：
- 开源社区的优秀项目和工具
- 企业用户的信任和支持
- 开发团队的辛勤付出

---

**ooderAgent 企业版 2.0** - 让企业级开发更简单、更可控 🚀

---

*本声明最终解释权归 ooder 团队所有*
