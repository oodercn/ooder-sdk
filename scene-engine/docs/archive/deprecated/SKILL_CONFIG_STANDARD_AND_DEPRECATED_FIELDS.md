# Skill 配置标准与废弃字段清单

> **文档版本**: 2.0.0  
> **创建日期**: 2026-03-11  
> **目标团队**: Skills Team  
> **状态**: 强制执行  
> **生效日期**: 2026-03-18

---

## 一、执行摘要

基于对 `E:\github\ooder-skills\skills` 目录下 **30+** 个 skill.yaml 文件的分析，本文档提供：

1. **标准配置模板** - 统一所有技能的配置格式
2. **废弃字段清单** - 明确标记需要移除的字段
3. **迁移指南** - 从旧配置迁移到新配置的步骤

---

## 二、废弃字段清单 (必须移除)

### 2.1 Metadata 层废弃字段

| 废弃字段 | 当前使用率 | 替代方案 | 移除期限 |
|----------|:----------:|----------|:--------:|
| `metadata.type` | 83% | `spec.type` | 2026-03-25 |
| `metadata.category` | 40% | `spec.classification.category` | 2026-03-25 |
| `metadata.sceneCategory` | 27% | `spec.classification.category` | 2026-03-25 |
| `metadata.tags` | 0% | `metadata.keywords` | 2026-03-25 |

### 2.2 Spec 层废弃字段

| 废弃字段 | 当前使用率 | 替代方案 | 移除期限 |
|----------|:----------:|----------|:--------:|
| `spec.category` | 40% | `spec.classification.category` | 2026-03-25 |
| `spec.capability` | 50% | `spec.capabilities` | 2026-03-25 |
| `spec.scenes` | 40% | `spec.sceneCapabilities` | 2026-03-25 |
| `spec.ownership` | 50% | 移除，由分类推断 | 2026-03-25 |
| `spec.supportedSceneTypes` | 33% | `spec.sceneCapabilities[].sceneType` | 2026-03-25 |
| `spec.autoStart` | 33% | `spec.sceneCapabilities[].mainFirstConfig.selfStart` | 2026-03-25 |
| `spec.autoJoin` | 27% | `spec.sceneCapabilities[].collaborativeCapabilities` | 2026-03-25 |
| `spec.providedInterfaces` | 33% | `spec.capabilities` | 2026-03-25 |
| `spec.sceneSkill` | 20% | `spec.type: scene-skill` | 2026-03-25 |
| `spec.mainFirst` | 33% | `spec.sceneCapabilities[].mainFirst` | 2026-03-25 |
| `spec.businessSemanticsScore` | 27% | `spec.classification.businessSemanticsScore` | 2026-03-25 |
| `spec.driverConditions` | 20% | `spec.sceneCapabilities[].mainFirstConfig.selfDrive` | 2026-03-25 |
| `spec.participants` | 13% | `spec.sceneCapabilities[].participants` | 2026-03-25 |
| `spec.visibility` | 13% | `spec.sceneCapabilities[].visibility` | 2026-03-25 |
| `spec.businessTags` | 7% | `metadata.keywords` | 2026-03-25 |

### 2.3 Capability 层废弃字段

| 废弃字段 | 当前使用率 | 替代方案 | 移除期限 |
|----------|:----------:|----------|:--------:|
| `capability.address` | 50% | 移除，使用 capabilityAddresses | 2026-03-25 |
| `capability.category` | 50% | `capabilities[].category` | 2026-03-25 |
| `capability.code` | 50% | 移除 | 2026-03-25 |
| `capability.operations` | 50% | `capabilities[].operations` | 2026-03-25 |

### 2.4 废弃字段使用统计

```
┌─────────────────────────────────────────────────────────────────┐
│                    废弃字段使用率统计                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  高使用率 (>40%)          中使用率 (20-40%)      低使用率 (<20%) │
│  ─────────────────        ────────────────       ─────────────  │
│  • metadata.type          • spec.sceneSkill      • spec.tags    │
│  • spec.capability        • spec.mainFirst       • spec.roleDetection│
│  • spec.scenes            • spec.driverConditions                 │
│  • spec.ownership         • spec.participants                     │
│  • capability.*           • spec.visibility                       │
│  • metadata.category      • spec.businessTags                     │
│  • spec.category                                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、标准配置模板 (强制执行)

### 3.1 通用字段 (所有技能必需)

```yaml
apiVersion: skill.ooder.net/v1  # 固定值
kind: Skill                      # 固定值

