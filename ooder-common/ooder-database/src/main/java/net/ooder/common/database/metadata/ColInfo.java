/**
 * $RCSfile: ColInfo.java,v $
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

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.util.StringUtility;
import net.ooder.annotation.CustomBean;
import net.ooder.annotation.ColType;
import net.ooder.annotation.DBField;
import net.ooder.web.util.AnnotationUtil;

import java.io.Serializable;
import java.sql.Types;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2009-8-21 Time: 16:02:45
 */
public class ColInfo implements Cacheable, Serializable,CustomBean {
    private String name;// DBFILED
    private String fieldname;
    private String cnname;
    private String tablename;
    private String configKey;
    private int dataType = Types.VARCHAR;
    private Integer fractions = 0;
    private String url;
    private String[] enums;
    private Class<? extends Enum>  enumClass;
    private ColType colType;
    private boolean canNull = true;
    private Boolean isPk = false;
    private Integer length;

    public ColInfo() {

    }

    public Boolean getPk() {
        return isPk;
    }

    public void setPk(Boolean pk) {
        isPk = pk;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public boolean isCanNull() {
        return canNull;
    }

    public void setCanNull(boolean canNull) {
        this.canNull = canNull;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnname() {
        if (cnname != null) {
            if (cnname.indexOf(",") > 0) {
                cnname.substring(0, cnname.indexOf(","));
            }
        }

        return cnname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColInfo) {
            ColInfo colInfo = (ColInfo) obj;
            if (colInfo.getTablename().equals(this.getTablename()) &&
                    colInfo.getName().equals(this.getName())) {
                return true;
            } else {
                return false;
            }

        }
        return super.equals(obj);
    }

    public String[] getEnums() {
        return enums;
    }

    public void setEnums(String[] enums) {
        this.enums = enums;
    }

    public Class<? extends Enum>  getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getFieldname() {
        if (fieldname == null) {
            fieldname = StringUtility.formatJavaName(this.getName().toLowerCase(), false);
        }
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public ColType getColType() {
        return colType;
    }

    public void setColType(ColType colType) {
        this.colType = colType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Integer getFractions() {
        return fractions > 0 ? fractions : 0 - fractions;
    }


    public void setFractions(Integer fractions) {
        this.fractions = fractions;
    }

    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfString(cnname);
        size += CacheSizes.sizeOfObject(enumClass);
        size += CacheSizes.sizeOfObject(enums);
        size += CacheSizes.sizeOfBoolean();//isSearchFiled
        size += CacheSizes.sizeOfString(fieldname);
        size += CacheSizes.sizeOfString(configKey);
        size += CacheSizes.sizeOfString(tablename);
        size += CacheSizes.sizeOfInt();
        size += CacheSizes.sizeOfInt();
        size += CacheSizes.sizeOfString(name);

        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // sb.append("[col::name=").append(name).append(",cnname=").append(cnname).append(",fieldname=").append(fieldname).append(",type=").append(type).append(",length=").append(length).append(",canNull=").append(canNull).append("]\n");
        sb.append(cnname + "[" + name + "]");
        return sb.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ColInfo(DBField annotation) {
        fillData(annotation);
    }

    public ColInfo fillData(DBField annotation) {
        return AnnotationUtil.fillBean(annotation, this);
    }

    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }
}


