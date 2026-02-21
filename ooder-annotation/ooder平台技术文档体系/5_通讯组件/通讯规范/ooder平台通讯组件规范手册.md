# 5.1 通讯组件规范手册

## 1. 概述

ooder平台提供了多种通讯组件，用于实现前后端数据交互、消息传递和远程通信。本规范手册详细描述了平台支持的各种通讯组件的使用规范、配置参数和最佳实践。

## 2. APIEvent通讯组件

### 2.1 概述
APIEvent是ooder平台中最常用的通讯组件，用于定义前后端交互的事件处理行为。通过@APIEventAnnotation注解，可以将服务方法与前端组件事件进行绑定。

### 2.2 核心注解
``java
@APIEventAnnotation(
    // 基础配置
    queryAsync = true,           // 异步查询
    autoRun = false,             // 自动运行
    autoDisplay = false,         // 自动显示
    isAllform = false,           // 是否作用于整个表单
    index = -1,                  // 索引
    
    // 绑定配置
    bindMenu = {},               // 绑定菜单项
    bindTreeGridMenu = {},           // 绑定TreeGrid菜单
    bindTreeMenu = {},           // 绑定Tree菜单
    bindFormMenu = {},           // 绑定Form菜单
    bindGalleryMenu = {},        // 绑定Gallery菜单
    bindAction = {},             // 绑定操作动作
    
    // 事件绑定
    bindFieldEvent = {},         // 绑定字段事件
    bindGalleryEvent = {},       // 绑定Gallery事件
    bindTitleBlockEvent = {},    // 绑定标题块事件
    bindContentBlockEvent = {},  // 绑定内容块事件
    bindTreeGridEvent = {},          // 绑定TreeGrid事件
    bindMTreeGridEvent = {},         // 绑定MTreeGrid事件
    bindTreeEvent = {},          // 绑定Tree事件
    bindFormEvent = {},          // 绑定Form事件
    bindMFormEvent = {},         // 绑定MForm事件
    bindTabsEvent = {},          // 绑定Tabs事件
    bindHotKeyEvent = {},        // 绑定热键事件
    
    // 数据路径配置
    requestDataSource = {},      // 请求数据源
    responseDataTarget = {},     // 响应数据目标
    responseCallback = {},       // 响应回调
    customRequestData = {},      // 自定义请求数据
    customResponseData = {},     // 自定义响应数据
    customResponseCallback = {}, // 自定义响应回调
    
    // 执行阶段配置
    beforeData = {},             // 数据准备前
    beforeDataAction = {},       // 数据准备前动作
    beforeInvoke = {},           // 调用前
    beforeInvokeAction = {},     // 调用前动作
    callback = {},               // 回调
    callbackAction = {},         // 回调动作
    afterInvok = {},             // 调用后
    afterInvokAction = {},       // 调用后动作
    onData = {},                 // 数据处理
    onDataAction = {},           // 数据处理动作
    onExecuteSuccess = {},       // 执行成功
    onExecuteSuccessAction = {}, // 执行成功动作
    onError = {},                // 错误处理
    onErrorAction = {},          // 错误处理动作
    onExecuteError = {},         // 执行错误
    onExecuteErrorAction = {}    // 执行错误动作
)
```

### 2.3 APIEvent事件类型
``java
public enum APIEventEnum {
    beforeData("开始准备数据", "fas fa-database"),        // 开始准备数据
    onData("数据准备完成", "fas fa-check-circle"),        // 数据准备完成
    beforeInvoke("开始调用", "fas fa-play"),              // 开始调用
    onError("调用失败", "fas fa-times-circle"),           // 调用失败
    afterInvoke("调用后", "fas fa-backward"),             // 调用后
    onExecuteSuccess("执行成功", "fas fa-check"),         // 执行成功
    onExecuteError("执行失败", "fas fa-exclamation-triangle"), // 执行失败
    callback("回调函数", "fas fa-code");                  // 回调函数
}
```

