/**
 * $RCSfile: TmpJavaFileManager.java,v $
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
package net.ooder.common.util.java;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TmpJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private Map<String, JavaFileObject> fileObjectMap = new HashMap<>();

    public TmpJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public TmpJavaFileManager(JavaFileManager fileManager, Map<String, JavaFileObject> fileObjectMap) {
        super(fileManager);
        this.fileObjectMap = fileObjectMap;
    }

    @Override
    public JavaFileObject getJavaFileForInput(JavaFileManager.Location location,
                                              String className,
                                              JavaFileObject.Kind kind) throws IOException {
        JavaFileObject javaFileObject = fileObjectMap.get(className);
        if (javaFileObject == null) {
            return super.getJavaFileForInput(location, className, kind);
        }
        return javaFileObject;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location,
                                               String className,
                                               JavaFileObject.Kind kind,
                                               FileObject sibling) throws IOException {
        JavaFileObject javaFileObject = new TmpJavaFileObject(className, kind);
        fileObjectMap.put(className, javaFileObject);
        return javaFileObject;
    }

    public Map<String, JavaFileObject> getFileObjectMap() {
        return fileObjectMap;
    }

    public void setFileObjectMap(Map<String, JavaFileObject> fileObjectMap) {
        this.fileObjectMap = fileObjectMap;
    }
}