# 4.1 Page规范

## 1. 概述

Page是ooder平台中面向用户的最小单位，作为视图的容器，它将各种视图组件（Form、TreeGrid、Tabs、Tree、Group等）有机地组织在一起，形成完整的用户界面。Page并不是通过显式的@Page注解来定义的，而是通过Web容器拦截机制动态包装的。当用户通过浏览器直接访问指定视图时（所有视图入口都具有Web可访问性），Web容器会拦截请求并将其动态包装为Page对象。Page不仅包含可见的视图部分，还集成了当前环境变量、数据通讯组件（ajax/api）、消息组件、样式主体组件等公共性支撑功能。

在Page的动态构建过程中，系统会将各个视图所需的数据接口转换为Ajax定义（APIEventAnnotation），同时将组件的Event定义实例化并与视图/元素进行绑定。根据视图入口方法的参数，系统会将参数汇聚到PageCtx页面当前环境中。此外，系统还会将视图所需的服务端交互从视图绑定的服务类中获取可Web访问的method方法（APIEvent），并将其编译为页面的Ajax通讯组件。

## 2. Page核心概念

### 2.1 定义
Page是视图组件的容器和组织者，具有以下特征：
- 最少由一个视图组成
- 是面向用户的最小单位
- 包含当前环境变量（隐含）
- 集成数据通讯组件（ajax/api）
- 集成消息组件
- 集成样式主体组件等公共性支撑功能

### 2.2 Page与视图的关系
- Page是视图的容器，视图是Page的组成部分
- 一个Page可以包含多个视图组件
- Page负责协调视图组件间的数据传递和状态同步

## 3. Page结构规范

### 3.1 Page基本结构
Page由以下核心组件构成：
1. **视图容器**：包含一个或多个视图组件
2. **环境变量**：当前用户的上下文信息、权限信息等
3. **数据通讯层**：负责与后端服务的数据交互
4. **消息处理层**：处理系统消息和用户通知
5. **样式主题层**：定义页面的整体外观和交互风格
6. **事件处理层**：处理用户交互事件和组件事件绑定
7. **参数上下文**：存储页面运行时参数的上下文环境

### 3.2 Page动态生成机制
Page对象通过Web容器拦截机制动态生成，当用户访问具有Web可访问性的视图入口时，系统会自动将其包装为Page对象：

``java
// 用户直接访问视图入口URL
// Web容器拦截请求并动态生成Page对象
@RestController
@RequestMapping("/user/management")
public class UserManagementViewController {
    // 视图入口方法具有Web可访问性
    @GetMapping
    public UserManagementView getUserManagementView() {
        // 返回视图对象，Web容器自动包装为Page
        return new UserManagementView();
    }
}
```

Page的动态生成过程遵循以下规则：
1. 所有视图入口都必须具有Web可访问性
2. Web容器拦截用户请求并识别视图入口
3. 系统自动将视图对象包装为Page对象
4. Page对象集成环境变量、数据通讯组件、消息组件等公共功能
5. 系统将视图所需的数据接口转换为Ajax定义（APIEventAnnotation）
6. 将组件的Event定义实例化并与视图/元素进行绑定
7. 根据视图入口方法的参数，将参数汇聚到PageCtx页面当前环境中
8. 将视图所需的服务端交互从视图绑定的服务类中获取可Web访问的method方法，并将其编译为页面的Ajax通讯组件

### 3.3 Page注解体系
虽然Page对象是动态生成的，但相关的视图组件和服务仍然需要使用注解来定义其行为和属性：

``java
// 视图类使用专用注解定义
@FormViewAnnotation(
    title = "用户管理",
    description = "用户信息管理页面"
)
public class UserManagementView {
    // 视图内容定义
}

// 服务类使用Spring注解确保Web可访问性
@Service
@RestController
@RequestMapping("/user/management")
public class UserManagementService {
    // 服务方法实现
}
```

## 4. Page与视图组件的关系

### 4.1 包含关系
Page作为容器包含各种视图组件。虽然Page对象是动态生成的，但在Page内部仍然可以包含多个视图组件：

``java
// 视图入口类
@RestController
@RequestMapping("/employee/management")
public class EmployeeManagementViewController {
    // 视图入口方法具有Web可访问性
    @GetMapping
    public EmployeeManagementView getEmployeeManagementView() {
        // 返回视图对象，Web容器自动包装为Page
        return new EmployeeManagementView();
    }
}

// 视图类定义
public class EmployeeManagementView {
    // 包含表单视图
    private EmployeeFormView employeeForm;
    
    // 包含列表视图
    private EmployeeTreeGridView employeeTreeGrid;
    
