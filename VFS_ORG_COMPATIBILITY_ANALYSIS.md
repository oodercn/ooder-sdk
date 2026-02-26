# VFS/Org Skill 兼容性分析与实现方案

**日期**: 2026-02-24  
**版本**: 2.3  
**状态**: 分析完成

---

## 一、现状分析

### 1.1 当前架构层次

```
┌─────────────────────────────────────────────────────────────────┐
│                    应用层 (org-web / vfs-web)                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  org-web    │  │  vfs-web    │  │    其他应用              │ │
│  │  (已部署)   │  │  (已部署)   │  │                         │ │
│  └──────┬──────┘  └──────┬──────┘  └─────────────────────────┘ │
└─────────┼────────────────┼──────────────────────────────────────┘
          │                │
          ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ooder-common (业务接口层)                      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  OrgManager (800+ API)                                  │   │
│  │  FileAdapter / AbstractFileAdapter                      │   │
│  │  OrgDataSourceProvider / DataSourceType                 │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    scene-engine/skill-* (新架构)                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  skill-org  │  │  skill-vfs  │  │    skill-ai (新增)       │ │
│  │  (接口定义)  │  │  (接口定义)  │  │                         │ │
│  └──────┬──────┘  └──────┬──────┘  └─────────────────────────┘ │
└─────────┼────────────────┼──────────────────────────────────────┘
          │                │
          ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ooder-skills (具体实现)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────┐│
│  │skill-org-   │  │skill-vfs-   │  │skill-vfs-   │  │skill-vfs││
│  │dingding     │  │local        │  │minio        │  │-oss/s3  ││
│  │feishu       │  │             │  │             │  │         ││
│  │wecom/ldap   │  │             │  │             │  │         ││
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 能力对比矩阵

#### Org 能力对比

| 能力 | org-web | skill-org (接口) | skill-org-* (实现) | 差异说明 |
|------|---------|------------------|-------------------|----------|
| 用户登录 | ✅ | ✅ | ✅ (OAuth) | 无差异 |
| 用户管理 | ✅ | ✅ | ⚠️ (部分支持) | 钉钉/飞书有限制 |
| 组织树查询 | ✅ | ✅ | ✅ | 无差异 |
| 部门管理 | ✅ | ✅ | ⚠️ (只读/受限) | 外部系统限制写入 |
| 角色管理 | ✅ | ✅ | ❌ (不支持) | 需回退到JSON |
| 职级管理 | ✅ | ✅ | ❌ (不支持) | 需回退到JSON |
| 用户组 | ✅ | ✅ | ❌ (不支持) | 需回退到JSON |
| 数据同步 | ✅ | ✅ | ✅ | 无差异 |

#### VFS 能力对比

| 能力 | vfs-web | skill-vfs (接口) | skill-vfs-* (实现) | 差异说明 |
|------|---------|------------------|-------------------|----------|
| 文件上传 | ✅ | ✅ | ✅ | 无差异 |
| 文件下载 | ✅ | ✅ | ✅ | 无差异 |
| 文件夹管理 | ✅ | ✅ | ✅ | 无差异 |
| 文件版本 | ✅ | ✅ | ⚠️ (部分支持) | local实现简单 |
| 文件分享 | ✅ | ✅ | ⚠️ (部分支持) | 依赖存储后端 |
| 存储适配器 | ✅ | ✅ | ✅ | 无差异 |
| 权限控制 | ✅ | ✅ | ⚠️ (需配置) | 需额外配置 |

---

## 二、兼容性问题识别

### 2.1 主要问题

1. **接口粒度不匹配**
   - `OrgManager` 有 800+ API,而 `OrgSkill` 只有核心方法
   - `FileAdapter` 是底层适配器,`VfsSkill` 是高层接口

2. **数据模型差异**
   - `org-web` 使用 `Org`, `Person`, `Role` 等 domain 对象
   - `skill-org` 使用 `UserInfo`, `OrgInfo` 等简化对象

3. **能力回退机制**
   - 钉钉/飞书等第三方不支持某些能力(如角色管理)
   - 需要明确的 fallback 策略

4. **配置方式不同**
   - `org-web` 使用 XML/数据库配置
   - `skill-org-*` 使用 YAML/环境变量配置

### 2.2 风险评估

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| API 不兼容 | 高 | 中 | 提供适配层 |
| 数据丢失 | 高 | 低 | 双写机制 |
| 性能下降 | 中 | 中 | 缓存优化 |
| 配置迁移 | 中 | 高 | 配置转换工具 |

---

## 三、实现方案

### 3.1 方案概述

采用**适配器模式** + **桥接层**实现平滑迁移:

```
应用层 (org-web / vfs-web)
         │
         ▼
