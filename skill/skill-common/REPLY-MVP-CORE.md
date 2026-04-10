# skill-common 协作回复

**回复方**: skill-common 团队  
**日期**: 2026-03-24  
**优先级**: 高  
**关联文档**: [COLLABORATION_REQUEST_SKILL_COMMON.md](file:///E:/github/ooder-skills/mvp/docs/COLLABORATION_REQUEST_SKILL_COMMON.md)

---

## 已完成的修改

### 1. 添加条件化加载注解 ✅

**文件**: [SkillCommonAutoConfiguration.java](file:///e:/github/ooder-skills/skills/_system/skill-common/src/main/java/net/ooder/skill/common/config/SkillCommonAutoConfiguration.java)

```java
@Configuration
@ConditionalOnProperty(name = "skill.common.enabled", havingValue = "true", matchIfMissing = true)
public class SkillCommonAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(JsonStorageService.class)
    public JsonStorageService jsonStorageService() {
        JsonStorageService service = new JsonStorageService(storagePath);
        service.init();
        return service;
    }
    // ... 其他 Bean 定义
}
```

### 2. 添加配置元数据 ✅

**文件**: [spring-configuration-metadata.json](file:///e:/github/ooder-skills/skills/_system/skill-common/src/main/resources/META-INF/spring-configuration-metadata.json)

```json
{
  "properties": [
    {
      "name": "skill.common.enabled",
      "type": "java.lang.Boolean",
      "description": "Enable skill-common auto configuration",
      "defaultValue": true
    },
    {
      "name": "app.storage.path",
      "type": "java.lang.String",
      "description": "Storage path for JSON files",
      "defaultValue": "./data"
    }
  ]
}
```

### 3. 添加 getStorageRoot() 方法 ✅

**文件**: [JsonStorageService.java](file:///e:/github/ooder-skills/skills/_system/skill-common/src/main/java/net/ooder/skill/common/storage/JsonStorageService.java)

```java
public String getStorageRoot() {
    return this.storagePath;
}
```

---

## JsonStorageService API 确认

| 方法 | 状态 | 说明 |
|------|------|------|
| `loadList(String key, Class<T> elementClass)` | ✅ 已有 | 加载列表数据 |
| `saveList(String key, List<T> list)` | ✅ 已有 | 保存列表数据 |
| `getAll(String key)` | ✅ 已有 | 获取所有数据 |
| `put(String key, String id, T entity)` | ✅ 已有 | 存储单个实体 |
| `remove(String key, String id)` | ✅ 已有 | 删除实体 |
| `clear(String key)` | ✅ 已有 | 清空集合 |
| `getStorageRoot()` | ✅ 新增 | 获取存储路径 |

---

## 需要其他团队配合的事项

### scene-engine 团队

请移除 `net.ooder.scene.skill.engine.context.impl.JsonStorageService` 中的 `@Service` 注解，改用依赖注入方式使用 skill-common 提供的 `JsonStorageService`。

**建议修改**:
```java
// 移除 @Service 注解，改为通过构造函数注入
public class SomeService {
    private final JsonStorageService storage;
    
    public SomeService(JsonStorageService storage) {
        this.storage = storage;
    }
}
```

---

## 验收确认

| 标准 | 状态 |
|------|------|
| 无 Bean 冲突 | ✅ 已添加 `@ConditionalOnMissingBean(JsonStorageService.class)` |
| 条件化加载 | ✅ 已添加 `@ConditionalOnProperty` |
| 功能开关配置 | ✅ 已创建 `spring-configuration-metadata.json` |
| API 兼容 | ✅ 已添加 `getStorageRoot()` 方法 |

---

## 使用说明

### 启用/禁用 skill-common

```properties
# 禁用 skill-common 自动配置
skill.common.enabled=false

# 自定义存储路径
app.storage.path=/custom/data/path
```

---

## 修改文件清单

| 文件 | 绝对路径 |
|------|----------|
| SkillCommonAutoConfiguration.java | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\config\SkillCommonAutoConfiguration.java` |
| JsonStorageService.java | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\storage\JsonStorageService.java` |
| spring-configuration-metadata.json | `e:\github\ooder-skills\skills\_system\skill-common\src\main\resources\META-INF\spring-configuration-metadata.json` |

---

*skill-common 项目组*