    // 包含树形视图
    private DepartmentTreeView departmentTree;
    
    // 包含标签页视图
    private EmployeeDetailTabs employeeTabs;
    
    // Getters and setters
}
```

### 4.2 数据流关系
Page负责协调各视图组件间的数据流：

``java
@Service
public class EmployeeManagementPageService {
    @Autowired
    private EmployeeFormService formService;
    
    @Autowired
    private EmployeeTreeGridService gridService;
    
    @Autowired
    private DepartmentTreeService treeService;
    
    public void onEmployeeSelected(Employee employee) {
        // 当在TreeGrid中选择员工时，更新Form和Tabs的数据
        formService.loadEmployeeData(employee);
        gridService.refreshEmployeeDetails(employee.getId());
    }
    
    public void onDepartmentSelected(Department department) {
        // 当在Tree中选择部门时，刷新TreeGrid数据
        gridService.loadEmployeesByDepartment(department.getId());
    }
}
```

## 5. Page动态构建过程

### 5.1 视图数据接口转换
在Page动态构建过程中，系统会将各个视图所需的数据接口转换为Ajax定义：

``java
// 视图类定义
@FormViewAnnotation
public class EmployeeManagementView {
    // 视图字段定义
    @TextField(label = "员工姓名")
    private String employeeName;
    
    // 视图入口方法
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE}
    )
    public ResultModel<List<Employee>> loadEmployeeData() {
        // 加载员工数据
        return employeeService.loadEmployees();
    }
}
```

### 5.2 事件绑定机制
Page在构建时会将组件的Event定义实例化并与视图/元素进行绑定：

``java
// 视图类中的事件定义
@FormViewAnnotation
public class EmployeeManagementView {
    // 按钮组件定义
    @ButtonAnnotation(caption = "搜索")
    private SearchButton searchButton;
    
    // 事件绑定方法
    @APIEventAnnotation(
        bindAction = "SEARCH",
        beforeInvoke = CustomBeforInvoke.BUSY,
        onExecuteSuccess = {CustomOnExecueSuccess.REFRESH}
    )
    public ResultModel<Boolean> searchEmployees(@RequestParam String keyword) {
        // 搜索员工逻辑
        return employeeService.searchEmployees(keyword);
    }
}
```

### 5.3 参数汇聚到Page上下文
Page会根据视图入口方法的参数，将参数汇聚到PageCtx页面当前环境中：

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
        
        // 参数会自动汇聚到Page上下文环境
        // PageCtx中会包含departmentId和employeeName参数
        return view;
    }
}
```

### 5.4 API通讯组件编译
Page会将视图所需的服务端交互从视图绑定的服务类中获取可Web访问的method方法，并将其编译为页面的Ajax通讯组件：

``java
// 服务类定义
@Service
@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    // 可Web访问的服务方法
    @GetMapping("/list")
    @Aggregation
    public ResultModel<List<Employee>> loadEmployees() {
        // 加载员工列表
        return employeeRepository.findAll();
    }
    
    @PostMapping("/search")
    @Aggregation
    public ResultModel<List<Employee>> searchEmployees(@RequestBody SearchParams params) {
        // 搜索员工
        return employeeRepository.search(params);
    }
    
    @PostMapping("/update")
    @Aggregation
    public ResultModel<Boolean> updateEmployee(@RequestBody Employee employee) {
        // 更新员工信息
        return employeeRepository.update(employee);
    }
}

// Page构建时会自动将这些方法编译为Ajax通讯组件
// 前端可以直接调用这些API进行数据交互
```

## 6. Page生命周期管理

### 6.1 初始化阶段
1. 加载Page配置信息
2. 初始化环境变量
3. 创建视图组件实例
4. 建立组件间关系

### 6.2 运行阶段
1. 处理用户交互事件
2. 协调视图组件数据更新
3. 管理组件状态变化
4. 处理消息通知

### 6.3 销毁阶段
1. 释放视图组件资源
2. 清理环境变量
3. 断开数据连接
4. 保存页面状态（如需要）

## 7. Page服务规范

### 7.1 PageDataService接口
``java
public interface PageDataService<T> {
    /**
     * 初始化Page数据
     */
    void initializePage(T page);
    
    /**
     * 加载Page环境变量
     */
    Map<String, Object> loadEnvironmentVariables();
    
    /**
     * 处理Page事件
     */
    void handlePageEvent(PageEvent event);
    
