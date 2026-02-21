# 3.4 Tabs和Group规范

## 1. 概述

Tabs和Group是ooder平台中用于组织和管理复杂界面的重要容器组件。Tabs通过标签页的方式组织内容，Group通过分组框的方式对相关元素进行归类，两者都能有效提升界面的结构化程度和用户体验。

## 2. Tabs组件规范

### 2.1 核心概念
Tabs组件允许用户在不同的视图或内容面板之间切换，通过标签页的形式组织相关内容，减少界面的复杂性。

### 2.2 @TabsAnnotation注解
Tabs组件的核心注解，用于定义Tabs视图的基本属性。

```java
@TabsAnnotation(
    customService = {UserProfileTabsService.class},
    tabPosition = TabPosition.TOP,
    closable = false,
    animated = true
)
public class UserProfileTabsView {
    // 枚举定义
}
```

### 2.3 Tabs枚举实现
Tabs通过枚举类型定义各个标签页的内容和属性。

```java
public enum UserProfileTab implements TabItem {
    BASIC_INFO("基本信息", "fas fa-user", BasicInfoService.class),
    CONTACT_INFO("联系方式", "fas fa-phone", ContactInfoService.class),
    SECURITY_SETTINGS("安全设置", "fas fa-shield-alt", SecuritySettingsService.class);
    
    private final String title;
    private final String icon;
    private final Class<? extends Service> serviceClass;
    
    UserProfileTab(String title, String icon, Class<? extends Service> serviceClass) {
        this.title = title;
        this.icon = icon;
        this.serviceClass = serviceClass;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getIcon() {
        return icon;
    }
    
    @Override
    public Class<? extends Service> getServiceClass() {
        return serviceClass;
    }
}
```

## 3. Group组件规范

### 3.1 核心概念
Group组件用于将相关的表单元素或控件组织在一起，通过分组框的形式提高界面的可读性和逻辑性。

### 3.2 @GroupAnnotation注解
Group组件的核心注解，用于定义Group视图的基本属性。

```java
@GroupAnnotation(
    title = "用户基本信息",
    borderType = BorderType.inset,
    collapsible = true,
    collapsed = false
)
public class UserBasicInfoGroup {
    // 字段定义
}
```

### 3.3 Group字段组织
Group内部可以包含各种表单字段和子组件。

```java
@GroupAnnotation(title = "用户基本信息")
public class UserBasicInfoGroup {
    @TextField(label = "姓名", required = true)
    private String name;
    
    @TextField(label = "邮箱", required = true)
    private String email;
    
    @DateField(label = "出生日期")
    private Date birthDate;
    
    // getter和setter方法
}
```

## 4. Tabs与Group的组合使用

### 4.1 层级嵌套
Tabs和Group可以相互嵌套，形成复杂的界面结构。

```java
@TabsAnnotation
public enum SystemConfigTabs implements TabItem {
    USER_MANAGEMENT("用户管理", null, UserManagementGroup.class),
    SYSTEM_SETTINGS("系统设置", null, SystemSettingsGroup.class);
}
```

### 4.2 数据联动
Tabs切换时可以触发Group内容的更新，实现数据联动。

## 5. Tabs子类型规范

### 5.1 ButtonViews
ButtonViews是Tabs的一种特殊形式，通过按钮组的方式切换内容。

```java
@ButtonViewsAnnotation(
    viewType = ButtonViewType.BUTTON_GROUP,
    customService = {ActionButtonsService.class}
)
public enum ActionButtons implements TabItem {
    SAVE("保存", "fas fa-save", SaveService.class),
    SUBMIT("提交", "fas fa-paper-plane", SubmitService.class),
    CANCEL("取消", "fas fa-times", CancelService.class);
}
```

### 5.2 FoldingTabs
FoldingTabs支持折叠功能的Tabs组件，节省界面空间。

```java
@FoldingTabsAnnotation(
    customService = {CollapsibleTabsService.class},
    foldable = true,
    defaultFolded = false
)
public enum CollapsibleTabs implements TabItem {
    SECTION_ONE("第一部分", "fas fa-section", SectionOneService.class),
    SECTION_TWO("第二部分", "fas fa-section", SectionTwoService.class);
}
```

### 5.3 Stacks
Stacks是一种堆叠式Tabs，支持内容的层叠展示。

```java
@StacksAnnotation(
    customService = {StackedViewsService.class},
    stackType = StackType.VERTICAL
)
public enum StackedViews implements TabItem {
    LAYER_ONE("图层一", "fas fa-layer", LayerOneService.class),
    LAYER_TWO("图层二", "fas fa-layer", LayerTwoService.class);
}
```

## 6. 服务规范

### 6.1 Tabs服务
```java
@Service
public class UserProfileTabsService implements TabsDataService {
    @Override
    public List<TabItem> loadTabs() {
        return Arrays.asList(UserProfileTab.values());
    }
}
```

### 6.2 Group服务
```java
@Service
public class UserBasicInfoGroupService implements GroupDataService {
    @Override
    public void loadData(UserBasicInfoGroup group) {
        // 加载组数据
    }
}
```

## 7. 最佳实践

### 7.1 界面设计原则
- Tabs适用于平级内容的切换
- Group适用于相关元素的归类
- 避免过深的嵌套层级
- 保持标签页标题简洁明了

### 7.2 性能优化
- 延迟加载非活跃标签页内容
- 合理使用缓存机制
- 优化Group内容渲染性能

### 7.3 用户体验
- 提供清晰的视觉层次
- 保持操作一致性
- 支持键盘快捷键导航
- 明确标识当前激活状态