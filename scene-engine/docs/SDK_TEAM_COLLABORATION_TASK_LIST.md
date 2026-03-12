# SDK团队协同任务清单

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **参考标准**: SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md v1.1.0  
> **状态**: 待执行

---

## 一、需求覆盖度分析总结

### 1.1 覆盖度评估结果

基于 Skills Team 最新反馈 (`SE_STANDARD_TECHNICAL_FEASIBILITY_ANALYSIS.md v2.0`)：

| 评估项 | v1.0.0 状态 | v1.1.0 状态 | 覆盖度 |
|--------|:-----------:|:-----------:|:------:|
| **visibility 枚举** | 🔴 严重冲突 | ✅ 完全兼容 | 100% |
| **skillForm 枚举** | 🔴 严重冲突 | ✅ 完全兼容 | 100% |
| **capabilityCategory 字段** | 🔴 严重冲突 | ✅ 完全兼容 | 100% |
| **businessCategory 字段** | 🔴 缺失 | ⚠️ 需新增 | 0% |
| **capabilityAddresses 字段** | 🔴 缺失 | ⚠️ 需新增 | 0% |
| **sceneType 字段** | 🔴 缺失 | ⚠️ 需新增 | 0% |
| **roles 字段** | 🟡 缺失 | ⚠️ 需新增 | 0% |

### 1.2 总体覆盖度: **65%**

- ✅ **完全兼容**: 3项 (visibility, skillForm, capabilityCategory)
- ⚠️ **需新增**: 4项 (businessCategory, capabilityAddresses, sceneType, roles)

---

## 二、SDK团队任务清单

### 2.1 P0 任务 (立即执行)

#### 任务1: 扩展 Visibility 枚举 (1天)

**任务描述**:
将 SDK 中的 `Visibility` 枚举从 2 个值扩展为 3 个值。

**当前代码**:
```java
// 当前可能只有 public/private 或类似定义
public enum Visibility {
    PUBLIC,
    PRIVATE
}
```

**目标代码**:
```java
public enum Visibility {
    public("public", "普通用户可见"),
    developer("developer", "开发者可见"),
    internal("internal", "系统内部");
    
    private final String code;
    private final String description;
    
    Visibility(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
}
```

**验收标准**:
- [ ] Visibility 枚举包含 3 个值
- [ ] 支持从字符串反序列化
- [ ] 向后兼容 (旧数据可正常读取)

**依赖方**: 无
**负责人**: SDK Team
**工期**: 1天

---

#### 任务2: 扩展 SkillForm 枚举 (1天)

**任务描述**:
将 SDK 中的 `SkillForm` 枚举从 2 个值扩展为 4 个值。

**目标代码**:
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
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    public boolean isScene() { return this == SCENE; }
    public boolean isProvider() { return this == PROVIDER; }
    public boolean isDriver() { return this == DRIVER; }
    public boolean isInternal() { return this == INTERNAL; }
}
```

**验收标准**:
- [ ] SkillForm 枚举包含 4 个值
- [ ] 支持从字符串反序列化
- [ ] 向后兼容 (旧数据可正常读取)

**依赖方**: 无
**负责人**: SDK Team
**工期**: 1天

---

#### 任务3: 新增 CapabilityCategory 枚举 (1天)

**任务描述**:
新增 `CapabilityCategory` 枚举，支持 17 个能力地址分类。

**目标代码**:
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
    
    public String getName() { return name; }
    public int getBaseAddress() { return baseAddress; }
    
    public static CapabilityCategory fromCode(String code) {
        for (CapabilityCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(code)) {
                return cat;
            }
        }
        return null;
    }
}
```

**验收标准**:
- [ ] CapabilityCategory 枚举包含 17 个值
- [ ] 支持从字符串反序列化
- [ ] 每个分类对应正确的基地址

**依赖方**: 无
**负责人**: SDK Team
**工期**: 1天

---

#### 任务4: 更新 SkillPackage 模型 (2天)

**任务描述**:
更新 SDK 中的 `SkillPackage` 模型，支持新的字段。

