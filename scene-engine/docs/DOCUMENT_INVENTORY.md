# Scene-Engine 文档清单与整理方案

## 一、当前文档清单

### 1. 过程性/待移除文档（7个）

| 文档路径 | 类型 | 状态 | 说明 |
|---------|------|------|------|
| `CLEANUP_SCENE_ENGINE_SKILLS.md` | 过程性 | ❌ 移除 | 清理 skill 模块的操作指南，已执行 |
| `DIRECTORY_STRUCTURE_REFACTORING.md` | 过程性 | ❌ 移除 | 目录重构方案，已执行 |
| `REMOVE_CODEGEN_AND_INFRA.md` | 过程性 | ❌ 移除 | 移除模块的操作指南，已执行 |
| `SKILL_MODULES_DEPENDENCY_ANALYSIS.md` | 分析性 | ❌ 移除 | 临时分析文档，已过时 |
| `SCENE_GATEWAY_PROTOCOL_ANALYSIS.md` | 分析性 | ❌ 移除 | scene-gateway 分析，模块已移除 |
| `agent-protocol.md` | 重复 | ❌ 移除 | 与 protocol/v0.7.3/agent-protocol.md 重复 |
| `SCENE_ENGINE_DESIGN.md` | 临时 | ❌ 移除 | 临时设计文档，内容已过时 |

### 2. 需要保留的核心文档（4个）

| 文档路径 | 类型 | 状态 | 说明 |
|---------|------|------|------|
| `protocol/v0.7.3/agent-protocol.md` | 协议规范 | ✅ 保留 | v0.7.3 Agent 协议规范 |
| `protocol/v0.7.3/protocol-main.md` | 协议规范 | ✅ 保留 | v0.7.3 协议主文档 |
| `protocol/v0.7.3/skill-discovery-protocol.md` | 协议规范 | ✅ 保留 | v0.7.3 技能发现协议 |
| `protocol/v0.8.0/discovery-implementation-guide.md` | 实现指南 | ✅ 保留 | v0.8.0 发现实现指南 |

## 二、文档校验结果

### 2.1 保留文档校验

#### ✅ `protocol/v0.7.3/agent-protocol.md`
- **版本**: v0.7.3
- **状态**: 有效
- **内容**: Agent 协议规范
- **建议**: 保留，作为历史协议文档

#### ✅ `protocol/v0.7.3/protocol-main.md`
- **版本**: v0.7.3
- **状态**: 有效
- **内容**: 协议主文档
- **建议**: 保留，作为历史协议文档

#### ✅ `protocol/v0.7.3/skill-discovery-protocol.md`
- **版本**: v0.7.3
- **状态**: 有效
- **内容**: 技能发现协议
- **建议**: 保留，作为历史协议文档

#### ✅ `protocol/v0.8.0/discovery-implementation-guide.md`
- **版本**: v0.8.0
- **状态**: 有效
- **内容**: 发现实现指南
- **建议**: 保留，当前版本实现参考

### 2.2 移除文档原因

| 文档 | 移除原因 |
|------|----------|
| `CLEANUP_SCENE_ENGINE_SKILLS.md` | 操作指南，skill 模块已清理完成 |
| `DIRECTORY_STRUCTURE_REFACTORING.md` | 重构方案，目录已整理完成 |
| `REMOVE_CODEGEN_AND_INFRA.md` | 操作指南，模块已移除完成 |
| `SKILL_MODULES_DEPENDENCY_ANALYSIS.md` | 临时分析，分析结果已应用 |
| `SCENE_GATEWAY_PROTOCOL_ANALYSIS.md` | scene-gateway 已移除，文档过时 |
| `agent-protocol.md` (根目录) | 与 protocol/v0.7.3/ 下文档重复 |
| `SCENE_ENGINE_DESIGN.md` | 临时设计文档，内容已过时 |

## 三、整理后文档结构

```
docs/
├── README.md                          # 新增：文档目录说明
├── protocol/
│   ├── README.md                      # 新增：协议文档索引
│   ├── v0.7.3/
│   │   ├── README.md                  # 新增：v0.7.3 协议说明
│   │   ├── agent-protocol.md          # 保留：Agent 协议
│   │   ├── protocol-main.md           # 保留：协议主文档
│   │   └── skill-discovery-protocol.md # 保留：发现协议
│   └── v0.8.0/
│       ├── README.md                  # 新增：v0.8.0 协议说明
│       └── discovery-implementation-guide.md # 保留：实现指南
└── architecture/                      # 新增：架构文档目录
    └── README.md                      # 新增：架构说明
```

## 四、执行步骤

### 步骤1: 移除废止文档

```powershell
cd E:\github\ooder-sdk\scene-engine\docs

# 移除过程性文档
Remove-Item -Path "CLEANUP_SCENE_ENGINE_SKILLS.md" -Force
Remove-Item -Path "DIRECTORY_STRUCTURE_REFACTORING.md" -Force
Remove-Item -Path "REMOVE_CODEGEN_AND_INFRA.md" -Force
Remove-Item -Path "SKILL_MODULES_DEPENDENCY_ANALYSIS.md" -Force
Remove-Item -Path "SCENE_GATEWAY_PROTOCOL_ANALYSIS.md" -Force
Remove-Item -Path "SCENE_ENGINE_DESIGN.md" -Force

# 移除重复文档
Remove-Item -Path "agent-protocol.md" -Force

# 移除本清单（执行完成后）
Remove-Item -Path "DOCUMENT_INVENTORY.md" -Force
```

### 步骤2: 创建文档索引

创建 `docs/README.md`：

```markdown
# Scene-Engine 文档

## 协议文档

### v0.8.0（当前版本）
- [发现实现指南](protocol/v0.8.0/discovery-implementation-guide.md)

### v0.7.3（历史版本）
- [Agent 协议](protocol/v0.7.3/agent-protocol.md)
- [协议主文档](protocol/v0.7.3/protocol-main.md)
- [技能发现协议](protocol/v0.7.3/skill-discovery-protocol.md)

## 架构文档
- 待补充
```

### 步骤3: 提交代码

```bash
cd E:\github\ooder-sdk
git add scene-engine/docs/
git commit -m "docs: 整理文档目录，移除废止文档

- 移除7个过程性/临时/重复文档
- 保留4个核心协议文档
- 创建文档索引 README"
git push
```

## 五、验证清单

- [ ] 移除 `CLEANUP_SCENE_ENGINE_SKILLS.md`
- [ ] 移除 `DIRECTORY_STRUCTURE_REFACTORING.md`
- [ ] 移除 `REMOVE_CODEGEN_AND_INFRA.md`
- [ ] 移除 `SKILL_MODULES_DEPENDENCY_ANALYSIS.md`
- [ ] 移除 `SCENE_GATEWAY_PROTOCOL_ANALYSIS.md`
- [ ] 移除 `SCENE_ENGINE_DESIGN.md`
- [ ] 移除根目录 `agent-protocol.md`
- [ ] 创建 `docs/README.md`
- [ ] 提交代码

## 六、注意事项

1. **备份**: 确保已提交当前代码
2. **验证**: 移除后检查文档链接是否有效
3. **历史**: 如需查看历史文档，可从 git 历史恢复
