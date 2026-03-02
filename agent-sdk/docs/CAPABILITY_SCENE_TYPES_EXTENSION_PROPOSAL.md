# Capability 支持场景类型扩展方案

## 背景

在skill-scene模块开发过程中，需要支持**声明式场景**功能：能力注册时声明支持的场景类型，系统自动匹配加入。当前 `Capability` 接口缺少此功能。

## 当前问题

```java
// 当前 Capability 接口
public interface Capability {
    String getCapId();
    String getName();
    String getType();
    // ... 其他方法
    // 缺少 supportedSceneTypes 字段
}
```

## 业务需求

### 声明式场景 vs 绑定式场景

| 场景类型 | 说明 | 示例 |
|---------|------|------|
| **声明式场景** | 能力注册时声明支持的场景类型，系统自动匹配加入 | 灯泡注册时声明支持"开关场景"，网关自动将其加入"离家模式"场景 |
| **绑定式场景** | 需要手动绑定能力到场景 | 开关控制灯泡，需要手动建立1:1或1:N的绑定关系 |

## 扩展方案

### 方案1：添加 supportedSceneTypes 字段（推荐）

在 `Capability` 接口中添加：

```java
package net.ooder.sdk.api.capability;

import java.util.List;
import java.util.Map;

/**
 * 能力接口，定义Agent的能力基本信息
 * 包含能力ID、名称、类型、版本、状态等属性
 * 
 * @author ooder
 * @since 2.3
 */
public interface Capability {
    
    // ==================== 现有方法 ====================
    
    String getCapId();
    void setCapId(String capId);
    
    String getName();
    void setName(String name);
    
    String getType();
    void setType(String type);
    
    String getVersion();
    void setVersion(String version);
    
    String getDescription();
    void setDescription(String description);
    
    CapabilityStatus getStatus();
    void setStatus(CapabilityStatus status);
    void setStatus(String status);
    
    Map<String, Object> getConfig();
    void setConfig(Map<String, Object> config);
    
    String getCapabilityId();
    void setCapabilityId(String capabilityId);
    
    String getSkillId();
    void setSkillId(String skillId);
    
    CapAddress getAddress();
    void setAddress(CapAddress address);
    
    List<String> getTags();
    void setTags(List<String> tags);
    
    long getRegisteredTime();
    void setRegisteredTime(long registeredTime);
    
    long getLastHeartbeat();
    void setLastHeartbeat(long lastHeartbeat);
    
    boolean isAvailable();
    void setAvailable(boolean available);
    
    // ==================== 新增方法 ====================
    
    /**
     * 获取能力支持的场景类型列表
     * 
     * <p>用于声明式场景自动匹配。能力注册时声明支持的场景类型，
     * 场景管理器根据此列表自动将能力加入匹配的场景。</p>
     * 
     * <p>示例：灯泡能力可以声明支持 ["switch", "dimmer", "color"] 场景类型</p>
     *
     * @return 支持的场景类型列表，为空表示不支持自动匹配
     */
    List<String> getSupportedSceneTypes();
    
    /**
     * 设置能力支持的场景类型列表
     *
     * @param sceneTypes 场景类型列表
     */
    void setSupportedSceneTypes(List<String> sceneTypes);
    
    /**
     * 判断能力是否支持指定场景类型
     *
     * @param sceneType 场景类型
     * @return true表示支持
     */
    default boolean supportsSceneType(String sceneType) {
        List<String> types = getSupportedSceneTypes();
        return types != null && types.contains(sceneType);
    }
    
    /**
     * 判断能力是否支持声明式场景匹配
     *
     * @return true表示支持声明式场景
     */
    default boolean supportsDeclarativeScenes() {
        List<String> types = getSupportedSceneTypes();
        return types != null && !types.isEmpty();
    }
}
```

### 方案2：使用 tags 字段替代（不推荐）

当前 `Capability` 已有 `getTags()` 方法，可以复用：

```java
// 约定：以 "scene:" 前缀的标签表示支持的场景类型
// 示例：["scene:switch", "scene:dimmer", "color:red"]

public default List<String> getSupportedSceneTypes() {
    return getTags().stream()
        .filter(tag -> tag.startsWith("scene:"))
        .map(tag -> tag.substring(6))
        .collect(Collectors.toList());
}
```

**缺点**：
- 隐式约定，不够明确
- 与其他标签混合，容易冲突
- 不支持精细化的场景匹配逻辑

## 实现示例

### Capability 实现类示例