### 2.4 使用示例
``java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    
    @APIEventAnnotation(
        bindAction = {CustomAction.SEARCH},
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},
        customResponseData = {ResponsePathEnum.FORM},
        beforeInvoke = {CustomBeforInvoke.BUSY},
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE}
    )
    @PostMapping("/search")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<List<Employee>> searchEmployees(@RequestBody SearchParams params) {
        // 搜索员工逻辑
        return employeeRepository.search(params);
    }
}
```

## 3. MQTT通讯组件

### 3.1 概述
MQTT是ooder平台支持的轻量级消息传输协议，适用于物联网设备通信和实时消息推送场景。

### 3.2 核心注解
``java
@MQTTAnnotation(
    dataBinder = "",             // 数据绑定器
    dataField = "",              // 数据字段
    libCDN = "",                 // CDN库路径
    autoConn = false,            // 自动连接
    autoSub = false,             // 自动订阅
    subscribers = {},            // 订阅者列表
    
    // 连接配置
    server = "127.0.0.1",        // 服务器地址
    port = 7019,                 // 端口号
    path = "/",                  // 路径
    clientId = "",               // 客户端ID
    timeout = 30,                // 超时时间
    userName = "admin",          // 用户名
    password = "admin",          // 密码
    keepAliveInterval = 60,      // 保持连接间隔
    cleanSession = false,        // 清理会话
    useSSL = false,              // 使用SSL
    reconnect = true,            // 自动重连
    
    // 遗嘱消息
    willTopic = "",              // 遗嘱主题
    willMessage = "",            // 遗嘱消息
    willQos = 0,                 // 遗嘱QoS
    willRetained = false,        // 遗嘱保留
    
    // 事件处理
    onMsgArrived = {},           // 消息到达事件
    onMsgArrivedAction = {}      // 消息到达动作
)
```

### 3.3 MQTT事件类型
``java
public enum MQTTEventEnum implements EventKey {
    onConnSuccess("onConnSuccess"),     // 连接成功
    onConnFailed("onConnFailed"),       // 连接失败
    onConnLost("onConnLost"),           // 连接丢失
    onSubSuccess("onSubSuccess"),       // 订阅成功
    onSubFailed("onSubFailed"),         // 订阅失败
    onUnsubSuccess("onUnsubSuccess"),   // 取消订阅成功
    onUnsubFailed("onUnsubFailed"),     // 取消订阅失败
    onMsgDelivered("onMsgDelivered"),   // 消息传递
    onMsgArrived("onMsgArrived");       // 消息到达
}
```

### 3.4 使用示例
``java
@Component
public class DeviceMQTTService {
    
    @MQTTAnnotation(
        server = "mqtt.example.com",
        port = 1883,
        clientId = "device_001",
        userName = "device_user",
        password = "device_password",
        autoConn = true,
        autoSub = true,
        subscribers = {"device/status", "device/data"},
        onMsgArrived = {MQTTEventEnum.onMsgArrived}
    )
    private MQTTClient mqttClient;
    
    @MQTTEvent(
        event = MQTTEventEnum.onMsgArrived,
        actions = {CustomAction.REFRESH},
        eventName = "messageArrived",
        desc = "处理设备消息"
    )
    public void handleMessage(String topic, String message) {
        // 处理接收到的MQTT消息
        System.out.println("Received message on topic " + topic + ": " + message);
    }
}
```

## 4. 按钮事件通讯组件

### 4.1 概述
按钮事件通讯组件用于处理用户界面中按钮的点击事件和其他交互行为。

### 4.2 核心注解
``java
@ButtonEvent(
    eventEnum = BottonEventEnum.onClick,  // 事件类型
    name = "",                            // 事件名称
    expression = "",                      // 表达式
    actions = {}                          // 动作列表
)
```

### 4.3 按钮事件类型
``java
public enum BottonEventEnum implements EventKey {
    onClick("onClick"),           // 点击事件
    onClickDrop("onClickDrop"),   // 点击下拉事件
    onChecked("onChecked");       // 选中事件
}
```

### 4.4 使用示例
``java
@FormViewAnnotation
public class EmployeeManagementView {
    