metadata:
  # ========== 基础信息 (必需) ==========
  id: string                 # 技能ID，全局唯一，kebab-case
  name: string               # 技能名称，中文，2-20字
  version: string            # 版本号，semver格式 (如 1.0.0)
  description: string        # 技能描述，50-200字
  author: string             # 作者
  license: string            # 许可证 (如 Apache-2.0)
  homepage: string           # 项目主页
  repository: string         # 代码仓库
  keywords: [string]         # 关键词，至少3个
  
  # ========== SE三维分类 (必需) ==========
  skillForm: enum            # SCENE | STANDALONE
  sceneType: enum            # AUTO | TRIGGER (仅SCENE时必需)
  visibility: enum           # public | internal
  
  # ========== 业务分类 (必需) ==========
  businessCategory: enum     # OFFICE_COLLABORATION | HUMAN_RESOURCE | AI_ASSISTANT | ...
  subCategory: string        # 子分类，如"日志汇报"

spec:
  # ========== 技能类型 (必需) ==========
  type: enum                 # scene-skill | service-skill | provider-skill | enterprise-skill | system-service
  
  # ========== 分类信息 (必需) ==========
  classification:
    category: string         # abs | tbs | ass | svc | driver | provider
    categoryName: string     # 分类显示名称
    mainFirst: boolean       # 是否主优先场景
    businessSemanticsScore: number  # 业务语义评分 0-10
    autoDetect: boolean      # 是否自动检测
  
  # ========== 能力地址配置 (必需) ==========
  capabilityAddresses:
    required:                # 必需能力地址，至少1个
      - address: hex         # 十六进制地址，如 0x30
        name: string         # 地址名称
        fallback: hex|null   # 降级地址，null表示必需
    optional: []             # 可选能力地址
  
  # ========== 依赖配置 (必需) ==========
  dependencies:
    - id: string             # 依赖技能ID
      version: string        # 版本要求，如 ">=1.0.0"
      required: boolean      # 是否必需
      autoInstall: boolean   # 是否自动安装
      description: string    # 描述
      capabilities: [string] # 依赖的能力
  
  # ========== 能力定义 (必需) ==========
  capabilities:
    - id: string             # 能力ID
      name: string           # 能力名称
      description: string    # 能力描述
      category: string       # 能力分类
      type: enum             # ATOMIC | COMPOSITE | DRIVER | SERVICE
      inputSchema: object    # 输入Schema (JSON Schema)
      outputSchema: object   # 输出Schema (JSON Schema)
  
  # ========== API端点 (必需) ==========
  endpoints:
    - path: string           # API路径
      method: string         # HTTP方法
      description: string    # 描述
      capability: string     # 关联的能力ID
  
  # ========== 运行时配置 (必需) ==========
  runtime:
    language: string         # 编程语言 (java)
    javaVersion: string      # Java版本 ("8")
    framework: string        # 框架 (spring-boot)
    mainClass: string        # 主类全名
  
  # ========== 配置项 (必需) ==========
  config:
    required:                # 必需配置项
      - name: string
        type: string         # string | number | boolean | object
        description: string
        secret: boolean      # 是否敏感
        validation: object   # 校验规则
    optional:                # 可选配置项
      - name: string
        type: string
        default: any         # 默认值
        description: string
  
  # ========== 资源需求 (必需) ==========
  resources:
    cpu: string              # 如 "100m"
    memory: string           # 如 "128Mi"
    storage: string          # 如 "50Mi"
  
  # ========== 离线模式 (必需) ==========
  offline:
    enabled: boolean         # 是否支持离线
    cacheStrategy: string    # local | remote
    syncOnReconnect: boolean # 重连后是否同步
