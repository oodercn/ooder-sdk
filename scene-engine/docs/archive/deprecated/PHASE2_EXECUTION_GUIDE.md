# Phase 2 执行指南 - 立即可执行

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **执行状态**: 准备就绪  
> **执行方**: Skills Team (有权限者)

---

## 一、执行前确认

### 1.1 前置条件检查

- [x] SDK枚举已实现 (Visibility, SkillForm, CapabilityCategory)
- [x] Phase 1基础层配置已完成 (config/目录)
- [ ] 确认有权限写入 `E:\github\ooder-skills\skills\` 目录

### 1.2 本次执行范围

**Batch 1 - P0核心技能 (5个场景技能)**:
1. skill-knowledge-qa (知识问答)
2. skill-llm-chat (LLM对话)
3. skill-collaboration (协作场景)
4. skill-business (业务场景)
5. skill-document-assistant (文档助手)

---

## 二、执行步骤

### Step 1: 进入工作目录

```bash
cd E:\github\ooder-skills\skills
```

### Step 2: 创建 skill-knowledge-qa 索引条目

```bash
cd scenes\skill-knowledge-qa

# 创建 skill-index-entry.yaml 文件
cat > skill-index-entry.yaml << 'EOF'
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-knowledge-qa
  name: 知识问答场景
  version: 3.0.0
  description: "基于知识库的智能问答场景，支持语义检索和AI回答"

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: 知识问答
  
  category: KNOWLEDGE
  capabilityCategory: know
  
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
  
  tags:
    - AI
    - 知识库
    - 问答
    - LLM
    - 语义检索
  
  dependencies:
    - skill-llm-ollama
    - skill-vector-chroma
  
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
EOF

cd ..\..
```

### Step 3: 创建 skill-llm-chat 索引条目

```bash
cd scenes\skill-llm-chat

cat > skill-index-entry.yaml << 'EOF'
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-llm-chat
  name: LLM智能对话
  version: 2.3.0
  description: "基于大语言模型的智能对话场景"

spec:
  skillForm: SCENE
  sceneType: AUTO
  visibility: public
  
  businessCategory: AI_ASSISTANT
  subCategory: 智能对话
  
  category: LLM
  capabilityCategory: llm
  
  capabilityAddresses:
    required:
      - address: 0x28
        name: LLM_OLLAMA
        fallback: 0x29
        description: "LLM服务"
    optional:
      - address: 0x34
        name: KNOW_EMBEDDING
        skipable: true
        description: "嵌入服务"
  
  tags:
    - AI
    - LLM
    - 对话
    - 聊天
  
  dependencies:
    - skill-llm-ollama
  
  roles:
    - name: MANAGER
      displayName: 场景管理员
      minCount: 1
      maxCount: 1
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: MEMBER
      displayName: 场景成员
      minCount: 0
      maxCount: 50
      permissions: [READ, WRITE]
EOF

cd ..\..
```

### Step 4: 创建 skill-collaboration 索引条目

```bash
cd scenes\skill-collaboration

cat > skill-index-entry.yaml << 'EOF'
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-collaboration
  name: 团队协作场景
  version: 2.0.0
  description: "团队协作、任务分配、进度跟踪场景"

spec:
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  businessCategory: OFFICE_COLLABORATION
  subCategory: 团队协作
  
  category: WORKFLOW
  capabilityCategory: org
  
  capabilityAddresses:
    required:
      - address: 0x08
        name: ORG_LOCAL
        description: "组织服务"
      - address: 0x48
        name: COMM_MSG
        description: "消息服务"
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
        description: "邮件通知"
  
  tags:
    - 协作
    - 团队
    - 任务
    - 办公
  
  dependencies:
    - skill-org-base
    - skill-msg
  
  roles:
    - name: MANAGER
      displayName: 项目经理
      minCount: 1
      maxCount: 2
      permissions: [READ, WRITE, CONFIG, DELETE]
    - name: LEADER
      displayName: 团队负责人
      minCount: 0
      maxCount: 5
      permissions: [READ, WRITE, CONFIG]
    - name: MEMBER
      displayName: 团队成员
      minCount: 0
      maxCount: 50
      permissions: [READ, WRITE]
EOF

cd ..\..
```

### Step 5: 创建 skill-business 索引条目

```bash
cd scenes\skill-business

cat > skill-index-entry.yaml << 'EOF'
apiVersion: skill.ooder.net/v1
kind: SkillIndexEntry

metadata:
  id: skill-business
  name: 业务流程场景
  version: 2.1.0
  description: "业务流程自动化、审批流程、工作流场景"

spec:
  skillForm: SCENE
  sceneType: TRIGGER
  visibility: public
  
  businessCategory: OFFICE_COLLABORATION
  subCategory: 业务流程
  
  category: WORKFLOW
  capabilityCategory: sched
  
  capabilityAddresses:
    required:
      - address: 0x68
        name: SCHED_QUARTZ
        description: "调度服务"
      - address: 0x20
        name: DB_SQLITE
        description: "数据库"
    optional:
      - address: 0x4A
        name: COMM_EMAIL
        skipable: true
        description: "邮件通知"
  
  tags:
    - 流程
    - 审批
    - 工作流
    - 自动化
  
  dependencies:
    - skill-scheduler-quartz
    - skill-msg
  
  roles:
    - name: MANAGER
      displayName: 流程管理员
      minCount: