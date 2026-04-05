/**
 * $RCSfile: VfsDataEngine.java,v $
 * $Revision: 1.4 $
 * $Date: 2016/01/23 16:29:55 $
 * Copyright: Copyright (c) 2008
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
package net.ooder.bpm.engine.data;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.ct.CtBPMCacheManager;
import net.ooder.bpm.client.data.DataConstants;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.DataEngine;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;

import java.util.*;

public class IndexDataEngine implements DataEngine {

    private static IndexDataEngine mapDAODataEngine;

    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, IndexDataEngine.class);
    private WorkflowClientService workflowClient;

    private String systemCode;

    public IndexDataEngine(String systemCode, WorkflowClientService clientService) throws BPMException {
        this.systemCode = systemCode;
        this.workflowClient = clientService;


    }


    private boolean hasId(List<FormClassBean> activityFormList, String id) {

        boolean hasId = false;
        for (int k = 0; k < activityFormList.size(); k++) {
            FormClassBean activityForm = activityFormList.get(k);
            String formId = activityForm.getId();
            if (formId.equals(id)) {
                return true;
            }
        }
        return hasId;

    }

    private List compForm(List<FormClassBean> activityFormList, List<FormClassBean> prcoessFormList) {
        List formList = activityFormList;

        for (int k = 0; k < prcoessFormList.size(); k++) {
            FormClassBean processForm = prcoessFormList.get(k);
            String processFormId = processForm.getId();
            boolean hasId = hasId(activityFormList, processFormId);
            if (!hasId) {
                formList.add(processForm);
            }
        }
        return formList;

    }


    @Override
    public ProcessDefForm getProcessDefForm(String processDefVersionID, Map<RightCtx, Object> ctx) throws BPMException {
        ProcessDefForm processDefForm = CtBPMCacheManager.getInstance().getProcessFormDef(processDefVersionID);

        return processDefForm;

    }

    private DataMap getNewDataMap(Object source, String userId) throws BPMException {
        LuceneDataMap dataMap = new LuceneDataMap(source, userId, this.systemCode);
        return dataMap;
    }

    private DataMap getActivityInstMapDAOMapFormDb(String activityInstID, String userId) throws BPMException {
        ActivityInst eiActivityInst = null;
        try {
            eiActivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInstID);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return this.getNewDataMap(eiActivityInst, userId);
    }

    private DataMap getActivityInstHistoryMapDAOMapFormDb(String activityInstHistoryId, String userId) throws BPMException {
        ActivityInstHistory his = null;
        try {
            his = CtBPMCacheManager.getInstance().getActivityInstHistory(activityInstHistoryId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return this.getNewDataMap(his, userId);
    }

    private DataMap getProcessInstMapDAOMapFormDb(String processInstId, String userId) throws BPMException {
        ProcessInst eiProcessInst = null;
        try {
            eiProcessInst = CtBPMCacheManager.getInstance().getProcessInst(processInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return this.getNewDataMap(eiProcessInst, null);
    }

    public List<FormClassBean> getProcessDefAllDataFormDef(String processDefID, Map<RightCtx, Object> ctx) throws BPMException {

        List<FormClassBean> formList = new ArrayList<FormClassBean>();
        try {
            ProcessDefVersion processDefVersion = processDefVersion = CtBPMCacheManager.getInstance().getProcessDefVersion(processDefID);
        } catch (JDSException e) {
            e.printStackTrace();
        }

//
//        String path = OrgConstants.WORKFLOWBASEPATH + OrgConstants.WORKFLOWFORMPATH + processDefID + "/";
//
//        formList = getVfsFormClassBeanListByPath(path, formList);
//
        return formList;
    }

    public void updateProcessInstMapDAO(String processInstID, DataMap formdata, String userId) throws BPMException {

        LuceneDataMap processDatamap = (LuceneDataMap) this.getProcessInstMapDAOMap(processInstID);

        processDatamap.getDataFactory().update(formdata.clone(processDatamap));


    }

    public void updateActivityInstMapDAO(String activityInstID, DataMap formdata, String userId) throws BPMException {

        LuceneDataMap datamap = (LuceneDataMap) formdata;


        String biaoti = null;

        for (Iterator<String> it = datamap.keySet().iterator(); it.hasNext(); ) {
            String formId = it.next();
            Map formmap = (Map) datamap.get(formId);
            if (formmap.containsKey("biaoti")) {
                biaoti = (String) formmap.get("biaoti");
            } else if (formmap.containsKey("title")) {
                biaoti = (String) formmap.get("title");
            }
        }

        if (biaoti != null) {

            try {
                ActivityInst inst = CtBPMCacheManager.getInstance().getActivityInst(activityInstID);
                inst.getProcessInst().updateProcessInstName(biaoti);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }


        Map userMap = (Map) JDSActionContext.getActionContext().getContext().get(activityInstID);
        if (userMap == null) {
            userMap = new HashMap();
        }
        DataMap dataMap = (DataMap) userMap.get(userId);
        JDSActionContext.getActionContext().getContext().put(activityInstID, userMap);

        if (dataMap != null) {
            userMap.put(userId, dataMap);
            JDSActionContext.getActionContext().getContext().put(activityInstID, userMap);
            datamap.putAll(dataMap);
        }
        datamap.getDataFactory().update(datamap);

    }

    public void updateActivityInstHisMapDAO(String activityInstHistoryId, DataMap formdata, String userId) throws BPMException {


        LuceneDataMap hismap = (LuceneDataMap) this.getActivityInstHistoryMapDAOMap(activityInstHistoryId, userId);
        hismap.getDataFactory().update(formdata.clone(hismap));


    }

    /**
     * 为兼容就有MAPDAO程序处理
     *
     * @param activityDefId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<FormClassBean> getActivityDefAllMapDaoDataFormDef(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        List<FormClassBean> formList = new ArrayList<FormClassBean>();
        String mdforms = null;
        ActivityDef activityDef = null;
        try {
            activityDef = CtBPMCacheManager.getInstance().getActivityDef(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        if ((activityDef.getAppAttribute("APPLICATION" + "." + DataConstants.JDSFORM)) != null) {
            mdforms = activityDef.getAppAttribute("APPLICATION" + "." + DataConstants.JDSFORM).toString();
        }
        String[] formsArr = null;
        if (mdforms != null) {
            formsArr = mdforms.split(":");
            for (int k = 0; formsArr.length > k; k++) {
                FormClassBean formClassBean = null;
                try {
                    formClassBean = FormClassFactory.getInstance().getFormClassBeanInst(formsArr[k]);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BPMException(e);
                }

            }
        }

        return formList;
    }

    /**
     * @param activityDefId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public String getActivityDefMaoDaoMainFormId(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        ActivityDef activityDef = null;
        try {
            activityDef = CtBPMCacheManager.getInstance().getActivityDef(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        String mainFormId = "MAINFORM";
        if ((activityDef.getAppAttribute("APPLICATION" + "." + DataConstants.MAINBEAN)) != null) {
            mainFormId = activityDef.getAppAttribute("APPLICATION" + "." + DataConstants.MAINBEAN).toString();
        }

        return mainFormId;

    }

    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);
        try {
            List<ActivityInstHistory> activityInstHistorys = CtBPMCacheManager.getInstance().getActivityInstHistoryListByProcessInst(processInstID).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        if (ctx != null && ctx.get(RightCtx.CONTEXT_ACTIVITYINSTHISTORY) != null) {
            LuceneDataMap datamap = (LuceneDataMap) this.getActivityInstHistoryMapDAOMap((String) ctx.get(RightCtx.CONTEXT_ACTIVITYINSTHISTORY), userId);
            this.updateProcessInstMapDAO(processInstID, datamap, userId);

        }

        // LuceneDataMap datamap=(LuceneDataMap)getProcessInstMapDAOMapFormDb( processInstID, userId);

        // try {
        // if (datamap.getSource() instanceof EIProcessInst){
        // datamap.getDataFactory().update(datamap);
        // }else{
        // LuceneDataMap processDatamap= (LuceneDataMap) this.getProcessInstMapDAOMap(processInstID);
        // processDatamap.getDataFactory().update(datamap);
        // }
        // } catch (MapDAOException e) {
        // e.printStackTrace();
        // new BPMException(e);
        // }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createProcessInst(String processInstId, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    //
    // public String getTableName(String processInstId) throws BPMException{
    // EIProcessInst eiProcessInst=this.processInstMgr.loadByKey(processInstId);
    // String tableName=eiProcessInst.getAttributeValue(Attribute.TYPE_APPLICATION+"."+"MAPDAOTABLENAME");
    //
    // if (tableName==null ||tableName.equals("")){
    //
    // tableName= new MapDaoDataDAO().getTableName();
    // EIAttributeInst eiAttributeInst = EIAttributeInstManager.getInstance()
    // .createAttributeInst();
    // eiAttributeInst.setId(UUIDGenerator.genUUID());
    // eiAttributeInst.setName("MAPDAOTABLENAME");
    // eiAttributeInst.setInterpretedValue(tableName);
    // eiProcessInst.setAttribute(Attribute.TYPE_APPLICATION, eiAttributeInst);
    //
    // }
    // return tableName;
    //
    // }

    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        try {

        } catch (Exception e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL, e.getMessage());
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType endRead(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public Filter getActivityInstListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Filter getProcessDefListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Object getProcessDefVersionDataAttribute(String processDefVersionId, String attName, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Filter getProcessInstListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    public Object getRouteDefDataAttribute(String routeDefId, String attName, Map<RightCtx, Object> ctx) throws BPMException {

        return null;
    }

    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        ActivityInst eiActivityInst = null;
        try {
            eiActivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        DataMap dataMap = getActivityInstMapDAOMap(activityInstId, userId);

        this.updateActivityInstMapDAO(activityInstId, dataMap, userId);

        try {
            ActivityDefPerformtype performType = CtBPMCacheManager.getInstance().getActivityDefRight(activityDefId).getPerformType();
            ActivityDefPerformSequence performSeqence = CtBPMCacheManager.getInstance().getActivityDefRight(activityDefId).getPerformSequence();

            if (eiActivityInst.getProcessInst().getCopyNumber() <= 1) {// 没有分裂发生时应该更新流程数据
                // &&!this.getProcessInstMapDAOMap(eiActivityInst.getProcessInstId()).isEmpty()
                this.updateProcessInstMapDAO(eiActivityInst.getProcessInstId(), dataMap, userId);
                updateProcessVfs(eiActivityInst.getProcessInstId(), activityInstId, userId);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        try {
            String processInstId = CtBPMCacheManager.getInstance().getActivityInst(activityInstId).getProcessInstId();
            DataMap dataMap = getActivityInstMapDAOMap(activityInstId, userId);
            this.updateActivityInstHisMapDAO(activityInstHistoryId, dataMap, userId);
            updateActivityInstHisVfs(activityInstHistoryId, userId);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }


    //更新VFS资源
    public void updateProcessVfs(String procssInstId, String activityInstId, String userId) throws JDSException {
        ProcessInst processInst = CtBPMCacheManager.getInstance().getProcessInst(procssInstId);
        ActivityInst inst = CtBPMCacheManager.getInstance().getActivityInst(activityInstId);
//        String spath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" +activityInstId;
//        String tpath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" +inst.getProcessInstId();;
//        CtVfsFactory.getCtVfsService().mkDir(spath);
//        CtVfsFactory.getCtVfsService().cloneFolder(spath, tpath);

    }


    //更新VFS资源
    public void updateActivityInstHisVfs(String activityInstHistoryId, String userId) throws JDSException {
        ActivityInstHistory history = CtBPMCacheManager.getInstance().getActivityInstHistory(activityInstHistoryId);
        ActivityInst inst = CtBPMCacheManager.getInstance().getActivityInst(history.getActivityInstId());
//        String spath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" + inst.getActivityInstId();
//        String tpath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" + history.getActivityHistoryId();
//        CtVfsFactory.getCtVfsService().mkDir(spath);
//        CtVfsFactory.getCtVfsService().cloneFolder(spath, tpath);

    }


    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        ActivityInst inst = null;
        try {
            inst = CtBPMCacheManager.getInstance().getActivityInst(activityInstID);
//                    String spath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" +inst.getProcessInstId();
//                    String tpath = OrgConstants.CMAILROOTPATH + inst.getProcessInstId() + "/" + inst.getActivityInstId();
//                    CtVfsFactory.getCtVfsService().mkDir(spath);
//                    CtVfsFactory.getCtVfsService().cloneFolder(spath, tpath);

        } catch (JDSException e) {
            e.printStackTrace();
        }

        // this.updateActivityInstMapDAO(activityInstID,
        // this.activityInstMgr.loadByKey(activityInstID).getProcessInstId(),
        // this.getProcessInstMapDAOMap(this.activityInstMgr.loadByKey(activityInstID).getProcessInstId()).clone(),userId);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);
        try {
            ActivityInst eiActivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType tackBack(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }


    public DataMap getActivityInstMapDAOMap(String activityInstID, String userId) throws BPMException {

        try {
            ActivityInst eiActivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInstID);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        Map userMap = (Map) JDSActionContext.getActionContext().getContext().get(activityInstID);
        if (userMap == null) {
            userMap = new HashMap();
        }
        DataMap dataMap = (DataMap) userMap.get(userId);

        if (dataMap == null) {
            // 没考虑好先采取兼容性设计
            // dataMap=this.getActivityInstMapDAOMapFormDb(activityInstID,userId);
            dataMap = this.getActivityInstMapDAOMapFormDb(activityInstID, null);

            userMap.put(userId, dataMap);
            JDSActionContext.getActionContext().getContext().put(activityInstID, userMap);
        }

        return dataMap;
    }

    public DataMap getActivityInstHistoryMapDAOMap(String activityInstyHistoryId, String userId) throws BPMException {

        Map userMap = (Map) JDSActionContext.getActionContext().getContext().get(activityInstyHistoryId);
        if (userMap == null) {
            userMap = new HashMap();
        }
        DataMap dataMap = (DataMap) userMap.get(userId);
        if (dataMap == null) {
            dataMap = this.getActivityInstHistoryMapDAOMapFormDb(activityInstyHistoryId, userId);
            userMap.put(userId, dataMap);
            JDSActionContext.getActionContext().getContext().put(activityInstyHistoryId, userMap);
        }
        return dataMap;
    }

    public DataMap getProcessInstMapDAOMap(String processInstId) throws BPMException {

        DataMap dataMap = (DataMap) JDSActionContext.getActionContext().getContext().get(processInstId);
        if (dataMap == null) {
            dataMap = this.getProcessInstMapDAOMapFormDb(processInstId, null);
            JDSActionContext.getActionContext().getContext().put(processInstId, dataMap);
        }
        return dataMap;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;

    }

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub

    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityInstFormValues(String activityInstID, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId, String userId) throws BPMException {
        return null;
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, String userId, DataMap dataMap) throws BPMException {

    }

    @Override
    public void setWorkflowClient(WorkflowClientService service) {
        this.workflowClient = service;
    }


    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public CtVfsService getVfsClient() {

        CtVfsService vfsClient = CtVfsFactory.getCtVfsService();
        return vfsClient;
    }
}
