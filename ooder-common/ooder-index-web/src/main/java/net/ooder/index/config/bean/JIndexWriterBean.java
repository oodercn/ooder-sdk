/**
 * $RCSfile: JIndexWriterBean.java,v $
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


import org.apache.lucene.index.IndexWriterConfig.OpenMode;

public class JIndexWriterBean implements JIndexWriter{




   OpenMode openMode;
    Class analyzer;
    Class clazz;
    String id;
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setClazz(Class clazz) {
	this.clazz=clazz;
    }

    @Override
    public Class getClazz(){
	return clazz;
    }
    
    
    public OpenMode getOpenMode() {
        return openMode;
    }
    public void setOpenMode(OpenMode openMode) {
        this.openMode = openMode;
    }
    public Class getAnalyzer() {
        return analyzer;
    }
    public void setAnalyzer(Class analyzer) {
        this.analyzer = analyzer;
    }
}


