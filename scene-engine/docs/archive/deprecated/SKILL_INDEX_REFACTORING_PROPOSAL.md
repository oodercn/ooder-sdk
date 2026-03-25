# Skill Index 分层重构方案

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **问题**: skill-index.yaml 2000+行，难以维护扩展  
> **目标**: 分层设计，解耦配置，提升可维护性

---

## 一、现状问题分析

### 1.1 当前设计问题

```
┌─────────────────────────────────────────────────────────────────┐
│                    当前 skill-index.yaml 结构                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-index.yaml (2000+ 行)                                    │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  ├── skills:                    # 技能列表 (800+ 行)            │
│  │   ├── id, name, version      # 基础信息                      │
│  │   ├── skillForm              # 形态分类                      │
│  │   ├── sceneType              # 场景类型                      │
│  │   ├── visibility             # 可见性                        │
│  │   ├── category               # 技术分类                      │
│  │   ├── capabilityCategory     # 能力分类                      │
│  │   ├── businessCategory       # 业务分类                      │
│  │   ├── addressRequired        # 必需地址 (数组)               │
│  │   ├── addressOptional        # 可选地址 (数组)               │
│  │   ├── dependencies           # 依赖 (数组)                   │
│  │   ├── tags                   # 标签 (数组)                   │
│  │   └── ...                    # 其他字段                      │
│  │                                                              │
│  ├── categories:                # 分类定义 (300+ 行)            │
│  │   ├── businessCategories     # 业务分类枚举                  │
│  │   ├── capabilityCategories   # 能力分类枚举                  │
│  │   └── skillCategories        # 技能分类枚举                  │
│  │                                                              │
│  ├── addresses:                 # 地址定义 (400+ 行)            │
│  │   ├── addressRanges          # 地址范围定义                  │
│  │   ├── addressMappings        # 地址映射表                    │
│  │   └── fallbackRules          # 降级规则                      │
│  │                                                              │
│  ├── mappings:                  # 映射规则 (300+ 行)            │
│  │   ├── categoryMappings       # 分类映射                      │
│  │   ├── visibilityMappings     # 可见性映射                    │
│  │   └── formMappings           # 形态映射                      │
│  │                                                              │
│  └── validation:                # 验证规则 (200+ 行)            │
│      ├── requiredFields         # 必需字段                      │
│      └── fieldRules             # 字段规则                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 核心问题

| 问题 | 影响 | 示例 |
|------|------|------|
| **单体文件过大** | 难以阅读和修改 | 2000+行，IDE卡顿 |
| **职责不单一** | 修改一处可能影响全局 | 改分类定义影响所有技能 |
| **版本控制困难** | 多人协作冲突频繁 | 同时修改技能列表和分类定义 |
| **扩展性差** | 新增字段需要修改大文件 | 新增 `roles` 字段 |
| **测试困难** | 无法单元测试单个部分 | 必须加载整个文件 |
| **热更新困难** | 无法局部更新 | 改一个技能需要重载整个文件 |

### 1.3 具体痛点场景

**场景1: 新增一个技能**
```bash
# 当前流程
1. 打开 2000+ 行的 skill-index.yaml
2. 滚动到 skills 部分 (约 800 行)
3. 找到合适位置插入新技能 (50+ 行配置)
4. 同时可能需要修改分类定义部分
5. 保存并验证整个文件
# 耗时: 10-15 分钟
```

**场景2: 修改分类定义**
```bash
# 当前流程
1. 修改 category 枚举
2. 需要检查所有 60+ 个技能的 category 字段
3. 可能还需要修改映射规则
4. 验证整个文件
# 风险: 可能影响所有技能
```

**场景3: 版本升级**
```bash
# 当前流程
1. 需要同时修改 schema 版本
2. 所有技能配置需要同步更新
3. 无法灰度发布
# 风险: 全量更新，回滚困难
```

---

## 二、分层设计方案

### 2.1 设计原则

```
┌─────────────────────────────────────────────────────────────────┐
│                    分层设计原则                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 单一职责原则 (SRP)                                          │
│     每个文件只负责一个层面的配置                                │
│                                                                 │
│  2. 依赖倒置原则 (DIP)                                          │
│     高层配置依赖低层配置，低层不依赖高层                        │
│                                                                 │
│  3. 开闭原则 (OCP)                                              │
│     对扩展开放，对修改关闭                                      │
│     新增技能不需要修改现有文件                                  │
│                                                                 │
│  4. 接口隔离原则 (ISP)                                          │
│     不同团队关注不同层面的配置                                  │
│     Skills Team: 技能配置                                       │
│     Engine Team: 分类和地址定义                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 分层架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    分层架构 (目标状态)                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Layer 4: 技能层 (Skills Layer)                                 │
│  ─────────────────────────────────────────────────────────────  │
│  文件: skills/{skill-id}/skill-index-entry.yaml                 │
│  配置者: Skills Team                                            │
│  职责: 单个技能的索引配置                                       │
│  数量: 60+ 个文件，每个 20-50 行                                │
│                                                                 │
│  Layer 3: 分类层 (Category Layer)                               │
│  ─────────────────────────────────────────────────────────────  │
│  文件: config/categories.yaml                                   │
│  配置者: Engine Team + Skills Team                              │
│  职责: 分类枚举定义                                             │
│  大小: ~200 行                                                  │
│                                                                 │
│  Layer 2: 地址层 (Address Layer)                                │
│  ─────────────────────────────────────────────────────────────  │
│  文件: config/addresses.yaml                                    │
│  配置者: Engine Team                                            │
│  职责: 能力地址空间定义                                         │
│  大小: ~300 行                                                  │
│                                                                 │
│  Layer 1: 基础层 (Base Layer)                                   │
│  ─────────────────────────────────────────────────────────────  │
│  文件: config/schema.yaml                                       │
│  配置者: Engine Team                                            │
│  职责: 配置规范、验证规则                                       │
│  大小: ~100 行                                                  │
│                                                                 │
│  Layer 0: 聚合层 (Aggregation Layer)                            │
│  ─────────────────────────────────────────────────────────────  │
│  文件: skill-index.yaml (自动生成)                              │
│  配置者: CI/CD 自动生成                                         │
│  职责: 聚合所有配置，供运行时读取                               │
│  大小: 2000+ 行 (自动生成，不手动修改)                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 文件结构对比

