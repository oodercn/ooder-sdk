# ooder平台树形结构设计专题

## 目录

1. [概述](#1-概述)
2. [Tree核心概念](#2-tree核心概念)
   - 2.1 Tree在ooder中的重要性
   - 2.2 Tree与多级数据的关系
   - 2.3 Tree在导航系统中的作用
3. [TreeBar多级导航](#3-treebar多级导航)
   - 3.1 TreeBar概述
   - 3.2 TreeBar核心特性
   - 3.3 TreeBar注解体系
   - 3.4 TreeBar实现机制
   - 3.5 TreeBar事件处理
4. [TreeView树形视图](#4-treeview树形视图)
   - 4.1 TreeView概述
   - 4.2 TreeView与TreeBar的关系
   - 4.3 TreeView注解体系
   - 4.4 TreeView实现机制
   - 4.5 TreeView事件处理
5. [Tree组件设计规范](#5-tree组件设计规范)
   - 5.1 Tree节点设计
   - 5.2 Tree数据模型
   - 5.3 Tree事件处理
   - 5.4 Tree样式配置
6. [Tree与页面导航](#6-tree与页面导航)
   - 6.1 多级页面嵌套导航
   - 6.2 数据驱动的导航系统
   - 6.3 导航与视图的联动
7. [Tree最佳实践](#7-tree最佳实践)
   - 7.1 性能优化
   - 7.2 用户体验
   - 7.3 数据一致性
8. [Tree应用示例](#8-tree应用示例)
   - 8.1 组织架构管理
   - 8.2 分类目录管理
   - 8.3 文件系统管理

## 1. 概述

在ooder平台中，树形结构(Tree)是设计的核心组件之一。多级数据是数据的常态，而多级页面嵌套导航也依赖树形视图。更重要的是，ooder平台构建了一个以数据为中心的多级数据视图导航系统。

Tree不仅用于展示具有层级关系的数据，还承担着导航功能。通过TreeBar多级导航和TreeView树形视图的协同工作，ooder平台实现了强大的数据管理和页面导航能力。

## 2. Tree核心概念

### 2.1 Tree在ooder中的重要性

Tree是ooder平台的核心组件，其重要性体现在以下几个方面：

1. **数据组织**：Tree是组织和展示多级数据的最自然方式
2. **导航支撑**：Tree为多级页面嵌套导航提供基础支撑
3. **用户体验**：Tree提供了直观、易用的数据浏览和操作方式
4. **系统集成**：Tree与ooder平台的其他组件无缝集成

### 2.2 Tree与多级数据的关系

在现实业务场景中，多级数据是常态：
- 组织架构：公司→部门→小组→员工
- 分类目录：大类→子类→细类
- 文件系统：根目录→子目录→文件
- 产品分类：品类→类别→型号

ooder平台通过Tree组件完美支持这些多级数据结构。

### 2.3 Tree在导航系统中的作用

Tree在ooder导航系统中发挥关键作用：
1. **TreeBar多级导航**：提供左侧导航树，支持多级页面跳转
2. **TreeView树形视图**：展示数据的层级关系，同时兼具导航功能
3. **数据驱动导航**：基于Tree的数据结构实现动态导航

## 3. TreeBar多级导航

### 3.1 TreeBar概述

TreeBar是ooder平台中的多级导航组件，通常显示在页面左侧，为用户提供层级化的导航菜单。TreeBar是[ComponentType.TREEBAR](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ui/ComponentType.java#L227-L227)类型的组件，具有Tree的所有特性，同时专门用于导航功能。

### 3.2 TreeBar核心特性

1. **层级导航**：支持无限层级的节点导航
2. **节点展开/折叠**：支持节点的展开和折叠操作
3. **节点选择**：支持节点的选择和高亮显示
4. **动态加载**：支持节点数据的动态加载
5. **状态保持**：保持用户的导航状态

### 3.3 TreeBar注解体系

```java
@TreeAnnotation(
    customService = {OrganizationTreeBarService.class},
    showRoot = true,
    selMode = SelModeType.single,
    bindTypes = {ComponentType.TREEBAR, ComponentType.TREEVIEW}
)
public class OrganizationTreeBarView {
    // TreeBar视图定义
}
```

**核心属性说明：**
- customService：关联的自定义服务类
- showRoot：是否显示根节点
- selMode：选择模式（单选、多选等）
- bindTypes：可绑定的组件类型

### 3.4 TreeBar实现机制

```java
@Service
@RestController
@RequestMapping("/api/treebar/organization")
public class OrganizationTreeBarService {
    
    @APIEventAnnotation(
        autoRun = true,
        customRequestData = {RequestPathEnum.CTX},
        customResponseData = {ResponsePathEnum.TREEVIEW}
    )
    @GetMapping("/load")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public TreeResultModel<OrganizationNode> loadTreeData() {
        // 加载组织架构树数据
        List<OrganizationNode> nodes = organizationService.loadOrganizationTree();
        
        TreeResultModel<OrganizationNode> result = new TreeResultModel<>();
        result.setData(nodes);
        return result;
    }
    
    @APIEventAnnotation(
        bindTreeEvent = {TreeViewEventEnum.onClick},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.REDIRECT}
    )
    @PostMapping("/select")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<Boolean> onNodeSelect(@RequestBody TreeNodeSelectionData data) {
        // 处理节点选择事件
        String selectedNodeId = data.getSelectedNodeId();
        
        // 根据选择的节点跳转到相应页面
        // 这里可以实现页面跳转逻辑
        return ResultModel.success(true);
    }
}
```

### 3.5 TreeBar事件处理

TreeBar支持丰富的事件处理机制，通过[TreeViewEventEnum](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/event/TreeViewEventEnum.java#L3-L77)枚举定义了多种事件类型：

```java
public enum TreeViewEventEnum implements EventKey {
    // 基础事件
    onClick("onClick"),           // 点击事件
    onDblclick("onDblclick"),     // 双击事件
    onContextmenu("onContextmenu"), // 右键菜单事件
    
    // 节点操作事件
    onGetContent("onGetContent"), // 获取内容事件
    onItemSelected("onItemSelected"), // 节点选择事件
    beforeFold("beforeFold"),     // 折叠前事件
    beforeExpand("beforeExpand"), // 展开前事件
    afterFold("afterFold"),       // 折叠后事件
    afterExpand("afterExpand"),   // 展开后事件
    
    // 拖拽事件
    onDrop("onDrop"),             // 拖拽放置事件
    onDragEnter("onDragEnter"),   // 拖拽进入事件
    onDragLeave("onDragLeave"),   // 拖拽离开事件
    
    // 命令事件
    onCmd("onCmd");               // 命令事件
}
```

## 4. TreeView树形视图

### 4.1 TreeView概述

TreeView是ooder平台中的树形数据展示组件，用于展示具有层级关系的数据。TreeView是[ComponentType.TREEVIEW](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ui/ComponentType.java#L228-L228)类型的组件，继承自TreeBar的功能，主要用于数据展示，但也可以兼具导航功能。

### 4.2 TreeView与TreeBar的关系

TreeView与TreeBar的关系：
1. **组件继承**：TreeView继承自TreeBar，具有TreeBar的所有功能
2. **功能扩展**：TreeView在TreeBar的基础上增加了数据展示功能
3. **导航兼容**：TreeView可以兼具导航功能
4. **数据驱动**：TreeView更注重数据的展示和操作

### 4.3 TreeView注解体系

```java
@TreeAnnotation(
    customService = {OrganizationTreeViewService.class},
    showRoot = true,
    selMode = SelModeType.multi,
    bindTypes = {ComponentType.TREEVIEW}
)
@TreeViewAnnotation(
    itemType = ResponsePathTypeEnum.TREEVIEW
)
public class OrganizationTreeView {
    // TreeView视图定义
}
```

**扩展属性说明：**
- itemType：指定响应数据类型

### 4.4 TreeView实现机制

```java
@Service
@RestController
@RequestMapping("/api/treeview/organization")
public class OrganizationTreeViewService {
    
    @APIEventAnnotation(
        autoRun = true,
        customRequestData = {RequestPathEnum.CTX},
        customResponseData = {ResponsePathEnum.TREEVIEW}
    )
    @GetMapping("/load")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public TreeResultModel<OrganizationNode> loadTreeData() {
        // 加载组织架构树数据
        List<OrganizationNode> nodes = organizationService.loadOrganizationTree();
        
        TreeResultModel<OrganizationNode> result = new TreeResultModel<>();
        result.setData(nodes);
        return result;
    }
    
    @APIEventAnnotation(
        bindTreeEvent = {TreeViewEventEnum.onItemSelected},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM}
    )
    @PostMapping("/check")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<List<OrganizationNode>> onNodeCheck(@RequestBody TreeNodeCheckData data) {
        // 处理节点勾选事件
        List<String> checkedNodeIds = data.getCheckedNodeIds();
        
        // 根据勾选的节点获取详细信息
        List<OrganizationNode> checkedNodes = organizationService.getNodesByIds(checkedNodeIds);
        
        ResultModel<List<OrganizationNode>> result = new ResultModel<>();
        result.setData(checkedNodes);
        return result;
    }
    
    @APIEventAnnotation(
        bindTreeEvent = {TreeViewEventEnum.onDrop},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.MESSAGE}
    )
    @PostMapping("/drop")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<Boolean> onNodeDrop(@RequestBody TreeNodeDropData data) {
        // 处理节点拖拽事件
        String dragNodeId = data.getDragNodeId();
        String dropNodeId = data.getDropNodeId();
        String dropPosition = data.getDropPosition(); // before, after, inner
        
        // 执行节点拖拽操作
        boolean success = organizationService.moveNode(dragNodeId, dropNodeId, dropPosition);
        
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(success);
        return result;
    }
}
```

### 4.5 TreeView事件处理

TreeView支持与TreeBar相同的事件处理机制，并且可以处理更复杂的数据操作事件：

```java
// 节点勾选事件处理
@APIEventAnnotation(
    bindTreeEvent = {TreeViewEventEnum.onItemSelected},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.FORM}
)
@PostMapping("/check")
public ResultModel<List<OrganizationNode>> onNodeCheck(@RequestBody TreeNodeCheckData data) {
    // 处理节点勾选事件
    List<String> checkedNodeIds = data.getCheckedNodeIds();
    
    // 根据勾选的节点获取详细信息
    List<OrganizationNode> checkedNodes = organizationService.getNodesByIds(checkedNodeIds);
    
    ResultModel<List<OrganizationNode>> result = new ResultModel<>();
    result.setData(checkedNodes);
    return result;
}
```

## 5. Tree组件设计规范

### 5.1 Tree节点设计

#### 节点基本结构
```java
public class TreeNode implements TreeItem {
    private String id;          // 节点唯一标识
    private String title;       // 节点标题
    private String key;         // 节点键值
    private String icon;        // 节点图标
    private boolean isLeaf;     // 是否为叶子节点
    private List<TreeNode> children; // 子节点列表
    private Map<String, Object> attributes; // 节点属性
    private Class[] bindClass;  // 绑定的服务类
    private boolean iniFold;    // 初始化时是否折叠
    private boolean dynDestory; // 是否动态销毁
    private boolean lazyLoad;   // 是否懒加载
    
    // getters and setters
    
    @Override
    public Class[] getBindClass() {
        return bindClass;
    }
    
    @Override
    public boolean isIniFold() {
        return iniFold;
    }
    
    @Override
    public boolean isDynDestory() {
        return dynDestory;
    }
    
    @Override
    public boolean isLazyLoad() {
        return lazyLoad;
    }
}
```

#### 节点接口定义
```java
public interface TreeItem extends IconEnumstype {
    Class[] getBindClass();
    boolean isIniFold();
    boolean isDynDestory();
    boolean isLazyLoad();
}
```

### 5.2 Tree数据模型

#### TreeResultModel
```java
public class TreeResultModel<T> extends ResultModel<T> {
    private List<String> expandedKeys;  // 展开的节点键值
    private List<String> selectedKeys;  // 选中的节点键值
    private List<String> checkedKeys;   // 勾选的节点键值
    
    // getters and setters
}
```

### 5.3 Tree事件处理

#### Tree事件类型
```java
public enum TreeViewEventEnum implements EventKey {
    // 基础事件
    onClick("onClick"),           // 点击事件
    onDblclick("onDblclick"),     // 双击事件
    onContextmenu("onContextmenu"), // 右键菜单事件
    
    // 节点操作事件
    onGetContent("onGetContent"), // 获取内容事件
    onItemSelected("onItemSelected"), // 节点选择事件
    beforeFold("beforeFold"),     // 折叠前事件
    beforeExpand("beforeExpand"), // 展开前事件
    afterFold("afterFold"),       // 折叠后事件
    afterExpand("afterExpand"),   // 展开后事件
    
    // 拖拽事件
    onDrop("onDrop"),             // 拖拽放置事件
    onDragEnter("onDragEnter"),   // 拖拽进入事件
    onDragLeave("onDragLeave"),   // 拖拽离开事件
    
    // 命令事件
    onCmd("onCmd");               // 命令事件
    
    private String event;
    
    TreeViewEventEnum(String event) {
        this.event = event;
    }
    
    @Override
    public String getEvent() {
        return event;
    }
}
```

#### Tree菜单项
```java
public enum TreeMenu implements CustomMenu, IconEnumstype {
    SAVEROW("保存", "fas fa-save", "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SAVEROW}),
    SORTDOWN("向下", "fas fa-arrow-down", "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.SORTDOWN}),
    SORTUP("向上", "fas fa-arrow-up", "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SORTUP}),
    RESET("重置", "fas fa-refresh", "true", IconColorEnum.YELLOW, new CustomAction[]{CustomTreeAction.RESET}),
    SAVE("确定", "fas fa-check", "true", IconColorEnum.DARKBLUE, new CustomAction[]{CustomTreeAction.SAVE}),
    DELETE("删除", "fas fa-trash", "true", IconColorEnum.PINK, new CustomAction[]{CustomTreeAction.DELETE, CustomPageAction.RELOAD}),
    ADD("添加", "fas fa-plus", "true", IconColorEnum.GREEN, new CustomAction[]{CustomPageAction.ADD}),
    LOADCHILD("刷新", "fas fa-refresh", "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.RELOADCHILD}),
    CLOSE("关闭", "fas fa-times", "true", IconColorEnum.CYAN, new CustomAction[]{CustomPageAction.CLOSE}),
    RELOAD("刷新", "fas fa-refresh", "true", IconColorEnum.CYAN, new CustomAction[]{CustomTreeAction.RELOAD});
    
    // 枚举实现
}
```

### 5.4 Tree样式配置

#### 节点图标配置
```java
@TreeAnnotation(
    customService = {FileTreeViewService.class}
)
public class FileTreeView implements TreeItem {
    // 根据节点类型显示不同图标
    // 文件夹: fas fa-folder
    // 文件: fas fa-file
    // 图片: fas fa-file-image
    // 文档: fas fa-file-alt
    
    @Override
    public String getImageClass() {
        // 根据节点类型返回相应的图标类
        return "fas fa-folder";
    }
}
```

## 6. Tree与页面导航

### 6.1 多级页面嵌套导航

ooder平台通过Tree实现多级页面嵌套导航：

```java
@TreeAnnotation(
    customService = {NavigationTreeService.class},
    selMode = SelModeType.single
)
public class NavigationTreeBarView {
    // 导航树视图
}

@Service
@RestController
@RequestMapping("/api/navigation")
public class NavigationTreeService {
    
    @APIEventAnnotation(
        bindTreeEvent = {TreeViewEventEnum.onClick},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.REDIRECT}
    )
    @PostMapping("/navigate")
    @Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)
    public ResultModel<String> navigateToPage(@RequestBody NavigationData data) {
        String selectedNodeId = data.getSelectedNodeId();
        
        // 根据节点ID确定目标页面
        String targetPage = navigationService.getTargetPage(selectedNodeId);
        
        ResultModel<String> result = new ResultModel<>();
        result.setData(targetPage);
        return result;
    }
}
```

### 6.2 数据驱动的导航系统

Tree导航系统是数据驱动的，节点数据决定了导航结构：

```java
public class NavigationNode implements TreeItem {
    private String id;
    private String title;
    private String pageUrl;      // 对应的页面URL
    private String pageType;     // 页面类型
    private String icon;
    private List<NavigationNode> children;
    
    // getters and setters
    
    @Override
    public Class[] getBindClass() {
        return new Class[0];
    }
    
    @Override
    public boolean isIniFold() {
        return false;
    }
    
    @Override
    public boolean isDynDestory() {
        return false;
    }
    
    @Override
    public boolean isLazyLoad() {
        return true;
    }
}
```

### 6.3 导航与视图的联动

Tree导航可以与页面内容视图联动：

```java
// 主页面结构
@FormAnnotation(
    customService = {MainPageService.class}
)
public class MainPageView {
    // 左侧导航树
    private NavigationTreeBarView navigationTree;
    
    // 右侧内容区域
    private ContentView contentView;
}

@Service
public class MainPageService {
    public void onTreeNodeSelected(String nodeId) {
        // 当Tree节点被选择时，更新右侧内容区域
        ContentView content = contentService.loadContentByNodeId(nodeId);
        // 更新页面内容
    }
}
```

## 7. Tree最佳实践

### 7.1 性能优化

1. **虚拟滚动**：对于大量节点的Tree，使用虚拟滚动技术
2. **懒加载**：子节点数据按需加载
3. **缓存机制**：合理缓存已加载的节点数据
4. **节点过滤**：提供搜索和过滤功能

```java
@TreeAnnotation(
    customService = {LargeDataTreeViewService.class},
    lazyLoad = true  // 启用懒加载
)
public class LargeDataTreeView {
    // 大数据量Tree视图
}
```

### 7.2 用户体验

1. **响应式设计**：适配不同屏幕尺寸
2. **键盘导航**：支持键盘操作
3. **状态反馈**：提供清晰的操作状态反馈
4. **动画效果**：适当的展开/折叠动画

### 7.3 数据一致性

1. **事务处理**：节点操作使用事务保证数据一致性
2. **并发控制**：处理多用户并发操作
3. **数据验证**：节点操作前进行数据验证
4. **错误恢复**：提供操作失败的恢复机制

## 8. Tree应用示例

### 8.1 组织架构管理

```java
// 组织架构节点定义
public class OrganizationNode implements TreeItem {
    private String id;
    private String name;
    private String type; // COMPANY, DEPARTMENT, TEAM, EMPLOYEE
    private String parentId;
    private List<OrganizationNode> children;
    private Class[] bindClass = {OrganizationManagementService.class};
    
    // getters and setters
    
    @Override
    public Class[] getBindClass() {
        return bindClass;
    }
    
    @Override
    public boolean isIniFold() {
        return false;
    }
    
    @Override
    public boolean isDynDestory() {
        return false;
    }
    
    @Override
    public boolean isLazyLoad() {
        return true;
    }
}

@TreeAnnotation(
    customService = {OrganizationManagementService.class},
    selMode = SelModeType.single
)
@TreeViewAnnotation(
    itemType = ResponsePathTypeEnum.TREEVIEW
)
public class OrganizationManagementView {
    // 组织架构管理视图
}

@Service
public class OrganizationManagementService {
    // 组织架构管理业务逻辑
}
```

### 8.2 分类目录管理

```java
// 分类目录节点定义
public class CategoryNode implements TreeItem {
    private String id;
    private String name;
    private String code;
    private String parentId;
    private List<CategoryNode> children;
    private Class[] bindClass = {CategoryManagementService.class};
    
    // getters and setters
    
    @Override
    public Class[] getBindClass() {
        return bindClass;
    }
    
    @Override
    public boolean isIniFold() {
        return true;
    }
    
    @Override
    public boolean isDynDestory() {
        return false;
    }
    
    @Override
    public boolean isLazyLoad() {
        return true;
    }
}

@TreeAnnotation(
    customService = {CategoryManagementService.class},
    selMode = SelModeType.multi
)
public class CategoryManagementView {
    // 分类目录管理视图
}
```

### 8.3 文件系统管理

```java
// 文件系统节点定义
public class FileNode implements TreeItem {
    private String id;
    private String name;
    private String type; // FOLDER, FILE
    private String parentId;
    private long size;
    private Date modifyTime;
    private List<FileNode> children;
    private Class[] bindClass = {FileManagerService.class};
    
    // getters and setters
    
    @Override
    public Class[] getBindClass() {
        return bindClass;
    }
    
    @Override
    public boolean isIniFold() {
        return true;
    }
    
    @Override
    public boolean isDynDestory() {
        return false;
    }
    
    @Override
    public boolean isLazyLoad() {
        return true;
    }
    
    @Override
    public String getImageClass() {
        if ("FOLDER".equals(type)) {
            return "fas fa-folder";
        } else {
            return "fas fa-file";
        }
    }
}

@TreeAnnotation(
    customService = {FileManagerService.class},
    selMode = SelModeType.single
)
public class FileManagerView {
    // 文件管理视图
}
```

通过以上设计，ooder平台的Tree组件能够很好地支持多级数据展示和导航需求，为用户提供直观、易用的数据管理和页面导航体验。