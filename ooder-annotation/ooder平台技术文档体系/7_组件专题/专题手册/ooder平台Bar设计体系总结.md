# ooder平台Bar设计体系总结

## 目录

1. [概述](#1-概述)
2. [Bar设计体系核心概念](#2-bar设计体系核心概念)
3. [Bar组件类型](#3-bar组件类型)
   - 3.1 传统Bar组件
   - 3.2 TreeBar组件
4. [Baritem设计](#4-baritem设计)
   - 4.1 Baritem核心概念
   - 4.2 Baritem与TreeBaritem的关系
5. [Bar注解体系](#5-bar注解体系)
6. [Bar实现机制](#6-bar实现机制)
7. [Bar事件处理](#7-bar事件处理)
8. [Bar延迟加载机制](#8-bar延迟加载机制)
   - 8.1 TreeBar延迟加载
   - 8.2 MenuBar和PopMenu延迟加载
   - 8.3 延迟加载的相似性与差异
9. [TreeBar用例详解](#9-treebar用例详解)
   - 9.1 URL跳转与弱引用
   - 9.2 强绑定关系与bindClass约束
   - 9.3 构造函数与ChildTreeAnnotation
   - 9.4 静态TreeBar与枚举实现
10. [Bar设计最佳实践](#10-bar设计最佳实践)

## 1. 概述

ooder平台的Bar设计体系是UI组件化的核心部分，它将界面元素与业务逻辑解耦，使得开发者可以专注于业务实现而不必关心界面细节。Bar设计体系包括传统的工具栏、菜单栏、底部栏等组件，以及新加入的TreeBar组件。

## 2. Bar设计体系核心概念

Bar设计体系的核心概念包括：

1. **组件化设计**：将界面元素抽象为可复用的组件
2. **注解驱动**：通过注解定义组件的外观和行为
3. **服务绑定**：组件绑定到特定的服务类方法，实现业务逻辑
4. **事件处理**：支持丰富的事件处理机制
5. **条件表达式**：通过表达式控制组件的可见性和可用性
6. **层级结构**：支持多级嵌套结构（TreeBar特有）

## 3. Bar组件类型

### 3.1 传统Bar组件

传统Bar组件包括：
- **ToolBar**：工具栏组件，通常位于页面顶部
- **MenuBar**：菜单栏组件，通常位于页面顶部
- **BottomBar**：底部栏组件，通常位于页面底部
- **PopMenu**：弹出菜单组件

这些组件通过相应的注解进行配置：
``java
@ToolBarMenu(...)
public class AttendanceToolBarView { ... }

@MenuBarMenu(...)
public class AttendanceMenuBarView { ... }

@BottomBarMenu(...)
public class AttendanceBottomBarView { ... }
```

### 3.2 TreeBar组件

TreeBar是一种特殊的Bar组件，它具有以下特点：
- **层级结构**：支持多级嵌套结构，每个节点都是一个Baritem
- **数据驱动**：内容由数据模型驱动，而非静态定义
- **动态加载**：支持子节点的动态加载（loadChild特性）
- **状态管理**：具有展开/折叠状态管理功能
- **懒加载**：支持懒加载特性，只有在需要时才加载子节点数据
- **动态销毁**：支持动态销毁特性，可以在不需要时销毁节点以节省资源

TreeBar通过[@TreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/TreeAnnotation.java#L11-L89)注解进行配置：
```java
@TreeAnnotation(
    customService = {AttendanceTreeBarService.class},
    showRoot = true,
    selMode = SelModeType.single,
    bindTypes = {ComponentType.TREEBAR}
)
public class AttendanceTreeBarView {
    // TreeBar视图定义
}
```

## 4. Baritem设计

### 4.1 Baritem核心概念

Baritem是构成各种Bar组件的基本单元，核心概念包括：
1. **功能单元**：每个Baritem代表一个独立的功能单元
2. **注解驱动**：通过[@CustomAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/CustomAnnotation.java#L13-L55)注解定义Baritem的外观和行为
3. **服务绑定**：每个Baritem绑定到特定的服务类方法，实现业务逻辑
4. **事件处理**：支持丰富的事件处理机制
5. **条件表达式**：通过表达式控制Baritem的可见性和可用性

### 4.2 Baritem与TreeBaritem的关系

TreeBaritem是TreeBar中的节点，它除了具有普通Baritem的所有特性外，还具有以下特殊特性：
1. **loadChild特性**：每个TreeBaritem除了具有bindClass之外，还会额外增加loadChild特性，这个方法特性由Tree自身的事件TreeEvent来触发
2. **lazyLoad特性**：支持懒加载，只有在需要时才加载子节点数据
3. **动态销毁特性**：可以在不需要时销毁节点以节省资源

## 5. Bar注解体系

Bar设计体系使用多种注解来配置不同类型的Bar组件：

### 5.1 通用注解
- [@CustomAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/CustomAnnotation.java#L13-L55)：定义Baritem的外观和行为
- [@APIEventAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/field/APIEventAnnotation.java#L34-L134)：定义事件处理行为

### 5.2 Bar类型注解
- [@ToolBarMenu](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ToolBarMenu.java#L14-L101)：配置工具栏
- [@MenuBarMenu](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/MenuBarMenu.java#L13-L81)：配置菜单栏
- [@BottomBarMenu](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/BottomBarMenu.java#L12-L89)：配置底部栏
- [@TreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/TreeAnnotation.java#L11-L89)：配置TreeBar

### 5.3 服务注解
- [@Aggregation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/annotation/Aggregation.java#L11-L35)：声明服务类型和用户空间
- [@RestController](file:///E:/ooder-gitee/ooder-annotation/src/main/java/org/springframework/web/bind/annotation/RestController.java#L43-L62)：声明REST控制器
- [@Service](file:///E:/ooder-gitee/ooder-annotation/src/main/java/org/springframework/stereotype/Service.java#L42-L56)：声明服务组件

## 6. Bar实现机制

Bar的实现机制包括视图类和相应的服务类：

### 6.1 视图类实现
``java
@CustomClass(
    caption = "考勤TreeBar视图",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.TREEBAR_VIEW
)
public class AttendanceTreeBarView {
    
    @TreeAnnotation(
        customService = {AttendanceTreeBarService.class},
        showRoot = true,
        selMode = SelModeType.single,
        bindTypes = {ComponentType.TREEBAR}
    )
    public void attendanceTreeBar() {
        // TreeBar视图逻辑
    }
}
```

### 6.2 服务类实现
```java
@Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/treebar")
@Service
public class AttendanceTreeBarService {
    
    @APIEventAnnotation(
        autoRun = true,
        customRequestData = {RequestPathEnum.CTX},
        customResponseData = {ResponsePathEnum.TREEVIEW}
    )
    @GetMapping("/data")
    public ListResultModel<List<AttendanceTreeNode>> getTreeData() {
        // 实现树形数据加载逻辑
    }
    
    @APIEventAnnotation(
        bindTreeEvent = {CustomTreeEvent.TREERELOAD},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM}
    )
    @PostMapping("/nodeClick")
    public ListResultModel<List<String>> onNodeClick(@RequestBody NodeSelectionData data) {
        // 实现节点点击事件处理逻辑
    }
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},   
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 1, caption = "加载子节点", imageClass = "fa-solid fa-folder-open")
    @GetMapping("/children")
    @ResponseBody
    public ListResultModel<List<TreeNode>> getChildren(String parentId) {
        // 实现子节点加载逻辑（loadChild特性）
    }
}
```


## 7. Bar事件处理

Bar支持丰富的事件处理机制：

### 7.1 传统Bar事件
``java
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
    beforeInvoke = CustomBeforInvoke.BUSY,
    onError = {CustomOnError.ALERT}
)
@CustomAnnotation(index = 0, caption = "保存", imageClass = "fa-solid fa-save")
@PostMapping("/save")
public ResultModel<Boolean> saveAttendanceData(@RequestBody AttendanceCheckInView view) {
    // 业务逻辑实现
}
```

### 7.2 TreeBar事件
``java
@APIEventAnnotation(
    bindTreeEvent = {CustomTreeEvent.TREERELOAD},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.FORM}
)
@PostMapping("/nodeClick")
public ListResultModel<List<String>> onNodeClick(@RequestBody NodeSelectionData data) {
    // 实现节点点击事件处理逻辑
}
```

## 8. Bar延迟加载机制

### 8.1 TreeBar延迟加载

TreeBar支持多种延迟加载机制：
1. **lazyLoad属性**：在[@TreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/TreeAnnotation.java#L11-L89)中配置lazyLoad=true，实现节点的懒加载
2. **loadChild特性**：通过loadChild方法实现子节点的动态加载
3. **dynDestory属性**：在[@TreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/TreeAnnotation.java#L11-L89)中配置dynDestory=true，实现节点的动态销毁

```java
@TreeAnnotation(
    customService = {AttendanceTreeBarService.class},
    lazyLoad = true,      // 启用懒加载
    dynDestory = true     // 启用动态销毁
)
public class AttendanceTreeBarView {
    // TreeBar视图定义
}
```

### 8.2 MenuBar和PopMenu延迟加载

MenuBar和PopMenu也支持延迟加载机制：

1. **MenuBar延迟加载**：
``java
@MenuBarMenu(
    lazy = true,          // 启用延迟加载
    dynLoad = true        // 启用动态加载
)
public class AttendanceMenuBarView {
    // MenuBar视图定义
}
```

2. **PopMenu延迟加载**：
``java
@PopMenuViewAnnotation(
    dynLoad = true        // 启用动态加载
)
public void showPopMenu() {
    // PopMenu逻辑
}
```

### 8.3 延迟加载的相似性与差异

TreeBar、MenuBar和PopMenu在延迟加载方面具有以下相似性：
1. **按需加载**：都是在需要时才加载数据，减少初始加载时间
2. **资源优化**：通过延迟加载减少内存占用
3. **用户体验**：提高页面响应速度，改善用户体验

但它们也存在一些差异：
1. **TreeBar**：支持更复杂的层级结构，具有loadChild特性和动态销毁机制
2. **MenuBar**：主要用于菜单项的延迟加载，结构相对简单
3. **PopMenu**：通常在用户交互时动态加载，具有缓存机制

## 9. TreeBar用例详解

### 9.1 URL跳转与弱引用

在TreeBar中，URL跳转通过视图入口的Web唯一地址完成弱引用。这种方式允许TreeBar节点通过URL直接跳转到特定的视图页面，而不需要强耦合的代码依赖。

``java
// TreeBar节点通过URL跳转到特定视图
@TreeAnnotation(
    customService = {NavigationTreeService.class},
    selMode = SelModeType.single
)
public class NavigationTreeBarView {
    // TreeBar视图定义
}

@Service
public class NavigationTreeService {
    @APIEventAnnotation(
        bindTreeEvent = {TreeViewEventEnum.onClick},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.REDIRECT}
    )
    @PostMapping("/navigate")
    public ResultModel<String> navigateToPage(@RequestBody NavigationData data) {
        String selectedNodeId = data.getSelectedNodeId();
        
        // 根据节点ID确定目标页面URL
        String targetUrl = navigationService.getTargetUrl(selectedNodeId);
        
        ResultModel<String> result = new ResultModel<>();
        result.setData(targetUrl);
        return result;
    }
}
```

### 9.2 强绑定关系与bindClass约束

系统推荐采用强绑定关系，使用Baritem的bindClass来约束实现静态视图节点推导。这种方式通过bindClass属性将TreeBar节点与特定的服务类绑定，实现更严格的类型检查和代码组织。

``java
// TreeItem实现类，通过bindClass属性实现强绑定
public class OrganizationNode implements TreeItem {
    private Class[] bindClass = {OrganizationManagementService.class};
    
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
```

### 9.3 构造函数与ChildTreeAnnotation

在通用实例中，推荐在Tree视图中使用构造函数上增加[@ChildTreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ChildTreeAnnotation.java#L13-L99)注解标识，实例化采用构造函数完成。

``java
// 使用ChildTreeAnnotation注解的构造函数示例
public class OrgRolePopTree implements TreeItem {
    private String caption;
    private PersonRoleType personRoleType;
    private String roleId;
    private String personId;
    private String imageClass;
    
    @ChildTreeAnnotation(
        bindClass = PersonRefRoleService.class, 
        lazyLoad = true, 
        dynDestory = true
    )
    public OrgRolePopTree(PersonRoleType personRoleType, String personId) {
        this.caption = personRoleType.getName();
        this.personRoleType = personRoleType;
        this.roleId = personRoleType.getType();
        this.personId = personId;
        this.imageClass = personRoleType.getImageClass();
    }
    
    // Getters and setters
    public String getCaption() {
        return caption;
    }
    
    @Pid
    public PersonRoleType getPersonRoleType() {
        return personRoleType;
    }
    
    @Uid
    public String getRoleId() {
        return roleId;
    }
    
    @Override
    public Class[] getBindClass() {
        return new Class[]{PersonRefRoleService.class};
    }
    
    @Override
    public boolean isIniFold() {
        return false;
    }
    
    @Override
    public boolean isDynDestory() {
        return true;
    }
    
    @Override
    public boolean isLazyLoad() {
        return true;
    }
}
```

### 9.4 静态TreeBar与枚举实现

在静态TreeBar中，同一级节点通常采用枚举的方式实现静态数据的注入，使用Baritem的enumClass来完成静态数据初始化。

``java
// ooder推荐的TreeItem枚举实现方案
public enum StaticTreeNodes implements TreeItem, IconEnumstype {
    ROOT("根节点", "fas fa-home", new Class[]{RootService.class}),
    ORGANIZATION("组织架构", "fas fa-sitemap", new Class[]{OrganizationService.class}),
    USER_MANAGEMENT("用户管理", "fas fa-users", new Class[]{UserService.class}),
    ROLE_MANAGEMENT("角色管理", "fas fa-user-tag", new Class[]{RoleService.class});
    
    private String name;
    private String imageClass;
    private Class[] bindClass;
    
    StaticTreeNodes(String name, String imageClass, Class[] bindClass) {
        this.name = name;
        this.imageClass = imageClass;
        this.bindClass = bindClass;
    }
    
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
        return false;
    }
    
    @Override
    public String getType() {
        return name();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getImageClass() {
        return imageClass;
    }
}

// 在TreeBar中使用枚举
@TreeAnnotation(
    customService = {StaticTreeService.class},
    selMode = SelModeType.single
)
public class StaticTreeBarView {
    // 使用枚举初始化静态TreeBar
}
```

## 10. Bar设计最佳实践

### 10.1 设计原则
1. **单一职责**：每个Bar组件应该有明确的职责
2. **可复用性**：设计可复用的Bar组件和Baritem
3. **一致性**：保持界面元素的一致性
4. **可配置性**：通过注解提供灵活的配置选项

### 10.2 实现规范
1. **服务分离**：将视图逻辑和服务逻辑分离
2. **Web可访问性**：所有服务必须实现Web可访问性
3. **事件处理**：合理使用事件处理机制
4. **错误处理**：正确处理错误情况

### 10.3 性能优化
1. **懒加载**：对于TreeBar，合理使用懒加载特性
2. **动态销毁**：对于TreeBar，合理使用动态销毁特性
3. **缓存机制**：合理使用缓存机制提高性能

### 10.4 TreeBar用例最佳实践
1. **URL跳转**：优先使用视图入口的Web唯一地址实现弱引用跳转
2. **强绑定关系**：在需要类型安全的场景下使用bindClass实现强绑定
3. **构造函数注解**：在通用实例中使用[@ChildTreeAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ChildTreeAnnotation.java#L13-L99)注解标识构造函数
4. **静态数据注入**：在静态TreeBar中使用枚举实现同一级节点的静态数据注入

通过以上设计体系，ooder平台的Bar组件能够很好地支持各种UI组件的需求，为用户提供直观、易用的操作界面.