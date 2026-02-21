# 6.1 参数规范手册

## 1. 概述

ooder平台的参数规范定义了在视图对象、服务方法和通讯组件中参数的使用规则和最佳实践。本规范旨在确保参数传递的一致性、安全性和高效性，同时提供清晰的参数标识和管理机制。

## 2. 视图对象中的参数规范

### 2.1 集合类数据视图对象
在集合类数据（视图）对象中，应指定字段具有@Uid属性，用于标识记录的唯一性：

```java
@TreeGridAnnotation(
    customService = {EmployeeListService.class},
    showHeader = true,
    colSortable = true
)
public class EmployeeTreeGridView {
    // 使用@Uid注解标识记录的唯一ID
    @Uid
    @HiddenField
    private String employeeId;
    
    @TextField(label = "员工姓名")
    private String employeeName;
    
    @TextField(label = "部门")
    private String department;
    
    // 其他字段定义
}
```

### 2.2 嵌套视图对象
在嵌套视图对象中，应将上级的@Uid属性在下级视图中复制并标识为@Pid：

```java
@FormViewAnnotation
public class DepartmentView {
    // 父级视图中的唯一标识
    @Uid
    @HiddenField
    private String departmentId;
    
    @TextField(label = "部门名称")
    private String departmentName;
    
    // 嵌套的员工列表视图
    private List<EmployeeView> employees;
}

public class EmployeeView {
    // 复制父级的@Uid属性并标识为@Pid
    @Pid
    @HiddenField
    private String departmentId;
    
    // 当前视图的唯一标识
    @Uid
    @HiddenField
    private String employeeId;
    
    @TextField(label = "员工姓名")
    private String employeeName;
    
    @TextField(label = "职位")
    private String position;
}
```

### 2.3 聚合类视图
在聚合类视图中也允许添加@Pid和@Uid属性，作为所有子级的共享环境变量：

```java
@TabsAnnotation(
    customService = {EmployeeManagementService.class},
    tabPosition = TabPosition.TOP
)
public class EmployeeManagementView {
    // 聚合视图中的共享环境变量
    @Pid
    @HiddenField
    private String companyId;
    
    @Uid
    @HiddenField
    private String departmentId;
    
    // 聚合的标签页内容
    @TabsItemsAnnotation(tabItems = EmployeeTabItems.class)
    private List<TabItem> tabItems;
}

public enum EmployeeTabItems implements TabItem {
    BASIC_INFO("基本信息", "fas fa-info", EmployeeBasicInfoService.class),
    WORK_HISTORY("工作经历", "fas fa-history", EmployeeWorkHistoryService.class);
    
    // 枚举实现
}
```

## 3. 服务方法中的参数规范

### 3.1 服务类中的公共变量
Service中允许添加@Pid和@Uid属性，作为所有服务方法的公共变量，在初始化服务时赋值：

```java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    // 服务类中的公共变量
    @Pid
    private String departmentId;
    
    @Uid
    private String employeeId;
    
    // 在服务初始化时赋值
    @PostConstruct
    public void init() {
        // 从上下文环境中获取departmentId和employeeId
        // 这些值通常来自PageCtx
    }
    
    @APIEventAnnotation(
        bindAction = {CustomAction.SEARCH},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM}
    )
    @PostMapping("/search")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<List<Employee>> searchEmployees(@RequestBody SearchParams params) {
        // 在方法中可以使用this.departmentId和this.employeeId
        // 这些值来自服务类的公共变量
        return employeeRepository.searchByDepartment(departmentId, params);
    }
    
    @APIEventAnnotation(
        bindAction = {CustomAction.UPDATE},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM}
    )
    @PostMapping("/update")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<Employee> updateEmployee(@RequestBody Employee employee) {
        // 使用this.employeeId作为更新条件
        employee.setId(employeeId);
        return employeeRepository.update(employee);
    }
}
```

### 3.2 参数传递决策树
根据ooder服务参数设计规范，应按照以下决策树选择参数类型：

