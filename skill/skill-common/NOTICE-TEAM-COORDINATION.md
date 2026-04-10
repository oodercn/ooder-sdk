# skill-common 团队协作通知

**来源**: mvp-core 团队  
**日期**: 2026-03-24  
**优先级**: 高

---

## 需要贵团队确认的事项

### 1. AuthApi 接口规范

**当前状态**:
- 路径: `/api/v1/auth/login`
- 类: `net.ooder.skill.common.api.AuthApi`

**需要确认**:
- [ ] `/api/v1/auth/login` 接口参数和返回值是否稳定
- [ ] `/api/v1/auth/session` 接口是否存在或计划添加
- [ ] 是否有接口文档可供其他团队参考

**建议**: 添加 OpenAPI/Swagger 文档说明

---

### 2. JsonStorageService Bean 冲突

**问题描述**:
```
skill-common: net.ooder.skill.common.storage.JsonStorageService (@Service)
scene-engine: net.ooder.scene.skill.engine.context.impl.JsonStorageService (@Service)
```

两个不同包路径的 `JsonStorageService` 类都带有 `@Service` 注解，导致 Bean 冲突。

**建议修改**:

```java
@Service
@ConditionalOnMissingBean(JsonStorageService.class)
public class JsonStorageService {
    // ...
}
```

或者在 `SkillCommonAutoConfiguration` 中使用条件化配置：

```java
@Bean
@ConditionalOnMissingBean
public JsonStorageService jsonStorageService() {
    return new JsonStorageService(storagePath);
}
```

---

## 影响范围

- mvp-core 启动时 Bean 冲突
- 其他依赖 skill-common 的项目可能遇到相同问题

---

## 联系方式

如有疑问，请联系 mvp-core 团队或回复此通知。

---

*mvp-core 项目组*
