package net.ooder.annotation.ui;


import net.ooder.annotation.IconEnumstype;

public enum CommandBtnType implements IconEnumstype {
    none("none", CustomImageType.notch),
    sava("保存", CustomImageType.save),
    delete("删除", CustomImageType.delete),
    add("添加", CustomImageType.add),
    remove("移除", CustomImageType.remove),
    pop("弹出", CustomImageType.pop),
    select("选择", CustomImageType.select),
    search("查询", CustomImageType.search),
    refresh("刷新", CustomImageType.refresh),
    function("方法", CustomImageType.function);

    private final String name;
    private final String imageClass;

    CommandBtnType(String name, CustomImageType imageClass) {
        this.name = name;
        this.imageClass = imageClass.getImageClass();
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String getName() {
        return name;
    }
}
