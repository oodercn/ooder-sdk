# 技能分类强制执行标准

> **文档版本**: 1.1.0  
> **创建日期**: 2026-03-11  
> **更新日期**: 2026-03-11  
> **执行团队**: Skills Team  
> **监督团队**: Engine Team  
> **状态**: 强制执行  
> **生效日期**: 2026-03-18
> **变更说明**: 响应Skills Team反馈，扩展visibility、skillForm枚举，新增capabilityCategory字段

---

## 一、强制执行声明

### 1.1 执行范围

本标准适用于 `E:\github\ooder-skills\skills` 目录下的**所有技能**，包括：
- 场景技能 (scenes/)
- 驱动技能 (_drivers/)
- 系统技能 (_system/)
- 能力技能 (capabilities/)
- 工具技能 (tools/)

### 1.2 不遵守的后果

| 违规情况 | 处理措施 |
|----------|----------|
| 缺少必需字段 | **禁止发布**到技能市场 |
| 分类不规范 | **拒绝合并**PR |
| 地址配置错误 | **安装失败**，返回错误提示 |
| 文档不完整 | **降低搜索排名**，影响发现 |

---

## 二、必需字段清单 (强制执行)

### 2.1 skill.yaml 必需字段

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  # ========== 基础信息 (必需) ==========
  id: string                 # 技能ID，全局唯一，kebab-case
  name: string               # 技能名称，中文，2-20字
  version: string            # 版本号，semver格式 (如 1.0.0)
  description: string        # 技能描述，50-200字
  
  # ========== SE三维分类 (必需) ==========
  skillForm: enum            # SCENE | PROVIDER | DRIVER | INTERNAL
  sceneType: enum            # AUTO | TRIGGER (仅SCENE时必需)
  visibility: enum           # public | developer | internal
  
  # ========== 业务分类 (必需) ==========
  businessCategory: enum     # 见下方业务分类枚举
  subCategory: string        # 子分类，如"日志汇报"
  tags: [string]             # 标签，至少3个，最多10个
  
  # ========== 技术分类 (必需) ==========
  category: enum             # KNOWLEDGE | LLM | TOOL | WORKFLOW | DATA | SERVICE | UI | OTHER
  capabilityCategory: string # 能力地址分类 (sys|org|auth|net|vfs|db|llm|know|payment|media|comm|mon|iot|search|sched|sec|util)

spec:
  # ========== 能力地址配置 (必需) ==========
  capabilityAddresses:
    required:                # 必需能力地址，至少1个
      - address: hex         # 十六进制地址，如 0x30
        name: string         # 地址名称
        fallback: hex|null   # 降级地址，null表示必需
    optional: []             # 可选能力地址，可为空
  
  # ========== 角色配置 (SCENE时必需) ==========
  roles:                     # 至少1个角色
    - name: enum             # MANAGER | LEADER | MEMBER | USER 等
      displayName: string    # 显示名称
      minCount: int          # 最小人数
      maxCount: int          # 最大人数
      permissions: [enum]    # READ | WRITE | CONFIG | DELETE

