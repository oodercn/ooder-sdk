# ooder平台单一视图规范：视图、数据、结构

## 1. 概述

单一视图规范定义了ooder平台中独立视图的设计原则、数据结构和组织方式。单一视图是平台中最基础的UI单元，具有明确的职责和清晰的数据结构。

## 2. 视图类型规范

### 2.1 表单视图
表单视图用于数据录入和展示，使用@FormAnnotation或@FormViewAnnotation注解定义。

**核心特征：**
- 使用@FormAnnotation定义表单布局和属性
- 支持字段分组和布局控制
- 可配置边框类型、列数、行数等属性

**示例：**
```java
@FormAnnotation(
    borderType = BorderType.inset,
    col = 2,
    row = 7,
    customService = {AttendanceCheckInService.class}
)
public class AttendanceCheckInView {
    // 字段定义
}
```

### 2.2 列表视图
列表视图用于数据展示和操作，使用@TreeGridAnnotation或@TreeGridViewAnnotation注解定义。

**核心特征：**
- 遵循列表视图单一性原则：一个视图类应仅定义一种类型的视图
- 配置默认的@Uid作为行主键
- 支持列配置和数据增强

**示例：**
```java
@TreeGridAnnotation(
    customService = {AttendanceRecordListService.class},
    showHeader = true,
    colSortable = true
)
public class AttendanceRecordTreeGridView {
    // 字段定义
}
```

### 2.3 门户视图
门户视图用于整合多个信息模块，使用@FormAnnotation注解定义。

## 3. 视图数据结构规范

### 3.1 视图对象的双重角色
视图对象在ooder平台中具有双重角色：
1. **数据传输对象(DTO)**：作为数据载体，用于前后端数据传输
2. **视图配置载体**：通过注解定义视图的外观、行为和交互

### 3.2 字段注解规范
视图中的字段通过不同注解定义其行为和外观：

**输入控件注解：**
- @InputAnnotation：输入框配置
- @DatePickerAnnotation：日期选择器配置
- @ComboBoxAnnotation：下拉框配置
- @ButtonAnnotation：按钮配置

**列表增强注解：**
- @CustomListAnnotation：自定义列表配置，用于增强枚举字段的数据能力

### 3.3 数据一致性规范
- 确保视图对象在前后端传输过程中保持数据一致性
- 使用@JSONField控制枚举序列化格式
- 合理设计字段类型和验证规则

## 4. 视图结构规范

### 4.1 视图类结构
视图类应遵循以下结构规范：
1. 类注解定义视图类型和属性
2. 字段定义及相应注解
3. 必要的getter/setter方法
4. 可选的业务逻辑方法

### 4.2 注解使用规范
- @CustomAnnotation用于定义字段的外观属性（caption、index、imageClass等）
- 视图类型注解定义视图的整体行为
- 菜单注解定义工具栏和底部栏配置

### 4.3 视图与服务分离
- 视图类仅负责UI展示和数据结构定义
- 业务逻辑由独立的服务类处理
- 通过customService属性关联服务类

## 5. 视图设计原则

### 5.1 单一职责原则
- 每个视图类应仅负责一种类型的展示或录入任务
- 避免在一个视图类中混合不同类型的UI元素

### 5.2 可复用性原则
- 设计通用的视图组件，提高代码复用率
- 通过注解参数化配置，适应不同场景需求

### 5.3 可维护性原则
- 保持视图类结构清晰，易于理解和修改
- 合理使用注解，避免过度复杂化

## 6. 最佳实践

### 6.1 字段设计
- 合理分组相关字段
- 使用适当的输入控件注解
- 为枚举字段提供数据增强支持

### 6.2 布局设计
- 根据内容密度选择合适的列数和行数
- 合理配置边框和间距
- 考虑不同设备的显示效果

### 6.3 数据处理
- 正确使用@Uid注解标识行主键
- 合理设计数据验证规则
- 确保数据传输的一致性和完整性