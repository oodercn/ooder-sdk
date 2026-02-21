# ooder平台数据服务常用注解结构整理

## 目录

1. [概述](#1-概述)
2. [核心注解分类](#2-核心注解分类)
   - 2.1 数据实体注解
   - 2.2 数据服务注解
   - 2.3 数据字段注解
   - 2.4 数据枚举注解
   - 2.5 数据访问注解
3. [注解使用规范](#3-注解使用规范)
   - 3.1 命名规范
   - 3.2 组合使用规范
   - 3.3 生命周期规范
4. [最佳实践](#4-最佳实践)
   - 4.1 实体映射最佳实践
   - 4.2 服务注解最佳实践
   - 4.3 字段注解最佳实践

## 1. 概述

ooder平台数据服务注解体系是整个数据层的核心，通过声明式注解配置实现数据实体映射、服务定义和数据访问控制。注解体系遵循Spring生态规范，同时扩展了平台特有的数据处理能力。

## 2. 核心注解分类

### 2.1 数据实体注解

#### @EntityMapping
用于将Java类映射到数据库表或其他持久化存储结构。

**主要属性：**
- `table()`: 数据库表名（必选）
- `schema()`: 数据库模式（可选）
- `primaryKey()`: 主键字段名（可选，默认为"id"）
- `primaryKeyStrategy()`: 主键生成策略（可选，默认为AUTO）
- `autoCRUD()`: 是否自动生成CRUD操作（可选，默认为false）

**使用示例：**
```java
@EntityMapping(
    table = "attendance_records",
    primaryKey = "id",
    primaryKeyStrategy = PrimaryKeyStrategy.AUTO
)
public class AttendanceRecord {
    // 字段定义
}
```

#### @DBTable
用于定义数据库表的基本信息。

**主要属性：**
- `tableName()`: 表名（必选）
- `primaryKey()`: 主键字段名（必选）
- `cname()`: 中文名称（可选）
- `configKey()`: 配置键（可选）

### 2.2 数据服务注解

#### @Aggregation
用于声明服务的聚合类型和用户空间。

**主要属性：**
- `type()`: 聚合类型（API、MENU等）
- `userSpace()`: 用户空间（SYS、USER等）
- `domainId()`: 领域ID（可选）
- `moduleName()`: 模块名称（可选）

**使用示例：**
```java
@Aggregation(
    type = AggregationType.API,
    userSpace = UserSpace.SYS
)
@RestController
@RequestMapping("/attendance")
@Service
public class AttendanceService {
    // 服务实现
}
```

#### @BusinessModule
用于标识和定义业务系统中的模块单元。

**主要属性：**
- `value()`: 业务模块名称（必选）
- `desc()`: 模块描述（可选）
- `moduleCode()`: 模块编码（可选）
- `version()`: 模块版本（可选）

### 2.3 数据字段注解

#### @DBField
用于定义数据库字段的映射信息。

**主要属性：**
- `dbFieldName()`: 数据库字段名（必选）
- `dbType()`: 数据库字段类型（可选，默认为VARCHAR）
- `length()`: 字段长度（可选，默认为64）
- `isNull()`: 是否允许为空（可选，默认为true）
- `cnName()`: 中文名称（可选）

**使用示例：**
```java
@DBField(
    dbFieldName = "employee_id",
    dbType = ColType.VARCHAR,
    length = 20,
    cnName = "员工ID"
)
private String employeeId;
```

#### @Uid
用于标识记录的唯一ID。

#### @Pid
用于标识父记录的ID。

### 2.4 数据枚举注解

#### Enumstype接口
枚举类实现的基础接口，定义了getType()和getName()方法。

**使用示例：**
```java
public enum AttendanceType implements Enumstype {
    NORMAL("正常"),
    LATE("迟到"),
    EARLY_LEAVE("早退");
    
    private final String name;
    
    @Override
    public String getType() {
        return name();
    }
    
    @Override
    public String getName() {
        return name;
    }
}
```

#### IconEnumstype接口
扩展了Enumstype接口，增加了getImageClass()方法用于定义图标。

### 2.5 数据访问注解

#### Spring MVC注解
- `@RestController`: 标识RESTful控制器
- `@Service`: 标识服务层组件
- `@Repository`: 标识数据访问层组件
- `@RequestMapping`: 映射HTTP请求路径
- `@PostMapping`/`@GetMapping`: 映射POST/GET请求
- `@RequestBody`/`@ResponseBody`: 处理请求体和响应体

## 3. 注解使用规范

### 3.1 命名规范

1. **注解类命名**：采用驼峰命名法，以Annotation结尾（如@EntityMapping）
2. **枚举类命名**：采用驼峰命名法，清晰表达业务含义（如AttendanceType）
3. **属性命名**：采用驼峰命名法，语义明确（如primaryKeyStrategy）

### 3.2 组合使用规范

1. **服务类注解组合**：
   ```java
   @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
   @RestController
   @RequestMapping("/api")
   @Service
   public class DataService {
       // 实现
   }
   ```

2. **实体类注解组合**：
   ```java
   @EntityMapping(table = "users", primaryKey = "id")
   @DBTable(tableName = "users", primaryKey = "id")
   public class User {
       // 字段定义
   }
   ```

### 3.3 生命周期规范

1. **运行时保留**：所有自定义注解都应使用`@Retention(RetentionPolicy.RUNTIME)`
2. **目标元素**：明确指定注解可应用的元素类型（TYPE、FIELD、METHOD等）
3. **继承性**：根据需要决定是否使用`@Inherited`注解

## 4. 最佳实践

### 4.1 实体映射最佳实践

1. **明确主键策略**：根据业务需求选择合适的主键生成策略
2. **合理索引设计**：在@EntityMapping中定义必要的索引
3. **逻辑删除处理**：合理使用逻辑删除字段避免数据丢失

### 4.2 服务注解最佳实践

1. **统一服务类型**：API服务使用AggregationType.API，菜单服务使用AggregationType.MENU
2. **规范路径映射**：使用清晰的@RequestMapping路径
3. **正确返回类型**：使用ResultModel或其子类作为返回值

### 4.3 字段注解最佳实践

1. **字段类型匹配**：确保@DBField的dbType与Java字段类型匹配
2. **长度合理设置**：根据实际业务需求设置字段长度
3. **非空约束**：合理使用isNull属性控制字段约束