#### 当前结构 (单体)

```
skill-repository/
└── skill-index.yaml          # 2000+ 行，包含所有配置
```

#### 目标结构 (分层)

```
skill-repository/
├── config/
│   ├── schema.yaml           # Layer 1: 配置规范 (~100行)
│   ├── addresses.yaml        # Layer 2: 地址定义 (~300行)
│   └── categories.yaml       # Layer 3: 分类定义 (~200行)
│
├── skills/                   # Layer 4: 技能配置 (60+ 文件)
│   ├── skill-knowledge-qa/
│   │   ├── skill.yaml        # 技能主配置
│   │   ├── skill-index-entry.yaml   # 索引条目 (20-50行)
│   │   └── capability-config.yaml   # 能力配置
│   ├── skill-daily-report/
│   │   ├── skill.yaml
│   │   ├── skill-index-entry.yaml
│   │   └── capability-config.yaml
│   └── ...
│
└── skill-index.yaml          # Layer 0: 聚合文件 (自动生成，2000+行)
```

---

## 三、详细设计方案

### 3.1 Layer 1: 基础层 (schema.yaml)

```yaml
# config/schema.yaml
# 配置规范定义，由 Engine Team 维护

apiVersion: config.ooder.net/v1
kind: SkillIndexSchema

metadata:
  version: "1.1.0"
  lastUpdated: "2026-03-11"

# 字段定义
spec:
  fields:
    # 基础字段
    - name: id
      type: string
      required: true
      pattern: "^skill-[a-z0-9-]+$"
      
    - name: name
      type: string
      required: true
      maxLength: 100
      
    - name: version
      type: string
      required: true
      pattern: "^\\d+\\.\\d+\\.\\d+$"
    
    # 分类字段
    - name: skillForm
      type: enum
      required: true
      values: [SCENE, PROVIDER, DRIVER, INTERNAL]
      
    - name: sceneType
      type: enum
      required: false
      values: [AUTO, TRIGGER]
      condition: "skillForm == SCENE"
      
    - name: visibility
      type: enum
      required: true
      values: [public, developer, internal]
      
    - name: category
      type: enum
      required: true
      ref: "categories.yaml#/skillCategories"
      
    - name: capabilityCategory
      type: enum
      required: true
      ref: "categories.yaml#/capabilityCategories"
      
    - name: businessCategory
      type: enum
      required: true
      ref: "categories.yaml#/businessCategories"
  
  # 验证规则
  validation:
    - rule: sceneTypeRequired
      condition: "skillForm == SCENE && !sceneType"
      message: "SCENE技能必须指定sceneType"
      
    - rule: versionFormat
      condition: "!version.matches('^\\d+\\.\\d+\\.\\d+$')"
      message: "版本号格式必须为 x.y.z"
```

