# SE SDK 2.3.1 协作响应：SceneGroupManager 实现说明

## 致：MVP前端团队

**日期**: 2026-03-19  
**响应方**: SE SDK 团队  
**优先级**: 🔴 高

---

## 一、问题澄清

### 1.1 两个不同的 SceneGroupManager

项目中存在**两个不同用途**的 SceneGroupManager：

| 组件 | 包路径 | 用途 | 状态 |
|------|--------|------|------|
| **SDK SceneGroupManager** | `net.ooder.sdk.api.scene.SceneGroupManager` | 场景集群高可用管理（接口） | ❌ 需实现 |
| **SE SceneGroupManager** | `net.ooder.scene.group.SceneGroupManager` | 业务场景组管理（实现类） | ✅ 已实现 |

### 1.2 当前状态

```
SDK层（agent-sdk）:
├── net.ooder.sdk.api.scene.SceneGroupManager (接口) ← 需要实现
│   ├── create() - 创建场景集群
│   ├── destroy() - 销毁场景集群
│   ├── join() - 加入集群
│   ├── handleFailover() - 故障转移
│   └── ... (高可用相关方法)

SE层（scene-engine）:
├── net.ooder.scene.group.SceneGroupManager (@Component 实现类) ← 已实现
│   ├── createSceneGroup() - 创建业务场景组
│   ├── activateSceneGroup() - 激活场景组
│   ├── addParticipant() - 添加参与者
│   └── ... (业务场景管理方法)
```

---

## 二、解决方案

### 2.1 SE SceneGroupManager 已可用

SE层的 `SceneGroupManager` 已经是一个完整的 Spring Bean（`@Component`），可直接注入使用：

```java
@Autowired
private net.ooder.scene.group.SceneGroupManager sceneGroupManager;

// 创建场景组
SceneGroup group = sceneGroupManager.createSceneGroup(
    "scene-group-001",
    "template-001", 
    "user-001",
    SceneGroup.CreatorType.USER
);

// 激活场景组
sceneGroupManager.activateSceneGroup("scene-group-001");

// 添加参与者
Participant participant = new Participant(...);
sceneGroupManager.addParticipant("scene-group-001", participant);
```

### 2.2 SDK SceneGroupManager 需要适配器实现

如果MVP需要使用SDK的 `net.ooder.sdk.api.scene.SceneGroupManager` 接口，SE将提供适配器实现：

```java
package net.ooder.scene.bridge;

import net.ooder.sdk.api.scene.SceneGroupManager;
import net.ooder.scene.group.SceneGroupManager as SeSceneGroupManager;
import org.springframework.stereotype.Component;

@Component
public class SdkSceneGroupManagerAdapter implements SceneGroupManager {
    
    private final SeSceneGroupManager seManager;
    
    public SdkSceneGroupManagerAdapter(SeSceneGroupManager seManager) {
        this.seManager = seManager;
    }
    
    @Override
    public CompletableFuture<SceneGroup> create(String sceneId, SceneGroupConfig config) {
        // 适配实现
    }
    
    // ... 其他方法适配
}
```

---

## 三、知识库持久化已完成

### 3.1 新增持久化实现

SE SDK 2.3.1 已完成知识库持久化功能：

| 文件 | 说明 |
|------|------|
| `KnowledgeRepository.java` | 知识库持久化接口 |
| `JsonKnowledgeRepository.java` | JSON文件存储（默认） |
| `InMemoryKnowledgeRepository.java` | 内存存储（开发测试） |
| `JsonVectorStore.java` | JSON向量存储 |
| `KnowledgePersistenceAutoConfiguration.java` | 自动配置 |

### 3.2 配置方式

```yaml
se:
  knowledge:
    persistence:
      type: json  # json | memory | sql
      base-path: ~/.ooder/data/knowledge
      auto-save: true
      save-interval-ms: 5000
    vector-store:
      type: json  # json | memory
      dimension: 1536
```

---

## 四、MVP集成指南

### 4.1 直接使用 SE SceneGroupManager

MVP项目可直接注入SE的SceneGroupManager：

```java
// MVP Controller
@RestController
@RequestMapping("/api/v1/scene-groups")
public class SceneGroupController {
    
    @Autowired
    private net.ooder.scene.group.SceneGroupManager sceneGroupManager;
    
    @PostMapping
    public SceneGroup create(@RequestBody CreateRequest request) {
        return sceneGroupManager.createSceneGroup(
            request.getSceneGroupId(),
            request.getTemplateId(),
            request.getCreatorId(),
            SceneGroup.CreatorType.USER
        );
    }
}
```

### 4.2 知识库API已就绪

MVP文档中的知识库API需求已在SE层实现：

| API | SE实现 | 状态 |
|-----|--------|------|
| 文档列表 | `KnowledgeBaseService.listDocuments()` | ✅ |
| 添加文本 | `KnowledgeBaseService.addDocument()` | ✅ |
| 上传文件 | `UserContributionService.uploadFile()` | ✅ |
| URL导入 | `UserContributionService.importFromUrl()` | ✅ |
| 删除文档 | `KnowledgeBaseService.deleteDocument()` | ✅ |
| 知识检索 | `KnowledgeBaseService.search()` | ✅ |
| 场景知识检索 | `KnowledgeCapability.searchKnowledge()` | ✅ |
| 索引状态 | `KnowledgeBaseService.getIndexStatus()` | ✅ |

---

## 五、待办事项

### 5.1 SE SDK团队将提供

- [ ] `SdkSceneGroupManagerAdapter` - SDK接口适配器
- [ ] `SceneGroupManagerAutoConfiguration` - 自动配置类
- [ ] spring.factories 注册

### 5.2 MVP团队需要确认

- [ ] 是否需要SDK的 `SceneGroupManager` 接口实现？
- [ ] 还是直接使用SE的 `SceneGroupManager` 实现类？
- [ ] 知识库API是否满足需求？

---

## 六、快速启动方案

如果MVP需要立即启动，可以添加以下临时配置：

```java
@Configuration
public class MvpSceneConfig {
    
    @Bean
    @ConditionalOnMissingBean(net.ooder.sdk.api.scene.SceneGroupManager.class)
    public net.ooder.sdk.api.scene.SceneGroupManager sdkSceneGroupManager(
            net.ooder.scene.group.SceneGroupManager seManager) {
        return new SdkSceneGroupManagerAdapter(seManager);
    }
}
```

---

## 七、联系方式

- SE SDK团队已就绪，可随时协作
- 请确认上述问题后，我们将提供最终实现

---

**文档版本**: v1.0  
**创建日期**: 2026-03-19  
**创建人**: SE SDK 团队
