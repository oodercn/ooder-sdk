package net.ooder.vfs.sync.restor;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.ct.CtVfsService;
import  net.ooder.vfs.sync.TaskResult;
import  net.ooder.web.RemoteConnectionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RestorCMD {
    private Path localDiskPath;
    private int maxTaskSize = 50;
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RestorCMD.class);
    List<String> errorPaths = new ArrayList<String>();
    List<String> updatePaths = new ArrayList<String>();

    public RestorCMD(Path localDiskPath, int maxTaskSize) {
        if (maxTaskSize > 0) {
            this.maxTaskSize = maxTaskSize;
        }
        this.localDiskPath = localDiskPath;
    }


    /**
     * 获取本地索引地址
     *
     * @param
     * @return
     */
    private void initPath(Path path) {
        if (Files.notExists(path)) {
            try {
                path = Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    static List<RestorObjectTask<TaskResult<String>>> tasks = new ArrayList<RestorObjectTask<TaskResult<String>>>();


    public static int tasksSize = 0;

    public static int updatetasksSize = 0;

    public void restore() throws IOException {
        tasksSize = 0;
        updatetasksSize = 0;
        List<RestorTask<Path>> imptasks = new ArrayList<RestorTask<Path>>();
        //
        if (maxTaskSize > 0) {
            RemoteConnectionManager.initConnection("localSync", maxTaskSize);
            logger.info("maxTaskSize=" + maxTaskSize);
        }
        logger.info("localDiskPath=" + localDiskPath);

        long time = System.currentTimeMillis();
        int k = 0;

        System.out.println("开始运算文件.....  ");
        File[] files = localDiskPath.toFile().listFiles();
        for (File file : files) {
            if (!file.isFile()) {
                imptasks.add(new RestorTask(file, maxTaskSize, tasks));

            }
        }

        try {

            List<Future<Path>> futures = RemoteConnectionManager.getConntctionService("restore").invokeAll(imptasks);
            for (Future<Path> resultFuture : futures) {
                try {
                    Path result = resultFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tasksSize = tasks.size();
        System.out.println("文件运算完毕   ");
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        System.out.println("共计" + tasksSize + "个  任务！");
        System.out.println("准备开始更新...");

        List<RestorObjectTask<TaskResult<String>>> allTasks = new ArrayList<>();

        List<Future<TaskResult<String>>> results = null;
        try {
            for (RestorObjectTask<TaskResult<String>> task : tasks) {
                try {
                    if (task != null) {
                        allTasks.add(task);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            results = RemoteConnectionManager.getConntctionService("localSync").invokeAll(allTasks);
            for (Future<TaskResult<String>> resultFuture : results) {
                k = k + 1;
                TaskResult<String> result = resultFuture.get();
                if (result.getResult() == -1) {
                    errorPaths.add(result.getData());
                }
                if (result.getResult() == 2) {
                    updatePaths.add(result.getData());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int ff = 0;
        while (ff < 5) {
            ff = ff + 1;
            System.out.println("");
        }


        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        System.out.println("上传完毕正在统计结果");
        int kk = 0;
        while (kk < 10) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            kk = kk + 1;
            System.out.print("..");
        }


        System.out.println("上传结束：共计" + tasks.size() + "个 文件， 成功恢复：" + updatetasksSize + "个 失败：" + errorPaths.size() + "个");

        for (String path : errorPaths) {
            logger.info(" 错误文件地址：" + path);
        }

    }


    public CtVfsService getVfsService() {

        CtVfsService client = CtVfsFactory.getCtVfsService();
        if (client == null) {
            this.logger.error("$VFSClientService  load err");
        }

        return client;

    }

}
