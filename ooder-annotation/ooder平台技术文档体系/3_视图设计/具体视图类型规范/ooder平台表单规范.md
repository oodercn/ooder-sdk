# ooder平台表单规范

## 1. 概述

表单是ooder平台中最常用的视图类型之一，用于数据录入、展示和编辑。表单规范定义了表单视图的设计原则、注解使用和最佳实践。

## 1.1 表单类型

ooder平台支持多种表单类型：
- **@FormAnnotation**：标准表单，用于常规数据录入和展示
- **@PanelFormAnnotation**：面板表单，用于在面板容器中展示的表单
- **@SVGPaperFormAnnotation**：SVG画布表单，用于矢量图绘制和展示
- **@FormViewAnnotation**：表单视图，通常用于模块入口

## 2. 表单类型注解

### 2.1 @FormAnnotation
@FormAnnotation用于定义表单配置，是最常用的表单注解。

**核心属性：**
- borderType：边框类型
- col：列数
- row：行数
- customService：关联的自定义服务类
- mode：表单模式（读写模式）

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

### 2.2 @PanelFormAnnotation
@PanelFormAnnotation用于定义面板表单，适用于在面板容器中展示的表单，支持多重嵌套。

**核心属性：**
- dock：停靠位置
- caption：标题
- borderType：边框类型
- customService：关联的自定义服务类
- toggle：是否可切换
- noFrame：是否无边框

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

**使用场景：**
- 面板容器中的表单展示
- 可折叠/展开的表单区域
- 需要停靠布局的表单
- 作为核心容器支持多重嵌套

### 2.3 @SVGPaperFormAnnotation
@SVGPaperFormAnnotation用于定义SVG画布表单，适用于矢量图绘制和展示场景。

**核心属性：**
- selectable：是否可选择
- iframeAutoLoad：iframe自动加载URL
- html：HTML内容
- width：宽度
- height：高度
- overflow：溢出处理方式
- scaleChildren：是否缩放子元素
- graphicZIndex：图形层级
- customMenu：自定义菜单
- event：事件处理
- bottombarMenu：底部栏菜单
- customService：关联的自定义服务类

**示例：**
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {FlowChartService.class}
)
public class FlowChartView {
    // SVG图形元素定义
}
```

**使用场景：**
- 流程图绘制和展示
- 架构图设计和展示
- 拓扑图绘制和展示
- 图形化数据可视化
- 需要矢量图形支持的界面

### 2.4 @FormViewAnnotation
@FormViewAnnotation用于定义表单视图，通常用于模块入口。

**使用场景：**
- 模块主入口视图
- 独立表单页面
- 需要专用视图标识的场景

## 3. 表单字段注解

### 3.1 基础输入控件

#### @InputAnnotation
用于定义文本输入框：

```java
@InputAnnotation(
    maxlength = 20
)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

**核心属性：**
- maxlength：最大输入长度
- readonly：是否只读
- required：是否必填

#### @DatePickerAnnotation
用于定义日期选择器：

```java
@DatePickerAnnotation(
    timeInput = true
)
@CustomAnnotation(caption = "签到日期", index = 3)
private String checkInDate;
```

**核心属性：**
- timeInput：是否包含时间输入
- format：日期格式
- readonly：是否只读

#### @ComboBoxAnnotation
用于定义下拉选择框：

```java
@ComboBoxAnnotation
@CustomAnnotation(caption = "部门", index = 2)
private String department;
```

#### @ButtonAnnotation
用于定义按钮：

```java
@ButtonAnnotation
@CustomAnnotation(caption = "提交", index = 4)
private String submitButton;
```

#### @ComboAnnotation
用于定义复合输入框，更多详情请参考[ooder平台技术文档体系/7_组件专题/专题手册/ooder平台Combo组件专题手册.md](../../../ooder平台技术文档体系/7_组件专题/专题手册/ooder平台Combo组件专题手册.md)：

```java
@ComboAnnotation(inputType = ComboInputType.input)
@CustomAnnotation(caption = "用户名", index = 5)
private String username;
```

#### @ComboBoxAnnotation
用于定义下拉输入框：

```java
@ComboBoxAnnotation
@CustomListAnnotation(enumClass = DepartmentType.class)
@CustomAnnotation(caption = "部门", index = 6)
private String department;
```

