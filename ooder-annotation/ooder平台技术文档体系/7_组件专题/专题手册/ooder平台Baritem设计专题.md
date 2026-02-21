# ooder平台Baritem设计专题

## 目录

1. [概述](#1-概述)
2. [Baritem核心概念](#2-baritem核心概念)
3. [Baritem与Treebar的关系](#3-baritem与treebar的关系)
4. [Baritem注解体系](#4-baritem注解体系)
5. [Baritem实现机制](#5-baritem实现机制)
6. [Treebar的特殊特性](#6-treebar的特殊特性)
   - 6.1 loadChild特性
   - 6.2 lazyLoad特性
   - 6.3 动态销毁特性
7. [Baritem事件处理](#7-baritem事件处理)
8. [Baritem应用示例](#8-baritem应用示例)

## 1. 概述

在ooder平台中，Baritem是构成各种Bar组件（如工具栏、菜单栏、页面栏等）的基本单元。每个Baritem都具有特定的功能和行为，通过注解和绑定服务类来实现其业务逻辑。

Baritem设计是ooder平台UI组件化的核心部分，它将界面元素与业务逻辑解耦，使得开发者可以专注于业务实现而不必关心界面细节。

## 2. Baritem核心概念

Baritem的核心概念包括：

1. **功能单元**：每个Baritem代表一个独立的功能单元，如保存、搜索、删除等操作
2. **注解驱动**：通过[@CustomAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/CustomAnnotation.java#L13-L55)注解定义Baritem的外观和行为
3. **服务绑定**：每个Baritem绑定到特定的服务类方法，实现业务逻辑
4. **事件处理**：支持丰富的事件处理机制，包括成功、失败、前置、后置等事件
5. **条件表达式**：通过表达式控制Baritem的可见性和可用性

## 3. Baritem与Treebar的关系

Treebar是一种特殊的Bar组件，它是多级Baritem的集合。与普通Baritem相比，Treebar具有以下特点：

1. **层级结构**：Treebar支持多级嵌套结构，每个节点都是一个Baritem
2. **数据驱动**：Treebar的内容由数据模型驱动，而非静态定义
3. **动态加载**：支持子节点的动态加载（loadChild特性）
4. **状态管理**：具有展开/折叠状态管理功能

## 4. Baritem注解体系

Baritem通过[@CustomAnnotation](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/CustomAnnotation.java#L13-L55)注解进行配置：

```java
@CustomAnnotation(
    index = 0, 
    caption = "保存", 
    imageClass = "fa-solid fa-save"
)
```

**核心属性说明：**
- index：Baritem在工具栏中的位置索引
- caption：Baritem的显示文本
- imageClass：Baritem的图标CSS类
- expression：控制Baritem可见性的表达式
- readonly：是否为只读状态

## 5. Baritem实现机制

Baritem的实现机制包括服务类和API事件处理：

```java
@Service
@RestController
@RequestMapping("/attendance")
public class AttendanceBarService {
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 0, caption = "保存", imageClass = "fa-solid fa-save")
    @PostMapping("/save")
    @ResponseBody
    public ResultModel<Boolean> saveAttendanceData(@RequestBody AttendanceCheckInView view) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            // 实现具体的保存逻辑
            resultModel.setData(true);
            resultModel.setRequestStatus(1); // 设置成功状态
        } catch (Exception e) {
            // 发生错误时返回ErrorResultModel封装的错误信息
            ErrorResultModel<Boolean> errorResult = new ErrorResultModel<>();
            errorResult.setErrdes(e.getMessage());
            errorResult.setErrcode(1000); // 设置默认错误码
            errorResult.setRequestStatus(-1); // 设置错误状态
            return errorResult;
        }
        
        return resultModel;
    }
}
```

## 6. Treebar的特殊特性

### 6.1 loadChild特性

Treebar作为多级Baritem，每个Baritem除了具有bindClass之外，还会额外增加loadChild特性。这个方法特性由Tree自身的事件TreeEvent来触发：

```java
/**
 * 获取子节点数据
 * 
 * @param parentId 父节点ID
 * @return 树形节点列表结果模型
 */
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.CURRFORM},   
    beforeInvoke = CustomBeforInvoke.BUSY
)
@CustomAnnotation(index = 1, caption = "加载子节点", imageClass = "fa-solid fa-folder-open")
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
        resultModel.setRequestStatus(1); // 设置成功状态
    } catch (Exception e) {
        // 发生错误时返回ErrorListResultModel封装的错误信息
        ErrorListResultModel<List<TreeNode>> errorResult = new ErrorListResultModel<>(e.getMessage());
        errorResult.setErrcode(1000);
        errorResult.setRequestStatus(-1);
        return errorResult;
    }
    
    return resultModel;
}
```

### 6.2 lazyLoad特性

Treebar支持懒加载特性，只有在需要时才加载子节点数据：

```java
@TreeAnnotation(
    customService = {AttendanceTreeBarService.class},
    lazyLoad = true  // 启用懒加载
)
public class AttendanceTreeBarView {
    // TreeBar视图定义
}
```

### 6.3 动态销毁特性

Treebar支持动态销毁特性，可以在不需要时销毁节点以节省资源：

```java
@TreeAnnotation(
    customService = {AttendanceTreeBarService.class},
    dynDestory = true  // 启用动态销毁
)
public class AttendanceTreeBarView {
    // TreeBar视图定义
}
```

## 7. Baritem事件处理

Baritem支持丰富的事件处理机制：

```java
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},  // 执行成功事件
    beforeInvoke = CustomBeforInvoke.BUSY,               // 调用前事件
    onError = {CustomOnError.ALERT}                      // 错误事件
)
@CustomAnnotation(index = 0, caption = "保存", imageClass = "fa-solid fa-save")
@PostMapping("/save")
public ResultModel<Boolean> saveAttendanceData(@RequestBody AttendanceCheckInView view) {
    // 业务逻辑实现
}
```

## 8. Baritem应用示例

### 8.1 考勤管理系统Baritem

```java
@Service
@RestController
@RequestMapping("/attendance")
public class AttendanceBarService {
    
    /**
     * 保存考勤数据
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 0, caption = "保存", imageClass = "fa-solid fa-save")
    @PostMapping("/save")
    @ResponseBody
    public ResultModel<Boolean> saveAttendanceData(@RequestBody AttendanceCheckInView view) {
        // 实现保存逻辑
    }
    
    /**
     * 搜索考勤数据
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 1, caption = "搜索", imageClass = "fa-solid fa-search")
    @GetMapping("/search")
    @ResponseBody
    public ResultModel<Boolean> searchAttendanceData(@RequestBody AttendanceQueryListView view) {
        // 实现搜索逻辑
    }
}
```

### 8.2 Treebar应用示例

```java
@TreeAnnotation(
    customService = {AttendanceNavigationTreeService.class},
    showRoot = true,
    selMode = SelModeType.single,
    bindTypes = {ComponentType.TREEBAR}
)
public class AttendanceTreeBarView {
    // TreeBar视图定义
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
    @CustomAnnotation(index = 0, caption = "刷新树形导航", imageClass = "fa-solid fa-sync")
    @GetMapping("/data")
    @ResponseBody
    public ListResultModel<List<TreeNode>> getTreeData() {
        // 实现树形数据加载逻辑
    }
    
    /**
     * 获取子节点数据（loadChild特性）
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},   
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 1, caption = "加载子节点", imageClass = "fa-solid fa-folder-open")
    @GetMapping("/children")
    @ResponseBody
    public ListResultModel<List<TreeNode>> getChildren(String parentId) {
        // 实现子节点加载逻辑
    }
}
```

通过以上设计，ooder平台的Baritem组件能够很好地支持各种UI组件的需求，为用户提供直观、易用的操作界面。