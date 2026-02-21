# ooder服务参数设计规范

## 概述

在ooder平台中，服务方法的参数设计应根据具体业务场景选择合适的参数类型。参数可以分为简单参数和复杂视图对象参数两种类型。

对于列表视图，被@Uid注解标记的字段应作为默认回参添加到所有服务方法中，以确保数据操作的准确性和一致性。

view参数只有在显示声明需要全部字段的时候才会使用，其他情况不需要该参数，只使用简单参数即可。

## 简单参数使用场景

### 1. 重置操作
重置操作通常不需要复杂的视图数据，只需要简单的标识参数：
```java
@PostMapping("/reset")
@ResponseBody
public ResultModel<Boolean> resetQueryConditions(@RequestParam(required = false) String resetFlag) {
    // 实现重置逻辑
}
```

### 2. 刷新操作
刷新操作通常只需要少量筛选参数：
```java
@GetMapping("/refresh")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> refreshAttendanceRecords(
        @RequestParam(required = false) String employeeId, 
        @RequestParam(required = false) String startDate) {
    // 根据简单参数刷新数据
}
```

### 3. 根据主键查询
根据主键或唯一标识查询数据时，只需要传递主键参数：
```java
@GetMapping("/employee/{employeeId}")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> getAttendanceRecordsByEmployeeId(@PathVariable String employeeId) {
    // 根据员工ID查询数据
}
```

### 4. 状态更新操作
更新特定记录的状态时，只需要ID和状态参数：
```java
@PutMapping("/status")
@ResponseBody
public ResultModel<Boolean> updateAttendanceStatus(
        @RequestParam String recordId, 
        @RequestParam String status) {
    // 更新考勤记录状态
}
```

## 复杂视图对象参数使用场景

### 1. 表单提交操作
表单提交通常需要完整的视图数据：
```java
@PostMapping("/submit")
@ResponseBody
public ResultModel<Boolean> submitAttendanceData(@RequestBody AttendanceCheckInView view) {
    // 处理完整的表单数据
}
```

### 2. 复杂查询操作
复杂查询需要多个查询条件时，使用视图对象：
```java
@PostMapping("/search")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> queryAttendanceRecords(@RequestBody AttendanceQueryListView view) {
    // 根据视图中的多个查询条件进行复杂查询
}
```

### 3. 批量操作
批量操作需要处理多个数据项时，使用视图对象：
```java
@PostMapping("/batchUpdate")
@ResponseBody
public ResultModel<Boolean> batchUpdateAttendanceRecords(@RequestBody AttendanceBatchUpdateView view) {
    // 批量更新考勤记录
}
```

### 4. 需要全部字段的场景
只有在明确需要全部字段数据时才使用view参数：
```java
@PostMapping("/detailedProcess")
@ResponseBody
public ResultModel<Boolean> detailedProcess(@RequestBody AttendanceQueryListView view) {
    // 需要访问视图中的所有字段数据
}
```

## 参数设计原则

### 1. 最小化原则
- 只传递必要的参数，避免传递不必要的数据
- 简单操作使用简单参数，复杂操作使用视图对象

### 2. 一致性原则
- 相同类型的业务操作应保持参数设计的一致性
- 同一服务类中的相似方法应采用相似的参数设计

### 3. 可读性原则
- 参数命名应清晰表达其用途
- 方法注释应详细说明参数的含义和用途

### 4. 性能原则
- 简单参数传输效率更高
- 复杂视图对象在必要时才使用

## 最佳实践

### 1. 根据业务场景选择参数类型
```java
// 适合使用简单参数的场景
@GetMapping("/count")
@ResponseBody
public ResultModel<Integer> getAttendanceCount(@RequestParam(required = false) String department) {
    // 获取考勤记录数量
}

// 适合使用视图对象的场景
@PostMapping("/detailedSearch")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> detailedSearch(@RequestBody AttendanceQueryListView view) {
    // 复杂查询
}
```

### 2. 合理使用@RequestParam和@PathVariable
```java
// 使用@RequestParam处理可选参数
@GetMapping("/list")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> listAttendanceRecords(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String employeeName) {
    // 分页查询
}

// 使用@PathVariable处理路径参数
@GetMapping("/employee/{employeeId}/month/{month}")
@ResponseBody
public ListResultModel<List<AttendanceRecord>> getMonthlyAttendance(
        @PathVariable String employeeId, 
        @PathVariable String month) {
    // 获取员工月度考勤
}
```

### 3. 混合使用简单参数和视图对象
```java
// 在某些场景下可以混合使用
@PostMapping("/conditionalSubmit")
@ResponseBody
public ResultModel<Boolean> submitWithCondition(
        @RequestParam String submitType,
        @RequestBody AttendanceCheckInView view) {
    // 根据提交类型和完整表单数据进行处理
}
```

## 注意事项

1. 所有服务方法都必须遵循ooder服务规范，确保Web可访问性
2. 简单参数和视图对象参数都必须通过JSON格式传输
3. 使用[@RequestBody](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/attendance/annotation/RequestBody.java)注解处理视图对象参数
4. 使用[@RequestParam](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/esd/annotation/RequestParam.java)和[@PathVariable](file:///E:/ooder-gitee/ooder-annotation/src/main/java/net/ooder/attendance/annotation/PathVariable.java)注解处理简单参数
5. 所有参数都应进行必要的验证和异常处理
6. 列表视图中被@Uid注解标记的字段应作为默认回参添加到所有服务方法中
7. 在需要标识特定记录的操作中，应优先使用@Uid字段作为参数
8. 参数默认值使用规范：默认不显式添加@RequestParam(required = false)，应使用参数默认值机制，以保持代码简洁和易读性
