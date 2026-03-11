# 能力地址空间协作声明

> **文档版本**: 2.0.0  
> **创建日期**: 2026-03-11  
> **发起团队**: Engine Team (scene-engine)  
> **目标团队**: Skills Team, Agent-SDK Team  
> **状态**: 待确认

---

## 一、协作背景

### 1.1 设计目标

实现统一的能力地址空间，支持：
- 固定地址调用（离线可用）
- 多层配置管理（MCP/ROUTE）
- 上下文隔离（多实例、多租户）
- 持久化与恢复

### 1.2 设计原则

| 原则 | 说明 |
|------|------|
| **底层减少不确定性** | 地址固定、枚举定义、无动态计算 |
| **需求固定** | 系统支持范围固定，扩充需大版本 |
| **对外输出最小化** | 每地址一个原子能力，复杂能力用参数区分 |
| **地址固定，配置动态** | 地址编译时确定，配置运行时管理 |
| **上下文区分实例** | 多实例通过上下文区分，不分配独立地址 |

---

## 二、完整地址分配表

### 2.1 固定地址区 (0x00-0x7F)

**共 128 个地址，16 个分类，每分类 8 个地址**

#### SYS - 系统核心 (0x00-0x07)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x00 | SYS_CORE | 系统核心 | 核心系统服务 |
| 0x01 | SYS_CONFIG | 系统配置 | 配置管理服务 |
| 0x02 | SYS_LICENSE | 许可证 | 许可证管理 |
| 0x03 | SYS_TENANT | 租户管理 | 多租户管理 |
| 0x04 | SYS_CACHE | 系统缓存 | 缓存服务 |
| 0x05 | SYS_LOCK | 分布式锁 | 锁服务 |
| 0x06 | SYS_ID | ID生成 | 分布式ID |
| 0x07 | SYS_RESERVED | 系统预留 | 预留 |

#### ORG - 组织服务 (0x08-0x0F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x08 | ORG_LOCAL | 本地组织 | 本地组织管理 |
| 0x09 | ORG_DINGDING | 钉钉组织 | 钉钉组织同步 |
| 0x0A | ORG_FEISHU | 飞书组织 | 飞书组织同步 |
| 0x0B | ORG_WECOM | 企业微信 | 企业微信组织 |
| 0x0C | ORG_LDAP | LDAP | LDAP目录服务 |
| 0x0D | ORG_AD | Active Directory | AD域服务 |
| 0x0E | ORG_CUSTOM | 自定义组织 | 自定义组织源 |
| 0x0F | ORG_RESERVED | 组织预留 | 预留 |

#### AUTH - 认证服务 (0x10-0x17)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x10 | AUTH_LOCAL | 本地认证 | 本地用户认证 |
| 0x11 | AUTH_OAUTH2 | OAuth2 | OAuth2认证 |
| 0x12 | AUTH_SAML | SAML | SAML认证 |
| 0x13 | AUTH_JWT | JWT | JWT令牌服务 |
| 0x14 | AUTH_SSO | 单点登录 | SSO服务 |
| 0x15 | AUTH_MFA | 多因素认证 | MFA服务 |
| 0x16 | AUTH_BIOMETRIC | 生物认证 | 生物特征认证 |
| 0x17 | AUTH_RESERVED | 认证预留 | 预留 |

#### VFS - 文件存储 (0x18-0x1F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x18 | VFS_LOCAL | 本地存储 | 本地文件系统 |
| 0x19 | VFS_DATABASE | 数据库存储 | 数据库BLOB存储 |
| 0x1A | VFS_MINIO | MinIO存储 | MinIO对象存储 |
| 0x1B | VFS_OSS | 阿里云OSS | 阿里云对象存储 |
| 0x1C | VFS_S3 | AWS S3 | AWS对象存储 |
| 0x1D | VFS_COS | 腾讯云COS | 腾讯云对象存储 |
| 0x1E | VFS_NAS | NAS存储 | 网络存储 |
| 0x1F | VFS_RESERVED | 存储预留 | 预留 |

