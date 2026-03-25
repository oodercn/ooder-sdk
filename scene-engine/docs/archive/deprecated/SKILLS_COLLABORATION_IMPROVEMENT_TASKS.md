# Skills 协作改进任务清单

> 生成时间: 2026-03-12
> 版本: v1.0
> 范围: E:\github\ooder-skills\skills

---

## 📋 任务概览

| 优先级 | 任务类型 | 数量 | 状态 |
|--------|----------|------|------|
| 🔴 P0 | 配置缺失 | 51 | 待处理 |
| 🟡 P1 | 配置冲突 | 1 | 待处理 |
| 🟢 P2 | 文档缺失 | 29 | 待处理 |
| 🔵 P3 | 规范统一 | 60+ | 待处理 |

---

## 🔴 P0 优先级 - 配置缺失修复

### 1. 缺少 skill-index-entry.yaml (22个)

需要创建 `skill-index-entry.yaml` 的完整路径列表：

```
E:\github\ooder-skills\skills\capabilities\communication\skill-email\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\communication\skill-group\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\communication\skill-im\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\communication\skill-mqtt\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\communication\skill-msg\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\communication\skill-notify\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-knowledge-base\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-local-knowledge\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-vector-sqlite\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-config-manager\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-context-builder\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-cmd-service\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-monitor\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-remote-terminal\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-res-service\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\scheduler\skill-scheduler-quartz\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\security\skill-security\skill-index-entry.yaml
E:\github\ooder-skills\skills\scenes\skill-knowledge-qa\skill-index-entry.yaml
E:\github\ooder-skills\skills\scenes\skill-knowledge-share\skill-index-entry.yaml
E:\github\ooder-skills\skills\scenes\skill-meeting-minutes\skill-index-entry.yaml
E:\github\ooder-skills\skills\scenes\skill-onboarding-assistant\skill-index-entry.yaml
E:\github\ooder-skills\skills\scenes\skill-project-knowledge\skill-index-entry.yaml
```

#### 配置文件模板

每个文件应包含以下内容：

```yaml
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: {skill-id}
  name: {skill-display-name}
  version: "0.7.3"
  description: "{skill-description}"

spec:
  skillForm: {SCENE|PROVIDER|DRIVER|INTERNAL}
  visibility: developer
  businessCategory: {BUSINESS_CATEGORY}
  subCategory: {sub-category}
  category: {TECHNICAL_CATEGORY}
  capabilityCategory: {capability-cat}
  
  capabilityAddresses:
    required:
      - address: 0x{XX}
        name: {CAPABILITY_NAME}
        description: "{capability-description}"
    optional: []
  
  tags:
    - {tag1}
    - {tag2}
  
  dependencies: []
```

---

### 2. 缺少 skill.yaml (29个)

需要创建 `skill.yaml` 的完整路径列表：

```
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-deepseek\skill.yaml
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-ollama\skill.yaml
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-openai\skill.yaml
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-qianwen\skill.yaml
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-volcengine\skill.yaml
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-minio\skill.yaml
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-oss\skill.yaml
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-s3\skill.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-local-knowledge\skill.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-vector-sqlite\skill.yaml
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-config-manager\skill.yaml
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-context-builder\skill.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-cmd-service\skill.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-remote-terminal\skill.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-res-service\skill.yaml
E:\github\ooder-skills\skills\capabilities\scheduler\skill-scheduler-quartz\skill.yaml
E:\github\ooder-skills\skills\capabilities\security\skill-security\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-knowledge-qa\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-knowledge-share\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-meeting-minutes\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-onboarding-assistant\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-project-knowledge\skill.yaml
E:\github\ooder-skills\skills\skill-agent-recommendation\skill.yaml
E:\github\ooder-skills\skills\skill-capability-coordinator\skill.yaml
E:\github\ooder-skills\skills\skill-command-shortcut\skill.yaml
E:\github\ooder-skills\skills\skill-failover-manager\skill.yaml
E:\github\ooder-skills\skills\skill-httpclient-okhttp\skill.yaml
E:\github\ooder-skills\skills\skill-load-balancer\skill.yaml
E:\github\ooder-skills\skills\skill-update-checker\skill.yaml
```