```java
// 1. 需要访问视图中的所有字段时使用@RequestBody视图对象参数
@PostMapping("/submit")
public ResultModel<Boolean> submitForm(@RequestBody EmployeeView employeeView) {
    // 处理完整的表单数据
    return employeeService.processForm(employeeView);
}

// 2. 只需要标识特定记录时使用@Uid或@Pid简单参数
@PostMapping("/delete")
public ResultModel<Boolean> deleteEmployee(@Uid String employeeId) {
    // 只需要删除特定员工
    return employeeService.deleteEmployee(employeeId);
}

// 3. 需要多个筛选条件时使用多个简单参数
@GetMapping("/search")
public ResultModel<List<Employee>> searchEmployees(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String department,
    @RequestParam(required = false) String position) {
    // 根据多个条件搜索员工
    return employeeService.searchEmployees(name, department, position);
}
```

## 4. 通讯组件中的参数规范

### 4.1 环境变量重置
环境变量允许在通讯服务ResultModel的pageCtx中进行重置：

```java
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
        
        // 重置环境变量
        Map<String, Object> pageCtx = new HashMap<>();
        pageCtx.put("lastUpdatedEmployee", updatedEmployee);
        pageCtx.put("lastUpdateTime", new Date());
        pageCtx.put("updateStatus", "success");
        result.setCtx(pageCtx);
        
        return result;
    }
}
```

### 4.2 组件自变量
一些组件具有自变量，如pageBar中的分页值，Tabs中的上一步、下一步：

```java
// PageBar组件中的分页参数
@TreeGridAnnotation(
    customService = {EmployeeListService.class},
    showHeader = true,
    colSortable = true,
    bottombarMenu = {TreeGridMenu.PAGEBAR} // 启用分页栏
)
public class EmployeeTreeGridView {
    @Uid
    @HiddenField
    private String employeeId;
    
    // 其他字段
}

@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeListService {
    @APIEventAnnotation(
        bindTreeGridMenu = {TreeGridMenu.PAGEBAR},
        customRequestData = {RequestPathEnum.PAGEBAR, RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.GRIDNEXT}
    )
    @PostMapping("/list")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ListResultModel<Employee> getEmployeeList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        // 使用分页参数查询数据
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        
        ListResultModel<Employee> result = new ListResultModel<>();
        result.setData(employeePage.getContent());
        result.setSize((int) employeePage.getTotalElements());
        
        // 设置分页上下文
        result.addCtx("currentPage", page);
        result.addCtx("pageSize", size);
        result.addCtx("totalPages", employeePage.getTotalPages());
        
        return result;
    }
}

// Tabs组件中的步骤参数
@TabsAnnotation(
    customService = {EmployeeOnboardingService.class},
    tabPosition = TabPosition.TOP
)
public class EmployeeOnboardingView {
    @Uid
    @HiddenField
    private String employeeId;
    
    @Pid
    @HiddenField
    private String departmentId;
}

@Service
@RestController
@RequestMapping("/api/onboarding")
public class EmployeeOnboardingService {
    @APIEventAnnotation(
        bindAction = {CustomAction.NEXT, CustomAction.PREVIOUS},
        customRequestData = {RequestPathEnum.CURRFORM},
        customResponseData = {ResponsePathEnum.FORM}
    )
    @PostMapping("/navigate")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<EmployeeOnboardingView> navigateStep(
        @RequestParam String direction, // "next" 或 "previous"
        @RequestBody EmployeeOnboardingView currentView) {
        
        // 根据方向导航到下一步或上一步
        EmployeeOnboardingView nextView = onboardingService.navigate(direction, currentView);
        
        ResultModel<EmployeeOnboardingView> result = new ResultModel<>();
        result.setData(nextView);
        
        // 更新步骤上下文
        result.addCtx("currentStep", getCurrentStep(nextView));
        result.addCtx("totalSteps", getTotalSteps());
        result.addCtx("canGoNext", canGoNext(nextView));
        result.addCtx("canGoPrevious", canGoPrevious(nextView));
        
        return result;
    }
}
```

## 5. 参数规范最佳实践

### 5.1 参数命名规范
1. 使用清晰、具有业务含义的命名
2. 保持命名风格一致性（驼峰命名法）
3. 避免使用保留关键字

```java
// 好的命名示例
@Uid
private String employeeId;

@Pid
private String departmentId;

// 避免的命名示例
@Uid
private String id; // 不够明确

@Pid
private String pid; // 不够明确
```

### 5.2 参数传递规范
1. 参数最小化原则：始终只传递必要的参数
2. 合理使用@RequestBody和简单参数
3. 正确使用@Uid和@Pid注解

