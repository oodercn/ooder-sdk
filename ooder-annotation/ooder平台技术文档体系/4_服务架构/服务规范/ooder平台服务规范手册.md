# ooder平台服务规范手册

## 目录

1. [概述](#1-概述)
2. [通用服务注解规范](#2-通用服务注解规范)
   - 2.1 Web可访问性规范
   - 2.2 服务类型定义
   - 2.3 注解使用规范
3. [服务参数设计规范](#3-服务参数设计规范)
   - 3.1 参数设计原则
   - 3.2 简单参数使用场景
   - 3.3 复杂视图对象参数使用场景
4. [BAR服务规范](#4-bar服务规范)
   - 4.1 BAR服务分类
   - 4.2 BAR服务规范
   - 4.3 BAR组件共性规范
   - 4.4 BAR组件差异性规范
5. [模块设计规范](#5-模块设计规范)
   - 5.1 模块入口规范
   - 5.2 模块跳转规范
6. [返回数据规范](#6-返回数据规范)
   - 6.1 标准返回类型
   - 6.2 数据序列化规范
7. [方法规范](#7-方法规范)
   - 7.1 方法注解规范
   - 7.2 异常处理规范
8. [通讯组件规范](#8-通讯组件规范)
   - 8.1 APIEvent规范
   - 8.2 MQTT规范
9. [最佳实践](#9-最佳实践)
   - 9.1 设计经验
   - 9.2 实现经验
   - 9.3 注意事项

## 1. 概述

ooder平台的服务规范定义了服务类的设计原则、注解使用、参数设计、返回数据格式等方面的标准化要求。所有服务都必须遵循统一的规范，以确保平台的一致性和可维护性。

服务规范涵盖了通用服务注解、参数设计、BAR服务、模块设计、返回数据、方法规范、通讯组件等多个方面。

## 2. 通用服务注解规范

### 2.1 Web可访问性规范

所有通用服务必须采用Spring MVC注解：

```java
@RestController
@RequestMapping("/service/path")
@Service
public class MyService {
    // 服务实现
}
```

必须使用@Aggregation注解声明服务类型：

```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance")
@Service
public class AttendanceService {
    // 服务实现
}
```

### 2.2 服务类型定义

1. **BAR服务**：处理工具栏和底部栏操作，声明为AggregationType.BAR
2. **通用视图服务**：处理视图业务逻辑，声明为AggregationType.VIEW
3. **服务入口服务**：模块主入口点，应作为普通Web服务实现，仅在特殊场景下使用MCP注解
4. **导航服务**：处理视图间跳转，声明为AggregationType.NAVIGATION
5. **模块跳转服务**：应使用@ModuleAnnotation进行配置，支持指定模块标题、图标、动态加载、视图类型、面板类型及绑定服务类，并通常与@APIEventAnnotation配合使用
6. **模块引用服务**：处理模块间引用，声明为AggregationType.MODULE

### 2.3 注解使用规范

- **@APIEventAnnotation**：定义事件处理行为
- **@CustomAnnotation**：定义服务显示信息（如标题、图标、索引等）
- **@ModuleAnnotation**：用于模块跳转配置
- **@Aggregation**：必须用于声明服务类型和用户空间

## 3. 服务参数设计规范

### 3.1 参数设计原则

1. **最小化原则**：只传递必要的参数
2. **一致性原则**：相同类型的业务操作应保持参数设计的一致性
3. **可读性原则**：参数命名应清晰表达其用途
4. **性能原则**：简单参数传输效率更高

### 3.2 简单参数使用场景

1. **重置操作**：通常只需要简单的标识参数
```java
@PostMapping("/reset")
@ResponseBody
public ResultModel<Boolean> resetQueryConditions(@RequestParam(required = false) String resetFlag) {
    // 实现重置逻辑
}
```

2. **刷新操作**：通常只需要少量筛选参数
```java
@GetMapping("/refresh")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> refreshAttendanceRecords(
        @RequestParam(required = false) String employeeId, 
        @RequestParam(required = false) String startDate) {
    // 根据简单参数刷新数据
}
```

3. **根据主键查询**：只需要传递主键参数
```java
@GetMapping("/employee/{employeeId}")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> getAttendanceRecordsByEmployeeId(@PathVariable String employeeId) {
    // 根据员工ID查询数据
}
```

4. **状态更新操作**：只需要ID和状态参数
```java
@PutMapping("/status")
@ResponseBody
public ResultModel<Boolean> updateAttendanceStatus(
        @RequestParam String recordId, 
        @RequestParam String status) {
    // 更新考勤记录状态
}
```

### 3.3 复杂视图对象参数使用场景

1. **表单提交操作**：需要完整的视图数据
```java
@PostMapping("/submit")
@ResponseBody
public ResultModel<Boolean> submitAttendanceData(@RequestBody AttendanceCheckInView view) {
    // 处理完整的表单数据
}
```

2. **复杂查询操作**：需要多个查询条件时，使用视图对象
```java
@PostMapping("/search")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> queryAttendanceRecords(@RequestBody AttendanceQueryListView view) {
    // 根据视图中的多个查询条件进行复杂查询
}
```

3. **批量操作**：需要处理多个数据项时，使用视图对象
```java
@PostMapping("/batchUpdate")
@ResponseBody
public ResultModel<Boolean> batchUpdateAttendanceRecords(@RequestBody AttendanceBatchUpdateView view) {
    // 批量更新考勤记录
}
```

4. **需要全部字段的场景**：只有在明确需要全部字段数据时才使用view参数
```java
@PostMapping("/detailedProcess")
@ResponseBody
public ResultModel<Boolean> detailedProcess(@RequestBody AttendanceQueryListView view) {
    // 需要访问视图中的所有字段数据
}
```

## 4. BAR服务规范

### 4.1 BAR服务分类

1. **具有Web访问能力的BAR服务**：提供具体的业务逻辑方法并暴露为Web服务
2. **仅提供菜单配置的BAR服务**：仅提供菜单配置方法

### 4.2 BAR服务规范

#### 注解要求：
- 必须使用@Aggregation注解声明服务类型为AggregationType.BAR
- 必须指定userSpace为UserSpace.SYS

#### 功能规范：
- 可实现具体业务逻辑并暴露为Web服务
- 也可仅用于提供菜单/工具栏配置
- 两类服务可根据实际场景共存，但应保持职责清晰

#### 命名规范：
- 类名应以BarService结尾
- 方法名应清晰表达其功能

### 4.3 BAR组件共性规范

在ooder平台中，BAR组件包括Tabs中的bar与独立的bar（menubar、toolbar、bottombar、statusbar）等，它们具有以下共性：

1. **统一的注解体系**：
   - 所有BAR组件均使用统一的@Aggregation(type = AggregationType.BAR)注解声明
   - 均遵循相同的Web可访问性规范，采用Spring MVC注解体系
   - 均可使用@CustomAnnotation定义显示信息（标题、图标、索引等）

2. **一致的行为配置**：
   - 均支持@APIEventAnnotation定义事件交互行为
   - 均支持事件绑定、参数配置、回调处理等核心功能
   - 均可与视图对象进行数据交互

3. **统一的服务规范**：
   - 均需声明userSpace为UserSpace.SYS
   - 均需遵循ooder服务规范，确保Web可访问性
   - 均使用统一的ResultModel或ListResultModel返回类型

4. **多视图嵌套构建原则在BAR规范中的应用**：
   - BAR组件作为视图的一部分，遵循多视图状态基础规则
   - 在复杂视图构建中，BAR组件作为独立视图优先构建
   - BAR组件可通过bindClass属性与服务解耦，支持递归引用
   - 在Tabs组件中，BAR组件可作为TabsItem通过枚举方式实现，使用TabItem接口定义不同外观和数据特性的分支

### 4.4 BAR组件差异性规范

1. **Tabs中的bar**：
   - 作为Tabs组件的一部分，与Tabs内容区域紧密关联
   - 通过barLocation属性控制显示位置
   - 支持与Tabs内容区域的联动交互

2. **独立的bar组件**：
   - **MenuBar**：通常位于页面顶部，提供全局导航功能
   - **ToolBar**：通常位于页面顶部或视图区域上方，提供工具操作功能
   - **BottomBar**：通常位于页面底部，提供辅助操作功能
   - **StatusBar**：通常位于页面底部，提供状态信息展示功能

## 5. 模块设计规范

### 5.1 模块入口规范

1. **入口设计**：
   - ood规范中不存在模块定义入口
   - 主入口是普通的Web服务
   - 在方法服务中声明视图类型使用

2. **MCP注解使用**：
   - 服务入口使用MCP注解
   - @MCPServerAnnotation定义服务基本信息
   - @MCPMethodAnnotation定义方法路径和类型

### 5.2 模块跳转规范

1. **使用@ModuleAnnotation**处理模块间跳转
2. **配置模块视图类型、面板类型和绑定服务类**
3. **支持动态加载(dynLoad)模块**

### 5.3 模块引用规范

1. **使用@ModuleRefFieldAnnotation**实现模块间引用
2. **通过src属性指定被引用模块的路径**
3. **通过bindClass属性绑定被引用模块的服务类**
4. **支持模块的嵌入和组合**

## 6. 返回数据规范

### 6.1 标准返回类型

- 统一使用ResultModel或ListResultModel标准返回类型
- ResultModel用于单个对象返回
- ListResultModel用于集合对象返回

### 6.2 数据序列化规范

- 遵循fastjson序列化规范
- 使用@JSONField注解控制序列化格式
- 在特定视图操作环境下，应优先使用视图对象作为返回值的泛型类型，确保数据传输的一致性和完整性

## 7. 方法规范

### 7.1 方法注解规范

- 使用@PostMapping/@GetMapping等Spring Web注解
- 方法命名清晰表达功能语义
- 包含完整的异常处理逻辑，设置正确的HTTP状态码

### 7.2 异常处理规范

- 统一异常处理机制
- 合理设置HTTP状态码
- 提供友好的错误信息

## 8. 通讯组件规范

### 8.1 APIEvent规范

APIEvent是ooder平台中最常用的通讯组件，用于定义前后端交互的事件处理行为。

#### 核心属性：
- queryAsync：异步查询
- autoRun：自动运行
- bindAction：绑定操作动作
- customRequestData：自定义请求数据
- customResponseData：自定义响应数据
- beforeInvoke：调用前处理
- onExecuteSuccess：执行成功处理
- onError：错误处理

### 8.2 MQTT规范

MQTT是ooder平台支持的轻量级消息传输协议，适用于物联网设备通信和实时消息推送场景。

#### 核心属性：
- server：服务器地址
- port：端口号
- clientId：客户端ID
- userName：用户名
- password：密码
- autoConn：自动连接
- autoSub：自动订阅

## 9. 最佳实践

### 9.1 设计经验

1. 视图与服务分离，保持职责清晰
2. 合理使用枚举增强数据表达能力
3. 统一异常处理机制
4. 规范化注解使用

### 9.2 实现经验

1. 所有通用服务必须具备Web可访问性
2. 正确使用Spring MVC注解
3. 遵循fastjson序列化规范
4. 合理处理遮罩和事件链条

### 9.3 注意事项

1. 避免在视图中混合使用不同类型的注解
2. 确保列表视图的单一性
3. 正确使用@Uid注解标识行主键
4. 合理配置模块跳转参数
5. 合理使用@ToolBarMenu、@BottomBarMenu、@MenuBarMenu等高级bar组件
6. 所有bar的绑定逻辑应在独立的服务类中实现
7. 避免在同一视图中重复添加相同类型的bar组件
8. 文本信息通过caption等方法动态设置
9. 每个页面仅在必要位置显示一个主要操作栏
10. 防止按钮重复和界面混乱，保持用户体验一致性