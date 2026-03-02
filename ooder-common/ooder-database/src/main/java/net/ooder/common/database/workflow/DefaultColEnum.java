/**
 * DefaultColEnum 定义了工作流数据库的默认列枚举。
 */
package net.ooder.common.database.workflow;


import net.ooder.annotation.ColType;

public enum DefaultColEnum {

    activityHistoryId("activityHistoryId", "activityHistoryId", 64, ColType.VARCHAR),
    activityInstId("activityInstId", "activityInstId", 64, ColType.VARCHAR),
    userId("userId", "userId", 64, ColType.VARCHAR),
    processInstId("processInstId", "processInstId", 64, ColType.VARCHAR);


    public ColType dbtype;

    public String cnname;

    public Integer length;

    public String name;

    public String getName() {
        return name;
    }

    public String getCnName() {
        return cnname;
    }


    DefaultColEnum(String name, String cnname, Integer length, ColType dbtype) {
        this.name = name;
        this.cnname = cnname;
        this.length = length;
        this.dbtype = dbtype;
    }

}
