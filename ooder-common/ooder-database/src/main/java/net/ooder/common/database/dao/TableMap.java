/**
 * $RCSfile: TableMap.java,v $
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
package net.ooder.common.database.dao;

import java.util.HashMap;

public class TableMap extends HashMap implements DBMap {

    boolean isNew = true;


    @Override
    public Boolean isNew() {
        return true;
    }

    @Override
    public void setNew(Boolean isNew) {

    }

    @Override
    public String getPkValue() {
        return this.get("pkValue") != null ? this.get("pkValue").toString() : null;
    }

    @Override
    public String getTableName() {

        return this.get("tableName") != null ? this.get("tableName").toString() : null;
    }

    @Override
    public String getPkName() {
        return this.get("pkName") != null ? this.get("pkName").toString() : null;
    }

    @Override
    public String getConfigKey() {
        return this.get("configKey") != null ? this.get("configKey").toString() : null;
    }

    @Override
    public void setPkValue(String pkValue) {
        this.put("pkValue", pkValue);
    }
}


