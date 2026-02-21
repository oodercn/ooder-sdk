# ooder平台注解使用规范手册

## 目录

1. [概述](#1-概述)
2. [核心注解体系](#2-核心注解体系)
   - 2.1 视图类注解
   - 2.2 字段注解
   - 2.3 服务类注解
   - 2.4 方法注解
3. [视图注解详解](#3-视图注解详解)
   - 3.1 FormAnnotation表单注解
   - 3.2 PanelFormAnnotation面板表单注解
   - 3.3 TreeGridAnnotation列表注解
   - 3.4 TabsAnnotation聚合注解
   - 3.5 TreeAnnotation树形注解
4. [导航注解详解](#4-导航注解详解)
   - 4.1 NavTreeViewAnnotation树形导航
   - 4.2 NavFoldingTabsViewAnnotation折叠TAB导航
   - 4.3 NavFoldingTreeViewAnnotation折叠树形导航
   - 4.4 NavMTabsViewAnnotation多TAB导航
5. [字段注解详解](#5-字段注解详解)
   - 5.1 InputAnnotation输入注解
   - 5.2 DatePickerAnnotation日期注解
   - 5.3 ComboBoxAnnotation下拉注解
   - 5.4 ButtonAnnotation按钮注解
6. [服务注解详解](#6-服务注解详解)
   - 6.1 Aggregation聚合注解
   - 6.2 APIEventAnnotation事件注解
   - 6.3 CustomAnnotation自定义注解
7. [注解组合使用规范](#7-注解组合使用规范)
8. [最佳实践](#8-最佳实践)

## 1. 概述

ooder平台通过一套完整的注解体系实现前后端的无缝对接，注解是平台的核心机制。本规范详细说明各类注解的使用方法和最佳实践。

## 2. 核心注解体系

### 2.1 视图类注解

1. **@FormAnnotation**：用于定义表单视图
2. **@TreeGridAnnotation**：用于定义列表视图
3. **@TabsAnnotation**：用于定义聚合视图
4. **@TreeAnnotation**：用于定义树形视图
5. **@ButtonViewsAnnotation**：用于定义按钮视图

### 2.2 字段注解

1. **@InputAnnotation**：定义输入框字段
2. **@DatePickerAnnotation**：定义日期选择字段
3. **@ComboBoxAnnotation**：定义下拉框字段
4. **@ButtonAnnotation**：定义按钮字段
5. **@CustomListAnnotation**：定义列表增强字段

### 2.3 服务类注解

1. **@Aggregation**：定义服务聚合类型
2. **@RestController**：定义REST控制器
3. **@Service**：定义服务组件
4. **@RequestMapping**：定义URL映射

### 2.4 方法注解

1. **@APIEventAnnotation**：定义API事件处理
2. **@CustomAnnotation**：定义自定义属性
3. **@PostMapping/@GetMapping**：定义HTTP方法
4. **@ResponseBody**：定义响应体

## 3. 视图注解详解

### 3.1 FormAnnotation表单注解

```java
@FormAnnotation(
    borderType = BorderType.inset,    // 边框类型
    col = 2,                          // 列数
    row = 7,                          // 行数
    customService = {AttendanceCheckInService.class}  // 绑定服务类
)
```

**核心属性说明：**
- `borderType`：表单边框样式（none、inset、outset等）
- `col`：表单列数，影响字段布局
- `row`：表单行数，影响表单高度
- `customService`：绑定的服务类，实现业务逻辑

### 3.2 PanelFormAnnotation面板表单注解

``java
@PanelFormAnnotation(
    dock = Dock.fill,                 // 停靠位置
    caption = "面板表单",              // 标题
    borderType = BorderType.inset,    // 边框类型
    customService = {MyPanelService.class}, // 绑定服务类
    toggle = true,                    // 是否可切换
    noFrame = false                   // 是否无边框
)
```

**核心属性说明：**
- `dock`：停靠位置（fill、top、bottom等）
- `caption`：面板标题
- `borderType`：边框样式
- `customService`：绑定的服务类
- `toggle`：是否支持切换
- `noFrame`：是否无边框

**使用场景：**
- 面板容器中的表单展示
- 可折叠/展开的表单区域
- 需要停靠布局的表单
- 作为核心容器支持多重嵌套

### 3.2 TreeGridAnnotation列表注解

```java
@TreeGridAnnotation(
    customService = {AttendanceQueryService.class},  // 绑定服务类
    showHeader = true,                // 是否显示表头
    colSortable = true,               // 是否支持列排序
    altRowsBg = true                  // 是否交替行背景
)
```

**核心属性说明：**
- `customService`：绑定的服务类
- `showHeader`：是否显示列表表头
- `colSortable`：是否支持列排序
- `altRowsBg`：是否使用交替行背景色

### 3.3 TabsAnnotation聚合注解

```java
@TabsAnnotation(
    caption = "考勤聚合信息",          // 标题
    autoSave = true,                  // 是否自动保存
    barLocation = BarLocationType.top, // 标签栏位置
    bindTypes = {ComponentType.TABS}  // 绑定组件类型
)
```

**核心属性说明：**
- `caption`：聚合视图标题
- `autoSave`：是否自动保存状态
- `barLocation`：标签栏位置（top、bottom、left、right）
- `bindTypes`：可绑定的组件类型

### 3.4 TreeAnnotation树形注解

```java
@TreeAnnotation(
    customService = {AttendanceNavigationTreeService.class},  // 绑定服务类
    showRoot = true,                  // 是否显示根节点
    selMode = SelModeType.single,     // 选择模式
    bindTypes = {ComponentType.TREEBAR}  // 绑定组件类型
)
```

**核心属性说明：**
- `customService`：绑定的服务类
- `showRoot`：是否显示根节点
- `selMode`：选择模式（single、multi等）
- `bindTypes`：可绑定的组件类型

## 4. 导航注解详解

### 4.1 NavTreeViewAnnotation树形导航

```java
@NavTreeViewAnnotation(
    expression = "attendanceTree",    // 表达式标识
    dataUrl = "/attendance/navigation/tree/data",  // 数据URL
    loadChildUrl = "/attendance/navigation/tree/children",  // 子节点URL
    rootId = "0"                      // 根节点ID
)
```

**核心属性说明：**
- `expression`：表达式标识符，用于标识导航实例
- `dataUrl`：根节点数据加载URL
- `loadChildUrl`：子节点数据加载URL
- `rootId`：根节点ID

### 4.2 NavFoldingTabsViewAnnotation折叠TAB导航

```java
@NavFoldingTabsViewAnnotation(
    expression = "attendanceTabs",    // 表达式标识
    autoSave = true                   // 是否自动保存
)
```

**核心属性说明：**
- `expression`：表达式标识符
- `autoSave`：是否自动保存导航状态

### 4.3 NavFoldingTreeViewAnnotation折叠树形导航

```java
@NavFoldingTreeViewAnnotation(
    expression = "attendanceFoldingTree",  // 表达式标识
    dataUrl = "/attendance/navigation/tree/data",  // 数据URL
    autoSave = true                   // 是否自动保存
)
```

**核心属性说明：**
- `expression`：表达式标识符
- `dataUrl`：树形数据加载URL
- `autoSave`：是否自动保存导航状态

### 4.4 NavMTabsViewAnnotation多TAB导航

```java
@NavMTabsViewAnnotation(
    expression = "attendanceMTabs",   // 表达式标识
    autoSave = true                   // 是否自动保存
)
```

**核心属性说明：**
- `expression`：表达式标识符
- `autoSave`：是否自动保存导航状态

## 5. 字段注解详解

### 5.1 InputAnnotation输入注解

```java
@InputAnnotation(
    maxlength = 20,                   // 最大长度
    multiLines = false                // 是否多行
)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

**核心属性说明：**
- `maxlength`：输入框最大字符数
- `multiLines`：是否支持多行输入

### 5.2 DatePickerAnnotation日期注解

```java
@DatePickerAnnotation(
    timeInput = true                  // 是否包含时间输入
)
@CustomAnnotation(caption = "签到日期", index = 3)
private String checkInDate;
```

**核心属性说明：**
- `timeInput`：是否包含时间选择

### 5.3 ComboBoxAnnotation下拉注解

```java
@ComboBoxAnnotation
@CustomListAnnotation(
    enumClass = AttendanceType.class  // 枚举类
)
@CustomAnnotation(caption = "考勤类型", index = 6)
private AttendanceType attendanceType;
```

**核心属性说明：**
- `enumClass`：绑定的枚举类

### 5.4 ButtonAnnotation按钮注解

```java
@ButtonAnnotation
@CustomAnnotation(caption = "考勤记录查询", index = 7, imageClass = "fa-solid fa-search")
private String queryAttendanceRecords;
```

**核心属性说明：**
- `imageClass`：按钮图标CSS类

### 5.5 ComboAnnotation复合输入注解

Combo组件是ooder平台中一种高级复合输入组件，它提供了多种输入类型和交互方式。更多关于Combo组件的详细信息，请参考[ooder平台Combo组件专题手册](../7_组件专题/专题手册/ooder平台Combo组件专题手册.md)。

#### @ComboAnnotation基础注解

```java
@ComboAnnotation(inputType = ComboInputType.input)
@CustomAnnotation(caption = "用户名", index = 1)
private String username;
```

**核心属性说明：**
- `inputType`：指定输入类型，默认为ComboInputType.none

#### @ComboBoxAnnotation下拉框注解

```java
@ComboBoxAnnotation
@CustomListAnnotation(
    enumClass = DepartmentType.class  // 枚举类
)
@CustomAnnotation(caption = "部门", index = 2)
private String department;
```

**核心属性说明：**
- `listKey`：列表键值
- `dropImageClass`：下拉图标CSS类
- `dropListWidth`：下拉列表宽度
- `dropListHeight`：下拉列表高度

#### @ComboInputAnnotation输入框注解

```java
@ComboInputAnnotation(
    inputType = ComboInputType.input,
    tips = "请输入用户名",
    labelCaption = "用户名"
)
@CustomAnnotation(caption = "用户名", index = 1)
private String username;
```

**核心属性说明：**
- `expression`：表达式
- `imageBgSize`：背景图片大小
- `imageClass`：图标CSS类
- `iconFontCode`：图标字体编码
- `unit`：单位
- `units`：多个单位
- `tips`：提示信息
- `commandBtn`：命令按钮
- `labelCaption`：标签标题
- `inputType`：输入类型，默认为ComboInputType.input
- `inputReadonly`：是否只读

#### @ComboNumberAnnotation数字输入注解

```java
@ComboNumberAnnotation(
    precision = 2,
    currencyTpl = "¥ *",
    min = "0"
)
@CustomAnnotation(caption = "价格", index = 3)
private Double price;
```

**核心属性说明：**
- `precision`：精度
- `decimalSeparator`：小数点分隔符
- `forceFillZero`：是否强制补零
- `trimTailZero`：是否去除尾部零
- `groupingSeparator`：分组分隔符
- `increment`：增量值
- `min`：最小值
- `max`：最大值
- `numberTpl`：数字模板
- `currencyTpl`：货币模板

#### @ComboPopAnnotation弹出框注解

```java
@ComboPopAnnotation(
    inputType = ComboInputType.popbox,
    width = "30em",
    height = "20em"
)
@CustomAnnotation(caption = "选择用户", index = 4)
private String userSelection;
```

**核心属性说明：**
- `parentID`：父级ID
- `cachePopWnd`：是否缓存弹窗
- `width`：宽度
- `height`：高度
- `src`：资源路径
- `dynLoad`：是否动态加载
- `inputType`：输入类型，默认为ComboInputType.popbox
- `bindClass`：绑定类

## 6. 服务注解详解

### 6.1 Aggregation聚合注解

```java
@Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)
```

**核心属性说明：**
- `type`：聚合类型（API、MENU等）
- `userSpace`：用户空间（SYS等）

### 6.2 APIEventAnnotation事件注解

```java
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
    beforeInvoke = CustomBeforInvoke.BUSY
)
```

**核心属性说明：**
- `customRequestData`：自定义请求数据
- `onExecuteSuccess`：执行成功事件
- `beforeInvoke`：调用前事件

### 6.3 CustomAnnotation自定义注解

```java
@CustomAnnotation(index = 0, caption = "签到", imageClass = "fa-solid fa-sign-in-alt")
```

**核心属性说明：**
- `index`：显示索引
- `caption`：显示标题
- `imageClass`：图标CSS类

## 7. 注解组合使用规范

### 7.1 视图类注解组合

```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {AttendanceCheckInService.class}
)
@BottomBarMenu(
    barDock = Dock.bottom,
    serviceClass = AttendanceCheckInBarService.class
)
public class AttendanceCheckInView {
    // 字段定义
}
```

### 7.2 服务类注解组合

``java
@Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/checkin/bar")
@Service
public class AttendanceCheckInBarService {
    // 方法定义
}
```

### 7.3 方法注解组合

``java
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
    beforeInvoke = CustomBeforInvoke.BUSY
)
@CustomAnnotation(index = 0, caption = "签到", imageClass = "fa-solid fa-sign-in-alt")
@PostMapping("/checkin")
@ResponseBody
public ResultModel<Boolean> checkIn(@RequestBody AttendanceCheckInView view) {
    // 方法实现
}
```

## 8. 最佳实践

### 8.1 注解使用原则

1. **一致性原则**：相同类型的视图应使用相同的注解配置
2. **最小化原则**：只配置必要的属性，避免冗余配置
3. **可读性原则**：注解配置应清晰表达业务意图

### 8.2 性能优化

1. **懒加载**：合理使用lazyLoad属性
2. **缓存机制**：利用autoSave属性实现状态缓存
3. **动态销毁**：使用dynDestory属性优化内存使用

### 8.3 可维护性

1. **注释完善**：为每个注解添加必要的说明
2. **命名规范**：遵循平台命名规范
3. **结构清晰**：保持注解配置的层次结构

通过遵循以上注解使用规范，可以充分发挥ooder平台注解体系的优势，实现高效、可维护的低代码开发。