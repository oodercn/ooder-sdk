# ooder平台视图设计最佳实践

## 目录

1. [概述](#1-概述)
2. [UI组件设计最佳实践](#2-ui组件设计最佳实践)
   - 2.1 组件设计原则
   - 2.2 注解使用建议
   - 2.3 性能优化建议
3. [视图类型设计最佳实践](#3-视图类型设计最佳实践)
   - 3.1 表单视图最佳实践
   - 3.2 列表视图最佳实践
   - 3.3 聚合视图最佳实践
   - 3.4 面板表单视图最佳实践
4. [嵌套视图设计最佳实践](#4-嵌套视图设计最佳实践)
   - 4.1 设计原则
   - 4.2 实现规范
   - 4.3 性能优化
5. [导航设计最佳实践](#5-导航设计最佳实践)
   - 5.1 设计原则
   - 5.2 实现规范
   - 5.3 用户体验
   - 5.4 性能优化
6. [视图关联最佳实践](#6-视图关联最佳实践)
   - 6.1 父子关系最佳实践
   - 6.2 顺序关系最佳实践
   - 6.3 并行关系最佳实践
7. [通用设计原则](#7-通用设计原则)
8. [代码示例](#8-代码示例)
9. [注解使用最佳实践](#9-注解使用最佳实践)
   - 9.1 注解分类与作用
   - 9.2 注解使用规范
10. [业务诉求转换最佳实践](#10-业务诉求转换最佳实践)
    - 10.1 转换思考过程
    - 10.2 示例：员工信息管理业务诉求转换

## 1. 概述

本文档总结了ooder平台视图设计的最佳实践，旨在为开发人员提供实用的指导和参考。通过遵循这些最佳实践，可以确保视图设计的一致性、可维护性和用户体验。

## 2. UI组件设计最佳实践

### 2.1 组件设计原则

1. **单一职责原则**：每个UI组件应具有明确且单一的职责，只负责特定的UI功能。
2. **可复用性原则**：组件设计应考虑通用性，使其能够在不同场景下复用。
3. **可配置性原则**：组件应提供丰富的配置选项，以满足不同业务场景的需求。
4. **一致性原则**：相同类型的组件在不同页面中应保持外观和行为的一致性。
5. **功能原子化原则**：每个原子组件只负责一个特定的功能。

### 2.2 注解使用建议

1. **优先级规则**：组件类型注解（如@InputAnnotation）具有最高优先级，@CustomAnnotation具有基础优先级，默认值作为兜底配置。
2. **注解组合**：Prop配置注解采用复合租借的方式，通过组合不同的注解来定义组件的完整属性。
3. **避免重复配置**：避免在多个注解中重复配置相同属性。
4. **合理使用bindClass**：对于涉及数据自身应用的组件，在行为注解中添加bindClass绑定属性。

### 2.3 性能优化建议

1. **避免不必要的组件嵌套**：避免过深的组件嵌套，建议嵌套层次不超过3层。
2. **合理使用懒加载机制**：对于非立即显示的嵌套组件，使用懒加载机制。
3. **优化组件渲染性能**：合理使用缓存机制和动态销毁策略。

## 3. 视图类型设计最佳实践

### 3.1 表单视图最佳实践

1. **字段分组**：合理分组相关字段，使用适当的输入控件注解。
2. **布局设计**：根据内容密度选择合适的列数和行数。
3. **验证规则**：合理设计数据验证规则，提供友好的错误提示。

### 3.2 列表视图最佳实践

1. **单一性原则**：遵循列表视图单一性原则，一个视图类应仅定义一种类型的视图。
2. **主键标识**：正确使用@Uid注解标识行主键。
3. **列配置**：合理配置列宽、排序等属性，提升用户体验。

### 3.3 聚合视图最佳实践

1. **TAB项设计**：为每个TAB项提供清晰的标题和图标。
2. **懒加载**：使用懒加载（lazyLoad）减少初始加载时间。
3. **自动保存**：合理使用自动保存（autoSave）确保数据及时保存。
4. **数据一致性**：确保聚合视图中各TAB项间的数据一致性。

### 3.4 面板表单视图最佳实践

1. **布局控制**：合理使用停靠布局和可切换特性。
2. **嵌套设计**：作为核心容器，允许多重嵌套但应避免过深嵌套。
3. **性能优化**：对于复杂嵌套结构，使用懒加载和缓存机制。

## 4. 嵌套视图设计最佳实践

### 4.1 设计原则

1. **层次清晰**：嵌套层次不宜过深，建议不超过3层。
2. **职责明确**：每个嵌套组件应有明确的职责。
3. **性能优化**：合理使用懒加载和缓存机制。
4. **用户体验**：确保嵌套结构的用户操作流畅性。

### 4.2 实现规范

1. **注解使用**：
   - 正确使用TreeGridFieldAnnotation、FormFieldAnnotation、BlockFieldAnnotation等Field注解
   - 合理配置各注解的核心属性
   - 配合@CustomAnnotation定义外观属性

2. **服务设计**：
   - 嵌套视图的服务类必须具有Web可访问性
   - bindClass绑定的服务类必须包含视图入口方法
   - 遵循平台服务设计规范

3. **数据传递**：
   - 合理设计父子视图间的数据传递机制
   - 使用@Uid和@Pid标识记录关系
   - 确保数据一致性

### 4.3 性能优化

1. **懒加载**：对于非立即显示的嵌套组件，使用懒加载机制。
2. **缓存机制**：合理使用autoSave属性实现状态缓存。
3. **动态销毁**：使用适当的动态销毁策略优化内存使用。

## 5. 导航设计最佳实践

### 5.1 设计原则

1. **一致性原则**：保持导航风格和交互方式的一致性。
2. **简洁性原则**：导航结构应简洁明了，避免过度复杂。
3. **可访问性原则**：确保导航对所有用户都易于使用。
4. **性能原则**：优化导航性能，提供流畅的用户体验。

### 5.2 实现规范

1. **注解使用**：正确使用导航相关注解。
2. **数据结构**：合理设计导航数据模型。
3. **事件处理**：完整处理各种导航事件。
4. **状态管理**：妥善管理导航状态。

### 5.3 用户体验

1. **响应式设计**：适配不同屏幕尺寸。
2. **状态反馈**：提供清晰的操作状态反馈。
3. **键盘导航**：支持键盘快捷键操作。
4. **动画效果**：适当的展开/折叠动画。

### 5.4 性能优化

1. **懒加载**：子节点数据按需加载。
2. **缓存机制**：合理缓存已加载的数据。
3. **虚拟滚动**：对于大量数据使用虚拟滚动。
4. **数据分页**：对大数据集进行分页处理。

## 6. 视图关联最佳实践

### 6.1 父子关系最佳实践

1. **生命周期管理**：确保子视图的生命周期依赖于父视图。
2. **数据传递**：合理设计从父视图向子视图的数据传递机制。
3. **控制权明确**：父视图应拥有对子视图的控制权。

### 6.2 顺序关系最佳实践

1. **执行顺序**：明确视图间的执行顺序。
2. **数据传递**：确保数据在视图间顺序传递。
3. **向导设计**：适用于向导式操作流程。

### 6.3 并行关系最佳实践

1. **独立运行**：确保并行视图间相对独立运行。
2. **数据共享**：合理处理可能存在的数据共享需求。
3. **状态同步**：必要时实现状态同步机制。

## 7. 通用设计原则

1. **单一职责原则**：每个视图类应仅负责一种类型的展示或录入任务。
2. **可复用性原则**：设计通用的视图组件，提高代码复用率。
3. **可维护性原则**：保持视图类结构清晰，易于理解和修改。
4. **用户友好原则**：界面简洁明了，操作流程清晰。
5. **性能优化原则**：合理设计视图结构，避免影响系统性能。

## 8. 代码示例

### 8.1 表单视图示例

```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {UserService.class}
)
public class UserFormView {
    @InputAnnotation(maxlength = 50)
    @CustomAnnotation(caption = "用户名", index = 1)
    private String username;
    
    @InputAnnotation(maxlength = 20)
    @CustomAnnotation(caption = "年龄", index = 2)
    private Integer age;
    
    @ButtonAnnotation(buttonType = ButtonType.primary)
    @CustomAnnotation(caption = "保存", index = 3)
    private String saveButton;
    
    // Getters and setters
}
```

### 8.2 聚合视图示例

```java
@TabsAnnotation(
    caption = "用户信息聚合",
    autoSave = true,
    barLocation = BarLocationType.top
)
public class UserAggregateView {
    @TabsViewAnnotation(
        expression = "basicInfo",
        dataUrl = "/user/basicInfo",
        autoSave = true
    )
    public void basicInfoTab() {
        // 基本信息TAB逻辑
    }
    
    @TabsViewAnnotation(
        expression = "contactInfo",
        dataUrl = "/user/contactInfo",
        autoSave = true
    )
    public void contactInfoTab() {
        // 联系信息TAB逻辑
    }
    
    // Getters and setters
}
```

### 8.3 嵌套视图示例

```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "综合信息面板",
    borderType = BorderType.inset,
    customService = {ComprehensiveInfoService.class}
)
public class ComprehensiveInfoView {
    // 内嵌表单视图
    @FormFieldAnnotation(borderType = BorderType.inset)
    @CustomAnnotation(caption = "基本信息", index = 1)
    private BasicInfoForm basicInfoForm;
    
    // 内嵌列表视图
    @TreeGridFieldAnnotation(bindClass = DetailListService.class)
    @CustomAnnotation(caption = "详细信息", index = 2)
    private DetailListView detailListView;
    
    // Getters and setters
}
```

### 8.4 导航视图示例

```java
@NavTreeViewAnnotation(
    expression = "userTree",
    dataUrl = "/navigation/tree/data",
    loadChildUrl = "/navigation/tree/children",
    rootId = "0",
    autoSave = true
)
public class UserNavigationView {
    // 导航视图逻辑
}
```

## 9. 注解使用最佳实践

### 9.1 注解分类与作用

在ooder平台中，注解分为三个层次：必须的、必要的和增强的。

**必须的注解**（用于建立基本的组件映射关系）：
1. **组件类型注解**：@InputAnnotation、@ButtonAnnotation、@ComboBoxAnnotation等
2. **视图类型注解**：@FormAnnotation、@TreeGridAnnotation、@TabsAnnotation等
3. **服务注解**：@RestController、@Service、@Aggregation等

**必要的修饰注解**（用于完善组件的外观和基本行为）：
1. **外观注解**：@CustomAnnotation（caption、index、imageClass等属性）
2. **布局注解**：@FormAnnotation（col、row、borderType等属性）
3. **数据注解**：@Uid、@Pid（标识记录关系）

**增强的注解**（用于扩展组件的功能和交互）：
1. **行为注解**：@APIEventAnnotation（定义事件交互行为）
2. **列表增强注解**：@CustomListAnnotation（增强枚举字段的数据能力）
3. **复合注解**：Combo组件相关注解（@ComboAnnotation、@ComboNumberAnnotation等）

### 9.2 注解使用规范

1. **注解组合原则**：
   - 遵循注解优先级规则，组件类型注解具有最高优先级
   - 合理组合不同类型的注解以实现完整的组件定义
   - 避免重复配置相同属性

2. **注解配置规范**：
   - 遵循最小化配置原则，只配置必要的属性
   - 保持注解配置的一致性和可读性
   - 合理使用默认值减少配置复杂度

## 10. 业务诉求转换最佳实践

### 10.1 转换思考过程

在将业务诉求转变为Java代码时，需要遵循以下思考过程：

1. **识别业务元素**：
   - 确定需要展示的数据项
   - 识别用户交互操作
   - 分析数据流向和处理逻辑

2. **选择合适组件**：
   - 根据数据类型选择合适的UI组件
   - 根据用户操作选择合适的交互方式
   - 考虑组件的可复用性和扩展性

3. **定义组件属性**：
   - 使用组件类型注解定义基本属性
   - 使用@CustomAnnotation定义外观属性
   - 使用行为注解定义交互行为

4. **建立映射关系**：
   - 确保字段名称与业务含义一致
   - 建立字段类型与UI组件的强引用关系
   - 定义数据验证和处理逻辑

5. **实现服务逻辑**：
   - 创建对应的服务类并确保Web可访问性
   - 实现业务逻辑处理方法
   - 定义数据返回格式和异常处理

### 10.2 示例：员工信息管理业务诉求转换

业务诉求：创建一个员工信息管理页面，包含员工基本信息录入、保存和查询功能。

```java
// 视图类定义
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 8,
    customService = {EmployeeManagementService.class}
)
public class EmployeeManagementView {
    // 员工ID输入框
    @InputAnnotation(maxlength = 10, required = true)
    @CustomAnnotation(caption = "员工ID", index = 1, imageClass = "fa-solid fa-id-card")
    @Uid
    private String employeeId;
    
    // 员工姓名输入框
    @InputAnnotation(maxlength = 20, required = true)
    @CustomAnnotation(caption = "员工姓名", index = 2, imageClass = "fa-solid fa-user")
    private String employeeName;
    
    // 部门下拉框
    @ComboBoxAnnotation(dropListWidth = "200px")
    @CustomListAnnotation(enumClass = DepartmentType.class)
    @CustomAnnotation(caption = "部门", index = 3, imageClass = "fa-solid fa-building")
    private String department;
    
    // 入职日期选择器
    @DatePickerAnnotation(timeInput = true)
    @CustomAnnotation(caption = "入职日期", index = 4, imageClass = "fa-solid fa-calendar")
    private String hireDate;
    
    // 保存按钮
    @ButtonAnnotation(buttonType = ButtonType.primary)
    @CustomAnnotation(caption = "保存", index = 5, imageClass = "fa-solid fa-save")
    @APIEventAnnotation(
        bindAction = {CustomAction.SAVE},
        customRequestData = {RequestPathEnum.CURRFORM},
        beforeInvoke = CustomBeforInvoke.BUSY,
        onExecuteSuccess = CustomOnExecueSuccess.MESSAGE
    )
    private String saveButton;
    
    // 查询按钮
    @ButtonAnnotation(buttonType = ButtonType.secondary)
    @CustomAnnotation(caption = "查询", index = 6, imageClass = "fa-solid fa-search")
    @APIEventAnnotation(
        bindAction = {CustomAction.SEARCH},
        customRequestData = {RequestPathEnum.CURRFORM},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    private String searchButton;
    
    // Getters and Setters
}

// 服务类定义
@Service
@RestController
@RequestMapping("/employee/management")
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
public class EmployeeManagementService {
    
    @PostMapping("/save")
    public ResultModel<Boolean> saveEmployee(@RequestBody EmployeeManagementView view) {
        // 保存员工信息逻辑
        try {
            // 处理保存逻辑
            return ResultModel.success(true, "保存成功");
        } catch (Exception e) {
            return ResultModel.error("保存失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/search")
    public ResultModel<EmployeeManagementView> searchEmployee(@RequestBody SearchParams params) {
        // 查询员工信息逻辑
        try {
            // 处理查询逻辑
            EmployeeManagementView view = new EmployeeManagementView();
            // 填充查询结果
            return ResultModel.success(view);
        } catch (Exception e) {
            return ResultModel.error("查询失败: " + e.getMessage());
        }
    }
}
```