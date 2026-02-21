# ooder平台Form容器类型对比手册

## 目录

1. [概述](#1-概述)
2. [Form容器](#2-Form容器)
   - 2.1 特点
   - 2.2 适用场景
   - 2.3 核心注解
3. [PanelForm容器](#3-PanelForm容器)
   - 3.1 特点
   - 3.2 适用场景
   - 3.3 核心注解
4. [SVGPageForm容器](#4-SVGPageForm容器)
   - 4.1 特点
   - 4.2 适用场景
   - 4.3 核心注解
5. [容器类型对比](#5-容器类型对比)
6. [最佳实践](#6-最佳实践)

## 1. 概述

ooder平台提供了多种表单容器类型，每种容器都有其特定的用途和特点。正确选择和使用这些容器类型对于构建高质量的用户界面至关重要。本手册将详细介绍Form、PanelForm和SVGPageForm三种容器类型的特点、适用场景和使用方法，并进行对比分析。

## 2. Form容器

### 2.1 特点

Form容器是ooder平台中最基础的表单容器类型，具有以下特点：
- **强制布局**：采用结构化布局方式，字段按照预定义的行列排列
- **标准化**：遵循统一的表单设计规范，确保界面一致性
- **数据驱动**：以数据录入和展示为主要目的
- **简单易用**：配置相对简单，适合快速开发常规表单

### 2.2 适用场景

Form容器适用于以下场景：
- 常规数据录入表单
- 结构化信息展示页面
- 简单的业务流程表单
- 需要统一布局规范的界面

### 2.3 核心注解

Form容器使用@FormAnnotation注解进行定义：

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

## 3. PanelForm容器

### 3.1 特点

PanelForm容器是ooder平台中的核心容器，具有以下特点：
- **自由布局**：支持自由定位和大小调整，不强制结构化布局
- **多重嵌套**：支持作为父级容器容纳其他视图组件
- **停靠布局**：支持dock属性实现灵活的停靠布局
- **可切换性**：支持toggle属性实现可折叠/展开功能
- **丰富属性**：提供丰富的外观和行为配置选项

### 3.2 适用场景

PanelForm容器适用于以下场景：
- 复杂布局的界面设计
- 需要嵌套其他视图组件的容器
- 可折叠/展开的界面区域
- 需要停靠布局的界面
- 作为核心容器承载多个子组件

### 3.3 核心注解

PanelForm容器使用@PanelFormAnnotation注解进行定义：

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

## 4. SVGPageForm容器

### 4.1 特点

SVGPageForm容器是ooder平台中专门用于矢量图绘制的容器，具有以下特点：
- **矢量绘图**：专为SVG矢量图形设计和展示而优化
- **嵌套支持**：同样支持**FieldAnnotation嵌套子结构
- **图形属性**：提供丰富的SVG图形相关属性配置
- **交互能力**：支持图形元素的交互和事件处理
- **尺寸控制**：可精确控制画布的宽度和高度

### 4.2 适用场景

SVGPageForm容器适用于以下场景：
- 流程图绘制和展示
- 架构图设计和展示
- 拓扑图绘制和展示
- 图形化数据可视化
- 需要矢量图形支持的界面

### 4.3 核心注解

SVGPageForm容器使用@SVGPaperFormAnnotation注解进行定义：

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

## 5. 容器类型对比

| 特性 | Form容器 | PanelForm容器 | SVGPageForm容器 |
|------|----------|---------------|-----------------|
| 布局方式 | 强制结构化布局 | 自由布局 | SVG画布布局 |
| 嵌套支持 | 有限 | 完全支持 | 完全支持 |
| 主要用途 | 数据录入/展示 | 复杂界面容器 | 矢量图形绘制 |
| 核心注解 | @FormAnnotation | @PanelFormAnnotation | @SVGPaperFormAnnotation |
| 停靠支持 | 否 | 是 | 否 |
| 可切换性 | 否 | 是 | 否 |
| 矢量图形 | 否 | 否 | 是 |
| 适用场景 | 常规表单 | 复杂布局 | 图形绘制 |

## 6. 最佳实践

### 6.1 选择建议

1. **选择Form容器**：
   - 当需要创建结构化的数据录入表单时
   - 当需要快速开发标准化表单界面时
   - 当界面布局相对简单且规整时

2. **选择PanelForm容器**：
   - 当需要构建复杂的嵌套界面时
   - 当需要自由布局和停靠功能时
   - 当需要可折叠/展开的界面区域时
   - 当作为核心容器承载多个子组件时

3. **选择SVGPageForm容器**：
   - 当需要绘制和展示SVG矢量图形时
   - 当需要创建流程图、架构图等图形化界面时
   - 当需要图形元素交互功能时

### 6.2 设计原则

1. **一致性**：在同一项目中保持容器类型使用的一致性
2. **适用性**：根据具体需求选择最合适的容器类型
3. **性能**：避免过度嵌套，合理使用懒加载机制
4. **用户体验**：确保界面操作流畅，布局合理

通过正确理解和使用这三种容器类型，可以构建出更加灵活、高效和用户友好的ooder平台应用界面。