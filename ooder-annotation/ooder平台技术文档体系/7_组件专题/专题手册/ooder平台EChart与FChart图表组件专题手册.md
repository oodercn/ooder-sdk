# ooder平台EChart与FChart图表组件专题手册

## 目录

1. [概述](#1-概述)
2. [EChart组件体系](#2-EChart组件体系)
   - 2.1 EChartAnnotation
   - 2.2 EChartFieldAnnotation
   - 2.3 EChart组件特性
3. [FChart组件体系](#3-FChart组件体系)
   - 3.1 FChartAnnotation
   - 3.2 FChartFieldAnnotation
   - 3.3 FChartType图表类型
   - 3.4 FChart组件特性
4. [图表注解详解](#4-图表注解详解)
   - 4.1 核心属性
   - 4.2 事件处理
   - 4.3 菜单配置
5. [数据绑定与服务](#5-数据绑定与服务)
   - 5.1 数据源配置
   - 5.2 服务类设计
   - 5.3 数据格式
6. [嵌套视图中的图表组件](#6-嵌套视图中的图表组件)
   - 6.1 PanelForm中的图表
   - 6.2 SVGPageForm中的图表
   - 6.3 其他容器中的图表
7. [应用场景](#7-应用场景)
   - 7.1 数据统计展示
   - 7.2 业务分析报表
   - 7.3 实时数据监控
8. [最佳实践](#8-最佳实践)
9. [注意事项](#9-注意事项)

## 1. 概述

ooder平台提供了两种强大的图表组件体系：EChart和FChart，用于创建丰富多样的数据可视化图表。这两种图表组件都支持在Web应用中展示各种类型的统计图表，包括柱状图、折线图、饼图、面积图等。

EChart基于百度开源的ECharts库，提供了丰富的图表类型和高度可定制的配置选项。FChart基于FusionCharts XT商业图表库，提供了专业的图表渲染能力和企业级特性。

在ooder平台中，图表组件可以通过两种方式使用：
1. **视图级图表**：使用@EChartAnnotation或@FChartAnnotation作为主视图注解
2. **字段级图表**：使用@EChartFieldAnnotation或@FChartFieldAnnotation作为字段注解，在容器视图中嵌套使用

## 2. EChart组件体系

### 2.1 EChartAnnotation

EChartAnnotation用于定义视图级的EChart图表组件：

**核心属性：**
- chartTheme：图表主题
- chartResizeSilent：图表调整大小时是否静默
- chartCDN：图表CDN地址
- chartCDNGL：图表CDN GL地址
- chartDevicePixelRatio：设备像素比率
- xAxisDateFormatter：X轴日期格式化器

**示例：**
```java
@EChartAnnotation(
    chartTheme = "dark",
    chartResizeSilent = true,
    chartCDN = "https://cdn.jsdelivr.net/npm/echarts@5.4.0/dist/echarts.min.js"
)
public class SalesChartView {
    // 图表配置
}
```

### 2.2 EChartFieldAnnotation

EChartFieldAnnotation用于在容器视图中嵌套EChart图表组件：

**核心属性：**
- renderer：渲染器
- selectable：是否可选择
- chartCDN：图表CDN地址
- JSONUrl：JSON数据URL
- XMLUrl：XML数据URL

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "数据分析面板",
    customService = {AnalysisService.class}
)
public class AnalysisPanelView {
    @EChartFieldAnnotation(
        renderer = "canvas",
        selectable = true,
        chartCDN = "https://cdn.jsdelivr.net/npm/echarts@5.4.0/dist/echarts.min.js"
    )
    @CustomAnnotation(caption = "销售趋势图", index = 1)
    private EChartFieldAnnotation salesTrendChart;
}
```

### 2.3 EChart组件特性

EChart组件具有以下特性：
- 支持多种图表类型（折线图、柱状图、饼图、散点图等）
- 高度可定制的样式和交互
- 响应式设计支持
- 丰富的动画效果
- 多语言支持

## 3. FChart组件体系

### 3.1 FChartAnnotation

FChartAnnotation用于定义视图级的FChart图表组件：

**核心属性：**
- renderer：渲染器
- selectable：是否可选择
- chartCDN：图表CDN地址
- chartType：图表类型（FChartType枚举）
- JSONUrl：JSON数据URL
- XMLUrl：XML数据URL
- bindService：绑定服务类

**示例：**
```java
@FChartAnnotation(
    chartType = FChartType.Column2D,
    renderer = "javascript",
    selectable = true,
    JSONUrl = "/chart/sales/data"
)
public class SalesColumnChartView {
    // 图表配置
}
```

### 3.2 FChartFieldAnnotation

FChartFieldAnnotation用于在容器视图中嵌套FChart图表组件：

**核心属性：**
- renderer：渲染器
- selectable：是否可选择
- chartCDN：图表CDN地址
- chartType：图表类型（FChartType枚举）
- JSONUrl：JSON数据URL
- XMLUrl：XML数据URL
- enumClass：枚举类
- bindService：绑定服务类

**示例：**
```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "业务仪表板",
    customService = {DashboardService.class}
)
public class BusinessDashboardView {
    @FChartFieldAnnotation(
        chartType = FChartType.Pie2D,
        renderer = "javascript",
        selectable = true,
        JSONUrl = "/chart/market/share"
    )
    @CustomAnnotation(caption = "市场份额图", index = 1)
    private FChartFieldAnnotation marketShareChart;
}
```

### 3.3 FChartType图表类型

FChart支持丰富的图表类型，包括：

**基础图表类型：**
- Column2D/Column3D：2D/3D柱状图
- Bar2D/Bar3D：2D/3D条形图
- Line：折线图
- Area2D：2D面积图
- Pie2D/Pie3D：2D/3D饼图
- Doughnut2D/Doughnut3D：2D/3D环形图

**组合图表类型：**
- MSColumn2D/MSColumn3D：多系列柱状图
- MSBar2D/MSBar3D：多系列条形图
- MSLine：多系列折线图
- MSCombi2D/MSCombi3D：多系列组合图

**特殊图表类型：**
- Scatter：散点图
- Bubble：气泡图
- Funnel：漏斗图
- Pyramid：金字塔图
- Radar：雷达图
- RealTimeLine：实时折线图

### 3.4 FChart组件特性

FChart组件具有以下特性：
- 企业级图表库，商业许可
- 丰富的图表类型和模板
- 专业的数据可视化效果
- 强大的交互功能
- 跨平台兼容性

## 4. 图表注解详解

### 4.1 核心属性

**通用属性：**
- renderer：指定图表渲染方式（canvas/svg/javascript）
- selectable：控制图表元素是否可选择
- chartCDN：指定图表库的CDN地址
- JSONUrl/XMLUrl：指定数据源URL

**EChart特有属性：**
- chartTheme：图表主题（light/dark等）
- chartResizeSilent：调整大小时是否静默
- xAxisDateFormatter：X轴日期格式化器

**FChart特有属性：**
- chartType：图表类型（FChartType枚举）
- enumClass：关联的枚举类
- bindService：绑定的服务类

### 4.2 事件处理

图表组件支持丰富的事件处理机制：

```java
@EChartFieldAnnotation(
    renderer = "canvas"
)
@CustomFieldEvent(
    eventEnum = CustomFieldEventEnum.onClick,
    actions = {
        @CustomAction(
            type = ActionTypeEnum.METHOD,
            name = "onChartClick",
            method = "handleChartClick"
        )
    }
)
@CustomAnnotation(caption = "交互式图表", index = 1)
private EChartFieldAnnotation interactiveChart;
```

### 4.3 菜单配置

图表组件支持自定义菜单配置：

```java
@FChartFieldAnnotation(
    chartType = FChartType.Column2D
)
@CustomFormMenu(
    caption = "导出图表",
    imageClass = "fas fa-download",
    actions = {
        @CustomAction(
            type = ActionTypeEnum.METHOD,
            name = "exportChart",
            method = "exportChartData"
        )
    }
)
@CustomAnnotation(caption = "带菜单的图表", index = 1)
private FChartFieldAnnotation chartWithMenu;
```

## 5. 数据绑定与服务

### 5.1 数据源配置

图表组件支持多种数据源配置方式：

**JSON数据源：**
```java
@EChartFieldAnnotation(
    JSONUrl = "/api/chart/sales/data",
    renderer = "canvas"
)
@CustomAnnotation(caption = "销售数据图表", index = 1)
private EChartFieldAnnotation salesChart;
```

**XML数据源：**
```java
@FChartFieldAnnotation(
    chartType = FChartType.Pie2D,
    XMLUrl = "/api/chart/market/xml"
)
@CustomAnnotation(caption = "市场数据图表", index = 1)
private FChartFieldAnnotation marketChart;
```

### 5.2 服务类设计

图表服务类需要遵循ooder平台的服务规范：

```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/api/chart")
@Service
public class ChartDataService {
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CURRFORM},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @GetMapping("/sales/data")
    @ResponseBody
    public ResultModel<Map<String, Object>> getSalesChartData() {
        ResultModel<Map<String, Object>> resultModel = new ResultModel<>();
        
        try {
            Map<String, Object> chartData = new HashMap<>();
            
            // 构造图表数据
            List<String> categories = Arrays.asList("一月", "二月", "三月", "四月", "五月", "六月");
            List<Integer> values = Arrays.asList(120, 132, 101, 134, 90, 230);
            
            chartData.put("categories", categories);
            chartData.put("values", values);
            
            resultModel.setData(chartData);
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

### 5.3 数据格式

**EChart数据格式：**
```json
{
  "xAxis": {
    "type": "category",
    "data": ["一月", "二月", "三月", "四月", "五月", "六月"]
  },
  "yAxis": {
    "type": "value"
  },
  "series": [
    {
      "data": [120, 132, 101, 134, 90, 230],
      "type": "line"
    }
  ]
}
```

**FChart数据格式：**
```json
{
  "chart": {
    "caption": "销售数据",
    "xAxisName": "月份",
    "yAxisName": "销售额"
  },
  "data": [
    {"label": "一月", "value": "120"},
    {"label": "二月", "value": "132"},
    {"label": "三月", "value": "101"},
    {"label": "四月", "value": "134"},
    {"label": "五月", "value": "90"},
    {"label": "六月", "value": "230"}
  ]
}
```

## 6. 嵌套视图中的图表组件

### 6.1 PanelForm中的图表

在PanelForm中嵌套图表组件是常见的使用场景：

```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "业务分析面板",
    borderType = BorderType.inset,
    customService = {BusinessAnalysisService.class}
)
public class BusinessAnalysisView {
    // 销售趋势图
    @EChartFieldAnnotation(
        renderer = "canvas",
        chartCDN = "https://cdn.jsdelivr.net/npm/echarts@5.4.0/dist/echarts.min.js"
    )
    @CustomAnnotation(caption = "销售趋势", index = 1)
    private EChartFieldAnnotation salesTrendChart;
    
    // 市场份额图
    @FChartFieldAnnotation(
        chartType = FChartType.Pie2D,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "市场份额", index = 2)
    private FChartFieldAnnotation marketShareChart;
    
    // 用户增长图
    @EChartFieldAnnotation(
        renderer = "canvas"
    )
    @CustomAnnotation(caption = "用户增长", index = 3)
    private EChartFieldAnnotation userGrowthChart;
}
```

### 6.2 SVGPageForm中的图表

在SVGPageForm中也可以嵌套图表组件：

```java
@SVGPaperFormAnnotation(
    width = "100%",
    height = "600px",
    customService = {DashboardService.class}
)
public class DashboardView {
    // 仪表盘图表
    @FChartFieldAnnotation(
        chartType = FChartType.RealTimeLine,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "实时监控", index = 1)
    private FChartFieldAnnotation realTimeChart;
    
    // 数据概览
    @EChartFieldAnnotation(
        renderer = "canvas"
    )
    @CustomAnnotation(caption = "数据概览", index = 2)
    private EChartFieldAnnotation overviewChart;
}
```

### 6.3 其他容器中的图表

图表组件也可以嵌套在其他容器中：

```java
@FormAnnotation(
    col = 2,
    row = 5,
    customService = {ReportService.class}
)
public class ReportFormView {
    // 报表图表
    @FChartFieldAnnotation(
        chartType = FChartType.Column2D,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "月度报表", index = 1)
    private FChartFieldAnnotation monthlyReportChart;
}
```

## 7. 应用场景

### 7.1 数据统计展示

图表组件非常适合用于数据统计展示：

```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "数据统计面板",
    customService = {StatisticsService.class}
)
public class StatisticsPanelView {
    // 用户活跃度图表
    @EChartFieldAnnotation(
        renderer = "canvas"
    )
    @CustomAnnotation(caption = "用户活跃度", index = 1)
    private EChartFieldAnnotation userActivityChart;
    
    // 收入趋势图表
    @FChartFieldAnnotation(
        chartType = FChartType.Line,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "收入趋势", index = 2)
    private FChartFieldAnnotation revenueTrendChart;
}
```

### 7.2 业务分析报表

在业务分析报表中广泛使用图表组件：

```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "业务分析报表",
    customService = {BusinessReportService.class}
)
public class BusinessReportView {
    // 销售分析图表
    @EChartFieldAnnotation(
        renderer = "canvas"
    )
    @CustomAnnotation(caption = "销售分析", index = 1)
    private EChartFieldAnnotation salesAnalysisChart;
    
    // 区域分布图表
    @FChartFieldAnnotation(
        chartType = FChartType.Doughnut2D,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "区域分布", index = 2)
    private FChartFieldAnnotation regionDistributionChart;
}
```

### 7.3 实时数据监控

图表组件支持实时数据监控场景：

```java
@PanelFormAnnotation(
    dock = Dock.fill,
    caption = "实时监控面板",
    customService = {MonitoringService.class}
)
public class MonitoringView {
    // 系统性能监控
    @FChartFieldAnnotation(
        chartType = FChartType.RealTimeLine,
        renderer = "javascript"
    )
    @CustomAnnotation(caption = "系统性能", index = 1)
    private FChartFieldAnnotation systemPerformanceChart;
    
    // 网络流量监控
    @EChartFieldAnnotation(
        renderer = "canvas"
    )
    @CustomAnnotation(caption = "网络流量", index = 2)
    private EChartFieldAnnotation networkTrafficChart;
}
```

## 8. 最佳实践

### 8.1 设计原则

1. **数据驱动**：图表应基于真实数据，确保数据准确性和实时性
2. **用户体验**：选择合适的图表类型，确保信息传达清晰
3. **性能优化**：合理控制图表复杂度，避免影响页面性能
4. **响应式设计**：确保图表在不同设备上都能良好显示

### 8.2 实现规范

1. **注解使用**：
   - 根据需求选择合适的图表注解类型
   - 合理配置图表属性
   - 正确设置数据源URL

2. **服务设计**：
   - 图表服务类必须具有Web可访问性
   - 遵循平台服务设计规范
   - 提供完整的异常处理机制

3. **数据处理**：
   - 确保数据格式符合图表要求
   - 合理处理大数据量场景
   - 提供数据缓存机制

### 8.3 性能优化

1. **懒加载**：对于非首屏显示的图表，使用懒加载机制
2. **数据分页**：对于大数据量图表，实现数据分页处理
3. **缓存机制**：合理使用缓存减少重复数据请求
4. **销毁处理**：及时销毁不需要的图表实例释放资源

## 9. 注意事项

1. **兼容性**：
   - 确保图表库在目标浏览器中的兼容性
   - 测试不同设备上的显示效果
   - 考虑移动端的适配问题

2. **性能考虑**：
   - 避免在单个页面中创建过多图表
   - 合理设置图表刷新频率
   - 及时清理不需要的图表实例

3. **安全问题**：
   - 验证图表数据的来源和格式
   - 防止恶意数据注入
   - 控制外部资源的加载

4. **维护性**：
   - 保持代码结构清晰
   - 添加必要的注释说明
   - 遵循团队编码规范

5. **许可证**：
   - 注意FChart的商业许可证要求
   - 确保合规使用图表库

通过遵循以上规范和最佳实践，可以充分利用ooder平台的EChart与FChart图表组件，创建出高质量、高性能的数据可视化应用。