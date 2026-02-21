# Ooder框架钩子实现问题解答2：不同类型的`@*ViewAnnotation`注解具体有哪些配置参数

## 1. 问题分析

在Ooder框架中，`@*ViewAnnotation`系列注解用于标识方法返回的视图类型，并配置视图的各种属性。了解这些注解的具体配置参数对于正确使用Ooder框架进行视图开发至关重要。

## 2. 主要`@*ViewAnnotation`注解类型

Ooder框架提供了多种视图注解，用于标识不同类型的视图组件。以下是主要的视图注解类型及其配置参数：

### 2.1 @BlockViewAnnotation - 块视图注解

**作用**：用于标识方法返回块视图组件

**使用场景**：适用于需要在页面中显示独立块区域的场景，如配置面板、信息展示块等

**代码示例**：

```java
@RequestMapping(value = "BasicInfo", method = RequestMethod.POST)
@GroupItemAnnotation(dock = Dock.left, width = "250", caption = "基础信息", lazyLoad = true)
@BlockViewAnnotation  // 块视图注解
@CustomAnnotation(index = 0)
@ResponseBody
public ResultModel<BasicInfo> getBasicInfo() {
    // 方法实现
}
```

**配置参数**：
- 该注解通常不直接配置参数，主要用于标识视图类型
- 视图的具体配置通过其他注解（如@GroupItemAnnotation）实现

### 2.2 @TreeGridViewAnnotation - 网格视图注解

**作用**：用于标识方法返回网格（表格）视图组件

**使用场景**：适用于需要以表格形式展示数据的场景

**代码示例**：

```java
@RequestMapping(method = RequestMethod.POST, value = "AllJavaTemp")
@TreeGridViewAnnotation()  // 网格视图注解
@ModuleAnnotation(caption = "所有JAVA模板")
@APIEventAnnotation(autoRun = true)
@ResponseBody
public ListResultModel<List<WebSiteCodeTempTreeGrid>> getAllJavaTemps(String dsmTempId) {
    // 方法实现
}
```

**配置参数**：
- 该注解通常不直接配置参数，主要用于标识视图类型
- 表格的具体配置通过返回的数据模型和其他注解实现

### 2.3 @FormViewAnnotation - 表单视图注解

**作用**：用于标识方法返回表单视图组件

**使用场景**：适用于需要展示和编辑表单数据的场景

**代码示例**：

```java
@RequestMapping(method = RequestMethod.POST, value = "TempFileInfo")
@FormViewAnnotation  // 表单视图注解
@DialogAnnotation(width = "850", height = "750")
@ModuleAnnotation(caption = "模板信息")
@APIEventAnnotation(autoRun = true, bindMenu = CustomMenuItem.EDITOR)
@ResponseBody
public ResultModel<JavaTempForm> getJavaTempInfo(String javaTempId) {
    // 方法实现
}
```

**配置参数**：
- 该注解通常不直接配置参数，主要用于标识视图类型
- 表单的具体配置通过返回的数据模型和其他注解（如@DialogAnnotation）实现

### 2.4 @PopTreeViewAnnotation - 弹出树视图注解

**作用**：用于标识方法返回弹出树视图组件

**使用场景**：适用于需要以弹出树形式选择数据的场景，如下拉树选择器

**代码示例**：

```java
@RequestMapping(value = {"AddDSMTemp"}, method = {RequestMethod.GET, RequestMethod.POST})
@PopTreeViewAnnotation()  // 弹出树视图注解
@ModuleAnnotation(caption = "添加JAVA模板")
@APIEventAnnotation(isAllform = true, autoRun = true, bindMenu = {CustomMenuItem.ADD})
@DialogAnnotation(height = "450", width = "300")
@ResponseBody
public TreeListResultModel<List<WebSitePopTree>> getAddDSMTemp(String dsmTempId, DSMType dsmType, String domainId, String currentClassName, String xpath) {
    // 方法实现
}
```

**配置参数**：
- 该注解通常不直接配置参数，主要用于标识视图类型
- 弹出窗口的具体配置通过@DialogAnnotation实现

### 2.5 @NavTreeViewAnnotation - 导航树视图注解

**作用**：用于标识方法返回导航树视图组件

**使用场景**：适用于需要以树形结构展示导航菜单或层级数据的场景

**代码示例**：

```java
@RequestMapping(method = RequestMethod.POST, value = "RePackage")
@APIEventAnnotation(autoRun = true)
@NavTreeViewAnnotation  // 导航树视图注解
@DialogAnnotation(caption = "重新打包", width = "900", height = "680")
@ModuleAnnotation(imageClass = "ri-box-line")
@ResponseBody
public TreeListResultModel<List<ViewConfigTree>> rePackage(String currentClassName, String currCom, String projectName) {
    // 方法实现
}
```

**配置参数**：
- 该注解通常不直接配置参数，主要用于标识视图类型
- 树的具体配置通过返回的数据模型和其他注解实现

