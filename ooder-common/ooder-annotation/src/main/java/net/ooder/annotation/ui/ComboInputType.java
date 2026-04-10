package net.ooder.annotation.ui;


import net.ooder.annotation.IconEnumstype;

public enum ComboInputType implements IconEnumstype {
    input("输入框", "ri-keyboard-line", ComboType.input),
    combobox("下拉输入框", "ri-arrow-down-s-line", ComboType.input),
    password("密码框", "ri-key-line", ComboType.input),
    radiobox("Radio复选", "ri-radio-button-line", ComboType.input),
    checkbox("单选框", "ri-checkbox-line", ComboType.input),
    file("文件", "ri-upload-line", ComboType.input),
    textarea("多行输入", "ri-align-left", ComboType.input),
    label("LABLE", "ri-font-size", ComboType.input),
    date("日期框", "ri-calendar-line", ComboType.input),
    profile("profile", "ri-filter-line", ComboType.input),
    image("图片", "ri-image-line", ComboType.input),
    text("文本", "ri-align-left", ComboType.input),
    time("时间框", "ri-time-line", ComboType.input),
    datetime("日期时间框", "ri-calendar-line", ComboType.input),
    color("颜色选择", "ri-palette-line", ComboType.input),
    spin("数字滚动", "ri-refresh-line", ComboType.number),
    listbox("下拉列表", "ri-list-check", ComboType.list),
    getter("动态字典", "ri-list-check", ComboType.list),
    helpinput("帮助索引", "ri-question-line", ComboType.list),
    button("按钮", "ri-square-line", ComboType.button),
    cmd("普通按钮", "ri-box-2-line", ComboType.button),
    dropbutton("状态按钮", "ri-square-line", ComboType.button),
    cmdbox("按钮组", "ri-keyboard-line", ComboType.button),
    popbox("弹出框", "ri-window-line", ComboType.module),
    counter("左右滚动", "ri-speed-line", ComboType.number),
    currency("货币", "ri-money-dollar-circle-line", ComboType.number),
    number("数字", "ri-hashtag", ComboType.number),

    none("默认", "ri-code-line", ComboType.other),
    auto("自适应", "ri-code-line", ComboType.input),
    split("分隔符", "ri-refresh-line", ComboType.other);

    private final String name;
    private final String imageClass;
    private final ComboType comboType;

    ComboInputType(String name, String imageClass, ComboType comboType) {
        this.comboType = comboType;
        this.name = name;
        this.imageClass = imageClass;
    }

    public ComboType getComboType() {
        return comboType;
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