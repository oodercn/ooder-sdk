# ooder平台模块设计规范手册

## 目录

1. [概述](#1-概述)
2. [模块结构规范](#2-模块结构规范)
   - 2.1 包结构规范
   - 2.2 类结构规范
   - 2.3 文件命名规范
3. [模块入口规范](#3-模块入口规范)
   - 3.1 主入口设计
   - 3.2 子模块入口
   - 3.3 模块跳转
4. [模块配置规范](#4-模块配置规范)
   - 4.1 注解配置
   - 4.2 路由配置
   - 4.3 权限配置
5. [模块间通信规范](#5-模块间通信规范)
   - 5.1 数据传递
   - 5.2 事件通信
   - 5.3 服务调用
6. [模块生命周期规范](#6-模块生命周期规范)
   - 6.1 初始化
   - 6.2 运行时
   - 6.3 销毁
7. [模块测试规范](#7-模块测试规范)
   - 7.1 单元测试
   - 7.2 集成测试
   - 7.3 端到端测试
8. [最佳实践](#8-最佳实践)

## 1. 概述

ooder平台的模块设计规范旨在统一模块开发标准，确保模块的可维护性、可扩展性和一致性。模块是ooder平台业务功能的核心组织单元，通过标准化的设计实现业务功能的模块化管理。

## 2. 模块结构规范

### 2.1 包结构规范

模块应遵循统一的包结构组织：

```
net.ooder.{业务域}
├── view                    // 视图类
├── service                 // 服务类
├── controller              // 控制器类
├── enums                   // 枚举类
├── menu                    // 菜单类
├── example                 // 示例类
├── doc                     // 文档类
└── {子模块}                // 子模块（如需要）
```

**示例：**
```
net.ooder.attendance
├── view
│   ├── AttendanceCheckInView.java
│   ├── AttendanceQueryListView.java
│   └── AttendancePortalView.java
├── service
│   ├── AttendanceCheckInService.java
│   ├── AttendanceQueryService.java
│   └── AttendancePortalService.java
├── controller
│   └── AttendanceAggregateController.java
├── enums
│   ├── AttendanceType.java
│   └── LeaveType.java
└── example
    └── AttendanceAggregateModuleExample.java
```

### 2.2 类结构规范

1. **模块主类**：
```java
/**
 * 模块描述
 */
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@MCPServerAnnotation(...)
public class ModuleName {
    // 模块实现
}
```

2. **视图类**：
```java
/**
 * 视图类描述
 */
@ViewAnnotation(...)
public class ViewClassName {
    // 视图实现
}
```

3. **服务类**：
```java
/**
 * 服务类描述
 */
@ServiceAnnotation(...)
@RestController
@RequestMapping("/...")
@Service
public class ServiceClassName {
    // 服务实现
}
```

### 2.3 文件命名规范

1. **视图文件**：以View结尾，如`AttendanceCheckInView.java`
2. **服务文件**：以Service结尾，如`AttendanceCheckInService.java`
3. **控制器文件**：以Controller结尾，如`AttendanceAggregateController.java`
4. **枚举文件**：以Type结尾，如`AttendanceType.java`
5. **示例文件**：以Example结尾，如`AttendanceAggregateModuleExample.java`

## 3. 模块入口规范

### 3.1 主入口设计

模块主入口应提供完整的功能访问点：

```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@MCPServerAnnotation(
    serviceId = "attendance-module",
    interfaceName = "AttendanceModuleService",
    protocol = ProtocolType.HTTP,
    host = "0.0.0.0",
    port = 8080
)
public class MobileAttendanceModule {
    
    /**
     * 模块主入口
     * 返回模块的主视图
     */
    @MethodChinaName(cname = "考勤模块主入口")
    @MCPMethodAnnotation(
        path = "/attendance/main",
        method = HttpMethod.GET
    )
    @APIEventAnnotation(
        autoRun = true,
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME}
    )
    @FormViewAnnotation
    @CustomAnnotation(imageClass = "fa-solid fa-clock", index = 1, caption = "考勤管理")
    public ResultModel<AttendanceCheckInView> attendanceModule() {
        ResultModel<AttendanceCheckInView> resultModel = new ResultModel<>();
        
        try {
            AttendanceCheckInView mainView = new AttendanceCheckInView();
            resultModel.setData(mainView);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorResultModel封装的错误信息
            ErrorResultModel<AttendanceCheckInView> errorResult = new ErrorResultModel<>();
            errorResult.setErrdes(e.getMessage());
            errorResult.setErrcode(1000); // 设置默认错误码
            errorResult.setRequestStatus(-1); // 设置错误状态
            return errorResult;
        }
        
        return resultModel;
    }
}
```

### 3.2 子模块入口

为每个子功能提供独立的入口点：

```java
/**
 * 考勤签到视图入口
 */
@MethodChinaName(cname = "考勤签到视图")
@MCPMethodAnnotation(
    path = "/attendance/checkin",
    method = HttpMethod.GET
)
@APIEventAnnotation(
    autoRun = true,
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME}
)
@FormViewAnnotation
@CustomAnnotation(imageClass = "fa-solid fa-sign-in-alt", index = 2, caption = "考勤签到")
public ResultModel<AttendanceCheckInView> attendanceCheckInView() {
    ResultModel<AttendanceCheckInView> resultModel = new ResultModel<>();
    
    try {
        AttendanceCheckInView view = new AttendanceCheckInView();
        resultModel.setData(view);
        resultModel.setRequestStatus(1);
    } catch (Exception e) {
        // 发生错误时返回ErrorResultModel封装的错误信息
        ErrorResultModel<AttendanceCheckInView> errorResult = new ErrorResultModel<>();
        errorResult.setErrdes(e.getMessage());
        errorResult.setErrcode(1000); // 设置默认错误码
        errorResult.setRequestStatus(-1); // 设置错误状态
        return errorResult;
    }
    
    return resultModel;
}
```

### 3.3 模块跳转

实现模块间的平滑跳转：

```java
/**
 * 考勤门户视图入口
 */
@MethodChinaName(cname = "考勤门户视图")
@MCPMethodAnnotation(
    path = "/attendance/portal",
    method = HttpMethod.GET
)
@APIEventAnnotation(
    autoRun = true,
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME}
)
@FormViewAnnotation
@CustomAnnotation(imageClass = "fa-solid fa-home", index = 7, caption = "考勤门户")
public ResultModel<AttendancePortalView> attendancePortalView() {
    ResultModel<AttendancePortalView> resultModel = new ResultModel<>();
    
    try {
        AttendancePortalView view = new AttendancePortalView();
        resultModel.setData(view);
        resultModel.setRequestStatus(1);
    } catch (Exception e) {
        // 发生错误时返回ErrorResultModel封装的错误信息
        ErrorResultModel<AttendancePortalView> errorResult = new ErrorResultModel<>();
        errorResult.setErrdes(e.getMessage());
        errorResult.setErrcode(1000); // 设置默认错误码
        errorResult.setRequestStatus(-1); // 设置错误状态
        return errorResult;
    }
    
    return resultModel;
}
```

## 4. 模块配置规范

### 4.1 注解配置

1. **模块级注解**：
```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@MCPServerAnnotation(
    serviceId = "module-id",
    interfaceName = "ModuleInterface",
    protocol = ProtocolType.HTTP,
    host = "0.0.0.0",
    port = 8080
)
```

2. **方法级注解**：
```java
@MethodChinaName(cname = "方法中文名")
@MCPMethodAnnotation(
    path = "/method/path",
    method = HttpMethod.GET
)
@APIEventAnnotation(...)
```

### 4.2 路由配置

1. **统一前缀**：
```java
@RequestMapping("/attendance")
```

2. **功能子路由**：
```java
@RequestMapping("/checkin")
@RequestMapping("/query")
@RequestMapping("/statistics")
```

### 4.3 权限配置

```java
@PreAuthorize("hasRole('ATTENDANCE_USER')")
public ResultModel<AttendanceCheckInView> attendanceCheckInView() {
    // 方法实现
}
```

## 5. 模块间通信规范

### 5.1 数据传递

1. **简单参数传递**：
```java
public ResultModel<Boolean> processData(String param1, int param2) {
    // 处理逻辑
}
```

2. **复杂对象传递**：
```java
public ResultModel<Boolean> processView(@RequestBody ViewClassName view) {
    // 处理逻辑
}
```

### 5.2 事件通信

```java
@EventListener
public void handleModuleEvent(ModuleEvent event) {
    // 事件处理逻辑
}
```

### 5.3 服务调用

```java
@Autowired
private OtherModuleService otherService;

public void callOtherModule() {
    ResultModel<?> result = otherService.someMethod();
    // 处理结果
}
```

## 6. 模块生命周期规范

### 6.1 初始化

```java
@PostConstruct
public void initModule() {
    // 模块初始化逻辑
    // 1. 加载配置信息
    // 2. 初始化数据库连接
    // 3. 注册事件监听器
    // 4. 启动定时任务
}
```

### 6.2 运行时

```java
// 运行时业务逻辑处理
public ResultModel<?> handleRequest(RequestType request) {
    // 业务逻辑实现
}
```

### 6.3 销毁

```java
@PreDestroy
public void destroyModule() {
    // 模块销毁逻辑
    // 1. 关闭数据库连接
    // 2. 清理缓存
    // 3. 注销事件监听器
    // 4. 保存状态信息
}
```

## 7. 模块测试规范

### 7.1 单元测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class AttendanceCheckInServiceTest {
    
    @Autowired
    private AttendanceCheckInService service;
    
    @Test
    public void testHandleCheckIn() {
        // 测试逻辑
        AttendanceCheckInView view = new AttendanceCheckInView();
        ResultModel<Boolean> result = service.handleCheckIn(view);
        assertTrue(result.getData());
    }
}
```

### 7.2 集成测试

```java
@Test
public void testModuleIntegration() {
    // 集成测试逻辑
    // 测试模块间的数据流转
    // 验证服务调用结果
}
```

### 7.3 端到端测试

```java
@Test
public void testEndToEnd() {
    // 端到端测试逻辑
    // 模拟用户操作流程
    // 验证完整业务链路
}
```

## 8. 最佳实践

### 8.1 设计原则

1. **高内聚低耦合**：模块内部功能紧密相关，模块间依赖最小化
2. **单一职责**：每个模块只负责一个业务领域
3. **可复用性**：设计通用组件，提高代码复用率
4. **可测试性**：模块设计应便于测试

### 8.2 实现规范

1. **注解使用**：正确使用模块相关注解
2. **异常处理**：完整处理各种异常情况
3. **日志记录**：记录必要的操作日志
4. **性能优化**：合理使用缓存和连接池

### 8.3 部署规范

1. **版本管理**：使用语义化版本号
2. **配置管理**：外部化配置参数
3. **监控告警**：实现必要的监控指标
4. **文档完善**：提供完整的使用文档

通过遵循以上模块设计规范，可以确保ooder平台模块的质量、性能和可维护性，为业务系统提供稳定可靠的模块化支撑。