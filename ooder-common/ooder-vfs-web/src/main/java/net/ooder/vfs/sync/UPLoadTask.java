package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.ct.CtVfsFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;

public class UPLoadTask<T extends TaskResult> implements Callable<T> {


    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, UPLoadTask.class);
    private final String vfsPath;
    private Set<String> paths;

    private Path fPath;
    private MinServerActionContextImpl autoruncontext;

    public UPLoadTask(Path fPath, String vfsPath, Set<String> paths) {

        this.fPath = fPath;
        this.vfsPath = vfsPath;
        this.paths = paths;
        JDSContext context = JDSActionContext.getActionContext();
        this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
        autoruncontext.setParamMap(context.getContext());
        String sessionId = context.getSessionId();
        autoruncontext.getParamMap().put("SYSTYPE", "IMPLTOOLS");
        if (sessionId != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            autoruncontext.getContext().put(JDSContext.SYSCODE, autoruncontext.getSystemCode());
            autoruncontext.setSessionMap(context.getSession());

        }
        logger.info(autoruncontext);

    }

    @Override
    public T call() {
        JDSActionContext.setContext(autoruncontext);
        File file = ((Path) fPath).toFile();
        TaskResult<String> resultModel = new TaskResult<String>();
        if (file.isFile() && file.length() > 0) {
            try {
                String localPath = SyncLocal.formartPath(fPath.toString(),true);
                // String localPath = SyncLocal.formartPath(fPath.getParent().toString());
                long time = System.currentTimeMillis();
                String cpath = localPath;
                if (localPath.indexOf("root/") > -1) {
                    cpath = localPath.substring(localPath.indexOf("root/"));
                } else {
                    cpath = localPath.substring(localPath.indexOf(this.vfsPath));
                }


                FileInfo fileInfo = null;
                try {
                    if (paths.contains(cpath)) {
                        fileInfo = CtVfsFactory.getCtVfsService().getFileByPath(cpath);
                    }
                    if (fileInfo == null) {
                        fileInfo = CtVfsFactory.getCtVfsService().createFile(cpath);
                    }
                } catch (JDSException e) {
                    logger.error("Failed to get or create file: " + cpath, e);
                }

//                FileInfo fileInfo = null;
//                try {
//                    if (paths.contains(cpath + file.getName())) {
//                        fileInfo = CtVfsFactory.getCtVfsService().getFileByPath(cpath + file.getName());
//                    }
//                    if (fileInfo == null) {
//                        fileInfo = CtVfsFactory.getCtVfsService().createFile(cpath, file.getName());
//                    }
//                } catch (JDSException e) {
//                    e.printStackTrace();
//                }

                if (fileInfo!=null){
                    String vfsHash = fileInfo.getCurrentVersonFileHash();
                    String md5 = null;
//               try {
                    //  md5 = MD5.getHashString(file);
                    // if (vfsHash == null || !vfsHash.equals(md5)) {
                    SyncLocal.updatetasksSize = SyncLocal.updatetasksSize + 1;
                    CtVfsFactory.getCtVfsService().upload(vfsPath, file, null);
                    logger.info("文件：" + vfsPath + "成功更新 ：" + file.length() + " 耗时：" + (System.currentTimeMillis() - time) + "ms" + "共更更新 ：" + SyncLocal.updatetasksSize);
                    //}
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                    SyncLocal.tasksSize = SyncLocal.tasksSize - 1;
                    logger.info("文件：" + vfsPath + "对比完成 完成！文件大小 ：" + file.length() + " 耗时：" + (System.currentTimeMillis() - time) + "ms" + "剩余：" + SyncLocal.tasksSize);

                }else{
                    logger.error("文件：" + cpath + " 上传失败 耗时：" + (System.currentTimeMillis() - time) + "ms" + "剩余：" + SyncLocal.tasksSize);
                    resultModel.setResult(-1);
                }


                resultModel.setResult(1);
            } catch (JDSException e) {
                resultModel.setResult(-1);
            }
        }
        return (T) resultModel;
    }


}
