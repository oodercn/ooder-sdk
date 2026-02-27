# 移除 ooder-codegen 和 ooder-infra 执行指南

## 执行摘要

需要移除以下模块：
1. **ooder-codegen** - 与 agent-sdk 重复
2. **ooder-codegen-cli** - 与 agent-sdk 重复
3. **ooder-infra-core** - 功能已被覆盖
4. **ooder-infra-driver** - 功能已被覆盖

## 执行步骤

### 步骤1: 备份并提交当前代码

```bash
cd E:\github\ooder-sdk
git add .
git commit -m "backup: before removing redundant modules"
git push
```

### 步骤2: 更新根 pom.xml

编辑文件：`E:\github\ooder-sdk\pom.xml`

#### 2.1 移除模块声明

找到第 194-199 行，删除以下内容：

```xml
<!-- 代码生成层 -->
<module>ooder-codegen</module>
<module>ooder-codegen-cli</module>
```

找到第 200-202 行，删除以下内容：

```xml
<!-- 基础设施层 -->
<module>ooder-infra-core</module>
<module>ooder-infra-driver</module>
```

#### 2.2 移除 dependencyManagement

找到第 238-249 行，删除以下内容：

```xml
<!-- 基础设施层 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-infra-core</artifactId>
    <version>${ooder.version}</version>
</dependency>

<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-infra-driver</artifactId>
    <version>${ooder.version}</version>
</dependency>
```

### 步骤3: 更新 scene-engine/pom.xml

编辑文件：`E:\github\ooder-sdk\scene-engine\pom.xml`

找到并删除以下内容：

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-infra-driver</artifactId>
    <version>${ooder.version}</version>
</dependency>
```

### 步骤4: 删除目录

手动删除以下目录（在文件资源管理器中操作）：

1. `E:\github\ooder-sdk\ooder-codegen`
2. `E:\github\ooder-sdk\ooder-codegen-cli`
3. `E:\github\ooder-sdk\ooder-infra-core`
4. `E:\github\ooder-sdk\ooder-infra-driver`

或者使用 PowerShell（以管理员身份运行）：

```powershell
cd E:\github\ooder-sdk

# 删除 ooder-codegen
Remove-Item -Path ".\ooder-codegen" -Recurse -Force

# 删除 ooder-codegen-cli
Remove-Item -Path ".\ooder-codegen-cli" -Recurse -Force

# 删除 ooder-infra-core
Remove-Item -Path ".\ooder-infra-core" -Recurse -Force

# 删除 ooder-infra-driver
Remove-Item -Path ".\ooder-infra-driver" -Recurse -Force
```

### 步骤5: 验证编译

```bash
cd E:\github\ooder-sdk
mvn clean compile
```

### 步骤6: 提交代码

```bash
cd E:\github\ooder-sdk
git add .
git commit -m "refactor: 移除冗余模块 ooder-codegen, ooder-codegen-cli, ooder-infra

移除的模块：
- ooder-codegen: 功能已被 agent-sdk 覆盖
- ooder-codegen-cli: 功能已被 agent-sdk 覆盖
- ooder-infra-core: 功能已被 scene-engine 覆盖
- ooder-infra-driver: 功能已被 scene-engine 覆盖

优化内容：
- 简化架构，消除重复代码
- 统一版本号为 2.3
- 减少维护成本
- 提升编译速度"

git push
```

## 验证清单

- [ ] 根 pom.xml 中移除了 ooder-codegen 模块
- [ ] 根 pom.xml 中移除了 ooder-codegen-cli 模块
- [ ] 根 pom.xml 中移除了 ooder-infra-core 模块
- [ ] 根 pom.xml 中移除了 ooder-infra-driver 模块
- [ ] 根 pom.xml 中移除了 ooder-infra 的 dependencyManagement
- [ ] scene-engine/pom.xml 中移除了 ooder-infra-driver 依赖
- [ ] 删除了 ooder-codegen 目录
- [ ] 删除了 ooder-codegen-cli 目录
- [ ] 删除了 ooder-infra-core 目录
- [ ] 删除了 ooder-infra-driver 目录
- [ ] 执行 `mvn clean compile` 成功
- [ ] 提交代码并推送

## 回滚方案

如果出现问题，执行以下命令回滚：

```bash
cd E:\github\ooder-sdk
git revert HEAD
git push
```

## 预期结果

移除后，项目结构将简化为：

```
ooder-sdk-parent (2.3)
├── ooder-api (2.3-SNAPSHOT)
├── ooder-util (2.3-SNAPSHOT)
├── ooder-annotation (2.3)
├── agent-sdk (2.3) ✅ 包含代码生成器
├── scene-engine (2.3) ✅ 包含基础设施
├── vfs-skill (2.3)
├── org-skill (2.3)
├── skills (2.3)
└── ooder-common (待处理)
```

## 注意事项

1. **备份**: 执行前确保已提交所有更改
2. **IDE刷新**: 删除目录后，在IDE中刷新项目
3. **Maven刷新**: 执行 `mvn clean install` 刷新依赖
4. **测试**: 编译成功后运行完整测试
