/**
 * $RCSfile: DefaultColEnum.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.database;


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