# ========== LLM文档 (必需) ==========
# 同目录下必须存在 skills.md 文件
```

### 2.2 字段验证规则

| 字段 | 类型 | 验证规则 | 错误处理 |
|------|------|----------|----------|
| `id` | string | 全局唯一，kebab-case，小写，数字字母连字符 | 重复则拒绝 |
| `name` | string | 2-20个字符，中文或英文 | 长度不符则拒绝 |
| `version` | string | semver格式 (x.y.z) | 格式错误则拒绝 |
| `skillForm` | enum | SCENE / PROVIDER / DRIVER / INTERNAL | 其他值则拒绝 |
| `sceneType` | enum | AUTO 或 TRIGGER，仅SCENE时必需 | STANDALONE时忽略 |
| `visibility` | enum | public / developer / internal | 其他值则拒绝 |
| `capabilityCategory` | string | 17个能力地址分类之一 | 未定义则警告 |
| `businessCategory` | enum | 见下方枚举 | 未定义则拒绝 |
| `category` | enum | SkillCategory枚举 | 未定义则拒绝 |
| `capabilityAddresses.required` | array | 至少1个，地址范围0x00-0x7F | 空数组则拒绝 |

---

## 三、业务分类枚举 (强制执行)

### 3.1 用户可见业务分类 (public)

| 枚举值 | 显示名称 | 说明 | 典型场景 |
|--------|----------|------|----------|
| `OFFICE_COLLABORATION` | 办公协作 | 团队协作、日志、会议、审批 | TRIGGER |
| `HUMAN_RESOURCE` | 人力资源 | 招聘、绩效、培训、员工管理 | TRIGGER |
| `AI_ASSISTANT` | 智能助手 | AI对话、知识问答、智能客服 | AUTO |
| `DATA_PROCESSING` | 数据处理 | 报表、分析、同步、可视化 | AUTO/TRIGGER |
| `PROJECT_MANAGEMENT` | 项目管理 | 项目跟踪、敏捷看板、里程碑 | TRIGGER |
| `MARKETING_OPERATIONS` | 营销运营 | 内容发布、社媒管理、活动 | AUTO/TRIGGER |
| `SYSTEM_TOOLS` | 系统工具 | 存储、通知、定时任务、备份 | AUTO |

### 3.2 系统内部业务分类 (internal)

| 枚举值 | 显示名称 | 说明 | 可见性 |
|--------|----------|------|--------|
| `SYSTEM_MONITOR` | 系统监控 | 监控告警、日志收集、健康检查 | internal |
| `SECURITY_AUDIT` | 安全审计 | 访问控制、审计日志、安全检测 | internal |
| `INFRASTRUCTURE` | 基础设施 | 调度服务、网络服务、认证服务 | internal |

### 3.3 分类映射到 SE 三维分类

```yaml
# 强制执行映射规则
businessCategoryMapping:
  OFFICE_COLLABORATION:
    skillForm: SCENE
    sceneType: TRIGGER
    visibility: public
    
  HUMAN_RESOURCE:
    skillForm: SCENE
    sceneType: TRIGGER
    visibility: public
    
  AI_ASSISTANT:
    skillForm: SCENE
    sceneType: AUTO
    visibility: public
    
  DATA_PROCESSING:
    skillForm: SCENE
    sceneType: AUTO  # 或 TRIGGER
    visibility: public
    
  PROJECT_MANAGEMENT:
    skillForm: SCENE
    sceneType: TRIGGER
    visibility: public
    
  MARKETING_OPERATIONS:
    skillForm: SCENE
    sceneType: AUTO  # 或 TRIGGER
    visibility: public
    
  SYSTEM_TOOLS:
    skillForm: STANDALONE  # 或 SCENE
    sceneType: AUTO
    visibility: public
    
  SYSTEM_MONITOR:
    skillForm: SCENE  # 或 STANDALONE
    sceneType: AUTO
    visibility: internal
    
  SECURITY_AUDIT:
    skillForm: SCENE
    sceneType: AUTO
    visibility: internal
    
  INFRASTRUCTURE:
    skillForm: STANDALONE
    visibility: internal
```

---

## 四、配置示例 (强制执行模板)

### 4.1 日志汇报场景 (办公协作)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  # 基础信息
  id: skill-daily-report
  name: 日志汇报场景
  version: 3.0.0
  description: 团队协作的日志汇报系统，支持日报/周报/月报，自动汇总分析
  
  # SE三维分类 (必需)
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  # 业务分类 (必需)
  businessCategory: OFFICE_COLLABORATION
  subCategory: 日志汇报
  tags: [日志, 汇报, 日报, 周报, 团队, 协作]
  
  # 技术分类 (必需)
  category: WORKFLOW
  capabilityCategory: workflow

spec:
  # 能力地址配置 (必需)
  capabilityAddresses:
    required:
      - address: 0x08
        name: ORG_LOCAL
        fallback: null
      - address: 0x20
        name: DB_SQLITE
        fallback: 0x21
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
      - address: 0x68
        name: SCHED_QUARTZ
        fallback: null
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
      - address: 0x4B
        name: COMM_DINGTALK
        skipable: true
  
  # 角色配置 (必需)
  roles:
    - name: MANAGER
      displayName: 管理者
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: EMPLOYEE
      displayName: 员工
      minCount: 1
      maxCount: 100
      permissions: [READ, WRITE]

# skills.md 文件必须存在
```

### 4.2 招聘管理场景 (人力资源)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-recruitment
  name: 招聘管理场景
  version: 3.0.0
  description: 全流程招聘管理，从职位发布、简历筛选到入职办理
  
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  businessCategory: HUMAN_RESOURCE
  subCategory: 招聘管理
  tags: [招聘, 面试, 简历, 入职, HR, 人才]
  
  category: WORKFLOW

