# Skill JPA Configuration Support

## 简介

`SkillJpaConfigurationSupport` 是一个为 Skill 提供通用 JPA 配置支持的基类。它封装了常见的 JPA 配置，包括数据源、EntityManagerFactory 和事务管理器的创建。

## 特性

- **零配置启动**：默认使用 SQLite 数据库，无需额外配置
- **自动命名**：根据 Skill 名称自动生成 Bean 名称和数据库文件名
- **可扩展**：支持通过系统属性自定义数据库路径
- **模块化**：每个 Skill 拥有独立的 JPA 配置，互不干扰

## 使用方法

### 1. 基本使用

```java
package net.ooder.skill.xxx.config;

import net.ooder.skill.common.jpa.SkillJpaConfigurationSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "net.ooder.skill.xxx.repository",
    entityManagerFactoryRef = "xxxEntityManagerFactory",
    transactionManagerRef = "xxxTransactionManager"
)
public class XxxJpaConfiguration extends SkillJpaConfigurationSupport {

    public XxxJpaConfiguration() {
        super("xxx", "net.ooder.skill.xxx.entity");
    }
}
```

### 2. 配置说明

构造函数参数：
- `skillName`: Skill 名称，用于生成 Bean 名称和数据库文件名
- `entityPackage`: Entity 类所在的包路径

生成的 Bean 名称：
- 数据源：`xxxDataSource`
- EntityManagerFactory：`xxxEntityManagerFactory`
- 事务管理器：`xxxTransactionManager`

### 3. 自定义数据库路径

通过系统属性配置数据库路径：

```bash
-Dskill.xxx.db.path=./custom/xxx.db
```

或在 `application.yml` 中配置：

```yaml
skill:
  xxx:
    db:
      path: ./custom/xxx.db
```

### 4. 覆盖默认配置

如果需要使用其他数据库或自定义配置，可以覆盖父类方法：

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "net.ooder.skill.xxx.repository",
    entityManagerFactoryRef = "xxxEntityManagerFactory",
    transactionManagerRef = "xxxTransactionManager"
)
public class XxxJpaConfiguration extends SkillJpaConfigurationSupport {

    public XxxJpaConfiguration() {
        super("xxx", "net.ooder.skill.xxx.entity");
    }

    @Override
    @Bean
    @ConditionalOnMissingBean(name = "xxxDataSource")
    public DataSource skillDataSource() {
        // 自定义数据源配置
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/xxx")
                .username("root")
                .password("password")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}
```

### 5. 事务管理

在 Service 层使用事务：

```java
@Service
public class XxxService {

    @Autowired
    private XxxRepository xxxRepository;

    @Transactional(transactionManager = "xxxTransactionManager")
    public XxxEntity create(XxxEntity entity) {
        return xxxRepository.save(entity);
    }
}
```

## 配置属性

| 属性名 | 默认值 | 说明 |
|--------|--------|------|
| `skill.{name}.db.path` | `./data/{name}.db` | 数据库文件路径 |

## 注意事项

1. **Bean 名称唯一性**：确保不同 Skill 的 `skillName` 不重复，否则会导致 Bean 名称冲突
2. **包路径正确性**：`entityPackage` 必须指向包含 `@Entity` 注解类的包
3. **事务管理器指定**：使用 `@Transactional` 时必须指定 `transactionManager`

## 示例项目

### skill-tenant 配置示例

```java
package net.ooder.skill.tenant.config;

import net.ooder.skill.common.jpa.SkillJpaConfigurationSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "net.ooder.skill.tenant.repository",
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
public class TenantJpaConfiguration extends SkillJpaConfigurationSupport {

    public TenantJpaConfiguration() {
        super("tenant", "net.ooder.skill.tenant.entity");
    }
}
```

### skill-scenes 配置示例

```java
package net.ooder.skill.scenes.config;

import net.ooder.skill.common.jpa.SkillJpaConfigurationSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "net.ooder.skill.scenes.repository",
    entityManagerFactoryRef = "scenesEntityManagerFactory",
    transactionManagerRef = "scenesTransactionManager"
)
public class ScenesJpaConfiguration extends SkillJpaConfigurationSupport {

    public ScenesJpaConfiguration() {
        super("scenes", "net.ooder.skill.scenes.entity");
    }
}
```

## 版本信息

- **版本**: 1.0
- **作者**: SDK Team
- **创建日期**: 2026-04-05
- **依赖**: Spring Boot 3.x, Spring Data JPA, Hibernate

## 相关文档

- [JPA 模块化方案详细实施指南](e:\apex\os\docs\jpa-modular-implementation-guide.md)
- [Spring Data JPA 官方文档](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
