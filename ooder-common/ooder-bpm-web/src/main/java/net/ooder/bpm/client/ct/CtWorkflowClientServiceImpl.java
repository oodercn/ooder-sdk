package net.ooder.bpm.client.ct;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.DataEngine;
import net.ooder.bpm.engine.FileEngine;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightPermission;
import net.ooder.command.Command;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.config.ListResultModel;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.esd.annotation.RouteToType;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@EsbBeanAnnotation(id = "BPMC", name = "工作流接口")
public class CtWorkflowClientServiceImpl implements WorkflowClientService {

    private JDSClientService jdsClient;
    private JDSServer jdsServer;
    private DataEngine dataEngine;
    private FileEngine fileEngine;
    private CtBPMCacheManager cacheManager;


    /**
     * 根据Session中的JDSSessionHandle信息取得工作流系统客户 接口WorkflowClientService
     *
     * @return 工作流系统客户接口实现
     * @throws JDSException
     */
    CtWorkflowClientServiceImpl(JDSClientService jdsClient) throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        this.jdsClient = jdsClient;

        String systemCode = jdsClient.getSystemCode();
        cacheManager = CtBPMCacheManager.getInstance();

        this.dataEngine = EsbUtil.parExpression("$DataEngine", DataEngine.class);//new DBDataEngine(systemCode, this);
        dataEngine.setSystemCode(systemCode);
        dataEngine.setWorkflowClient(this);

