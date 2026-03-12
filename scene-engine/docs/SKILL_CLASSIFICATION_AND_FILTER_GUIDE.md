# 技能分类与过滤规则指南

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **维护团队**: Engine Team  
> **适用对象**: Skills Team, 前端开发团队

---

## 一、分类体系设计

### 1.1 三维分类模型

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能三维分类模型                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  维度1: SkillForm (技能形态)                                                  │
│  ├── STANDALONE  - 独立技能 (非场景技能，单一功能)                            │
│  └── SCENE       - 场景技能 (复杂业务场景，多能力组合)                        │
│                                                                             │
│  维度2: SceneType (场景类型) - 仅 SkillForm=SCENE 时有效                       │
│  ├── AUTO        - 自驱场景 (自动运行，hasSelfDrive=true)                     │
│  └── TRIGGER     - 触发场景 (需要触发，hasSelfDrive=false)                    │
│                                                                             │
│  维度3: visibility (可见性)                                                   │
│  ├── public      - 公开可见 (用户可发现、可激活)                              │
│  └── internal    - 内部使用 (后台运行，用户不可见)                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 为什么这样分？

| 维度 | 设计理由 | 解决的问题 |
|------|----------|-----------|
| **SkillForm** | 区分技能复杂度 | 独立技能简单直接，场景技能需要配置和激活 |
| **SceneType** | 区分运行模式 | 自驱场景自动运行，触发场景需要用户参与 |
| **visibility** | 区分用户权限 | 公开技能用户可见，内部技能后台静默运行 |

---

## 二、普通用户视角

### 2.1 普通用户看到的分类

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     普通用户技能市场                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【分类筛选】                                                                │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐              │
│  │ 全部    │ │ AI智能  │ │ 办公协作│ │ 数据处理│ │ 系统工具│              │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘              │
│                                                                             │
│  【运行模式】                                                                │
│  ┌─────────┐ ┌─────────┐                                                   │
│  │ 自动运行│ │ 手动触发│                                                   │
│  └─────────┘ └─────────┘                                                   │
│                                                                             │
│  【技能列表】                                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ [图标] 知识问答助手          [AI智能] [自动运行]  [安装]            │   │
│  │        基于知识库的智能问答系统...                                   │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 日志汇报场景          [办公协作] [手动触发] [安装]            │   │
│  │        团队协作的日志汇报系统...                                     │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 智能客服助手          [AI智能] [自动运行]  [安装]            │   │
│  │        7×24小时自动回复客户咨询...                                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 普通用户过滤规则

```java
// 普通用户可见的技能过滤条件
public class UserSkillFilter {
    
    /**
     * 普通用户可见的技能
     */
    public static boolean isVisibleToUser(Skill skill) {
        // 规则1: 必须是公开可见
        if (!"public".equals(skill.getVisibility())) {
            return false;
        }
        
        // 规则2: 必须是已发布状态
        if (!"PUBLISHED".equals(skill.getStatus())) {
            return false;
        }
        
        // 规则3: 用户有权限访问
        if (!hasPermission(skill)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 用户可安装的技能
     */
    public static boolean isInstallableByUser(Skill skill, User user) {
        // 基础可见性检查
        if (!isVisibleToUser(skill)) {
            return false;
        }
        
        // 规则4: 依赖的能力地址可用
        for (Integer address : skill.getRequiredAddresses()) {
            if (!isAddressAvailable(address)) {
                return false;
            }
        }
        
        // 规则5: 满足前置条件
        if (!meetsPrerequisites(skill, user)) {
            return false;
        }
        
        return true;
    }
}
```

### 2.3 普通用户分类展示

| 展示分类 | 包含内容 | 过滤条件 |
|----------|----------|----------|
| **AI智能** | LLM相关技能 | category=ai OR hasCapability(LLM) |
| **办公协作** | 多人协作场景 | sceneType=TRIGGER AND hasRole(MANAGER) |
| **数据处理** | 数据相关技能 | hasCapability(DB) OR hasCapability(SEARCH) |
| **系统工具** | 系统管理技能 | category=infrastructure |
| **自动运行** | 自驱场景 | sceneType=AUTO |
| **手动触发** | 触发场景 | sceneType=TRIGGER |

---

## 三、管理员视角