spec:
  capabilityAddresses:
    required:
      - address: 0x08
        name: ORG_LOCAL
        fallback: null
      - address: 0x20
        name: DB_SQLITE
        fallback: 0x21
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
  
  roles:
    - name: HR
      displayName: HR专员
      minCount: 1
      maxCount: 5
      permissions: [READ, WRITE, CONFIG]
    - name: INTERVIEWER
      displayName: 面试官
      minCount: 1
      maxCount: 20
      permissions: [READ, WRITE]
    - name: CANDIDATE
      displayName: 候选人
      minCount: 0
      maxCount: 1000
      permissions: [READ]
```

### 4.3 知识问答场景 (智能助手)

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-knowledge-qa
  name: 知识问答助手
  version: 3.0.0
  description: 基于知识库的智能问答，支持多轮对话和上下文理解
  
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: 知识问答
  tags: [AI, 问答, 知识库, 智能客服, 对话]
  
  category: KNOWLEDGE

spec:
  capabilityAddresses:
    required:
      - address: 0x30
        name: KNOW_VECTOR
        fallback: null
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
      - address: 0x34
        name: KNOW_EMBEDDING
        fallback: null
    optional: []
  
  roles:
    - name: USER
      displayName: 用户
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE]
```

---

## 五、验证检查清单

### 5.1 提交前自检

Skills Team 在提交 PR 前必须完成以下检查：

```markdown
## PR 提交检查清单

### 基础信息
- [ ] `id` 全局唯一，kebab-case
- [ ] `name` 2-20个字符
- [ ] `version` 符合 semver
- [ ] `description` 50-200字

### SE三维分类
- [ ] `skillForm` 为 SCENE 或 STANDALONE
- [ ] `sceneType` 为 AUTO 或 TRIGGER (SCENE时)
- [ ] `visibility` 为 public 或 internal

### 业务分类
- [ ] `businessCategory` 在枚举范围内
- [ ] `subCategory` 不为空
- [ ] `tags` 至少3个，最多10个

### 技术分类
- [ ] `category` 在 SkillCategory 枚举中

### 能力地址
- [ ] `capabilityAddresses.required` 至少1个
- [ ] 所有地址在 0x00-0x7F 范围内
- [ ] 地址在 CapabilityAddress 枚举中

### 角色配置 (SCENE时)
- [ ] `roles` 至少1个角色
- [ ] 每个角色有 name, displayName, permissions

### LLM文档
- [ ] 同目录存在 `skills.md` 文件
- [ ] `skills.md` 包含标题和能力描述

### 验证测试
- [ ] 通过 SkillValidationRunner 验证
```

### 5.2 Engine Team 审核检查

```markdown
## 审核检查清单

### 规范性检查
- [ ] 符合强制执行标准
- [ ] 分类映射正确
- [ ] 地址分配合理

### 技术检查
- [ ] 能力地址可用
- [ ] 依赖配置正确
- [ ] 无循环依赖

### 文档检查
- [ ] skills.md 完整
- [ ] 示例清晰
- [ ] 使用说明准确
```

---

## 六、自动化验证

### 6.1 CI/CD 集成

```yaml
# .github/workflows/skill-validation.yml
name: Skill Validation

on:
  pull_request:
    paths:
      - '**/skill.yaml'

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run Skill Validation
        run: |
          cd scene-engine
          mvn compile test-compile exec:java \
            -Dexec.mainClass="net.ooder.scene.skill.validation.SkillValidationRunner"
      
      - name: Check Results
        run: |
          # 检查是否有错误
          if grep -q "有错误:" validation-report.txt; then
            echo "❌ 验证失败，请修复错误"
            exit 1
          fi
          echo "✅ 验证通过"
```

### 6.2 本地验证命令

```bash
# 进入 scene-engine 目录
cd E:\github\ooder-sdk\scene-engine

# 编译并运行验证
mvn compile test-compile exec:java \
  -Dexec.mainClass="net.ooder.scene.skill.validation.SkillValidationRunner"

# 查看验证报告
cat target/validation-report.txt
```

---

## 七、违规处理流程

### 7.1 处理流程图