#### 配置文件模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: {skill-id}
  name: {skill-display-name}
  version: "0.7.3"
  description: {skill-description}
  author: Ooder Team
  license: Apache-2.0
  homepage: https://gitee.com/ooderCN/skills
  keywords:
    - {keyword1}
    - {keyword2}

spec:
  type: {service-skill|provider-skill|driver-skill|scene-skill}
  ownership: platform
  
  capability:
    address: 0x{XX}
    category: {CAT}
    code: {CAT_CODE}
    operations: [{op1, op2}]
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: {fully-qualified-main-class}
  
  dependencies: []
  
  capabilities:
    - id: {capability-id}
      name: {capability-name}
      description: {capability-description}
      category: {category}
  
  endpoints:
    - path: {/api/endpoint}
      method: {GET|POST|PUT|DELETE}
      description: {endpoint-description}
      capability: {capability-id}
  
  config:
    required: []
    optional: []
  
  resources:
    cpu: "100m"
    memory: "256Mi"
    storage: "100Mi"
  
  offline:
    enabled: true
    cacheStrategy: local
    syncOnReconnect: true
```

---

## 🟡 P1 优先级 - 配置冲突修复

### Capability Address 冲突

**问题**: skill-knowledge-base 和 skill-rag 都声明了 address 0x38

**修复文件**:
```
E:\github\ooder-skills\skills\capabilities\knowledge\skill-rag\skill-index-entry.yaml
```

**修复内容**:
```yaml
# 当前（冲突）
capabilityAddresses:
  required:
    - address: 0x38      # ❌ 冲突，应移除
      name: KNOW_BASE
      description: "知识库基础服务"
    - address: 0x3A      # ✅ 保留
      name: KNOW_RAG
      description: "RAG检索服务"

# 修复后
capabilityAddresses:
  required:
    - address: 0x3A      # ✅ 仅保留RAG专属地址
      name: KNOW_RAG
      description: "RAG检索服务"
  optional: []
```

---

## 🟢 P2 优先级 - README.md 补充

### 需要创建 README.md 的完整路径

```
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-deepseek\README.md
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-ollama\README.md
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-openai\README.md
E:\github\ooder-skills\skills\_drivers\llm\skill-llm-qianwen\README.md
E:\github\ooder-skills\skills\_drivers\org\skill-org-base\README.md
E:\github\ooder-skills\skills\_drivers\payment\skill-payment-alipay\README.md
E:\github\ooder-skills\skills\_drivers\payment\skill-payment-unionpay\README.md
E:\github\ooder-skills\skills\_drivers\payment\skill-payment-wechat\README.md
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-base\README.md
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-minio\README.md
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-oss\README.md
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-s3\README.md
E:\github\ooder-skills\skills\capabilities\knowledge\skill-local-knowledge\README.md
E:\github\ooder-skills\skills\capabilities\knowledge\skill-vector-sqlite\README.md
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-config-manager\README.md
E:\github\ooder-skills\skills\capabilities\llm\skill-llm-context-builder\README.md
E:\github\ooder-skills\skills\capabilities\monitor\skill-cmd-service\README.md
E:\github\ooder-skills\skills\capabilities\monitor\skill-monitor\README.md
E:\github\ooder-skills\skills\capabilities\monitor\skill-remote-terminal\README.md
E:\github\ooder-skills\skills\capabilities\monitor\skill-res-service\README.md
E:\github\ooder-skills\skills\capabilities\scheduler\skill-scheduler-quartz\README.md
E:\github\ooder-skills\skills\capabilities\security\skill-access-control\README.md
E:\github\ooder-skills\skills\capabilities\security\skill-audit\README.md
E:\github\ooder-skills\skills\scenes\skill-knowledge-qa\README.md
E:\github\ooder-skills\skills\scenes\skill-knowledge-share\README.md
E:\github\ooder-skills\skills\scenes\skill-meeting-minutes\README.md
E:\github\ooder-skills\skills\scenes\skill-onboarding-assistant\README.md
E:\github\ooder-skills\skills\scenes\skill-project-knowledge\README.md
```

---

## 🔵 P3 优先级 - 规范统一

### 1. 版本号格式统一

**需要修改的文件列表**:

```
E:\github\ooder-skills\skills\capabilities\knowledge\skill-knowledge-base\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-knowledge-base\skill-index-entry.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-agent\skill.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-monitor\src\main\resources\skill.yaml
```

**修改规则**:
```yaml
# 从
version: 2.3        # ❌ 不规范
version: "2.3"      # ❌ 不规范