#### DB - 数据库 (0x20-0x27)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x20 | DB_SQLITE | SQLite | SQLite数据库 |
| 0x21 | DB_MYSQL | MySQL | MySQL数据库 |
| 0x22 | DB_POSTGRESQL | PostgreSQL | PostgreSQL数据库 |
| 0x23 | DB_MONGODB | MongoDB | MongoDB文档库 |
| 0x24 | DB_REDIS | Redis | Redis缓存库 |
| 0x25 | DB_ELASTICSEARCH | Elasticsearch | ES搜索引擎 |
| 0x26 | DB_CLICKHOUSE | ClickHouse | ClickHouse分析库 |
| 0x27 | DB_RESERVED | 数据库预留 | 预留 |

#### LLM - 大语言模型 (0x28-0x2F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x28 | LLM_OLLAMA | Ollama | Ollama本地模型 |
| 0x29 | LLM_OPENAI | OpenAI | OpenAI GPT |
| 0x2A | LLM_QIANWEN | 通义千问 | 阿里云通义 |
| 0x2B | LLM_DEEPSEEK | DeepSeek | DeepSeek模型 |
| 0x2C | LLM_VOLCENGINE | 火山引擎 | 字节火山引擎 |
| 0x2D | LLM_ZHIPU | 智谱AI | 智谱GLM |
| 0x2E | LLM_BAIDU | 文心一言 | 百度文心 |
| 0x2F | LLM_RESERVED | LLM预留 | 预留 |

#### KNOW - 知识库 (0x30-0x37)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x30 | KNOW_VECTOR | 向量知识库 | 向量存储知识库 |
| 0x31 | KNOW_DOCUMENT | 文档知识库 | 文档存储知识库 |
| 0x32 | KNOW_GRAPH | 图谱知识库 | 知识图谱 |
| 0x33 | KNOW_RAG | RAG服务 | 检索增强生成 |
| 0x34 | KNOW_EMBEDDING | 嵌入服务 | 向量嵌入 |
| 0x35 | KNOW_CHUNK | 分块服务 | 文档分块 |
| 0x36 | KNOW_EXTRACT | 提取服务 | 信息提取 |
| 0x37 | KNOW_RESERVED | 知识库预留 | 预留 |

#### PAY - 支付服务 (0x38-0x3F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x38 | PAY_MOCK | 模拟支付 | 开发测试用 |
| 0x39 | PAY_ALIPAY | 支付宝 | 支付宝支付 |
| 0x3A | PAY_WECHAT | 微信支付 | 微信支付 |
| 0x3B | PAY_UNIONPAY | 银联支付 | 银联支付 |
| 0x3C | PAY_STRIPE | Stripe | Stripe国际支付 |
| 0x3D | PAY_PAYPAL | PayPal | PayPal支付 |
| 0x3E | PAY_CUSTOM | 自定义支付 | 自定义渠道 |
| 0x3F | PAY_RESERVED | 支付预留 | 预留 |

#### MEDIA - 媒体服务 (0x40-0x47)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x40 | MEDIA_WECHAT_MP | 微信公众号 | 微信公众号 |
| 0x41 | MEDIA_WEIBO | 微博 | 微博 |
| 0x42 | MEDIA_XIAOHONGSHU | 小红书 | 小红书 |
| 0x43 | MEDIA_ZHIHU | 知乎 | 知乎 |
| 0x44 | MEDIA_TOUTIAO | 今日头条 | 今日头条 |
| 0x45 | MEDIA_DOUYIN | 抖音 | 抖音 |
| 0x46 | MEDIA_BILIBILI | B站 | Bilibili |
| 0x47 | MEDIA_RESERVED | 媒体预留 | 预留 |

