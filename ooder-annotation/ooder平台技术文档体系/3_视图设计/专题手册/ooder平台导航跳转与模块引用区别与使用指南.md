# ooder平台导航跳转与模块引用区别与使用指南

## 目录

1. [概述](#1-概述)
2. [导航跳转(Nav)详解](#2-导航跳转nav详解)
   - 2.1 核心概念
   - 2.2 实现机制
   - 2.3 使用场景
3. [模块引用(REF)详解](#3-模块引用ref详解)
   - 3.1 核心概念
   - 3.2 实现机制
   - 3.3 使用场景
4. [导航跳转与模块引用的区别](#4-导航跳转与模块引用的区别)
   - 4.1 功能差异
   - 4.2 技术实现差异
   - 4.3 使用场景差异
5. [如何选择使用导航跳转或模块引用](#5-如何选择使用导航跳转或模块引用)
6. [最佳实践](#6-最佳实践)
7. [示例代码](#7-示例代码)

## 1. 概述

在ooder平台中，导航跳转(Nav)和模块引用(REF)是两种不同的机制，用于实现视图间的关联和复用。虽然它们都可以实现页面间的跳转或引用，但在设计理念、实现机制和使用场景上存在显著差异。

本指南将详细解释这两种机制的区别，并提供使用建议和最佳实践。

## 2. 导航跳转(Nav)详解

### 2.1 核心概念

导航跳转(Nav)是指在不同视图间进行页面跳转的机制，主要用于实现用户在不同功能模块间的导航。导航跳转服务通常声明为`AggregationType.NAVIGATION`类型。

**核心特征：**
- 实现页面间的跳转和导航
- 通常涉及URL的变化
- 支持浏览器历史记录和前进后退功能
- 适用于功能模块间的切换

### 2.2 实现机制

导航跳转通过以下方式实现：

1. **导航服务类**：创建专门的导航服务类，声明为`AggregationType.NAVIGATION`
2. **注解驱动**：使用`@Aggregation(type = AggregationType.NAVIGATION)`注解
3. **事件处理**：通过`@APIEventAnnotation`处理跳转事件
4. **URL跳转**：通过视图入口的Web唯一地址完成跳转

**示例：**
```java
@Aggregation(type = AggregationType.NAVIGATION, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/navigation")
@Service
public class AttendanceNavigationService {
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 0, caption = "考勤记录查询", imageClass = "fa-solid fa-search")
    @PostMapping("/toQueryView")
    @ResponseBody
    public ResultModel<Boolean> navigateToQueryView(@RequestBody AttendanceCheckInView view) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            // 实现跳转到考勤记录查询视图的逻辑
            resultModel.setData(true);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(false);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
}
```

### 2.3 使用场景

导航跳转适用于以下场景：

1. **功能模块切换**：在不同的业务功能模块间进行切换
2. **页面流程导航**：实现业务流程中的页面跳转
3. **菜单导航**：通过菜单项实现页面跳转
4. **面包屑导航**：实现层级导航路径

## 3. 模块引用(REF)详解

### 3.1 核心概念

模块引用(REF)是指在一个视图中引用另一个模块的机制，主要用于实现模块间的复用和组合。模块引用服务通常声明为`AggregationType.MODULE`类型。

**核心特征：**
- 实现模块间的引用和复用
- 通常不涉及URL的变化
- 支持模块的嵌入和组合
- 适用于功能组件的复用

### 3.2 实现机制

模块引用通过以下方式实现：

1. **ModuleRefFieldAnnotation**：使用`@ModuleRefFieldAnnotation`注解实现模块引用
2. **模块路径引用**：通过`src`属性指定被引用模块的路径
3. **服务类绑定**：通过`bindClass`属性绑定被引用模块的服务类
4. **弱引用机制**：通过视图入口的Web唯一地址完成弱引用

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "综合信息",
    borderType = BorderType.inset,
    customService = {ComprehensiveInfoService.class}
)
public class ComprehensiveInfoView {
    // 引用其他模块的视图
    @ModuleRefFieldAnnotation(
        src = "/modules/person/basicInfo",
        bindClass = BasicInfoService.class,
        dock = Dock.fill
    )
    @CustomAnnotation(caption = "人员基本信息", index = 1)
    private BasicInfoModule basicInfoModule;
}
```

### 3.3 使用场景

模块引用适用于以下场景：

1. **功能组件复用**：在多个页面中复用相同的功能组件
2. **页面组合**：将多个独立的模块组合成一个复杂页面
3. **第三方模块集成**：集成第三方提供的功能模块
4. **微前端架构**：实现微前端架构中的模块引用

## 4. 导航跳转与模块引用的区别

### 4.1 功能差异

| 特性 | 导航跳转(Nav) | 模块引用(REF) |
|------|---------------|---------------|
| 主要功能 | 页面间跳转 | 模块间引用 |
| URL变化 | 通常会改变URL | 通常不改变URL |
| 浏览器历史 | 支持前进后退 | 不影响浏览器历史 |
| 页面刷新 | 通常会刷新页面 | 通常不会刷新页面 |
| 独立性 | 跳转后独立运行 | 嵌入到父页面中运行 |

### 4.2 技术实现差异

| 实现方面 | 导航跳转(Nav) | 模块引用(REF) |
|----------|---------------|---------------|
| 注解类型 | `@Aggregation(type = AggregationType.NAVIGATION)` | `@ModuleRefFieldAnnotation` |
| 服务类型 | `AggregationType.NAVIGATION` | `AggregationType.MODULE` |
| 引用方式 | 通过URL跳转 | 通过模块路径引用 |
| 数据传递 | 通过请求参数 | 通过组件属性 |
| 生命周期 | 独立的生命周期 | 依赖父组件生命周期 |

### 4.3 使用场景差异

| 场景类型 | 导航跳转(Nav) | 模块引用(REF) |
|----------|---------------|---------------|
| 业务流程 | 适用于业务流程中的页面切换 | 适用于功能组件的复用 |
| 用户体验 | 适用于需要明确页面切换的场景 | 适用于需要无缝集成的场景 |
| 性能考虑 | 每次跳转需要重新加载页面 | 模块可缓存，性能更好 |
| 维护成本 | 页面独立，维护相对简单 | 模块间耦合，需要考虑兼容性 |

## 5. 如何选择使用导航跳转或模块引用

### 5.1 选择导航跳转的情况

1. **需要明确的页面切换**：当用户需要从一个功能模块切换到另一个功能模块时
2. **需要浏览器历史记录**：当需要支持浏览器的前进后退功能时
3. **独立的业务流程**：当不同的页面代表独立的业务流程时
4. **权限控制需求**：当不同页面需要不同的权限控制时

### 5.2 选择模块引用的情况

1. **功能组件复用**：当需要在多个页面中复用相同的功能组件时
2. **页面组合需求**：当需要将多个独立模块组合成一个复杂页面时
3. **性能优化需求**：当需要避免页面刷新，提高用户体验时
4. **第三方集成**：当需要集成第三方提供的功能模块时

### 5.3 决策流程图

```mermaid
graph TD
    A[需要实现视图间关联] --> B{是否需要页面切换?}
    B -->|是| C{是否需要浏览器历史记录?}
    C -->|是| D[使用导航跳转(Nav)]
    C -->|否| E{是否需要复用组件?}
    E -->|是| F[使用模块引用(REF)]
    E -->|否| D
    B -->|否| F
```

## 6. 最佳实践

### 6.1 导航跳转最佳实践

1. **明确的导航路径**：为每个导航跳转定义清晰的业务含义
2. **合理的URL设计**：设计有意义的URL路径，便于理解和维护
3. **状态保持**：在跳转时考虑用户状态的保持和恢复
4. **错误处理**：提供友好的错误提示和恢复机制

### 6.2 模块引用最佳实践

1. **模块解耦**：确保被引用模块与引用模块间保持低耦合
2. **接口规范**：定义清晰的模块接口规范，便于集成
3. **版本管理**：对被引用模块进行版本管理，确保兼容性
4. **性能优化**：合理使用懒加载和缓存机制优化性能

### 6.3 混合使用最佳实践

1. **架构设计**：在系统架构设计阶段就明确哪些地方使用导航跳转，哪些地方使用模块引用
2. **统一规范**：制定统一的使用规范，确保团队成员理解并遵循
3. **文档记录**：详细记录每个模块的使用方式和依赖关系
4. **测试覆盖**：确保导航跳转和模块引用的测试覆盖完整

## 7. 示例代码

### 7.1 导航跳转示例

```java
/**
 * 考勤导航服务类
 * 处理考勤模块间的页面跳转
 */
@Aggregation(type = AggregationType.NAVIGATION, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/navigation")
@Service
public class AttendanceNavigationService {
    
    /**
     * 从考勤签到页面跳转到考勤记录查询页面
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 0, caption = "考勤记录查询", imageClass = "fa-solid fa-search")
    @PostMapping("/toQueryView")
    @ResponseBody
    public ResultModel<Boolean> navigateToQueryView(@RequestBody AttendanceCheckInView view) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            // 实现跳转逻辑
            resultModel.setData(true);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(false);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
    
    /**
     * 从考勤记录查询页面返回到考勤签到页面
     */
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 1, caption = "返回签到", imageClass = "fa-solid fa-arrow-left")
    @PostMapping("/toCheckInView")
    @ResponseBody
    public ResultModel<Boolean> navigateToCheckInView(@RequestBody AttendanceQueryListView view) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            // 实现跳转逻辑
            resultModel.setData(true);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(false);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
}
```

### 7.2 模块引用示例

```java
/**
 * 员工综合信息视图类
 * 展示如何在视图中引用其他模块
 */
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "员工综合信息管理",
    borderType = BorderType.inset,
    customService = {EmployeeInfoService.class},
    toggle = true
)
public class EmployeeInfoView {
    // 基本信息表单（内嵌）
    @FormFieldAnnotation(
        borderType = BorderType.inset,
        imageClass = "fa-solid fa-user"
    )
    @CustomAnnotation(caption = "基本信息", index = 1)
    private BasicInfoForm basicInfoForm;
    
    // 工作经历表格（内嵌）
    @TreeGridFieldAnnotation(
        bindClass = WorkExperienceService.class,
        borderType = BorderType.inset,
        imageClass = "fa-solid fa-briefcase"
    )
    @CustomAnnotation(caption = "工作经历", index = 2)
    private WorkExperienceTreeGrid workExperienceTreeGrid;
    
    // 引用其他模块的组织架构图
    @ModuleRefFieldAnnotation(
        src = "/modules/org/chart",
        bindClass = OrgChartService.class,
        dock = Dock.fill
    )
    @CustomAnnotation(caption = "组织架构", index = 3)
    private OrgChartModule orgChartModule;
    
    // 引用第三方的绩效评估模块
    @ModuleRefFieldAnnotation(
        src = "/thirdparty/performance/evaluation",
        bindClass = PerformanceEvaluationService.class,
        dock = Dock.fill
    )
    @CustomAnnotation(caption = "绩效评估", index = 4)
    private PerformanceEvaluationModule performanceEvaluationModule;
}
```

通过以上详细说明，开发者可以清楚地理解导航跳转和模块引用的区别，并根据具体需求选择合适的实现方式。