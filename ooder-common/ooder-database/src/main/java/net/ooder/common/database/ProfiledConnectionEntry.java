/**
 * $RCSfile: ProfiledConnectionEntry.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:49 $
 *
 * Copyright (C) 2003 spk, Inc. All rights reserved.
 *
 * This software is the proprietary information of spk, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: ProfiledConnectionEntry.java,v $
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

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 
 * Simple class for tracking profiling stats for individual SQL queries.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: raddev.cn</p>
 * @author wenzhang li
 * @version 1.0
 */
public class ProfiledConnectionEntry {
    /**
     * The SQL query.
     */
    public String sql;

    /**
     * Number of times the query has been executed.
     */
    public int count;

    /**
     * The total time spent executing the query (in milliseconds).
     */
    public int totalTime;

    public ProfiledConnectionEntry(String sql) {
        this.sql = sql;
        count = 0;
        totalTime = 0;
    }
}