┌─────────────────────────────────────┐
│  桥接层 (Bridge Layer)              │
│  - OrgManagerBridge                 │
│  - FileAdapterBridge                │
│  (保持原有API,内部调用skill接口)      │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  适配层 (Adapter Layer)             │
│  - OrgSkillAdapter                  │
│  - VfsSkillAdapter                  │
│  (数据模型转换,能力映射)              │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  scene-engine/skill-org/skill-vfs   │
│  (标准接口)                          │
└─────────────────────────────────────┘
```

### 3.2 具体实现

#### 3.2.1 Org 桥接实现

**1. 创建 OrgManagerBridge**

```java
package net.ooder.org.bridge;

/**
 * OrgManager 桥接实现
 * 
 * 保持与原有 OrgManager 相同的 API,
 * 内部通过 OrgSkill 调用具体实现
 */
public class OrgManagerBridge implements OrgManager {
    
    private final OrgSkill orgSkill;
    private final FallbackManager fallbackManager;
    
    public OrgManagerBridge(OrgSkill orgSkill, FallbackManager fallbackManager) {
        this.orgSkill = orgSkill;
        this.fallbackManager = fallbackManager;
    }
    
    @Override
    public Person getPerson(String personId) {
        // 转换调用
        UserInfo userInfo = orgSkill.getUser(personId);
        return convertToPerson(userInfo);
    }
    
    @Override
    public List<Role> getPersonRoles(String personId) {
        // 检查能力支持
        if (!orgSkill.isSupport("person-role")) {
            // 使用回退机制
            return fallbackManager.getRoles(personId);
        }
        return orgSkill.getUserRoles(personId).stream()
            .map(this::convertToRole)
            .collect(Collectors.toList());
    }
    
    @Override
    public Org getOrg(String orgId) {
        OrgInfo orgInfo = orgSkill.getOrg(orgId);
        return convertToOrg(orgInfo);
    }
    
    // ... 其他方法类似实现
    
    private Person convertToPerson(UserInfo userInfo) {
        // 数据模型转换
        Person person = new Person();
        person.setId(userInfo.getUserId());
        person.setName(userInfo.getUsername());
        person.setAccount(userInfo.getAccount());
        // ...
        return person;
    }
}
```

**2. 创建 FallbackManager**

```java
package net.ooder.org.fallback;

/**
 * 能力回退管理器
 * 
 * 当 skill 不支持某些能力时,提供本地回退实现
 */
public class FallbackManager {
    
    private final JsonDataStore dataStore;
    
    public List<Role> getRoles(String personId) {
        // 从本地 JSON 存储读取
        return dataStore.getRoles(personId);
    }
    
    public List<OrgLevel> getOrgLevels(String orgId) {
        // 从本地 JSON 存储读取
        return dataStore.getOrgLevels(orgId);
    }
    
    // ...
}
```

#### 3.2.2 VFS 桥接实现

**1. 创建 FileAdapterBridge**

```java
package net.ooder.vfs.bridge;

/**
 * FileAdapter 桥接实现
 * 
 * 保持 FileAdapter 接口,
 * 内部通过 VfsSkill 调用
 */
public class FileAdapterBridge extends AbstractFileAdapter {
    
    private final VfsSkill vfsSkill;
    private final String storageType;
    
    public FileAdapterBridge(VfsSkill vfsSkill, String storageType) {
        this.vfsSkill = vfsSkill;
        this.storageType = storageType;
    }
    
    @Override
    public boolean exists(String path) {
        try {
            FileInfo fileInfo = vfsSkill.getFileInfo(path);
            return fileInfo != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public InputStream getInputStream(String path) throws IOException {
        return vfsSkill.downloadFile(path);
    }
    
    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        // VfsSkill 使用 uploadFile 方法
        // 需要包装为 OutputStream
        return new VfsOutputStreamWrapper(vfsSkill, path);
    }
    
    // ... 其他方法
}
```

#### 3.2.3 配置迁移工具

```java
package net.ooder.config.migration;

/**
 * 配置迁移工具
 * 
 * 将原有的 XML/数据库配置转换为 YAML 配置
 */
public class ConfigMigrationTool {
    
