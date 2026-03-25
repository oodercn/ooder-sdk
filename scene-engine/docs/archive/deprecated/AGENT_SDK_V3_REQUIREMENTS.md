# agent-sdk v3.0 详细需求说明

## 一、SkillPackage 重写需求

### 1.1 当前结构（v2.x）

```java
public class SkillPackage {
    private String id;
    private String name;
    private String version;
    private String sceneId;           // 删除
    private String category;          // 删除（改为枚举）
    private String subCategory;       // 删除
    private boolean sceneSkill;       // 删除
    private boolean mainFirst;        // 删除
    private Object mainFirstConfig;   // 删除
    private List<String> sceneCapabilities;  // 删除
    // ...
}
```

### 1.2 目标结构（v3.0）

```java
package net.ooder.skills.api;

import java.util.List;
import java.util.Set;

public class SkillPackage {
    // 基础信息
    private String id;
    private String name;
    private String version;
    private String description;
    
    // v3.0 核心字段
    private SkillForm form;                    // SCENE | STANDALONE
    private SkillCategory category;            // knowledge | llm | tool | ...
    private Set<ServicePurpose> purposes;      // 服务目的组合
    private SceneType sceneType;               // AUTO | TRIGGER | HYBRID（仅SCENE时）
    
    // 场景特有（仅 form=SCENE 时有效）
    private SceneStructure sceneStructure;     // 场景结构
    private String entryCapability;            // 入口能力ID
    
    // 能力列表
    private List<Capability> capabilities;
    
    // 协作配置
    private CollaborationConfig collaboration;
    
    // 元数据
    private Map<String, Object> metadata;
    private List<String> tags;
    
    // Getters/Setters...
    
    // 便捷方法
    public boolean isScene() {
        return form == SkillForm.SCENE;
    }
    
    public boolean canSelfDrive() {
        return isScene() && sceneType != null && sceneType.canSelfDrive();
    }
}
```

## 二、SkillManifest 重写需求

### 2.1 当前结构（v2.x）

```java
public class SkillManifest {
    private String skillId;
    private String sceneId;           // 删除
    private String skillType;         // 删除
    private boolean sceneSkill;       // 删除
    private boolean mainFirstScene;   // 删除
    private List<String> collaborativeScenes;  // 删除
    // ...
}
```

### 2.2 目标结构（v3.0）

```java
package net.ooder.skills.api;

import java.util.List;
import java.util.Set;

public class SkillManifest {
    // 基础信息
    private String id;
    private String name;
    private String version;
    private String description;
    
    // v3.0 核心字段
    private SkillForm form;
    private SkillCategory category;
    private Set<ServicePurpose> purposes;
    private SceneType sceneType;              // 可选，仅SCENE时
    
    // 能力声明
    private List<CapabilityDeclaration> capabilities;
    
    // 场景配置（仅 form=SCENE 时有效）
    private SceneConfig sceneConfig;
    
    // 协作配置
    private CollaborationDeclaration collaboration;
    
    // 入口点
    private String entryPoint;                // 入口能力或函数
    
    // 依赖
    private List<Dependency> dependencies;
    
    // 元数据
    private SkillMetadata metadata;
    
    // Getters/Setters...
}

/**
 * 能力声明
 */
public class CapabilityDeclaration {
    private String id;
    private String name;
    private String type;          // internal | exposed
    private String description;
    private Map<String, Object> parameters;
}

/**
 * 场景配置
 */
public class SceneConfig {
    private SceneType sceneType;
    private OrchestrationConfig orchestration;
    private List<String> internalCapabilities;
    private List<String> childSkills;
}

/**
 * 协作声明
 */
public class CollaborationDeclaration {
    private boolean externallyAccessible;
    private List<String> exposedCapabilities;
    private List<ExternalDependency> externalDependencies;
}
```

## 三、SkillYamlParser 重写需求

### 3.1 解析逻辑变化

