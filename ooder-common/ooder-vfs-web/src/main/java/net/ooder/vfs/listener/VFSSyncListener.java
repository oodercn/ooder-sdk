package net.ooder.vfs.listener;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.StringUtility;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.VFSException;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.engine.event.FileEvent;
import  net.ooder.vfs.engine.event.FileListener;

@EsbBeanAnnotation(id = "VFSSyncListener", name = "文档本地同步", expressionArr = "VFSSyncListener()", desc = "文档本地同步", flowType = EsbFlowType.listener)
public class VFSSyncListener implements FileListener {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSSyncListener.class);



   void clearCacheByPath(String paths){
        if (paths.indexOf(";")>-1){
            String[] folderpaht=StringUtility.split(";",paths);
            for(  String path: folderpaht){
                try {
                    CtVfsFactory.getCtVfsService().clearFileCache(path);
                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        }else{
            try {
                CtVfsFactory.getCtVfsService().clearFileCache(paths);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void beforeCopy(FileEvent event) throws VFSException {

    }

    @Override
    public void create(FileEvent event) throws VFSException {



    }


    @Override
    public void upLoadError(FileEvent event) throws VFSException {

    }

    @Override
    public void upLoadEnd(FileEvent event) throws VFSException {
        log.info("start clear "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void upLoading(FileEvent event) throws VFSException {

    }

    @Override
    public void beforeUpLoad(FileEvent event) throws VFSException {

    }

    @Override
    public void beforeDownLoad(FileEvent event) throws VFSException {

    }

    @Override
    public void downLoading(FileEvent event) throws VFSException {

    }

    @Override
    public void downLoadEnd(FileEvent event) throws VFSException {

    }

    @Override
    public void beforeReName(FileEvent event) throws VFSException {
        log.info("start beforeReName "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void reNameEnd(FileEvent event) throws VFSException {
        log.info("start reNameEnd "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void reStore(FileEvent event) throws VFSException {

    }

    @Override
    public void share(FileEvent event) throws VFSException {

    }

    @Override
    public void clear(FileEvent event) throws VFSException {

    }

    @Override
    public void open(FileEvent event) throws VFSException {

    }

    @Override
    public void send(FileEvent event) throws VFSException {

    }

    @Override
    public void deleteEnd(FileEvent event) throws VFSException {
        log.info("start deleteEnd "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void beforeDelete(FileEvent event) throws VFSException {

    }

    @Override
    public void moveEnd(FileEvent event) throws VFSException {
        log.info("start moveEnd "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void beforeMove(FileEvent event) throws VFSException {

    }

    @Override
    public void updateEnd(FileEvent event) throws VFSException {
        log.info("start clear "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void beforeUpdate(FileEvent event) throws VFSException {

    }

    @Override
    public void save(FileEvent event) throws VFSException {
        log.info("start save "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public void copyEnd(FileEvent event) throws VFSException {
        log.info("start copyEnd "+ event.getFilePath());
        String path = event.getFilePath();
        clearCacheByPath(path);
    }

    @Override
    public String getSystemCode() {
        return VFSConstants.CONFIG_KEY;
    }
}
