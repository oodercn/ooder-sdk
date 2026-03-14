/**
 * $RCSfile: JDocument.java,v $
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

public interface JDocument extends JIndexBean {

    JFSDirectory getFsDirectory();

    JIndexWriter getIndexWriter();

    String getName();
    
    VFSJsonBean getVfsJson();
    

    public void setVfsValid(boolean vfsValid);

    public void setIndexValid(boolean indexValid);

    public boolean isVfsValid();

    public boolean isIndexValid();

}


