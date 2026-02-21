# ooder平台聚合视图设计规范手册

## 目录

1. [概述](#1-概述)
   - 1.1 聚合视图定义
   - 1.2 聚合视图作用
   - 1.3 聚合视图设计原则
2. [聚合视图核心概念](#2-聚合视图核心概念)
   - 2.1 核心设计理念
   - 2.2 bindClass核心作用
   - 2.3 主视图区域设计
3. [聚合视图类型](#3-聚合视图类型)
   - 3.1 Tabs聚合视图
   - 3.2 Group聚合视图
   - 3.3 Tree聚合视图
   - 3.4 Wizard聚合视图（建议新增）
   - 3.5 Dashboard聚合视图（建议新增）
4. [聚合视图实现机制](#4-聚合视图实现机制)
   - 4.1 枚举驱动模式
   - 4.2 动态加载模式
   - 4.3 递归嵌套实现
5. [聚合视图设计规范](#5-聚合视图设计规范)
   - 5.1 注解使用规范
   - 5.2 接口实现规范
   - 5.3 数据一致性规范
6. [聚合视图最佳实践](#6-聚合视图最佳实践)
   - 6.1 设计原则
   - 6.2 实现规范
   - 6.3 性能优化
7. [聚合视图应用示例](#7-聚合视图应用示例)
   - 7.1 考勤管理系统
   - 7.2 组织架构管理
   - 7.3 项目管理系统
8. [注意事项](#8-注意事项)

## 1. 概述

### 1.1 聚合视图定义

聚合视图是ooder平台中用于处理复杂数据模型组合展现的核心组件。通过聚合视图设计，可以实现具有关联关系（1:1, 1:n, n:n）的数据模型的整合展示与动态加载。

### 1.2 聚合视图作用

聚合视图在ooder平台中具有重要作用：
1. **数据整合**：整合多个相关数据模型，提供统一的展示界面
2. **动态加载**：支持按需加载数据，提高系统性能
3. **导航支持**：提供多种导航模式，提升用户体验
4. **解耦设计**：通过bindClass实现视图与服务的解耦

### 1.3 聚合视图设计原则

1. **解耦原则**：通过bindClass实现视图与服务的解耦
2. **复用原则**：设计可复用的聚合组件
3. **性能原则**：合理使用动态加载和懒加载机制
4. **一致性原则**：保持聚合视图间数据的一致性

## 2. 聚合视图核心概念

### 2.1 核心设计理念

以Nav***View为核心的聚合视图类，通过TabsAnnotation定义TAB结构，将视图通用外观属性抽取为TabItem、TreeItem或GroupItem等接口实现的枚举类作为导航入口。

### 2.2 bindClass核心作用

bindClass作为解耦的核心干节点，在聚合视图中起到关键作用：
- 实现视图与服务的解耦
- 作为视图聚合服务类的核心属性
- 必须包含视图入口方法

### 2.3 主视图区域设计

主视图区域设为可切换面板，支持根据节点父子关系递归创建嵌套结构，实现如考勤记录、请假记录等关联数据的整合展示与动态加载。

## 3. 聚合视图类型

### 3.1 Tabs聚合视图

Tabs聚合视图通过TabsAnnotation定义，是最常用的聚合视图类型。

**核心特征：**
- 通过TabsAnnotation定义TAB结构
- 将视图通用外观属性抽取为TabItem等接口实现
- 主视图区域设为可切换面板

**示例：**
```java
@TabsAnnotation(
    caption = "考勤聚合信息",
    autoSave = true,
    barLocation = BarLocationType.top
)
public class AttendanceAggregateView {
    // TAB项定义
}
```

### 3.2 Group聚合视图

Group聚合视图通过GroupFormAnnotation定义，用于分组展示相关信息。

**核心特征：**
- 通过GroupFormAnnotation定义分组结构
- 支持分组折叠和展开
- 适用于逻辑相关的数据分组展示

### 3.3 Tree聚合视图

Tree聚合视图通过TreeViewAnnotation定义，用于展示层级结构数据。

**核心特征：**
- 通过TreeViewAnnotation定义树形结构
- 支持节点展开和折叠
- 适用于组织架构、分类目录等层级数据展示

### 3.4 Wizard聚合视图（建议新增）

Wizard聚合视图用于引导用户完成多步骤操作的向导式聚合视图。

**核心特征：**
- 通过WizardAnnotation定义向导结构
- 支持步骤导航和进度显示
- 每步操作可独立验证和保存

**适用场景：**
- 复杂业务流程引导
- 新用户注册向导
- 系统配置向导

### 3.5 Dashboard聚合视图（建议新增）

Dashboard聚合视图用于展示关键指标和重要信息的仪表板式聚合视图。

**核心特征：**
- 通过DashboardAnnotation定义仪表板结构
- 支持多种图表和数据展示组件
- 支持自定义布局和组件排列

**适用场景：**
- 数据统计和分析展示
- 系统监控面板
- 业务指标展示

## 4. 聚合视图实现机制

### 4.1 枚举驱动模式

使用枚举类实现聚合视图的导航入口：

```java
public enum NavigationType implements TabItem {
    // 基础信息节点
    BASIC_INFO("基本信息", "fas fa-info", BasicInfoService.class),
    
    // 详细信息节点（可能包含子节点）
    DETAIL_INFO("详细信息", "fas fa-list", DetailInfoService.class) {
        @Override
        public List<TabItem> getChildren() {
            return Arrays.asList(
                new SubItem("联系信息", "fas fa-phone", ContactService.class),
                new SubItem("地址信息", "fas fa-map", AddressService.class)
            );
        }
        
        @Override
        public boolean hasChildren() {
            return true;
        }
    };
    
    // 枚举实现
}
```

### 4.2 动态加载模式

支持动态加载聚合视图内容：

```java
@TabsAnnotation(
    caption = "动态聚合视图",
    barLocation = BarLocationType.top,
    dynLoad = true
)
public class DynamicAggregateView {
    // 动态加载实现
}
```

### 4.3 递归嵌套实现

对于复杂的递归嵌套场景，平台通过以下方式实现：

1. **枚举方式实现递归**
```java
public enum TreeItems implements TreeItem {
    NODE1("节点1", Node1Service.class),
    NODE2("节点2", Node2Service.class);
    
    private String name;
    private Class serviceClass;
    
    TreeItems(String name, Class serviceClass) {
        this.name = name;
        this.serviceClass = serviceClass;
    }
}
```

2. **递归引用特性**
当主干节点出现递归引用时，bindClass作为核心节点属性。

## 5. 聚合视图设计规范

### 5.1 注解使用规范

1. **TabsAnnotation使用规范**
   - 正确配置caption、autoSave、barLocation等属性
   - 合理使用dynLoad属性实现动态加载
   - 遵循命名规范，使用AggregateView作为类名后缀

2. **GroupFormAnnotation使用规范**
   - 正确配置caption、customService等属性
   - 合理使用分组折叠特性

3. **TreeViewAnnotation使用规范**
   - 正确配置树形结构相关属性
   - 合理使用节点展开和折叠特性

### 5.2 接口实现规范

1. **TabItem接口实现规范**
   - 正确实现getName、getImageClass、getBindClass等方法
   - 合理处理子节点的getChildren和hasChildren方法

2. **TreeItem接口实现规范**
   - 正确实现树形节点相关方法
   - 合理处理节点状态（展开、折叠、选中等）

3. **GroupChild接口实现规范**
   - 正确实现分组子项相关方法

### 5.3 数据一致性规范

1. **环境变量共享**
   - 在聚合类视图中允许添加@Pid和@Uid属性，作为所有子级的共享环境变量
   - 确保父子视图间的数据传递正确

2. **状态同步**
   - 合理使用autoSave属性确保数据及时保存
   - 正确处理聚合视图中各子项间的数据一致性

## 6. 聚合视图最佳实践

### 6.1 设计原则

1. **解耦原则**：通过bindClass实现视图与服务的解耦
2. **复用原则**：设计可复用的聚合组件
3. **性能原则**：合理使用动态加载和懒加载机制
4. **用户体验原则**：提供直观、易用的导航方式

### 6.2 实现规范

1. **注解使用规范**：正确使用聚合视图相关注解
2. **接口实现规范**：正确实现TabItem、TreeItem、GroupItem等接口
3. **数据一致性规范**：确保聚合视图中数据的一致性
4. **命名规范**：遵循聚合视图命名规范，使用AggregateView作为类名后缀

### 6.3 性能优化

1. **懒加载**：使用懒加载（lazyLoad）减少初始加载时间
2. **自动保存**：合理使用自动保存（autoSave）确保数据及时保存
3. **动态销毁**：使用适当的动态销毁策略优化内存使用
4. **缓存机制**：合理使用缓存机制提高响应速度

## 7. 聚合视图应用示例

### 7.1 考勤管理系统

```java
/**
 * 考勤聚合视图类
 */
@TabsAnnotation(
    caption = "考勤聚合信息",
    autoSave = true,
    barLocation = BarLocationType.top
)
public class AttendanceAggregateView {
    
    @TabsItemsAnnotation(
        tabItems = AttendanceTabItems.class
    )
    private List<TabItem> tabItems;
    
    // Getters and setters
}

public enum AttendanceTabItems implements TabItem {
    CHECK_IN("考勤签到", "fas fa-clock", AttendanceCheckInService.class),
    RECORD("考勤记录", "fas fa-history", AttendanceRecordService.class),
    STATISTICS("统计分析", "fas fa-chart-bar", AttendanceStatisticsService.class);
    
    private final String name;
    private final String imageClass;
    private final Class[] bindClass;
    
    AttendanceTabItems(String name, String imageClass, Class... bindClass) {
        this.name = name;
        this.imageClass = imageClass;
        this.bindClass = bindClass;
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getImageClass() { return imageClass; }
    
    @Override
    public Class[] getBindClass() { return bindClass; }
}
```

### 7.2 组织架构管理

```java
/**
 * 组织架构聚合视图类
 */
@TreeViewAnnotation(
    caption = "组织架构",
    customService = {OrganizationService.class}
)
public class OrganizationAggregateView {
    
    // 树形结构定义
}
```

### 7.3 项目管理系统

```java
/**
 * 项目管理聚合视图类
 */
@GroupFormAnnotation(
    caption = "项目信息",
    customService = {ProjectService.class}
)
public class ProjectAggregateView {
    
    // 分组信息定义
}
```

## 8. 注意事项

1. **避免过深的嵌套结构**：过深的嵌套结构会影响性能和用户体验
2. **合理设计导航结构**：聚合视图的导航结构应清晰明确
3. **确保Web可访问性**：聚合视图入口必须具有Web可访问性
4. **正确处理数据传递**：正确处理聚合视图间的数据传递和状态同步
5. **遵循命名规范**：聚合视图类名应以AggregateView结尾
6. **异常处理**：在聚合视图中使用ErrorResultModel时，需要设置错误描述errdes和错误码errcode，其中errcode默认设置为1000，errdes设置为异常消息e.getMessage()，并将ErrorResultModel赋值给结果对象。