        this.fileEngine = EsbUtil.parExpression("$FileEngine", FileEngine.class);// new VFSFileEngine(this);
        fileEngine.setSystemCode(systemCode);
        fileEngine.setWorkflowClient(this);
    }

    CtWorkflowClientServiceImpl(JDSSessionHandle sessionHandle, String systemCode) throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        this.jdsClient = jdsServer.getJDSClientService(sessionHandle, jdsClient.getConfigCode());
        cacheManager = CtBPMCacheManager.getInstance();
        this.dataEngine = EsbUtil.parExpression("$DataEngine", DataEngine.class);//new DBDataEngine(systemCode, this);
        dataEngine.setSystemCode(systemCode);
        dataEngine.setWorkflowClient(this);

        this.fileEngine = EsbUtil.parExpression("$FileEngine", FileEngine.class);// new VFSFileEngine(this);
        fileEngine.setSystemCode(systemCode);
        fileEngine.setWorkflowClient(this);
    }


    public String getSystemCode() {
        return this.getJdsClient().getSystemCode();
    }

    public JDSSessionHandle getSessionHandle() {
        return this.getJdsClient().getSessionHandle();
    }

    // --------------------------------------------- 登陆注销操作

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#connect(net.ooder.bpm.engine.ConnectInfo)
     */
    public void connect(ConnectInfo connInfo) throws JDSException {

        this.getJdsClient().connect(connInfo);

    }

    /*
     * @see net.ooder.bpm.engine.WorkflowClientService#disconnect()
     */
    public ReturnType disconnect() throws JDSException {
        this.getJdsClient().disconnect();

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public JDSClientService getJdsClient() {
        return jdsClient;
    }

    public void setJdsClient(JDSClientService jdsClient) {
        this.jdsClient = jdsClient;
    }

    public ConnectInfo getConnectInfo() {
        return this.getJdsClient().getConnectInfo();
    }

    @Override
    public OrgManager getOrgManager() {
        return OrgManagerFactory.getOrgManager();
    }

    @Override
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return cacheManager.getProcessDefVersionList(condition);
    }

    @Override
    public ListResultModel<List<ProcessDef>> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return cacheManager.getProcessDefList(condition, filter, this.fillInUserID(ctx));
    }

    @Override
    public ProcessDef getProcessDef(String processDefID) throws BPMException {
        ProcessDef processDef = null;
        try {
            processDef = cacheManager.getProcessDef(processDefID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return processDef;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion(String processDefVersionID) throws BPMException {
        ProcessDefVersion processDefVersion = null;
        try {
            processDefVersion = cacheManager.getProcessDefVersion(processDefVersionID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return processDefVersion;
    }

    @Override
    public ActivityDef getActivityDef(String activityDefID) throws BPMException {
        ActivityDef activityDef = null;
        try {
            activityDef = cacheManager.getActivityDef(activityDefID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return activityDef;
    }

    @Override
    public RouteDef getRouteDef(String routeDefId) throws BPMException {
        RouteDef routeDef = null;
        try {
            routeDef = cacheManager.getRouteDef(routeDefId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return routeDef;
    }

    @Override
    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        ctx = this.fillInUserID(ctx);

        return cacheManager.getProcessInstList(condition, conditionEnus, filter, ctx);
    }

    @Override
    public ListResultModel<List<ActivityInst>> getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        ctx = this.fillInUserID(ctx);
        return cacheManager.getActivityInstList(condition, conditionEnus, filter, ctx);
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        ctx = this.fillInUserID(ctx);
        return cacheManager.getActivityInstHistoryList(condition, conditionEnus);
    }

    @Override
    public List<ActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        List<ActivityInst> activityList = null;
        try {
            activityList = cacheManager.getActivityInstListByOutActvityInstHistory(activityInstHistoryId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return activityList;
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        List<ActivityInstHistory> activityInstHistory = null;
        try {
            activityInstHistory = cacheManager.getActivityInstHistoryListByActvityInst(actvityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return activityInstHistory;
    }

    @Override
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        ctx = this.fillInUserID(ctx);
        List<ActivityInstHistory> activityInstHistory = cacheManager.getLastActivityInstHistoryListByActvityInst(actvityInstId);
        return activityInstHistory;
    }

    @Override
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx, boolean noSplit) throws BPMException {
        ctx = this.fillInUserID(ctx);
        List<ActivityInstHistory> activityInstHistory = cacheManager.getLastActivityInstHistoryListByActvityInst(actvityInstId);
        return activityInstHistory;
    }

    @Override
    public List<ActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException {

        List<ActivityInstHistory> activityInstHistory = cacheManager.getAllOutActivityInstHistoryByActvityInstHistory(historyHisroryId, noSplit);
        return activityInstHistory;
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        ctx = this.fillInUserID(ctx);
        return cacheManager.getActivityInstHistoryListByProcessInst(processInstId);
    }

    @Override
    public ProcessInst getProcessInst(String processInstID) throws BPMException {
        ProcessInst processInst = null;
        try {
            processInst = cacheManager.getProcessInst(processInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return processInst;
    }

    @Override
    public ReturnType updateProcessInstName(String processInstId, String name) throws BPMException {

        return cacheManager.updateProcessInstName(processInstId, name);
    }

    @Override
    public ReturnType updateProcessInstUrgency(String processInstId, String urgency) throws BPMException {
        return cacheManager.updateProcessInstUrgency(processInstId, urgency);
    }

    @Override
    public ActivityInst getActivityInst(String activityInstID) throws BPMException {
        ActivityInst activityInst = null;
        try {
            activityInst = cacheManager.getActivityInst(activityInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return activityInst;
    }

    @Override
    public RouteInst getRouteInst(String routeInstId) throws BPMException {
        try {
            return cacheManager.getRouteInst(routeInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ActivityInstHistory getActivityInstHistory(String activityInstHistoryID) throws BPMException {
        ActivityInstHistory activityInstHistory = null;
        try {
            activityInstHistory = cacheManager.getActivityInstHistory(activityInstHistoryID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return activityInstHistory;
    }

    @Override
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, Map<RightCtx, Object> ctx) throws BPMException {
        ProcessInst processInst = null;
        try {
            processInst = cacheManager.newProcess(processDefId, processInstName).getProcessInst();
            ActivityInst inst = processInst.getActivityInstList().get(0);
            fileEngine.startProcessInst(processInst.getProcessInstId(), inst.getActivityInstId(), ctx);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return processInst;
    }

    @Override
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        ProcessInst processInst = null;
        try {
            processInst = cacheManager.newProcess(processDefId, processInstName).getProcessInst();
            ActivityInst inst = processInst.getActivityInstList().get(0);
            fileEngine.startProcessInst(processInst.getProcessInstId(), inst.getActivityInstId(), ctx);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return processInst;
    }

    @Override
    public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        List<RouteDef> routeDefs = null;
        try {
            routeDefs = cacheManager.getNextRoutes(startActivityInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return routeDefs;
    }

    @Override
    //根据当前环境重新组装对象完成远程调用
    public ReturnType routeTo(String startActivityInstID, List<String> nextActivityDefIDs, List<Map<RightCtx, Object>> ctxs) throws BPMException {
        ReturnType returnType = new ReturnType(ReturnType.MAINCODE_FAIL);
        if (nextActivityDefIDs.size() == 1) {
            RouteBean bean = new RouteBean();
            bean.setAction(RouteToType.RouteTo);
            bean.setActivityInstId(startActivityInstID);
            bean.setNextActivityDefId(nextActivityDefIDs.get(0));
            if (ctxs.size() > 0) {
                Map<RightCtx, Object> ctx = ctxs.get(0);
                List<String> performIds = (List<String>) ctx.get(RightCtx.PERFORMERS);
                if (performIds.size() > 0) {
                    StringBuffer performStr = new StringBuffer("");
                    for (String personId : performIds) {
                        performStr.append(personId);
                        performStr.append(";");
                    }
                    Perform perform = new Perform();
                    perform.setPerforms(performStr.substring(0, performStr.length() - 1));
                    bean.setPerforms(perform);
                }
                List<String> readIds = (List<String>) ctx.get(RightCtx.READERS);
                if (readIds != null && readIds.size() > 0) {
                    StringBuffer readStr = new StringBuffer("");
                    for (String personId : readIds) {
                        readStr.append(personId);
                        readStr.append(";");
                    }
                    Perform reader = new Perform();
                    reader.setReaders(readStr.substring(0, readStr.length() - 1));
                    bean.setReaders(reader);
                }

            }
            Map<RightCtx, Object> ctxMap = ctxs.get(0);
            this.fillInUserID(ctxMap);
            returnType = cacheManager.routeTo(bean);
            dataEngine.routeTo(startActivityInstID, nextActivityDefIDs.get(0), ctxMap);
            ActivityInst activityInst = this.getActivityInst(startActivityInstID);

        } else {
            RouteToBean bean = new RouteToBean();
            bean.setActivityInstId(startActivityInstID);
            int k = 0;
            for (String nextActivityDefID : nextActivityDefIDs) {
                RouteBean subbean = new RouteBean();
                subbean.setAction(RouteToType.Multirouteto);
                subbean.setActivityInstId(startActivityInstID);
                subbean.setNextActivityDefId(nextActivityDefID);
                if (ctxs.size() > 0) {
                    Map<RightCtx, Object> ctx = ctxs.get(k);
                    List<String> performIds = (List<String>) ctx.get(RightCtx.PERFORMERS);
                    if (performIds.size() > 0) {
                        StringBuffer performStr = new StringBuffer("");
                        for (String personId : performIds) {
                            performStr.append(personId);
                            performStr.append(";");
                        }
                        Perform perform = new Perform();
                        perform.setPerforms(performStr.substring(0, performStr.length() - 1));
                        subbean.setPerforms(perform);
                    }
                    List<String> readIds = (List<String>) ctx.get(RightCtx.READERS);
                    if (readIds != null && readIds.size() > 0) {
                        StringBuffer readStr = new StringBuffer("");
                        for (String personId : readIds) {
                            readStr.append(personId);
                            readStr.append(";");
                        }
                        Perform reader = new Perform();
                        reader.setReaders(readStr.substring(0, readStr.length() - 1));
                        subbean.setReaders(reader);
                    }
                    PerformBean performBean = new PerformBean();
                    performBean.setPerformSelect(subbean);
                    bean.getMultiSelect().put(nextActivityDefID, performBean);
                    k = k + 1;
                }
            }
            returnType = cacheManager.mrouteto(bean);
        }
        if (this.getActivityInstHistoryListByActvityInst(startActivityInstID, this.fillInUserID(null)).size() > 0) {
            ActivityInstHistory history = this.getActivityInstHistoryListByActvityInst(startActivityInstID, this.fillInUserID(null)).get(0);
            fileEngine.saveActivityHistoryInst(startActivityInstID, history.getActivityHistoryId(), this.fillInUserID(null));
            dataEngine.saveActivityHistoryInst(startActivityInstID, history.getActivityHistoryId(), this.fillInUserID(null));
        }

        this.cacheManager.clearActivityInstCache(startActivityInstID);
        return returnType;
    }

    @Override
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {
        ActivityInst activityInst = null;
        try {
            activityInst = cacheManager.copyActivityInstByHistory(activityHistoryInstId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        ctx = this.fillInUserID(ctx);
        dataEngine.copyActivityInstByHistory(activityInst.getActivityInstId(), activityHistoryInstId, this.fillInUserID(ctx));
        return activityInst;

    }

    @Override
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx, boolean isnew) throws BPMException {
        ActivityInst activityInst = null;
        try {
            activityInst = cacheManager.copyActivityInstByHistory(activityHistoryInstId, isnew);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        dataEngine.copyActivityInstByHistory(activityInst.getActivityInstId(), activityHistoryInstId, this.fillInUserID(ctx));
        return activityInst;
    }

    @Override
    public ActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        ActivityInst activityInst = null;
        try {
            activityInst = cacheManager.newActivityInstByActivityDefId(processInstId, activityDefId);

        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return activityInst;
    }

    @Override
    public ReturnType copyTo(String activityHistoryInstId, List readers) throws BPMException {
        ReturnType type = null;
        try {
            type = cacheManager.copyTo(activityHistoryInstId, readers);

        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return type;
    }

    @Override
    public boolean canTakeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {

        Boolean canTakeBack = null;
        try {
            canTakeBack = cacheManager.canTakeBack(activityInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return canTakeBack;
    }

    @Override
    public ReturnType takeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.takeBack(activityInstID);

        return type;
    }

    @Override
    public boolean canEndRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {

        Boolean canEndRead = null;
        try {
            canEndRead = cacheManager.canEndRead(activityInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return canEndRead;
    }

    @Override
    public ReturnType endRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.endRead(activityInstID);

        return type;
    }

    @Override
    public ReturnType endTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.endTask(activityInstID);

        return type;
    }

    @Override
    public ReturnType abortedTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.abortedTask(activityInstID);

        return type;
    }

    @Override
    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.deleteHistory(activityInstHistoryID);

        return type;
    }

    @Override
    public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.restoreHistory(activityInstHistoryID);

        return type;
    }

    @Override
    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.clearHistory(activityInstHistoryID);

        return type;
    }

    @Override
    public boolean canRouteBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Boolean canEndRead = cacheManager.canRouteBack(activityInstID);

        return canEndRead;
    }

    @Override
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstID, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        List<ActivityInstHistory> activityInstHistory = null;
        try {
            activityInstHistory = cacheManager.getRouteBackActivityHistoryInstList(activityInstID);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return activityInstHistory;
    }

    @Override
    public ReturnType routeBack(String fromActivityInstID, String toActivityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.routeBack(fromActivityInstID, toActivityInstHistoryID);
        ctx = this.fillInUserID(ctx);
        dataEngine.routeBack(fromActivityInstID, toActivityInstHistoryID, ctx);
        return type;
    }

    @Override
    public boolean canPerform(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        Boolean canPerform = null;
        try {
            canPerform = cacheManager.canPerform(activityInstID);
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return canPerform;
    }

    @Override
    public boolean canSignReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {

        Boolean canSignReceive = cacheManager.canSignReceive(activityInstID);
        ctx = this.fillInUserID(ctx);
        return canSignReceive;
    }

    @Override
    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.signReceive(activityInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.signReceive(activityInstID, ctx);
        return type;
    }

    @Override
    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.suspendActivityInst(activityInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.suspendActivityInst(activityInstID, ctx);
        return type;
    }

    @Override
    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.resumeActivityInst(activityInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.resumeActivityInst(activityInstID, ctx);
        return type;
    }

    @Override
    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.suspendProcessInst(processInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.suspendProcessInst(processInstID, ctx);
        return type;
    }

    @Override
    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.resumeProcessInst(processInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.resumeProcessInst(processInstID, ctx);
        return type;
    }

    @Override
    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.abortProcessInst(processInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.abortProcessInst(processInstID, ctx);
        return type;
    }

    @Override
    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.completeProcessInst(processInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.completeProcessInst(processInstID, ctx);
        return type;
    }

    @Override
    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        ReturnType type = cacheManager.deleteProcessInst(processInstID);
        ctx = this.fillInUserID(ctx);
        dataEngine.deleteProcessInst(processInstID, ctx);
        return type;
    }

    @Override
    public void beginTransaction() throws BPMException {

    }

    @Override
    public void commitTransaction() throws BPMException {

    }

    @Override
    public void rollbackTransaction() throws BPMException {

    }

    @Override
    public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException {
        try {
            return cacheManager.getActivityDefRight(activityDefId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }

    }

    @Override
    public List<AttributeDef> getActivityDefAttributes(String activityDefId) throws BPMException {

        return cacheManager.getActivityDefAttributes(activityDefId);

    }

    @Override
    public ActivityDefEvent getActivityDefEventAttribute(String activityDefId) throws BPMException {

        return null;
    }


    @Override
    public ActivityDefDevice getActivityDefDeviceAttribute(String activityDefId) throws BPMException {
        return null;
    }


    @Override
    public List<Person> getActivityInstPersons(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
        List<Person> personList = new ArrayList<Person>();
        try {
            List<String> personIdList = cacheManager.getActivityInstRightAttribute(activityInstId, attName);
            for (String personId : personIdList) {
                Person person = null;
                try {
                    person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
                    personList.add(person);
                } catch (PersonNotFoundException e) {
                    //e.printStackTrace();
                }
            }

        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return personList;
    }

    @Override
    public List<DeviceEndPoint> getActivityInstDevices(String activityInstId, ActivityInstRightAtt attName) throws BPMException {

        return null;
    }

    @Override
    public List<Command> getActivityInstCommands(String activityInstId) throws BPMException {
        return null;
    }

    @Override
    public List<Person> getActivityInstHistoryPersons(String activityInstHistoryId, ActivityInstHistoryAtt attName) throws BPMException {
        List<Person> personList = new ArrayList<Person>();
        try {
            List<String> personIdList = CtBPMCacheManager.getInstance().getActivityHistoryRightAttribute(activityInstHistoryId, attName);
            for (String personId : personIdList) {
                Person person = null;
                try {
                    person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
                    personList.add(person);
                } catch (PersonNotFoundException e) {
                    //e.printStackTrace();
                }
            }

        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return personList;
    }


    @Override
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return false;
    }

    @Override
    public List<RightPermission> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public DataEngine getMapDAODataEngine() {
        return this.dataEngine;
    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID) throws BPMException {

        return dataEngine.getActivityInstFormValues(activityInstID, this.getConnectInfo().getUserID());
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, DataMap dataMap) throws BPMException {
        dataEngine.updateActivityHistoryFormValues(activityHistoryID, this.getConnectInfo().getUserID(), dataMap);
    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID) throws BPMException {
        return dataEngine.getActivityHistoryFormValues(activityHistoryID, this.getConnectInfo().getUserID());
    }

    @Override
    public void updateActivityInstFormValues(String activityInstID, DataMap dataMap) throws BPMException {
        dataEngine.updateActivityInstFormValues(activityInstID, this.getConnectInfo().getUserID(), dataMap);
    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId) throws BPMException {
        return dataEngine.getProcessInstFormValues(processInstId, this.getConnectInfo().getUserID());
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, DataMap dataMap) throws BPMException {
        dataEngine.updateProcessInstFormValues(processInstId, this.getConnectInfo().getUserID(), dataMap);
    }


    @Override
    public ReturnType display(String activityInstId) throws BPMException {
        try {

            return cacheManager.display(activityInstId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }
    }

    @Override
    public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        try {
            cacheManager.addPersonTagToHistory(activityInstHistoryID, tagName, this.fillInUserID(ctx));

        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    @Override
    public ReturnType deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        try {
            cacheManager.deletePersonTagToHistory(activityInstHistoryID, tagName, this.fillInUserID(ctx));
        } catch (JDSException e) {
            throw new BPMException(e);
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    @Override
    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException {
        try {
            return cacheManager.getFirstActivityDefInProcess(processDefVersionId);
        } catch (JDSException e) {
            throw new BPMException(e);
        }

    }

    @Override
    public void setOrgManager(OrgManager orgManager) {

    }

    @Override
    public FileEngine getfileEngine() {
        return null;
    }

    private Map<RightCtx, Object> fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put(RightCtx.USERID, this.getJdsClient().getConnectInfo().getUserID());
        return result;
    }

}