#### COMM - 通讯服务 (0x48-0x4F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x48 | COMM_MSG | 消息服务 | 消息推送 |
| 0x49 | COMM_NOTIFY | 通知服务 | 通知推送 |
| 0x4A | COMM_EMAIL | 邮件服务 | 邮件发送 |
| 0x4B | COMM_SMS | 短信服务 | 短信发送 |
| 0x4C | COMM_VOICE | 语音服务 | 语音通话 |
| 0x4D | COMM_VIDEO | 视频服务 | 视频通话 |
| 0x4E | COMM_IM | 即时通讯 | IM服务 |
| 0x4F | COMM_RESERVED | 通讯预留 | 预留 |

#### MON - 监控服务 (0x50-0x57)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x50 | MON_METRICS | 指标监控 | 指标采集 |
| 0x51 | MON_LOG | 日志服务 | 日志采集 |
| 0x52 | MON_TRACE | 链路追踪 | 分布式追踪 |
| 0x53 | MON_ALERT | 告警服务 | 告警通知 |
| 0x54 | MON_DASHBOARD | 仪表盘 | 监控面板 |
| 0x55 | MON_REPORT | 报表服务 | 报表生成 |
| 0x56 | MON_ANALYSIS | 分析服务 | 数据分析 |
| 0x57 | MON_RESERVED | 监控预留 | 预留 |

#### IOT - 物联网 (0x58-0x5F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x58 | IOT_DEVICE | 设备管理 | IoT设备管理 |
| 0x59 | IOT_GATEWAY | 网关服务 | IoT网关 |
| 0x5A | IOT_DATA | 数据采集 | 数据采集 |
| 0x5B | IOT_RULE | 规则引擎 | 规则处理 |
| 0x5C | IOT_SHADOW | 设备影子 | 影子服务 |
| 0x5D | IOT_OTA | OTA升级 | 固件升级 |
| 0x5E | IOT_EDGE | 边缘计算 | 边缘服务 |
| 0x5F | IOT_RESERVED | IoT预留 | 预留 |

#### SEARCH - 搜索服务 (0x60-0x67)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x60 | SEARCH_FULLTEXT | 全文搜索 | 全文检索 |
| 0x61 | SEARCH_VECTOR | 向量搜索 | 向量检索 |
| 0x62 | SEARCH_HYBRID | 混合搜索 | 混合检索 |
| 0x63 | SEARCH_SUGGEST | 搜索建议 | 自动补全 |
| 0x64 | SEARCH_AGGREGATE | 聚合分析 | 聚合统计 |
| 0x65 | SEARCH_RANK | 排序服务 | 结果排序 |
| 0x66 | SEARCH_INDEX | 索引服务 | 索引管理 |
| 0x67 | SEARCH_RESERVED | 搜索预留 | 预留 |

#### SCHED - 调度服务 (0x68-0x6F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x68 | SCHED_QUARTZ | Quartz调度 | Quartz定时任务 |
| 0x69 | SCHED_XXLJOB | XXL-JOB | XXL-JOB调度 |
| 0x6A | SCHED_DELAY | 延迟队列 | 延迟任务 |
| 0x6B | SCHED_CRON | Cron服务 | Cron表达式 |
| 0x6C | SCHED_WORKFLOW | 工作流调度 | 流程编排 |
| 0x6D | SCHED_BATCH | 批处理 | 批量任务 |
| 0x6E | SCHED_DAG | DAG调度 | 有向无环图 |
| 0x6F | SCHED_RESERVED | 调度预留 | 预留 |

#### SEC - 安全服务 (0x70-0x77)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x70 | SEC_ENCRYPT | 加密服务 | 数据加密 |
| 0x71 | SEC_DECRYPT | 解密服务 | 数据解密 |
| 0x72 | SEC_SIGN | 签名服务 | 数字签名 |
| 0x73 | SEC_VERIFY | 验签服务 | 签名验证 |
| 0x74 | SEC_KEY | 密钥管理 | 密钥服务 |
| 0x75 | SEC_CERT | 证书管理 | 证书服务 |
| 0x76 | SEC_AUDIT | 审计服务 | 安全审计 |
| 0x77 | SEC_RESERVED | 安全预留 | 预留 |