```
发现违规
    │
    ▼
┌─────────────────┐
│ 记录违规信息     │
│ - 技能ID        │
│ - 违规类型      │
│ - 违规详情      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ 通知 Skills Team │
│ (Issue/邮件)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐     ┌─────────────────┐
│ 限期修复        │────►│ 未修复则        │
│ (3个工作日)     │     │ - 禁止发布      │
└─────────────────┘     │ - 降低优先级    │
                        └─────────────────┘
```

### 7.2 违规类型与处罚

| 违规类型 | 处罚措施 | 修复期限 |
|----------|----------|----------|
| 缺少必需字段 | 禁止发布 | 3个工作日 |
| 分类不规范 | 拒绝合并 | 即时修复 |
| 地址配置错误 | 安装失败 | 即时修复 |
| 文档不完整 | 降低搜索排名 | 5个工作日 |
| 重复提交违规 | 暂停提交权限 | 1周 |

---

## 八、LLM 与知识库分层配置方案 (强制执行)

### 8.1 分层配置架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     分层配置架构                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Layer 1: Skill 元数据 (skill.yaml)                                         │
│  ─────────────────────────────────────────────────────────────────────────  │
│  职责: 声明技能需要的能力地址和能力ID                                          │
│  配置者: Skills Team (开发者)                                                │
│                                                                             │
│  Layer 2: MCP 配置 (capability-config.yaml)                                 │
│  ─────────────────────────────────────────────────────────────────────────  │
│  职责: capability → address 映射，定义降级策略                                 │
│  配置者: Engine Team / 系统管理员                                            │
│  触发: 安装/启动时                                                           │
│                                                                             │
│  Layer 3: ROUTE 配置 (route-config.yaml)                                    │
│  ─────────────────────────────────────────────────────────────────────────  │
│  职责: 动态链路管理，运行时最优选择                                            │
│  配置者: Route Agent (自动)                                                  │
│  触发: 场景激活/协作添加时                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 LLM 配置方案

#### 8.2.1 Skill 元数据层 (skill.yaml)

```yaml
# ============================================
# LLM 能力声明 - 在 skill.yaml 中配置
# ============================================
spec:
  # 能力地址配置 (必需)
  capabilityAddresses:
    required:
      # LLM 主地址 (必需)
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
        description: "主LLM服务，用于对话生成"
      # 备选 LLM 地址
      - address: 0x29
        name: LLM_OPENAI
        fallback: null
        description: "备选LLM服务，Ollama不可用时使用"
    optional:
      # Embedding 服务
      - address: 0x34
        name: KNOW_EMBEDDING
        skipable: true
        description: "向量嵌入服务，用于语义检索"

  # 能力定义 (必需)
  capabilities:
    - id: llm-chat
      name: 智能对话
      description: 基于LLM的对话能力
      category: ai
      type: DRIVER
      driverType: INTENT_RECEIVER
      requiredAddresses: [0x28, 0x29]  # 依赖的地址
    
    - id: text-embedding
      name: 文本嵌入
      description: 生成文本向量嵌入
      category: ai
      type: DRIVER
      requiredAddresses: [0x34]
```

#### 8.2.2 MCP 配置层 (capability-config.yaml)

```yaml
# ============================================
# MCP 能力映射配置 - 在 skill 目录下 capability-config.yaml
# ============================================
# 文件位置: E:/github/ooder-skills/skills/{skill-id}/capability-config.yaml
# 配置者: Engine Team / 系统管理员
# 触发时机: 安装/启动时
# ============================================

apiVersion: config.ooder.net/v1
kind: CapabilityConfig

metadata:
  skillId: skill-daily-report
  version: "1.0.0"
  lastUpdated: "2026-03-11T10:00:00Z"

# 能力到地址的映射
spec:
  capabilityMappings:
    # LLM 对话能力映射
    llm-chat:
      address: 0x28              # LLM_OLLAMA
      operation: chat
      priority: 1                # 优先级 1=最高
      fallback:
        address: 0x29            # LLM_OPENAI
        operation: chat
        condition: on_failure    # 主地址失败时触发
      
    # 文本嵌入能力映射
    text-embedding:
      address: 0x34              # KNOW_EMBEDDING
      operation: embed
      priority: 1
      fallback: null             # 无降级，必需
  
  # 地址提供者配置
  addressProviders:
    0x28:                        # LLM_OLLAMA
      providerId: skill-llm-ollama
      endpoint: http://localhost:11434
      config:
        model: qwen2.5:7b
        temperature: 0.7
        maxTokens: 4096
    
    0x29:                        # LLM_OPENAI
      providerId: skill-llm-openai
      endpoint: https://api.openai.com/v1
      config:
        model: gpt-4
        temperature: 0.7
      
    0x34:                        # KNOW_EMBEDDING
      providerId: skill-embedding-local
      endpoint: http://localhost:8000
      config:
        model: text-embedding-3-small
  
  # 降级策略
  fallbackStrategies:
    llm-chat:
      type: cascade              # cascade | parallel | abort
      retries: 3
      retryDelay: 5s
      providers:
        - 0x28
        - 0x29
```