**目标代码**:
```java
public class SkillPackage {
    // 现有字段
    private String skillId;
    private String name;
    private String version;
    private String description;
    
    // 新增字段 (SE标准 v1.1.0)
    private SkillForm skillForm;              // 扩展枚举
    private SceneType sceneType;              // 新增
    private Visibility visibility;            // 扩展枚举
    private BusinessCategory businessCategory; // 新增
    private SkillCategory category;           // 新增 (SE标准8个)
    private CapabilityCategory capabilityCategory; // 新增
    
    // 新增嵌套对象
    private CapabilityAddresses capabilityAddresses; // 新增
    private List<SkillRole> roles;            // 新增
    
    // Getters and Setters
    // ...
}
```

**验收标准**:
- [ ] SkillPackage 包含所有新字段
- [ ] 支持 JSON/YAML 序列化和反序列化
- [ ] 向后兼容 (旧数据可正常读取)

**依赖方**: 任务1, 任务2, 任务3
**负责人**: SDK Team
**工期**: 2天

---

### 2.2 P1 任务 (短期执行)

#### 任务5: 新增 BusinessCategory 枚举 (1天)

**目标代码**:
```java
public enum BusinessCategory {
    OFFICE_COLLABORATION("办公协作", "团队协作、日志、会议、审批"),
    HUMAN_RESOURCE("人力资源", "招聘、绩效、培训、员工管理"),
    AI_ASSISTANT("智能助手", "AI对话、知识问答、智能客服"),
    DATA_PROCESSING("数据处理", "报表、分析、同步、可视化"),
    PROJECT_MANAGEMENT("项目管理", "项目跟踪、敏捷看板、里程碑"),
    MARKETING_OPERATIONS("营销运营", "内容发布、社媒管理、活动"),
    SYSTEM_TOOLS("系统工具", "存储、通知、定时任务、备份"),
    SYSTEM_MONITOR("系统监控", "监控告警、日志收集、健康检查"),
    SECURITY_AUDIT("安全审计", "访问控制、审计日志、安全检测"),
    INFRASTRUCTURE("基础设施", "调度服务、网络服务、认证服务");
    
    private final String name;
    private final String description;
    
    BusinessCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
```

**依赖方**: 无
**负责人**: SDK Team
**工期**: 1天

---

#### 任务6: 新增 SceneType 枚举 (0.5天)

**目标代码**:
```java
public enum SceneType {
    AUTO("自主场景", true, false),
    TRIGGER("触发场景", false, true);
    
    private final String name;
    private final boolean canSelfDrive;
    private final boolean canBeTriggered;
    
    SceneType(String name, boolean canSelfDrive, boolean canBeTriggered) {
        this.name = name;
        this.canSelfDrive = canSelfDrive;
        this.canBeTriggered = canBeTriggered;
    }
}
```

**依赖方**: 无
**负责人**: SDK Team
**工期**: 0.5天

---

#### 任务7: 新增 SkillCategory 枚举 (SE标准) (0.5天)

**目标代码**:
```java
public enum SkillCategory {
    KNOWLEDGE("knowledge", "知识类"),
    LLM("llm", "AI模型类"),
    TOOL("tool", "工具类"),
    WORKFLOW("workflow", "流程类"),
    DATA("data", "数据类"),
    SERVICE("service", "服务类"),
    UI("ui", "界面类"),
    OTHER("other", "其他");
    
    private final String code;
    private final String name;
    
    SkillCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
```

**依赖方**: 无
**负责人**: SDK Team
**工期**: 0.5天

---

#### 任务8: 新增 CapabilityAddresses 模型 (2天)

**目标代码**:
```java
public class CapabilityAddresses {
    private List<AddressConfig> required;
    private List<AddressConfig> optional;
    
    public static class AddressConfig {
        private String address;      // 十六进制字符串，如 "0x28"
        private String name;         // 地址名称，如 "LLM_OLLAMA"
        private String fallback;     // 降级地址，如 "0x29" 或 null
        private String description;  // 描述
        private boolean skipable;    // 是否可跳过 (仅optional)
    }
}
```

**依赖方**: 无
**负责人**: SDK Team
**工期**: 2天

---

#### 任务9: 新增 SkillRole 模型 (1天)