#### @ComboInputAnnotation
用于定义输入框：

```java
@ComboInputAnnotation(
    inputType = ComboInputType.input,
    tips = "请输入姓名"
)
@CustomAnnotation(caption = "姓名", index = 7)
private String name;
```

#### @ComboNumberAnnotation
用于定义数字输入框：

```java
@ComboNumberAnnotation(
    precision = 2,
    currencyTpl = "¥ *",
    min = "0"
)
@CustomAnnotation(caption = "薪资", index = 8)
private Double salary;
```

#### @ComboPopAnnotation
用于定义弹出框：

```java
@ComboPopAnnotation(
    inputType = ComboInputType.popbox,
    width = "30em",
    height = "20em"
)
@CustomAnnotation(caption = "选择用户", index = 9)
private String userSelection;
```

### 3.2 列表增强注解

#### @CustomListAnnotation
用于增强枚举字段的数据能力：

```java
@CustomListAnnotation(
    enumClass = AttendanceType.class
)
@CustomAnnotation(caption = "考勤类型", index = 5)
private String attendanceType;
```

## 4. 表单布局规范

### 4.1 布局属性
- **col**：定义表单列数，影响字段的水平排列
- **row**：定义表单行数，影响表单整体高度
- **borderType**：定义表单边框样式
- **mode**：定义表单模式（读写模式）

### 4.2 字段排列
- 使用@index属性控制字段的显示顺序
- 合理分配字段到不同列中
- 考虑字段的重要性和逻辑分组

### 4.3 样式配置
- 使用@CustomAnnotation定义字段标题、图标等外观属性
- 合理设置边框和间距
- 考虑响应式设计需求

## 5. 表单数据处理

### 5.1 数据验证
- 在服务端进行数据验证
- 合理设置字段的必填性和长度限制
- 提供友好的错误提示信息

### 5.2 数据传输
- 使用@RequestBody注解处理表单提交数据
- 确保前后端数据结构一致性
- 合理设计数据传输对象

### 5.3 数据持久化
- 通过customService关联数据处理服务
- 实现数据的增删改查操作
- 处理事务和异常情况

## 6. 表单服务规范

### 6.1 服务注解
表单相关服务必须使用以下注解：
- @Aggregation：声明服务类型
- @RestController：声明REST控制器
- @Service：声明服务组件
- @RequestMapping：声明请求映射

### 6.2 服务方法
- 使用@PostMapping/@GetMapping等Spring Web注解
- 方法命名清晰表达功能语义
- 包含完整的异常处理逻辑

### 6.3 返回数据
- 统一使用ResultModel或ListResultModel标准返回类型
- 遵循fastjson序列化规范
- 在特定视图操作环境下，应优先使用视图对象作为返回值的泛型类型

## 7. 表单BAR组件

### 7.1 工具栏配置
使用@ToolBarMenu配置表单工具栏：

```java
@ToolBarMenu(
    barDock = Dock.top,
    serviceClass = AttendanceToolBarService.class
)
```

### 7.2 底部栏配置
使用@BottomBarMenu配置表单底部栏：

```java
@BottomBarMenu(
    barDock = Dock.bottom,
    serviceClass = AttendanceBarService.class
)
```

### 7.3 BAR服务实现
BAR服务类必须遵循以下规范：
- 使用@Aggregation(type = AggregationType.MENU)注解
- 实现具体的业务逻辑方法
- 使用@APIEventAnnotation和@CustomAnnotation注解

## 8. 最佳实践

### 8.1 设计原则
1. **用户友好**：界面简洁明了，操作流程清晰
2. **数据一致**：确保前后端数据传输的一致性
3. **性能优化**：合理使用懒加载和缓存机制
4. **可维护性**：代码结构清晰，便于后续维护

### 8.2 实现规范
1. **注解使用**：正确使用表单相关注解
2. **字段设计**：合理设计字段类型和验证规则
3. **服务分离**：视图与服务分离，职责清晰
4. **异常处理**：完善的异常处理机制

### 8.3 注意事项
1. 避免在表单中混合使用不同类型的注解
2. 合理配置表单布局属性
3. 正确使用@Uid注解标识行主键
4. 所有BAR服务逻辑应在独立的服务类中实现