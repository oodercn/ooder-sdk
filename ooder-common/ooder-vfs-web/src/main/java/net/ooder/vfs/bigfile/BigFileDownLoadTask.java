package net.ooder.vfs.bigfile;

import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;

import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.vfs.FileView;
import  net.ooder.vfs.sync.TaskResult;

import java.io.*;

import java.util.concurrent.Callable;

public class BigFileDownLoadTask<T extends TaskResult> implements Callable<T> {


    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, BigFileDownLoadTask.class);

    private FileView fileView;
    private MinServerActionContextImpl autoruncontext;

    public BigFileDownLoadTask(FileView fileView) {
        this.fileView = fileView;
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
        TaskResult<String> resultModel = new TaskResult<String>();
        String localPath = fileView.getFileObject().getPath();
        File file = new File(localPath);
        try {
            if (!file.exists() || file.length() == 0) {
                fileView.getFileObject().downLoad();
                resultModel.setData(localPath);
                resultModel.setResult(1);
            }
        } catch (JDSException e) {
            resultModel.setResult(-1);
        }
        return (T) resultModel;
    }

}