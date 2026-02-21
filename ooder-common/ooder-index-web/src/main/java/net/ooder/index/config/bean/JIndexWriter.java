/**
 * $RCSfile: JIndexWriter.java,v $
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

public interface JIndexWriter extends JIndexBean{



    public OpenMode getOpenMode();

    public void setOpenMode(OpenMode openMode);

    public Class getAnalyzer();

    public void setAnalyzer(Class analyzer);
}