```

### 3.2 场景技能特有字段

```yaml
spec:
  type: scene-skill
  
  # ========== 场景能力定义 (场景技能必需) ==========
  sceneCapabilities:
    - id: string             # 场景能力ID
      name: string           # 场景能力名称
      type: SCENE            # 固定值
      mainFirst: boolean     # 是否主优先
      
      # 主优先配置
      mainFirstConfig:
        # 自检配置
        selfCheck:
          checkCapabilities: [string]   # 检查的能力列表
          checkDependencies: [string]   # 检查的依赖列表
          onCheckFailed:
            action: enum               # degrade | retry | abort
            retry:
              maxAttempts: number
              delay: string            # 如 "5s"
        
        # 自启动配置
        selfStart:
          installDependencies: enum    # auto | manual
          initCapabilities: [string]   # 初始化的能力
          bindAddresses: enum          # auto | manual
        
        # 自驱配置
        selfDrive:
          eventRules:
            - event: string            # 事件名称
              action: string           # 触发的动作
          scheduleRules:
            - cron: string             # Cron表达式
              action: string
          capabilityChains:
            chainName:
              - capability: string     # 能力ID
                input: object          # 输入参数
      
      # 场景包含的能力
      capabilities: [string]   # 能力ID列表
      
      # 协作能力配置
      collaborativeCapabilities:
        - capabilityId: string
          role: enum               # PROVIDER | CONSUMER | BOTH
          interface: string        # 接口名称
          autoStart: boolean
          optional: boolean
  
  # ========== 能力绑定 (场景技能必需) ==========
  capabilityBindings:
    - sceneCapabilityType: string
      autoBind: boolean
      capabilities: [string]
```

### 3.3 Provider 技能特有字段

```yaml
spec:
  type: provider-skill
  
  # ========== Provider配置 (Provider技能必需) ==========
  provider:
    type: string             # llm | vfs | org | payment | ...
    vendor: string           # 厂商名称
    models:                  # 支持的模型列表
      - id: string
        name: string
        type: string         # chat | completion | embedding
        maxTokens: number
        supportsStreaming: boolean
```

---

## 四、完整配置示例

### 4.1 日志汇报场景 (办公协作)

```yaml
# ============================================
# 日志汇报场景 - 标准配置 v2.0
# 业务分类: 办公协作
# SE分类: SCENE + TRIGGER + public
# ============================================
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  # 基础信息
  id: skill-daily-report
  name: 日志汇报场景
  version: 3.0.0
  description: 团队协作的日志汇报系统，支持日报/周报/月报，自动汇总分析
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/ooder-skills
  repository: https://gitee.com/ooderCN/ooder-skills
  keywords: [日志, 汇报, 日报, 周报, 团队, 协作]
  
  # SE三维分类
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  # 业务分类
  businessCategory: OFFICE_COLLABORATION
  subCategory: 日志汇报

spec:
  type: scene-skill
  
  # 分类信息
  classification:
    category: tbs
    categoryName: 团队协作场景
    mainFirst: true
    businessSemanticsScore: 8
    autoDetect: true
  
  # 能力地址配置
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
  
  # 依赖配置
  dependencies:
    - id: skill-org-base
      version: ">=1.0.0"
      required: true
      autoInstall: true
      description: 组织基础服务
      capabilities: [org-management]
  
  # 场景能力定义
  sceneCapabilities:
    - id: scene-daily-report
      name: 日志汇报场景能力
      type: SCENE
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          checkCapabilities: [daily-report-submit, report-summary]
          checkDependencies: [skill-org-base]
          onCheckFailed:
            action: degrade
            retry:
              maxAttempts: 3
              delay: 5s
        
        selfStart:
          installDependencies: auto
          initCapabilities: [daily-report-submit, report-summary]
          bindAddresses: auto
        
        selfDrive:
          eventRules:
            - event: daily.remind
              action: send-reminder
          scheduleRules:
            - cron: "0 18 * * 1-5"
              action: daily-remind
          capabilityChains:
            send-reminder:
              - capability: daily-report-submit
                input:
                  action: remind
            submit-report:
              - capability: daily-report-submit
                input:
                  action: submit
              - capability: report-summary
                input:
                  action: analyze
      
      capabilities:
        - daily-report-submit
        - report-summary
        - report-export
      
      collaborativeCapabilities:
        - capabilityId: team-notification
          role: PROVIDER
          interface: notification-service
          autoStart: true
          optional: false
  
  # 能力定义
  capabilities:
    - id: daily-report-submit
      name: 日志提交
      description: 提交日报/周报/月报
      category: workflow
      type: ATOMIC
      inputSchema:
        type: object
        properties:
          reportType:
            type: string
            enum: [daily, weekly, monthly]
          content:
            type: string
          date:
            type: string
            format: date
      outputSchema:
        type: object
        properties:
          reportId:
            type: string
          status:
            type: string
    
    - id: report-summary
      name: 报告汇总
      description: 自动汇总分析日志
      category: ai
      type: ATOMIC
    
    - id: report-export
      name: 报告导出
      description: 导出报告为PDF/Excel
      category: tool
      type: ATOMIC
  
  # 能力绑定
  capabilityBindings:
    - sceneCapabilityType: daily-report
      autoBind: true
      capabilities:
        - daily-report-submit
        - report-summary
        - report-export
  
  # API端点
  endpoints:
    - path: /api/report/submit
      method: POST
      description: 提交日志
      capability: daily-report-submit
    - path: /api/report/summary
      method: GET
      description: 获取汇总
      capability: report-summary
  
  # 运行时配置
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.dailyreport.DailyReportApplication
  
  # 配置项
  config:
    required:
      - name: REPORT_REMIND_TIME
        type: string
        description: 提醒时间
        default: "18:00"
    optional:
      - name: ENABLE_WEEKLY_REPORT
        type: boolean
        default: true
        description: 是否启用周报
      - name: ENABLE_MONTHLY_REPORT
        type: boolean
        default: true
        description: 是否启用月报
  
  # 资源需求
  resources:
    cpu: "200m"
    memory: "256Mi"
    storage: "100Mi"
  
  # 离线模式
  offline:
    enabled: true
    cacheStrategy: local
    syncOnReconnect: true