```java
// 好的实践示例
@PostMapping("/update")
public ResultModel<Employee> updateEmployee(
    @Uid String employeeId, // 使用@Uid标识记录
    @RequestBody EmployeeUpdateData updateData) { // 只传递需要更新的数据
    return employeeService.updateEmployee(employeeId, updateData);
}

// 避免的实践示例
@PostMapping("/update")
public ResultModel<Employee> updateEmployee(@RequestBody Employee employee) {
    // 传递了完整的Employee对象，包含可能不需要的字段
    return employeeService.updateEmployee(employee);
}
```

### 5.3 安全性考虑
1. 对敏感信息进行适当处理
2. 验证参数的合法性
3. 避免在客户端暴露敏感的参数信息

```java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    
    @PostMapping("/update")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ResultModel<Employee> updateEmployee(
        @Uid String employeeId,
        @RequestBody EmployeeUpdateData updateData) {
        
        // 验证参数合法性
        if (employeeId == null || employeeId.isEmpty()) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        
        if (updateData == null) {
            throw new IllegalArgumentException("更新数据不能为空");
        }
        
        // 验证权限：确保用户只能更新自己的数据
        if (!permissionService.canUpdateEmployee(employeeId)) {
            throw new SecurityException("无权限更新该员工信息");
        }
        
        // 执行更新操作
        return employeeRepository.updateEmployee(employeeId, updateData);
    }
}
```

### 5.4 性能优化
1. 只传递必要的参数
2. 避免传递大对象
3. 合理使用分页参数

```java
// 好的性能实践
@GetMapping("/list")
public ListResultModel<EmployeeSummary> getEmployeeList(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "20") int size) {
    // 只返回员工摘要信息，而不是完整信息
    return employeeService.getEmployeeSummary(page, size);
}

// 避免的性能问题
@GetMapping("/list")
public ListResultModel<Employee> getEmployeeList(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "100") int size) {
    // 返回完整的员工信息，包含可能不需要的字段
    // 并且每页返回100条记录，可能导致性能问题
    return employeeService.getAllEmployeeDetails(page, size);
}
```

## 6. 参数规范与PageCtx的集成

### 6.1 PageCtx参数汇聚
在Page动态构建过程中，系统会根据视图入口方法的参数，将参数汇聚到PageCtx页面上下文中：

```java
@RestController
@RequestMapping("/employee/management")
public class EmployeeManagementViewController {
    // 视图入口方法，参数会自动汇聚到PageCtx
    @GetMapping
    public EmployeeManagementView getEmployeeManagementView(
        @RequestParam(required = false) String departmentId,
        @RequestParam(required = false) String employeeName,
        @RequestParam(defaultValue = "1") int page) {
        
        EmployeeManagementView view = new EmployeeManagementView();
        // departmentId, employeeName, page 参数会自动汇聚到PageCtx
        return view;
    }
}
```

### 6.2 PageCtx参数使用
在服务方法中可以通过访问PageCtx获取上下文参数：

```java
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.CTX, RequestPathEnum.CURRFORM}
    )
    @PostMapping("/search")
    @Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
    public ListResultModel<Employee> searchEmployees(@RequestBody SearchParams params) {
        // 从PageCtx中获取上下文参数
        String departmentId = (String) params.getCtx().get("departmentId");
        String employeeName = (String) params.getCtx().get("employeeName");
        Integer page = (Integer) params.getCtx().get("page");
        
        // 使用上下文参数执行搜索
        return employeeRepository.searchEmployees(departmentId, employeeName, page);
    }
}
```

## 7. 常见问题与解决方案

### 7.1 参数传递问题
1. **问题**：@Uid或@Pid参数未正确传递
   **解决方案**：确保在视图对象中正确使用@Uid和@Pid注解，并在服务方法中正确接收参数

2. **问题**：PageCtx中的参数丢失
   **解决方案**：检查APIEventAnnotation中的customRequestData配置，确保包含了RequestPathEnum.CTX

### 7.2 性能问题
1. **问题**：传递了不必要的大对象参数
   **解决方案**：使用参数最小化原则，只传递必要的数据

2. **问题**：分页参数未正确使用
   **解决方案**：合理设置分页大小，避免一次性加载过多数据

### 7.3 安全问题
1. **问题**：敏感信息暴露
   **解决方案**：对敏感参数进行适当处理，不在客户端暴露敏感信息

2. **问题**：权限验证缺失
   **解决方案**：在服务方法中添加权限验证逻辑，确保用户只能访问授权的数据