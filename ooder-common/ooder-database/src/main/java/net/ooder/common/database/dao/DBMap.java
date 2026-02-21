/**
 * $RCSfile: DBMap.java,v $
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

import java.util.Map;

public interface DBMap<K, V> extends Map<K, V> {

    Boolean isNew();

    void setNew(Boolean isNew);

    String getPkValue();

    String getTableName();

    String getPkName();

    String getConfigKey();


    void setPkValue(String pkValue);
}