### 3.1 管理员看到的分类

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     管理员技能管理界面                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【全部技能】                                                                │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐              │
│  │ 全部    │ │ 公开    │ │ 内部    │ │ 场景技能│ │ 独立技能│              │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘              │
│                                                                             │
│  【系统状态】                                                                │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                          │
│  │ 已发布  │ │ 待审核  │ │ 已禁用  │ │ 系统内置│                          │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                          │
│                                                                             │
│  【能力依赖】                                                                │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐                          │
│  │ LLM     │ │ 数据库  │ │ 知识库  │ │ 组织服务│                          │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘                          │
│                                                                             │
│  【技能列表 - 管理员视图】                                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ [图标] 系统监控服务    [internal] [系统内置] [已启用]  [管理]        │   │
│  │        系统监控和告警服务，后台自动运行...                           │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 知识问答助手    [public]   [AI智能]   [已发布]  [管理]        │   │
│  │        基于知识库的智能问答系统...                                   │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ [图标] 日志汇报场景    [public]   [待审核]   [未发布]  [审核]        │   │
│  │        团队协作的日志汇报系统...                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 管理员过滤规则

```java
// 管理员技能过滤
public class AdminSkillFilter {
    
    /**
     * 管理员可见的所有技能（包括内部）
     */
    public static boolean isVisibleToAdmin(Skill skill) {
        // 管理员可以看到所有技能，包括internal
        // 只需要检查基本权限
        return hasAdminPermission();
    }
    
    /**
     * 按可见性筛选
     */
    public static boolean filterByVisibility(Skill skill, String visibility) {
        return visibility.equals(skill.getVisibility());
    }
    
    /**
     * 按形态筛选
     */
    public static boolean filterByForm(Skill skill, SkillForm form) {
        return form.equals(skill.getSkillForm());
    }
    
    /**
     * 按场景类型筛选
     */
    public static boolean filterBySceneType(Skill skill, SceneType sceneType) {
        if (skill.getSkillForm() != SkillForm.SCENE) {
            return false;
        }
        return sceneType.equals(skill.getSceneType());
    }
    
    /**
     * 按能力地址依赖筛选
     */
    public static boolean filterByCapability(Skill skill, Integer address) {
        return skill.getRequiredAddresses().contains(address) ||
               skill.getOptionalAddresses().contains(address);
    }
    
    /**
     * 按状态筛选
     */
    public static boolean filterByStatus(Skill skill, SkillStatus status) {
        return status.equals(skill.getStatus());
    }
}
```

### 3.3 管理员专属功能

| 功能 | 说明 | 权限要求 |
|------|------|----------|
| **审核技能** | 审核待发布的技能 | admin |
| **禁用技能** | 禁用有问题的技能 | admin |
| **配置系统内置** | 设置系统内置技能 | super-admin |
| **查看内部技能** | 查看后台运行技能 | admin |
| **管理能力地址** | 配置能力地址绑定 | admin |
| **查看统计报表** | 查看技能使用统计 | admin |

---

## 四、安装规则

### 4.1 通用安装规则

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能安装规则                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  规则1: 可见性检查                                                           │
│  ├── public技能: 所有用户可见                                                │
│  └── internal技能: 仅管理员可见                                              │
│                                                                             │
│  规则2: 能力地址可用性检查                                                    │
│  ├── 必需地址: 必须全部可用，否则无法安装                                     │
│  └── 可选地址: 部分可用即可，缺失时提示                                       │
│                                                                             │
│  规则3: 前置条件检查                                                         │
│  ├── 依赖技能: 必须先安装依赖技能                                            │
│  ├── 系统版本: 检查SE版本兼容性                                              │
│  └── 资源要求: 检查CPU/内存/存储资源                                          │
│                                                                             │
│  规则4: 权限检查                                                             │
│  ├── 普通用户: 只能安装public技能                                            │
│  └── 管理员: 可以安装所有技能                                                │
│                                                                             │
│  规则5: 安装后行为                                                           │
│  ├── AUTO + public: 安装后等待用户确认激活                                    │
│  ├── AUTO + internal: 安装后自动激活运行                                      │
│  └── TRIGGER + public: 安装后等待触发条件                                     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 不同角色的安装权限

| 角色 | public技能 | internal技能 | 系统内置技能 |
|------|:----------:|:------------:|:------------:|
| **普通用户** | ✅ 可安装 | ❌ 不可见 | ❌ 不可见 |
| **管理员** | ✅ 可安装 | ✅ 可安装 | ✅ 可配置 |
| **系统管理员** | ✅ 可安装 | ✅ 可安装 | ✅ 可安装/卸载 |

### 4.3 安装流程差异