# skills.md 文件必须存在
```

### 4.2 LLM Provider (智能助手)

```yaml
# ============================================
# Ollama LLM Provider - 标准配置 v2.0
# 业务分类: 智能助手 - 基础设施
# SE分类: STANDALONE + public
# ============================================
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-llm-ollama
  name: Ollama LLM Provider
  version: 3.0.0
  description: Ollama本地LLM服务驱动，支持多种开源模型
  author: Ooder Team
  license: Apache-2.0
  homepage: https://ollama.com
  repository: https://gitee.com/ooderCN/ooder-skills
  keywords: [llm, ollama, ai, provider, local]
  
  skillForm: STANDALONE
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: LLM驱动

spec:
  type: provider-skill
  
  classification:
    category: driver
    categoryName: LLM驱动
    mainFirst: false
    businessSemanticsScore: 0
    autoDetect: false
  
  capabilityAddresses:
    required:
      - address: 0x28
        name: LLM_OLLAMA
        fallback: null
    optional: []
  
  provider:
    type: llm
    vendor: ollama
    models:
      - id: qwen2.5:7b
        name: Qwen 2.5 7B
        type: chat
        maxTokens: 4096
        supportsStreaming: true
      - id: llama3.2:3b
        name: Llama 3.2 3B
        type: chat
        maxTokens: 4096
        supportsStreaming: true
  
  capabilities:
    - id: llm-chat
      name: 智能对话
      description: 基于Ollama的对话能力
      category: ai
      type: DRIVER
      driverType: INTENT_RECEIVER
    
    - id: llm-embedding
      name: 文本嵌入
      description: 生成文本向量嵌入
      category: ai
      type: DRIVER
  
  endpoints:
    - path: /api/llm/chat
      method: POST
      description: 对话接口
      capability: llm-chat
    - path: /api/llm/embeddings
      method: POST
      description: 嵌入接口
      capability: llm-embedding
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.llm.ollama.OllamaProviderApplication
  
  config:
    required:
      - name: OLLAMA_BASE_URL
        type: string
        default: "http://localhost:11434"
        description: Ollama服务地址
    optional:
      - name: DEFAULT_MODEL
        type: string
        default: "qwen2.5:7b"
        description: 默认模型
      - name: TIMEOUT_SECONDS
        type: number
        default: 60
        description: 超时时间(秒)
  
  resources:
    cpu: "500m"
    memory: "512Mi"
    storage: "1Gi"
  
  offline:
    enabled: false
