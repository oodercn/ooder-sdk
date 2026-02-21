package net.ooder.vfs.ct.task;

import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.service.VFSDiskService;

import java.util.concurrent.Callable;

public class SyncLoadFileByPathTask implements Callable<FileInfo> {

    private final String path;

    public SyncLoadFileByPathTask(String path) {
        this.path = path;
    }

    @Override
    public FileInfo call() throws Exception {
        VFSDiskService service = (VFSDiskService) EsbUtil.parExpression(VFSDiskService.class);
        FileInfo fileInfo = service.getFileInfoByPath(path).get();
        return fileInfo;
    }
}

