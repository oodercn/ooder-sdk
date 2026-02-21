# ooder平台Combo组件专题手册

## 1. 概述

Combo组件是ooder平台中一种高级复合输入组件，它提供了多种输入类型和交互方式，能够满足不同业务场景下的数据录入需求。Combo组件基于COMBOINPUT基础组件构建，通过不同的注解配置实现各种功能。

## 2. Combo组件体系结构

### 2.1 核心概念

Combo组件体系由以下几个核心部分组成：

1. **COMBOINPUT基础组件**：所有Combo组件的基础，定义在[ComponentType.COMBOINPUT](src/main/java/net/ooder/esd/annotation/ui/ComponentType.java)
2. **ComboInputType枚举**：定义了Combo组件支持的各种输入类型
3. **CustomViewType.COMBOBOX**：Combo组件的视图类型
4. **多种注解类**：针对不同输入类型的专用注解

### 2.2 组件层次结构

```
UI (基础面板)
└── INPUT (输入域)
    └── COMBOINPUT (复合输入框)
```

## 3. ComboInputType输入类型详解

ComboInputType枚举定义了Combo组件支持的所有输入类型，按功能分类如下：

### 3.1 输入框类型 (ComboType.input)
- [input](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：普通输入框
- [combobox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：下拉输入框
- [password](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：密码输入框
- [radiobox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：Radio复选框
- [checkbox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：单选框
- [file](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：文件上传
- [textarea](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：多行文本输入
- [label](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：标签显示
- [date](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：日期输入框
- [profile](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：配置文件输入
- [image](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：图片输入
- [text](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：文本输入
- [time](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：时间输入框
- [datetime](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：日期时间输入框
- [color](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：颜色选择器
- [auto](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：自适应输入框

### 3.2 数字类型 (ComboType.number)
- [spin](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：数字滚动输入
- [counter](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：左右滚动输入
- [currency](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：货币输入
- [number](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：数字输入

### 3.3 列表类型 (ComboType.list)
- [listbox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：下拉列表
- [getter](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：动态字典列表
- [helpinput](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：帮助索引输入

### 3.4 按钮类型 (ComboType.button)
- [button](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：普通按钮
- [cmd](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：命令按钮
- [dropbutton](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：下拉按钮
- [cmdbox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：按钮组

### 3.5 模块类型 (ComboType.module)
- [popbox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：弹出框模块

### 3.6 其他类型 (ComboType.other)
- [none](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：默认类型
- [split](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)：分隔符

## 4. Combo注解详解

### 4.1 @ComboAnnotation 基础注解

[@ComboAnnotation](src/main/java/net/ooder/esd/annotation/field/ComboAnnotation.java) 是Combo组件的基础注解，用于定义一个Combo输入组件。

**属性说明：**
- [inputType](src/main/java/net/ooder/esd/annotation/field/ComboAnnotation.java)：指定输入类型，默认为[ComboInputType.none](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)

**使用示例：**
```java
@ComboAnnotation(inputType = ComboInputType.input)
@CustomAnnotation(caption = "用户名", index = 1)
private String username;
```

### 4.2 @ComboBoxAnnotation 下拉框注解

[@ComboBoxAnnotation](src/main/java/net/ooder/esd/annotation/field/ComboBoxAnnotation.java) 用于定义下拉输入框组件。

**属性说明：**
- [listKey](src/main/java/net/ooder/esd/annotation/field/ComboBoxAnnotation.java)：列表键值
- [dropImageClass](src/main/java/net/ooder/esd/annotation/field/ComboBoxAnnotation.java)：下拉图标CSS类
- [dropListWidth](src/main/java/net/ooder/esd/annotation/field/ComboBoxAnnotation.java)：下拉列表宽度
- [dropListHeight](src/main/java/net/ooder/esd/annotation/field/ComboBoxAnnotation.java)：下拉列表高度

**使用示例：**
```java
@ComboBoxAnnotation
@CustomListAnnotation(enumClass = DepartmentType.class)
@CustomAnnotation(caption = "部门", index = 2)
private String department;
```

### 4.3 @ComboInputAnnotation 输入框注解

[@ComboInputAnnotation](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java) 用于定义普通输入框组件。

**属性说明：**
- [expression](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：表达式
- [imageBgSize](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：背景图片大小
- [imageClass](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：图标CSS类
- [iconFontCode](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：图标字体编码
- [unit](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：单位
- [units](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：多个单位
- [tips](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：提示信息
- [commandBtn](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：命令按钮
- [labelCaption](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：标签标题
- [inputType](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：输入类型，默认为[ComboInputType.input](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)
- [inputReadonly](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)：是否只读

**使用示例：**
```java
@ComboInputAnnotation(
    inputType = ComboInputType.input,
    tips = "请输入用户名",
    labelCaption = "用户名"
)
@CustomAnnotation(caption = "用户名", index = 1)
private String username;
```

### 4.4 @ComboNumberAnnotation 数字输入注解

[@ComboNumberAnnotation](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java) 用于定义数字输入组件。

**属性说明：**
- [precision](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：精度
- [decimalSeparator](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：小数点分隔符
- [forceFillZero](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：是否强制补零
- [trimTailZero](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：是否去除尾部零
- [groupingSeparator](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：分组分隔符
- [increment](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：增量值
- [min](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：最小值
- [max](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：最大值
- [numberTpl](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：数字模板
- [currencyTpl](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)：货币模板

**使用示例：**
```java
@ComboNumberAnnotation(
    precision = 2,
    currencyTpl = "¥ *",
    min = "0"
)
@CustomAnnotation(caption = "价格", index = 3)
private Double price;
```

### 4.5 @ComboPopAnnotation 弹出框注解

[@ComboPopAnnotation](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java) 用于定义弹出框组件。

**属性说明：**
- [parentID](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：父级ID
- [cachePopWnd](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：是否缓存弹窗
- [width](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：宽度
- [height](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：高度
- [src](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：资源路径
- [dynLoad](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：是否动态加载
- [inputType](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：输入类型，默认为[ComboInputType.popbox](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)
- [bindClass](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)：绑定类

**使用示例：**
```java
@ComboPopAnnotation(
    inputType = ComboInputType.popbox,
    width = "30em",
    height = "20em"
)
@CustomAnnotation(caption = "选择用户", index = 4)
private String userSelection;
```

## 5. Combo组件使用规范

### 5.1 基本使用原则

1. **选择合适的注解**：根据业务需求选择对应的Combo注解类型
2. **合理配置属性**：只配置必要的属性，避免冗余配置
3. **配合@CustomAnnotation使用**：必须与[@CustomAnnotation](src/main/java/net/ooder/esd/annotation/CustomAnnotation.java)配合使用来定义显示属性

### 5.2 组合使用示例

```java
// 下拉选择框
@ComboBoxAnnotation
@CustomListAnnotation(enumClass = UserType.class)
@CustomAnnotation(caption = "用户类型", index = 1)
private UserType userType;

// 数字输入框
@ComboNumberAnnotation(
    precision = 2,
    min = "0",
    currencyTpl = "$ *"
)
@CustomAnnotation(caption = "薪资", index = 2)
private Double salary;

// 弹出选择框
@ComboPopAnnotation(
    inputType = ComboInputType.popbox,
    width = "40em",
    height = "30em"
)
@CustomAnnotation(caption = "部门选择", index = 3)
private String department;

// 普通输入框
@ComboInputAnnotation(
    inputType = ComboInputType.input,
    tips = "请输入姓名"
)
@CustomAnnotation(caption = "姓名", index = 4)
private String name;
```

## 6. 最佳实践

### 6.1 性能优化建议

1. **合理使用缓存**：对于弹出框等组件，合理使用[cachePopWnd](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)属性提高性能
2. **动态加载**：对于内容较多的组件，使用[dynLoad](src/main/java/net/ooder/esd/annotation/field/ComboPopAnnotation.java)属性实现按需加载
3. **限制范围**：合理设置[min](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)和[max](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)属性限制输入范围

### 6.2 用户体验优化

1. **提供提示信息**：使用[tips](src/main/java/net/ooder/esd/annotation/field/ComboInputAnnotation.java)属性为用户提供输入指导
2. **合理设置默认值**：为组件设置合适的默认值
3. **使用合适的模板**：对于数字和货币类型，使用[numberTpl](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)和[currencyTpl](src/main/java/net/ooder/esd/annotation/field/ComboNumberAnnotation.java)属性优化显示效果

## 7. 服务示例

以下是一个使用Combo组件的完整服务示例，展示了如何正确处理异常：

```java
@Aggregation(type = AggregationType.API, userSpace = UserSpace.SYS)
@RestController
@RequestMapping("/user")
@Service
public class UserService {
    
    @APIEventAnnotation(
        customRequestData = {RequestPathEnum.SPA_PROJECTNAME},   
        onExecuteSuccess = {CustomOnExecueSuccess.MESSAGE},
        beforeInvoke = CustomBeforInvoke.BUSY
    )
    @CustomAnnotation(index = 0, caption = "保存用户信息", imageClass = "fa-solid fa-save")
    @PostMapping("/save")
    @ResponseBody
    public ResultModel<Boolean> saveUserInfo(@RequestBody UserInfoView view) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        
        try {
            // 实现具体的保存逻辑
            // 验证Combo字段数据
            if (view.getUsername() == null || view.getUsername().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            // 保存用户信息
            boolean success = userService.saveUser(view);
            resultModel.setData(success);
            resultModel.setRequestStatus(1); // 设置成功状态
        } catch (Exception e) {
            // 发生错误时返回ErrorResultModel封装的错误信息
            ErrorResultModel<Boolean> errorResult = new ErrorResultModel<>();
            errorResult.setErrdes(e.getMessage());
            errorResult.setErrcode(1000); // 设置默认错误码
            errorResult.setRequestStatus(-1); // 设置错误状态
            return errorResult;
        }
        
        return resultModel;
    }
    
    @GetMapping("/departments")
    @ResponseBody
    public ListResultModel<List<Department>> getDepartments() {
        ListResultModel<List<Department>> resultModel = new ListResultModel<>();
        
        try {
            List<Department> departments = departmentService.getAllDepartments();
            resultModel.setData(departments);
            resultModel.setRequestStatus(1);
        } catch (Exception e) {
            // 发生错误时返回ErrorListResultModel封装的错误信息
            ErrorListResultModel<List<Department>> errorResult = new ErrorListResultModel<>(e.getMessage());
            errorResult.setErrcode(1000);
            errorResult.setRequestStatus(-1);
            return errorResult;
        }
        
        return resultModel;
    }
}
```

## 8. 注意事项

1. Combo组件必须与[@CustomAnnotation](src/main/java/net/ooder/esd/annotation/CustomAnnotation.java)配合使用
2. 不同类型的Combo注解支持的[ComboInputType](src/main/java/net/ooder/esd/annotation/ui/ComboInputType.java)不同，需要根据具体需求选择
3. 对于列表类型的组件，通常需要配合[@CustomListAnnotation](src/main/java/net/ooder/esd/annotation/CustomListAnnotation.java)使用