```java
package net.ooder.sdk.discovery;

public class SkillYamlParser {
    
    /**
     * 解析 v3.0 YAML 格式
     */
    public SkillManifest parse(InputStream yamlStream) {
        Map<String, Object> yaml = parseYaml(yamlStream);
        
        SkillManifest manifest = new SkillManifest();
        
        // 基础信息
        manifest.setId(getString(yaml, "id"));
        manifest.setName(getString(yaml, "name"));
        manifest.setVersion(getString(yaml, "version"));
        manifest.setDescription(getString(yaml, "description"));
        
        // v3.0 核心字段
        manifest.setForm(parseSkillForm(getString(yaml, "form")));
        manifest.setCategory(parseSkillCategory(getString(yaml, "category")));
        manifest.setPurposes(parsePurposes(getList(yaml, "purposes")));
        
        // 场景类型（仅 SCENE 时）
        if (manifest.getForm() == SkillForm.SCENE) {
            manifest.setSceneType(parseSceneType(getString(yaml, "sceneType")));
            manifest.setSceneConfig(parseSceneConfig(yaml));
        }
        
        // 能力列表
        manifest.setCapabilities(parseCapabilities(yaml));
        
        // 协作配置
        manifest.setCollaboration(parseCollaboration(yaml));
        
        return manifest;
    }
    
    private SkillForm parseSkillForm(String value) {
        if (value == null) return SkillForm.STANDALONE;
        return SkillForm.valueOf(value.toUpperCase());
    }
    
    private SceneType parseSceneType(String value) {
        if (value == null) return null;
        return SceneType.valueOf(value.toUpperCase());
    }
    
    private SkillCategory parseSkillCategory(String value) {
        if (value == null) return SkillCategory.OTHER;
        return SkillCategory.valueOf(value.toUpperCase());
    }
    
    private Set<ServicePurpose> parsePurposes(List<String> values) {
        if (values == null) return Collections.emptySet();
        return values.stream()
            .map(v -> ServicePurpose.valueOf(v.toUpperCase()))
            .collect(Collectors.toSet());
    }
}
```

## 四、A2AService 修改需求

### 4.1 Agent 发现条件适配

```java
package net.ooder.sdk.a2a;

public interface A2AService {
    
    /**
     * 发现技能（v3.0）
     * 
     * @param form 技能形态过滤（可选）
     * @param category 技能分类过滤（可选）
     * @param sceneType 场景类型过滤（可选）
     * @return 匹配的技能列表
     */
    List<SkillCard> discoverSkills(
        SkillForm form, 
        SkillCategory category, 
        SceneType sceneType
    );
    
    /**
     * 发现场景技能
     */
    default List<SkillCard> discoverSceneSkills() {
        return discoverSkills(SkillForm.SCENE, null, null);
    }
    
    /**
     * 发现自主场景
     */
    default List<SkillCard> discoverAutoScenes() {
        return discoverSkills(SkillForm.SCENE, null, SceneType.AUTO);
    }
    
    /**
     * 发现触发场景
     */
    default List<SkillCard> discoverTriggerScenes() {
        return discoverSkills(SkillForm.SCENE, null, SceneType.TRIGGER);
    }
    
    /**
     * 发现独立技能
     */
    default List<SkillCard> discoverStandaloneSkills() {
        return discoverSkills(SkillForm.STANDALONE, null, null);
    }
}
```

## 五、SkillCard 重写需求

### 5.1 目标结构

```java
package net.ooder.sdk.a2a.capability;

public class SkillCard {
    // 基础信息
    private String skillId;
    private String name;
    private String version;
    private String description;
    
    // v3.0 核心属性
    private SkillForm form;
    private SkillCategory category;
    private Set<ServicePurpose> purposes;
    private SceneType sceneType;
    
    // 能力端点
    private List<CapabilityEndpoint> capabilities;
    
    // Agent 信息
    private String agentId;
    private String agentEndpoint;
    
    // 状态
    private SkillStatus status;
    private long lastHeartbeat;
    
    // 元数据
    private Map<String, Object> metadata;
    
    // Getters/Setters...
    
    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return form == SkillForm.SCENE;
    }
    
    /**
     * 是否可自驱动
     */
    public boolean canSelfDrive() {
        return isScene() && sceneType != null && sceneType.canSelfDrive();
    }
}
```

