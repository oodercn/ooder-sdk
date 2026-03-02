/**
 * $RCSfile: TableInfo.java,v $
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

import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.*;


public class TableInfo implements Cacheable, Serializable {
    private String name;

    private String cnname;

    private String className;


    private String configKey;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();


    private Set<String> pkNames = new HashSet<String>();

    private String url;

    @JSONField(serialize = false)
    private List<ColInfo> colList = new ArrayList<ColInfo>();

    @JSONField(serialize = false)
    public ColInfo getCoInfoByName(String name) {
        for (ColInfo colInfo : colList) {
            if (colInfo.getName() != null && name.toLowerCase().equals(colInfo.getName().toLowerCase())) {
                return colInfo;
            }
            if (colInfo.getFieldname() != null && name.toLowerCase().equals(colInfo.getFieldname().toLowerCase())) {
                return colInfo;
            }
        }
        return null;
    }

    public List<ColInfo> getColList() {

        return colList;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<String> getPkNames() {
        return pkNames;
    }

    public void setPkNames(Set<String> pkNames) {
        this.pkNames = pkNames;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setColList(List<ColInfo> colList) {
        this.colList = colList;
    }

    public String getPkName() {
        StringBuffer pkStr = new StringBuffer();
        if (pkNames.size() > 0) {
            for (String pkName : pkNames) {
                pkStr.append(pkName.toLowerCase());
                pkStr.append(",");
            }
            pkStr.deleteCharAt(pkStr.length() - 1);
        }


        return pkStr.toString();
    }

    public void setPkName(String... pkNameArr) {
        for (String pkName : pkNameArr) {
            if (!this.pkNames.contains(pkName)) {
                pkNames.add(pkName);
            }
        }

    }

    public void setUniqueConstraint(UniqueConstraint... uniqueConstraints) {

        for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
            this.setPkName(uniqueConstraint.columnNames());
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public Map<String, ColInfo> getCols() {
//        return cols;
//    }
//
//    public void setCols(Map<String, ColInfo> cols) {
//        this.cols = cols;
//    }
//
//
//    public ColInfo getCol(String name) {
//        return cols == null ? null : cols.get(name);
//    }

    public void setCol(String name, ColInfo col) {
        //   cols.put(name, col);
        String fileName = col.getFieldname();
        if (fileName == null) {
            fileName = name;
            col.setFieldname(name);
        }
//        cols.put(fileName, col);
        if (!colList.contains(col)) {
            colList.add(col);
        }
    }

    public void addCol(ColInfo col) {
        setCol(col.getName(), col);
    }


    public TableInfo clone() {
        TableInfo info = new TableInfo();
        info.setCnname(cnname);
        info.setColList(colList);
        info.setName(name);
        info.setConfigKey(configKey);
        return info;

    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("name=").append(name).append(",cnname=").append(cnname).append(",pk=").append(pkName).append(",cols[").append(cols.toString()).append("]");
        //  sb.append("name=").append(name).append(",cnname=").append(cnname).append(",pk=").append(pkName).append(",cols[").append(cols.toString()).append("]");
        return cnname == null ? cnname : name;
    }

    @JSONField(serialize = false)
    public int getCachedSize() {
        int size = 0;

        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfString(cnname);
        size += CacheSizes.sizeOfString(configKey);
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfObject(pkNames);
        size += CacheSizes.sizeOfString(url);
        size += CacheSizes.sizeOfList(colList);

        return size;
    }
}