**优点**:
- 集中管理字段定义和验证规则
- 版本控制清晰
- 可作为代码生成的输入

---

### 3.2 Layer 2: 地址层 (addresses.yaml)

```yaml
# config/addresses.yaml
# 能力地址空间定义，由 Engine Team 维护

apiVersion: config.ooder.net/v1
kind: CapabilityAddressConfig

metadata:
  version: "1.0.0"
  description: "能力地址空间配置"

# 地址范围定义
spec:
  addressSpace:
    total: 128
    range: "0x00-0x7F"
    
  # 分类地址范围
  categories:
    - name: sys
      displayName: "系统核心"
      range: "0x00-0x07"
      baseAddress: 0x00
      
    - name: org
      displayName: "组织服务"
      range: "0x08-0x0F"
      baseAddress: 0x08
      
    - name: auth
      displayName: "认证服务"
      range: "0x10-0x17"
      baseAddress: 0x10
      
    - name: vfs
      displayName: "文件存储"
      range: "0x18-0x1F"
      baseAddress: 0x18
      
    - name: db
      displayName: "数据库"
      range: "0x20-0x27"
      baseAddress: 0x20
      
    - name: llm
      displayName: "大语言模型"
      range: "0x28-0x2F"
      baseAddress: 0x28
      
    - name: know
      displayName: "知识库"
      range: "0x30-0x37"
      baseAddress: 0x30
      
    # ... 其他分类
  
  # 具体地址定义
  addresses:
    # 系统核心 (0x00-0x07)
    - address: 0x00
      name: SYS_REGISTRY
      category: sys
      description: "系统注册中心"
      
    - address: 0x01
      name: SYS_CONFIG
      category: sys
      description: "系统配置中心"
    
    # 组织服务 (0x08-0x0F)
    - address: 0x08
      name: ORG_LOCAL
      category: org
      description: "本地组织服务"
      
    - address: 0x09
      name: ORG_DINGDING
      category: org
      description: "钉钉组织服务"
    
    # LLM (0x28-0x2F)
    - address: 0x28
      name: LLM_OLLAMA
      category: llm
      description: "Ollama本地模型"
      fallback: 0x29
      
    - address: 0x29
      name: LLM_OPENAI
      category: llm
      description: "OpenAI API"
      
    - address: 0x2A
      name: LLM_QIANWEN
      category: llm
      description: "通义千问"
    
    # 知识库 (0x30-0x37)
    - address: 0x30
      name: KNOW_VECTOR
      category: know
      description: "向量知识库"
      
    - address: 0x34
      name: KNOW_EMBEDDING
      category: know
      description: "嵌入服务"
  
  # 降级规则
  fallbackRules:
    - from: 0x28
      to: 0x29
      condition: "on_failure"
      
    - from: 0x30
      to: null
      condition: "required"
```

**优点**:
- 地址定义集中管理
- 支持地址范围分配
- 降级规则独立配置

---

### 3.3 Layer 3: 分类层 (categories.yaml)

