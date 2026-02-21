# ooder平台视图聚合关系高级组件

## 1. 概述

视图聚合关系高级组件是ooder平台中用于处理复杂数据模型组合展现的核心组件。通过聚合视图设计，可以实现具有关联关系（1:1, 1:n, n:n）的数据模型的整合展示与动态加载。

## 2. 聚合视图设计规范

### 2.1 核心设计理念
以Nav***View为核心的聚合视图类，通过TabsAnnotation定义TAB结构，将视图通用外观属性抽取为TabItem、TreeItem或GroupItem等接口实现的枚举类作为导航入口。

### 2.2 bindClass核心作用
bindClass作为解耦的核心干节点，在聚合视图中起到关键作用：
- 实现视图与服务的解耦
- 作为视图聚合服务类的核心属性
- 必须包含视图入口方法

### 2.3 主视图区域设计
主视图区域设为可切换面板，支持根据节点父子关系递归创建嵌套结构。

## 3. Tabs聚合组件

### 3.1 TabsAnnotation核心注解
TabsAnnotation是Tabs聚合组件的核心，定义了聚合视图的基本行为和属性。

**核心属性：**
- barLocation：控制标签栏的显示位置
- sideBarStatus：控制侧边栏的展开状态
- iniFold：控制Tabs初始化时是否折叠
- bindTypes：可绑定的组件类型

### 3.2 TabsItem聚合实现
通过TabsItem实现聚合视图的子项：

```java
@TabsAnnotation(
    caption = "聚合视图",
    barLocation = BarLocationType.top
)
public class AggregateView {
    @TabsItemsAnnotation(
        tabItems = AggregateViewType.class
    )
    private List<TabItem> tabItems;
    
    // Getters and setters
}

public enum AggregateViewType implements TabItem {
    BASIC_INFO("基本信息", "fas fa-info-circle", BasicInfoService.class),
    DETAIL_INFO("详细信息", "fas fa-list", DetailInfoService.class);
    
    private final String name;
    private final String imageClass;
    private final Class[] bindClass;
    
    AggregateViewType(String name, String imageClass, Class... bindClass) {
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

## 4. Group聚合组件

### 4.1 GroupFormAnnotation注解
GroupFormAnnotation用于定义分组表单聚合视图：

```java
@GroupFormAnnotation(
    caption = "分组信息",
    customService = {GroupInfoService.class}
)
public class GroupInfoView {
    // 分组字段定义
}
```

### 4.2 GroupChild聚合实现
通过GroupChild实现分组视图的子项：

```java
public class GroupChildItem implements GroupChild {
    private String name;
    private String imageClass;
    private Class[] bindClass;
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getImageClass() { return imageClass; }
    
    @Override
    public Class[] getBindClass() { return bindClass; }
}
```

## 5. Tree聚合组件

### 5.1 TreeViewAnnotation注解
TreeViewAnnotation用于定义树形聚合视图：

```java
@TreeViewAnnotation(
    caption = "树形结构",
    customService = {TreeInfoService.class}
)
public class TreeInfoView {
    // 树形字段定义
}
```

### 5.2 TreeItem聚合实现
通过TreeItem实现树形视图的节点：

```java
public class TreeNodeItem implements TreeItem {
    private String name;
    private String imageClass;
    private Class[] bindClass;
    private List<TreeItem> children;
    
    @Override
    public String getName() { return name; }
    
    @Override
    public String getImageClass() { return imageClass; }
    
    @Override
    public Class[] getBindClass() { return bindClass; }
    
    @Override
    public List<TreeItem> getChildren() { return children; }
    
    @Override
    public boolean hasChildren() { return children != null && !children.isEmpty(); }
}
```

## 6. 聚合视图实现模式

### 6.1 枚举驱动模式
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

### 6.2 动态加载模式
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

## 7. 最佳实践

### 7.1 设计原则
1. **解耦原则**：通过bindClass实现视图与服务的解耦
2. **复用原则**：设计可复用的聚合组件
3. **性能原则**：合理使用动态加载和懒加载机制

### 7.2 实现规范
1. **注解使用规范**：正确使用聚合视图相关注解
2. **接口实现规范**：正确实现TabItem、TreeItem、GroupItem等接口
3. **数据一致性规范**：确保聚合视图中数据的一致性

### 7.3 注意事项
1. 避免过深的嵌套结构，影响性能和用户体验
2. 合理设计聚合视图的导航结构
3. 确保聚合视图入口具有Web可访问性
4. 正确处理聚合视图间的数据传递和状态同步