/**
 * $RCSfile: VFSJson.java,v $
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

public interface VFSJson extends JIndexBean {

   
    public String getFileName(); 
    
    public void setFileName(String fileName); 
    // VFS 云端VFS对应路径
    public String getVfsPath();
 
    public String getPath();

    public void setPath(String path);

    public void setVfsPath(String vfspath);

 
}