```yaml
# config/categories.yaml
# 分类枚举定义，由 Engine Team 和 Skills Team 共同维护

apiVersion: config.ooder.net/v1
kind: CategoryConfig

metadata:
  version: "1.1.0"
  description: "分类枚举配置"

# SE标准技术分类 (8个)
spec:
  skillCategories:
    - code: KNOWLEDGE
      name: "知识类"
      description: "知识库、文档管理类技能"
      icon: "book"
      
    - code: LLM
      name: "AI模型类"
      description: "大语言模型、AI对话类技能"
      icon: "robot"
      
    - code: TOOL
      name: "工具类"
      description: "工具、实用程序类技能"
      icon: "tool"
      
    - code: WORKFLOW
      name: "流程类"
      description: "工作流、流程自动化类技能"
      icon: "workflow"
      
    - code: DATA
      name: "数据类"
      description: "数据处理、分析类技能"
      icon: "database"
      
    - code: SERVICE
      name: "服务类"
      description: "基础服务、API类技能"
      icon: "service"
      
    - code: UI
      name: "界面类"
      description: "界面、交互类技能"
      icon: "layout"
      
    - code: OTHER
      name: "其他"
      description: "其他未分类技能"
      icon: "box"
  
  # 能力地址分类 (17个)
  capabilityCategories:
    - code: sys
      name: "系统核心"
      description: "系统核心服务"
      
    - code: org
      name: "组织服务"
      description: "组织架构、用户管理"
      
    - code: auth
      name: "认证服务"
      description: "身份认证、权限管理"
      
    - code: vfs
      name: "文件存储"
      description: "文件系统、对象存储"
      
    - code: db
      name: "数据库"
      description: "关系型/NoSQL数据库"
      
    - code: llm
      name: "大语言模型"
      description: "LLM服务"
      
    - code: know
      name: "知识库"
      description: "向量知识库、文档库"
      
    - code: payment
      name: "支付服务"
      description: "支付、账单"
      
    - code: media
      name: "媒体服务"
      description: "音视频处理"
      
    - code: comm
      name: "通讯服务"
      description: "消息、邮件、通知"
      
    - code: mon
      name: "监控服务"
      description: "监控、告警、日志"
      
    - code: iot
      name: "物联网"
      description: "IoT设备管理"
      
    - code: search
      name: "搜索服务"
      description: "全文搜索、语义搜索"
      
    - code: sched
      name: "调度服务"
      description: "定时任务、调度"
      
    - code: sec
      name: "安全服务"
      description: "安全、审计"
      
    - code: util
      name: "工具服务"
      description: "通用工具"
      
    - code: net
      name: "网络服务"
      description: "网络、代理"
  
  # 业务分类 (10个)
  businessCategories:
    - code: OFFICE_COLLABORATION
      name: "办公协作"
      description: "团队协作、日志、会议、审批"
      userVisible: true
      
    - code: HUMAN_RESOURCE
      name: "人力资源"
      description: "招聘、绩效、培训、员工管理"
      userVisible: true
      
    - code: AI_ASSISTANT
      name: "智能助手"
      description: "AI对话、知识问答、智能客服"
      userVisible: true
      
    - code: DATA_PROCESSING
      name: "数据处理"
      description: "报表、分析、同步、可视化"
      userVisible: true
      
    - code: PROJECT_MANAGEMENT
      name: "项目管理"
      description: "项目跟踪、敏捷看板、里程碑"
      userVisible: true
      
    - code: MARKETING_OPERATIONS
      name: "营销运营"
      description: "内容发布、社媒管理、活动"
      userVisible: true
      
    - code: SYSTEM_TOOLS
      name: "系统工具"
      description: "存储、通知、定时任务、备份"
      userVisible: true
      
    - code: SYSTEM_MONITOR
      name: "系统监控"
      description: "监控告警、日志收集、健康检查"
      userVisible: false
      
    - code: SECURITY_AUDIT
      name: "安全审计"
      description: "访问控制、审计日志、安全检测"
      userVisible: false
      
    - code: INFRASTRUCTURE
      name: "基础设施"
      description: "调度服务、网络服务、认证服务"
      userVisible: false
```

**优点**:
- 分类定义集中管理
- 支持多维度分类
- 用户可见性控制

---

### 3.4 Layer 4: 技能层 (skill-index-entry.yaml)

