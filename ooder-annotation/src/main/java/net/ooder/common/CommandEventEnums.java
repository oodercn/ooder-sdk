package net.ooder.common;

import net.ooder.annotation.EventEnums;

import java.io.IOException;

public enum CommandEventEnums implements EventEnums {

    COMMANDINIT("命令初始化", "CommandInit", 0),

    COMMANDSENDSUCCESS("命令成功到达", "CommandSendSuccess", 1000),

    COMMANDSENDING("等待设备执行", "CommandSendIng", 1001),

    COMMANDSENDWAITE("延迟发送等待结果", "CommandSended", 1002),

    COMMANDERROR("命令错误", "CommandError", 2001),

    COMMANDVERSORERROR("命令版本错误", "CommandVersorError", 2002),

    COMMANDSENDTIMEOUT("设备不存或命令执行超时", "CommandSendTimeOut", 2003),

    // 当设备出现，低电池电压，无路由能力或无间接能力时上报 2004设备故障。
    COMMANDLINKFAIL("无线链路失效", "CommandLinkFail", 2004),

    // 2005路由失效 当父设备链路失败，源路由失败时，上报2005.
    COMMANDROUTEERROR("链路错误设备故障", "CommandRouteError", 2005),

    // 命令执行失败
    COMMANDEXECUTEFAIL("命令执行错误设备故障", "CommandExecuteFail", 2006),

    PASSWORDALREADYEXISTS("密码不存在", "PasswordAlreadyExists", 2007),

    PASSWORDNOTEXISTS("密码删除失败", "PasswordNotExists", 2008),

    PASSWORDFULL("密码满", "PassWordFull", 2009),

    UNKNOWCOMMAND("未知命令", "UNKNOWCOMMAND", 2010),

    COMMANDOUTERERROR("未知错误", "COMMANDOUTERERROR", 3001),

    COMMANDTIMEOUT("执行超时", "CommandTimeOut", 4001),

    COMMANDROUTING("服务器调度开始", "CommandRouteing", 5001),

    COMMANDROUESUCCESS("服务器调度成功", "CommandRouteSuccess", 5010),

    COMMANDROUTEFAIL("服务器调度失败", "CommandRouteFail", 5020),

    COMMANDSENDTIME("命令发送超时", "CommandTimeOut", 6000),

    COMMANDROUTED("服务器调度结束", "CommandRouted", 5000);


    private String name;

    private Integer code;

    private String method;

    public Integer getCode() {
        return code;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    CommandEventEnums(String name, String method, Integer code) {

        this.name = name;
        this.method = method;
        this.code = code;

    }

    @Override
    public String toString() {
        return method.toString();
    }

    public static CommandEventEnums fromCode(Integer code) {
        for (CommandEventEnums type : CommandEventEnums.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return COMMANDINIT;
    }

    public static CommandEventEnums fromMethod(String method) {
        for (CommandEventEnums type : CommandEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return COMMANDINIT;
    }

    public static CommandEventEnums fromType(String method) {
        for (CommandEventEnums type : CommandEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return COMMANDINIT;
    }

    @Override
    public String getType() {
        return method.toString();
    }

    public static void main(String[] args) throws IOException {

        String msgid = "command||29c2badc-0d52-48f4-95e4-22b36449e4a9";
        System.out.println(msgid.substring(msgid.indexOf("||") + 2, msgid.length()));
        //   msg = (T) commandCache.get(msgId.substring( msgId.indexOf("||"), msgId.length()));
//
//        String text = "{\"times\":1,\"receiver\":\"29c2badc-0d52-48f4-95e4-22b36449e4a9\",\"systemCode\":\"org\",\"arrivedTime\":1552716967176,\"resultCode\":\"COMMANDINIT\",\"id\":\"cb25f7c8-1fad-479e-a04c-42c903d675ee\",\"type\":\"COMMAND\",\"event\":\"InitGateway\",\"gatewayId\":\"00D6FFFFE6AA528\"}";
//        RMsg rmsg = JSONObject.parseObject(text, RMsg.class, Feature.DisableSpecialKeyDetect);
//        System.out.println(JSONObject.toJSON(rmsg) + rmsg.getModeId());
    }
}
