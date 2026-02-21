/**
 * $RCSfile: DataBaseType.java,v $
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
package net.ooder.common.database.enums;

import net.ooder.annotation.Enumstype;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public enum DataBaseType implements Enumstype {
    oracle("oracle"), db2("db2"), mysql("myslq"), mssql("mssql"), interbase("interbase"), other("other");

    //
    private final String name;


    DataBaseType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }


    public static DataBaseType getDataBaseType(DatabaseMetaData dbMetaData) throws SQLException {
        String name = dbMetaData.getDatabaseProductName();
        for (DataBaseType type : DataBaseType.values())
            if (name.toLowerCase().indexOf(type.name()) != -1) {
                return type;
            }
        return DataBaseType.other;
    }
}