# 改为
version: "2.3.1"    # ✅ 规范 (语义化版本)
```

---

### 2. skill.yaml 位置统一

**当前位置不一致的文件**:

```
# 根目录位置
E:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-local\skill.yaml
E:\github\ooder-skills\skills\capabilities\security\skill-access-control\skill.yaml

# src/resources 位置
E:\github\ooder-skills\skills\_system\skill-capability\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\_system\skill-management\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\_system\skill-protocol\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\capabilities\knowledge\skill-knowledge-base\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\capabilities\monitor\skill-monitor\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-business\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-collaboration\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-document-assistant\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\scenes\skill-llm-chat\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\skill-scene\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\tools\skill-market\src\main\resources\skill.yaml
E:\github\ooder-skills\skills\tools\skill-share\src\main\resources\skill.yaml
```

**建议**: 统一移动到根目录 `skill.yaml`

---

## 📁 批量操作脚本

### Windows PowerShell 脚本

```powershell
# 创建所有缺失的 skill-index-entry.yaml
$missingIndexEntries = @(
    "capabilities\communication\skill-email",
    "capabilities\communication\skill-group",
    "capabilities\communication\skill-im",
    "capabilities\communication\skill-mqtt",
    "capabilities\communication\skill-msg",
    "capabilities\communication\skill-notify",
    "capabilities\knowledge\skill-knowledge-base",
    "capabilities\knowledge\skill-local-knowledge",
    "capabilities\knowledge\skill-vector-sqlite",
    "capabilities\llm\skill-llm-config-manager",
    "capabilities\llm\skill-llm-context-builder",
    "capabilities\monitor\skill-cmd-service",
    "capabilities\monitor\skill-monitor",
    "capabilities\monitor\skill-remote-terminal",
    "capabilities\monitor\skill-res-service",
    "capabilities\scheduler\skill-scheduler-quartz",
    "capabilities\security\skill-security",
    "scenes\skill-knowledge-qa",
    "scenes\skill-knowledge-share",
    "scenes\skill-meeting-minutes",
    "scenes\skill-onboarding-assistant",
    "scenes\skill-project-knowledge"
)

foreach ($path in $missingIndexEntries) {
    $fullPath = "E:\github\ooder-skills\skills\$path\skill-index-entry.yaml"
    if (-not (Test-Path $fullPath)) {
        New-Item -ItemType File -Path $fullPath -Force
        Write-Host "Created: $fullPath"
    }
}
```

---

## ✅ 任务完成检查清单

### Phase 1: P0 配置缺失修复
- [ ] 创建 22 个 skill-index-entry.yaml
- [ ] 创建 29 个 skill.yaml
- [ ] 验证所有配置文件格式正确

### Phase 2: P1 配置冲突修复
- [ ] 修复 skill-rag 的 capability address 冲突
- [ ] 验证 address 唯一性

### Phase 3: P2 文档补充
- [ ] 创建 29 个 README.md

### Phase 4: P3 规范统一
- [ ] 统一版本号格式为 x.y.z
- [ ] 统一 skill.yaml 位置到根目录
- [ ] 统一 dependencies 格式

---

## 📊 工作量估算

| 任务 | 数量 | 预估时间 | 负责人 |
|------|------|----------|--------|
| 创建 skill-index-entry.yaml | 22 | 2小时 | TBD |
| 创建 skill.yaml | 29 | 4小时 | TBD |
| 修复配置冲突 | 1 | 0.5小时 | TBD |
| 创建 README.md | 29 | 3小时 | TBD |
| 规范统一 | 60+ | 4小时 | TBD |
| **总计** | **141** | **13.5小时** | - |

---

## 📝 备注

1. 所有路径基于 `E:\github\ooder-skills\skills`
2. 配置文件模板参考已存在的规范文件
3. Capability Address 分配需避免冲突，建议使用统一分配表
4. 版本号建议统一为 `2.3.1` 或 `0.7.3`

---

**文档生成**: 2026-03-12  
**最后更新**: 2026-03-12  
**维护者**: Ooder Skills Team
