package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum ModuleMethod implements Enumstype {
    load("load", "加载模块", "cls", "onEnd", "lang", "theme", "showUI", "parent", "subId", "onCreated", "onDomReady"),
    refresh("refresh", "刷新模块", "properties", "events", "host", "onEnd", "lang", "theme", "showUI", "parent", "subId", "onCreated", "onDomReady"),
    destroy("destroy", "销毁模块", "ignoreEffects", "purgeNow"),
    show("show", "显示模块", "parent", "subId", "onCreated", "onDomReady"),
    hide("hide", "隐藏模块", "ignoreEffects"),
    setHost("setHost", "设置宿主", "value", "alias"),
    getHost("getHost", "获取宿主"),
    setAlias("setAlias", "设置别名", "alias"),
    getAlias("getAlias", "获取别名"),
    setProperties("setProperties", "设置属性", "obj", "syncUI", "force"),
    getProperties("getProperties", "获取属性"),
    getProperty("getProperty", "获取属性值", "key"),
    setProperty("setProperty", "设置属性值", "key", "value", "syncUI", "force"),
    getUIValue("getUIValue", "获取UI值"),
    setUIValue("setUIValue", "设置UI值", "value", "flag", "ignoreErr", "fromEvent"),
    getValue("getValue", "获取值"),
    setValue("setValue", "设置值", "value", "flag", "ignoreErr", "fromEvent"),
    resetValue("resetValue", "重置值", "fromEvent"),
    updateValue("updateValue", "更新值"),
    isDirtied("isDirtied", "是否已修改"),
    checkValid("checkValid", "检查有效性"),
    checkRequired("checkRequired", "检查必填项"),
    getFormValues("getFormValues", "获取表单值", "isAll"),
    getAllFormValues("getAllFormValues", "获取所有表单值", "isAll"),
    setFormValues("setFormValues", "设置表单值", "obj", "useNull"),
    clearErrors("clearErrors", "清除错误"),
    getChildren("getChildren", "获取子元素"),
    append("append", "追加元素", "target", "subId", "base", "callback"),
    removeChildren("removeChildren", "移除子元素", "subId", "destroy", "noRedraw"),
    destroyChildren("destroyChildren", "销毁子元素", "subId", "noRedraw"),
    getContainer("getContainer", "获取容器", "subId"),
    setCustomStyle("setCustomStyle", "设置自定义样式", "obj", "target"),
    setCustomAttr("setCustomAttr", "设置自定义属性", "obj", "target"),
    getCustomStyle("getCustomStyle", "获取自定义样式", "target"),
    getCustomAttr("getCustomAttr", "获取自定义属性", "target"),
    serialize("serialize", "序列化", "pure", "host", "ignoreHost", "ignoreTpl", "ignoreChildren", "ignoreEvents", "ignoreEnv", "withValue", "withAlias", "withClass", "withHost", "withTpl", "withChildren", "withEvents", "withEnv", "withPrfId", "withPrfAlias"),
    toHtml("toHtml", "转换为HTML", "tpl", "data", "subId", "tplMarker", "htmlMarker"),
    render("render", "渲染", "force", "host", "subId", "callback"),
    showView("showView", "显示视图", "viewId", "callback", "keepState"),
    getView("getView", "获取视图", "viewId"),
    addView("addView", "添加视图", "viewId", "view", "before"),
    removeView("removeView", "移除视图", "viewId"),
    activate("activate", "激活"),
    getRoot("getRoot", "获取根元素"),
    getRootNode("getRootNode", "获取根节点"),
    iniComponents("iniComponents", "初始化组件", "host", "subId", "callback"),
    iniExModules("iniExModules", "初始化扩展模块", "callback"),
    getModule("getModule", "获取模块"),
    getChildModule("getChildModule", "获取子模块", "alias"),
    addModule("addModule", "添加模块", "alias", "module", "options"),
    removeModule("removeModule", "移除模块", "alias"),
    getCom("getCom", "获取组件", "alias"),
    addCom("addCom", "添加组件", "alias", "com", "options"),
    removeCom("removeCom", "移除组件", "alias"),
    getInstance("getInstance", "获取实例", "alias"),
    getInstances("getInstances", "获取所有实例");

    private final String type;
    private final String name;
    private final String[] parameters;

    ModuleMethod(String type, String name, String... parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }
}