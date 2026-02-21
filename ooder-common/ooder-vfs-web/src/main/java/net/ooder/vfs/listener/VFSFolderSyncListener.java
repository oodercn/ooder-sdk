package net.ooder.vfs.listener;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.StringUtility;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.vfs.Folder;
import  net.ooder.vfs.VFSConstants;
import  net.ooder.vfs.VFSException;
import  net.ooder.vfs.ct.CtCacheManager;
import  net.ooder.vfs.ct.CtVfsFactory;
import  net.ooder.vfs.engine.event.FolderEvent;
import  net.ooder.vfs.engine.event.FolderListener;

@EsbBeanAnnotation(id = "VFSFolderSyncListener", name = "文件夹本地同步", expressionArr = "VFSFolderSyncListener()", desc = "文件夹本地同步", flowType = EsbFlowType.listener)
public class VFSFolderSyncListener implements FolderListener {

    protected static Log log = LogFactory.getLog(OrgConstants.VFSCONFIG_KEY.getType(), VFSFolderSyncListener.class);


    void clearCacheByPath(String paths) {
        if (paths.indexOf(";") > -1) {
            String[] folderpaht = StringUtility.split(";", paths);
            for (String path : folderpaht) {
                try {
                    CtVfsFactory.getCtVfsService().clearCache(path);
                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                CtVfsFactory.getCtVfsService().clearCache(paths);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void create(FolderEvent event) throws VFSException {
        log.info("start create " + event.getFolderPath());
        String path = event.getFolderPath();
        Folder folder = null;
        try {
            if (!CtCacheManager.getInstance().filePathCache.containsKey(path)) {
                folder = CtVfsFactory.getCtVfsService().getFolderByPath(path);
                if (folder != null) {
                    Folder pfolder = folder.getParent();
                    pfolder.getChildrenIdList().add(folder.getID());
                }
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lock(FolderEvent event) throws VFSException {

    }

    @Override
    public void beforeReName(FolderEvent event) throws VFSException {
        log.info("start beforReName " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reNameEnd(FolderEvent event) throws VFSException {


    }

    @Override
    public void beforeDelete(FolderEvent event) throws VFSException {
        log.info("start beforDelete " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteing(FolderEvent event) throws VFSException {

    }

    @Override
    public void deleteEnd(FolderEvent event) throws VFSException {
        clearCacheByPath(event.getFolderPath());
    }

    @Override
    public void save(FolderEvent event) throws VFSException {
        log.info("start save " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeCopy(FolderEvent event) throws VFSException {

    }

    @Override
    public void copying(FolderEvent event) throws VFSException {

    }

    @Override
    public void copyEnd(FolderEvent event) throws VFSException {
        log.info("start copyEnd " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeMove(FolderEvent event) throws VFSException {
        log.info("start beforMove " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moving(FolderEvent event) throws VFSException {

    }

    @Override
    public void moveEnd(FolderEvent event) throws VFSException {
        log.info("start moveEnd " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeClean(FolderEvent event) throws VFSException {
        log.info("start beforClean " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanEnd(FolderEvent event) throws VFSException {

    }

    @Override
    public void reStore(FolderEvent event) throws VFSException {
        log.info("start restore " + event.getFolderPath());
        String path = event.getFolderPath();
        try {
            CtVfsFactory.getCtVfsService().clearCache(path);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSystemCode() {
        return VFSConstants.CONFIG_KEY;
    }
}
