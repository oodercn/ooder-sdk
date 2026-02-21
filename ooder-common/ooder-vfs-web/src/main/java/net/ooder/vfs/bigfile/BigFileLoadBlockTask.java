package net.ooder.vfs.bigfile;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.sync.TaskResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class BigFileLoadBlockTask<T extends TaskResult> implements Callable<T> {


    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, BigFileLoadBlockTask.class);
    private final String fileVersionId;
    private final Integer fileIndex;
    private Path fPath;
    private MinServerActionContextImpl autoruncontext;

    public BigFileLoadBlockTask(Path fPath, String fileVersionId, Integer fileIndex) {
        this.fPath = fPath;
        this.fileVersionId = fileVersionId;
        this.fileIndex = fileIndex;
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

    @Override
    public T call() {
        JDSActionContext.setContext(autoruncontext);
        File file = ((Path) fPath).toFile();
        TaskResult<String> resultModel = new TaskResult<String>();
        if (file.isFile() && file.length() > 0) {
            try {
                FileObject fileObject = CtVfsFactory.getCtVfsService().createFileObject(new MD5InputStream(new FileInputStream(file)));
                CtVfsFactory.getCtVfsService().createView(fileVersionId, fileObject.getID(), fileIndex);
                resultModel.setResult(1);
            } catch (JDSException e) {
                resultModel.setResult(-1);
            } catch (FileNotFoundException e) {
                resultModel.setResult(-1);
            }
        }
        return (T) resultModel;
    }


}