#### 8.2.3 ROUTE 配置层 (route-config.yaml)

```yaml
# ============================================
# ROUTE 动态配置 - 由 Route Agent 自动生成
# ============================================
# 文件位置: 运行时生成，存储于 VFS
# 配置者: Route Agent (自动)
# 触发时机: 场景激活/协作添加时
# ============================================

apiVersion: route.ooder.net/v1
kind: RouteConfig

metadata:
  contextId: ctx-xxx-xxx
  sceneId: scene-daily-report
  generatedAt: "2026-03-11T10:30:00Z"

# 动态路由表
spec:
  routes:
    # LLM 路由 (动态计算)
    llm-chat:
      selectedAddress: 0x28      # 根据链路质量自动选择
      qualityScore: 0.95         # 链路质量评分
      latency: 120ms             # 延迟
      availability: 0.99         # 可用性
      
      # 备选路由
      alternatives:
        - address: 0x29
          qualityScore: 0.88
          latency: 250ms
      
      # 负载均衡配置
      loadBalance:
        strategy: round_robin    # round_robin | least_conn | weighted
        weights:
          0x28: 70
          0x29: 30
  
  # 链路质量监控
  linkQuality:
    checkInterval: 30s
    timeout: 5s
    failureThreshold: 3
```

### 8.3 知识库配置方案

#### 8.3.1 Skill 元数据层 (skill.yaml)

```yaml
# ============================================
# 知识库能力声明 - 在 skill.yaml 中配置
# ============================================
spec:
  # 能力地址配置 (必需)
  capabilityAddresses:
    required:
      # 知识库主地址
      - address: 0x30
        name: KNOW_VECTOR
        fallback: null
        description: "向量知识库，用于语义检索"
      # Embedding 服务
      - address: 0x34
        name: KNOW_EMBEDDING
        fallback: null
        description: "文本向量化服务"
    optional:
      # 文档存储
      - address: 0x18
        name: VFS_LOCAL
        skipable: true
        description: "本地文档存储"

  # 知识库定义 (场景技能必需)
  knowledgeBases:
    - id: kb-daily-report
      name: 日志知识库
      description: 存储历史日志数据用于分析和检索
      type: vector                    # vector | document | hybrid
      embeddingModel: text-embedding-3-small
      requiredAddresses: [0x30, 0x34]
      
      # 数据源配置
      dataSources:
        - type: skill-data            # skill-data | vfs | api
          source: daily-report-data
          syncMode: real-time         # real-time | scheduled | manual
        - type: vfs
          source: /reports/
          fileTypes: [pdf, docx, txt]
      
      # 索引配置
      indexConfig:
        chunkSize: 512
        chunkOverlap: 50
        indexType: hnsw               # hnsw | flat | ivf
  
  # 能力定义
  capabilities:
    - id: knowledge-search
      name: 知识检索
      description: 基于知识库的语义检索
      category: knowledge
      type: ATOMIC
      requiredAddresses: [0x30, 0x34]
      
    - id: knowledge-ingest
      name: 知识入库
      description: 文档解析和向量化入库
      category: knowledge
      type: ATOMIC
      requiredAddresses: [0x30, 0x34, 0x18]
```

#### 8.3.2 MCP 配置层 (capability-config.yaml)

