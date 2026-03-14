/**
 * $RCSfile: FileIndexEnmu.java,v $
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
package net.ooder.vfs.index;


import net.ooder.common.ConditionKey;

public  enum FileIndexEnmu implements ConditionKey {

    name ("name"),
    userId ("userId"),
    text ("text"),
    desc ("desc"),
    right ("right"),
    docpath ("docpath"),
    createtime("createtime");

    private FileIndexEnmu(String conditionKey) {
        this.conditionKey = conditionKey;
    }

    private String conditionKey;

    public String toString() {
        return conditionKey;
    }

    @Override
    public String getValue() {
        return conditionKey;
    }
}


