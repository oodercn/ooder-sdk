package net.ooder.vfs.sync;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileInfo;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.ct.CtVfsFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocalFileVisitor implements FileVisitor<Path> {

    private Set<String> paths = new LinkedHashSet<String>();
    private Path fPath;
    private String vfsPath;
    private Path localDiskPath;
    private List<Folder> folders;

    private long syncDelayTime;
    private long defaultDelayTime = 180 * 1000;
    private int maxTaskSize;
    private MinServerActionContextImpl autoruncontext;

    List<UPLoadTask<TaskResult<String>>> tasks;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, LocalFileVisitor.class);

    public LocalFileVisitor(String vfsPath, Path localDiskPath, long syncDelayTime, int maxTaskSize, List<UPLoadTask<TaskResult<String>>> tasks) {
        // 延时执行时间
        this.syncDelayTime = syncDelayTime;
        this.maxTaskSize = maxTaskSize;
        this.tasks = tasks;
        this.vfsPath = vfsPath;
        this.localDiskPath = localDiskPath;

        try {
            Folder folder = CtVfsFactory.getCtVfsService().getFolderByPath(vfsPath);
            List<Folder> folders = folder.getChildrenRecursivelyList();
            for (Folder childFoder : folders) {
                if (childFoder != null) {
                    paths.add(childFoder.getPath());
                }
            }
            List<FileInfo> vfsfiles = folder.getFileListRecursively();
            for (FileInfo vfsfile : vfsfiles) {
                if (vfsfile != null) {
                    paths.add(vfsfile.getPath());
                }
            }


        } catch (JDSException e) {
            e.printStackTrace();
        }


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
        String vfspath = SyncLocal.formartPath(fPath.toString(), true);
        if (vfspath.indexOf("root/") > -1) {
            vfspath = vfspath.substring(vfspath.indexOf("root/"));
        } else if (vfspath.indexOf("form/") > -1) {
            vfspath = vfspath.substring(vfspath.indexOf("form/"));
        } else {
            vfspath = vfspath.substring(vfspath.indexOf(this.vfsPath));
        }

        System.out.println("创建对比任务： " + vfspath + "当前任务数：" + tasks.size());
        UPLoadTask task = new UPLoadTask(fPath, vfspath, this.paths);
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
        JDSActionContext.setContext(autoruncontext);
        if (dir.toFile().isDirectory()) {
            String localPath = SyncLocal.formartPath(localDiskPath.toAbsolutePath().toString(), false);
            if (!localPath.endsWith("/")) {
                localPath = localPath + "/";
            }

            String defPath = this.vfsPath;

            if (!defPath.endsWith("/")) {
                defPath = defPath + "/";
            }


            String cpath = localPath;
            if (localPath.indexOf("root/") > -1) {
                cpath = localPath.substring(localPath.indexOf("root/"));
            } else if (localPath.indexOf("form/") > -1) {
                cpath = localPath.substring(localPath.indexOf("form/"));

            } else {
                cpath = localPath.substring(localPath.indexOf(SyncLocal.formartPath(defPath, false)));
            }

            //String cpath = StringUtility.replace(SyncLocal.formartPath(dir.toFile().getAbsolutePath()), SyncLocal.formartPath(localDiskPath.toAbsolutePath().toString()), vfsPath);

            logger.info("start preVisitDirectory cpath=" + cpath);
            System.out.println("开始对比： " + cpath);
            try {
                if (!paths.contains(cpath)) {
                    Folder folder = CtVfsFactory.getCtVfsService().mkDir(cpath);
                    paths.add(cpath);
                }

            } catch (JDSException e) {
                logger.error(e);
            }

        }

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
