# Ooder SDK 重构执行计划 v0.8.0

## 执行摘要

基于对 v0.8.0 升级主计划的深入分析,以及 agent-sdk、scene-engine、ooder-common 三个核心工程的详细检查,本计划制定了系统性的重构方案,旨在:
- **删除50+完全冗余文件**(scene-engine drivers/)
- **合并重复类定义**(agent-sdk CmdClientConfig等)
- **分离业务逻辑与通用组件**(ooder-common)
- **对齐CAP驱动架构**

---

## 一、问题汇总

### 1. scene-engine (最严重 - 50+冗余文件)

| 问题类型 | 具体描述 | 影响文件数 |
|---------|---------|-----------|
| 完全重复代码 | drivers/ 与 skill-*/ 模块代码几乎相同 | 50+ |
| 重复类 | VfsSkill, OrgSkill, MsgSkill 等 | 15+ |
| 重复配置 | VfsSkillProperties, OrgSkillProperties 等 | 10+ |
| 重复服务 | VfsService, OrgService, MsgService 等 | 15+ |

**关键重复文件对:**
```
drivers/vfs/VfsSkill.java ↔ skill-vfs/src/.../VfsSkill.java (完全相同)
drivers/org/OrgSkill.java ↔ skill-org/src/.../OrgSkill.java (完全相同)
drivers/msg/MsgSkill.java ↔ skill-msg/src/.../MsgSkill.java (完全相同)
drivers/vfs/VfsSkillProperties.java ↔ skill-vfs/.../VfsSkillProperties.java
...
```

### 2. agent-sdk (中等 - 重复类定义)

| 问题类型 | 具体描述 | 影响类数 |
|---------|---------|---------|
| 重复配置类 | CmdClientConfig 在两个包中定义 | 2 |
| 重复配置类 | MsgClientConfig 在两个包中定义 | 2 |
| 重复接口 | Will/Story 相关接口多处定义 | 3+ |
| 重复工具 | VFS 实现与 scene-engine 重叠 | 5+ |

**重复类:**
```
net.ooder.sdk.api.cmd.CmdClientConfig
net.ooder.sdk.cmd.CmdClientConfig

net.ooder.sdk.api.msg.MsgClientConfig
net.ooder.sdk.msg.MsgClientConfig
```

### 3. ooder-common (结构性问题)

| 问题类型 | 具体描述 | 影响 |
|---------|---------|------|
| 业务逻辑污染 | 包含 IoT、VFS、Org 等业务实现 | 违反通用库原则 |
| 循环依赖风险 | 与 agent-sdk/scene-engine 相互依赖 | 架构混乱 |
| 职责不清 | 通用工具与业务逻辑混合 | 维护困难 |

**需要迁移的业务模块:**
```
ooder-common/iot/ → scene-engine/skill-iot/ 或独立模块
ooder-common/vfs/ → scene-engine/skill-vfs/
ooder-common/org/ → scene-engine/skill-org/
```

---

## 二、重构执行计划

### 阶段一: scene-engine 清理 (优先级:最高)

**目标:** 删除 drivers/ 目录下所有冗余文件,保留 skill-*/ 作为唯一实现

#### 步骤 1.1: 验证 drivers/ 与 skill-*/ 的完全等价性
```bash
# 对比 VfsSkill
diff drivers/vfs/src/main/java/.../VfsSkill.java skill-vfs/src/main/java/.../VfsSkill.java

# 对比 OrgSkill
diff drivers/org/src/main/java/.../OrgSkill.java skill-org/src/main/java/.../OrgSkill.java

# 对比 MsgSkill
diff drivers/msg/src/main/java/.../MsgSkill.java skill-msg/src/main/java/.../MsgSkill.java
```

#### 步骤 1.2: 检查 drivers/ 的引用情况
```bash
# 搜索整个SDK对 drivers/ 的引用
grep -r "drivers\." --include="*.java" .
grep -r "import.*drivers\." --include="*.java" .
```

#### 步骤 1.3: 删除冗余目录
```bash
# 删除整个 drivers/ 目录
rm -rf scene-engine/drivers/
```

**预计删除文件:** 50+ Java文件,10+ 配置文件

#### 步骤 1.4: 更新 pom.xml
- 移除 drivers/ 相关模块引用
- 确保 skill-*/ 模块依赖正确

---

### 阶段二: agent-sdk 合并 (优先级:高)

**目标:** 合并重复类定义,统一包结构

#### 步骤 2.1: 确定保留位置
根据 v0.8.0 CAP驱动架构,统一使用 `net.ooder.sdk.api.*` 包:

```
保留: net.ooder.sdk.api.cmd.CmdClientConfig
删除: net.ooder.sdk.cmd.CmdClientConfig

保留: net.ooder.sdk.api.msg.MsgClientConfig
删除: net.ooder.sdk.msg.MsgClientConfig
```