```java
package net.ooder.sdk.impl.capability;

import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.capability.CapabilityStatus;
import net.ooder.sdk.api.capability.CapAddress;

import java.util.*;

/**
 * 能力实现类
 */
public class DefaultCapability implements Capability {
    
    private String capId;
    private String capabilityId;
    private String name;
    private String type;
    private String version;
    private String description;
    private CapabilityStatus status;
    private Map<String, Object> config;
    private String skillId;
    private CapAddress address;
    private List<String> tags;
    private long registeredTime;
    private long lastHeartbeat;
    private boolean available;
    
    // ==================== 新增字段 ====================
    private List<String> supportedSceneTypes;
    
    // ==================== 现有方法实现 ====================
    
    @Override
    public String getCapId() { return capId; }
    
    @Override
    public void setCapId(String capId) { this.capId = capId; }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public void setName(String name) { this.name = name; }
    
    // ... 其他现有方法
    
    // ==================== 新增方法实现 ====================
    
    @Override
    public List<String> getSupportedSceneTypes() {
        return supportedSceneTypes != null ? 
               new ArrayList<>(supportedSceneTypes) : 
               Collections.emptyList();
    }
    
    @Override
    public void setSupportedSceneTypes(List<String> sceneTypes) {
        this.supportedSceneTypes = sceneTypes != null ? 
                                   new ArrayList<>(sceneTypes) : 
                                   null;
    }
}
```

### 场景类型定义（建议）

```java
package net.ooder.sdk.api.scene;

/**
 * 标准场景类型常量
 */
public final class SceneTypes {
    
    private SceneTypes() {}
    
    // 开关控制场景
    public static final String SWITCH = "switch";
    
    // 调光场景
    public static final String DIMMER = "dimmer";
    
    // 颜色控制场景
    public static final String COLOR = "color";
    
    // 温度控制场景
    public static final String TEMPERATURE = "temperature";
    
    // 湿度控制场景
    public static final String HUMIDITY = "humidity";
    
    // 安防场景
    public static final String SECURITY = "security";
    
    // 监控场景
    public static final String MONITOR = "monitor";
    
    // 定时场景
    public static final String SCHEDULE = "schedule";
    
    // 离家场景
    public static final String AWAY = "away";
    
    // 回家场景
    public static final String HOME = "home";
    
    // 睡眠场景
    public static final String SLEEP = "sleep";
    
    // 起床场景
    public static final String WAKE_UP = "wake_up";
}
```

## 使用场景

### 场景1：能力注册时声明支持的场景类型

```java
// 创建灯泡能力
Capability lightCapability = new DefaultCapability();
lightCapability.setCapId("light-001");
lightCapability.setName("智能灯泡");
lightCapability.setType("light");

// 声明支持的场景类型
lightCapability.setSupportedSceneTypes(Arrays.asList(
    SceneTypes.SWITCH,      // 开关控制
    SceneTypes.DIMMER,      // 调光
    SceneTypes.COLOR        // 颜色
));

// 注册能力
capRegistry.register(lightCapability);
```

### 场景2：场景自动匹配

```java
// 场景管理器自动匹配能力
public class SceneManager {
    
    @Autowired
    private CapRegistry capRegistry;
    
    /**
     * 创建声明式场景
     */
    public Scene createDeclarativeScene(String sceneType) {
        Scene scene = new Scene();
        scene.setType(sceneType);
        
        // 自动查找支持该场景类型的能力
        List<Capability> matchingCaps = capRegistry.findAll().stream()
            .filter(cap -> cap.supportsSceneType(sceneType))
            .collect(Collectors.toList());
        
        // 自动加入场景
        for (Capability cap : matchingCaps) {
            scene.addCapability(cap);
        }
        
        return scene;
    }
}

// 使用
Scene switchScene = sceneManager.createDeclarativeScene(SceneTypes.SWITCH);
// 自动包含所有支持 "switch" 场景类型的能力
```

### 场景3：能力发现与筛选

```java
// 查找支持特定场景类型的能力
List<Capability> switchCaps = capRegistry.findAll().stream()
    .filter(cap -> cap.supportsSceneType(SceneTypes.SWITCH))
    .collect(Collectors.toList());

// 查找支持声明式场景的能力
List<Capability> declarativeCaps = capRegistry.findAll().stream()
    .filter(Capability::supportsDeclarativeScenes)
    .collect(Collectors.toList());
```

## 影响范围

| 组件 | 影响 | 修改内容 |
|------|------|----------|
| Capability | 新增方法 | 添加 `getSupportedSceneTypes()` 等方法 |
| DefaultCapability | 需要实现 | 实现新增方法 |
| CapRegistry | 可能需要适配 | 添加按场景类型查询方法 |
| SceneManager | 新增功能 | 支持声明式场景自动匹配 |

## 兼容性

- **向后兼容**：现有代码无需修改（新增方法有默认实现）
- **新增功能**：新代码可以使用场景类型功能

## 与现有功能的关系

| 功能 | 关系 | 说明 |
|------|------|------|
| `getTags()` | 互补 | `tags` 用于通用标签，`supportedSceneTypes` 专门用于场景匹配 |
| `getType()` | 不同 | `type` 表示能力类型（如"light"），`supportedSceneTypes` 表示支持的场景类型（如"switch"） |
| `getConfig()` | 互补 | `config` 存储配置参数，`supportedSceneTypes` 存储场景类型 |

---

**建议**：采用方案1，在 `Capability` 接口中添加 `supportedSceneTypes` 字段和相关方法。