## 六、配置文件格式需求

### 6.1 cmd-scene.yaml（v3.0）

```yaml
id: cmd-scene
name: 命令场景
version: 3.0.0
form: SCENE
sceneType: TRIGGER
category: workflow
purposes:
  - TEAM
  - INSTANT
  - REACTIVE
description: 命令处理场景

capabilities:
  - id: execute
    name: 执行命令
    type: exposed
    parameters:
      command:
        type: string
        required: true
      args:
        type: array
        required: false

entryPoint: execute

collaboration:
  externallyAccessible: true
  exposedCapabilities:
    - execute
```

### 6.2 msg-scene.yaml（v3.0）

```yaml
id: msg-scene
name: 消息场景
version: 3.0.0
form: SCENE
sceneType: AUTO
category: workflow
purposes:
  - TEAM
  - PERSISTENT
  - PROACTIVE
description: 消息处理场景

sceneConfig:
  orchestration:
    type: STATE_MACHINE
    steps:
      - id: receive
        capabilityId: receiveMessage
      - id: process
        capabilityId: processMessage
      - id: send
        capabilityId: sendMessage

capabilities:
  - id: receiveMessage
    name: 接收消息
    type: internal
  - id: processMessage
    name: 处理消息
    type: internal
  - id: sendMessage
    name: 发送消息
    type: exposed

entryPoint: receiveMessage
```

## 七、迁移脚本需求

### 7.1 数据迁移 SQL

```sql
-- 1. 备份旧数据
CREATE TABLE skill_package_backup AS SELECT * FROM skill_package;
CREATE TABLE skill_manifest_backup AS SELECT * FROM skill_manifest;

-- 2. 添加新字段
ALTER TABLE skill_package ADD COLUMN form VARCHAR(20) DEFAULT 'STANDALONE';
ALTER TABLE skill_package ADD COLUMN scene_type VARCHAR(20);
ALTER TABLE skill_package ADD COLUMN skill_category VARCHAR(20);
ALTER TABLE skill_package ADD COLUMN purposes TEXT;

-- 3. 迁移数据
UPDATE skill_package SET 
    form = CASE 
        WHEN scene_skill = true THEN 'SCENE'
        ELSE 'STANDALONE'
    END,
    scene_type = CASE 
        WHEN main_first = true THEN 'AUTO'
        WHEN scene_skill = true THEN 'TRIGGER'
        ELSE NULL
    END,
    skill_category = CASE 
        WHEN category = 'ABS' OR category = 'ASS' THEN 'WORKFLOW'
        WHEN category = 'TBS' THEN 'WORKFLOW'
        ELSE 'OTHER'
    END;

-- 4. 删除旧字段
ALTER TABLE skill_package DROP COLUMN scene_skill;
ALTER TABLE skill_package DROP COLUMN main_first;
ALTER TABLE skill_package DROP COLUMN scene_id;

-- 5. 删除旧分类表
DROP TABLE IF EXISTS scene_skill_classification;
```

## 八、测试需求

### 8.1 单元测试

```java
public class SkillPackageTest {
    
    @Test
    public void testSceneSkillDetection() {
        SkillPackage pkg = new SkillPackage();
        pkg.setForm(SkillForm.SCENE);
        pkg.setSceneType(SceneType.AUTO);
        
        assertTrue(pkg.isScene());
        assertTrue(pkg.canSelfDrive());
    }
    
    @Test
    public void testStandaloneSkillDetection() {
        SkillPackage pkg = new SkillPackage();
        pkg.setForm(SkillForm.STANDALONE);
        
        assertFalse(pkg.isScene());
    }
    
    @Test
    public void testYamlParsing() {
        SkillYamlParser parser = new SkillYamlParser();
        SkillManifest manifest = parser.parse(getTestYaml());
        
        assertEquals(SkillForm.SCENE, manifest.getForm());
        assertEquals(SceneType.AUTO, manifest.getSceneType());
        assertEquals(SkillCategory.KNOWLEDGE, manifest.getCategory());
    }
}
```

---

**文档版本**：v3.0
**创建时间**：2026-03-10
**状态**：待评审