#### 步骤 2.2: 更新引用
```bash
# 查找所有使用旧包的文件
grep -r "import net\.ooder\.sdk\.cmd\." --include="*.java" .
grep -r "import net\.ooder\.sdk\.msg\." --include="*.java" .

# 批量替换为 api.cmd / api.msg
```

#### 步骤 2.3: 删除旧包
```bash
rm -rf agent-sdk/src/main/java/net/ooder/sdk/cmd/
rm -rf agent-sdk/src/main/java/net/ooder/sdk/msg/
```

#### 步骤 2.4: 验证编译
```bash
cd agent-sdk
mvn clean compile
```

---

### 阶段三: ooder-common 重构 (优先级:高)

**目标:** 分离业务逻辑,保留纯通用组件

#### 步骤 3.1: 创建新的模块结构
```
ooder-common/
├── ooder-common-core/          # 核心工具类
│   ├── utils/
│   ├── codec/
│   ├── crypto/
│   └── lang/
├── ooder-common-cache/         # 缓存抽象
├── ooder-common-database/      # 数据库工具
└── ooder-common-api/           # 通用API定义
```

#### 步骤 3.2: 迁移业务模块
```bash
# 将业务相关代码迁移到 scene-engine
mv ooder-common/iot/ scene-engine/skill-iot/
mv ooder-common/vfs/ scene-engine/skill-vfs/
mv ooder-common/org/ scene-engine/skill-org/
mv ooder-common/msg/ scene-engine/skill-msg/
```

#### 步骤 3.3: 更新依赖关系
- ooder-common 不再依赖任何业务模块
- scene-engine skill-*/ 依赖 ooder-common-core

---

### 阶段四: CAP驱动架构对齐 (优先级:中)

**目标:** 实现 v0.8.0 计划中的 CAP注册表和 SceneAgent

#### 步骤 4.1: 创建 CAP注册表
```java
// 新文件: agent-sdk/src/main/java/net/ooder/sdk/api/cap/CapRegistry.java
public interface CapRegistry {
    void register(Capability cap);
    void unregister(String capId);
    Capability findByAddress(CapAddress address);
    List<Capability> findByDomain(String domainId);
}
```

#### 步骤 4.2: 实现 SceneAgent
```java
// 新文件: agent-sdk/src/main/java/net/ooder/sdk/api/agent/SceneAgent.java
public class SceneAgent implements EndAgent {
    private CapRegistry capRegistry;
    private SceneContext context;
    // ...
}
```

#### 步骤 4.3: 扩展 CommandPacket
```java
// 修改: CommandPacket.java
public class CommandPacket {
    // 现有字段...
    
    // 新增LLM相关字段
    private String llmIntent;           // LLM意图描述
    private String reasoningChain;      // 推理链
    private String a2aContext;          // A2A上下文
    private int contextLevel;           // 上下文级别
}
```

---

## 三、执行顺序

```
第1周: scene-engine 清理
  ├── Day 1-2: 验证等价性,检查引用
  ├── Day 3-4: 删除 drivers/ 目录
  └── Day 5: 验证编译,更新文档

第2周: agent-sdk 合并
  ├── Day 1-2: 更新引用,合并重复类
  ├── Day 3-4: 删除旧包,验证编译
  └── Day 5: 单元测试

第3周: ooder-common 重构
  ├── Day 1-2: 创建新模块结构
  ├── Day 3-4: 迁移业务模块
  └── Day 5: 更新依赖,验证编译

第4周: CAP架构对齐
  ├── Day 1-2: 实现 CAPRegistry
  ├── Day 3-4: 实现 SceneAgent
  └── Day 5: 扩展 CommandPacket
```

---

## 四、风险评估

| 风险 | 可能性 | 影响 | 缓解措施 |
|-----|-------|------|---------|
| 删除 drivers/ 后编译失败 | 中 | 高 | 提前完整检查引用,保留备份 |
| 合并类后运行时错误 | 低 | 高 | 保持API完全兼容,单元测试 |
| 迁移业务模块破坏依赖 | 中 | 中 | 逐步迁移,每次验证编译 |
| 新架构与旧代码不兼容 | 中 | 高 | 保持向后兼容,渐进式迁移 |

---

## 五、验证清单

### 编译验证
- [ ] scene-engine 编译通过
- [ ] agent-sdk 编译通过
- [ ] ooder-common 编译通过
- [ ] llm-sdk 编译通过
- [ ] 整体项目编译通过

### 功能验证
- [ ] 单元测试全部通过
- [ ] 集成测试通过
- [ ] 示例程序运行正常

### 架构验证
- [ ] 无重复类定义
- [ ] 无循环依赖
- [ ] 业务逻辑与通用组件分离
- [ ] CAP驱动架构实现

---

## 六、立即执行操作

现在开始执行**阶段一: scene-engine 清理**
