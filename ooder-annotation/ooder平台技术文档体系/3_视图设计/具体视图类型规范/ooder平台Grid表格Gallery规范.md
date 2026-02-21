# ooder平台TreeGrid表格和Gallery规范

## 1. 概述

TreeGrid表格和Gallery是ooder平台中用于数据展示的重要视图类型。TreeGrid表格适用于结构化数据的展示和操作，而Gallery则适用于图片或卡片形式的内容展示。

## 2. TreeGrid表格规范

### 2.1 TreeGrid注解类型

#### @TreeGridAnnotation
@TreeGridAnnotation用于定义标准的网格列表视图：

```java
@TreeGridAnnotation(
    customService = {AttendanceRecordListService.class},
    showHeader = true,
    colSortable = true,
    altRowsBg = true,
    customMenu = {TreeGridMenu.RELOAD, TreeGridMenu.SEARCH},
    bottombarMenu = {TreeGridMenu.ADD, TreeGridMenu.DELETE}
)
public class AttendanceRecordTreeGridView {
    // 字段定义
}
```

**核心属性：**
- customService：关联的自定义服务类
- showHeader：是否显示表头
- colSortable：列是否可排序
- altRowsBg：是否交替行背景色
- customMenu：自定义菜单项
- bottombarMenu：底部栏菜单项

#### @TreeGridViewAnnotation
@TreeGridViewAnnotation用于定义网格视图，通常用于模块入口。

#### @MTreeGridAnnotation
@MTreeGridAnnotation用于定义移动端的网格列表视图。

### 2.2 TreeGrid字段注解

TreeGrid视图中的字段通常使用标准的输入注解：

```java
@InputAnnotation
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;

@DatePickerAnnotation
@CustomAnnotation(caption = "考勤日期", index = 2)
private String attendanceDate;

@ComboBoxAnnotation
@CustomAnnotation(caption = "考勤状态", index = 3)
private String attendanceStatus;
```

### 2.3 TreeGrid菜单和事件

#### TreeGridMenu预定义菜单项
```java
customMenu = {TreeGridMenu.RELOAD, TreeGridMenu.SEARCH}
bottombarMenu = {TreeGridMenu.ADD, TreeGridMenu.DELETE}
```

#### CustomTreeGridEvent预定义事件
```java
@CustomTreeGridEvent(event = TreeGridEventEnum.onDblclickRow, action = "editRecord")
```

### 2.4 TreeGrid数据结构

#### ListResultModel
用于普通列表数据：
```java
public class ListResultModel<T extends Collection> extends ResultModel<T> {
    public Integer size = -1;
    
    public int getSize() {
        // 返回列表大小
    }
}
```

#### TreeListResultModel
用于树形列表数据：
```java
public class TreeListResultModel<T extends Collection> extends ListResultModel<T> {
    public List<String> ids;
    public String euClassName;
}
```

## 3. Gallery规范

### 3.1 Gallery注解类型

#### @GalleryAnnotation
@GalleryAnnotation用于定义画廊视图：

```java
@GalleryAnnotation(
    customService = {ImageGalleryService.class},
    showHeader = true,
    colResizable = true,
    customMenu = {TreeGridMenu.RELOAD, TreeGridMenu.SEARCH}
)
public class ImageGalleryView {
    // 字段定义
}
```

**核心属性：**
- customService：关联的自定义服务类
- showHeader：是否显示表头
- colResizable：列是否可调整大小
- customMenu：自定义菜单项

### 3.2 Gallery字段注解

Gallery视图中的字段通常使用图片相关的注解：

```java
@InputAnnotation
@CustomAnnotation(caption = "图片标题", index = 1)
private String imageTitle;

@InputAnnotation
@CustomAnnotation(caption = "图片路径", index = 2)
private String imagePath;

@InputAnnotation
@CustomAnnotation(caption = "描述", index = 3)
private String description;
```

### 3.3 Gallery布局规范

#### 布局属性
- **列数控制**：通过CSS控制每行显示的图片数量
- **响应式设计**：根据屏幕尺寸自动调整布局
- **缩略图处理**：合理设置缩略图大小和质量

#### 样式配置
- 使用@CustomAnnotation定义字段标题、图标等外观属性
- 合理设置边框和间距
- 考虑图片加载和显示效果

## 4. 数据处理规范

### 4.1 数据验证
- 在服务端进行数据验证
- 合理设置字段的必填性和长度限制
- 提供友好的错误提示信息

### 4.2 数据传输
- 使用@RequestBody注解处理数据提交
- 确保前后端数据结构一致性
- 合理设计数据传输对象

### 4.3 数据分页
- 实现分页查询功能
- 合理设置每页显示记录数
- 提供页码导航功能

## 5. TreeGrid和Gallery服务规范

### 5.1 服务注解
TreeGrid和Gallery相关服务必须使用以下注解：
- @Aggregation：声明服务类型
- @RestController：声明REST控制器
- @Service：声明服务组件
- @RequestMapping：声明请求映射

### 5.2 服务方法
- 使用@PostMapping/@GetMapping等Spring Web注解
- 方法命名清晰表达功能语义
- 包含完整的异常处理逻辑

### 5.3 返回数据
- 统一使用ListResultModel或TreeListResultModel标准返回类型
- 遵循fastjson序列化规范
- 在特定视图操作环境下，应优先使用视图对象作为返回值的泛型类型

## 6. BAR组件规范

### 6.1 工具栏配置
使用@ToolBarMenu配置TreeGrid/Gallery工具栏：

```java
@ToolBarMenu(
    barDock = Dock.top,
    serviceClass = TreeGridToolBarService.class
)
```

### 6.2 底部栏配置
使用@BottomBarMenu配置TreeGrid/Gallery底部栏：

```java
@BottomBarMenu(
    barDock = Dock.bottom,
    serviceClass = TreeGridBarService.class
)
```

### 6.3 BAR服务实现
BAR服务类必须遵循以下规范：
- 使用@Aggregation(type = AggregationType.MENU)注解
- 实现具体的业务逻辑方法
- 使用@APIEventAnnotation和@CustomAnnotation注解

## 7. 最佳实践

### 7.1 TreeGrid设计原则
1. **数据清晰**：表格数据结构清晰，易于理解
2. **操作便捷**：提供常用的操作功能，如排序、筛选、分页
3. **性能优化**：合理使用分页和懒加载机制
4. **响应式设计**：适配不同屏幕尺寸

### 7.2 Gallery设计原则
1. **视觉美观**：图片展示效果良好，布局合理
2. **加载优化**：使用缩略图和懒加载提高性能
3. **交互友好**：提供图片预览和操作功能
4. **响应式设计**：适配不同屏幕尺寸

### 7.3 实现规范
1. **注解使用**：正确使用TreeGrid/Gallery相关注解
2. **字段设计**：合理设计字段类型和显示方式
3. **服务分离**：视图与服务分离，职责清晰
4. **异常处理**：完善的异常处理机制

### 7.4 注意事项
1. 避免在TreeGrid/Gallery中混合使用不兼容的注解
2. 合理配置TreeGrid/Gallery布局属性
3. 正确使用@Uid注解标识行主键
4. 所有BAR服务逻辑应在独立的服务类中实现
5. 确保列表视图的单一性，不得添加表单视图注解