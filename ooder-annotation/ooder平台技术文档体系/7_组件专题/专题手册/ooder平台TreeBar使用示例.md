# ooder平台TreeBar使用示例

## 目录

1. [概述](#1-概述)
2. [TreeBar核心概念](#2-treebar核心概念)
3. [TreeBar注解体系](#3-treebar注解体系)
4. [TreeBar实现机制](#4-treebar实现机制)
5. [TreeBar事件处理](#5-treebar事件处理)
6. [TreeBar应用示例](#6-treebar应用示例)
   - 6.1 考勤管理系统TreeBar
   - 6.2 组织架构管理TreeBar

## 1. 概述

TreeBar是ooder平台中的多级导航组件，通常显示在页面左侧，为用户提供层级化的导航菜单。TreeBar是[ComponentType.TREEBAR](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/ui/ComponentType.java#L227-L227)类型的组件，具有Tree的所有特性，同时专门用于导航功能。

## 2. TreeBar核心概念

TreeBar的核心概念包括：
1. **层级导航**：支持无限层级的节点导航
2. **节点展开/折叠**：支持节点的展开和折叠操作
3. **节点选择**：支持节点的选择和高亮显示
4. **动态加载**：支持节点数据的动态加载
5. **状态保持**：保持用户的导航状态

## 3. TreeBar注解体系

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

**核心属性说明：**
- customService：关联的自定义服务类
- showRoot：是否显示根节点
- selMode：选择模式（单选、多选等）
- bindTypes：可绑定的组件类型

## 4. TreeBar实现机制

TreeBar的实现机制包括两个主要部分：

### 4.1 数据加载服务

```java
@Service
@RestController
@RequestMapping("/attendance/treebar")
public class AttendanceTreeBarService {
    
    @APIEventAnnotation(
        autoRun = true,
        customRequestData = {RequestPathEnum.CTX},
        customResponseData = {ResponsePathEnum.TREEVIEW}
    )
    @GetMapping("/data")
    public ListResultModel<List<AttendanceTreeNode>> getTreeData() {
        ListResultModel<List<AttendanceTreeNode>> result = new ListResultModel<>();
        
        try {
            List<AttendanceTreeNode> nodes = new ArrayList<>();
            
            // 添加根节点
            AttendanceTreeNode root = new AttendanceTreeNode();
            root.setId("0");
            root.setPid("-1");
            root.setName("考勤管理系统");
            root.setType("root");
            root.setImageClass("fas fa-calendar-alt");
            nodes.add(root);
            
            // 添加子节点
            AttendanceTreeNode checkInNode = new AttendanceTreeNode();
            checkInNode.setId("1");
            checkInNode.setPid("0");
            checkInNode.setName("考勤签到");
            checkInNode.setType("module");
            checkInNode.setImageClass("fas fa-sign-in-alt");
            nodes.add(checkInNode);
            
            result.setData(nodes);
            result.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorResultModel封装的错误信息
            ErrorResultModel<List<AttendanceTreeNode>> errorResult = new ErrorResultModel<>();
            errorResult.setErrdes(e.getMessage());
            errorResult.setErrcode(1000); // 设置默认错误码
            errorResult.setRequestStatus(-1); // 设置错误状态
            return errorResult;
        }
        
        return result;
    }
}
```

### 4.2 节点数据模型

```java
public class AttendanceTreeNode implements TreeItem {
    private String id;
    private String pid;
    private String name;
    private String type;
    private String imageClass;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getImageClass() { return imageClass; }
    public void setImageClass(String imageClass) { this.imageClass = imageClass; }
    
    @Override
    public Class[] getBindClass() { return new Class[0]; }
    
    @Override
    public boolean isIniFold() { return false; }
    
    @Override
    public boolean isDynDestory() { return false; }
    
    @Override
    public boolean isLazyLoad() { return true; }
}
```

## 5. TreeBar事件处理

TreeBar支持丰富的事件处理机制，通过[CustomTreeEvent](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/event/CustomTreeEvent.java#L11-L125)枚举定义了多种事件类型：

```java
@APIEventAnnotation(
    bindTreeEvent = {CustomTreeEvent.TREERELOAD},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.FORM}
)
@PostMapping("/nodeClick")
public ListResultModel<List<String>> onNodeClick(@RequestBody NodeSelectionData data) {
    ListResultModel<List<String>> result = new ListResultModel<>();
    
    try {
        String selectedNodeId = data.getSelectedNodeId();
        String redirectUrl = "";
        
        // 根据选中的节点确定跳转URL
        switch (selectedNodeId) {
            case "1":
                redirectUrl = "/attendance/checkin";
                break;
            // 其他节点处理...
            default:
                redirectUrl = "/attendance/dashboard";
                break;
        }
        
        List<String> urls = new ArrayList<>();
        urls.add(redirectUrl);
        result.setData(urls);
        result.setRequestStatus(1);
    } catch (Exception e) {
        // 发生错误时返回ErrorResultModel封装的错误信息
        ErrorListResultModel<List<String>> errorResult = new ErrorListResultModel<>(e.getMessage());
        errorResult.setErrcode(1000);
        errorResult.setRequestStatus(-1);
        return errorResult;
    }
    
    return result;
}
```

## 6. TreeBar应用示例

### 6.1 考勤管理系统TreeBar

```java
// 视图类定义
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

### 6.2 组织架构管理TreeBar

```java
// 组织架构TreeBar视图
@CustomClass(
    caption = "组织架构TreeBar视图",
    moduleViewType = ModuleViewType.NAVIGATION,
    customViewType = CustomViewType.TREEBAR_VIEW
)
public class OrganizationTreeBarView {
    
    @TreeAnnotation(
        customService = {OrganizationTreeBarService.class},
        showRoot = true,
        selMode = SelModeType.single,
        bindTypes = {ComponentType.TREEBAR}
    )
    public void organizationTreeBar() {
        // 组织架构TreeBar视图逻辑
    }
}
```

通过以上设计，ooder平台的TreeBar组件能够很好地支持多级导航需求，为用户提供直观、易用的页面导航体验。