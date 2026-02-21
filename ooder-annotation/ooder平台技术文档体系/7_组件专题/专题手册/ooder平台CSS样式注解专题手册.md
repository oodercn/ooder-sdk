# ooder平台CSS样式注解专题手册

## 目录

1. [概述](#1-概述)
2. [设计理念](#2-设计理念)
3. [包结构](#3-包结构)
4. [基础样式注解](#4-基础样式注解)
   - 4.1 @CSStyle 组合样式
   - 4.2 @CSLayout 布局样式
   - 4.3 @CSFont 字体样式
   - 4.4 @CSBorder 边框样式
   - 4.5 @CSFlex Flex布局
   - 4.6 @CSTransform 变换动画
5. [组件样式注解](#5-组件样式注解)
   - 5.1 @ButtonStyle 按钮样式
   - 5.2 @InputStyle 输入框样式
   - 5.3 @FormStyle 表单样式
   - 5.4 @TreeGridStyle 网格样式
   - 5.5 @TreeStyle 树形样式
   - 5.6 @TabsStyle 标签页样式
   - 5.7 @PanelStyle 面板样式
   - 5.8 @DialogStyle 对话框样式
   - 5.9 @MenuStyle 菜单样式
   - 5.10 @CardStyle 卡片样式
   - 5.11 @AlertStyle 警告样式
   - 5.12 @TableStyle 表格样式
   - 5.13 @TagStyle 标签样式
6. [预设样式](#6-预设样式)
   - 6.1 @CSPreset 组合预设
   - 6.2 ButtonPreset 按钮预设
   - 6.3 InputPreset 输入框预设
   - 6.4 AlertPreset 警告预设
   - 6.5 CardPreset 卡片预设
   - 6.6 TagPreset 标签预设
   - 6.7 TablePreset 表格预设
   - 6.8 FormPreset 表单预设
   - 6.9 DialogPreset 对话框预设
   - 6.10 PanelPreset 面板预设
   - 6.11 TabsPreset 标签页预设
   - 6.12 MenuPreset 菜单预设
   - 6.13 TreePreset 树形预设
7. [CSS枚举类型](#7-css枚举类型)
8. [虚拟DOM映射](#8-虚拟dom映射)
9. [使用示例](#9-使用示例)
10. [最佳实践](#10-最佳实践)

## 1. 概述

ooder平台CSS样式注解体系是一个声明式的样式配置系统，通过Java注解定义组件的CSS样式，实现样式与结构的统一管理。该体系支持：

- **原子级样式配置**：通过基础样式注解精细控制每个CSS属性
- **组件级样式封装**：针对特定组件提供结构化的样式配置
- **预设样式方案**：基于Material Design、Ant Design等现代UI框架的预设样式
- **类型安全**：通过枚举类型确保CSS属性值的正确性
- **状态管理**：支持hover、active、focus、disabled等交互状态

## 2. 设计理念

### 2.1 虚拟DOM映射

每个CSS样式注解属性都对应组件虚拟DOM的特定节点，实现样式与结构的精确映射：

```
组件虚拟DOM结构
├── panel (容器)
│   ├── layout (布局属性)
│   ├── border (边框属性)
│   └── flex (Flex属性)
├── caption (标题)
│   └── font (字体属性)
├── icon (图标)
│   └── layout (布局属性)
└── states (状态)
    ├── hover
    ├── active
    ├── focus
    └── disabled
```

### 2.2 分层设计

CSS样式注解采用分层设计：

| 层级 | 包路径 | 说明 |
|------|--------|------|
| 基础层 | `net.ooder.annotation.ui.css` | 原子级CSS属性注解 |
| 组件层 | `net.ooder.annotation.ui.css.component` | 组件级样式封装 |
| 预设层 | `net.ooder.annotation.ui.css.preset` | 预设样式方案 |
| 枚举层 | `net.ooder.annotation.ui.css.enums` | 类型安全的枚举值 |

### 2.3 状态支持

所有组件样式注解都支持以下状态：

- **normal**：正常状态
- **hover**：鼠标悬停
- **active**：激活/按下
- **focus**：获得焦点
- **disabled**：禁用状态
- **loading**：加载中（部分组件）
- **error**：错误状态（表单组件）

## 3. 包结构

```
net.ooder.annotation.ui.css
├── CSStyle.java           # 组合样式注解
├── CSLayout.java          # 布局样式注解
├── CSFont.java            # 字体样式注解
├── CSBorder.java          # 边框样式注解
├── CSFlex.java            # Flex布局注解
├── CSTransform.java       # 变换动画注解
├── component/             # 组件样式注解
│   ├── package-info.java
│   ├── ButtonStyle.java
│   ├── InputStyle.java
│   ├── FormStyle.java
│   ├── TreeGridStyle.java
│   ├── TreeStyle.java
│   ├── TabsStyle.java
│   ├── PanelStyle.java
│   ├── DialogStyle.java
│   ├── MenuStyle.java
│   ├── CardStyle.java     # 卡片样式（新增）
│   ├── AlertStyle.java    # 警告样式（新增）
│   ├── TableStyle.java    # 表格样式（新增）
│   └── TagStyle.java      # 标签样式（新增）
├── enums/                 # CSS枚举类型
│   ├── CSDisplay.java
│   ├── CSPosition.java
│   ├── CSOverflow.java
│   ├── CSVisibility.java
│   ├── CSCursor.java
│   ├── CSBoxSizing.java
│   ├── CSFontWeight.java
│   ├── CSFontStyle.java
│   ├── CSTextAlign.java
│   ├── CSTextDecoration.java
│   ├── CSTextTransform.java
│   ├── CSWhiteSpace.java
│   ├── CSVerticalAlign.java
│   ├── CSBorderStyle.java
│   ├── CSFlexDirection.java
│   ├── CSFlexWrap.java
│   ├── CSJustifyContent.java
│   ├── CSAlignItems.java
│   ├── CSAlignSelf.java
│   ├── CSAlignContent.java
│   ├── CSButtonVariant.java
│   ├── CSInputVariant.java
│   ├── CSCardVariant.java
│   ├── CSTabsVariant.java
│   └── CSTableVariant.java
└── preset/                # 预设样式
    ├── CSPreset.java      # 组合预设注解（新增）
    ├── ButtonPreset.java
    ├── InputPreset.java
    ├── AlertPreset.java   # 警告预设（新增）
    ├── CardPreset.java    # 卡片预设（新增）
    ├── TagPreset.java     # 标签预设（新增）
    ├── TablePreset.java   # 表格预设（新增）
    ├── FormPreset.java    # 表单预设（新增）
    ├── DialogPreset.java  # 对话框预设（新增）
    ├── PanelPreset.java   # 面板预设（新增）
    ├── TabsPreset.java    # 标签页预设（新增）
    ├── MenuPreset.java    # 菜单预设（新增）
    └── TreePreset.java    # 树形预设（新增）
```

## 4. 基础样式注解

### 4.1 @CSStyle 组合样式

`@CSStyle`是CSS样式的组合注解，用于定义完整的样式配置。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSStyle {
    String className() default "";
    String customCss() default "";
    String sandbox() default "";
    CSFont font() default @CSFont();
    CSLayout layout() default @CSLayout();
    CSBorder border() default @CSBorder();
    CSFlex flex() default @CSFlex();
    CSTransform transform() default @CSTransform();
    String normal() default "";
    String hover() default "";
    String active() default "";
    String focus() default "";
    String disabled() default "";
}
```

**属性详解：**

| 属性 | 类型 | 说明 |
|------|------|------|
| className | String | CSS类名，用于引用外部CSS类 |
| customCss | String | 自定义CSS字符串，用于内联样式 |
| sandbox | String | 沙箱主题标识，用于样式隔离 |
| font | CSFont | 字体样式配置 |
| layout | CSLayout | 布局样式配置 |
| border | CSBorder | 边框样式配置 |
| flex | CSFlex | Flex布局配置 |
| transform | CSTransform | 变换与动画配置 |
| normal | String | 正常状态CSS |
| hover | String | 悬停状态CSS |
| active | String | 激活状态CSS |
| focus | String | 焦点状态CSS |
| disabled | String | 禁用状态CSS |

**使用示例：**

```java
@CSStyle(
    className = "primary-button",
    font = @CSFont(fontSize = "14px", color = "#ffffff"),
    layout = @CSLayout(padding = "8px 16px", width = "120px"),
    border = @CSBorder(borderRadius = "4px", backgroundColor = "#1976d2"),
    hover = "background-color: #1565c0;",
    active = "background-color: #0d47a1;"
)
private String submitButton;
```

### 4.2 @CSLayout 布局样式

`@CSLayout`用于定义元素布局相关的CSS属性。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSLayout {
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;
    InputPreset inputPreset() default InputPreset.UNSET;
    CSDisplay display() default CSDisplay.UNSET;
    CSPosition position() default CSPosition.UNSET;
    String top() default "";
    String left() default "";
    String right() default "";
    String bottom() default "";
    String width() default "";
    String height() default "";
    String minWidth() default "";
    String maxWidth() default "";
    String minHeight() default "";
    String maxHeight() default "";
    String padding() default "";
    String paddingLeft() default "";
    String paddingRight() default "";
    String paddingTop() default "";
    String paddingBottom() default "";
    String margin() default "";
    String marginLeft() default "";
    String marginRight() default "";
    String marginTop() default "";
    String marginBottom() default "";
    CSOverflow overflow() default CSOverflow.UNSET;
    CSOverflow overflowX() default CSOverflow.UNSET;
    CSOverflow overflowY() default CSOverflow.UNSET;
    String zIndex() default "";
    String floatValue() default "";
    String clear() default "";
    CSVisibility visibility() default CSVisibility.UNSET;
    String opacity() default "";
    CSCursor cursor() default CSCursor.UNSET;
    String pointerEvents() default "";
    String userSelect() default "";
    CSBoxSizing boxSizing() default CSBoxSizing.UNSET;
}
```

**属性分组：**

| 分组 | 属性 | 说明 |
|------|------|------|
| 显示 | display | 显示类型 |
| 定位 | position, top, left, right, bottom | 定位模式与偏移 |
| 尺寸 | width, height, min*, max* | 元素尺寸 |
| 间距 | padding*, margin* | 内外边距 |
| 溢出 | overflow, overflowX, overflowY | 内容溢出处理 |
| 层叠 | zIndex | 层叠顺序 |
| 可见性 | visibility, opacity | 可见性控制 |
| 交互 | cursor, pointerEvents, userSelect | 用户交互 |
| 盒模型 | boxSizing | 盒模型计算方式 |
| 预设 | buttonPreset, inputPreset | 预设样式引用 |

**使用示例：**

```java
@CSLayout(
    display = CSDisplay.FLEX,
    position = CSPosition.RELATIVE,
    width = "100%",
    height = "auto",
    padding = "16px",
    margin = "8px 0",
    overflow = CSOverflow.HIDDEN,
    cursor = CSCursor.POINTER
)
private String containerField;
```

### 4.3 @CSFont 字体样式

`@CSFont`用于定义文本相关的CSS属性。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSFont {
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;
    InputPreset inputPreset() default InputPreset.UNSET;
    String color() default "";
    String fontSize() default "";
    CSFontWeight fontWeight() default CSFontWeight.UNSET;
    String fontFamily() default "";
    CSFontStyle fontStyle() default CSFontStyle.UNSET;
    String lineHeight() default "";
    String letterSpacing() default "";
    CSTextAlign textAlign() default CSTextAlign.UNSET;
    CSTextDecoration textDecoration() default CSTextDecoration.UNSET;
    CSTextTransform textTransform() default CSTextTransform.UNSET;
    CSWhiteSpace whiteSpace() default CSWhiteSpace.UNSET;
    String wordWrap() default "";
    String textOverflow() default "";
    String textShadow() default "";
    CSVerticalAlign verticalAlign() default CSVerticalAlign.UNSET;
}
```

**属性分组：**

| 分组 | 属性 | 说明 |
|------|------|------|
| 颜色 | color | 文字颜色 |
| 字体 | fontSize, fontFamily, fontWeight, fontStyle | 字体属性 |
| 行距 | lineHeight, letterSpacing | 行间距与字间距 |
| 对齐 | textAlign, verticalAlign | 文本对齐 |
| 装饰 | textDecoration, textShadow | 文本装饰 |
| 转换 | textTransform | 大小写转换 |
| 换行 | whiteSpace, wordWrap, textOverflow | 换行与溢出 |

**使用示例：**

```java
@CSFont(
    color = "#333333",
    fontSize = "16px",
    fontWeight = CSFontWeight.BOLD,
    fontFamily = "Roboto, sans-serif",
    lineHeight = "1.5",
    textAlign = CSTextAlign.CENTER,
    whiteSpace = CSWhiteSpace.NOWRAP
)
private String titleField;
```

### 4.4 @CSBorder 边框样式

`@CSBorder`用于定义边框和背景相关的CSS属性。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSBorder {
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;
    InputPreset inputPreset() default InputPreset.UNSET;
    String border() default "";
    String borderWidth() default "";
    CSBorderStyle borderStyle() default CSBorderStyle.UNSET;
    String borderColor() default "";
    String borderRadius() default "";
    String borderTop() default "";
    String borderRight() default "";
    String borderBottom() default "";
    String borderLeft() default "";
    String background() default "";
    String backgroundColor() default "";
    String backgroundImage() default "";
    String backgroundSize() default "";
    String backgroundPosition() default "";
    String backgroundRepeat() default "";
    String boxShadow() default "";
    String outline() default "";
    String outlineColor() default "";
    String outlineWidth() default "";
    CSBorderStyle outlineStyle() default CSBorderStyle.UNSET;
}
```

**属性分组：**

| 分组 | 属性 | 说明 |
|------|------|------|
| 边框 | border, borderWidth, borderStyle, borderColor | 边框属性 |
| 圆角 | borderRadius | 圆角半径 |
| 分边 | borderTop/Right/Bottom/Left | 各方向边框 |
| 背景 | background, backgroundColor | 背景颜色 |
| 背景图 | backgroundImage, Size, Position, Repeat | 背景图片 |
| 阴影 | boxShadow | 盒阴影 |
| 轮廓 | outline, Color, Width, Style | 轮廓属性 |

**使用示例：**

```java
@CSBorder(
    border = "1px solid #e0e0e0",
    borderRadius = "8px",
    backgroundColor = "#ffffff",
    boxShadow = "0 2px 4px rgba(0,0,0,0.1)"
)
private String cardField;
```

### 4.5 @CSFlex Flex布局

`@CSFlex`用于定义Flex容器和项目属性。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSFlex {
    String flexDirection() default "";
    String flexWrap() default "";
    String flexFlow() default "";
    String justifyContent() default "";
    String alignItems() default "";
    String alignContent() default "";
    String order() default "";
    String flexGrow() default "";
    String flexShrink() default "";
    String flexBasis() default "";
    String flex() default "";
    String alignSelf() default "";
    String gap() default "";
    String rowGap() default "";
    String columnGap() default "";
}
```

**属性分组：**

| 分组 | 属性 | 说明 |
|------|------|------|
| 容器方向 | flexDirection | 主轴方向 |
| 容器换行 | flexWrap | 换行方式 |
| 容器简写 | flexFlow | 方向与换行简写 |
| 主轴对齐 | justifyContent | 主轴对齐方式 |
| 交叉轴对齐 | alignItems, alignContent | 交叉轴对齐 |
| 项目伸缩 | flexGrow, flexShrink, flexBasis, flex | 项目伸缩属性 |
| 项目对齐 | alignSelf | 项目自身对齐 |
| 项目排序 | order | 项目排序 |
| 间隙 | gap, rowGap, columnGap | 项目间隙 |

**使用示例：**

```java
@CSFlex(
    flexDirection = "row",
    justifyContent = "space-between",
    alignItems = "center",
    gap = "16px"
)
private String flexContainerField;
```

### 4.6 @CSTransform 变换动画

`@CSTransform`用于定义2D/3D变换和动画效果。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSTransform {
    String transform() default "";
    String transformOrigin() default "";
    String transformStyle() default "";
    String perspective() default "";
    String perspectiveOrigin() default "";
    String backfaceVisibility() default "";
    String transitionProperty() default "";
    String transitionDuration() default "";
    String transitionTimingFunction() default "";
    String transitionDelay() default "";
    String transition() default "";
    String animationName() default "";
    String animationDuration() default "";
    String animationTimingFunction() default "";
    String animationDelay() default "";
    String animationIterationCount() default "";
    String animationDirection() default "";
    String animationFillMode() default "";
    String animationPlayState() default "";
    String animation() default "";
}
```

**属性分组：**

| 分组 | 属性 | 说明 |
|------|------|------|
| 变换 | transform | 2D/3D变换 |
| 变换原点 | transformOrigin, transformStyle | 变换参考点 |
| 透视 | perspective, perspectiveOrigin | 3D透视 |
| 背面 | backfaceVisibility | 背面可见性 |
| 过渡 | transition* | 过渡动画属性 |
| 动画 | animation* | 关键帧动画属性 |

**使用示例：**

```java
@CSTransform(
    transform = "rotate(45deg) scale(1.1)",
    transformOrigin = "center center",
    transition = "all 0.3s ease"
)
private String animatedField;
```

## 5. 组件样式注解

### 5.1 @ButtonStyle 按钮样式

对应组件：`ood.UI.Button`, `ood.UI.HTMLButton`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ButtonStyle {
    CSStyle panel() default @CSStyle();
    CSFont caption() default @CSFont();
    CSLayout icon() default @CSLayout();
    CSBorder border() default @CSBorder();
    CSStyle hover() default @CSStyle();
    CSStyle active() default @CSStyle();
    CSStyle disabled() default @CSStyle();
    CSStyle focus() default @CSStyle();
    CSStyle loading() default @CSStyle();
    CSStyle primary() default @CSStyle();
    CSStyle secondary() default @CSStyle();
    CSStyle danger() default @CSStyle();
    CSStyle link() default @CSStyle();
}
```

**虚拟DOM映射：**

```
Button
├── panel (按钮容器)
├── caption (按钮文字)
├── icon (按钮图标)
├── border (边框)
├── hover (悬停状态)
├── active (激活状态)
├── disabled (禁用状态)
├── focus (焦点状态)
├── loading (加载状态)
├── primary (主要按钮)
├── secondary (次要按钮)
├── danger (危险按钮)
└── link (链接按钮)
```

**使用示例：**

```java
@ButtonStyle(
    panel = @CSStyle(className = "custom-btn"),
    caption = @CSFont(fontSize = "14px", fontWeight = CSFontWeight.MEDIUM),
    border = @CSBorder(borderRadius = "6px"),
    hover = @CSStyle(customCss = "opacity: 0.9; transform: translateY(-1px);"),
    primary = @CSStyle(customCss = "background: #1976d2; color: #fff;")
)
private String actionButton;
```

### 5.2 @InputStyle 输入框样式

对应组件：`ood.UI.Input`, `ood.UI.ComboInput`, `ood.UI.DatePicker`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InputStyle {
    CSStyle panel() default @CSStyle();
    CSFont label() default @CSFont();
    CSFont caption() default @CSFont();
    CSBorder border() default @CSBorder();
    CSStyle focus() default @CSStyle();
    CSStyle error() default @CSStyle();
    CSStyle disabled() default @CSStyle();
    CSStyle readonly() default @CSStyle();
    CSFont placeholder() default @CSFont();
    CSStyle dropdown() default @CSStyle();
    CSStyle clear() default @CSStyle();
    CSLayout icon() default @CSLayout();
    CSFont hint() default @CSFont();
    CSStyle prefix() default @CSStyle();
    CSStyle suffix() default @CSStyle();
}
```

**虚拟DOM映射：**

```
Input
├── panel (输入框容器)
├── label (标签)
├── caption (输入文字)
├── border (边框)
├── focus (焦点状态)
├── error (错误状态)
├── disabled (禁用状态)
├── readonly (只读状态)
├── placeholder (占位符)
├── dropdown (下拉按钮)
├── clear (清除按钮)
├── icon (图标)
├── hint (提示信息)
├── prefix (前缀)
└── suffix (后缀)
```

**使用示例：**

```java
@InputStyle(
    panel = @CSStyle(className = "form-input"),
    label = @CSFont(fontSize = "12px", color = "#666"),
    border = @CSBorder(border = "1px solid #d9d9d9", borderRadius = "6px"),
    focus = @CSStyle(customCss = "border-color: #1976d2; box-shadow: 0 0 0 2px rgba(25,118,210,0.2);"),
    error = @CSStyle(customCss = "border-color: #f44336;")
)
private String emailInput;
```

### 5.3 @FormStyle 表单样式

对应组件：`ood.UI.FormLayout`, `ood.UI.MFormLayout`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormStyle {
    CSStyle panel() default @CSStyle();
    CSFlex flex() default @CSFlex();
    CSStyle item() default @CSStyle();
    CSFont label() default @CSFont();
    CSFont caption() default @CSFont();
    CSStyle field() default @CSStyle();
    CSStyle required() default @CSStyle();
    CSStyle error() default @CSStyle();
    CSStyle help() default @CSStyle();
    CSStyle group() default @CSStyle();
    CSBorder border() default @CSBorder();
    CSStyle row() default @CSStyle();
    CSStyle column() default @CSStyle();
}
```

**使用示例：**

```java
@FormStyle(
    panel = @CSStyle(className = "login-form"),
    flex = @CSFlex(flexDirection = "column", gap = "16px"),
    label = @CSFont(fontSize = "14px", fontWeight = CSFontWeight.MEDIUM),
    required = @CSStyle(customCss = "color: #f44336;"),
    error = @CSStyle(customCss = "color: #f44336; font-size: 12px;")
)
public class LoginFormView {
    // 表单字段
}
```

### 5.4 @TreeGridStyle 网格样式

对应组件：`ood.UI.TreeGrid`, `ood.UI.MTreeGrid`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TreeGridStyle {
    CSStyle list() default @CSStyle();
    CSStyle header() default @CSStyle();
    CSFont headerCell() default @CSFont();
    CSStyle row() default @CSStyle();
    CSFont cell() default @CSFont();
    CSStyle selected() default @CSStyle();
    CSStyle hover() default @CSStyle();
    CSStyle odd() default @CSStyle();
    CSStyle even() default @CSStyle();
    CSStyle sortIcon() default @CSStyle();
    CSStyle scrollbar() default @CSStyle();
    CSStyle summary() default @CSStyle();
}
```

**使用示例：**

```java
@TreeGridStyle(
    list = @CSStyle(className = "data-grid"),
    header = @CSStyle(customCss = "background: #f5f5f5;"),
    headerCell = @CSFont(fontWeight = CSFontWeight.BOLD, fontSize = "13px"),
    selected = @CSStyle(customCss = "background: #e3f2fd;"),
    odd = @CSStyle(customCss = "background: #fafafa;"),
    even = @CSStyle(customCss = "background: #ffffff;")
)
private String dataTreeGrid;
```

### 5.5 @TreeStyle 树形样式

对应组件：`ood.UI.TreeView`, `ood.UI.MTreeView`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TreeStyle {
    CSStyle list() default @CSStyle();
    CSStyle item() default @CSStyle();
    CSFont caption() default @CSFont();
    CSLayout icon() default @CSLayout();
    CSStyle expand() default @CSStyle();
    CSStyle selected() default @CSStyle();
    CSStyle hover() default @CSStyle();
    CSLayout indent() default @CSLayout();
    CSBorder line() default @CSBorder();
    CSStyle checkbox() default @CSStyle();
    CSStyle loading() default @CSStyle();
}
```

**使用示例：**

```java
@TreeStyle(
    list = @CSStyle(className = "file-tree"),
    item = @CSStyle(customCss = "padding: 4px 8px; cursor: pointer;"),
    caption = @CSFont(fontSize = "14px"),
    selected = @CSStyle(customCss = "background: #e3f2fd;"),
    indent = @CSLayout(paddingLeft = "20px")
)
private String fileTree;
```

### 5.6 @TabsStyle 标签页样式

对应组件：`ood.UI.Tabs`, `ood.UI.MTabs`, `ood.UI.FoldingTabs`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TabsStyle {
    CSStyle list() default @CSStyle();
    CSStyle item() default @CSStyle();
    CSFont caption() default @CSFont();
    CSStyle close() default @CSStyle();
    CSStyle selected() default @CSStyle();
    CSStyle hover() default @CSStyle();
    CSStyle panel() default @CSStyle();
    CSStyle content() default @CSStyle();
    CSLayout icon() default @CSLayout();
    CSStyle scrollBtn() default @CSStyle();
    CSStyle dropdown() default @CSStyle();
    CSStyle foldBtn() default @CSStyle();
}
```

**使用示例：**

```java
@TabsStyle(
    list = @CSStyle(customCss = "border-bottom: 1px solid #e0e0e0;"),
    item = @CSStyle(customCss = "padding: 12px 16px; cursor: pointer;"),
    selected = @CSStyle(customCss = "border-bottom: 2px solid #1976d2; color: #1976d2;"),
    panel = @CSStyle(customCss = "padding: 16px;")
)
private String mainTabs;
```

### 5.7 @PanelStyle 面板样式

对应组件：`ood.UI.Panel`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PanelStyle {
    CSStyle panel() default @CSStyle();
    CSBorder border() default @CSBorder();
    CSFlex flex() default @CSFlex();
    CSStyle header() default @CSStyle();
    CSStyle content() default @CSStyle();
    CSStyle footer() default @CSStyle();
    CSFont caption() default @CSFont();
    CSStyle close() default @CSStyle();
}
```

**使用示例：**

```java
@PanelStyle(
    panel = @CSStyle(className = "info-panel"),
    border = @CSBorder(border = "1px solid #e0e0e0", borderRadius = "8px"),
    header = @CSStyle(customCss = "background: #f5f5f5; padding: 12px 16px; border-bottom: 1px solid #e0e0e0;"),
    content = @CSStyle(customCss = "padding: 16px;"),
    caption = @CSFont(fontSize = "16px", fontWeight = CSFontWeight.BOLD)
)
private String infoPanel;
```

### 5.8 @DialogStyle 对话框样式

对应组件：`ood.UI.Dialog`, `ood.UI.MDialog`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DialogStyle {
    CSStyle panel() default @CSStyle();
    CSStyle mask() default @CSStyle();
    CSStyle header() default @CSStyle();
    CSStyle content() default @CSStyle();
    CSStyle footer() default @CSStyle();
    CSFont title() default @CSFont();
    CSStyle close() default @CSStyle();
    CSStyle max() default @CSStyle();
    CSStyle min() default @CSStyle();
    CSStyle buttons() default @CSStyle();
    CSBorder border() default @CSBorder();
    String boxShadow() default "";
    CSTransform animation() default @CSTransform();
}
```

**使用示例：**

```java
@DialogStyle(
    panel = @CSStyle(customCss = "background: #fff; border-radius: 8px; min-width: 400px;"),
    mask = @CSStyle(customCss = "background: rgba(0,0,0,0.5);"),
    header = @CSStyle(customCss = "padding: 16px; border-bottom: 1px solid #e0e0e0;"),
    title = @CSFont(fontSize = "18px", fontWeight = CSFontWeight.BOLD),
    boxShadow = "0 4px 12px rgba(0,0,0,0.15)",
    animation = @CSTransform(transition = "all 0.3s ease")
)
private String confirmDialog;
```

### 5.9 @MenuStyle 菜单样式

对应组件：`ood.UI.MenuBar`, `ood.UI.PopMenu`

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MenuStyle {
    CSStyle list() default @CSStyle();
    CSStyle item() default @CSStyle();
    CSFont caption() default @CSFont();
    CSLayout icon() default @CSLayout();
    CSStyle arrow() default @CSStyle();
    CSBorder split() default @CSBorder();
    CSStyle selected() default @CSStyle();
    CSStyle hover() default @CSStyle();
    CSStyle submenu() default @CSStyle();
    CSStyle toolbar() default @CSStyle();
    CSStyle disabled() default @CSStyle();
    CSFont shortcut() default @CSFont();
}
```

**使用示例：**

```java
@MenuStyle(
    list = @CSStyle(customCss = "background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.15);"),
    item = @CSStyle(customCss = "padding: 8px 16px; cursor: pointer;"),
    caption = @CSFont(fontSize = "14px"),
    hover = @CSStyle(customCss = "background: #f5f5f5;"),
    split = @CSBorder(borderTop = "1px solid #e0e0e0"),
    shortcut = @CSFont(fontSize = "12px", color = "#999")
)
private String contextMenu;
```

### 5.10 @CardStyle 卡片样式

对应组件：`ood.UI.Card`

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CardStyle {
    CardPreset preset() default CardPreset.UNSET;
    CSFont font() default @CSFont;
    CSLayout layout() default @CSLayout;
    CSBorder border() default @CSBorder;
}
```

**使用示例：**

```java
@CardStyle(
    preset = CardPreset.SHADOW,
    layout = @CSLayout(width = "300px", padding = "16px"),
    border = @CSBorder(borderRadius = "12px")
)
private String productCard;
```

### 5.11 @AlertStyle 警告样式

对应组件：`ood.UI.Alert`, `ood.UI.Message`

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AlertStyle {
    AlertPreset preset() default AlertPreset.UNSET;
    CSFont font() default @CSFont;
    CSLayout layout() default @CSLayout;
    CSBorder border() default @CSBorder;
}
```

**使用示例：**

```java
@AlertStyle(preset = AlertPreset.SUCCESS)
private String successAlert;

@AlertStyle(
    preset = AlertPreset.WARNING,
    layout = @CSLayout(margin = "20px")
)
private String warningAlert;
```

### 5.12 @TableStyle 表格样式

对应组件：`ood.UI.Table`

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableStyle {
    TablePreset preset() default TablePreset.UNSET;
    CSFont font() default @CSFont;
    CSLayout layout() default @CSLayout;
    CSBorder border() default @CSBorder;
}
```

**使用示例：**

```java
@TableStyle(preset = TablePreset.STRIPED)
private String dataTable;

@TableStyle(
    preset = TablePreset.BORDERED,
    layout = @CSLayout(width = "100%")
)
private String borderedTable;
```

### 5.13 @TagStyle 标签样式

对应组件：`ood.UI.Tag`

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TagStyle {
    TagPreset preset() default TagPreset.UNSET;
    CSFont font() default @CSFont;
    CSLayout layout() default @CSLayout;
    CSBorder border() default @CSBorder;
}
```

**使用示例：**

```java
@TagStyle(preset = TagPreset.PRIMARY)
private String statusTag;

@TagStyle(
    preset = TagPreset.SUCCESS,
    layout = @CSLayout(margin = "4px")
)
private String successTag;
```

## 6. 预设样式

### 6.1 @CSPreset 组合预设

`@CSPreset`是一个组合预设注解，用于快速应用多种预设样式。

```java
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSPreset {
    ButtonPreset button() default ButtonPreset.UNSET;
    InputPreset input() default InputPreset.UNSET;
    AlertPreset alert() default AlertPreset.UNSET;
    CardPreset card() default CardPreset.UNSET;
    TagPreset tag() default TagPreset.UNSET;
}
```

**使用示例：**

```java
@CSPreset(
    button = ButtonPreset.MATERIAL_CONTAINED,
    input = InputPreset.MATERIAL_OUTLINED
)
public class MaterialFormView {
    // 表单字段
}
```

### 6.2 ButtonPreset 按钮预设

`ButtonPreset`枚举提供基于现代UI框架的按钮预设样式。

**预设类型：**

| 预设值 | 框架 | 说明 |
|--------|------|------|
| UNSET | - | 未设置 |
| MATERIAL_CONTAINED | Material Design | 实心按钮 |
| MATERIAL_OUTLINED | Material Design | 描边按钮 |
| MATERIAL_TEXT | Material Design | 文本按钮 |
| ANT_PRIMARY | Ant Design | 主要按钮 |
| ANT_DEFAULT | Ant Design | 默认按钮 |
| ANT_DASHED | Ant Design | 虚线按钮 |
| ANT_LINK | Ant Design | 链接按钮 |
| ELEMENT_PRIMARY | Element Plus | 主要按钮 |
| ELEMENT_SUCCESS | Element Plus | 成功按钮 |
| ELEMENT_WARNING | Element Plus | 警告按钮 |
| ELEMENT_DANGER | Element Plus | 危险按钮 |
| ELEMENT_ROUND | Element Plus | 圆角按钮 |
| BOOTSTRAP_PRIMARY | Bootstrap | 主要按钮 |
| BOOTSTRAP_SECONDARY | Bootstrap | 次要按钮 |
| BOOTSTRAP_OUTLINE_PRIMARY | Bootstrap | 描边主要按钮 |
| ICON_BUTTON | - | 图标按钮 |
| FAB | Material Design | 浮动操作按钮 |
| FAB_MINI | Material Design | 小型浮动按钮 |

**预设样式值：**

```java
// Material Design Contained
backgroundColor: #1976d2
color: #ffffff
borderRadius: 4px
padding: 8px 22px
boxShadow: 0 3px 1px -2px rgba(0,0,0,0.2), 0 2px 2px 0 rgba(0,0,0,0.14)

// Ant Design Primary
backgroundColor: #1677ff
color: #ffffff
borderRadius: 6px
padding: 6px 16px

// Element Plus Primary
backgroundColor: #409eff
color: #ffffff
borderRadius: 4px
padding: 12px 20px
```

**使用方式：**

```java
// 方式1：在CSLayout中使用
@CSLayout(buttonPreset = ButtonPreset.MATERIAL_CONTAINED)
private String materialBtn;

// 方式2：在CSBorder中使用
@CSBorder(buttonPreset = ButtonPreset.ANT_PRIMARY)
private String antBtn;

// 方式3：在CSFont中使用
@CSFont(buttonPreset = ButtonPreset.ELEMENT_PRIMARY)
private String elementBtn;

// 方式4：通过枚举方法获取样式
ButtonPreset.MATERIAL_CONTAINED.getFont();  // 获取字体样式
ButtonPreset.MATERIAL_CONTAINED.getLayout(); // 获取布局样式
ButtonPreset.MATERIAL_CONTAINED.getBorder(); // 获取边框样式
```

### 6.3 InputPreset 输入框预设

`InputPreset`枚举提供多种输入框预设样式。

**预设类型：**

| 预设值 | 框架 | 说明 |
|--------|------|------|
| UNSET | - | 未设置 |
| MATERIAL_FILLED | Material Design | 填充输入框 |
| MATERIAL_OUTLINED | Material Design | 描边输入框 |
| MATERIAL_STANDARD | Material Design | 标准输入框 |
| ANT_DEFAULT | Ant Design | 默认尺寸 |
| ANT_LARGE | Ant Design | 大尺寸 |
| ANT_SMALL | Ant Design | 小尺寸 |
| ANT_TEXTAREA | Ant Design | 文本域 |
| ELEMENT_DEFAULT | Element Plus | 默认输入框 |
| ELEMENT_DISABLED | Element Plus | 禁用状态 |
| ELEMENT_READONLY | Element Plus | 只读状态 |
| BOOTSTRAP_DEFAULT | Bootstrap | 默认尺寸 |
| BOOTSTRAP_LARGE | Bootstrap | 大尺寸 |
| BOOTSTRAP_SMALL | Bootstrap | 小尺寸 |
| SEARCH | - | 搜索框 |
| PASSWORD | - | 密码框 |
| NUMBER | - | 数字输入框 |
| DATE_PICKER | - | 日期选择器 |

**预设样式值：**

```java
// Material Design Outlined
border: 1px solid rgba(0, 0, 0, 0.23)
borderRadius: 4px
padding: 12px 16px
height: 48px
backgroundColor: transparent

// Ant Design Default
border: 1px solid #d9d9d9
borderRadius: 6px
padding: 4px 11px
height: 32px
backgroundColor: #ffffff

// Element Plus Default
border: 1px solid #dcdfe6
borderRadius: 4px
padding: 0 15px
height: 32px
backgroundColor: #ffffff
```

**使用方式：**

```java
// 方式1：在CSLayout中使用
@CSLayout(inputPreset = InputPreset.MATERIAL_OUTLINED)
private String materialInput;

// 方式2：在CSBorder中使用
@CSBorder(inputPreset = InputPreset.ANT_DEFAULT)
private String antInput;

// 方式3：通过枚举方法获取样式
InputPreset.MATERIAL_OUTLINED.getFont();  // 获取字体样式
InputPreset.MATERIAL_OUTLINED.getLayout(); // 获取布局样式
InputPreset.MATERIAL_OUTLINED.getBorder(); // 获取边框样式
```

### 6.4 AlertPreset 警告预设

`AlertPreset`枚举提供警告/消息组件的预设样式。

**预设类型：**

| 预设值 | 说明 | 背景色 | 文字色 |
|--------|------|--------|--------|
| UNSET | 未设置 | - | - |
| INFO | 信息提示 | #e6f7ff | #1890ff |
| SUCCESS | 成功提示 | #f6ffed | #52c41a |
| WARNING | 警告提示 | #fffbe6 | #faad14 |
| ERROR | 错误提示 | #fff2f0 | #ff4d4f |

**使用示例：**

```java
@AlertStyle(preset = AlertPreset.SUCCESS)
private String successAlert;
```

### 6.5 CardPreset 卡片预设

`CardPreset`枚举提供卡片组件的预设样式。

**预设类型：**

| 预设值 | 说明 | 特点 |
|--------|------|------|
| UNSET | 未设置 | - |
| BASIC | 基础卡片 | 白色背景，8px圆角 |
| BORDERED | 边框卡片 | 带边框 |
| SHADOW | 阴影卡片 | 带阴影效果 |

**使用示例：**

```java
@CardStyle(preset = CardPreset.SHADOW)
private String productCard;
```

### 6.6 TagPreset 标签预设

`TagPreset`枚举提供标签组件的预设样式。

**预设类型：**

| 预设值 | 说明 | 背景色 |
|--------|------|--------|
| UNSET | 未设置 | - |
| DEFAULT | 默认标签 | #f0f0f0 |
| PRIMARY | 主要标签 | #1890ff |
| SUCCESS | 成功标签 | #52c41a |
| WARNING | 警告标签 | #faad14 |
| DANGER | 危险标签 | #ff4d4f |

**使用示例：**

```java
@TagStyle(preset = TagPreset.PRIMARY)
private String statusTag;
```

### 6.7 TablePreset 表格预设

`TablePreset`枚举提供表格组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| BASIC | 基础表格 |
| BORDERED | 边框表格 |
| STRIPED | 条纹表格 |

**使用示例：**

```java
@TableStyle(preset = TablePreset.STRIPED)
private String dataTable;
```

### 6.8 FormPreset 表单预设

`FormPreset`枚举提供表单组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| VERTICAL | 垂直表单 |
| HORIZONTAL | 水平表单 |
| INLINE | 行内表单 |

### 6.9 DialogPreset 对话框预设

`DialogPreset`枚举提供对话框组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| MODAL | 模态对话框 |
| DRAWER | 抽屉式对话框 |
| FULLSCREEN | 全屏对话框 |

### 6.10 PanelPreset 面板预设

`PanelPreset`枚举提供面板组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| BASIC | 基础面板 |
| CARD | 卡片面板 |
| COLLAPSIBLE | 可折叠面板 |

### 6.11 TabsPreset 标签页预设

`TabsPreset`枚举提供标签页组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| LINE | 线型标签页 |
| CARD | 卡片标签页 |
| BUTTON | 按钮标签页 |

### 6.12 MenuPreset 菜单预设

`MenuPreset`枚举提供菜单组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| VERTICAL | 垂直菜单 |
| HORIZONTAL | 水平菜单 |
| INLINE | 行内菜单 |

### 6.13 TreePreset 树形预设

`TreePreset`枚举提供树形组件的预设样式。

**预设类型：**

| 预设值 | 说明 |
|--------|------|
| UNSET | 未设置 |
| DEFAULT | 默认树形 |
| DIRECTORY | 目录树形 |
| SIMPLE | 简单树形 |

## 7. CSS枚举类型

所有CSS枚举类型都位于`net.ooder.annotation.ui.css.enums`包下。

### 7.1 布局相关枚举

**CSDisplay - 显示类型**

```java
public enum CSDisplay {
    UNSET(""),          // 未设置
    NONE("none"),       // 不显示
    BLOCK("block"),     // 块级
    INLINE("inline"),   // 行内
    INLINE_BLOCK("inline-block"),  // 行内块
    FLEX("flex"),       // Flex容器
    INLINE_FLEX("inline-flex"),    // 行内Flex
    GRID("grid"),       // TreeGrid容器
    INLINE_GRID("inline-grid"),    // 行内TreeGrid
    TABLE("table"),     // 表格
    TABLE_CELL("table-cell"),      // 表格单元格
    TABLE_ROW("table-row"),        // 表格行
    LIST_ITEM("list-item"),        // 列表项
    CONTENTS("contents")           // 内容
}
```

**CSPosition - 定位类型**

```java
public enum CSPosition {
    UNSET(""),          // 未设置
    STATIC("static"),   // 静态定位
    RELATIVE("relative"), // 相对定位
    ABSOLUTE("absolute"), // 绝对定位
    FIXED("fixed"),     // 固定定位
    STICKY("sticky")    // 粘性定位
}
```

**CSOverflow - 溢出处理**

```java
public enum CSOverflow {
    UNSET(""),          // 未设置
    VISIBLE("visible"), // 可见
    HIDDEN("hidden"),   // 隐藏
    SCROLL("scroll"),   // 滚动
    AUTO("auto")        // 自动
}
```

**CSVisibility - 可见性**

```java
public enum CSVisibility {
    UNSET(""),          // 未设置
    VISIBLE("visible"), // 可见
    HIDDEN("hidden"),   // 隐藏（占位）
    COLLAPSE("collapse") // 折叠（不占位）
}
```

**CSCursor - 光标样式**

```java
public enum CSCursor {
    UNSET(""),
    AUTO("auto"),
    DEFAULT("default"),
    POINTER("pointer"),       // 手型
    MOVE("move"),             // 移动
    TEXT("text"),             // 文本
    WAIT("wait"),             // 等待
    CROSSHAIR("crosshair"),   // 十字
    NOT_ALLOWED("not-allowed"), // 禁止
    GRAB("grab"),             // 抓取
    GRABBING("grabbing")      // 抓取中
}
```

**CSBoxSizing - 盒模型**

```java
public enum CSBoxSizing {
    UNSET(""),
    CONTENT_BOX("content-box"), // 内容盒模型
    BORDER_BOX("border-box")    // 边框盒模型
}
```

### 7.2 字体相关枚举

**CSFontWeight - 字体粗细**

```java
public enum CSFontWeight {
    UNSET(""),
    NORMAL("normal"),
    BOLD("bold"),
    W100("100"),
    W200("200"),
    W300("300"),
    W400("400"),    // Normal
    W500("500"),    // Medium
    W600("600"),    // Semi Bold
    W700("700"),    // Bold
    W800("800"),
    W900("900")
}
```

**CSFontStyle - 字体样式**

```java
public enum CSFontStyle {
    UNSET(""),
    NORMAL("normal"),
    ITALIC("italic"),   // 斜体
    OBLIQUE("oblique")  // 倾斜
}
```

**CSTextAlign - 文本对齐**

```java
public enum CSTextAlign {
    UNSET(""),
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    JUSTIFY("justify")
}
```

**CSTextDecoration - 文本装饰**

```java
public enum CSTextDecoration {
    UNSET(""),
    NONE("none"),
    UNDERLINE("underline"),       // 下划线
    OVERLINE("overline"),         // 上划线
    LINE_THROUGH("line-through")  // 删除线
}
```

**CSTextTransform - 文本转换**

```java
public enum CSTextTransform {
    UNSET(""),
    NONE("none"),
    CAPITALIZE("capitalize"), // 首字母大写
    UPPERCASE("uppercase"),   // 全大写
    LOWERCASE("lowercase")    // 全小写
}
```

**CSWhiteSpace - 空白处理**

```java
public enum CSWhiteSpace {
    UNSET(""),
    NORMAL("normal"),     // 正常换行
    NOWRAP("nowrap"),     // 不换行
    PRE("pre"),           // 保留空白
    PRE_WRAP("pre-wrap"), // 保留空白并换行
    PRE_LINE("pre-line")  // 合并空白但换行
}
```

**CSVerticalAlign - 垂直对齐**

```java
public enum CSVerticalAlign {
    UNSET(""),
    BASELINE("baseline"),
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom"),
    TEXT_TOP("text-top"),
    TEXT_BOTTOM("text-bottom")
}
```

### 7.3 边框相关枚举

**CSBorderStyle - 边框样式**

```java
public enum CSBorderStyle {
    UNSET(""),
    NONE("none"),
    HIDDEN("hidden"),
    DOTTED("dotted"),   // 点线
    DASHED("dashed"),   // 虚线
    SOLID("solid"),     // 实线
    DOUBLE("double"),   // 双线
    GROOVE("groove"),   // 凹槽
    RIDGE("ridge"),     // 脊线
    INSET("inset"),     // 内陷
    OUTSET("outset")    // 外凸
}
```

### 7.4 Flex相关枚举

**CSFlexDirection - Flex方向**

```java
public enum CSFlexDirection {
    ROW("row"),
    ROW_REVERSE("row-reverse"),
    COLUMN("column"),
    COLUMN_REVERSE("column-reverse")
}
```

**CSFlexWrap - Flex换行**

```java
public enum CSFlexWrap {
    NOWRAP("nowrap"),
    WRAP("wrap"),
    WRAP_REVERSE("wrap-reverse")
}
```

**CSJustifyContent - 主轴对齐**

```java
public enum CSJustifyContent {
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    CENTER("center"),
    SPACE_BETWEEN("space-between"),
    SPACE_AROUND("space-around"),
    SPACE_EVENLY("space-evenly")
}
```

**CSAlignItems - 交叉轴对齐**

```java
public enum CSAlignItems {
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    CENTER("center"),
    BASELINE("baseline"),
    STRETCH("stretch")
}
```

**CSAlignSelf - 项目自身对齐**

```java
public enum CSAlignSelf {
    AUTO("auto"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    CENTER("center"),
    BASELINE("baseline"),
    STRETCH("stretch")
}
```

**CSAlignContent - 多行对齐**

```java
public enum CSAlignContent {
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    CENTER("center"),
    SPACE_BETWEEN("space-between"),
    SPACE_AROUND("space-around"),
    STRETCH("stretch")
}
```

### 7.5 组件变体枚举

**CSButtonVariant - 按钮变体**

```java
public enum CSButtonVariant {
    UNSET(""),
    CONTAINED("contained"),
    OUTLINED("outlined"),
    TEXT("text"),
    PRIMARY("primary"),
    DASHED("dashed"),
    LINK("link"),
    GHOST("ghost"),
    DEFAULT("default"),
    PLAIN("plain"),
    ROUND("round"),
    CIRCLE("circle"),
    SUCCESS("success"),
    WARNING("warning"),
    DANGER("danger"),
    INFO("info"),
    LARGE("large"),
    SMALL("small"),
    MINI("mini")
}
```

**CSInputVariant - 输入框变体**

```java
public enum CSInputVariant {
    UNSET(""),
    DEFAULT("default"),
    LARGE("large"),
    SMALL("small"),
    MINI("mini")
}
```

**CSCardVariant - 卡片变体**

```java
public enum CSCardVariant {
    UNSET(""),
    ELEVATED("elevated"),  // 阴影卡片
    OUTLINED("outlined"),  // 描边卡片
    FILLED("filled")       // 填充卡片
}
```

**CSTabsVariant - 标签页变体**

```java
public enum CSTabsVariant {
    UNSET(""),
    LINE("line"),     // 线型标签
    CARD("card"),     // 卡片标签
    PILLS("pills")    // 胶囊标签
}
```

**CSTableVariant - 表格变体**

```java
public enum CSTableVariant {
    UNSET(""),
    STRIPED("striped"),   // 条纹表格
    BORDERED("bordered"), // 边框表格
    HOVER("hover"),       // 悬停高亮
    COMPACT("compact")    // 紧凑表格
}
```

## 8. 虚拟DOM映射

### 8.1 映射原理

CSS样式注解与组件虚拟DOM节点一一对应，每个注解属性映射到特定的DOM节点样式：

```
组件类
├── @ButtonStyle
│   ├── panel → <button> 元素样式
│   ├── caption → 按钮文字样式
│   ├── icon → 图标样式
│   ├── hover → :hover 伪类样式
│   └── ...
├── @InputStyle
│   ├── panel → <div class="input-wrapper"> 样式
│   ├── label → <label> 样式
│   ├── caption → <input> 样式
│   └── ...
└── @FormStyle
    ├── panel → <form> 样式
    ├── item → <div class="form-item"> 样式
    └── ...
```

### 8.2 状态映射

状态样式映射到CSS伪类：

| 注解属性 | CSS伪类 |
|----------|---------|
| normal | 默认状态 |
| hover | :hover |
| active | :active |
| focus | :focus |
| disabled | :disabled |
| readonly | :read-only |
| error | .error 类 |
| loading | .loading 类 |

### 8.3 组件层级映射

不同层级的组件样式注解对应不同的虚拟DOM结构：

**Level 1 - 基础组件**
- Button: panel → caption, icon
- Input: panel → label, caption, icon

**Level 2 - 容器组件**
- Panel: panel → header, content, footer
- Dialog: panel → mask, header, content, footer

**Level 3A - 列表组件**
- Menu: list → item → caption, icon, arrow

**Level 3B - 数据组件**
- Tree: list → item → caption, icon, expand
- TreeGrid: list → header, row → cell

**Level 4 - 复合组件**
- Tabs: list → item, panel → content
- Form: panel → item → label, field

## 9. 使用示例

### 9.1 基础按钮样式

```java
public class ButtonExample {
    
    @CSStyle(
        className = "btn btn-primary",
        font = @CSFont(
            fontSize = "14px",
            fontWeight = CSFontWeight.MEDIUM,
            color = "#ffffff"
        ),
        layout = @CSLayout(
            display = CSDisplay.INLINE_FLEX,
            padding = "8px 16px",
            cursor = CSCursor.POINTER
        ),
        border = @CSBorder(
            borderRadius = "4px",
            backgroundColor = "#1976d2"
        ),
        hover = "background-color: #1565c0;",
        active = "background-color: #0d47a1;"
    )
    private String primaryButton;
}
```

### 9.2 使用预设样式

```java
public class PresetExample {
    
    @CSLayout(buttonPreset = ButtonPreset.MATERIAL_CONTAINED)
    private String materialButton;
    
    @CSBorder(buttonPreset = ButtonPreset.ANT_PRIMARY)
    private String antButton;
    
    @CSLayout(inputPreset = InputPreset.MATERIAL_OUTLINED)
    private String materialInput;
    
    @CSBorder(inputPreset = InputPreset.ANT_DEFAULT)
    private String antInput;
}
```

### 9.3 组件样式注解

```java
public class ComponentStyleExample {
    
    @ButtonStyle(
        panel = @CSStyle(className = "custom-btn"),
        caption = @CSFont(fontSize = "16px", fontWeight = CSFontWeight.BOLD),
        border = @CSBorder(borderRadius = "8px"),
        hover = @CSStyle(customCss = "transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0,0,0,0.2);"),
        primary = @CSStyle(customCss = "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);")
    )
    private String gradientButton;
    
    @InputStyle(
        panel = @CSStyle(className = "modern-input"),
        label = @CSFont(fontSize = "12px", color = "#666"),
        border = @CSBorder(
            border = "2px solid transparent",
            borderRadius = "8px",
            background = "linear-gradient(#fff, #fff) padding-box, linear-gradient(135deg, #667eea, #764ba2) border-box"
        ),
        focus = @CSStyle(customCss = "box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);")
    )
    private String gradientInput;
}
```

### 9.4 表单样式配置

```java
@FormStyle(
    panel = @CSStyle(className = "login-form"),
    flex = @CSFlex(
        flexDirection = "column",
        gap = "24px"
    ),
    item = @CSStyle(customCss = "display: flex; flex-direction: column; gap: 8px;"),
    label = @CSFont(
        fontSize = "14px",
        fontWeight = CSFontWeight.MEDIUM,
        color = "#333"
    ),
    required = @CSStyle(customCss = "color: #f44336; margin-left: 4px;"),
    error = @CSStyle(customCss = "color: #f44336; font-size: 12px; margin-top: 4px;")
)
public class LoginFormView {
    
    @InputStyle(
        border = @CSBorder(
            border = "1px solid #d9d9d9",
            borderRadius = "6px"
        ),
        focus = @CSStyle(customCss = "border-color: #1976d2;")
    )
    private String username;
    
    @InputStyle(
        border = @CSBorder(
            border = "1px solid #d9d9d9",
            borderRadius = "6px"
        ),
        focus = @CSStyle(customCss = "border-color: #1976d2;")
    )
    private String password;
    
    @ButtonStyle(
        panel = @CSStyle(customCss = "width: 100%; background: #1976d2; color: #fff; border: none; padding: 12px; border-radius: 6px; cursor: pointer;"),
        hover = @CSStyle(customCss = "background: #1565c0;")
    )
    private String loginButton;
}
```

### 9.5 响应式布局

```java
public class ResponsiveExample {
    
    @CSStyle(
        className = "responsive-container",
        layout = @CSLayout(
            display = CSDisplay.FLEX,
            width = "100%",
            padding = "16px"
        ),
        flex = @CSFlex(
            flexDirection = "row",
            flexWrap = "wrap",
            gap = "16px"
        ),
        customCss = "@media (max-width: 768px) { flex-direction: column; }"
    )
    private String container;
    
    @CSStyle(
        layout = @CSLayout(
            width = "calc(50% - 8px)",
            padding = "16px"
        ),
        border = @CSBorder(
            borderRadius = "8px",
            backgroundColor = "#fff",
            boxShadow = "0 2px 4px rgba(0,0,0,0.1)"
        ),
        customCss = "@media (max-width: 768px) { width: 100%; }"
    )
    private String card;
}
```

## 10. 最佳实践

### 10.1 样式组织原则

1. **预设优先**：优先使用ButtonPreset和InputPreset预设样式，保持UI一致性
2. **组件封装**：复杂组件使用组件样式注解（如@ButtonStyle），简单场景使用基础样式注解
3. **状态完整**：为交互元素配置完整的hover、active、focus、disabled状态
4. **类型安全**：使用CSS枚举类型替代字符串值，避免拼写错误

### 10.2 性能优化

1. **类名复用**：使用className属性引用外部CSS类，减少内联样式
2. **样式继承**：利用CSS继承特性，在父元素设置可继承的样式
3. **避免过度嵌套**：保持注解结构简洁，避免过深的嵌套

### 10.3 可维护性

1. **命名规范**：使用语义化的className，如`btn-primary`、`form-input`
2. **注释说明**：为复杂的样式配置添加注释
3. **统一管理**：将公共样式提取为预设或常量

### 10.4 兼容性考虑

1. **浏览器前缀**：对于需要浏览器前缀的CSS属性，使用customCss直接编写
2. **降级方案**：为不支持新特性的浏览器提供降级样式
3. **渐进增强**：先保证基本功能，再添加高级效果

### 10.5 注意事项

1. 不要在运行时动态修改注解属性，注解是编译时确定的
2. 避免在customCss中编写过长的样式，应使用className引用外部CSS
3. 状态样式（hover、active等）应保持简洁，避免重复定义基础样式
4. 使用枚举类型时注意UNSET值表示未设置，不会生成对应的CSS属性

---

**版本历史**

| 版本 | 日期 | 说明 |
|------|------|------|
| 2.0.0 | 2026-02-14 | 初始版本，包含完整的CSS样式注解体系 |
