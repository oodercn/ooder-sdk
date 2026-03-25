# TODO 实现计划与依赖分析

**分析日期**: 2026-03-06  
**版本**: scene-engine v2.3  
**状态**: 规划中

---

## 一、TODO 清单分析

### 1.1 InstallCoordinator.installSkill()

**位置**: `coordinator/InstallCoordinator.java:204-215`

**当前代码**:
```java
private void installSkill(RichSkill skill) throws Exception {
    if (skillInstaller == null) {
        throw new IllegalStateException("SkillInstaller not configured");
    }
    
    // 调用SDK层Installer进行实际安装
    // skillInstaller.install(skill.getSkillPackage());
    
    // 当前为简化实现，模拟安装耗时
    // TODO: 移除模拟代码，启用实际安装
    Thread.sleep(1000);
}
```

**问题**: 仅模拟安装过程，未调用真实的 SkillInstaller

---

### 1.2 RichSkill.getDependencies()

**位置**: `model/RichSkill.java:118-123`

**当前代码**:
```java
public List<RichSkill> getDependencies() {
    // 从DiscoveryCoordinator查询依赖的RichSkill
    // 注意：当前为简化实现，返回空列表
    // TODO: 集成 DiscoveryCoordinator 实现实际查询
    return java.util.Collections.emptyList();
}
```

**问题**: 无法获取技能的依赖关系，影响安装计划的拓扑排序

---

## 二、依赖关系分析

### 2.1 InstallCoordinator.installSkill() 依赖图

```
InstallCoordinator.installSkill()
    │
    ├── depends on ──► SkillInstaller (外部接口)
    │                      │
    │                      ├── implemented by ──► SkillSDKAdapter (已存在)
    │                      │                      │
    │                      │                      ├── depends on ──► SkillPackage
    │                      │                      │
    │                      │                      └── depends on ──► 文件系统/网络
    │                      │
    │                      └── implemented by ──► 外部 Skill 实现
    │
    ├── depends on ──► RichSkill.getSkillPackage()
    │
    └── depends on ──► InstallSession (状态更新)
```

**关键依赖**:
1. `SkillInstaller` 接口 - 已定义，需要确认实现可用性
2. `SkillSDKAdapter` - 已存在，但可能有未实现的方法
3. 文件系统/网络 - 用于下载和安装 Skill 包

---

### 2.2 RichSkill.getDependencies() 依赖图

```
RichSkill.getDependencies()
    │
    ├── depends on ──► DiscoveryCoordinator (外部服务)
    │                      │
    │                      ├── depends on ──► SkillRegistry (技能注册表)
    │                      │                      │
    │                      │                      ├── depends on ──► 本地存储
    │                      │                      │
    │                      │                      └── depends on ──► 远程仓库
    │                      │
    │                      └── depends on ──► RichSkill 缓存
    │
    ├── depends on ──► rawPackage.getDependencies() (依赖列表)
    │
    └── depends on ──► skillService.findSkill() (查询技能)
```

**关键依赖**:
1. `DiscoveryCoordinator` - 已存在，需要集成
2. `SkillService` - 已定义，需要注入到 RichSkill
3. `rawPackage.getDependencies()` - 需要确认返回格式

---

## 三、实现方案

### 3.1 方案 A: 直接集成现有组件 (推荐)

**实现 InstallCoordinator.installSkill()**:

```java
private void installSkill(RichSkill skill) throws Exception {
    if (skillInstaller == null) {
        throw new IllegalStateException("SkillInstaller not configured");
    }
    
    // 获取技能包
    SkillPackage skillPackage = skill.getSkillPackage();
    if (skillPackage == null) {
        throw new IllegalArgumentException("Skill package is null");
    }
    
    // 调用 SDK Installer 进行实际安装
    try {
        skillInstaller.install(skillPackage);
        session.addLog("Skill installed successfully: " + skill.getSkillId());
    } catch (Exception e) {
        session.addLog("Failed to install skill: " + e.getMessage());
        throw e;
    }
}
```

**依赖检查**:
- ✅ `SkillInstaller` 接口已定义
- ✅ `SkillSDKAdapter` 已存在
- ⚠️ 需要确认 `SkillSDKAdapter.install()` 实现是否完整

---

**实现 RichSkill.getDependencies()**:

