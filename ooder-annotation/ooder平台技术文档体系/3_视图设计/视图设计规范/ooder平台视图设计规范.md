# ooder平台视图设计规范

## 目录

1. [概述](#1-概述)
2. [UI组件设计规范](#2-ui组件设计规范)
   - 2.1 组件化设计核心思想
   - 2.2 原子化设计核心思想
   - 2.3 简单UI组件设计规范
   - 2.4 组件设计原则
   - 2.5 组件实现规范
3. [视图类型规范](#3-视图类型规范)
   - 3.1 表单视图
   - 3.2 列表视图
   - 3.3 门户视图
   - 3.4 聚合视图
   - 3.5 面板表单视图
   - 3.6 SVG画布表单视图
4. [视图注解规范](#4-视图注解规范)
   - 4.1 视图类注解
   - 4.2 字段注解
   - 4.3 布局注解
5. [数据结构规范](#5-数据结构规范)
   - 5.1 视图对象双重角色
   - 5.2 字段注解规范
   - 5.3 数据一致性
6. [视图结构规范](#6-视图结构规范)
   - 6.1 类结构规范
   - 6.2 字段结构规范
   - 6.3 方法结构规范
7. [嵌套视图规范](#7-嵌套视图规范)
   - 7.1 嵌套视图设计原则
   - 7.2 容器嵌套规范
   - 7.3 Field注解体系
   - 7.4 嵌套视图实现机制
   - 7.5 PanelFormView嵌套机制
8. [导航设计规范](#8-导航设计规范)
   - 8.1 导航类型规范
   - 8.2 导航注解规范
   - 8.3 导航数据规范
   - 8.4 导航事件规范
   - 8.5 导航服务规范
9. [视图关联规范](#9-视图关联规范)
   - 9.1 父子关系
   - 9.2 顺序关系
   - 9.3 并行关系

## 1. 概述

ooder平台的视图设计规范旨在统一视图开发标准，确保视图的一致性、可维护性和可扩展性。视图是用户界面的核心组成部分，负责数据展示和用户交互。

视图设计遵循组件化和原子化理念，将用户界面拆分为可复用的组件模块，并通过组合这些原子组件构建复杂的应用界面。

## 2. UI组件设计规范

### 2.1 组件化设计核心思想

在ooder平台中，UI组件是构成用户界面的基本单元。每个组件都具有明确的职责和功能，通过组合不同的组件来构建复杂的用户界面。

**组件分类：**
1. **简单UI组件（Field）**：基础的UI元素，如输入框、按钮、下拉框等
2. **复合组件**：由多个简单组件组合而成的复杂组件
3. **容器组件**：用于容纳其他组件的布局容器

**组件与注解的关系：**
- 每个UI组件通过对应的注解来定义其属性和行为
- 字段名称、字段类型与UI组件建立强引用关系
- 在字段上添加特定的注解（如@InputAnnotation、@ButtonAnnotation等）是对组件属性的默认配置

### 2.2 原子化设计核心思想

在ooder平台中，原子化设计将UI组件划分为不同层级：
- **原子组件（Atoms）**：最小的UI单元，如按钮、输入框、标签等
- **分子组件（Molecules）**：由多个原子组件组合而成的功能单元
- **组织组件（Organisms）**：由分子组件和原子组件组合而成的复杂组件
- **模板（Templates）**：页面级别的布局结构
- **页面（Pages）**：具体的页面实现

**功能原子化原则：**
1. **单一职责**：每个原子组件只负责一个特定的功能
2. **高内聚**：组件内部功能紧密相关
3. **低耦合**：组件之间依赖关系简单清晰
4. **可复用**：组件可在不同场景下重复使用

**组件封装原则：**
1. **类封装**：每个原子组件在前端采用class封装
2. **属性抽离**：将样式、行为、数据属性从虚拟DOM中抽离
3. **生命周期管理**：在class中实现组件的生命周期管理
4. **映射关系**：为每个原子组件创建虚拟DOM节点与原生DOM形成映射关系

### 2.3 简单UI组件设计规范

#### 2.3.1 Field组件概念
在ooder平台中，简单的UI组件以"field"来表示。每个field组件具有以下特征：
- 与特定的字段名称关联
- 与特定的字段类型建立强引用关系
- 通过注解定义其UI属性和行为

#### 2.3.2 注解体系
##### 基础外观注解
@CustomAnnotation是所有UI属性的基础外观描述注解，包含以下核心属性：
- `id`：组件唯一标识
- `caption`：组件显示标题
- `imageClass`：图标CSS类
- `index`：组件排列顺序
- `hidden`：是否隐藏
- `readonly`：是否只读
- `disabled`：是否禁用

##### 组件类型注解
根据不同类型的UI组件，使用相应的类型注解：
- @InputAnnotation：输入框组件
- @ButtonAnnotation：按钮组件
- @ComboBoxAnnotation：下拉框组件
- @DatePickerAnnotation：日期选择器组件
- @CheckBoxAnnotation：复选框组件
- @PanelFieldAnnotation：面板组件
- @TreeGridFieldAnnotation：表格组件

#### 2.3.3 Prop配置注解
Prop配置注解采用复合租借的方式，通过组合不同的注解来定义组件的完整属性。

##### 复合注解示例
``java
@InputAnnotation(
    maxlength = 50,
    multiLines = false
)
@CustomAnnotation(
    caption = "用户名",
    index = 1,
    tips = "请输入用户名"
)
private String username;
```

##### 注解优先级
当多个注解定义相同属性时，遵循以下优先级规则：
1. 组件类型注解（如@InputAnnotation）具有最高优先级
2. @CustomAnnotation具有基础优先级
3. 默认值作为兜底配置

### 2.4 组件设计原则

1. **单一职责原则**：每个UI组件应具有明确且单一的职责，只负责特定的UI功能。
2. **可复用性原则**：组件设计应考虑通用性，使其能够在不同场景下复用。
3. **可配置性原则**：组件应提供丰富的配置选项，以满足不同业务场景的需求。
4. **一致性原则**：相同类型的组件在不同页面中应保持外观和行为的一致性。
5. **功能原子化原则**：每个原子组件只负责一个特定的功能。

### 2.5 组件实现规范

1. **注解定义规范**：
   - 所有组件注解必须使用@CustomClass注解声明其视图类型和组件类型
   - 注解属性应具有合理的默认值
   - 注解应提供详细的JavaDoc说明

2. **字段命名规范**：
   - 字段名称应清晰表达组件的用途
   - 遵循驼峰命名法
   - 避免使用过于简单的名称

3. **组件组合规范**：
   - 容器组件可以包含其他组件
   - 组件嵌套应遵循合理的层级结构
   - 避免过深的组件嵌套

## 3. 视图类型规范

### 3.1 表单视图

表单视图用于数据录入和展示，使用@FormAnnotation注解定义。

**核心特征：**
- 使用@FormAnnotation定义表单布局和属性
- 支持字段分组和布局控制
- 可配置边框类型、列数、行数等属性

**示例：**
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {AttendanceCheckInService.class}
)
public class AttendanceCheckInView {
    // 字段定义
}
```

### 3.2 列表视图

列表视图用于数据展示和操作，使用@TreeGridAnnotation注解定义。

**核心特征：**
- 遵循列表视图单一性原则：一个视图类应仅定义一种类型的视图
- 配置默认的@Uid作为行主键
- 支持列配置和数据增强

**示例：**
```java
@TreeGridAnnotation(
    customService = {AttendanceQueryService.class},
    showHeader = true,
    colSortable = true
)
public class AttendanceQueryListView {
    // 字段定义
}
```

### 3.3 门户视图

门户视图用于整合多个信息模块，使用@FormAnnotation注解定义。

**核心特征：**
- 作为模块入口页面
- 整合多个功能入口
- 提供快捷操作按钮

**示例：**
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 3,
    row = 4,
    customService = {AttendancePortalService.class}
)
public class AttendancePortalView {
    // 字段定义
}
```

### 3.4 聚合视图

聚合视图用于处理具有关联关系的数据模型，使用@TabsAnnotation注解定义。

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

### 3.5 面板表单视图

面板表单视图用于在面板容器中展示的表单，使用@PanelFormAnnotation注解定义。

**核心特征：**
- 使用@PanelFormAnnotation定义面板表单布局和属性
- 适用于面板容器中的表单展示
- 支持停靠布局和可切换特性
- 可配置边框类型、标题等属性
- 作为核心容器，允许多重嵌套

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "面板表单",
    borderType = BorderType.inset,
    customService = {MyPanelService.class},
    toggle = true,
    noFrame = false
)
public class MyPanelFormView {
    // 字段定义
}
```

### 3.6 SVG画布表单视图

SVG画布表单视图用于矢量图绘制和展示，使用@SVGPaperFormAnnotation注解定义。

**核心特征：**
- 使用@SVGPaperFormAnnotation定义SVG画布表单属性
- 适用于矢量图绘制和展示场景
- 支持丰富的SVG图形相关属性配置
- 可配置宽度、高度、可选择性等属性

**示例：**
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {FlowChartService.class}
)
public class FlowChartView {
    // 字段定义
}
```

## 4. 视图注解规范

### 4.1 视图类注解

1. **@FormAnnotation**：表单视图注解
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {ServiceClass.class}
)
```

2. **@TreeGridAnnotation**：列表视图注解
```java
@TreeGridAnnotation(
    customService = {ServiceClass.class},
    showHeader = true,
    colSortable = true
)
```

3. **@TabsAnnotation**：聚合视图注解
```java
@TabsAnnotation(
    caption = "标题",
    autoSave = true,
    barLocation = BarLocationType.top
)
```

4. **@PanelFormAnnotation**：面板表单注解
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "面板表单",
    borderType = BorderType.inset,
    customService = {ServiceClass.class},
    toggle = true,
    noFrame = false
)
```

5. **@SVGPaperFormAnnotation**：SVG画布表单注解
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {ServiceClass.class}
)
```

### 4.2 字段注解

1. **@InputAnnotation**：输入框注解
```java
@InputAnnotation(
    maxlength = 20
)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

2. **@DatePickerAnnotation**：日期选择注解
```java
@DatePickerAnnotation(
    timeInput = true
)
@CustomAnnotation(caption = "签到日期", index = 3)
private String checkInDate;
```

3. **@ComboBoxAnnotation**：下拉框注解
```java
@ComboBoxAnnotation
@CustomListAnnotation(
    enumClass = EnumClass.class
)
@CustomAnnotation(caption = "类型", index = 6)
private EnumType fieldType;
```

### 4.3 布局注解

1. **@TreeGridColItemAnnotation**：列表列注解
```java
@TreeGridColItemAnnotation(title = "员工姓名", width = "120px")
private String employeeName;
```

2. **@ButtonAnnotation**：按钮注解
```java
@ButtonAnnotation
@CustomAnnotation(caption = "查询", index = 7, imageClass = "fa-solid fa-search")
private String queryButton;
```

## 5. 数据结构规范

### 5.1 视图对象双重角色

视图对象在ooder平台中具有双重角色：
1. **数据传输对象(DTO)**：作为数据载体，用于前后端数据传输
2. **视图配置载体**：通过注解定义视图的外观、行为和交互

### 5.2 字段注解规范

**输入控件注解：**
- @InputAnnotation：输入框配置
- @DatePickerAnnotation：日期选择器配置
- @ComboBoxAnnotation：下拉框配置
- @ButtonAnnotation：按钮配置

**列表增强注解：**
- @CustomListAnnotation：自定义列表配置，用于增强枚举字段的数据能力

### 5.3 数据一致性规范

- 确保视图对象在前后端传输过程中保持数据一致性
- 使用@JSONField控制枚举序列化格式
- 合理设计字段类型和验证规则

## 6. 视图结构规范

### 6.1 类结构规范

视图类应遵循以下结构规范：
1. 类注解定义视图类型和属性
2. 字段定义及相应注解
3. 必要的getter/setter方法
4. 可选的业务逻辑方法

**示例：**
```
/**
 * 视图类描述
 */
@ViewAnnotation(...)
public class ViewClassName {
    
    // 字段定义
    @FieldAnnotation(...)
    @CustomAnnotation(...)
    private FieldType fieldName;
    
    // Getter/Setter方法
    public FieldType getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(FieldType fieldName) {
        this.fieldName = fieldName;
    }
}
```

### 6.2 字段结构规范

1. **字段注解顺序**：
```java
@InputAnnotation(...)          // 字段类型注解
@CustomListAnnotation(...)     // 增强注解（如需要）
@CustomAnnotation(...)         // 自定义注解
private String fieldName;      // 字段定义
```

2. **字段命名规范**：
- 使用驼峰命名法
- 字段名应清晰表达业务含义
- 避免使用缩写和简写

### 6.3 方法结构规范

1. **Getter/Setter方法**：
```java
public FieldType getFieldName() {
    return fieldName;
}

public void setFieldName(FieldType fieldName) {
    this.fieldName = fieldName;
}
```

2. **业务方法**（如需要）：
```
/**
 * 方法描述
 */
public ReturnType businessMethod() {
    // 方法实现
}
```

## 7. 嵌套视图规范

### 7.1 嵌套视图设计原则

1. **层次化设计原则**：
   - 嵌套视图应遵循清晰的层次结构
   - 父级视图负责整体布局和子视图协调
   - 子视图专注于自身功能实现

2. **职责分离原则**：
   - 父级视图：负责布局控制和子视图管理
   - 子视图：负责具体业务功能实现
   - 通过bindClass实现视图与服务的解耦

3. **可复用性原则**：
   - 设计通用的嵌套组件，提高代码复用率
   - 通过参数化配置实现组件的灵活应用

### 7.2 容器嵌套规范

#### 7.2.1 Panel容器嵌套

Panel作为基础容器，支持自由布局，适用于自定义表单场景。Panel容器嵌套时具有以下特点：
- 支持多重嵌套
- 元素可自行定义定位与大小
- 不同于Form的强制布局

**示例：**
```java
@PanelAnnotation(
    borderType = BorderType.inset,
    dock = Dock.fill
)
public class ParentPanel {
    // 嵌套子Panel
    private ChildPanel childPanel;
    
    // 嵌套子Form
    private ChildForm childForm;
    
    // 嵌套子TreeGrid
    private ChildTreeGrid childTreeGrid;
}
```

#### 7.2.2 Form容器嵌套

Form容器具有强制布局特性，适用于结构化数据录入场景。

**示例：**
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7
)
public class ParentForm {
    // 嵌套子Form
    private ChildForm childForm;
    
    // 嵌套子TreeGrid
    private ChildTreeGrid childTreeGrid;
}
```

#### 7.2.3 TreeGrid容器嵌套

TreeGrid容器适用于数据展示和操作场景。

**示例：**
```java
@TreeGridAnnotation(
    customService = {ParentTreeGridService.class},
    showHeader = true
)
public class ParentTreeGrid {
    // TreeGrid中嵌套其他视图组件
    // 通过TreeGridFieldAnnotation等注解实现
}
```

#### 7.2.4 Block容器嵌套

Block是UI组件的基础容器，同时兼具数据容器特点。

**示例：**
```java
// Block容器嵌套示例
public class ParentBlock {
    // Block中嵌套其他组件
    private ChildComponent childComponent;
}
```

#### 7.2.5 PanelForm容器嵌套

PanelForm作为核心容器，支持自由布局并允许多重嵌套。PanelForm容器嵌套时具有以下特点：
- 支持多重嵌套，可作为父级容器容纳其他视图组件
- 元素可自行定义定位与大小
- 支持停靠布局和可切换特性
- 可配置边框类型、标题等属性

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "面板表单",
    borderType = BorderType.inset,
    customService = {ParentPanelFormService.class},
    toggle = true,
    noFrame = false
)
public class ParentPanelForm {
    // 嵌套子Panel
    private ChildPanel childPanel;
    
    // 嵌套子Form
    private ChildForm childForm;
    
    // 嵌套子TreeGrid
    private ChildTreeGrid childTreeGrid;
    
    // 嵌套子PanelForm
    private ChildPanelForm childPanelForm;
}
```

#### 7.2.6 SVGPageForm容器嵌套

SVGPageForm作为矢量图容器，同样支持嵌套其他视图组件。SVGPageForm容器嵌套时具有以下特点：
- 支持多重嵌套，可作为父级容器容纳其他视图组件
- 专为SVG矢量图形设计和展示而优化
- 同样支持**FieldAnnotation嵌套子结构
- 提供丰富的SVG图形相关属性配置

**示例：**
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {FlowChartService.class}
)
public class FlowChartView {
    // 嵌套子组件
    private ChildComponent childComponent;
}
```

### 7.3 Field注解体系

针对嵌套视图设计，ooder平台提供了独立的Field注解体系，用于在容器嵌套时定义子组件的外观和行为。

#### 7.3.1 TreeGridFieldAnnotation

TreeGridFieldAnnotation用于在嵌套视图中定义TreeGrid类型的子组件。

**核心属性：**
- bindClass：绑定的视图聚合服务类
- borderType：边框类型
- bgimg：背景图片
- imageClass：图标CSS类
- backgroundColor：背景颜色

**使用示例：**
```java
@TreeGridFieldAnnotation(
    bindClass = ChildTreeGridService.class,
    borderType = BorderType.inset,
    imageClass = "fa-solid fa-table"
)
@CustomAnnotation(caption = "子表格", index = 1)
private ChildTreeGrid childTreeGrid;
```

**适用场景：**
- 在Form中嵌套TreeGrid组件
- 在Panel中嵌套TreeGrid组件
- 在PanelForm中嵌套TreeGrid组件
- 在其他容器中嵌套TreeGrid组件

#### 7.3.2 FormFieldAnnotation

FormFieldAnnotation用于在嵌套视图中定义Form类型的子组件。

**核心属性：**
- borderType：边框类型
- bgimg：背景图片
- imageClass：图标CSS类
- backgroundColor：背景颜色

**使用示例：**
```java
@FormFieldAnnotation(
    borderType = BorderType.inset,
    imageClass = "fa-solid fa-form",
    backgroundColor = "#f5f5f5"
)
@CustomAnnotation(caption = "子表单", index = 2)
private ChildForm childForm;
```

**适用场景：**
- 在Form中嵌套子Form组件
- 在Panel中嵌套Form组件
- 在PanelForm中嵌套Form组件
- 在Tabs中嵌套Form组件

#### 7.3.3 BlockFieldAnnotation

BlockFieldAnnotation用于在嵌套视图中定义Block类型的子组件。

**核心属性：**
- borderType：边框类型
- dock：停靠位置
- resizer：是否可调整大小
- sideBarCaption：侧边栏标题
- sideBarType：侧边栏类型
- sideBarStatus：侧边栏状态
- sideBarSize：侧边栏大小
- background：背景颜色

**使用示例：**
```java
@BlockFieldAnnotation(
    borderType = BorderType.inset,
    dock = Dock.fill,
    resizer = true,
    sideBarCaption = "侧边栏",
    sideBarStatus = SideBarStatusType.expand
)
@CustomAnnotation(caption = "子块", index = 3)
private ChildBlock childBlock;
```

**适用场景：**
- 在Panel中嵌套Block组件
- 在Form中嵌套Block组件
- 在PanelForm中嵌套Block组件
- 构建复杂的布局结构

#### 7.3.4 PanelFieldAnnotation

PanelFieldAnnotation用于在嵌套视图中定义Panel类型的子组件。

**核心属性：**
- dock：停靠位置
- caption：标题
- html：HTML内容
- image：图片路径
- imageClass：图标CSS类
- borderType：边框类型
- noFrame：是否无边框
- hAlign：水平对齐方式
- toggle：是否可切换

**使用示例：**
```java
@PanelFieldAnnotation(
    dock = Dock.fill,
    caption = "子面板",
    borderType = BorderType.inset,
    toggle = true
)
@CustomAnnotation(caption = "子面板", index = 4)
private ChildPanel childPanel;
```

**适用场景：**
- 在PanelForm中嵌套Panel组件
- 构建复杂的嵌套布局结构

#### 7.3.5 TreeFieldAnnotation

TreeFieldAnnotation用于在嵌套视图中定义Tree类型的子组件。

**核心属性：**
- bindClass：绑定的视图聚合服务类
- borderType：边框类型
- bgimg：背景图片
- imageClass：图标CSS类
- backgroundColor：背景颜色

**使用示例：**
```java
@TreeFieldAnnotation(
    bindClass = ChildTreeService.class,
    borderType = BorderType.inset,
    imageClass = "fa-solid fa-tree"
)
@CustomAnnotation(caption = "子树", index = 5)
private ChildTree childTree;
```

**适用场景：**
- 在PanelForm中嵌套Tree组件
- 构建层次化数据展示结构

#### 7.3.6 TabsFieldAnnotation

TabsFieldAnnotation用于在嵌套视图中定义Tabs类型的子组件。

**核心属性：**
- selectable：是否可选择
- iframeAutoLoad：iframe自动加载URL
- html：HTML内容
- width：宽度
- height：高度
- overflow：溢出处理方式
- scaleChildren：是否缩放子元素

**使用示例：**
```java
@TabsFieldAnnotation(
    selectable = true,
    width = "100%",
    height = "300px"
)
@CustomAnnotation(caption = "标签页", index = 6)
private ChildTabs childTabs;
```

**适用场景：**
- 在PanelForm中嵌套Tabs组件
- 构建多页面展示结构

#### 7.3.7 ModuleRefFieldAnnotation

ModuleRefFieldAnnotation用于在嵌套视图中引用其他模块的子组件。

**核心属性：**
- src：模块源路径
- dynLoad：是否动态加载
- embed：嵌入类型
- dock：停靠位置
- bindClass：绑定的视图聚合服务类
- append：追加类型

**使用示例：**
```java
@ModuleRefFieldAnnotation(
    src = "/module/child",
    bindClass = ChildModuleService.class,
    dock = Dock.fill
)
@CustomAnnotation(caption = "模块引用", index = 7)
private ChildModule childModule;
```

**适用场景：**
- 在PanelForm中引用其他模块
- 实现模块间解耦和复用

#### 7.3.8 其他Field注解

ooder平台还提供了多种其他Field注解用于在嵌套视图中定义不同类型的组件：
- GalleryFieldAnnotation：用于嵌套画廊组件
- ButtonViewsFieldAnnotation：用于嵌套按钮视图组件
- NavFoldingTabsFieldAnnotation：用于嵌套折叠标签页组件
- MenuBarFieldAnnotation：用于嵌套菜单栏组件
- DivFieldAnnotation：用于嵌套DIV层组件
- SVGPaperFieldAnnotation：用于嵌套SVG画布组件
- ContentBlockFieldAnnotation：用于嵌套内容块组件
- EChartFieldAnnotation：用于嵌套ECharts图表组件
- FChartFieldAnnotation：用于嵌套FusionCharts图表组件
- GroupFieldAnnotation：用于嵌套分组组件
- LayoutFieldAnnotation：用于嵌套布局组件
- StacksFieldAnnotation：用于嵌套堆栈组件
- TitleBlockFieldAnnotation：用于嵌套标题块组件
- OpinionFieldAnnotation：用于嵌套意见组件
- ModuleEmbedFieldAnnotation：用于嵌套模块嵌入组件
- ComboFieldAnnotation：用于嵌套Combo组件

对于以上未详细说明的Field注解，其使用方式与已介绍的Field注解类似，都需要：
1. 在父级视图中定义相应的字段
2. 为字段添加对应的Field注解
3. 配合@CustomAnnotation定义外观属性
4. 确保服务类具有Web可访问性

### 7.4 嵌套视图实现机制

#### 7.4.1 bindClass解耦机制

bindClass作为解耦的核心干节点，在嵌套视图中起到关键作用：
- 实现视图与服务的解耦
- 作为视图聚合服务类的核心属性
- 必须包含视图入口方法，且具有Web可访问性

**示例：**
```java
@TreeGridFieldAnnotation(
    bindClass = ChildViewService.class  // bindClass实现解耦
)
@CustomAnnotation(caption = "嵌套视图", index = 1)
private ChildView childView;
```

#### 7.4.2 递归嵌套实现

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

#### 7.4.3 视图引用机制

支持视图间的引用关系，实现模块化设计：

**实现方式：**
- 通过字段引用其他视图类
- 在父级视图中直接通过field或method方式创建子视图
- 使用bindClass属性实现视图与服务的解耦

### 7.5 PanelFormView嵌套机制

PanelFormView是ooder平台中的核心容器视图，支持多重嵌套其他类型的视图组件。通过在PanelFormView定义文件中添加子视图的入口方法，并使用相应的****FieldAnnotation注解，可以将其他视图作为PanelFormView的字段引入。

PanelFormView的嵌套机制与ModuleAnnotation嵌入方式有所不同：
- 内嵌视图：在内部完成实例化，附属于PanelFormView，适用于人员基本信息和人员详细信息这种附属结构
- Module引用：通过ModuleAnnotation方式嵌入，适用于独立模块间的引用

#### 7.5.1 内嵌视图方式

内嵌视图是在PanelFormView内部完成实例化，附属于PanelFormView的嵌套方式。这种方式适用于具有附属关系的视图结构，如人员基本信息和人员详细信息。

**实现步骤：**
1. 在PanelFormView类中定义子视图字段
2. 为子视图字段添加相应的****FieldAnnotation注解
3. 为子视图字段添加@CustomAnnotation注解定义外观属性
4. 确保子视图服务类具有Web可访问性且包含视图入口方法

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "人员信息",
    borderType = BorderType.inset,
    customService = {PersonInfoService.class}
)
public class PersonInfoView {
    // 基本信息表单（内嵌）
    @FormFieldAnnotation(
        borderType = BorderType.inset
    )
    @CustomAnnotation(caption = "基本信息", index = 1)
    private BasicInfoForm basicInfoForm;
    
    // 详细信息表单（内嵌）
    @FormFieldAnnotation(
        borderType = BorderType.inset
    )
    @CustomAnnotation(caption = "详细信息", index = 2)
    private DetailInfoForm detailInfoForm;
}
```

#### 7.5.2 Module引用方式

Module引用方式是通过ModuleAnnotation方式嵌入其他模块的视图组件。这种方式适用于独立模块间的引用关系。

**实现步骤：**
1. 在PanelFormView类中定义子视图字段
2. 为子视图字段添加@ModuleRefFieldAnnotation注解
3. 为子视图字段添加@CustomAnnotation注解定义外观属性
4. 确保被引用模块具有正确的访问路径和服务类

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
        bindClass = BasicInfoService.class
    )
    @CustomAnnotation(caption = "人员基本信息", index = 1)
    private BasicInfoModule basicInfoModule;
}
```

## 8. 导航设计规范

### 8.1 导航类型规范

#### 8.1.1 树形导航

树形导航用于展示具有层级关系的数据结构，支持节点的展开/折叠操作。

**核心特性：**
- 层级结构展示
- 节点展开/折叠
- 动态数据加载
- 状态保持

**适用场景：**
- 组织架构管理
- 分类目录浏览
- 功能菜单导航

**示例：**
```java
@NavTreeViewAnnotation(
    expression = "attendanceTree",
    dataUrl = "/attendance/navigation/tree/data",
    loadChildUrl = "/attendance/navigation/tree/children",
    rootId = "0"
)
public void treeNavigationView() {
    // 树形导航视图逻辑
}
```

#### 8.1.2 TAB导航

TAB导航用于在多个同级视图间进行切换，提供并行内容展示。

**核心特性：**
- TAB页切换
- 内容面板切换
- 状态保持
- 自动保存

**适用场景：**
- 信息分类展示
- 多步骤表单
- 功能模块切换

**示例：**
```java
@NavMTabsViewAnnotation(
    expression = "attendanceMTabs",
    autoSave = true
)
public void mTabsView() {
    // 多TAB导航视图逻辑
}
```

#### 8.1.3 折叠导航

折叠导航结合了树形结构和TAB导航的特点，支持折叠/展开操作。

**核心特性：**
- 折叠/展开功能
- 层级结构展示
- 状态保持
- 自动保存

**适用场景：**
- 复杂功能导航
- 多级菜单展示
- 空间优化展示

**示例：**
```java
@NavFoldingTabsViewAnnotation(
    expression = "attendanceTabs",
    autoSave = true
)
public void foldingTabsNavigationView() {
    // 折叠式TAB导航视图逻辑
}
```

#### 8.1.4 多级导航

多级导航支持复杂的层级嵌套结构，提供灵活的导航方式。

**核心特性：**
- 多级嵌套结构
- 动态内容加载
- 状态管理
- 事件处理

**适用场景：**
- 复杂业务系统
- 多维度数据展示
- 综合管理平台

### 8.2 导航注解规范

#### 8.2.1 NavTreeViewAnnotation

用于实现左树右面板的导航模式。

**属性说明：**
```java
@NavTreeViewAnnotation(
    expression = "导航表达式标识",
    dataUrl = "根节点数据加载URL",
    loadChildUrl = "子节点数据加载URL",
    rootId = "根节点ID",
    itemType = ResponsePathTypeEnum.TREEVIEW,
    autoSave = false
)
```

**核心属性：**
- `expression`：表达式标识符，用于标识导航实例
- `dataUrl`：根节点数据加载URL
- `loadChildUrl`：子节点数据加载URL
- `rootId`：根节点ID
- `itemType`：响应数据类型
- `autoSave`：是否自动保存状态

#### 8.2.2 NavFoldingTabsViewAnnotation

用于实现可折叠的TAB导航模式。

**属性说明：**
```java
@NavFoldingTabsViewAnnotation(
    expression = "导航表达式标识",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TABS,
    saveUrl = "状态保存URL"
)
```

**核心属性：**
- `expression`：表达式标识符
- `autoSave`：是否自动保存导航状态
- `itemType`：响应数据类型
- `saveUrl`：状态保存URL

#### 8.2.3 NavFoldingTreeViewAnnotation

用于实现可折叠的树形导航模式。

**属性说明：**
```java
@NavFoldingTreeViewAnnotation(
    expression = "导航表达式标识",
    dataUrl = "树形数据加载URL",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TREEVIEW
)
```

**核心属性：**
- `expression`：表达式标识符
- `dataUrl`：树形数据加载URL
- `autoSave`：是否自动保存导航状态
- `itemType`：响应数据类型

#### 8.2.4 NavMTabsViewAnnotation

用于实现多TAB页的导航模式。

**属性说明：**
```java
@NavMTabsViewAnnotation(
    expression = "导航表达式标识",
    autoSave = true,
    itemType = ResponsePathTypeEnum.TABS,
    dataUrl = "数据加载URL",
    reSetUrl = "重置URL"
)
```

**核心属性：**
- `expression`：表达式标识符
- `autoSave`：是否自动保存导航状态
- `itemType`：响应数据类型
- `dataUrl`：数据加载URL
- `reSetUrl`：重置URL

### 8.3 导航数据规范

#### 8.3.1 树形节点数据

树形节点数据模型用于描述树形导航的节点结构。

**数据模型：**
```java
public class TreeNode {
    private String id;          // 节点唯一标识
    private String pid;         // 父节点ID
    private String name;        // 节点名称
    private String type;        // 节点类型
    private String icon;        // 节点图标
    private boolean isLeaf;     // 是否为叶子节点
    private List<TreeNode> children; // 子节点列表
    
    // getters and setters
}
```

**服务实现：**
```java
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
        
        resultModel.setData(nodes);
        resultModel.setRequestStatus(1);
    } catch (Exception e) {
        // 发生错误时返回ErrorResultModel封装的错误信息
        ErrorListResultModel<List<TreeNode>> errorResult = new ErrorListResultModel<>(e.getMessage());
        errorResult.setErrcode(1000);
        errorResult.setRequestStatus(-1);
        return errorResult;
    }
    
    return resultModel;
}
```

#### 8.3.2 TAB项数据

TAB项数据模型用于描述TAB导航的项结构。

**数据模型：**
```java
public class TabItem {
    private String id;          // TAB项ID
    private String title;       // TAB项标题
    private String icon;        // TAB项图标
    private String contentUrl;  // 内容URL
    
    // getters and setters
}
```

**服务实现：**
```java
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
        
        resultModel.setData(tabs);
        resultModel.setRequestStatus(1);
    } catch (Exception e) {
        // 发生错误时返回ErrorResultModel封装的错误信息
        ErrorListResultModel<List<TabItem>> errorResult = new ErrorListResultModel<>(e.getMessage());
        errorResult.setErrcode(1000);
        errorResult.setRequestStatus(-1);
        return errorResult;
    }
    
    return resultModel;
}
```

#### 8.3.3 导航状态数据

导航状态数据用于保存和恢复导航组件的状态。

**状态数据模型：**
```java
public class NavigationState {
    private String currentView;     // 当前视图
    private String selectedNode;    // 选中节点
    private List<String> expandedNodes; // 展开节点列表
    private List<String> activeTabs;    // 激活TAB列表
    
    // getters and setters
}
```

### 8.4 导航事件规范

#### 8.4.1 节点选择事件

处理树形导航中节点的选择操作。

**事件处理：**
```java
@APIEventAnnotation(
    bindTreeEvent = {TreeViewEventEnum.onClick},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.REDIRECT}
)
@PostMapping("/select")
@ResponseBody
public ResultModel<String> onNodeSelect(@RequestBody TreeNodeSelectionData data) {
    String selectedNodeId = data.getSelectedNodeId();
    
    // 根据选择的节点跳转到相应页面
    String targetPage = navigationService.getTargetPage(selectedNodeId);
    
    ResultModel<String> result = new ResultModel<>();
    result.setData(targetPage);
    return result;
}
```

#### 8.4.2 TAB切换事件

处理TAB导航中TAB页的切换操作。

**事件处理：**
```java
@APIEventAnnotation(
    bindTabsEvent = {CustomTabsEvent.onTabChange},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.FORM}
)
@PostMapping("/tabChange")
@ResponseBody
public ResultModel<Boolean> onTabChange(@RequestBody TabChangeData data) {
    String activeTabId = data.getActiveTabId();
    
    // 处理TAB切换逻辑
    boolean success = tabService.switchToTab(activeTabId);
    
    ResultModel<Boolean> result = new ResultModel<>();
    result.setData(success);
    return result;
}
```

#### 8.4.3 折叠展开事件

处理折叠导航中节点的折叠/展开操作。

**事件处理：**
```java
@APIEventAnnotation(
    bindTreeEvent = {TreeViewEventEnum.beforeFold, TreeViewEventEnum.beforeExpand},
    customRequestData = {RequestPathEnum.CURRFORM},
    customResponseData = {ResponsePathEnum.MESSAGE}
)
@PostMapping("/foldExpand")
@ResponseBody
public ResultModel<Boolean> onFoldExpand(@RequestBody FoldExpandData data) {
    String nodeId = data.getNodeId();
    boolean isFold = data.isFold();
    
    // 处理折叠/展开逻辑
    boolean success = treeService.handleFoldExpand(nodeId, isFold);
    
    ResultModel<Boolean> result = new ResultModel<>();
    result.setData(success);
    return result;
}
```

### 8.5 导航服务规范

#### 8.5.1 数据加载服务

负责导航数据的加载和处理。

**服务实现：**
```java
@Service
@RestController
@RequestMapping("/attendance/navigation")
public class AttendanceNavigationService {
    
    /**
     * 获取导航数据
     */
    @GetMapping("/data")
    @ResponseBody
    public ListResultModel<List<NavigationNode>> getNavigationData() {
        ListResultModel<List<NavigationNode>> resultModel = new ListResultModel<>();
        
        try {
            List<NavigationNode> nodes = navigationDataService.loadNavigationData();
            resultModel.setData(nodes);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorResultModel封装的错误信息
            ErrorListResultModel<List<NavigationNode>> errorResult = new ErrorListResultModel<>(e.getMessage());
            errorResult.setErrcode(1000);
            errorResult.setRequestStatus(-1);
            return errorResult;
        }
        
        return resultModel;
    }
}
```

#### 8.5.2 事件处理服务

负责导航事件的处理和响应。

**服务实现：**
```java
@Service
@RestController
@RequestMapping("/attendance/navigation/events")
public class AttendanceNavigationEventService {
    
    /**
     * 处理节点选择事件
     */
    @PostMapping("/nodeSelect")
    @ResponseBody
    public ResultModel<String> handleNodeSelect(@RequestBody NodeSelectEvent event) {
        ResultModel<String> resultModel = new ResultModel<>();
        
        try {
            String redirectUrl = eventHandlerService.processNodeSelect(event);
            resultModel.setData(redirectUrl);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(null);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
}
```

#### 8.5.3 状态管理服务

负责导航状态的保存和恢复。

**服务实现：**
```java
@Service
@RestController
@RequestMapping("/attendance/navigation/state")
public class AttendanceNavigationStateService {
    
    /**
     * 保存导航状态
     */
    @PostMapping("/save")
    @ResponseBody
    public ResultModel<Boolean> saveNavigationState(@RequestBody NavigationState state) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            boolean success = stateService.saveState(state);
            resultModel.setData(success);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(false);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
    
    /**
     * 恢复导航状态
     */
    @GetMapping("/restore")
    @ResponseBody
    public ResultModel<NavigationState> restoreNavigationState() {
        ResultModel<NavigationState> resultModel = new ResultModel<>();
        
        try {
            NavigationState state = stateService.restoreState();
            resultModel.setData(state);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            resultModel.setData(null);
            resultModel.setRequestStatus(0);
            e.printStackTrace();
        }
        
        return resultModel;
    }
}
```

## 9. 视图关联规范

### 9.1 视图父子关系

#### 定义
父子关系指一个视图作为另一个视图的子组件存在，父视图拥有对子视图的控制权。

#### 特征
- 父视图决定子视图的显示和隐藏
- 子视图的生命周期依赖于父视图
- 数据传递通常从父视图流向子视图
- 通过bindClass属性实现视图与服务的解耦
- 父级视图中可直接通过field或method方式创建子视图

#### 实现方式
```java
// 方式一：直接字段引用
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7
)
public class ParentView {
    // 子视图
    private ChildView childView;
    
    // Getters and setters
}

// 方式二：通过Field注解实现嵌套
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "人员信息",
    borderType = BorderType.inset,
    customService = {PersonInfoService.class}
)
public class PersonInfoView {
    // 基本信息表单（内嵌）
    @FormFieldAnnotation(
        borderType = BorderType.inset
    )
    @CustomAnnotation(caption = "基本信息", index = 1)
    private BasicInfoForm basicInfoForm;
    
    // 详细信息表单（内嵌）
    @FormFieldAnnotation(
        borderType = BorderType.inset
    )
    @CustomAnnotation(caption = "详细信息", index = 2)
    private DetailInfoForm detailInfoForm;
}
```

#### 应用场景
- 表单中的子表单区域
- 面板中的嵌套内容
- Tabs中的TabItem内容
- 聚合视图中的子视图组件

#### 注意事项
- 避免过复杂的视图关系设计，影响系统性能和用户体验
- 确保所有视图入口都具有Web可访问性
- 正确处理视图间的数据同步和状态管理

### 9.2 视图顺序关系

#### 定义
顺序关系指多个视图按照一定的顺序依次执行或显示，前一个视图的结果影响后一个视图的行为。

#### 特征
- 视图间存在明确的执行顺序
- 数据在视图间顺序传递
- 通常用于向导式操作流程或多步骤表单
- 通过TabsItemAnnotation等注解实现视图间的顺序关联
- 在多步骤模式中，每一个步骤都应作为独立的视图入口

#### 实现方式
```java
// 多步骤向导示例
@TabsAnnotation(
    caption = "信息录入向导",
    barLocation = BarLocationType.top,
    singleOpen = true
)
public class WizardView {
    @TabsItemAnnotation(
        expression = "step1",
        index = 1,
        bindClass = Step1Service.class
    )
    private Step1View step1View;
    
    @TabsItemAnnotation(
        expression = "step2",
        index = 2,
        bindClass = Step2Service.class
    )
    private Step2View step2View;
    
    @TabsItemAnnotation(
        expression = "step3",
        index = 3,
        bindClass = Step3Service.class
    )
    private Step3View step3View;
    
    // Getters and setters
}

// 多步骤CARD模式示例
@MTabsAnnotation(
    caption = "多步骤信息录入",
    autoSave = true
)
public class MultiStepView {
    @TabsItemAnnotation(
        expression = "basicInfo",
        dataUrl = "/multistep/basicInfo",
        autoSave = true,
        caption = "基本信息",
        index = 1,
        bindClass = BasicInfoStepService.class
    )
    private BasicInfoView basicInfoStep;
    
    @TabsItemAnnotation(
        expression = "detailInfo",
        dataUrl = "/multistep/detailInfo",
        autoSave = true,
        caption = "详细信息",
        index = 2,
        bindClass = DetailInfoStepService.class
    )
    private DetailInfoView detailInfoStep;
    
    @TabsItemAnnotation(
        expression = "confirmInfo",
        dataUrl = "/multistep/confirmInfo",
        autoSave = true,
        caption = "确认信息",
        index = 3,
        bindClass = ConfirmInfoStepService.class
    )
    private ConfirmInfoView confirmInfoStep;
    
    // Getters and setters
}
```

#### 应用场景
- 多步骤表单录入
- 业务流程审批
- 向导式配置界面
- 多步骤CARD模式

#### 注意事项
- 每一个步骤都应作为独立的视图入口，具备Web可访问性
- 确保可通过URL直接访问并保持状态一致性

### 9.3 视图并行关系

#### 定义
并行关系指多个视图可以同时存在和操作，彼此相对独立但又存在一定的关联。

#### 特征
- 多个视图可同时显示
- 视图间相对独立运行
- 可能存在数据共享或状态同步需求
- 在完成父子、顺序等关联关系分析后，根据外观使用不同的TabItem、GroupItem等组件完成挂接
- 适用于仪表板、多面板操作、数据对比和聚合视图并行子视图等场景

#### 实现方式
```java
// 仪表板示例
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7
)
public class DashboardView {
    // 并行视图组件
    private SummaryView summaryView;
    private ChartView chartView;
    private ListView listView;
    
    // Getters and setters
}

// 聚合视图中的并行子视图示例
@TabsAnnotation(
    caption = "考勤聚合信息",
    autoSave = true,
    barLocation = BarLocationType.top
)
public class AttendanceAggregateView {
    // TAB项定义 - 并行关系
    private AttendanceCheckInView checkInView;
    private AttendanceRecordView recordView;
    private AttendanceStatisticsView statisticsView;
    
    // Getters and setters
}
```

#### 应用场景
- 仪表板展示
- 多面板操作界面
- 数据对比分析界面
- 聚合视图中的并行子视图

#### 注意事项
- 确保并行视图间相对独立运行
- 合理处理可能存在的数据共享需求
- 必要时实现状态同步机制