#### NET - 网络服务 (0x78-0x7F)

| 地址 | 代码 | 名称 | 说明 |
|:----:|------|------|------|
| 0x78 | NET_PROXY | 代理服务 | 网络代理 |
| 0x79 | NET_GATEWAY | 网关服务 | API网关 |
| 0x7A | NET_DNS | DNS服务 | 域名解析 |
| 0x7B | NET_LB | 负载均衡 | 负载均衡 |
| 0x7C | NET_TUNNEL | 隧道服务 | 网络隧道 |
| 0x7D | NET_VPN | VPN服务 | 虚拟网络 |
| 0x7E | NET_FIREWALL | 防火墙 | 访问控制 |
| 0x7F | NET_RESERVED | 网络预留 | 预留 |

### 2.2 扩展地址区 (0x80-0xFF)

**共 128 个地址，4 个区域**

#### 系统预留区 (0x80-0x8F)

| 地址范围 | 用途 | 固定性 |
|----------|------|:------:|
| 0x80-0x8F | 未来官方扩展 | 固定 |

#### 用户绑定区 (0x90-0x9F)

| 地址范围 | 用途 | 固定性 |
|----------|------|:------:|
| 0x90-0x9F | 用户动态绑定能力 | 动态分配 |

#### 开发者扩展区 (0xA0-0xAF)

| 地址范围 | 用途 | 固定性 |
|----------|------|:------:|
| 0xA0-0xAF | 开发者自定义能力 | 动态分配 |

#### 紧急预留区 (0xB0-0xFF)

| 地址范围 | 用途 | 固定性 |
|----------|------|:------:|
| 0xB0-0xFF | 应急使用 | 固定 |

---

## 三、职责分工

### 3.1 Engine Team 职责

| 任务 | 优先级 | 预计时间 | 状态 |
|------|:------:|:--------:|:----:|
| CapabilityCategory 枚举 (16分类) | P0 | 0.5天 | 待开发 |
| CapabilityAddress 枚举 (128地址) | P0 | 1天 | 待开发 |
| CapabilityRouter 路由器 | P0 | 2天 | 待开发 |
| CapabilityInstanceRegistry 实例注册 | P0 | 2天 | 待开发 |
| CapabilityMappingService 映射服务 | P1 | 0.5天 | 待开发 |
| LlmContextRegistry 扩展 | P1 | 0.5天 | 待开发 |
| SecurityContext 扩展 | P1 | 0.5天 | 待开发 |
| CapabilityInstanceSnapshot 持久化 | P1 | 1天 | 待开发 |
| CapabilityInstanceRestorer 恢复 | P1 | 1天 | 待开发 |

### 3.2 Skills Team 职责

| 任务 | 优先级 | 预计时间 | 依赖 |
|------|:------:|:--------:|:----:|
| 驱动实现 (skill-vfs-*, skill-llm-*, etc.) | P0 | 按技能规划 | Engine P0 |
| 驱动注册到 CapabilityAddress | P0 | 0.5天 | Engine P0 |
| skill.yaml 扩展 (能力地址声明) | P1 | 1天 | Engine P1 |
| 驱动配置适配 | P1 | 1天 | Engine P1 |

### 3.3 Agent-SDK Team 职责

| 任务 | 优先级 | 预计时间 | 依赖 |
|------|:------:|:--------:|:----:|
| FunctionDefinition 扩展 (capabilityAddress字段) | P1 | 0.5天 | Engine P0 |
| 驱动注册接口适配 | P1 | 0.5天 | Engine P0 |

---

## 四、接口定义

### 4.1 Engine 提供的接口

