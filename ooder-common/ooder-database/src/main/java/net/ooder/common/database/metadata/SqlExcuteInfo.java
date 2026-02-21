/**
 * $RCSfile: SqlExcuteInfo.java,v $
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
package net.ooder.common.database.metadata;

import net.ooder.common.database.ProfiledConnection;
import net.ooder.common.util.StringUtility;

public class SqlExcuteInfo {

    String configKey;
    SqlType type;
    Long queryConnt = 0L;
    String queryTime;
    String perSecond;


    SqlExcuteInfo(String configKey, SqlType type) {
        this.type = type;
        this.configKey = configKey;
        queryConnt = ProfiledConnection.getQueryCount(type.getType());
        queryTime = StringUtility.formatNumber(ProfiledConnection.getQueriesPerSecond(ProfiledConnection.SELECT), 1);
        perSecond = StringUtility.formatNumber(ProfiledConnection.getAverageQueryTime(ProfiledConnection.SELECT), 1);
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }


    public SqlType getType() {
        return type;
    }

    public void setType(SqlType type) {
        this.type = type;
    }

    public Long getQueryConnt() {
        return queryConnt;
    }

    public void setQueryConnt(Long queryConnt) {
        this.queryConnt = queryConnt;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    public String getPerSecond() {
        return perSecond;
    }

    public void setPerSecond(String perSecond) {
        this.perSecond = perSecond;
    }
}