```yaml
# ============================================
# MCP 知识库映射配置
# ============================================
spec:
  capabilityMappings:
    # 知识检索能力
    knowledge-search:
      address: 0x30                  # KNOW_VECTOR
      operation: search
      priority: 1
      fallback: null
      
    # 知识入库能力
    knowledge-ingest:
      address: 0x30                  # KNOW_VECTOR
      operation: ingest
      priority: 1
      fallback: null
  
  # 知识库提供者配置
  knowledgeBaseProviders:
    kb-daily-report:
      vectorStore:
        address: 0x30
        providerId: skill-vector-chroma
        config:
          collectionName: daily_reports
          distanceMetric: cosine
      
      embedding:
        address: 0x34
        providerId: skill-embedding-local
        config:
          model: text-embedding-3-small
          batchSize: 100
      
      documentStore:
        address: 0x18
        providerId: skill-vfs-local
        config:
          basePath: /data/knowledge/
  
  # 同步策略
  syncStrategies:
    kb-daily-report:
      mode: incremental              # full | incremental | real-time
      schedule: "0 2 * * *"          # 每天凌晨2点同步
      conflictResolution: latest     # latest | merge | manual
```

#### 8.3.3 ROUTE 配置层 (route-config.yaml)

```yaml
# ============================================
# ROUTE 知识库动态配置
# ============================================
spec:
  routes:
    # 知识库路由
    knowledge-search:
      selectedAddress: 0x30
      qualityScore: 0.98
      
      # 缓存配置
      cache:
        enabled: true
        ttl: 300s
        maxSize: 1000
  
  # 知识库状态
  knowledgeBaseStatus:
    kb-daily-report:
      status: active                 # active | syncing | error | offline
      lastSync: "2026-03-11T02:00:00Z"
      documentCount: 1523
      vectorCount: 45678
      size: "256MB"
```

### 8.4 配置优先级规则

```yaml
# ============================================
# 配置优先级 (高优先级覆盖低优先级)
# ============================================

priority:
  # Level 1: 系统默认值 (最低)
  system_defaults:
    source: SDK 内置
    override: false
  
  # Level 2: Skill 元数据
  skill_metadata:
    source: skill.yaml
    override: true
    description: 声明需要的能力和地址
  
  # Level 3: MCP 配置
  mcp_config:
    source: capability-config.yaml
    override: true
    description: 定义 capability → address 映射和降级策略
  
  # Level 4: ROUTE 配置 (最高)
  route_config:
    source: route-config.yaml (运行时生成)
    override: true
    description: 动态链路选择和负载均衡
```

### 8.5 配置验证检查清单

```markdown
## LLM/知识库配置检查清单

### Skill 元数据层 (skill.yaml)
- [ ] `capabilityAddresses` 包含 LLM 地址 (0x28-0x2F)
- [ ] `capabilityAddresses` 包含知识库地址 (0x30-0x37)
- [ ] `capabilities` 定义了 llm-chat 或 knowledge-search
- [ ] `knowledgeBases` 定义了知识库 (如需要)
- [ ] 每个地址都有 `fallback` 配置 (如适用)

### MCP 配置层 (capability-config.yaml)
- [ ] 文件存在于 skill 目录下
- [ ] `capabilityMappings` 包含所有 capability 映射
- [ ] `addressProviders` 配置了每个地址的提供者
- [ ] `fallbackStrategies` 定义了降级策略
- [ ] LLM 配置包含 model, temperature, maxTokens
- [ ] 知识库配置包含 vectorStore, embedding

### ROUTE 配置层 (自动)
- [ ] 场景激活时自动生成 route-config.yaml
- [ ] 链路质量评分 > 0.8
- [ ] 知识库状态为 active
- [ ] 缓存配置正确
```

### 8.6 完整配置示例

