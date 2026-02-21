package net.ooder.scene.drivers.vfs;

import java.io.InputStream;
import java.util.List;

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
    
    List<String> readLine(List<Integer> lineNums) throws VfsException;
}
