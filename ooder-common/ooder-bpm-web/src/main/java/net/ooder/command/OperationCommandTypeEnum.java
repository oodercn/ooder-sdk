package net.ooder.command;

import net.ooder.annotation.Enums;

public enum OperationCommandTypeEnum implements Enums {
    LOCK("锁定"),
    UNLOCK("解锁"),
    RESTART("重启"),
    UPGRADE("升级"),
    FACTORY_RESET("恢复出厂"),
    DELETE_CACHE("清除缓存"),
    SYNC_TIME("时间同步"),
    ADD_PASSWORD("添加密码"),
    TEST_TIMES("测试次数");

    private final String displayName;

    OperationCommandTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return name();
    }

    public String getName() {
        return displayName;
    }
}
