# ooder平台注解体系手册

## 目录

1. [概述](#1-概述)
2. [视图层注解](#2-视图层注解)
   - 2.1 通用外观注解
   - 2.2 视图类型注解
   - 2.3 容器注解
3. [行为和交互注解](#3-行为和交互注解)
   - 3.1 核心行为注解
   - 3.2 模块注解
4. [字段注解](#4-字段注解)
   - 4.1 输入控件注解
   - 4.2 列表增强注解
5. [菜单注解](#5-菜单注解)
   - 5.1 工具栏注解
   - 5.2 菜单项注解
6. [通讯组件注解](#6-通讯组件注解)
   - 6.1 APIEvent注解
   - 6.2 MQTT注解
   - 6.3 按钮事件注解
7. [服务注解](#7-服务注解)
   - 7.1 通用服务注解
   - 7.2 BAR服务注解
8. [枚举注解](#8-枚举注解)
9. [参数注解](#9-参数注解)
10. [CSS样式注解](#10-css样式注解)
    - 10.1 CSS基础样式注解
    - 10.2 CSS组件样式注解
    - 10.3 CSS预设样式
    - 10.4 CSS枚举类型
11. [最佳实践](#11-最佳实践)

## 1. 概述

ooder平台的注解体系是整个框架的核心，通过声明式注解配置实现视图结构和行为定义。注解体系分为视图层注解、行为和交互注解、字段注解、菜单注解、通讯组件注解、服务注解和CSS样式注解等多个类别，共同构成了完整的注解驱动框架。

**包结构说明：**
- `net.ooder.annotation` - 根包，包含核心注解
- `net.ooder.annotation.ui` - UI相关注解和枚举
- `net.ooder.annotation.ui.css` - CSS基础样式注解
- `net.ooder.annotation.ui.css.component` - CSS组件样式注解（Button、Input、Form、TreeGrid、Tree、Tabs、Panel、Dialog、Menu、Card、Alert、Table、Tag）
- `net.ooder.annotation.ui.css.enums` - CSS枚举类型
- `net.ooder.annotation.ui.css.preset` - CSS预设样式（Button、Input、Alert、Card、Tag、Table、Form、Dialog、Panel、Tabs、Menu、Tree）
- `net.ooder.annotation.view` - 视图注解
- `net.ooder.annotation.svg` - SVG相关注解
- `net.ooder.annotation.action` - 行为注解
- `net.ooder.annotation.event` - 事件注解

## 2. 视图层注解

### 2.1 通用外观注解

#### @CustomAnnotation
底层通用外观配置，包含caption、index、imageClass等基础行为无关性配置。

**属性说明：**
- caption：标题文本
- index：显示索引
- imageClass：图标CSS类名

**使用示例：**
```java
@CustomAnnotation(caption = "员工姓名", index = 1, imageClass = "fa-solid fa-user")
private String employeeName;
```

### 2.2 视图类型注解

#### @FormViewAnnotation
表单视图定义注解，用于定义表单类型的视图。

#### @TreeGridViewAnnotation
网格视图定义注解，用于定义表格类型的视图。

#### @FormAnnotation
表单配置注解，用于详细配置表单的布局和属性。

**属性说明：**
- borderType：边框类型
- col：列数
- row：行数
- customService：关联的自定义服务类

**使用示例：**
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

#### @PanelFormAnnotation
面板表单注解，用于在面板容器中展示的表单，支持多重嵌套。

**属性说明：**
- dock：停靠位置
- caption：标题
- borderType：边框类型
- customService：关联的自定义服务类
- toggle：是否可切换
- noFrame：是否无边框

**使用示例：**
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

#### @TreeGridAnnotation
网格配置注解，用于详细配置表格的属性。

**属性说明：**
- customService：关联的自定义服务类
- showHeader：是否显示表头
- colSortable：列是否可排序
- altRowsBg：是否交替行背景色

### 2.3 容器注解

#### @PanelAnnotation
面板容器定义注解，用于定义面板类型的容器。

#### @DialogAnnotation
对话框容器定义注解，用于定义对话框类型的容器。

## 3. 行为和交互注解

### 3.1 核心行为注解

#### @APIEventAnnotation
核心基础行为配置，定义事件交互行为。

**主要属性：**
- queryAsync：异步查询
- autoRun：自动运行
- bindAction：绑定操作动作
- customRequestData：自定义请求数据
- customResponseData：自定义响应数据
- beforeInvoke：调用前处理
- onExecuteSuccess：执行成功处理
- onError：错误处理

**使用示例：**
```java
@APIEventAnnotation(
    bindAction = {CustomAction.SEARCH},
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},
    customResponseData = {ResponsePathEnum.FORM},
    beforeInvoke = {CustomBeforInvoke.BUSY},
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE}
)
@PostMapping("/search")
public ResultModel<List<Employee>> searchEmployees(@RequestBody SearchParams params) {
    // 搜索员工逻辑
}
```

### 3.2 模块注解

#### @ModuleAnnotation
模块定义注解，用于定义应用模块。

**属性说明：**
- caption：模块标题
- imageClass：模块图标
- dynLoad：是否动态加载
- moduleViewType：模块视图类型
- panelType：面板类型
- bindService：绑定服务类

## 4. 字段注解

### 4.1 输入控件注解

#### @InputAnnotation
输入框配置注解。

**属性说明：**
- maxlength：最大输入长度
- readonly：是否只读
- required：是否必填

**使用示例：**
```java
@InputAnnotation(maxlength = 20, required = true)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

#### @DatePickerAnnotation
日期选择器配置注解。

**属性说明：**
- timeInput：是否包含时间输入
- format：日期格式
- readonly：是否只读

#### @ComboBoxAnnotation
下拉框配置注解。

#### @ButtonAnnotation
按钮配置注解。

### 4.3 Combo组件注解

Combo组件是ooder平台中一种高级复合输入组件，它提供了多种输入类型和交互方式。更多关于Combo组件的详细信息，请参考[ooder平台Combo组件专题手册](ooder平台Combo组件专题手册.md)。

#### @ComboAnnotation
基础Combo注解，用于定义一个Combo输入组件。

**属性说明：**
- inputType：指定输入类型，默认为ComboInputType.none

#### @ComboBoxAnnotation
下拉框注解，用于定义下拉输入框组件。

**属性说明：**
- listKey：列表键值
- dropImageClass：下拉图标CSS类
- dropListWidth：下拉列表宽度
- dropListHeight：下拉列表高度

#### @ComboInputAnnotation
输入框注解，用于定义普通输入框组件。

**属性说明：**
- expression：表达式
- imageBgSize：背景图片大小
- imageClass：图标CSS类
- iconFontCode：图标字体编码
- unit：单位
- units：多个单位
- tips：提示信息
- commandBtn：命令按钮
- labelCaption：标签标题
- inputType：输入类型，默认为ComboInputType.input
- inputReadonly：是否只读

#### @ComboNumberAnnotation
数字输入注解，用于定义数字输入组件。

**属性说明：**
- precision：精度
- decimalSeparator：小数点分隔符
- forceFillZero：是否强制补零
- trimTailZero：是否去除尾部零
- groupingSeparator：分组分隔符
- increment：增量值
- min：最小值
- max：最大值
- numberTpl：数字模板
- currencyTpl：货币模板

#### @ComboPopAnnotation
弹出框注解，用于定义弹出框组件。

**属性说明：**
- parentID：父级ID
- cachePopWnd：是否缓存弹窗
- width：宽度
- height：高度
- src：资源路径
- dynLoad：是否动态加载
- inputType：输入类型，默认为ComboInputType.popbox
- bindClass：绑定类

### 4.2 列表增强注解

#### @CustomListAnnotation
自定义列表配置，用于增强枚举字段的数据能力。

**属性说明：**
- enumClass：关联的枚举类

**使用示例：**
```java
@CustomListAnnotation(enumClass = AttendanceType.class)
@CustomAnnotation(caption = "考勤类型", index = 5)
private String attendanceType;
```

## 5. 菜单注解

### 5.1 工具栏注解

#### @ToolBarMenu
工具栏配置注解。

**属性说明：**
- barDock：工具栏停靠位置
- serviceClass：关联的服务类

**使用示例：**
```java
@ToolBarMenu(
    barDock = Dock.top,
    serviceClass = AttendanceToolBarService.class
)
```

#### @BottomBarMenu
底部栏配置注解。

**属性说明：**
- barDock：底部栏停靠位置
- serviceClass：关联的服务类

### 5.2 菜单项注解

#### CustomFormMenu
预定义菜单项。

#### TreeGridMenu
网格菜单项，包含RELOAD、SEARCH、ADD、DELETE等预定义菜单项。

## 6. 通讯组件注解

### 6.1 APIEvent注解

#### @APIEventAnnotation
已在3.1中详细介绍。

### 6.2 MQTT注解

#### @MQTTAnnotation
MQTT通讯组件配置注解。

**主要属性：**
- server：服务器地址
- port：端口号
- clientId：客户端ID
- userName：用户名
- password：密码
- autoConn：自动连接
- autoSub：自动订阅

### 6.3 按钮事件注解

#### @ButtonEvent
按钮事件配置注解。

**属性说明：**
- eventEnum：事件类型
- name：事件名称
- expression：表达式
- actions：动作列表

## 7. 服务注解

### 7.1 通用服务注解

#### @Aggregation
服务聚合注解，声明服务类型和用户空间。

**属性说明：**
- type：服务类型（AggregationType.API/MENU）
- userSpace：用户空间（UserSpace.SYS）

**使用示例：**
```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance")
@Service
public class AttendanceService {
    // 服务实现
}
```

#### Spring MVC注解
- @RestController：声明REST控制器
- @Service：声明服务组件
- @RequestMapping：声明请求映射

### 7.2 BAR服务注解

BAR服务必须使用@Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)注解。

## 8. 枚举注解

### Enumstype接口
枚举类实现Enumstype接口，提供getType()和getName()方法。

**实现示例：**
```java
public enum AttendanceType implements Enumstype {
    NORMAL("正常"),
    LATE("迟到"),
    EARLY_LEAVE("早退"),
    ABSENCE("缺勤"),
    OVERTIME("加班");
    
    private final String name;
    
    AttendanceType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name();
    }
    
    @Override
    public String getType() {
        return name();
    }
    
    @Override
    public String getName() {
        return name;
    }
}
```

## 9. 参数注解

### @Uid和@Pid注解
用于标识记录唯一性和父子关系。

#### @Uid
标识记录的唯一ID。

#### @Pid
标识父记录的ID。

**使用示例：**
```java
@FormViewAnnotation
public class EmployeeManagementView {
    // 使用@Uid注解将字段显式加入到页面上下文
    @Uid
    @HiddenField
    private String employeeId;
    
    // 使用@Pid注解将字段显式加入到页面上下文
    @Pid
    @HiddenField
    private String departmentId;
}
```

## 10. CSS样式注解

CSS样式注解体系是ooder平台新增的声明式样式配置系统，通过注解方式定义组件的CSS样式，实现样式与结构的统一管理。

### 10.1 CSS基础样式注解

CSS基础样式注解位于`net.ooder.annotation.ui.css`包下，提供原子级别的样式配置能力。

#### @CSStyle

CSS样式组合注解，用于在组件类上定义完整的CSS样式配置。

**属性说明：**
- className：CSS类名
- customCss：自定义CSS样式字符串
- sandbox：沙箱主题
- font：字体样式（嵌套@CSFont）
- layout：布局样式（嵌套@CSLayout）
- border：边框样式（嵌套@CSBorder）
- flex：Flex布局样式（嵌套@CSFlex）
- transform：变换与动画（嵌套@CSTransform）
- normal：正常状态样式
- hover：悬停状态样式
- active：激活状态样式
- focus：焦点状态样式
- disabled：禁用状态样式

**使用示例：**
```java
@CSStyle(
    className = "custom-button",
    font = @CSFont(fontSize = "14px", color = "#333"),
    layout = @CSLayout(padding = "8px 16px"),
    hover = "background-color: #e0e0e0;"
)
private String buttonField;
```

#### @CSLayout

CSS布局样式注解，用于定义布局相关的CSS属性。

**属性说明：**
- display：显示类型（CSDisplay枚举）
- position：定位类型（CSPosition枚举）
- top/left/right/bottom：定位偏移
- width/height：宽高
- minWidth/maxWidth/minHeight/maxHeight：最小/最大宽高
- padding/paddingLeft/paddingRight/paddingTop/paddingBottom：内边距
- margin/marginLeft/marginRight/marginTop/marginBottom：外边距
- overflow/overflowX/overflowY：溢出处理（CSOverflow枚举）
- zIndex：层叠顺序
- visibility：可见性（CSVisibility枚举）
- opacity：透明度
- cursor：光标样式（CSCursor枚举）
- boxSizing：盒模型（CSBoxSizing枚举）
- buttonPreset：按钮预设样式
- inputPreset：输入框预设样式

#### @CSFont

CSS字体样式注解，用于定义文本相关的CSS属性。

**属性说明：**
- color：文字颜色
- fontSize：字体大小
- fontWeight：字体粗细（CSFontWeight枚举）
- fontFamily：字体族
- fontStyle：字体样式（CSFontStyle枚举）
- lineHeight：行高
- letterSpacing：字间距
- textAlign：文本对齐（CSTextAlign枚举）
- textDecoration：文本装饰（CSTextDecoration枚举）
- textTransform：文本转换（CSTextTransform枚举）
- whiteSpace：空白处理（CSWhiteSpace枚举）
- textOverflow：文本溢出
- textShadow：文字阴影
- verticalAlign：垂直对齐（CSVerticalAlign枚举）

#### @CSBorder

CSS边框与背景样式注解，用于定义边框和背景相关的CSS属性。

**属性说明：**
- border/borderWidth/borderStyle/borderColor：边框
- borderRadius：圆角
- borderTop/Right/Bottom/Left：各方向边框
- background/backgroundColor：背景
- backgroundImage/Size/Position/Repeat：背景图片
- boxShadow：阴影
- outline/Color/Width/Style：轮廓

#### @CSFlex

CSS Flex布局样式注解，用于定义Flex容器和项目属性。

**属性说明：**
- flexDirection：主轴方向
- flexWrap：换行方式
- flexFlow：方向和换行简写
- justifyContent：主轴对齐
- alignItems：交叉轴对齐
- alignContent：多行对齐
- order：项目排序
- flexGrow/shrink/basis：项目伸缩
- flex：伸缩简写
- alignSelf：项目自身对齐
- gap/rowGap/columnGap：间隙

#### @CSTransform

CSS变换与动画样式注解，用于定义2D/3D变换和动画效果。

**属性说明：**
- transform：2D/3D变换
- transformOrigin：变换原点
- transformStyle：变换样式
- perspective/perspectiveOrigin：透视
- backfaceVisibility：背面可见性
- transition*：过渡相关属性
- animation*：动画相关属性

### 10.2 CSS组件样式注解

CSS组件样式注解位于`net.ooder.annotation.ui.css.component`包下，针对特定组件类型提供结构化的样式配置。

#### @ButtonStyle

Button组件样式注解，对应组件：ood.UI.Button, ood.UI.HTMLButton。

**属性说明：**
- panel：按钮整体样式
- caption：按钮标题样式
- icon：按钮图标样式
- border：边框样式
- hover/active/disabled/focus：状态样式
- loading：加载中状态样式
- primary/secondary/danger/link：变体样式

**使用示例：**
```java
@ButtonStyle(
    panel = @CSStyle(className = "my-button"),
    caption = @CSFont(fontSize = "14px", fontWeight = CSFontWeight.BOLD),
    hover = @CSStyle(customCss = "opacity: 0.8;")
)
private String submitButton;
```

#### @InputStyle

Input组件样式注解，对应组件：ood.UI.Input, ood.UI.ComboInput, ood.UI.DatePicker。

**属性说明：**
- panel：输入框整体样式
- label：标签样式
- caption：标题样式
- border：边框样式
- focus/error/disabled/readonly：状态样式
- placeholder：占位符样式
- dropdown：下拉按钮样式
- clear：清除按钮样式
- icon：图标样式
- hint：提示信息样式
- prefix/suffix：前缀/后缀样式

#### @FormStyle

FormLayout组件样式注解，对应组件：ood.UI.FormLayout, ood.UI.MFormLayout。

**属性说明：**
- panel：表单整体样式
- flex：表单布局（Flex）
- item：表单项样式
- label：标签样式
- caption：标题样式
- field：输入框区域样式
- required：必填标记样式
- error：错误提示样式
- help：帮助信息样式
- group：分组标题样式
- border：边框样式
- row/column：行列样式

#### @TreeGridStyle

TreeGrid组件样式注解，对应组件：ood.UI.TreeGrid, ood.UI.MTreeGrid。

**属性说明：**
- list：网格整体样式
- header：表头样式
- headerCell：表头单元格样式
- row：数据行样式
- cell：数据单元格样式
- selected：选中行样式
- hover：悬停行样式
- odd/even：奇偶行样式
- sortIcon：排序图标样式
- scrollbar：滚动条样式
- summary：汇总行样式

#### @TreeStyle

TreeView组件样式注解，对应组件：ood.UI.TreeView, ood.UI.MTreeView。

**属性说明：**
- list：树列表整体样式
- item：树项样式
- caption：树项标题样式
- icon：树项图标样式
- expand：展开/折叠图标样式
- selected：选中状态样式
- hover：悬停状态样式
- indent：缩进样式
- line：连接线样式
- checkbox：复选框样式
- loading：加载中样式

#### @TabsStyle

Tabs组件样式注解，对应组件：ood.UI.Tabs, ood.UI.MTabs, ood.UI.FoldingTabs。

**属性说明：**
- list：标签列表整体样式
- item：标签项样式
- caption：标签标题样式
- close：关闭按钮样式
- selected：选中标签样式
- hover：悬停标签样式
- panel：标签面板样式
- content：面板内容样式
- icon：标签图标样式
- scrollBtn：滚动按钮样式
- dropdown：下拉菜单样式
- foldBtn：折叠按钮样式

#### @PanelStyle

Panel组件样式注解，对应组件：ood.UI.Panel。

**属性说明：**
- panel：面板整体样式
- border：面板边框样式
- flex：面板布局（Flex）
- header：面板头部样式
- content：面板内容样式
- footer：面板底部样式
- caption：标题样式
- close：关闭按钮样式

#### @DialogStyle

Dialog组件样式注解，对应组件：ood.UI.Dialog, ood.UI.MDialog。

**属性说明：**
- panel：对话框整体样式
- mask：遮罩层样式
- header：头部样式
- content：内容区域样式
- footer：底部区域样式
- title：标题样式
- close：关闭按钮样式
- max/min：最大化/最小化按钮样式
- buttons：按钮区域样式
- border：边框样式
- boxShadow：阴影样式
- animation：动画效果

#### @MenuStyle

MenuBar/PopMenu组件样式注解，对应组件：ood.UI.MenuBar, ood.UI.PopMenu。

**属性说明：**
- list：菜单列表整体样式
- item：菜单项样式
- caption：菜单标题样式
- icon：菜单图标样式
- arrow：箭头图标样式
- split：分隔线样式
- selected：选中项样式
- hover：悬停项样式
- submenu：子菜单样式
- toolbar：工具栏样式
- disabled：禁用项样式
- shortcut：快捷键样式

#### @CardStyle

Card组件样式注解，对应组件：ood.UI.Card。

**属性说明：**
- preset：卡片预设样式（CardPreset）
- font：字体样式
- layout：布局样式
- border：边框样式

#### @AlertStyle

Alert/Message组件样式注解，对应组件：ood.UI.Alert, ood.UI.Message。

**属性说明：**
- preset：警告预设样式（AlertPreset）
- font：字体样式
- layout：布局样式
- border：边框样式

#### @TableStyle

Table组件样式注解，对应组件：ood.UI.Table。

**属性说明：**
- preset：表格预设样式（TablePreset）
- font：字体样式
- layout：布局样式
- border：边框样式

#### @TagStyle

Tag组件样式注解，对应组件：ood.UI.Tag。

**属性说明：**
- preset：标签预设样式（TagPreset）
- font：字体样式
- layout：布局样式
- border：边框样式

### 10.3 CSS预设样式

CSS预设样式位于`net.ooder.annotation.ui.css.preset`包下，提供基于现代UI框架的预设样式方案。

#### @CSPreset

CSS预设样式组合注解，用于快速应用多种预设样式。

**属性说明：**
- button：按钮预设
- input：输入框预设
- alert：警告预设
- card：卡片预设
- tag：标签预设

#### ButtonPreset

Button组件预设样式枚举，基于Material Design、Ant Design、Element Plus、Bootstrap等框架。

**预设类型：**
- UNSET：未设置
- MATERIAL_CONTAINED：Material实心按钮
- MATERIAL_OUTLINED：Material描边按钮
- MATERIAL_TEXT：Material文本按钮
- ANT_PRIMARY：Ant Design主要按钮
- ANT_DEFAULT：Ant Design默认按钮
- ANT_DASHED：Ant Design虚线按钮
- ANT_LINK：Ant Design链接按钮
- ELEMENT_PRIMARY/SUCCESS/WARNING/DANGER：Element Plus语义按钮
- ELEMENT_ROUND：Element Plus圆角按钮
- BOOTSTRAP_PRIMARY/SECONDARY：Bootstrap按钮
- BOOTSTRAP_OUTLINE_PRIMARY：Bootstrap描边按钮
- ICON_BUTTON：图标按钮
- FAB/FAB_MINI：浮动操作按钮

**使用示例：**
```java
@CSLayout(buttonPreset = ButtonPreset.MATERIAL_CONTAINED)
private String materialButton;

@CSBorder(buttonPreset = ButtonPreset.ANT_PRIMARY)
private String antButton;
```

#### InputPreset

Input组件预设样式枚举，提供多种输入框样式方案。

**预设类型：**
- UNSET：未设置
- MATERIAL_FILLED：Material填充输入框
- MATERIAL_OUTLINED：Material描边输入框
- MATERIAL_STANDARD：Material标准输入框
- ANT_DEFAULT/LARGE/SMALL：Ant Design尺寸变体
- ANT_TEXTAREA：Ant Design文本域
- ELEMENT_DEFAULT/DISABLED/READONLY：Element Plus状态变体
- BOOTSTRAP_DEFAULT/LARGE/SMALL：Bootstrap尺寸变体
- SEARCH/PASSWORD/NUMBER/DATE_PICKER：功能变体

#### AlertPreset

Alert组件预设样式枚举，提供警告/消息样式方案。

**预设类型：**
- UNSET：未设置
- INFO：信息提示（蓝色）
- SUCCESS：成功提示（绿色）
- WARNING：警告提示（橙色）
- ERROR：错误提示（红色）

#### CardPreset

Card组件预设样式枚举，提供卡片样式方案。

**预设类型：**
- UNSET：未设置
- BASIC：基础卡片
- BORDERED：边框卡片
- SHADOW：阴影卡片

#### TagPreset

Tag组件预设样式枚举，提供标签样式方案。

**预设类型：**
- UNSET：未设置
- DEFAULT：默认标签（灰色）
- PRIMARY：主要标签（蓝色）
- SUCCESS：成功标签（绿色）
- WARNING：警告标签（橙色）
- DANGER：危险标签（红色）

#### TablePreset

Table组件预设样式枚举，提供表格样式方案。

**预设类型：**
- UNSET：未设置
- BASIC：基础表格
- BORDERED：边框表格
- STRIPED：条纹表格

#### FormPreset

Form组件预设样式枚举，提供表单布局方案。

**预设类型：**
- UNSET：未设置
- VERTICAL：垂直表单
- HORIZONTAL：水平表单
- INLINE：行内表单

#### DialogPreset

Dialog组件预设样式枚举，提供对话框样式方案。

**预设类型：**
- UNSET：未设置
- MODAL：模态对话框
- DRAWER：抽屉式对话框
- FULLSCREEN：全屏对话框

#### PanelPreset

Panel组件预设样式枚举，提供面板样式方案。

**预设类型：**
- UNSET：未设置
- BASIC：基础面板
- CARD：卡片面板
- COLLAPSIBLE：可折叠面板

#### TabsPreset

Tabs组件预设样式枚举，提供标签页样式方案。

**预设类型：**
- UNSET：未设置
- LINE：线型标签页
- CARD：卡片标签页
- BUTTON：按钮标签页

#### MenuPreset

Menu组件预设样式枚举，提供菜单样式方案。

**预设类型：**
- UNSET：未设置
- VERTICAL：垂直菜单
- HORIZONTAL：水平菜单
- INLINE：行内菜单

#### TreePreset

Tree组件预设样式枚举，提供树形样式方案。

**预设类型：**
- UNSET：未设置
- DEFAULT：默认树形
- DIRECTORY：目录树形
- SIMPLE：简单树形

### 10.4 CSS枚举类型

CSS枚举类型位于`net.ooder.annotation.ui.css.enums`包下，提供类型安全的CSS属性值。

**主要枚举类：**

| 枚举类 | 说明 | 示例值 |
|--------|------|--------|
| CSDisplay | 显示类型 | BLOCK, FLEX, GRID, INLINE_FLEX |
| CSPosition | 定位类型 | STATIC, RELATIVE, ABSOLUTE, FIXED |
| CSOverflow | 溢出处理 | VISIBLE, HIDDEN, SCROLL, AUTO |
| CSVisibility | 可见性 | VISIBLE, HIDDEN, COLLAPSE |
| CSCursor | 光标样式 | POINTER, MOVE, TEXT, NOT_ALLOWED |
| CSBoxSizing | 盒模型 | CONTENT_BOX, BORDER_BOX |
| CSFontWeight | 字体粗细 | NORMAL, BOLD, W100-W900 |
| CSFontStyle | 字体样式 | NORMAL, ITALIC, OBLIQUE |
| CSTextAlign | 文本对齐 | LEFT, CENTER, RIGHT, JUSTIFY |
| CSTextDecoration | 文本装饰 | NONE, UNDERLINE, LINE_THROUGH |
| CSWhiteSpace | 空白处理 | NORMAL, NOWRAP, PRE, PRE_WRAP |
| CSBorderStyle | 边框样式 | NONE, SOLID, DASHED, DOTTED |
| CSFlexDirection | Flex方向 | ROW, ROW_REVERSE, COLUMN |
| CSFlexWrap | Flex换行 | NOWRAP, WRAP, WRAP_REVERSE |
| CSJustifyContent | 主轴对齐 | FLEX_START, CENTER, SPACE_BETWEEN |
| CSAlignItems | 交叉轴对齐 | FLEX_START, CENTER, STRETCH |
| CSButtonVariant | 按钮变体 | CONTAINED, OUTLINED, PRIMARY, DANGER |
| CSInputVariant | 输入框变体 | DEFAULT, LARGE, SMALL |
| CSCardVariant | 卡片变体 | ELEVATED, OUTLINED, FILLED |
| CSTabsVariant | 标签页变体 | LINE, CARD, PILLS |
| CSTableVariant | 表格变体 | STRIPED, BORDERED, HOVER |

**枚举使用示例：**
```java
@CSLayout(
    display = CSDisplay.FLEX,
    position = CSPosition.RELATIVE,
    overflow = CSOverflow.HIDDEN
)
@CSFont(
    fontWeight = CSFontWeight.BOLD,
    textAlign = CSTextAlign.CENTER,
    whiteSpace = CSWhiteSpace.NOWRAP
)
@CSBorder(
    borderStyle = CSBorderStyle.SOLID,
    borderRadius = "4px"
)
private String styledComponent;
```

## 11. 最佳实践

### 11.1 注解使用原则
1. **明确性**：选择合适的注解类型，准确表达设计意图
2. **一致性**：在同类场景中保持注解使用的一致性
3. **简洁性**：避免过度使用注解，保持代码简洁

### 11.2 视图注解最佳实践
1. **表单注解**：合理设置col和row属性，优化表单布局
2. **网格注解**：正确配置showHeader和colSortable属性
3. **外观注解**：统一使用caption和imageClass属性，保持界面风格一致

### 11.3 服务注解最佳实践
1. **Web可访问性**：所有服务必须使用@RestController和@RequestMapping注解
2. **服务类型声明**：正确使用@Aggregation注解声明服务类型
3. **方法注解**：使用@PostMapping/@GetMapping等Spring Web注解

### 11.4 通讯注解最佳实践
1. **事件绑定**：合理使用bindAction等属性绑定用户操作
2. **数据路径**：正确配置customRequestData和customResponseData
3. **执行阶段**：合理设置beforeInvoke、onExecuteSuccess等执行阶段处理

### 11.5 CSS样式注解最佳实践
1. **预设优先**：优先使用ButtonPreset和InputPreset预设样式，保持UI一致性
2. **状态管理**：合理配置hover、active、focus等状态样式，提升交互体验
3. **类型安全**：使用CSS枚举类型替代字符串值，避免拼写错误
4. **组件样式**：复杂组件使用组件样式注解（如@ButtonStyle），简单场景使用基础样式注解

### 11.6 注意事项
1. 避免在视图中混合使用不兼容的注解类型
2. 确保所有服务方法都具有Web可访问性
3. 正确使用@Uid和@Pid注解标识记录关系
4. 合理配置模块跳转参数和绑定服务类