```yaml
# skills/skill-knowledge-qa/skill-index-entry.yaml
# 单个技能的索引配置，由 Skills Team 维护

apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-knowledge-qa
  name: 知识问答场景
  version: 3.0.0
  description: "基于知识库的智能问答场景"

# SE三维分类
spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  # 业务分类
  businessCategory: AI_ASSISTANT
  subCategory: 知识问答
  
  # 技术分类
  category: KNOWLEDGE
  capabilityCategory: know
  
  # 能力地址需求
  capabilityAddresses:
    required:
      - address: 0x30
        name: KNOW_VECTOR
        description: "向量知识库"
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
        description: "LLM服务"
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
        description: "邮件通知"
  
  # 标签
  tags:
    - AI
    - 知识库
    - 问答
    - LLM
  
  # 依赖
  dependencies:
    - skill-llm-ollama
    - skill-vector-chroma
  
  # 角色 (SCENE技能必需)
  roles:
    - name: MANAGER
      displayName: 场景管理员
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
      
    - name: MEMBER
      displayName: 场景成员
      minCount: 0
      maxCount: 100
      permissions: [READ, WRITE]
```

**优点**:
- 单个技能独立配置
- 易于新增和修改
- 版本控制粒度细
- 支持热更新单个技能

---

### 3.5 Layer 0: 聚合层 (skill-index.yaml)

```yaml
# skill-index.yaml
# 自动生成，请勿手动修改
# 生成时间: 2026-03-11T10:00:00Z
# 生成工具: skill-index-aggregator v1.0.0

apiVersion: skill.ooder.net/v1
kind: SkillIndex

metadata:
  version: "1.1.0"
  generatedAt: "2026-03-11T10:00:00Z"
  sourceVersion: "abc123"

# 聚合配置
spec:
  # 从 config/schema.yaml 聚合
  schema:
    version: "1.1.0"
    fields: [...]
  
  # 从 config/addresses.yaml 聚合
  addresses:
    categories: [...]
    addresses: [...]
  
  # 从 config/categories.yaml 聚合
  categories:
    skillCategories: [...]
    capabilityCategories: [...]
    businessCategories: [...]
  
  # 从 skills/*/skill-index-entry.yaml 聚合
  skills:
    - id: skill-knowledge-qa
      # ... 完整配置
    - id: skill-daily-report
      # ... 完整配置
    # ... 其他技能
```

**生成方式**:
```bash
# CI/CD 自动生成
skill-index-aggregator \
  --schema config/schema.yaml \
  --addresses config/addresses.yaml \
  --categories config/categories.yaml \
  --skills-dir skills/ \
  --output skill-index.yaml
```

**优点**:
- 运行时只需读取一个文件
- 保证配置一致性
- 自动生成，避免人为错误

---

## 四、迁移方案

### 4.1 迁移步骤

```
┌─────────────────────────────────────────────────────────────────┐
│                    迁移步骤                                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Phase 1: 基础层创建 (1天)                                      │
│  ─────────────────────────────────────────────────────────────  │
│  1. 创建 config/schema.yaml                                     │
│  2. 创建 config/addresses.yaml                                  │
│  3. 创建 config/categories.yaml                                 │
│  4. 验证基础层配置                                              │
│                                                                 │
│  Phase 2: 技能层拆分 (3天)                                      │
│  ─────────────────────────────────────────────────────────────  │
│  1. 为每个技能创建 skill-index-entry.yaml                       │
│  2. 从原 skill-index.yaml 迁移配置                              │
│  3. 验证每个技能配置                                            │
│  4. 废弃原 skill-index.yaml 中的技能配置                        │
│                                                                 │
│  Phase 3: 聚合层实现 (1天)                                      │
│  ─────────────────────────────────────────────────────────────  │
│  1. 实现 skill-index-aggregator 工具                            │
│  2. 配置 CI/CD 自动生成                                         │
│  3. 验证生成的 skill-index.yaml                                 │
│                                                                 │
│  Phase 4: 切换与验证 (1天)                                      │
│  ─────────────────────────────────────────────────────────────  │
│  1. 切换运行时读取生成的 skill-index.yaml                       │
│  2. 全量功能测试                                                │
│  3. 回滚方案准备                                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 向后兼容

```java
// SkillIndexLoader.java
public class SkillIndexLoader {
    
