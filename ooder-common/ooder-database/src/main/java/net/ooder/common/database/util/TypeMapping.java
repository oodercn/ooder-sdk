
/**
 * $RCSfile: TypeMapping.java,v $
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
package net.ooder.common.database.util;

import net.ooder.common.database.metadata.ColInfo;

import java.awt.*;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code Generation>Code and
 * Comments
 */
public class TypeMapping {
    private static Map enclosingTypes = new HashMap();
    protected static Map resultSetGetMethods = new HashMap();

    static {
        enclosingTypes.put("int", "Integer");
        enclosingTypes.put("long", "Long");
        enclosingTypes.put("short", "Short");
        enclosingTypes.put("float", "Float");
        enclosingTypes.put("double", "Double");
        enclosingTypes.put("boolean", "Boolean");

        resultSetGetMethods.put("boolean", "Boolean");
        resultSetGetMethods.put("byte", "Byte");
        resultSetGetMethods.put("double", "Double");
        resultSetGetMethods.put("float", "Float");
        resultSetGetMethods.put("long", "Long");
        resultSetGetMethods.put("short", "Short");
        resultSetGetMethods.put("int", "Integer");
        resultSetGetMethods.put("java.io.InputStream", "InputStream");
        resultSetGetMethods.put("java.io.Reader", "String");
        resultSetGetMethods.put("java.lang.Byte", "Byte");
        resultSetGetMethods.put("java.lang.Byte[]", "Bytes");
        resultSetGetMethods.put("java.lang.Double", "Double");
        resultSetGetMethods.put("java.lang.Float", "Float");
        resultSetGetMethods.put("java.lang.Integer", "Integer");
        resultSetGetMethods.put("java.lang.Long", "Long");
        resultSetGetMethods.put("java.lang.Object", "Object");
        resultSetGetMethods.put("java.lang.Short", "Short");
        resultSetGetMethods.put("java.lang.String", "String");
        resultSetGetMethods.put("java.math.BigDecimal", "BigDecimal");
        resultSetGetMethods.put("java.sql.Array", "java.sql.Array");
        resultSetGetMethods.put("java.sql.Blob", "java.sql.Blob");
        resultSetGetMethods.put("java.sql.Clob", "java.sql.Clob");
        resultSetGetMethods.put("java.sql.Date", "Date");
        resultSetGetMethods.put("java.sql.Time", "Date");
        resultSetGetMethods.put("java.sql.Timestamp", "Date");
        resultSetGetMethods.put("java.util.Date", "Date");
    }

    public static String getEnclosingType(String primitive) {
        return (String) enclosingTypes.get(primitive);
    }

    public static boolean isNumeric(int type) {
        switch (type) {
            case Types.BIGINT:
            case Types.BIT:
            case 16: // Types.BOOLEAN
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isString(int type) {
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
                return true;
            default:
                return false;
        }
    }

    public static String getMappedType(ColInfo field) {
        int type = field.getDataType();
        String mappedType;
        int fractions = field.getFractions();
        if (field.getColType().equals("CLOB")) {
            return "java.lang.String";
        }
        if (field.getColType().equals("BLOB")) {
            type = Types.BLOB;
        }

        boolean primitive = false;
        switch (type) {
            case Types.ARRAY:
                mappedType = "java.sql.Array";
                break;
            case Types.BIGINT:
                mappedType = primitive ? "long" : "java.lang.Long";
                break;
            case Types.BINARY:
                mappedType = "java.io.InputStream";
                break;
            case Types.BIT:
                mappedType = primitive ? "int" : "java.lang.Integer";
                break;
            case Types.BLOB:
                mappedType = "java.io.InputStream";
                break;
            case 16: // Types.BOOLEAN
                mappedType = primitive ? "boolean" : "java.lang.Boolean";
                break;
            case Types.CHAR:
                mappedType = "java.lang.String";
                break;
            case Types.CLOB:
                mappedType = "java.lang.String";
                break;
            case Types.DATE:
                mappedType = "java.sql.Date";
                break;
            case Types.DECIMAL:
//	    if (fractions == 0) {
//		mappedType = primitive ? "int" : "java.lang.Integer";
//	    } else {
//		mappedType = primitive ? "float" : "java.lang.Float";
//	    }
                mappedType = primitive ? "int" : "java.lang.Integer";
                break;
            case Types.DOUBLE:
                // if (fractions == 0) {
                // mappedType = primitive ? "long" : "java.lang.Long";
                // } else {
                // mappedType = primitive ? "double" : "java.lang.Double";
                // }
                mappedType = primitive ? "double" : "java.lang.Double";
                break;
            case Types.FLOAT:
                // if (fractions == 0) {
                // mappedType = primitive ? "int" : "java.lang.Integer";
                // } else {
                // mappedType = primitive ? "float" : "java.lang.Float";
                // }
                mappedType = primitive ? "float" : "java.lang.Float";
                break;
            case Types.INTEGER:
                mappedType = primitive ? "java.lang.Integer" : "java.lang.Integer";
                break;
            case Types.LONGVARBINARY:
                mappedType = "java.io.InputStream";
                break;
            case Types.LONGVARCHAR:
                mappedType = "java.io.Reader";
                break;
            case Types.NUMERIC:
                mappedType = primitive ? "long" : "java.lang.Long";
//	    if (fractions == 0) {
//		mappedType = primitive ? "long" : "java.lang.Long";
//	    } else {
//		mappedType = primitive ? "double" : "java.lang.Double";
//	    }
                break;
            case Types.REAL:
                mappedType = primitive ? "int" : "java.lang.Integer";
//	    if (fractions == 0) {
//		mappedType = primitive ? "int" : "java.lang.Integer";
//	    } else {
//		mappedType = primitive ? "float" : "java.lang.Float";
//	    }
                break;
            case Types.SMALLINT:
                mappedType = primitive ? "short" : "java.lang.Short";
                break;
            case Types.TIME:
                mappedType = "java.sql.Time";
                break;
            case Types.TIMESTAMP:
                mappedType = "java.sql.Timestamp";
                break;
            case Types.TINYINT:
                mappedType = primitive ? "short" : "java.lang.Short";
                break;
            case Types.VARBINARY:
                mappedType = "java.io.InputStream";
                break;
            case Types.VARCHAR:
                mappedType = "java.lang.String";
                break;
            default:
                mappedType = "java.lang.String";
        }
        return mappedType;
    }




    public static String getSimpleType(ColInfo field) {
        return (String) resultSetGetMethods.get(getMappedType(field));
    }
}