```java
/**
 * 能力路由器 - Skills 调用此接口获取驱动
 */
public interface CapabilityRouter {
    
    <T> T getDriver(CapabilityAddress address, Class<T> driverType);
    
    Set<CapabilityAddress> getActiveDrivers(CapabilityCategory category);
}

/**
 * 驱动注册 - Skills 调用此接口注册驱动
 */
public interface DriverRegistry {
    
    void register(CapabilityAddress address, Object driver);
    
    void unregister(CapabilityAddress address);
}
```

### 4.2 Skills 需要实现的接口

```java
/**
 * 原子能力接口 - 由具体技能实现
 */
public interface AtomicCapability {
    
    CapabilityAddress getAddress();
    
    Set<String> getSupportedOperations();
    
    Result execute(String operation, Map<String, Object> params, ContextReference contextRef);
}
```

### 4.3 skill.yaml 扩展

```yaml
# 技能声明能力地址
skillId: skill-vfs-minio
name: MinIO存储
version: "1.0.0"

# 能力地址声明
capability:
  address: 0x1A          # CapabilityAddress.VFS_MINIO
  category: VFS          # CapabilityCategory.VFS
  operations: [upload, download, delete, list]
  
# 驱动配置
driver:
  class: net.ooder.skill.vfs.minio.MinioVfsDriver
  config:
    endpoint: ${MINIO_ENDPOINT}
    accessKey: ${MINIO_ACCESS_KEY}
    secretKey: ${MINIO_SECRET_KEY}
```

---

## 五、时间计划

### 5.1 Phase 1 (P0 任务)

| 周 | Engine Team | Skills Team |
|----|-------------|-------------|
| W1 | CapabilityCategory 枚举 | - |
| W1 | CapabilityAddress 枚举 | - |
| W1 | CapabilityRouter 实现 | 驱动接口适配 |
| W2 | CapabilityInstanceRegistry 实现 | 驱动注册实现 |

### 5.2 Phase 2 (P1 任务)

| 周 | Engine Team | Skills Team |
|----|-------------|-------------|
| W3 | CapabilityMappingService | - |
| W3 | 上下文扩展 | skill.yaml 扩展 |
| W3 | 持久化与恢复 | 驱动配置适配 |

---

## 六、验收标准

### 6.1 Engine Team 交付

- [ ] CapabilityCategory 枚举包含 16 个分类
- [ ] CapabilityAddress 枚举包含 128 个地址
- [ ] CapabilityRouter 正确路由到驱动
- [ ] CapabilityInstanceRegistry 支持实例注册
- [ ] 持久化与恢复机制正常工作

### 6.2 Skills Team 交付

- [ ] 驱动实现 CapabilityAddress 声明
- [ ] 驱动正确注册到 Engine
- [ ] skill.yaml 包含能力地址配置
- [ ] 驱动可正常启动/停止

### 6.3 Agent-SDK Team 交付

- [ ] FunctionDefinition 包含 capabilityAddress 字段
- [ ] 驱动注册接口适配完成

---

## 七、风险与依赖

### 7.1 风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 地址分配冲突 | 能力注册失败 | 统一地址分配表 |
| 驱动注册时机 | 系统启动顺序 | 懒加载机制 |
| 配置迁移 | 现有技能配置失效 | 提供迁移工具 |

### 7.2 依赖

| 依赖方 | 被依赖方 | 依赖内容 |
|--------|----------|----------|
| Skills Team | Engine Team | CapabilityAddress 枚举 |
| Skills Team | Engine Team | DriverRegistry 接口 |
| Agent-SDK Team | Engine Team | CapabilityAddress 枚举 |
| Engine Team | Skills Team | 驱动实现 |

---

## 八、联系方式

| 角色 | 团队 | 职责 |
|------|------|------|
| 能力框架 | Engine Team | 地址定义、路由实现 |
| 驱动实现 | Skills Team | 具体驱动开发 |
| SDK适配 | Agent-SDK Team | 接口适配 |

---

**文档状态**: 待 Skills Team 和 Agent-SDK Team 确认  
**创建日期**: 2026-03-11  
**维护团队**: Engine Team