    /**
     * 迁移 Org 配置
     */
    public void migrateOrgConfig(String xmlConfigPath, String yamlOutputPath) {
        // 解析 XML
        OrgXmlConfig xmlConfig = parseXml(xmlConfigPath);
        
        // 转换为 YAML
        OrgYamlConfig yamlConfig = new OrgYamlConfig();
        yamlConfig.setDataSourceType(mapDataSourceType(xmlConfig.getType()));
        yamlConfig.setAppKey(xmlConfig.getAppKey());
        yamlConfig.setAppSecret(xmlConfig.getAppSecret());
        yamlConfig.setBaseUrl(xmlConfig.getBaseUrl());
        
        // 写入 YAML
        writeYaml(yamlConfig, yamlOutputPath);
    }
    
    /**
     * 迁移 VFS 配置
     */
    public void migrateVfsConfig(String oldConfigPath, String yamlOutputPath) {
        // 类似实现
    }
}
```

### 3.3 迁移步骤

#### 阶段1: 准备期 (1周)

1. **引入桥接依赖**
   ```xml
   <dependency>
       <groupId>net.ooder</groupId>
       <artifactId>org-bridge</artifactId>
       <version>2.3</version>
   </dependency>
   ```

2. **配置迁移**
   - 运行配置迁移工具
   - 验证 YAML 配置正确性

3. **双写准备**
   - 启用双写模式(同时写入旧系统和新系统)
   - 数据一致性校验

#### 阶段2: 并行期 (2周)

1. **灰度切换**
   - 10% 流量切换到新系统
   - 监控错误率和性能

2. **全量切换**
   - 100% 流量切换到新系统
   - 保留旧系统作为回退

#### 阶段3: 清理期 (1周)

1. **移除桥接层**
   - 应用直接调用 skill 接口
   - 移除 ooder-common 依赖

2. **清理旧代码**
   - 删除 org-web / vfs-web 中的适配代码

---

## 四、兼容性保证

### 4.1 接口兼容性

| 接口 | 兼容方式 | 备注 |
|------|----------|------|
| OrgManager | 桥接层 | 100%兼容 |
| FileAdapter | 桥接层 | 100%兼容 |
| OrgDataSourceProvider | 适配器 | 需配置转换 |
| OrgConfig | 迁移工具 | 自动转换 |

### 4.2 数据兼容性

- **用户数据**: 自动迁移,无丢失
- **组织架构**: 自动同步,保持一致
- **文件数据**: 存储位置不变,元数据迁移
- **配置数据**: 自动转换,备份保留

### 4.3 回退策略

```
如果出现问题:
1. 立即切换回旧系统 (配置开关)
2. 数据双写保证一致性
3. 修复问题后重新切换
```

---

## 五、实施建议

### 5.1 优先级

1. **高优先级**: skill-vfs-local (无外部依赖,风险低)
2. **中优先级**: skill-org-ldap (标准协议,兼容性好)
3. **低优先级**: skill-org-dingding/feishu/wecom (需测试第三方API)

### 5.2 测试策略

1. **单元测试**: 桥接层接口测试
2. **集成测试**: 与现有应用集成测试
3. **性能测试**: 对比旧系统性能
4. **兼容性测试**: 验证所有API兼容性

### 5.3 风险控制

| 风险 | 应对措施 |
|------|----------|
| 数据丢失 | 双写 + 备份 |
| 性能下降 | 缓存 + 异步 |
| API不兼容 | 桥接层 + 回退 |
| 第三方变更 | 熔断 + 降级 |

---

## 六、总结

### 6.1 关键结论

1. **无功能阉割**: 通过桥接层和回退机制,保证 100% 能力兼容
2. **平滑迁移**: 分阶段实施,可随时回退
3. **架构升级**: 从单体架构演进为 skill 化架构
4. **生态扩展**: 支持更多第三方集成(钉钉/飞书/企业微信等)

### 6.2 下一步行动

1. [ ] 创建桥接层模块 (org-bridge, vfs-bridge)
2. [ ] 实现配置迁移工具
3. [ ] 编写迁移文档和操作手册
4. [ ] 搭建测试环境验证
5. [ ] 制定详细的迁移计划

---

**文档版本**: 1.0  
**最后更新**: 2026-02-24
