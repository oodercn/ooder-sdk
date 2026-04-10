package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum CustomMsgMethod implements Enumstype {
    alert("alert", "警告"),
    echo("echo", "调试窗口"),
    msg("msg", "消息框"),
    message("message", "提示框"),
    log("log", "浏览器日志"),
    busy("busy", "显示遮罩"),
    confirm("confirm", "确认框"),
    prompt("prompt", "提示对话框"),
    free("free", "解除遮罩");
    private final String type;
    private final String name;

    CustomMsgMethod(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
