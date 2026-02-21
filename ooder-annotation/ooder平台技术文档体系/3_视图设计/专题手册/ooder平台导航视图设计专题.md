# ooder平台导航视图设计专题

## 目录

1. [概述](#1-概述)
2. [导航视图核心概念](#2-导航视图核心概念)
   - 2.1 导航视图的重要性
   - 2.2 导航视图与Tree的关系
   - 2.3 导航视图的分类
3. [NavTreeViewAnnotation树形导航](#3-navtreeviewannotation树形导航)
   - 3.1 核心特性
   - 3.2 注解属性
   - 3.3 实现机制
4. [NavFoldingTabsViewAnnotation折叠TAB导航](#4-navfoldingtabsviewannotation折叠tab导航)
   - 4.1 核心特性
   - 4.2 注解属性
   - 4.3 实现机制
5. [NavFoldingTreeViewAnnotation折叠树形导航](#5-navfoldingtreeviewannotation折叠树形导航)
   - 5.1 核心特性
   - 5.2 注解属性
   - 5.3 实现机制
6. [NavMTabsViewAnnotation多TAB导航](#6-navmtabsviewannotation多tab导航)
   - 6.1 核心特性
   - 6.2 注解属性
   - 6.3 实现机制
7. [导航视图设计规范](#7-导航视图设计规范)
   - 7.1 视图类型规范
   - 7.2 数据模型规范
   - 7.3 事件处理规范
8. [导航视图最佳实践](#8-导航视图最佳实践)
   - 8.1 性能优化
   - 8.2 用户体验
   - 8.3 数据一致性
9. [导航视图应用示例](#9-导航视图应用示例)
   - 9.1 考勤管理系统
   - 9.2 组织架构管理
   - 9.3 项目管理系统
10. [SVG导航视图](#10-svg导航视图)
   - 10.1 SVG视图组件体系
   - 10.2 动画与时间事件组件
   - 10.3 应用场景

## 1. 概述

在ooder平台中，导航视图是一组专门用于实现常用导航模式的注解和组件。这些导航视图以数据为中心，通过不同的注解实现树形导航（左树右面板）、折叠TAB导航、多TAB导航等多种常见的导航模式。

导航视图是ooder平台导航系统的重要组成部分，它们与Tree组件紧密相关，但提供了更高层次的抽象和更丰富的功能。

除了传统的导航视图外，ooder平台还提供了基于SVG的导航视图解决方案，适用于对展示效果要求较高的场景，如产品展示网站、转盘游戏等。关于SVG导航视图的详细信息，请参考[ooder平台SVG视图与动画组件专题手册](../7_组件专题/专题手册/ooder平台SVG视图与动画组件专题手册.md)。

## 2. 导航视图核心概念

### 2.1 导航视图的重要性

导航视图在ooder平台中具有重要地位：

1. **用户体验**：提供直观、易用的导航方式
2. **数据组织**：以数据驱动的方式组织和展示信息
3. **界面统一**：提供标准化的导航界面模式
4. **功能集成**：集成多种导航模式于统一框架

### 2.2 导航视图与Tree的关系

导航视图与Tree组件的关系：

1. **基础支撑**：导航视图以Tree组件为基础实现
2. **功能扩展**：在Tree基础上提供更丰富的导航功能
3. **界面抽象**：提供更高层次的界面抽象
4. **数据驱动**：基于数据结构实现动态导航

### 2.3 导航视图的分类

根据功能和界面特点，导航视图可分为：

1. **树形导航**：左树右面板的导航模式
2. **折叠TAB导航**：可折叠的TAB导航模式
3. **折叠树形导航**：可折叠的树形导航模式
4. **多TAB导航**：多TAB页的导航模式
5. **SVG导航**：基于SVG的图形化导航模式

这些导航视图在ooder平台的视图体系中发挥着重要作用，支持父子关系、顺序关系和并行关系等多种视图关系模式，为构建复杂的用户界面和业务流程提供了坚实的基础。

## 3. NavTreeViewAnnotation树形导航

### 3.1 核心特性

NavTreeViewAnnotation用于实现左树右面板的树形导航模式，具有以下核心特性：

1. **树形结构**：左侧显示树形导航结构
2. **内容面板**：右侧显示选中节点的详细内容
3. **数据驱动**：基于数据结构动态生成导航
4. **事件绑定**：支持节点选择事件处理

这种导航模式体现了视图间的父子关系，树形结构作为父视图控制内容面板子视图的显示和隐藏，数据传递通常从父视图流向子视图。

### 3.2 注解属性

``java
@NavTreeViewAnnotation(
    expression = "attendanceTree",
    dataUrl = "/attendance/navigation/tree/data",
    loadChildUrl = "/attendance/navigation/tree/children",
    rootId = "0",
    itemType = ResponsePathTypeEnum.TREEVIEW,
    autoSave = false
)
```

**核心属性说明：**
- expression：表达式，用于标识导航视图实例
- dataUrl：根节点数据加载URL
- loadChildUrl：子节点数据加载URL
- rootId：根节点ID
- itemType：响应数据类型
- autoSave：是否自动保存

### 3.3 实现机制

``java
/**
 * 考勤导航视图类
 * 展示如何使用NavTreeViewAnnotation注解实现导航功能
 */
@CustomClass(
    caption = "考勤导航视图",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.NAVIGATION_VIEW
)
public class AttendanceNavigationView {
    
    /**
     * 树形导航视图
     * 使用NavTreeViewAnnotation注解实现左树右面板的导航效果
     */
    @NavTreeViewAnnotation(
        expression = "attendanceTree",
        dataUrl = "/attendance/navigation/tree/data",
        loadChildUrl = "/attendance/navigation/tree/children",
        rootId = "0"
    )
    public void treeNavigationView() {
        // 树形导航视图逻辑
    }
}

@Service
@RestController
@RequestMapping("/attendance/navigation/tree")
public class AttendanceNavigationTreeService {
    
    /**
     * 获取树形导航数据
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},   
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @GetMapping("/data")
    @ResponseBody
    public ListResultModel<List<TreeNode>> getTreeData() {
        ListResultModel<List<TreeNode>> resultModel = new ListResultModel<>();
        
        try {
            List<TreeNode> nodes = new ArrayList<>();
            
            // 添加根节点
            TreeNode root = new TreeNode();
            root.setId("0");
            root.setPid("-1");
            root.setName("考勤管理系统");
            root.setType("root");
            nodes.add(root);
            
            // 添加子节点
            TreeNode checkInNode = new TreeNode();
            checkInNode.setId("1");
            checkInNode.setPid("0");
            checkInNode.setName("考勤签到");
            checkInNode.setType("module");
            nodes.add(checkInNode);
            
            resultModel.setData(nodes);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorListResultModel封装的错误信息
            ErrorListResultModel<List<TreeNode>> errorResult = new ErrorListResultModel<>(e.getMessage());
            errorResult.setErrcode(1000);
            errorResult.setRequestStatus(-1);
            return errorResult;
        }
        
        return resultModel;
    }
    
    /**
     * 获取子节点数据
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},   
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @GetMapping("/children")
    @ResponseBody
    public ListResultModel<List<TreeNode>> getChildren(String parentId) {
        ListResultModel<List<TreeNode>> resultModel = new ListResultModel<>();
        
        try {
            List<TreeNode> nodes = new ArrayList<>();
            
            // 根据父节点ID返回相应的子节点
            if ("1".equals(parentId)) {
                // 考勤签到的子节点
                TreeNode dailyCheckIn = new TreeNode();
                dailyCheckIn.setId("11");
                dailyCheckIn.setPid("1");
                dailyCheckIn.setName("日常签到");
                dailyCheckIn.setType("submodule");
                nodes.add(dailyCheckIn);
            }
            
            resultModel.setData(nodes);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorListResultModel封装的错误信息
            ErrorListResultModel<List<TreeNode>> errorResult = new ErrorListResultModel<>(e.getMessage());
            errorResult.setErrcode(1000);
            errorResult.setRequestStatus(-1);
            return errorResult;
        }
        
        return resultModel;
    }
}
```

## 4. NavFoldingTabsViewAnnotation折叠TAB导航

### 4.1 核心特性

NavFoldingTabsViewAnnotation用于实现可折叠的TAB导航模式，具有以下核心特性：

1. **TAB导航**：以TAB页形式组织内容
2. **折叠功能**：支持TAB区域的折叠和展开
3. **自动保存**：支持导航状态的自动保存
4. **动态加载**：支持TAB内容的动态加载

这种导航模式支持顺序关系和并行关系，多个TAB页可以按照一定的顺序依次显示（顺序关系），也可以同时存在和操作（并行关系）。

### 4.2 注解属性

``java
@NavFoldingTabsViewAnnotation(
    expression = "attendanceTabs",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TABS,
    saveUrl = "/attendance/navigation/tabs/save"
)
```

**核心属性说明：**
- expression：表达式，用于标识导航视图实例
- autoSave：是否自动保存导航状态
- itemType：响应数据类型
- saveUrl：状态保存URL

### 4.3 实现机制

``java
/**
 * 考勤导航视图类
 */
public class AttendanceNavigationView {
    
    /**
     * 折叠式TAB导航视图
     * 使用NavFoldingTabsViewAnnotation注解实现折叠式TAB导航效果
     */
    @NavFoldingTabsViewAnnotation(
        expression = "attendanceTabs",
        autoSave = true
    )
    public void foldingTabsNavigationView() {
        // 折叠式TAB导航视图逻辑
    }
}

@Service
@RestController
@RequestMapping("/attendance/navigation/tabs")
public class AttendanceNavigationTabsService {
    
    /**
     * 获取TAB导航数据
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},   
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @GetMapping("/data")
    @ResponseBody
    public ListResultModel<List<TabItem>> getTabData() {
        ListResultModel<List<TabItem>> resultModel = new ListResultModel<>();
        
        try {
            List<TabItem> tabs = new ArrayList<>();
            
            // 添加TAB项
            TabItem checkInTab = new TabItem();
            checkInTab.setId("1");
            checkInTab.setTitle("考勤签到");
            checkInTab.setIcon("fas fa-clock");
            tabs.add(checkInTab);
            
            TabItem queryTab = new TabItem();
            queryTab.setId("2");
            queryTab.setTitle("考勤查询");
            queryTab.setIcon("fas fa-search");
            tabs.add(queryTab);
            
            resultModel.setData(tabs);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorListResultModel封装的错误信息
            ErrorListResultModel<List<TabItem>> errorResult = new ErrorListResultModel<>(e.getMessage());
            errorResult.setErrcode(1000);
            errorResult.setRequestStatus(-1);
            return errorResult;
        }
        
        return resultModel;
    }
}
```

## 5. NavFoldingTreeViewAnnotation折叠树形导航

### 5.1 核心特性

NavFoldingTreeViewAnnotation用于实现可折叠的树形导航模式，具有以下核心特性：

1. **树形结构**：以树形结构组织导航内容
2. **折叠功能**：支持树形区域的折叠和展开
3. **数据驱动**：基于数据结构动态生成导航
4. **状态保持**：保持用户的导航状态

### 5.2 注解属性

``java
@NavFoldingTreeViewAnnotation(
    expression = "attendanceFoldingTree",
    dataUrl = "/attendance/navigation/tree/data",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TREEVIEW
)
```

**核心属性说明：**
- expression：表达式，用于标识导航视图实例
- dataUrl：树形数据加载URL
- autoSave：是否自动保存导航状态
- itemType：响应数据类型

### 5.3 实现机制

``java
/**
 * 考勤导航视图类
 */
public class AttendanceNavigationView {
    
    /**
     * 折叠式树形导航视图
     * 使用NavFoldingTreeViewAnnotation注解实现折叠式树形导航效果
     */
    @NavFoldingTreeViewAnnotation(
        expression = "attendanceFoldingTree",
        dataUrl = "/attendance/navigation/tree/data",
        autoSave = true
    )
    public void foldingTreeView() {
        // 折叠式树形导航视图逻辑
    }
}
```

## 6. NavMTabsViewAnnotation多TAB导航

### 6.1 核心特性

NavMTabsViewAnnotation用于实现多TAB页的导航模式，具有以下核心特性：

1. **多TAB页**：支持多个TAB页并行显示
2. **动态内容**：支持TAB内容的动态加载
3. **状态管理**：管理多个TAB页的状态
4. **事件处理**：处理TAB切换等事件

这种导航模式主要体现并行关系，多个TAB页可以同时存在和操作，彼此相对独立但又存在一定的关联。

### 6.2 注解属性

``java
@NavMTabsViewAnnotation(
    expression = "attendanceMTabs",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TABS,
    dataUrl = "/attendance/navigation/mtabs/data"
)
```

**核心属性说明：**
- expression：表达式，用于标识导航视图实例
- autoSave：是否自动保存导航状态
- itemType：响应数据类型
- dataUrl：数据加载URL

### 6.3 实现机制

``java
/**
 * 考勤导航视图类
 */
public class AttendanceNavigationView {
    
    /**
     * 多TAB导航视图
     * 使用NavMTabsViewAnnotation注解实现多TAB导航效果
     */
    @NavMTabsViewAnnotation(
        expression = "attendanceMTabs",
        autoSave = true
    )
    public void mTabsView() {
        // 多TAB导航视图逻辑
    }
}
```

## 7. 导航视图设计规范

### 7.1 视图类型规范

导航视图需要与ModuleViewType正确匹配：

1. **NavTreeViewAnnotation**：对应ModuleViewType.NAVTREECONFIG
2. **NavFoldingTabsViewAnnotation**：对应ModuleViewType.NAVFOLDINGTABSCONFIG
3. **NavFoldingTreeViewAnnotation**：对应ModuleViewType.NAVFOLDINGTREECONFIG
4. **NavMTabsViewAnnotation**：对应ModuleViewType.NAVTABSCONFIG

### 7.2 数据模型规范

导航视图的数据模型需要遵循特定规范：

```java
// 树形节点数据模型
public class TreeNode {
    private String id;
    private String pid;
    private String name;
    private String type;
    private String icon;
    // getters and setters
}

// TAB项数据模型
public class TabItem {
    private String id;
    private String title;
    private String icon;
    // getters and setters
}
```

在设计导航视图时，需要考虑视图间的父子关系、顺序关系和并行关系，合理设计数据模型以支持这些关系。

### 7.3 事件处理规范

导航视图的事件处理需要遵循规范：

1. **节点选择事件**：处理树形节点的选择
2. **TAB切换事件**：处理TAB页的切换
3. **折叠展开事件**：处理折叠和展开操作
4. **数据加载事件**：处理动态数据加载

事件处理应确保视图间关系的正确维护，特别是在父子关系中的数据传递和状态同步。

## 8. 导航视图最佳实践

### 8.1 性能优化

1. **懒加载**：子节点数据按需加载
2. **缓存机制**：合理缓存已加载的数据
3. **虚拟滚动**：对于大量数据使用虚拟滚动
4. **数据分页**：对大数据集进行分页处理

### 8.2 用户体验

1. **响应式设计**：适配不同屏幕尺寸
2. **状态反馈**：提供清晰的操作状态反馈
3. **键盘导航**：支持键盘快捷键操作
4. **动画效果**：适当的展开/折叠动画

### 8.3 数据一致性

1. **事务处理**：导航操作使用事务保证数据一致性
2. **并发控制**：处理多用户并发操作
3. **数据验证**：操作前进行数据验证
4. **错误恢复**：提供操作失败的恢复机制

在处理视图间的父子关系、顺序关系和并行关系时，需要特别注意数据一致性，确保父视图与子视图间的数据传递正确，顺序关系中的数据流向准确，以及并行关系中的数据共享和状态同步。

## 9. 导航视图应用示例

### 9.1 考勤管理系统

```java
/**
 * 考勤导航视图类
 */
@CustomClass(
    caption = "考勤导航视图",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.NAVIGATION_VIEW
)
public class AttendanceNavigationView {
    
    /**
     * 树形导航视图
     */
    @NavTreeViewAnnotation(
        expression = "attendanceTree",
        dataUrl = "/attendance/navigation/tree/data",
        loadChildUrl = "/attendance/navigation/tree/children",
        rootId = "0"
    )
    public void treeNavigationView() {
        // 树形导航视图逻辑
    }
    
    /**
     * 折叠式TAB导航视图
     */
    @NavFoldingTabsViewAnnotation(
        expression = "attendanceTabs",
        autoSave = true
    )
    public void foldingTabsNavigationView() {
        // 折叠式TAB导航视图逻辑
    }
}
```

### 9.2 组织架构管理

```java
/**
 * 组织架构导航视图类
 */
@CustomClass(
    caption = "组织架构导航",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.NAVIGATION_VIEW
)
public class OrganizationNavigationView {
    
    /**
     * 树形导航视图
     */
    @NavTreeViewAnnotation(
        expression = "orgTree",
        dataUrl = "/organization/navigation/tree/data",
        loadChildUrl = "/organization/navigation/tree/children",
        rootId = "0"
    )
    public void treeNavigationView() {
        // 组织架构树形导航逻辑
    }
}
```

### 9.3 项目管理系统

``java
/**
 * 项目管理导航视图类
 */
@CustomClass(
    caption = "项目管理导航",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.NAVIGATION_VIEW
)
public class ProjectNavigationView {
    
    /**
     * 多TAB导航视图
     */
    @NavMTabsViewAnnotation(
        expression = "projectTabs",
        autoSave = true
    )
    public void mTabsView() {
        // 项目管理多TAB导航逻辑
    }
}
```

通过以上设计，ooder平台的导航视图能够很好地支持各种导航需求，为用户提供直观、易用的导航体验。

## 10. SVG导航视图

### 10.1 SVG视图组件体系

ooder平台提供了丰富的SVG矢量图形支持，专门用于创建交互量不大但对展示效果要求高的应用场景。SVG导航视图基于SVGPaperFormAnnotation注解构建，支持丰富的图形属性配置和事件处理机制。

SVG视图组件体系包括：
- **基础图形组件**：圆形、矩形、路径、图片、文本等
- **组合图形组件**：增强版的基础图形组件，提供更多属性配置
- **特殊图形组件**：分组组件、连接器组件等

### 10.2 动画与时间事件组件

SVG导航视图支持丰富的动画效果和时间事件处理：
- **AnimBinder动画绑定器**：为SVG元素添加动画效果
- **CustomAnimType动画类型**：提供多种预定义动画效果
- **TimerAnnotation计时器**：实现定时触发事件
- **SVGEvent事件处理**：处理用户交互事件

### 10.3 应用场景

SVG导航视图适用于以下场景：
- **产品展示网站**：高质量的矢量图形展示
- **转盘游戏**：精确的图形控制和流畅的旋转动画
- **流程图展示**：灵活的图形组合和连接线支持
- **数据可视化**：丰富的图表和图形展示

SVG导航视图可以作为独立的视图组件，也可以与其他导航视图结合使用，形成父子关系或并行关系，为用户提供更加生动、直观的导航体验。