    /**
     * 保存Page状态
     */
    void savePageState(T page);
}
```

### 7.2 Page事件处理
``java
// 订单管理视图入口
@RestController
@RequestMapping("/order/management")
public class OrderManagementViewController {
    @Autowired
    private OrderManagementPageService pageService;
    
    // 视图入口方法具有Web可访问性
    @GetMapping
    public OrderManagementView getOrderManagementView() {
        // 返回视图对象，Web容器自动包装为Page
        OrderManagementView view = new OrderManagementView();
        // 页面加载事件处理
        pageService.initializePage(view);
        return view;
    }
    
    @PostMapping("/view/change")
    public void onViewChange(@RequestParam String viewName) {
        // 视图切换事件处理
        pageService.handleViewChange(viewName);
    }
    
    @PostMapping("/data/update")
    public void onDataUpdate(@RequestBody Object data) {
        // 数据更新事件处理
        pageService.handleDataUpdate(data);
    }
}
```

## 8. Page与BAR服务的关系

### 8.1 BAR服务在Page中的作用
BAR服务作为Page的一部分，提供导航和菜单功能。虽然Page对象是动态生成的，但BAR服务仍然可以与Page集成：

``java
// BAR服务类
@Service
@RestController
@RequestMapping("/system/bar")
public class SystemBARService {
    // BAR服务方法具有Web可访问性
    
    @BARAnnotation(
        position = BARPosition.TOP,
        items = SystemBARItems.class
    )
    public BARView getBARView() {
        // 返回BAR视图对象
        return new BARView();
    }
}

// 视图入口类
@RestController
@RequestMapping("/system/management")
public class SystemManagementViewController {
    // 视图入口方法具有Web可访问性
    @GetMapping
    public SystemManagementView getSystemManagementView() {
        // 返回视图对象，Web容器自动包装为Page
        return new SystemManagementView();
    }
}
```

### 8.2 BAR与Page的集成
``java
public enum SystemBARItems implements BARItem {
    USER_MANAGEMENT("用户管理", "fas fa-users", UserManagementView.class),
    ROLE_MANAGEMENT("角色管理", "fas fa-user-tag", RoleManagementView.class),
    PERMISSION_MANAGEMENT("权限管理", "fas fa-key", PermissionManagementView.class);
    
    private final String title;
    private final String icon;
    private final Class<?> targetView;
    
    SystemBARItems(String title, String icon, Class<?> targetView) {
        this.title = title;
        this.icon = icon;
        this.targetView = targetView;
    }
    
    @Override
    public String getTitle() { return title; }
    
    @Override
    public String getIcon() { return icon; }
    
    @Override
    public Class<?> getTargetView() { return targetView; }
}
```

## 9. 最佳实践

### 9.1 设计原则
1. **单一职责原则**：每个Page应有明确的业务职责
2. **高内聚低耦合**：Page内部组件高内聚，Page间低耦合
3. **可复用性**：设计可复用的Page模板和组件

### 9.2 实现规范
1. **注解使用规范**：正确使用Page相关注解
2. **数据一致性**：确保Page内各组件数据的一致性
3. **性能优化**：合理使用懒加载和缓存机制
4. **API设计规范**：确保所有服务方法都具有Web可访问性并声明@Aggregation注解

### 9.3 用户体验
1. **响应式设计**：适配不同设备和屏幕尺寸
2. **状态反馈**：提供清晰的操作状态反馈
3. **导航便利**：提供便捷的页面导航和返回机制
4. **事件绑定清晰**：明确标注组件与服务方法的绑定关系

## 10. Page组合与嵌套

### 10.1 Page组合
多个Page可以通过导航关系组合使用。虽然Page对象是动态生成的，但可以通过视图入口的关联实现Page组合：

``java
// 主控制台视图入口
@RestController
@RequestMapping("/dashboard")
public class MainDashboardViewController {
    // 视图入口方法具有Web可访问性
    @GetMapping
    public MainDashboardView getMainDashboardView() {
        // 返回视图对象，Web容器自动包装为Page
        return new MainDashboardView();
    }
}

public class MainDashboardView {
    // 包含多个子页面的导航入口
    private List<ViewNavigation> subViews;
    
    // 主要内容区域
    private MainContentView mainContent;
}
```

### 10.2 Page嵌套
在复杂应用中，可以通过视图嵌套实现Page的嵌套关系：

``java
// 综合管理平台视图入口
@RestController
@RequestMapping("/integrated/management")
public class IntegratedManagementViewController {
    // 视图入口方法具有Web可访问性
    @GetMapping
    public IntegratedManagementView getIntegratedManagementView() {
        // 返回视图对象，Web容器自动包装为Page
        return new IntegratedManagementView();
    }
}

public class IntegratedManagementView {
    // 嵌套的子视图
    private UserManagementView userManagementView;
    private SystemManagementView systemManagementView;
    private ReportManagementView reportManagementView;
}
```