```

---

## 五、迁移指南

### 5.1 迁移步骤

```
┌─────────────────────────────────────────────────────────────────┐
│                    配置迁移步骤                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 备份原始配置                                                 │
│     cp skill.yaml skill.yaml.backup                             │
│                                                                 │
│  2. 添加新必需字段                                               │
│     - skillForm / sceneType / visibility                        │
│     - businessCategory / subCategory                            │
│     - capabilityAddresses                                       │
│                                                                 │
│  3. 移动字段位置                                                 │
│     - metadata.type → spec.type                                 │
│     - metadata.category → spec.classification.category          │
│     - spec.capability → spec.capabilities                       │
│                                                                 │
│  4. 移除废弃字段                                                 │
│     - 删除所有标记为废弃的字段                                   │
│                                                                 │
│  5. 验证配置                                                     │
│     运行 SkillValidationRunner 验证                             │
│                                                                 │
│  6. 提交PR                                                      │
│     在PR描述中注明"配置标准化迁移"                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 字段映射表

| 旧字段路径 | 新字段路径 | 操作 |
|------------|------------|------|
| `metadata.type` | `spec.type` | 移动 |
| `metadata.category` | `spec.classification.category` | 移动 |
| `metadata.sceneCategory` | `spec.classification.category` | 移动 |
| `spec.category` | `spec.classification.category` | 移动 |
| `spec.capability` | `spec.capabilities` | 重构 |
| `spec.scenes` | `spec.sceneCapabilities` | 重构 |
| `spec.ownership` | - | 删除 |
| `spec.autoStart` | `spec.sceneCapabilities[].mainFirstConfig.selfStart` | 移动 |
| `spec.mainFirst` | `spec.sceneCapabilities[].mainFirst` | 移动 |
| `capability.address` | `spec.capabilityAddresses` | 重构 |

---

## 六、验证检查清单

### 6.1 迁移前检查

```markdown
## 迁移前检查清单

### 废弃字段清理
- [ ] 已移除 `metadata.type`
- [ ] 已移除 `metadata.category`
- [ ] 已移除 `spec.category`
- [ ] 已移除 `spec.capability`
- [ ] 已移除 `spec.scenes`
- [ ] 已移除 `spec.ownership`
- [ ] 已移除 `capability.address`

### 新字段添加
- [ ] 已添加 `metadata.skillForm`
- [ ] 已添加 `metadata.sceneType` (SCENE时)
- [ ] 已添加 `metadata.visibility`
- [ ] 已添加 `metadata.businessCategory`
- [ ] 已添加 `metadata.subCategory`
- [ ] 已添加 `spec.capabilityAddresses`

### 字段移动
- [ ] `metadata.type` → `spec.type`
- [ ] `metadata.category` → `spec.classification.category`
- [ ] `spec.capability` → `spec.capabilities`

### 验证
- [ ] 通过 SkillValidationRunner 验证
- [ ] 无废弃字段警告
- [ ] 所有必需字段已填充
```

### 6.2 PR 提交模板

```markdown
## 配置标准化迁移

### 变更内容
- 迁移技能: `skill-xxxx`
- 移除废弃字段: X 个
- 添加新字段: X 个
- 移动字段: X 个

### 废弃字段清单
| 字段 | 操作 |
|------|------|
| metadata.type | 已移除，使用 spec.type |
| ... | ... |

### 验证结果
- [x] SkillValidationRunner 通过
- [x] 无错误
- [x] 无警告

### 影响范围
- 无功能变更，仅配置格式调整
```

---

## 七、时间节点

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| **标准发布** | 2026-03-11 | 本标准文档 |
| **迁移开始** | 2026-03-11 | Skills Team 开始迁移 |
| **废弃字段移除截止** | 2026-03-25 | 所有技能移除废弃字段 |
| **验证完成** | 2026-03-28 | 通过自动化验证 |
| **正式发布** | 2026-04-01 | 技能市场 v3.0 上线 |

---

## 八、参考文档

| 文档 | 路径 |
|------|------|
| 强制执行标准 | `SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` |
| 用户视角设计 | `SE_SKILL_CLASSIFICATION_USER_VIEW.md` |
| 能力地址设计 | `CAPABILITY_ADDRESS_SPACE_DESIGN.md` |
| 协作排查文档 | `SKILLS_TEAM_COLLABORATION_TROUBLESHOOTING.md` |

---

**文档状态**: 强制执行  
**生效日期**: 2026-03-18  
**维护团队**: Engine Team