    @ButtonAnnotation(caption = "搜索")
    @ButtonEvent(
        eventEnum = BottonEventEnum.onClick,
        actions = {CustomAction.RELOAD}
    )
    private SearchButton searchButton;
    
    @ButtonAnnotation(caption = "新增")
    @ButtonEvent(
        eventEnum = BottonEventEnum.onClick,
        actions = {CustomAction.ADD}
    )
    private AddButton addButton;
}
```

## 5. 自定义动作通讯组件

### 5.1 概述
自定义动作通讯组件提供了丰富的动作类型，用于定义组件在特定事件发生时应执行的操作。

### 5.2 核心注解
``java
@CustomAction(
    desc = "",                    // 描述
    type = ActionTypeEnum.page,   // 动作类型
    name = "",                    // 动作名称
    expression = "true",          // 表达式
    target = "",                  // 目标
    method = "",                  // 方法
    _return = true,               // 是否返回
    redirection = "",             // 重定向
    conditions = {},              // 条件
    args = {}                     // 参数
)
```

### 5.3 动作类型枚举
``java
public enum ActionTypeEnum {
    page("当前页面"),              // 当前页面
    control("内部调用"),           // 内部调用
    module("模块间"),              // 模块间
    otherModuleCall("外部页面调用"), // 外部页面调用
    other("方法调用"),             // 方法调用
    msg("发送消息"),              // 发送消息
    var("对象定义"),              // 对象定义
    callback("回调执行"),          // 回调执行
    undefined("空实现");          // 空实现
}
```

### 5.4 消息动作类型
``java
public enum CustomMsgAction {
    ALERT("警告", CustomMsgMethod.alert, new String[]{"{args[0]}", "{args[1]}"}),
    ECHO("调试框", CustomMsgMethod.echo, new String[]{"{args[0]}", "{args[1]}", "{page.projectName}", "{page.getValue()}"}),
    CONFIRM("确认框", CustomMsgMethod.busy, new String[]{"{args[0]}", "{args[1]}"}),
    PROMPT("提示对话框", CustomMsgMethod.prompt, new String[]{"{args[0]}", "{args[1]}"}),
    MESSAGE("提示框", CustomMsgMethod.message, new String[]{"{args[0]}", "{args[1]}"}),
    SUCCESSMSG("成功调用提示", CustomMsgMethod.message, new String[]{null,"操作成功"}),
    ERRORMSG("错误信息提示", CustomMsgMethod.alert, new String[]{null,"操作成功","{args[1].errdes}"}),
    BUSY("遮罩", CustomMsgMethod.busy, new String[]{"{false}","正在处理..."}),
    FREE("解除遮罩", CustomMsgMethod.free, new String[]{}),
    MSG("消息", CustomMsgMethod.msg, new String[]{"{args[0]}", "{args[1]}", "200", "5000"}),
    LOG("console日志", CustomMsgMethod.log, new String[]{"{args[0]}", "{args[1]}"});
}
```

