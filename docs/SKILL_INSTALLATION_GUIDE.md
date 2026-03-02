# Skill 安装步骤逻辑指南

> **版本**: 2.3  
> **日期**: 2026-03-02  
> **目标读者**: Skills 开发者、SDK 开发者

---

## 目录

1. [Skill 类型概述](#1-skill-类型概述)
2. [通用安装流程](#2-通用安装流程)
3. [各类型 Skill 安装步骤](#3-各类型-skill-安装步骤)
4. [依赖处理逻辑](#4-依赖处理逻辑)
5. [安装状态管理](#5-安装状态管理)

---

## 1. Skill 类型概述

| Skill 类型 | 说明 | 示例 |
|-----------|------|------|
| **nexus-ui** | Nexus 前端 UI 组件 | skill-knowledge-ui |
| **scene** | 场景定义 | skill-knowledge-base |
| **capability** | 能力实现 | skill-llm-openai |
| **adapter** | 适配器 | skill-org-feishu-adapter |
| **driver** | 驱动程序 | skill-mqtt-driver |
| **service** | 后台服务 | skill-notification-service |
| **library** | 工具库 | skill-common-utils |

---

## 2. 通用安装流程

```
installWithDependencies(skillId)
    │
    ├─ 1. 获取 Skill 元数据
    │      └─ getManifest(skillId) → SkillManifest
    │
    ├─ 2. 解析依赖列表
    │      └─ manifest.getDependencies() → List<Dependency>
    │
    ├─ 3. 递归安装依赖
    │      └─ installWithDependencies(depSkillId)
    │
    ├─ 4. 检查主 Skill 是否已安装
    │      └─ isInstalled(skillId)
    │
    ├─ 5. 根据 Skill 类型执行特定安装步骤
    │      └─ installByType(skillType)
    │
    └─ 6. 注册 Skill
           └─ registerSkill(skillId)
```

---

## 3. 各类型 Skill 安装步骤

### 3.1 nexus-ui 类型

**安装步骤**:

```
installNexusUi(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 下载 UI 资源
    │      ├─ 下载 JS 文件
    │      ├─ 下载 CSS 文件
    │      └─ 下载静态资源
    │
    ├─ 3. 解析 UI 配置
    │      ├─ 解析菜单配置 → MenuConfig
    │      ├─ 解析路由配置 → RouteConfig
    │      └─ 解析入口配置
    │
    ├─ 4. 注册到 UI 注册表
    │      └─ NexusUiRegistry.register(config)
    │
    ├─ 5. 更新前端路由
    │      └─ 通知 console-ui 刷新
    │
    └─ 6. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

**配置示例**:
```yaml
skillId: skill-knowledge-ui
skillType: nexus-ui
config:
  ui:
    entryPath: /knowledge
    menu:
      menuId: knowledge-menu
      title: 知识库管理
      path: /knowledge
    routes:
      - path: /knowledge
        component: KnowledgeBasePage
```

---

### 3.2 scene 类型

**安装步骤**:

```
installScene(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载场景定义
    │      ├─ 解析场景配置 → SceneConfig
    │      ├─ 解析能力定义 → List<Capability>
    │      └─ 解析协作场景 → List<CollaborativeScene>
    │
    ├─ 3. 注册场景
    │      └─ SceneManager.register(sceneConfig)
    │
    ├─ 4. 初始化场景状态
    │      ├─ 创建场景实例
    │      ├─ 初始化场景上下文
    │      └─ 设置场景状态为 INACTIVE
    │
    ├─ 5. 绑定能力
    │      └─ 将场景与能力关联
    │
    └─ 6. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

**配置示例**:
```yaml
skillId: skill-knowledge-base
skillType: scene
mainFirstScene:
  sceneId: knowledge-base
  sceneName: 知识库场景
sceneCapabilities:
  - capabilityId: kb-management
    name: 知识库管理
```

---

### 3.3 capability 类型

**安装步骤**:

```
installCapability(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载能力定义
    │      ├─ 解析能力接口 → Capability
    │      ├─ 解析实现类
    │      └─ 解析配置参数
    │
    ├─ 3. 注册能力
    │      └─ CapabilityRegistry.register(capability)
    │
    ├─ 4. 初始化能力
    │      ├─ 创建能力实例
    │      ├─ 注入依赖
    │      └─ 调用初始化方法
    │
    ├─ 5. 发布能力接口
    │      └─ publishInterface(capability)
    │
    └─ 6. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

**配置示例**:
```yaml
skillId: skill-llm-openai
skillType: capability
providedInterfaces:
  - llm-provider
capabilities:
  - capabilityId: openai-chat
    interface: llm-provider
```

---

### 3.4 adapter 类型

**安装步骤**:

```
installAdapter(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载适配器配置
    │      ├─ 解析源接口
    │      ├─ 解析目标接口
    │      └─ 解析转换规则
    │
    ├─ 3. 注册适配器
    │      └─ AdapterRegistry.register(adapter)
    │
    ├─ 4. 初始化适配器
    │      ├─ 创建适配器实例
    │      ├─ 加载转换规则
    │      └─ 启动适配器
    │
    └─ 5. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

---

### 3.5 driver 类型

**安装步骤**:

```
installDriver(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载驱动配置
    │      ├─ 解析驱动类型
    │      ├─ 解析协议配置
    │      └─ 解析连接参数
    │
    ├─ 3. 注册驱动
    │      └─ DriverRegistry.register(driver)
    │
    ├─ 4. 初始化驱动
    │      ├─ 创建驱动实例
    │      ├─ 初始化连接池
    │      └─ 启动驱动
    │
    └─ 5. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

---

### 3.6 service 类型

**安装步骤**:

```
installService(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载服务配置
    │      ├─ 解析服务接口
    │      ├─ 解析实现类
    │      └─ 解析启动参数
    │
    ├─ 3. 注册服务
    │      └─ ServiceRegistry.register(service)
    │
    ├─ 4. 启动服务
    │      ├─ 创建服务实例
    │      ├─ 注入依赖
    │      └─ 调用 start() 方法
    │
    └─ 5. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

---

### 3.7 library 类型

**安装步骤**:

```
installLibrary(skillId)
    │
    ├─ 1. 安装基础依赖
    │      └─ installDependencies(skillId)
    │
    ├─ 2. 加载库配置
    │      ├─ 解析导出类
    │      ├─ 解析版本信息
    │      └─ 解析兼容性
    │
    ├─ 3. 注册库
    │      └─ LibraryRegistry.register(library)
    │
    └─ 4. 记录安装状态
           └─ saveInstallStatus(skillId, "INSTALLED")
```

---

## 4. 依赖处理逻辑

### 4.1 依赖安装流程

```
installDependencies(skillId)
    │
    ├─ 1. 获取 Skill 包
    │      └─ registry.get(skillId) → SkillPackage
    │
    ├─ 2. 获取依赖列表
    │      └─ manifest.getDependencies() → List<Dependency>
    │
    ├─ 3. 遍历依赖
    │      │
    │      ├─ 检查是否已安装
    │      │      └─ isInstalled(depSkillId)
    │      │
    │      ├─ 已安装 → SKIPPED
    │      │      ├─ itemResult.setAction(SKIPPED)
    │      │      └─ result.incrementSkipped()
    │      │
    │      └─ 未安装 → 执行安装
    │              │
    │              ├─ 创建安装请求
    │              │      └─ InstallRequest
    │              │
    │              ├─ 执行安装
    │              │      └─ install(request) → InstallResult
    │              │
    │              ├─ 安装成功 → INSTALLED
    │              │      ├─ itemResult.setAction(INSTALLED)
    │              │      ├─ result.incrementInstalled()
    │              │      └─ 递归安装子依赖
    │              │              └─ installDependencies(depSkillId)
    │              │
    │              └─ 安装失败 → FAILED
    │                      ├─ itemResult.setAction(FAILED)
    │                      ├─ result.incrementFailed()
    │                      └─ 如果是 required 依赖，设置 result.success = false
    │
    └─ 4. 返回结果
           └─ DependencyResult
```

### 4.2 依赖类型

| 依赖属性 | 说明 | 处理逻辑 |
|---------|------|---------|
| **required** | 必需依赖 | 安装失败则整个安装失败 |
| **optional** | 可选依赖 | 安装失败只记录警告，继续安装 |
| **versionRange** | 版本范围 | 检查兼容性，不满足则报错 |

---

## 5. 安装状态管理

### 5.1 安装状态流转

```
                    ┌─────────────┐
                    │   PENDING   │
                    └──────┬──────┘
                           │
                           ▼
                    ┌─────────────┐
         ┌─────────│ INSTALLING  │─────────┐
         │         └──────┬──────┘         │
         │                │                │
         ▼                ▼                ▼
  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
  │  INSTALLED  │  │   FAILED    │  │   SKIPPED   │
  └─────────────┘  └──────┬──────┘  └─────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │   RETRY     │
                   └──────┬──────┘
                          │
                          └──────────────► INSTALLING
```

### 5.2 状态定义

| 状态 | 说明 |
|------|------|
| **PENDING** | 等待安装 |
| **INSTALLING** | 正在安装 |
| **INSTALLED** | 安装成功 |
| **FAILED** | 安装失败 |
| **SKIPPED** | 已存在，跳过安装 |
| **RETRY** | 重试安装 |

### 5.3 结果对象

```java
// 带依赖的安装结果
public class InstallResultWithDependencies {
    private String skillId;
    private boolean success;
    private String status;  // installed, failed, existing
    private List<String> installedDependencies;
    private List<String> failedDependencies;
    private List<String> existingDependencies;
    private String error;
    private long duration;
}

// 依赖处理结果
public class DependencyResult {
    private String skillId;
    private boolean success;
    private int totalCount;
    private int installedCount;
    private int skippedCount;
    private int failedCount;
    private List<DependencyItemResult> items;
    private String errorMessage;
}
```

---

## 6. 安装流程图

```
┌─────────────────────────────────────────────────────────────┐
│                    开始安装 Skill                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. 获取 Skill 元数据 (SkillManifest)                         │
│    - skillId, name, version, skillType                      │
│    - dependencies                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. 解析依赖列表                                              │
│    - 获取所有直接依赖                                        │
│    - 解析版本范围                                            │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. 递归安装依赖                                              │
│    - 检查是否已安装                                          │
│    - 未安装则递归调用 installWithDependencies               │
│    - 记录每个依赖的状态 (INSTALLED/SKIPPED/FAILED)          │
└──────────────────────┬──────────────────────────────────────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
    有依赖失败?                     所有依赖成功
         │                           │
         ▼                           ▼
┌─────────────────┐         ┌─────────────────────────────┐
│ 返回失败结果     │         │ 4. 根据 skillType 执行安装   │
│ (包含失败依赖)   │         │    - nexus-ui → 注册 UI      │
└─────────────────┘         │    - scene → 注册场景        │
                            │    - capability → 注册能力   │
                            │    - ...                     │
                            └─────────────┬───────────────┘
                                          │
                                          ▼
                            ┌─────────────────────────────┐
                            │ 5. 注册 Skill               │
                            │    - 添加到注册表            │
                            │    - 更新安装状态            │
                            └─────────────┬───────────────┘
                                          │
                                          ▼
                            ┌─────────────────────────────┐
                            │ 6. 返回安装结果              │
                            │    InstallResultWithDeps    │
                            └─────────────────────────────┘
```

---

**Made with ❤️ by Ooder Team**