```yaml
# AUTO + public 安装流程
install-auto-public:
  steps:
    - 用户点击安装
    - 系统检查依赖
    - 系统安装依赖技能
    - 系统创建场景实例
    - 系统提示"等待激活"
    - 用户进入激活向导
    - 用户完成激活配置
    - 系统激活并生成菜单

# AUTO + internal 安装流程
install-auto-internal:
  steps:
    - 管理员点击安装
    - 系统检查依赖
    - 系统自动安装依赖
    - 系统自动创建实例
    - 系统自动激活运行
    - 后台静默运行，无用户界面

# TRIGGER + public 安装流程
install-trigger-public:
  steps:
    - 用户点击安装
    - 系统检查依赖
    - 系统安装依赖技能
    - 系统创建场景实例
    - 系统提示"等待触发"
    - 用户等待触发条件
    - 触发后进入激活流程
```

---

## 五、过滤规则总结

### 5.1 前端过滤规则

```javascript
// 技能市场过滤规则
const skillFilters = {
  // 普通用户默认过滤
  userDefault: {
    visibility: 'public',
    status: 'PUBLISHED',
    skillForm: ['STANDALONE', 'SCENE']
  },
  
  // 管理员默认过滤（显示全部）
  adminDefault: {
    // 不过滤，显示所有
  },
  
  // 分类过滤
  byCategory: (skills, category) => {
    return skills.filter(s => s.category === category);
  },
  
  // 运行模式过滤
  bySceneType: (skills, sceneType) => {
    return skills.filter(s => 
      s.skillForm === 'SCENE' && s.sceneType === sceneType
    );
  },
  
  // 能力依赖过滤
  byCapability: (skills, address) => {
    return skills.filter(s => 
      s.requiredAddresses.includes(address) ||
      s.optionalAddresses.includes(address)
    );
  },
  
  // 可安装过滤
  installable: (skills, user) => {
    return skills.filter(s => {
      // 可见性
      if (s.visibility !== 'public' && !user.isAdmin) return false;
      // 状态
      if (s.status !== 'PUBLISHED') return false;
      // 依赖可用
      return s.requiredAddresses.every(addr => isAddressAvailable(addr));
    });
  }
};
```

### 5.2 后端过滤规则

```java
// SkillDiscoveryService.java
@Service
public class SkillDiscoveryService {
    
    /**
     * 发现用户可见的技能
     */
    public List<Skill> discoverForUser(User user) {
        return skillRepository.findAll().stream()
            .filter(this::isPublished)
            .filter(skill -> isVisibleToUser(skill, user))
            .filter(skill -> hasRequiredCapabilities(skill))
            .collect(Collectors.toList());
    }
    
    /**
     * 发现管理员可见的技能
     */
    public List<Skill> discoverForAdmin(User admin) {
        if (!admin.isAdmin()) {
            throw new UnauthorizedException();
        }
        return skillRepository.findAll();
    }
    
    private boolean isVisibleToUser(Skill skill, User user) {
        // public技能对所有用户可见
        if ("public".equals(skill.getVisibility())) {
            return true;
        }
        // internal技能仅管理员可见
        return user.isAdmin();
    }
    
    private boolean hasRequiredCapabilities(Skill skill) {
        return skill.getRequiredAddresses().stream()
            .allMatch(this::isCapabilityAvailable);
    }
}
```

---

## 六、实施建议

### 6.1 对 Skills Team 的建议

1. **技能配置规范**
   ```yaml
   # skill.yaml 必须包含
   metadata:
     skillForm: SCENE          # 或 STANDALONE
     sceneType: AUTO           # 或 TRIGGER (仅SCENE)
     visibility: public        # 或 internal
     category: ai              # 业务分类
   
   spec:
     capabilityAddresses:
       required: [...]         # 必需能力地址
       optional: [...]         # 可选能力地址
   ```

2. **分类选择建议**
   - **AI智能**: LLM相关技能，使用 AUTO 模式
   - **办公协作**: 多人协作场景，使用 TRIGGER 模式
   - **数据处理**: 数据处理技能，使用 AUTO 模式
   - **系统工具**: 系统管理技能，使用 internal 可见性

### 6.2 对前端团队的建议

1. **普通用户界面**
   - 默认只显示 public + PUBLISHED 技能
   - 提供分类筛选和运行模式筛选
   - 不可安装的技能显示"依赖缺失"提示

2. **管理员界面**
   - 显示所有技能，包括 internal
   - 提供状态筛选（已发布/待审核/已禁用）
   - 提供能力地址依赖筛选
   - 显示系统内置技能

### 6.3 对 Engine Team 的建议

1. **接口实现**
   - 实现 `SkillDiscoveryService` 过滤逻辑
   - 实现权限检查拦截器
   - 实现能力地址可用性检查

2. **数据初始化**
   - 系统内置技能标记为 internal
   - 预置必需的能力地址驱动

---

**文档状态**: 规范定义  
**下一步**: 各团队按规范实施