### 5.5 页面动作类型
``java
public enum CustomPageAction {
    CLOSE("关闭页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroy, new String[]{}, "true", true),
    RELOAD("刷新页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.initData, new String[]{}, "true", true),
    EDITOR("编辑", CustomTarget.DYNEDITORMODULENAME.getName(), CustomModuleMethod.show2, new String[]{"{page.show2()}", CustomTarget.DYNEDITORMODULETARGET.getName(), null, null, null, null, "{args[1]}", "{page}", "{" + CustomTarget.EDITERMODULEDIO.getName() + "}"}, "true", true),
    ADD("新增", CustomTarget.DYNADDMODULENAME.getName(), CustomModuleMethod.show2, new String[]{null, null, null, null, null, null, "{page.getData()}", "{page}", "{" + CustomTarget.EDITERMODULEDIO.getName() + "}"}, "true", true),
    CLOSEPARENT("关闭父级页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroyParent, new String[]{}, "true", true),
    RELOADPARENT("刷新父级页面", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.reloadParent, new String[]{}, "true", true),
    CLOSETOP("关闭当前窗体", CustomTarget.DYNCURRMODULENAME.getName(), CustomModuleMethod.destroyCurrDio, new String[]{}, "true", true),
    RELOADTOP("刷新顶级页面", CustomTarget.TOPMODULE.getName(), CustomModuleMethod.initData, new String[]{}, "true", true);
}
```

## 6. Page上下文环境（PageCtx）

### 6.1 概述
Page上下文环境（PageCtx）是ooder平台中一个重要的概念，它作为统一的环境变量管理机制，在Page动态构建过程中发挥关键作用。PageCtx包含页面运行所需的所有上下文信息，包括视图中的环境变量、用户会话信息、页面参数等。

### 6.2 环境变量汇聚机制
在Page动态构建过程中，系统会根据视图入口方法的参数，将参数汇聚到PageCtx页面上下文中：

``java
// 视图入口类
@RestController
@RequestMapping("/employee/management")
public class EmployeeManagementViewController {
    // 视图入口方法，包含参数
    @GetMapping
    public EmployeeManagementView getEmployeeManagementView(
        @RequestParam(required = false) String departmentId,
        @RequestParam(required = false) String employeeName) {
        
        // 创建视图对象
        EmployeeManagementView view = new EmployeeManagementView();
        
        // 参数会自动汇聚到PageCtx上下文环境
        // PageCtx中会包含departmentId和employeeName参数
        return view;
    }
}
```

### 6.3 视图环境变量收集
在视图中，通常会使用字段上添加@Pid、@Uid等注解显式地将其加入到整个页面的当前环境中：

``java
@FormViewAnnotation
public class EmployeeManagementView {
    // 通过@Uid注解将字段显式加入到页面上下文
    // @Uid通常用于标识记录的唯一ID
    @Uid
    @HiddenField
    private String employeeId;
    
    // 通过@Pid注解将字段显式加入到页面上下文
    // @Pid通常用于标识父记录的ID
    @Pid
    @HiddenField
    private String departmentId;
    
    @TextField(label = "员工姓名")
    private String employeeName;
    
    // 其他字段定义
}
```

在Page初始化（编译）的过程中，系统会将所有视图中的环境变量都搜集到PageCtx中。

@Pid和@Uid注解的定义：
``java
// @Pid注解定义
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Pid {
}

// @Uid注解定义
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Uid {
}
```

### 6.4 APIEvent中的上下文参数传递

#### 6.4.1 上行参数配置
在APIEvent配置上行参数时，通常会默认将PageCtx加入进来：

``java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    
    @APIEventAnnotation(
        bindAction = {CustomAction.SEARCH},
        // 默认将PageCtx加入上行参数
        customRequestData = {RequestPathEnum.CTX, RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM},
        beforeInvoke = {CustomBeforInvoke.BUSY},
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE}
    )
    @PostMapping("/search")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<List<Employee>> searchEmployees(@RequestBody SearchParams params) {
        // 在方法中可以通过params访问PageCtx中的环境变量
        // 搜索员工逻辑
        return employeeRepository.search(params);
    }
}
```

#### 6.4.2 下行参数配置
在下行参数中，也允许通过向ResultModel的ctx属性赋值来修改当前页面：

``java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    
    @APIEventAnnotation(
        bindAction = {CustomAction.UPDATE},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM, ResponsePathEnum.CTX},
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE}
    )
    @PostMapping("/update")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<Employee> updateEmployee(@RequestBody Employee employee) {
        // 更新员工信息
        Employee updatedEmployee = employeeRepository.update(employee);
        
        // 创建结果模型
        ResultModel<Employee> result = new ResultModel<>();
        result.setData(updatedEmployee);
        
        // 可以通过向ctx赋值来修改当前页面的上下文环境
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("lastUpdatedEmployee", updatedEmployee);
        ctx.put("updateTime", new Date());
        result.setCtx(ctx);
        
        // 也可以使用addCtx方法添加上下文变量
        result.addCtx("operation", "update");
        result.addCtx("status", "success");
        
        return result;
    }
}
```

ResultModel中与PageCtx相关的方法：
``java
public class ResultModel<T> {
    // 存储上下文环境的Map
    public Map<String, Object> ctx;
    
