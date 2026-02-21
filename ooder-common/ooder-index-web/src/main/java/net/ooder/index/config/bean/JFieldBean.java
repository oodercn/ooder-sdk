/**
 * $RCSfile: JFieldBean.java,v $
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
package net.ooder.index.config.bean;

import org.apache.lucene.document.Field.Store;
import net.ooder.annotation.MethodChinaName;

public class JFieldBean implements JField{
    
    String id;
    String name;



    Boolean highlighter;
    String converter;  
    Object value;
   
    Store store;
    Class clazz;

    public JFieldBean(){

    }

    @MethodChinaName("获取高亮标志")
    public Boolean getHighlighter() {
        return highlighter;
    }

    @MethodChinaName("设置高亮标志")
    public void setHighlighter(Boolean highlighter) {
        this.highlighter = highlighter;
    }
    @MethodChinaName("获取值")
    public Object getValue() {
        return value;
    }

    @MethodChinaName("设置值")
    public void setValue(Object value) {
        this.value = value;
    }

    
    @MethodChinaName("设置类对象")
    @Override
    public void setClazz(Class clazz) {
	this.clazz=clazz;
    }

    @MethodChinaName("获取类对象")
    @Override
    public Class getClazz(){
	return clazz;
    }
    

    @MethodChinaName("获取ID")
    public String getId() {
        return id;
    }

    @MethodChinaName("设置ID")
    public void setId(String id) {
        this.id = id;
    }

    @MethodChinaName("获取名称")
    public String getName() {
        return name;
    }

    @MethodChinaName("设置名称")
    public void setName(String name) {
        this.name = name;
    }

    @MethodChinaName("获取转换器")
    public String getConverter() {
        return converter;
    }

    @MethodChinaName("设置转换器")
    public void setConverter(String converter) {
        this.converter = converter;
    }

    @MethodChinaName("获取存储方式")
    public Store getStore() {
        return store;
    }

    @MethodChinaName("设置存储方式")
    public void setStore(Store store) {
        this.store = store;
    }

 
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

}