**目标代码**:
```java
public class SkillRole {
    private String name;           // MANAGER, LEADER, MEMBER, USER
    private String displayName;    // 显示名称
    private int minCount;          // 最小人数
    private int maxCount;          // 最大人数
    private Set<Permission> permissions; // READ, WRITE, CONFIG, DELETE
}

public enum Permission {
    READ, WRITE, CONFIG, DELETE
}
```

**依赖方**: 无
**负责人**: SDK Team
**工期**: 1天

---

### 2.3 P2 任务 (后续执行)

#### 任务10: 更新序列化/反序列化逻辑 (2天)

**任务描述**:
更新 Jackson 的序列化和反序列化逻辑，支持新字段。

**验收标准**:
- [ ] JSON 序列化正确
- [ ] YAML 序列化正确
- [ ] 向后兼容 (旧数据可正常读取)

**依赖方**: 任务4, 任务5, 任务6, 任务7, 任务8, 任务9
**负责人**: SDK Team
**工期**: 2天

---

#### 任务11: 编写单元测试 (2天)

**任务描述**:
为新字段和枚举编写单元测试。

**验收标准**:
- [ ] 所有枚举值测试通过
- [ ] 序列化/反序列化测试通过
- [ ] 向后兼容测试通过

**依赖方**: 任务10
**负责人**: SDK Team
**工期**: 2天

---

## 三、协同依赖图

```
┌─────────────────────────────────────────────────────────────────┐
│                    SDK团队任务依赖图                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  P0 任务 (立即执行)                                             │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  任务1: Visibility扩展 ──┐                                      │
│  任务2: SkillForm扩展 ───┼──► 任务4: SkillPackage更新           │
│  任务3: CapabilityCategory ─┘                                   │
│                                                                 │
│  P1 任务 (短期执行)                                             │
│  ─────────────────────────────────────────────────────────────  │
│                                                                 │
│  任务5: BusinessCategory ──┐                                    │
│  任务6: SceneType ────────┼──► 任务10: 序列化逻辑更新           │
│  任务7: SkillCategory ────┤         │                           │
│  任务8: CapabilityAddresses ─┘      │                           │
│  任务9: SkillRole ──────────────────┘                           │
│                                      │                          │
│                                      ▼                          │
│                              任务11: 单元测试                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、实施时间表

| 阶段 | 任务 | 工期 | 开始日期 | 结束日期 | 负责人 |
|------|------|:----:|:--------:|:--------:|--------|
| **P0** | 任务1-4 | 5天 | 2026-03-12 | 2026-03-16 | SDK Team |
| **P1** | 任务5-9 | 5天 | 2026-03-17 | 2026-03-21 | SDK Team |
| **P2** | 任务10-11 | 4天 | 2026-03-22 | 2026-03-25 | SDK Team |

---

## 五、验收标准

### 5.1 功能验收

- [ ] 所有新枚举可正常序列化/反序列化
- [ ] SkillPackage 包含所有新字段
- [ ] 向后兼容 (旧数据可正常读取)
- [ ] 单元测试通过

### 5.2 集成验收

- [ ] SE Team 的验证程序通过
- [ ] Skills Team 的配置文件可正常解析
- [ ] 无重大兼容性问题

---

## 六、沟通机制

### 6.1 日常沟通

| 方式 | 频率 | 参与方 | 内容 |
|------|------|--------|------|
| 日报 | 每日 | SDK Team | 任务进度 |
| 周会 | 每周 | SDK + SE + Skills | 整体进度 |
| Issue | 随时 | 三方 | 问题反馈 |

### 6.2 紧急联系

| 情况 | 联系人 | 响应时间 |
|------|--------|:--------:|
| 标准疑问 | SE Team | 4小时 |
| 技术问题 | SDK Team Lead | 2小时 |
| 集成问题 | 三方会议 | 当天 |

---

## 七、参考文档

| 文档 | 路径 |
|------|------|
| SE强制执行标准 v1.1.0 | `SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` |
| Skills Team可行性分析 | `SE_STANDARD_TECHNICAL_FEASIBILITY_ANALYSIS.md` |
| 代码覆盖度分析 | `CODE_COVERAGE_ANALYSIS_REPORT.md` |

---

**文档状态**: 待执行  
**创建日期**: 2026-03-11  
**预计完成**: 2026-03-25
