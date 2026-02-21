package net.ooder.scene.skills.vfs;

import java.io.InputStream;

/**
 * FileObject 文件对象接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface FileObject {

    String getID();
    
    String getName();
    
    String getRootPath();
    
    String getAdapter();
    
    Long getLength();
    
    String getHash();
    
    String getPath();
    
    Long getCreateTime();
    
    InputStream downLoad() throws VfsException;
    
    Integer writeLine(String str) throws VfsException;
    
    java.util.List<String> readLine(java.util.List<Integer> lineNums) throws VfsException;
}
