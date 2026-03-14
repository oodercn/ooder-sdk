/**
 * $RCSfile: JField.java,v $
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

public interface JField extends JIndexBean {
    
    public Object getValue();

    public void setValue(Object value);
 
    public String getName();

    public void setName(String name);

    public String getConverter();

    public void setConverter(String converter);

    public Store getStore();

    public void setStore(Store store);

    public Boolean getHighlighter() ;

    public void setHighlighter(Boolean highlighter);
}