```yaml
# ============================================
# 智能客服场景 - 完整分层配置示例
# 业务分类: AI_ASSISTANT
# SE分类: SCENE + AUTO + public
# ============================================

# ========== Layer 1: skill.yaml ==========
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-customer-service
  name: 智能客服场景
  version: 3.0.0
  description: 基于LLM和知识库的智能客服系统
  
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: 智能客服
  tags: [AI, 客服, 问答, 知识库, 对话]
  
  category: KNOWLEDGE

spec:
  type: scene-skill
  
  capabilityAddresses:
    required:
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
      - address: 0x30
        name: KNOW_VECTOR
        fallback: null
      - address: 0x34
        name: KNOW_EMBEDDING
        fallback: null
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
  
  knowledgeBases:
    - id: kb-product-faq
      name: 产品FAQ知识库
      type: vector
      embeddingModel: text-embedding-3-small
      requiredAddresses: [0x30, 0x34]
      dataSources:
        - type: vfs
          source: /docs/faq/
          syncMode: scheduled
  
  capabilities:
    - id: customer-chat
      name: 客服对话
      type: DRIVER
      driverType: INTENT_RECEIVER
      requiredAddresses: [0x28, 0x30, 0x34]
    
    - id: faq-search
      name: FAQ检索
      type: ATOMIC
      requiredAddresses: [0x30, 0x34]

---
# ========== Layer 2: capability-config.yaml ==========
apiVersion: config.ooder.net/v1
kind: CapabilityConfig

metadata:
  skillId: skill-customer-service
  version: "1.0.0"

spec:
  capabilityMappings:
    customer-chat:
      address: 0x28
      operation: chat
      priority: 1
      fallback:
        address: 0x29
        operation: chat
    
    faq-search:
      address: 0x30
      operation: search
      priority: 1
  
  addressProviders:
    0x28:
      providerId: skill-llm-ollama
      config:
        model: qwen2.5:7b
        temperature: 0.3
        systemPrompt: "你是一个专业的客服助手..."
    
    0x30:
      providerId: skill-vector-chroma
      config:
        collectionName: customer_faq
  
  knowledgeBaseProviders:
    kb-product-faq:
      vectorStore:
        address: 0x30
      embedding:
        address: 0x34
```

---

## 九、参考文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 能力地址设计 | `docs/CAPABILITY_ADDRESS_SPACE_DESIGN.md` | 地址分配规范 |
| 分类用户视角 | `docs/SE_SKILL_CLASSIFICATION_USER_VIEW.md` | 用户展示设计 |
| 配置调整说明 | `docs/SKILLS_CONFIG_ADJUSTMENT_SPEC.md` | 配置迁移指南 |
| 协作排查文档 | `docs/SKILLS_TEAM_COLLABORATION_TROUBLESHOOTING.md` | 问题排查指南 |
| 配置标准 | `docs/SKILL_CONFIG_STANDARD_AND_DEPRECATED_FIELDS.md` | 配置标准与废弃字段 |

---

## 十、枚举定义参考

### 10.1 Visibility 枚举 (扩展版)

```java
public enum Visibility {
    public("public", "普通用户可见", "所有用户可见"),
    developer("developer", "开发者可见", "仅开发者可见"),
    internal("internal", "系统内部", "系统内部使用");
    
    private final String code;
    private final String name;
    private final String description;
    
    Visibility(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
```

### 10.2 SkillForm 枚举 (扩展版)

```java
public enum SkillForm {
    SCENE("SCENE", "场景技能", "容器型技能，可包含子技能"),
    PROVIDER("PROVIDER", "能力提供者", "提供基础能力的技能"),
    DRIVER("DRIVER", "驱动技能", "驱动场景运行的技能"),
    INTERNAL("INTERNAL", "内部能力", "系统内部使用的技能");
    
    private final String code;
    private final String name;
    private final String description;
    
    SkillForm(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
```

### 10.3 CapabilityCategory 枚举 (17个能力地址分类)

```java
public enum CapabilityCategory {
    sys("系统核心", 0x00),
    org("组织服务", 0x08),
    auth("认证服务", 0x10),
    net("网络服务", 0x78),
    vfs("文件存储", 0x18),
    db("数据库", 0x20),
    llm("大语言模型", 0x28),
    know("知识库", 0x30),
    payment("支付服务", 0x38),
    media("媒体服务", 0x40),
    comm("通讯服务", 0x48),
    mon("监控服务", 0x50),
    iot("物联网", 0x58),
    search("搜索服务", 0x60),
    sched("调度服务", 0x68),
    sec("安全服务", 0x70),
    util("工具服务", 0x08);
    
    private final String name;
    private final int baseAddress;
    
    CapabilityCategory(String name, int baseAddress) {
        this.name = name;
        this.baseAddress = baseAddress;
    }
}
```

---

## 十一、版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| 1.0.0 | 2026-03-11 | 初始版本 |
| 1.1.0 | 2026-03-11 | 响应Skills Team反馈：扩展visibility枚举(3值)，扩展skillForm枚举(4值)，新增capabilityCategory字段 |

---

## 十二、联系方式

| 事项 | 联系方式 |
|------|----------|
| 标准疑问 | 在 scene-engine 提交 Issue |
| 紧急问题 | 联系 Engine Team 负责人 |
| 规范解释 | 参考本标准文档 |

---

**文档状态**: 强制执行  
**生效日期**: 2026-03-18  
**下次评审**: 2026-04-18
