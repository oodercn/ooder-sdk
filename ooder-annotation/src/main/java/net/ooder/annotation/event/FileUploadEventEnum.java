package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * FileUpload组件事件枚举
 * 对应JS中的ood.UI.FileUpload.EventHandlers定义
 */
public enum FileUploadEventEnum implements EventKey {
    // 上传文件事件
    uploadfile("uploadfile", "上传文件", "profile", "eventType", "item", "response"),
    
    // 上传失败事件
    uploadfail("uploadfail", "上传失败", "profile", "eventType", "item", "response"),
    
    // 上传完成事件
    uploadcomplete("uploadcomplete", "上传完成", "profile", "eventType", "item", "response"),
    
    // 上传进度事件
    uploadprogress("uploadprogress", "上传进度", "profile", "eventType", "item", "response"),
    
    // 父类事件
    beforeRender("beforeRender", "渲染前", "profile"),
    onRender("onRender", "渲染时", "profile"),
    onLayout("onLayout", "布局时", "profile"),
    onResize("onResize", "调整大小", "profile", "width", "height"),
    onMove("onMove", "移动时", "profile", "left", "top", "right", "bottom"),
    onDock("onDock", "停靠时", "profile", "region"),
    beforePropertyChanged("beforePropertyChanged", "属性变更前", "profile", "name", "value", "ovalue"),
    afterPropertyChanged("afterPropertyChanged", "属性变更后", "profile", "name", "value", "ovalue"),
    beforeAppend("beforeAppend", "添加前", "profile", "child"),
    afterAppend("afterAppend", "添加后", "profile", "child"),
    beforeRemove("beforeRemove", "移除前", "profile", "child", "subId", "bdestroy"),
    afterRemove("afterRemove", "移除后", "profile", "child", "subId", "bdestroy"),
    onDestroy("onDestroy", "销毁时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),
    afterDestroy("afterDestroy", "销毁后", "profile"),
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos");

    private String event;
    private String[] params;
    private String name;

    FileUploadEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
}