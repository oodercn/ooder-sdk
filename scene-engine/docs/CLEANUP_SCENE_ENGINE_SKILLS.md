# Scene-Engine Skill 模块清理指南

## 背景

根据 ooder-skills 仓库的结构，所有 skill 模块已经迁移到独立的仓库 `E:\github\ooder-skills\skills\` 中。

scene-engine 中残留的 skill-* 模块需要清理，以避免重复和维护负担。

## 需要删除的模块

### 1. skill-mqtt/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-mqtt/`

### 2. skill-msg/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-msg/`

### 3. skill-org/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-org-*/`
  - skill-org-base
  - skill-org-dingding
  - skill-org-feishu
  - skill-org-ldap
  - skill-org-wecom

### 4. skill-vfs/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-vfs-*/`
  - skill-vfs-base
  - skill-vfs-database
  - skill-vfs-local
  - skill-vfs-minio
  - skill-vfs-oss
  - skill-vfs-s3

### 5. skill-agent/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-agent/`

### 6. skill-security/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-security/`

### 7. skill-business/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-business/`

### 8. skill-ai/
- 整个目录删除
- 已迁移到 `ooder-skills/skills/skill-llm-*/`
  - skill-llm-deepseek
  - skill-llm-ollama
  - skill-llm-openai
  - skill-llm-qianwen
  - skill-llm-volcengine

## 执行步骤

### 步骤1: 备份当前代码

```bash
cd E:\github\ooder-sdk
git add .
git commit -m "backup: before cleaning up scene-engine skills"
git push
```

### 步骤2: 删除 skill 模块目录

手动删除或使用 PowerShell（以管理员身份运行）：

```powershell
cd E:\github\ooder-sdk\scene-engine

# 删除 skill-mqtt
Remove-Item -Path ".\skill-mqtt" -Recurse -Force

# 删除 skill-msg
Remove-Item -Path ".\skill-msg" -Recurse -Force

# 删除 skill-org
Remove-Item -Path ".\skill-org" -Recurse -Force

# 删除 skill-vfs
Remove-Item -Path ".\skill-vfs" -Recurse -Force

# 删除 skill-agent
Remove-Item -Path ".\skill-agent" -Recurse -Force

# 删除 skill-security
Remove-Item -Path ".\skill-security" -Recurse -Force

# 删除 skill-business
Remove-Item -Path ".\skill-business" -Recurse -Force

# 删除 skill-ai
Remove-Item -Path ".\skill-ai" -Recurse -Force
```

### 步骤3: 更新 scene-engine/pom.xml

编辑文件：`E:\github\ooder-sdk\scene-engine\pom.xml`

找到 `<modules>` 部分，删除所有 skill-* 模块：

```xml
<!-- 修改前 -->
<modules>
    <module>scene-engine</module>
    <module>skill-org</module>
    <module>skill-vfs</module>
    <module>skill-msg</module>
    <module>skill-mqtt</module>
    <module>skill-agent</module>
    <module>skill-security</module>
    <module>skill-business</module>
    <module>skill-ai</module>
    <module>scene-gateway</module>
</modules>

<!-- 修改后 -->
<modules>
    <module>scene-engine</module>
    <module>scene-gateway</module>
</modules>
```

### 步骤4: 验证编译

```bash
cd E:\github\ooder-sdk\scene-engine
mvn clean compile
```

### 步骤5: 提交代码

```bash
cd E:\github\ooder-sdk
git add scene-engine/pom.xml
git commit -m "refactor: 清理 scene-engine 中已迁移的 skill 模块

删除的模块：
- skill-mqtt (已迁移到 ooder-skills)
- skill-msg (已迁移到 ooder-skills)
- skill-org (已迁移到 ooder-skills)
- skill-vfs (已迁移到 ooder-skills)
- skill-agent (已迁移到 ooder-skills)
- skill-security (已迁移到 ooder-skills)
- skill-business (已迁移到 ooder-skills)
- skill-ai (已迁移到 ooder-skills)

优化内容：
- 避免与 ooder-skills 仓库重复
- 简化 scene-engine 结构
- 统一在 ooder-skills 中维护技能模块"

git push
```

## 验证清单

- [ ] 删除 skill-mqtt/ 目录
- [ ] 删除 skill-msg/ 目录
- [ ] 删除 skill-org/ 目录
- [ ] 删除 skill-vfs/ 目录
- [ ] 删除 skill-agent/ 目录
- [ ] 删除 skill-security/ 目录
- [ ] 删除 skill-business/ 目录
- [ ] 删除 skill-ai/ 目录
- [ ] 更新 scene-engine/pom.xml 的 modules
- [ ] 验证编译成功
- [ ] 提交代码

## 回滚方案

如果出现问题，执行以下命令回滚：

```bash
cd E:\github\ooder-sdk
git revert HEAD
git push
```

## 注意事项

1. **备份**: 执行前确保已提交所有更改
2. **IDE刷新**: 删除目录后，在IDE中刷新项目
3. **依赖检查**: 确保 scene-engine 核心模块不依赖这些 skill 模块
4. **测试**: 编译成功后运行完整测试

## 迁移后的架构

```
ooder-sdk/
├── scene-engine/
│   ├── scene-engine/          # 核心引擎
│   └── scene-gateway/         # 网关
└── ...

ooder-skills/
└── skills/
    ├── skill-mqtt/
    ├── skill-msg/
    ├── skill-org-*/
    ├── skill-vfs-*/
    ├── skill-agent/
    ├── skill-security/
    ├── skill-business/
    └── skill-llm-*/
```

这种分离的架构：
- **职责清晰**: scene-engine 专注核心引擎，skills 专注业务技能
- **独立演进**: skills 可以独立开发、测试、发布
- **按需加载**: 运行时通过 SPI 机制加载需要的技能
