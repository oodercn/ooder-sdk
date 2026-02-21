# ooder平台SVGPageFormView嵌套视图结构手册

## 目录

1. [概述](#1-概述)
2. [SVGPageFormView嵌套机制](#2-SVGPageFormView嵌套机制)
3. [Field注解体系详解](#3-Field注解体系详解)
   - 3.1 TreeGridFieldAnnotation
   - 3.2 FormFieldAnnotation
   - 3.3 BlockFieldAnnotation
   - 3.4 PanelFieldAnnotation
   - 3.5 TreeFieldAnnotation
   - 3.6 TabsFieldAnnotation
   - 3.7 ModuleRefFieldAnnotation
   - 3.8 其他Field注解
4. [嵌套视图实现方式](#4-嵌套视图实现方式)
   - 4.1 内嵌视图方式
   - 4.2 Module引用方式
5. [SVGPageFormView嵌套示例](#5-SVGPageFormView嵌套示例)
6. [最佳实践](#6-最佳实践)
7. [注意事项](#7-注意事项)

## 1. 概述

SVGPageFormView是ooder平台中的矢量图容器视图，专门用于SVG矢量图形的绘制和展示。SVGPageFormView同样支持多重嵌套其他类型的视图组件，通过在SVGPageFormView定义文件中添加子视图的入口方法，并使用相应的****FieldAnnotation注解，可以将其他视图作为SVGPageFormView的字段引入。

SVGPageFormView的嵌套机制与PanelFormView类似：
- 内嵌视图：在内部完成实例化，附属于SVGPageFormView
- Module引用：通过ModuleAnnotation方式嵌入，适用于独立模块间的引用

关于EChart和FChart图表组件的详细使用说明，请参考[ooder平台EChart与FChart图表组件专题手册](file:///e:/ooder-gitee/ooder-annotation/ooder%E5%B9%B3%E5%8F%B0EChart%E4%B8%8EFChart%E5%9B%BE%E8%A1%A8%E7%BB%84%E4%BB%B6%E4%B8%93%E9%A2%98%E6%89%8B%E5%86%8C.md)。

## 2. SVGPageFormView嵌套机制

SVGPageFormView作为矢量图容器，支持自由布局并允许多重嵌套。SVGPageFormView容器嵌套时具有以下特点：
- 支持多重嵌套，可作为父级容器容纳其他视图组件
- 专为SVG矢量图形设计和展示而优化
- 同样支持**FieldAnnotation嵌套子结构
- 提供丰富的SVG图形相关属性配置

## 3. Field注解体系详解

SVGPageFormView支持与PanelFormView相同的完整Field注解体系，可以嵌套各种类型的子组件。

### 3.1 TreeGridFieldAnnotation

TreeGridFieldAnnotation用于在SVGPageFormView中嵌套TreeGrid类型的子组件。

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

### 3.2 FormFieldAnnotation

FormFieldAnnotation用于在SVGPageFormView中嵌套Form类型的子组件。

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

### 3.3 BlockFieldAnnotation

BlockFieldAnnotation用于在SVGPageFormView中嵌套Block类型的子组件。

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

### 3.4 PanelFieldAnnotation

PanelFieldAnnotation用于在SVGPageFormView中嵌套Panel类型的子组件。

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

### 3.5 TreeFieldAnnotation

TreeFieldAnnotation用于在SVGPageFormView中嵌套Tree类型的子组件。

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

### 3.6 TabsFieldAnnotation

TabsFieldAnnotation用于在SVGPageFormView中嵌套Tabs类型的子组件。

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

### 3.7 ModuleRefFieldAnnotation

ModuleRefFieldAnnotation用于在SVGPageFormView中引用其他模块的子组件。

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

### 3.8 其他Field注解

ooder平台还提供了多种其他Field注解用于在SVGPageFormView中嵌套不同类型的组件：
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
- ComboFieldAnnotation：用于嵌套Combo组件，更多详情请参考[ooder平台Combo组件专题手册](ooder平台Combo组件专题手册.md)

对于以上未详细说明的Field注解，其使用方式与已介绍的Field注解类似，都需要：
1. 在父级视图中定义相应的字段
2. 为字段添加对应的Field注解
3. 配合@CustomAnnotation定义外观属性
4. 确保服务类具有Web可访问性

关于EChartFieldAnnotation和FChartFieldAnnotation的详细使用说明，请参考[ooder平台EChart与FChart图表组件专题手册](file:///e:/ooder-gitee/ooder-annotation/ooder%E5%B9%B3%E5%8F%B0EChart%E4%B8%8EFChart%E5%9B%BE%E8%A1%A8%E7%BB%84%E4%BB%B6%E4%B8%93%E9%A2%98%E6%89%8B%E5%86%8C.md)。

## 4. 嵌套视图实现方式

### 4.1 内嵌视图方式

内嵌视图是在SVGPageFormView内部完成实例化，附属于SVGPageFormView的嵌套方式。

**实现步骤：**
1. 在SVGPageFormView类中定义子视图字段
2. 为子视图字段添加相应的****FieldAnnotation注解
3. 为子视图字段添加@CustomAnnotation注解定义外观属性
4. 确保子视图服务类具有Web可访问性且包含视图入口方法

**示例：**
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {FlowChartService.class}
)
public class FlowChartView {
    // 嵌套子表格
    @TreeGridFieldAnnotation(
        bindClass = PropertyTreeGridService.class,
        borderType = BorderType.inset
    )
    @CustomAnnotation(caption = "属性表格", index = 1)
    private PropertyTreeGrid propertyTreeGrid;
    
    // 嵌套控制面板
    @PanelFieldAnnotation(
        dock = Dock.right,
        caption = "控制面板",
        width = "200px"
    )
    @CustomAnnotation(caption = "控制面板", index = 2)
    private ControlPanel controlPanel;
}
```

### 4.2 Module引用方式

Module引用方式是通过ModuleAnnotation方式嵌入其他模块的视图组件。

**实现步骤：**
1. 在SVGPageFormView类中定义子视图字段
2. 为子视图字段添加@ModuleRefFieldAnnotation注解
3. 为子视图字段添加@CustomAnnotation注解定义外观属性
4. 确保被引用模块具有正确的访问路径和服务类

**示例：**
```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "500px",
    selectable = true,
    customService = {ArchitectureChartService.class}
)
public class ArchitectureChartView {
    // 引用其他模块的图例组件
    @ModuleRefFieldAnnotation(
        src = "/modules/chart/legend",
        bindClass = LegendService.class,
        dock = Dock.bottom
    )
    @CustomAnnotation(caption = "图例", index = 1)
    private LegendModule legendModule;
}
```

## 5. SVGPageFormView嵌套示例

以下是一个完整的SVGPageFormView嵌套示例，展示了如何在SVGPageFormView中嵌套多种类型的视图组件：

```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "600px",
    selectable = true,
    customService = {NetworkTopologyService.class}
)
public class NetworkTopologyView {
    // 嵌套设备属性表格
    @TreeGridFieldAnnotation(
        bindClass = DevicePropertyService.class,
        borderType = BorderType.inset,
        imageClass = "fa-solid fa-table"
    )
    @CustomAnnotation(caption = "设备属性", index = 1)
    private DevicePropertyTreeGrid devicePropertyTreeGrid;
    
    // 嵌套控制面板表单
    @FormFieldAnnotation(
        borderType = BorderType.inset,
        imageClass = "fa-solid fa-sliders"
    )
    @CustomAnnotation(caption = "控制面板", index = 2)
    private ControlPanelForm controlPanelForm;
    
    // 嵌套设备树
    @TreeFieldAnnotation(
        bindClass = DeviceTreeService.class,
        borderType = BorderType.inset,
        imageClass = "fa-solid fa-network-wired"
    )
    @CustomAnnotation(caption = "设备树", index = 3)
    private DeviceTreeView deviceTree;
    
    // 嵌套统计图表
    @EChartFieldAnnotation(
        chartType = EChartType.Line
    )
    @CustomAnnotation(caption = "流量统计", index = 4)
    private TrafficChart trafficChart;
    
    // 引用其他模块的工具栏
    @ModuleRefFieldAnnotation(
        src = "/modules/chart/toolbar",
        bindClass = ChartToolbarService.class,
        dock = Dock.top
    )
    @CustomAnnotation(caption = "工具栏", index = 5)
    private ToolbarModule toolbarModule;
}
```

## 6. 最佳实践

### 6.1 设计原则

1. **层次清晰**：嵌套层次不宜过深，建议不超过3层
2. **职责明确**：每个嵌套组件应有明确的职责
3. **性能优化**：合理使用懒加载和缓存机制
4. **用户体验**：确保嵌套结构的用户操作流畅性

### 6.2 实现规范

1. **注解使用**：
   - 根据子视图类型选择正确的Field注解
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

### 6.3 性能优化

1. **懒加载**：对于非立即显示的嵌套组件，使用懒加载机制
2. **缓存机制**：合理使用autoSave属性实现状态缓存
3. **动态销毁**：使用适当的动态销毁策略优化内存使用

## 7. 注意事项

1. **避免过深嵌套**：嵌套层次过深会影响性能和用户体验，建议不超过3层
2. **确保Web可访问性**：所有嵌套视图的服务类都必须具有Web可访问性
3. **正确使用bindClass**：bindClass绑定的服务类必须包含视图入口方法
4. **数据一致性**：确保嵌套视图间的数据传递和状态同步正确
5. **性能考虑**：合理设计嵌套结构，避免影响系统性能
6. **用户体验**：确保嵌套视图的操作流畅性和界面友好性
7. **兼容性**：确保不同类型的Field注解在不同浏览器中的兼容性

通过遵循以上SVGPageFormView嵌套视图结构规范，可以确保ooder平台SVGPageFormView嵌套视图的一致性、可维护性和用户体验，为复杂业务场景提供高质量的界面解决方案。