    // 添加上下文变量
    public void addCtx(String name, Object value) {
        if (ctx == null) {
            ctx = new HashMap<>();
        }
        ctx.put(name, value);
    }
    
    // 获取上下文环境
    public Map<String, Object> getCtx() {
        return ctx;
    }
    
    // 设置上下文环境
    public void setCtx(Map<String, Object> ctx) {
        this.ctx = ctx;
    }
}
```

### 6.5 PageCtx使用最佳实践

#### 6.5.1 环境变量命名规范
1. 使用清晰、具有业务含义的命名
2. 避免使用保留关键字
3. 保持命名风格一致性

#### 6.5.2 数据传递规范
1. 上行参数中合理选择RequestPathEnum枚举值
2. 下行参数中通过ctx传递需要更新的上下文信息
3. 避免在ctx中传递大量不必要的数据

#### 6.5.3 安全性考虑
1. 对敏感信息进行适当处理
2. 验证ctx中传递的数据合法性
3. 避免在客户端暴露敏感的上下文信息

#### 6.5.4 性能优化
1. 只传递必要的上下文变量
2. 避免在ctx中存储大对象
3. 合理使用addCtx方法进行增量更新

## 7. 通讯组件最佳实践

### 7.1 设计原则
1. **单一职责原则**：每个通讯组件应有明确的职责和用途
2. **可配置性**：通过注解参数提供灵活的配置选项
3. **可扩展性**：支持自定义事件和动作扩展
4. **安全性**：确保通讯过程中的数据安全

### 7.2 实现规范
1. **注解使用规范**：正确使用各类通讯注解
2. **Web可访问性**：所有服务必须实现Web可访问性
3. **数据一致性**：确保前后端数据传输的一致性
4. **错误处理**：提供完善的错误处理机制

### 7.3 性能优化
1. **异步处理**：合理使用异步查询提高响应速度
2. **连接复用**：对于长连接通讯组件，实现连接复用
3. **数据压缩**：在网络传输中使用数据压缩技术
4. **缓存机制**：合理使用缓存减少重复请求

### 7.4 安全规范
1. **认证授权**：实现完善的认证授权机制
2. **数据加密**：敏感数据传输时使用加密技术
3. **访问控制**：严格控制组件的访问权限
4. **日志记录**：记录关键通讯操作日志

## 8. 通讯组件集成

### 8.1 与Page的集成
通讯组件在Page动态构建过程中发挥重要作用：
1. APIEvent组件将服务方法编译为页面的Ajax通讯组件
2. MQTT组件实现页面与消息服务器的实时通信
3. 数据路径枚举确保参数在PageCtx中的正确传递

### 8.2 与视图组件的集成
1. **Form视图**：通过APIEvent实现表单数据提交和验证
2. **TreeGrid视图**：通过APIEvent实现数据加载和操作
3. **Tree视图**：通过MQTT实现节点状态实时更新
4. **Tabs视图**：通过APIEvent实现标签页内容动态加载

### 8.3 与BAR服务的集成
1. BAR服务中的菜单项通过APIEvent与后端服务绑定
2. BAR服务中的按钮动作通过CustomAction与通讯组件关联
3. 实现BAR操作的异步处理和状态反馈

## 9. 常见问题与解决方案

### 9.1 连接问题
1. **问题**：MQTT连接失败
   **解决方案**：检查服务器地址、端口、用户名和密码配置

2. **问题**：API调用超时
   **解决方案**：调整timeout参数，优化后端服务性能

### 9.2 数据传输问题
1. **问题**：数据传输不一致
   **解决方案**：使用RequestPathEnum和ResponsePathEnum确保数据路径正确

2. **问题**：消息丢失
   **解决方案**：启用MQTT的遗嘱消息功能，实现消息确认机制

### 9.3 性能问题
1. **问题**：通讯延迟高
   **解决方案**：使用异步处理，优化网络连接

2. **问题**：连接资源耗尽
   **解决方案**：实现连接池，合理管理连接生命周期