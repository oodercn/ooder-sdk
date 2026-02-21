package net.ooder.vfs.sync.restor;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.sync.TaskResult;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class RestorFileVisitor implements FileVisitor<Path> {

    private MinServerActionContextImpl autoruncontext;

    List<RestorObjectTask<TaskResult<String>>> tasks;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RestorFileVisitor.class);

    public RestorFileVisitor(Path localDiskPath, int maxTaskSize, List<RestorObjectTask<TaskResult<String>>> tasks) {

        this.tasks = tasks;
        JDSContext context = JDSActionContext.getActionContext();
        this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
        autoruncontext.setParamMap(context.getContext());
        String sessionId = context.getSessionId();
        if (sessionId != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            autoruncontext.getContext().put(JDSContext.SYSCODE, autoruncontext.getSystemCode());
            autoruncontext.setSessionMap(context.getSession());

        }
        logger.info(autoruncontext);

    }

    /**
     * 同步对比文件
     *
     * @param fPath
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Path fPath, BasicFileAttributes attrs) throws IOException {
        JDSActionContext.setContext(autoruncontext);
        //  String vfspath = fPath.toString().substring(SyncLocal.formartPath(fPath.toString()).indexOf("root/"));
        System.out.println("创建对比任务： " + fPath.toString() + "当前任务数：" + tasks.size());
        RestorObjectTask task = new RestorObjectTask(fPath);
        tasks.add(task);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 同步文件夹
     *
     * @param dir
     * @param attrs
     * @return
     * @throws IOException
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

};
