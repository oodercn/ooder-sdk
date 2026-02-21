# ooder平台编码规范手册

## 目录

1. [概述](#1-概述)
2. [命名规范](#2-命名规范)
   - 2.1 类命名规范
   - 2.2 方法命名规范
   - 2.3 变量命名规范
   - 2.4 常量命名规范
3. [注解使用规范](#3-注解使用规范)
   - 3.1 视图类注解
   - 3.2 字段注解
   - 3.3 服务类注解
   - 3.4 方法注解
4. [代码结构规范](#4-代码结构规范)
   - 4.1 包结构规范
   - 4.2 类结构规范
   - 4.3 方法结构规范
5. [注释规范](#5-注释规范)
   - 5.1 类注释
   - 5.2 方法注释
   - 5.3 字段注释
6. [异常处理规范](#6-异常处理规范)
7. [数据传输规范](#7-数据传输规范)
8. [服务设计规范](#8-服务设计规范)
9. [视图设计规范](#9-视图设计规范)
10. [最佳实践](#10-最佳实践)

## 1. 概述

ooder平台是一套基于注解驱动的低代码开发框架，通过标准化的注解体系实现前后端的无缝对接。本编码规范旨在统一开发团队的编码风格，提高代码质量和可维护性。

## 2. 命名规范

### 2.1 类命名规范

1. **视图类命名**：
   - 表单视图：以View结尾，如`AttendanceCheckInView`
   - 列表视图：以ListView结尾，如`AttendanceQueryListView`
   - 门户视图：以PortalView结尾，如`AttendancePortalView`
   - 聚合视图：以AggregateView结尾，如`AttendanceAggregateView`

2. **服务类命名**：
   - 业务服务：以Service结尾，如`AttendanceCheckInService`
   - BAR服务：以BarService结尾，如`AttendanceCheckInBarService`
   - 导航服务：以NavigationService结尾，如`AttendanceNavigationService`

3. **枚举类命名**：
   - 业务枚举：以Type结尾，如`AttendanceType`
   - 菜单枚举：以Menu结尾，如`AttendanceMenu`

### 2.2 方法命名规范

1. **服务方法命名**：
   - 查询方法：以query/get/find开头，如`queryAttendanceRecords`
   - 操作方法：以动词开头，如`saveAttendanceRecord`
   - 初始化方法：以init开头，如`initPortal`

2. **事件处理方法命名**：
   - 事件处理：以handle开头，如`handleCheckIn`
   - 回调方法：以on开头，如`onNodeSelect`

### 2.3 变量命名规范

1. **字段变量**：
   - 私有字段：使用驼峰命名法，如`employeeId`
   - 常量字段：使用全大写加下划线，如`MAX_RETRY_COUNT`

2. **局部变量**：
   - 使用有意义的变量名，避免使用单字母变量名
   - 临时变量可使用简短命名，但需保持可读性

### 2.4 常量命名规范

1. **静态常量**：
   - 使用全大写字母，单词间用下划线分隔
   - 如：`public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";`

## 3. 注解使用规范

### 3.1 视图类注解

1. **表单视图**：
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {AttendanceCheckInService.class}
)
```

2. **列表视图**：
```java
@TreeGridAnnotation(
    customService = {AttendanceQueryService.class},
    showHeader = true,
    colSortable = true
)
```

3. **聚合视图**：
```java
@TabsAnnotation(
    caption = "考勤聚合信息",
    autoSave = true,
    barLocation = BarLocationType.top
)
```

### 3.2 字段注解

1. **输入字段**：
```java
@InputAnnotation(
    maxlength = 20
)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

2. **日期字段**：
```java
@DatePickerAnnotation(
    timeInput = true
)
@CustomAnnotation(caption = "签到日期", index = 3)
private String checkInDate;
```

3. **下拉框字段**：
```java
@ComboBoxAnnotation
@CustomListAnnotation(
    enumClass = AttendanceType.class
)
@CustomAnnotation(caption = "考勤类型", index = 6)
private AttendanceType attendanceType;
```

### 3.3 服务类注解

1. **通用服务注解**：
```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/checkin")
@Service
```

2. **BAR服务注解**：
```java
@Aggregation(type = AggregationType.MENU, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/attendance/checkin/bar")
@Service
```

### 3.4 方法注解

1. **API方法注解**：
```java
@APIEventAnnotation(
    customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
    onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
    beforeInvoke = CustomBeforInvoke.BUSY
)
@CustomAnnotation(index = 0, caption = "签到", imageClass = "fa-solid fa-sign-in-alt")
@PostMapping("/checkin")
@ResponseBody
public ResultModel<Boolean> checkIn(@RequestBody AttendanceCheckInView view) {
    // 方法实现
}
```

## 4. 代码结构规范

### 4.1 包结构规范

```
net.ooder.attendance
├── view                    // 视图类
├── service                 // 服务类
├── controller              // 控制器类
├── enums                   // 枚举类
├── menu                    // 菜单类
├── example                 // 示例类
└── doc                     // 文档类
```

### 4.2 类结构规范

1. **视图类结构**：
```java
/**
 * 类描述
 */
@ViewAnnotation(...)
public class ViewClassName {
    
    // 字段定义
    @FieldAnnotation(...)
    @CustomAnnotation(...)
    private FieldType fieldName;
    
    // Getter/Setter方法
    public FieldType getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(FieldType fieldName) {
        this.fieldName = fieldName;
    }
}
```

2. **服务类结构**：
```java
/**
 * 类描述
 */
@ServiceAnnotation(...)
@RestController
@RequestMapping("/...")
@Service
public class ServiceClassName {
    
    /**
     * 方法描述
     */
    @MethodAnnotation(...)
    @RequestMapping(...)
    public ReturnType methodName(Parameters...) {
        // 方法实现
    }
}
```

### 4.3 方法结构规范

1. **方法签名**：
   - 方法名应清晰表达功能
   - 参数应有明确的业务含义
   - 返回值应符合平台规范

2. **方法实现**：
   - 使用try-catch处理异常
   - 设置正确的返回状态
   - 记录必要的日志信息

## 5. 注释规范

### 5.1 类注释

```java
/**
 * 考勤签到视图类
 * 绑定考勤签到服务类处理业务逻辑
 * 
 * @author 开发者姓名
 * @version 1.0
 * @since 2023-10-01
 */
```

### 5.2 方法注释

```java
/**
 * 处理签到逻辑
 * 
 * @param view 考勤签到视图对象
 * @return 签到结果
 */
@PostMapping("/checkin")
@ResponseBody
public ResultModel<Boolean> handleCheckIn(@RequestBody AttendanceCheckInView view) {
    // 方法实现
}
```

### 5.3 字段注释

```java
/**
 * 员工ID
 */
@InputAnnotation(
    maxlength = 20
)
@CustomAnnotation(caption = "员工ID", index = 1)
private String employeeId;
```

## 6. 异常处理规范

1. **统一异常处理**：
```java
try {
    // 业务逻辑
    resultModel.setData(data);
    resultModel.setRequestStatus(1); // 设置成功状态
} catch (Exception e) {
    // 发生错误时返回ErrorResultModel封装的错误信息
    ErrorResultModel<T> errorResult = new ErrorResultModel<>();
    errorResult.setErrdes(e.getMessage());
    errorResult.setErrcode(1000); // 设置默认错误码
    errorResult.setRequestStatus(-1); // 设置错误状态
    return errorResult;
}
```

2. **使用ErrorResultModel处理异常**：
对于需要返回详细错误信息的场景，应使用`ErrorResultModel`：
```java
try {
    // 业务逻辑
    resultModel.setData(data);
    resultModel.setRequestStatus(1); // 设置成功状态
} catch (Exception e) {
    // 只有发生错误时才返回ErrorResultModel封装的错误信息
    ErrorResultModel<T> errorResult = new ErrorResultModel<>();
    errorResult.setErrdes(e.getMessage());
    errorResult.setErrcode(1000); // 设置默认错误码
    errorResult.setRequestStatus(-1); // 设置错误状态
    return errorResult;
}
```

3. **自定义异常**：
   - 继承平台异常类
   - 提供清晰的错误信息
   - 包含错误码和错误描述

## 7. 数据传输规范

1. **返回值类型**：
   - 查询操作：使用`ListResultModel<T>`
   - 操作执行：使用`ResultModel<Boolean>`
   - 数据获取：使用`ResultModel<T>`

2. **数据封装**：
```java
ResultModel<Boolean> resultModel = new ResultModel<>();
resultModel.setData(true);
resultModel.setRequestStatus(1); // 设置成功状态
```

## 8. 服务设计规范

1. **Web可访问性**：
   - 所有服务必须使用`@RestController`注解
   - 必须使用`@Aggregation`声明服务类型
   - 提供完整的URL映射

2. **参数设计**：
   - 遵循参数最小化原则
   - 合理使用`@RequestBody`和简单参数
   - 优先使用`@Uid`字段标识记录

3. **方法设计**：
   - 方法职责单一
   - 命名清晰表达功能
   - 包含完整的异常处理

## 9. 视图设计规范

1. **视图类型匹配**：
   - 表单视图使用`@FormAnnotation`
   - 列表视图使用`@TreeGridAnnotation`
   - 聚合视图使用`@TabsAnnotation`

2. **字段注解规范**：
   - 正确使用字段类型注解
   - 合理设置字段属性
   - 使用`@CustomAnnotation`定义显示属性

3. **服务绑定**：
   - 在视图类上使用`customService`绑定服务类
   - 确保服务类实现Web可访问性

## 10. 最佳实践

1. **代码复用**：
   - 提取公共方法和工具类
   - 使用继承和接口实现代码复用
   - 避免重复代码

2. **性能优化**：
   - 合理使用懒加载
   - 避免不必要的数据传输
   - 优化数据库查询

3. **安全性**：
   - 验证输入参数
   - 处理敏感数据
   - 防止SQL注入

4. **可维护性**：
   - 保持代码结构清晰
   - 添加必要的注释
   - 遵循编码规范

通过遵循以上编码规范，可以确保ooder平台项目的代码质量、可维护性和团队协作效率。