```java
public List<RichSkill> getDependencies() {
    // 获取依赖ID列表
    List<String> dependencyIds = rawPackage.getDependencies();
    if (dependencyIds == null || dependencyIds.isEmpty()) {
        return java.util.Collections.emptyList();
    }
    
    // 使用 SkillService 查询依赖的 RichSkill
    List<RichSkill> dependencies = new ArrayList<>();
    if (skillService != null) {
        for (String depId : dependencyIds) {
            RichSkill depSkill = skillService.findSkill(depId);
            if (depSkill != null) {
                dependencies.add(depSkill);
            }
        }
    }
    
    return dependencies;
}
```

**依赖检查**:
- ✅ `SkillService` 接口已定义
- ✅ `RichSkill` 已有 `skillService` 字段
- ⚠️ 需要确保 `skillService` 在创建 RichSkill 时被注入

---

### 3.2 方案 B: 增强现有组件

如果现有组件功能不足，需要增强：

**增强 SkillSDKAdapter**:
- 完善 `install()` 方法实现
- 添加错误处理和日志
- 支持安装进度回调

**增强 RichSkill 创建流程**:
- 在创建 RichSkill 时注入 `skillService`
- 确保 `DiscoveryCoordinator` 可访问

---

## 四、实现计划

### 4.1 任务分解

| 任务 | 优先级 | 依赖 | 预计工时 | 状态 |
|------|--------|------|----------|------|
| T1: 检查 SkillSDKAdapter.install() 实现 | P0 | - | 1h | 待开始 |
| T2: 检查 SkillInstaller 配置 | P0 | T1 | 1h | 待开始 |
| T3: 实现 InstallCoordinator.installSkill() | P0 | T2 | 2h | 待开始 |
| T4: 检查 RichSkill skillService 注入 | P1 | - | 1h | 待开始 |
| T5: 实现 RichSkill.getDependencies() | P1 | T4 | 2h | 待开始 |
| T6: 测试安装流程 | P1 | T3, T5 | 4h | 待开始 |
| T7: 测试依赖解析 | P2 | T5 | 2h | 待开始 |

### 4.2 实施顺序

```
Week 1:
  Day 1: T1, T2 (检查现有实现)
  Day 2: T3 (实现安装)
  Day 3: T4 (检查注入)
  Day 4: T5 (实现依赖查询)
  Day 5: T6 (测试安装)

Week 2:
  Day 1-2: T7 (测试依赖)
  Day 3-5: 修复问题和优化
```

---

## 五、风险评估

### 5.1 技术风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| SkillSDKAdapter 实现不完整 | 中 | 高 | 检查并补充实现 |
| SkillInstaller 未配置 | 低 | 高 | 添加配置检查 |
| 循环依赖 | 中 | 中 | 添加循环依赖检测 |
| 网络下载失败 | 高 | 中 | 添加重试机制 |

### 5.2 依赖风险

| 依赖 | 状态 | 风险 | 备选方案 |
|------|------|------|----------|
| SkillInstaller | 接口已定义 | 低 | 使用 Mock 实现测试 |
| SkillService | 接口已定义 | 低 | 直接注入 |
| DiscoveryCoordinator | 已存在 | 低 | 直接使用 |

---

## 六、验收标准

### 6.1 InstallCoordinator.installSkill()

- [ ] 能够调用真实的 SkillInstaller
- [ ] 安装成功时更新 InstallSession 状态
- [ ] 安装失败时记录错误信息
- [ ] 支持安装进度回调
- [ ] 单元测试覆盖率 > 80%

### 6.2 RichSkill.getDependencies()

- [ ] 能够解析 rawPackage.getDependencies()
- [ ] 能够查询依赖的 RichSkill
- [ ] 返回的依赖列表按安装顺序排序
- [ ] 处理依赖不存在的情况
- [ ] 单元测试覆盖率 > 80%

---

## 七、相关文档

- [ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md) - 架构图
- [SECONDARY_DEVELOPMENT_GUIDE.md](./SECONDARY_DEVELOPMENT_GUIDE.md) - 二次开发指南
- [CODE_QUALITY_REPORT.md](./CODE_QUALITY_REPORT.md) - 代码质量报告

---

**计划制定**: 2026-03-06  
**计划版本**: 1.0  
**负责人**: 待分配