    public SkillIndex load() {
        // 优先读取生成的聚合文件
        File aggregatedFile = new File("skill-index.yaml");
        if (aggregatedFile.exists()) {
            return loadFromAggregated(aggregatedFile);
        }
        
        // 回退到分层读取 (开发模式)
        return loadFromLayers();
    }
    
    private SkillIndex loadFromLayers() {
        Schema schema = loadSchema("config/schema.yaml");
        Addresses addresses = loadAddresses("config/addresses.yaml");
        Categories categories = loadCategories("config/categories.yaml");
        List<SkillIndexEntry> skills = loadSkillEntries("skills/");
        
        // 实时聚合
        return aggregate(schema, addresses, categories, skills);
    }
}
```

---

## 五、收益分析

### 5.1 维护性提升

| 指标 | 当前 (单体) | 目标 (分层) | 提升 |
|------|:-----------:|:-----------:|:----:|
| 平均文件大小 | 2000+ 行 | 50 行 | **40x** |
| 修改影响范围 | 全局 | 单个技能 | **60x** |
| 冲突概率 | 高 | 低 | **10x** |
| 新增技能时间 | 15 分钟 | 2 分钟 | **7.5x** |

### 5.2 协作效率提升

| 场景 | 当前 | 目标 |
|------|------|------|
| Skills Team 新增技能 | 需要修改大文件，冲突风险高 | 独立文件，无冲突 |
| Engine Team 修改地址 | 影响所有技能，需要全量验证 | 只修改地址层，技能层无感知 |
| 多人协作 | 串行修改 | 并行修改 |
| 代码审查 | 难以定位变更 | 变更清晰可见 |

### 5.3 扩展性提升

| 扩展场景 | 当前 | 目标 |
|----------|------|------|
| 新增字段 | 修改大文件，风险高 | 修改 schema，自动生效 |
| 新增分类 | 修改分类定义 + 所有技能 | 修改分类层，技能层自动引用 |
| 新增地址 | 修改地址定义 + 相关技能 | 修改地址层，技能层按需引用 |
| 版本升级 | 全量更新 | 分层升级，灰度发布 |

---

## 六、风险评估

### 6.1 风险与缓解

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|:------:|:----:|----------|
| 迁移期间配置不一致 | 中 | 高 | 使用版本控制，分阶段验证 |
| 工具 bug 导致聚合错误 | 低 | 高 | 充分测试，保留手动检查 |
| 团队成员不适应新流程 | 中 | 中 | 提供培训，保留旧方式过渡期 |
| 性能下降 (多文件读取) | 低 | 低 | 生产环境使用聚合文件 |

### 6.2 回滚方案

```bash
# 如果出现问题，快速回滚到单体文件
git revert HEAD
# 或
cp skill-index.yaml.backup skill-index.yaml
```

---

## 七、实施建议

### 7.1 推荐实施顺序

1. **立即实施**: 保持当前 skill-index.yaml，同时创建分层文件
2. **短期 (1周内)**: 实现聚合工具，CI/CD 自动生成
3. **中期 (2周内)**: 逐步迁移技能配置到分层结构
4. **长期 (1月后)**: 完全切换到分层模式，废弃单体文件

### 7.2 决策建议

**强烈建议实施分层设计**，理由：

1. **当前痛点明确**: 2000+ 行文件难以维护
2. **收益显著**: 维护性提升 40 倍，协作效率提升 10 倍
3. **风险可控**: 有成熟的迁移方案和回滚机制
4. **符合趋势**: 现代配置管理都趋向分层和模块化

---

## 八、参考文档

| 文档 | 路径 |
|------|------|
| SE强制执行标准 | `SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` |
| Skills配置调整说明 | `SKILLS_CONFIG_ADJUSTMENT_SPEC.md` |
| 代码覆盖度分析 | `CODE_COVERAGE_ANALYSIS_REPORT.md` |

---

**文档状态**: 提案  
**建议决策**: 采纳并实施分层设计  
**预计收益**: 维护性提升 40x，协作效率提升 10x