## 3. 辅助视图注解

除了上述主要的视图注解外，Ooder框架还提供了一系列辅助注解，用于配置视图的各种属性：

### 3.1 @ModuleAnnotation - 模块配置注解

**作用**：配置视图模块的基本信息

**配置参数**：
- `caption`：模块标题
- `imageClass`：模块图标
- `moduleViewType`：模块视图类型（如GRIDCONFIG、TREECONFIG、FORMCONFIG等）
- `iconColor`：图标颜色

**代码示例**：

```java
@ModuleAnnotation(
        caption = "数据列表",
        imageClass = "ri-table-line",
        moduleViewType = ModuleViewType.GRIDCONFIG
)
```

### 3.2 @GroupItemAnnotation - 布局配置注解

**作用**：配置视图布局信息

**配置参数**：
- `dock`：布局位置（left、right、top、bottom、fill等）
- `width`：视图宽度
- `height`：视图高度
- `caption`：视图标题
- `lazyLoad`：是否懒加载

**代码示例**：

```java
@GroupItemAnnotation(
        dock = Dock.left,
        width = "250",
        caption = "基础信息",
        lazyLoad = true
)
```

### 3.3 @DialogAnnotation - 弹窗配置注解

**作用**：配置子视图弹窗属性

**配置参数**：
- `width`：弹窗宽度
- `height`：弹窗高度
- `caption`：弹窗标题

**代码示例**：

```java
@DialogAnnotation(
        caption = "重新打包",
        width = "900",
        height = "680"
)
```

### 3.4 @APIEventAnnotation - 事件绑定注解

**作用**：绑定视图事件与操作

**配置参数**：
- `autoRun`：是否自动执行
- `callback`：回调函数数组（如RELOAD、RELOADPARENT、CLOSE等）
- `bindMenu`：绑定的菜单
- `bindTreeEvent`：绑定的树事件
- `isAllform`：是否包含所有表单数据

**代码示例**：

```java
@APIEventAnnotation(
        autoRun = true,
        callback = {CustomCallBack.RELOADPARENT, CustomCallBack.CLOSE},
        bindTreeEvent = CustomTreeEvent.TREESAVE,
        bindMenu = CustomMenuItem.SAVE
)
```

### 3.5 @CustomAnnotation - 自定义配置注解

**作用**：配置字段或视图的自定义属性

**配置参数**：
- `hidden`：是否隐藏
- `pid`：是否为主键
- `uid`：是否为唯一标识
- `caption`：字段标题
- `index`：排序索引
- `colSpan`：列跨度

**代码示例**：

```java
@CustomAnnotation(
        hidden = true,
        pid = true
)
private String domainId;
```

## 4. 注解使用最佳实践

1. **注解组合使用**：不同类型的注解可以组合使用，实现更完整的视图配置

   ```java
   @RequestMapping(value = "BasicInfo", method = RequestMethod.POST)
   @GroupItemAnnotation(dock = Dock.left, width = "250", caption = "基础信息", lazyLoad = true)
   @BlockViewAnnotation
   @CustomAnnotation(index = 0)
   @ResponseBody
   ```

2. **注解顺序**：按照Ooder框架的最佳实践，保持注解的合理顺序：
   ```
   @RequestMapping → @GroupItemAnnotation → @BlockViewAnnotation → @CustomAnnotation → @ResponseBody
   ```

3. **参数配置**：根据业务需求合理配置注解参数，避免不必要的配置

4. **统一命名规范**：保持注解参数值的命名规范，如图标名称使用统一的图标库

## 5. 总结

Ooder框架提供了丰富的`@*ViewAnnotation`系列注解，用于标识视图类型和配置视图属性。主要包括：

| 注解类型 | 作用 | 主要配置参数 |
|---------|------|--------------|
| `@BlockViewAnnotation` | 标识块视图 | - |
| `@TreeGridViewAnnotation` | 标识网格视图 | - |
| `@FormViewAnnotation` | 标识表单视图 | - |
| `@PopTreeViewAnnotation` | 标识弹出树视图 | - |
| `@NavTreeViewAnnotation` | 标识导航树视图 | - |
| `@ModuleAnnotation` | 配置模块信息 | caption, imageClass, moduleViewType |
| `@GroupItemAnnotation` | 配置布局 | dock, width, height, caption, lazyLoad |
| `@DialogAnnotation` | 配置弹窗 | width, height, caption |
| `@APIEventAnnotation` | 绑定事件 | autoRun, callback, bindMenu, bindTreeEvent |
| `@CustomAnnotation` | 自定义配置 | hidden, pid, uid, caption, index |

这些注解的组合使用，使得开发者可以灵活配置各种类型的视图组件，实现复杂的页面布局和交互效果。

通过合理使用这些注解及其配置参数，开发者可以高效地构建符合业务需求的视图组件，提高开发效率和代码质量。