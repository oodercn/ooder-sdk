package net.ooder.vfs.sync.restor;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.IOUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.sync.TaskResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class RestorObjectTask<T extends TaskResult> implements Callable<T> {


    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RestorObjectTask.class);
    private Path fPath;
    private MinServerActionContextImpl autoruncontext;

    public RestorObjectTask(Path fPath) {
        this.fPath = fPath;
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
        long time = System.currentTimeMillis();

        TaskResult<String> resultModel = new TaskResult<String>();
        if (file.isFile() && file.length() > 0) {
            FileInputStream stream = null;
            try {
                String md5 = null;
                stream = new FileInputStream(file);
                String hash = DigestUtils.md5Hex(stream);
                FileObject fileObject = CtVfsFactory.getCtVfsService().getFileObjectByHash(hash);

                if (fileObject != null) {
                    System.out.println("文件大小 ：" + file.length() + " 耗时：" + (System.currentTimeMillis() - time) + "ms" + "剩余：" + RestorCMD.tasksSize);
                    resultModel.setResult(1);
                } else {
                    fileObject = CtVfsFactory.getCtVfsService().createFileObject(file);
                    RestorCMD.updatetasksSize = RestorCMD.updatetasksSize + 1;
                    System.out.println("开始恢复  文件大小 ：" + file.length() + " 耗时：" + (System.currentTimeMillis() - time) + "ms 已完成更新" + RestorCMD.updatetasksSize + "个 剩余：" + RestorCMD.tasksSize);
                    resultModel.setResult(2);
                }
                RestorCMD.tasksSize = RestorCMD.tasksSize - 1;

            } catch (Throwable e) {
                e.printStackTrace();
                resultModel.setResult(-1);
            } finally {
                IOUtility.shutdownStream(stream);
            }
        }
        return (T) resultModel;
    }


}
