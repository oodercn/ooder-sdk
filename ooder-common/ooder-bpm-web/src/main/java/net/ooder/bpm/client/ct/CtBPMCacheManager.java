package net.ooder.bpm.client.ct;

import net.ooder.annotation.Operator;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.FormClassBean;
import net.ooder.bpm.client.service.*;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.data.FormClassFactory;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.engine.query.WebRightCondition;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstRunStatus;
import net.ooder.bpm.enums.activityinst.ActivityInstStatus;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.route.RouteCondition;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ListResultModel;
import net.ooder.context.JDSActionContext;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.conf.OrgConstants;
import net.ooder.vfs.FileInfo;
import net.ooder.vfs.Folder;
import net.ooder.vfs.ct.CtVfsFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtBPMCacheManager implements Serializable {
    private static CtBPMCacheManager cacheManager;


    public static final String THREAD_LOCK = "Thread Lock";


    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtBPMCacheManager.class);

    // ID caches
    public static final String activityDefCacheName = "CtActivityDefCache";
    public static final String activityInstCacheName = "CtActivityInstCache";
    public static final String activityHistoryCacheName = "CtActivityHistoryCache";
    public static final String activityListenerCacheName = "CtActivityListenerCache";
    public static final String routeDefCacheName = "CtRouteDefCache";
    public static final String routeInstCacheName = "CtRouteInstCache";
    public static final String processInstCacheName = "CtProcessInstCache";
    public static final String processDefCacheName = "CtProcessDefCache";
    public static final String processDefFormCacheName = "CtProcessDefFormCache";
    public static final String processDefVersionCacheName = "CtProcessDefVersionCache";
    public static final String attributeInstCacheName = "CtAttributeInstCache";
    public static final String attributeInstIdsCacheName = "CtAttributeInstIdsCache";
    public static final String attributeInstHistoryIdsCacheName = "CtAttributeInstHistoryIdsCache";
    public static final String attributeInstHistoryCacheName = "CtAttributeInstHistoryCache";
    public static final String attributeDefCacheName = "CtAttributeDefCache";
    public static final String attributeCacheName = "CtaAttributeCacheName";
    public static final String attributeDefListName = "CtAttributeDefListName";

    public static final String participantSelectName = "CtParticipantSelectCache";

    public static final String activityDefRightName = "CtActivityDefRightCacheCache";

    private final RightService rightService;
    private IDSClientService idsClientService;
    private final ProcessDefService processDefService;
    private final ProcessHistoryService processHistoryService;
    private final ProcessInstService processInstService;
    private final FDTService fdtService;

    public static CtBPMCacheManager getInstance() {
        if (cacheManager == null) {
            synchronized (THREAD_LOCK) {
                if (cacheManager == null) {
                    cacheManager = new CtBPMCacheManager();
                }
            }
        }
        return cacheManager;
    }

    /* TODO here */
    private boolean cacheEnabled = true;

    public Cache<String, ActivityDef> activityDefCache;
    public Cache<String, ActivityDefRight> activityDefRightCache;
    private Cache<String, RouteDef> routeDefCache;
    private Cache<String, ProcessDef> processDefCache;
    private Cache<String, ProcessDefForm> processDefFormCache;
    public Cache<String, ProcessDefVersion> processDefVersionCache;
    public Cache<String, AttributeDef> attributeDefCache;
    public Cache<String, List<AttributeDef>> attributeDefListCache;


    public Cache<String, ActivityInst> activityInstCache;
    public Cache<String, ActivityInstHistory> activityHistoryCache;
    public Cache<String, RouteInst> routeInstCache;
    public Cache<String, ProcessInst> processInstCache;
    public Cache<String, AttributeInst> attributeInstCache;
    public Cache<String, AttributeInst> attributeInstHistoryCache;
    public Cache<String, List<AttributeInst>> attributeListCache;
    public Cache<String, Map<String, List<String>>> attributeIdListCache;
    public Cache<String, List<String>> attributeInstHistoryIdsCache;
    public Cache<String, Attribute> attributeCache;


    /**
     * Creates a new cache manager.
     */
    CtBPMCacheManager() {
        //   this.formulaService = (FormulaService) EsbUtil.parExpression("$FormulaService");
        this.fdtService = (FDTService) EsbUtil.parExpression("$FDTService");
        this.rightService = (RightService) EsbUtil.parExpression("$RightService");
        //   this.getIdsClientService() = (getIdsClientService()) EsbUtil.parExpression("$getIdsClientService()");
        this.processDefService = (ProcessDefService) EsbUtil.parExpression("$ProcessDefService");
        this.processHistoryService = (ProcessHistoryService) EsbUtil.parExpression("$ProcessHistoryService");
        this.processInstService = (ProcessInstService) EsbUtil.parExpression("$ProcessInstService");

        initCache();
    }


    public void clearActivityInstCache(String activityInstId) {


        if (activityInstId != null) {


            ActivityInst activityInst = null;
            try {
                activityInst = this.getActivityInst(activityInstId);
                //移除活动实例
                this.activityInstCache.remove(activityInstId);
                //移除活动扩展
                this.attributeListCache.remove(activityInstId);
                //移除活动扩展
                this.attributeIdListCache.remove(activityInstId);


                //移除流程实例
                this.processInstCache.remove(activityInst.getProcessInstId());

                this.attributeListCache.remove(activityInst.getProcessInstId());

                this.attributeIdListCache.remove(activityInst.getProcessInstId());
            } catch (BPMException e) {
                e.printStackTrace();
            }


        }
    }


    public void clearProcessDefCache(String processDefVersionId) throws JDSException {

        ProcessDefVersion version = this.getProcessDefVersion(processDefVersionId);
        if (version != null) {
            List<ActivityDef> activityDefs = version.getAllActivityDefs();
            for (ActivityDef activityDef : activityDefs) {
                if (activityDef != null) {
                    this.activityDefCache.remove(activityDef.getActivityDefId());
                    activityDefRightCache.remove(activityDef.getActivityDefId());
                    attributeDefListCache.remove(activityDef.getActivityDefId());

                }


            }

            List<RouteDef> routeDefs = version.getAllRouteDefs();
            for (RouteDef routeDef : routeDefs) {
                if (routeDef != null) {
                    this.routeDefCache.remove(routeDef.getRouteDefId());
                }
            }

            this.processDefVersionCache.remove(processDefVersionId);
            this.processDefCache.remove(version.getProcessDefId());
            processDefFormCache.remove(processDefVersionId);
            attributeDefListCache.remove(processDefVersionId);


        }
    }


    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {
        cacheEnabled = true;

        activityDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, activityDefCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        activityInstCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, activityInstCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        activityHistoryCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, activityHistoryCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        routeDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, routeDefCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        routeInstCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, routeInstCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        processInstCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, processInstCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        processDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, processDefCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        processDefVersionCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, processDefVersionCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        processDefFormCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, processDefFormCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeInstCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeInstCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeDefCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeDefCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeDefListCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeDefListName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeListCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeInstIdsCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);


        attributeIdListCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, "attributeIdListCache", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);


        attributeInstHistoryIdsCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeInstHistoryIdsCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeInstHistoryCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeInstHistoryCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        attributeCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, attributeCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);


        activityDefRightCache = CacheManagerFactory.createCache(BPMConstants.CONFIG_KEY, activityDefRightName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

    }


    public ActivityDefRight getActivityDefRight(String activityDefId) throws JDSException {
        ActivityDefRight activityDefRight = null;
        if (!cacheEnabled) {
            activityDefRight = rightService.getActivityDefRight(activityDefId).get();
        } else { // cache enabled
            activityDefRight = (ActivityDefRight) activityDefRightCache.get(activityDefId);
            if (activityDefRight == null) {
                activityDefRight = rightService.getActivityDefRight(activityDefId).get();
                activityDefRight = new CtActivityDefRight(activityDefRight);
                activityDefRightCache.put(activityDefId, activityDefRight);
            }
        }
        return activityDefRight;

    }


    /**
     * @param activityInstId
     * @return
     * @throws JDSException
     */
    public ActivityInst getActivityInst(String activityInstId) throws BPMException {
        ActivityInst activityInst = null;
        try {
            if (activityInstId != null) {
                if (!cacheEnabled) {
                    activityInst = getIdsClientService().getActivityInst(activityInstId).get();
                } else { // cache enabled
                    activityInst = activityInstCache.get(activityInstId);
                    if (activityInst == null) {
                        synchronized (activityInstId) {
                            activityInst = getIdsClientService().getActivityInst(activityInstId).get();
                            if (activityInst != null) {
                                activityInst = new CtActivityInst(activityInst);
                                activityInstCache.put(activityInstId, activityInst);
                            }

                        }

                    }
                }
            }
        } catch (JDSException e) {
            new BPMException(e);
        }
        return activityInst;
    }

    private IDSClientService getIdsClientService() {
        if (idsClientService == null) {
            this.idsClientService = (IDSClientService) EsbUtil.parExpression("$IDSClientService");
        }
        return idsClientService;
    }

//    public ParticipantSelect getParticipantSelect(String selectedId) throws JDSException {
//        ParticipantSelect articipantSelect = null;
//        if (!cacheEnabled) {
//            articipantSelect = formulaService.getParticipantSelect(selectedId).get();
//        } else { // cache enabled
//            articipantSelect = (ParticipantSelect) participantSelectCache.get(selectedId);
//            if (articipantSelect == null) {
//                articipantSelect = formulaService.getParticipantSelect(selectedId).get();
//                articipantSelect = new CtParticipantSelect(articipantSelect);
//                participantSelectCache.put(selectedId, articipantSelect);
//            }
//        }
//        return articipantSelect;
//    }


    /**
     * @param activityDefId
     * @return
     * @throws JDSException
     */
    public ActivityDef getActivityDef(String activityDefId) throws JDSException {
        ActivityDef activityDef = null;
        if (!cacheEnabled) {
            activityDef = this.getIdsClientService().getActivityDef(activityDefId).get();
        } else { // cache enabled
            activityDef = (ActivityDef) activityDefCache.get(activityDefId);
            if (activityDef == null) {
                activityDef = getIdsClientService().getActivityDef(activityDefId).get();
                if (activityDef != null) {
                    activityDef = new CtActivityDef(activityDef);
                    activityDefCache.put(activityDefId, activityDef);
                }

            }
        }
        return activityDef;
    }

    /**
     * @param activityInstHistoryId
     * @return
     * @throws JDSException
     */
    public ActivityInstHistory getActivityInstHistory(String activityInstHistoryId) throws JDSException {
        ActivityInstHistory history = null;
        if (!cacheEnabled) {
            history = getIdsClientService().getActivityInstHistory(activityInstHistoryId).get();
        } else { // cache enabled
            history = (ActivityInstHistory) activityHistoryCache.get(activityInstHistoryId);
            if (history == null) {
                synchronized (activityInstHistoryId) {
                    history = getIdsClientService().getActivityInstHistory(activityInstHistoryId).get();
                    history = new CtActivityInstHistory(history);
                    activityHistoryCache.put(activityInstHistoryId, history);
                }
            }
        }
        return history;
    }

    /**
     * @param processDefId
     * @return
     * @throws JDSException
     */
    public ProcessDef getProcessDef(String processDefId) throws JDSException {
        ProcessDef processDef = null;
        if (!cacheEnabled) {
            processDef = getIdsClientService().getProcessDef(processDefId).get();
        } else { // cache enabled
            processDef = (ProcessDef) processDefCache.get(processDefId);
            if (processDef == null) {
                processDef = getIdsClientService().getProcessDef(processDefId).get();
                processDef = new CtProcessDef(processDef);
                processDefCache.put(processDefId, processDef);
            }
        }
        return processDef;
    }

    /**
     * @param processDefVersionId
     * @return
     * @throws JDSException
     */
    public ProcessDefVersion getProcessDefVersion(String processDefVersionId) throws JDSException {
        ProcessDefVersion processDefVersion = null;
        if (!cacheEnabled) {
            processDefVersion = getIdsClientService().getProcessDefVersion(processDefVersionId).get();
        } else { // cache enabled
            processDefVersion = (ProcessDefVersion) processDefVersionCache.get(processDefVersionId);
            if (processDefVersion == null) {
                processDefVersion = getIdsClientService().getProcessDefVersion(processDefVersionId).get();
                processDefVersion = new CtProcessDefVersion(processDefVersion);
                processDefVersionCache.put(processDefVersionId, processDefVersion);

            }
        }
        return processDefVersion;
    }


    /**
     * @param processInstId
     * @return
     * @throws JDSException
     */
    public ProcessInst getProcessInst(String processInstId) throws JDSException {
        ProcessInst processInst = null;
        if (!cacheEnabled) {
            processInst = getIdsClientService().getProcessInst(processInstId).get();
        } else { // cache enabled
            processInst = (ProcessInst) processInstCache.get(processInstId);
            if (processInst == null) {
                synchronized (processInstId) {
                    processInst = getIdsClientService().getProcessInst(processInstId).get();
                    processInst = new CtProcessInst(processInst);
                    processInstCache.put(processInstId, processInst);
                }
            }
        }
        return processInst;
    }

    /**
     * @param routeInstId
     * @return
     * @throws JDSException
     */
    public RouteInst getRouteInst(String routeInstId) throws JDSException {
        RouteInst routeInst = null;
        if (!cacheEnabled) {
            routeInst = getIdsClientService().getRouteInst(routeInstId).get();
        } else { // cache enabled
            routeInst = (RouteInst) routeInstCache.get(routeInstId);
            if (routeInst == null) {
                routeInst = getIdsClientService().getRouteInst(routeInstId).get();
                routeInst = new CtRouteInst(routeInst);
                routeInstCache.put(routeInstId, routeInst);
            }
        }
        return routeInst;
    }

    /**
     * @param routeDefId
     * @return
     * @throws JDSException
     */
    public RouteDef getRouteDef(String routeDefId) throws JDSException {
        RouteDef routeDef = null;
        if (!cacheEnabled) {
            routeDef = getIdsClientService().getRouteDef(routeDefId).get();
        } else { // cache enabled
            routeDef = (RouteDef) routeDefCache.get(routeDefId);
            if (routeDef == null) {
                routeDef = getIdsClientService().getRouteDef(routeDefId).get();
                routeDef = new CtRouteDef(routeDef);
                routeDefCache.put(routeDefId, routeDef);
            }
        }
        return routeDef;
    }


    /**
     * @return Returns the cacheEnabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }


    public List<ActivityInst> getActivityInstList(String processInstId) throws JDSException {
        List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();
        BPMCondition condition = new BPMCondition(BPMConditionKey.ACTIVITYINST_PROCESSINST_ID, Operator.EQUALS, processInstId);

        WebRightCondition webcondition = new WebRightCondition();
        webcondition.setCondition(condition);
        webcondition.setPage(condition.getPage());
        List<String> activityInstIds = this.processInstService.getActivityInstList(webcondition).get();
        for (String activityInstId : activityInstIds) {
            activityInsts.add(this.getActivityInst(activityInstId));
        }
        return activityInsts;
    }

    public ReturnType routeTo(RouteBean routeBean) {
        try {
            this.activityInstCache.remove(routeBean.getActivityInstId());

            return getIdsClientService().routeto(routeBean).get();

        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }

    }

    public ReturnType mrouteto(RouteToBean routetoBean) {
        ReturnType returnType = new ReturnType(ReturnType.MAINCODE_FAIL);
        try {
            ActivityInst inst = this.getActivityInst(routetoBean.getActivityInstId());

            this.activityInstCache.remove(routetoBean.getActivityInstId());
            returnType = getIdsClientService().mrouteto(routetoBean).get();
            this.processInstCache.remove(inst.getProcessInstId());
        } catch (JDSException e) {
            // return new ReturnType(ReturnType.MAINCODE_FAIL);
        }

        return returnType;
    }

    public ReturnType endRead(String activityInstId) {
        try {
            this.activityInstCache.remove(activityInstId);
            return getIdsClientService().endRead(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType takeBack(String activityInstId) {
        try {
            this.activityInstCache.remove(activityInstId);
            return getIdsClientService().takeBack(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType routeBack(String fromActivityInstID,
                                String toActivityInstHistoryID) {
        try {
            this.activityInstCache.remove(fromActivityInstID);
            return getIdsClientService().routeBack(fromActivityInstID, toActivityInstHistoryID).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType signReceive(String activityInstId) {
        try {
            this.activityInstCache.remove(activityInstId);
            return getIdsClientService().signReceive(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType suspendActivityInst(String activityInstId) {
        try {
            this.activityInstCache.remove(activityInstId);
            return getIdsClientService().suspendActivityInst(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType resumeActivityInst(String activityInstId) {
        try {
            this.activityInstCache.remove(activityInstId);
            return getIdsClientService().resumeActivityInst(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }


    public List<AttributeDef> getProcessDefAttributes(String processVersionDefId) throws JDSException {
        List<AttributeDef> attributeDefs = attributeDefListCache.get(processVersionDefId);
        if (attributeDefs == null) {
            try {
                if (attributeDefs == null) {
                    attributeDefs = new ArrayList<AttributeDef>();
                    List<AttributeDef> dbattributeDefs = processDefService.loadProcessDefArrtibutes(processVersionDefId).get();
                    if (dbattributeDefs != null) {
                        for (int k = 0; k < dbattributeDefs.size(); k++) {
                            AttributeDef attributeDef = dbattributeDefs.get(k);
                            if (attributeDef != null) {
                                attributeDef = new CtAttributeDef(attributeDef);
                                attributeDefs.add(attributeDef);
                                this.attributeDefCache.put(attributeDef.getId(), attributeDef);
                            }
                        }
                    }
                    attributeDefListCache.put(processVersionDefId, attributeDefs);
                }

            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return attributeDefs;
    }

    public List<String> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt group) throws JDSException {
        List<String> ids = null;
        Map<String, List<String>> arrMap = attributeIdListCache.get(activityInstId);

        if (arrMap == null) {
            arrMap = new HashMap<String, List<String>>();
            attributeIdListCache.put(activityInstId, arrMap);
        }


        if (arrMap.get(group.getType()) == null) {
            ids = this.rightService.getActivityInstRightAttribute(activityInstId, group).get();
            arrMap.put(group.getType(), ids);
            attributeIdListCache.put(activityInstId, arrMap);

        } else {
            ids = arrMap.get(group.getType());
        }
        return ids;
    }

    public List<AttributeInst> getActivityInstAttributes(String activityInstId) throws JDSException {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();

        if (attributeListCache.get(activityInstId) == null) {
            List<AttributeInst> remoteAttributeInsts = processInstService.loadActivityInstArrtibutes(activityInstId).get();

            if (remoteAttributeInsts != null) {
                for (AttributeInst attributeInst : remoteAttributeInsts) {
                    if (attributeInst != null) {
                        attributeInst = new CtAttributeInst(attributeInst);
                        attributeInsts.add(attributeInst);
                    }
                }
                attributeListCache.put(activityInstId, attributeInsts);
            }

        } else {
            attributeInsts = attributeListCache.get(activityInstId);
        }


        return attributeInsts;
    }

    public List<AttributeInst> getActivityInstHistoryAttributes(String activityInstHistoryId) throws JDSException {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        List<String> attributeInstHistoryIds = attributeInstHistoryIdsCache.get(activityInstHistoryId);

        if (attributeInstHistoryIds == null) {
            attributeInstHistoryIds = this.processHistoryService.loadActivityInstHistoryArrtibuteIds(activityInstHistoryId).get();
        }

        List<String> loadattributeInstIds = new ArrayList<String>();
        for (String attributeInstId : attributeInstHistoryIds) {
            AttributeInst attributeInst = attributeInstHistoryCache.get(attributeInstId);
            if (attributeInst != null) {
                attributeInsts.add(attributeInst);
            } else {
                loadattributeInstIds.add(attributeInstId);
            }
        }
        if (loadattributeInstIds.size() > 0) {
            List<AttributeInst> remoteAttributeInsts = processHistoryService.loadActivityInstHistoryArrtibutes(loadattributeInstIds.toArray(new String[loadattributeInstIds.size()])).get();
            attributeInsts.addAll(remoteAttributeInsts);
        }


        return attributeInsts;
    }


    public List<AttributeInst> getProcessInstAttributes(String processInstId) throws JDSException {

        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();

        if (attributeListCache.get(processInstId) == null) {

            List<AttributeInst> remoteAttributeInsts = processInstService.loadProcessInstArrtibutes(processInstId).get();
            if (remoteAttributeInsts != null) {
                for (AttributeInst attributeInst : remoteAttributeInsts) {

                    if (attributeInst != null) {
                        attributeInst = new CtAttributeInst(attributeInst);
                        attributeInsts.add(attributeInst);
                    }
                }
                attributeListCache.put(processInstId, attributeInsts);
            }

        } else {
            attributeInsts = attributeListCache.get(processInstId);
        }


        return attributeInsts;
    }


    public void setActivityInstAttribute(String activityInstId, String name, String value) throws JDSException {

        processInstService.setActivityInstAttribute(activityInstId, name, value);
    }


    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String activityInstId) throws JDSException {
        List<ActivityInstHistory> historyList = new ArrayList<ActivityInstHistory>();
        List<String> historyIds = processHistoryService.getActivityInstHistoryListByActvityInst(activityInstId).get();

        return fillActivityInstHistory(historyIds);

    }

    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstId) throws JDSException {
        List<ActivityInstHistory> historyList = new ArrayList<ActivityInstHistory>();
        List<String> historyIds = processHistoryService.getRouteBackActivityHistoryInstList(activityInstId).get();

        return fillActivityInstHistory(historyIds);
    }


    public ProcessDefForm getProcessFormDef(String processDefVersionId) {
        ProcessDefForm processDefForm = this.processDefFormCache.get(processDefVersionId);
        try {
            if (processDefForm == null) {
                ProcessDefForm rprocessDefForm = this.fdtService.getProcessDefForm(processDefVersionId).get();
                processDefForm = new CtProcessDefForm(rprocessDefForm);
                processDefFormCache.put(processDefVersionId, processDefForm);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processDefForm;
    }

    public List<FormClassBean> getAllDataFormDef(String activityDefId) {
        try {

            ActivityDef activityDef = this.getActivityDef(activityDefId);

            List<FormClassBean> formList = (List<FormClassBean>) JDSActionContext.getActionContext().getContext().get("visForm[" + activityDefId + "]");

//            if (formList == null) {
//
//                formList = new ArrayList<FormClassBean>();
//               // String path = OrgConstants.WORKFLOWBASEPATH + OrgConstants.WORKFLOWFORMPATH + activityDef.getProcessDefVersionId() + "/" + activityDef.getActivityDefId() + "/";
//
//                formList = getVfsFormClassBeanListByPath(path, formList);
//                // JDSActionContext.getActionContext().getContext().put("visForm[" + activityDefId + "]", formList);
//
//                List processDefForm = this.getProcessDefAllDataFormDef(activityDef.getProcessDefVersionId());
//                formList = compForm(formList, processDefForm);
//            }

            return formList;

            //  return this.fdtService.getAllActivityDataFormDef(activityDefId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List compForm(List<FormClassBean> activityFormList, List<FormClassBean> prcoessFormList) {
        List formList = activityFormList;

        for (int k = 0; k < prcoessFormList.size(); k++) {
            FormClassBean processForm = prcoessFormList.get(k);
            String processFormId = processForm.getId();
            boolean hasid = hasId(activityFormList, processFormId);
            if (!hasid) {
                formList.add(processForm);
            }
        }
        return formList;

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

    public List<FormClassBean> getActivityDefAllDataFormDef(String activityDefId) throws BPMException {

        List<FormClassBean> formList = (List<FormClassBean>) JDSActionContext.getActionContext().getContext().get("visForm[" + activityDefId + "]");
        try {
            if (formList == null) {
                ActivityDef activityDef = null;

                activityDef = this.getActivityDef(activityDefId);

                ProcessDef pdf = activityDef.getProcessDef();
                formList = new ArrayList<FormClassBean>();
//                String path = OrgConstants.WORKFLOWBASEPATH + OrgConstants.WORKFLOWFORMPATH + activityDef.getProcessDefVersionId() + "/" + activityDef.getActivityDefId() + "/";
//
//                formList = getVfsFormClassBeanListByPath(path, formList);
//                // JDSActionContext.getActionContext().getContext().put("visForm[" + activityDefId + "]", formList);

                List processDefForm = this.getProcessDefAllDataFormDef(activityDef.getProcessDefVersionId());
                formList = compForm(formList, processDefForm);

            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return formList;
    }


    public List<FormClassBean> getProcessDefAllDataFormDef(String processDefVersionId) throws JDSException {

        List<FormClassBean> formList = new ArrayList<FormClassBean>();
        ProcessDefVersion processDefVersion = processDefVersion = this.getProcessDefVersion(processDefVersionId);

//
//        String path = OrgConstants.WORKFLOWBASEPATH + OrgConstants.WORKFLOWFORMPATH + processDefVersionId + "/";
//
//        formList = getVfsFormClassBeanListByPath(path, formList);

        return formList;
    }

    ;


    public List<FormClassBean> getVfsFormClassBeanListByPath(String path, List<FormClassBean> formList) {


        try {
            Folder folder = CtVfsFactory.getCtVfsService().getFolderByPath(path);

            if (folder != null) {
                List<FileInfo> files = folder.getFileList();
                for (int k = 0; k < files.size(); k++) {
                    FileInfo fileInfo = files.get(k);
                    FormClassBean formClassBean = null;
                    try {
                        formClassBean = FormClassFactory.getInstance().vfsfile2FormClassbean(fileInfo.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formClassBean != null) {
                        formList.add(formClassBean);
                    }

                }

            }
        } catch (JDSException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return formList;
    }


    public FormClassBean getActivityDefMainForm(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {

        FormClassBean formClassBean = new FormClassBean();

        ActivityDef activityDef = null;
        try {
            activityDef = this.getActivityDef(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        String mdforms = null;
        if ((activityDef.getAttribute("APPLICATION.VFSFORM")) != null) {
            mdforms = activityDef.getAttribute("APPLICATION.VFSFORM").toString();
        }
        String[] formsArr = null;
        if (mdforms != null) {
            formsArr = mdforms.split(":");
            for (int k = 0; formsArr.length > k; k++) {

                try {
                    String value = activityDef.getAttribute("APPLICATION.VFSFORM." + formsArr[k]).toString();
                    formClassBean.setId(formsArr[k]);
                    formClassBean.setName(value.split(";")[0]);
                    formClassBean.setExperss(value.split(";")[1]);

                } catch (Exception e) {
                    throw new BPMException(e);
                }

            }
        }
        return formClassBean;
    }

    public List<String> getActivityHistoryRightAttribute(String activityHistoryId, ActivityInstHistoryAtt name) throws JDSException {

        List<String> ids = null;
        Map<String, List<String>> arrMap = attributeIdListCache.get(activityHistoryId);

        if (arrMap == null) {
            arrMap = new HashMap<String, List<String>>();
            attributeIdListCache.put(activityHistoryId, arrMap);
        }


        if (arrMap.get(name.getType()) == null) {
            ids = this.rightService.getActivityInstHistoryRightAttribute(activityHistoryId, name).get();
            arrMap.put(name.getType(), ids);
            attributeIdListCache.put(activityHistoryId, arrMap);

        } else {
            ids = arrMap.get(name.getType());
        }
        return ids;

    }

    public String getActivityHistoryPersonAttribute(String activityHistoryId, String personId, String name) {
        return null;
    }

    public void setActivityHistoryPersonAttribute(String activityHistoryId, String personId, String name, String value) {
    }

    public void setActivityHistoryAttribute(String activityDefId, String name, String value) {
        this.processHistoryService.setActivityHistoryAttribute(activityDefId, name, value);
    }


    public String getProcessInstPersonAttribute(String processInstId, String personId, String name) {
        return null;
    }

    public void setProcessInstAttribute(String processInstId, String name, String value) {
        this.processInstService.setProcessInstAttribute(processInstId, name, value);
    }


    public ReturnType updateProcessInstName(String processInstId, String name) {
        try {
            return this.getIdsClientService().updateProcessInstName(processInstId, name).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType updateProcessInstUrgency(String processInstId, String urgency) {

        try {
            return this.getIdsClientService().updateProcessInstUrgency(processInstId, urgency).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType suspendProcessInst(String processInstId) {
        try {
            return this.getIdsClientService().suspendProcessInst(processInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType resumeProcessInst(String processInstId) {
        try {
            return this.getIdsClientService().resumeProcessInst(processInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }


    public ReturnType abortProcessInst(String processInstId) {
        try {
            return this.getIdsClientService().abortProcessInst(processInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType completeProcessInst(String processInstId) {
        try {
            return this.getIdsClientService().completeProcessInst(processInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ReturnType deleteProcessInst(String processInstId) {
        try {
            return this.getIdsClientService().deleteProcessInst(processInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId) {
        ListResultModel<List<ActivityInstHistory>> historyModule = new ListResultModel<>();
        List<String> historyIds = null;
        try {
            ListResultModel<List<String>> module = this.processHistoryService.getActivityInstHistoryListByProcessInst(processInstId);

            historyIds = module.get();
            historyModule.setSize(module.getSize());
            List<ActivityInstHistory> histories = fillActivityInstHistory(historyIds);
            historyModule.setData(histories);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return historyModule;
    }

    public List<ActivityInstHistory> fillActivityInstHistory(List<String> historyIds) {

        if (historyIds == null) {
            return new ArrayList<ActivityInstHistory>();
        }
        List<ActivityInstHistory> histories = new ArrayList<ActivityInstHistory>();
        try {
            List<String> loadIds = new ArrayList<String>();
            for (String historyId : historyIds) {
                ActivityInstHistory history = activityHistoryCache.get(historyId);
                if (history == null) {
                    loadIds.add(historyId);
                }
            }

            if (loadIds.size() > 0) {
                List<ActivityInstHistory> remotehistories = null;

                remotehistories = processHistoryService.loadHistoryActivityInstHistoryList(loadIds.toArray(new String[loadIds.size()])).get();

                for (ActivityInstHistory his : remotehistories) {
                    his = new CtActivityInstHistory(his);
                    activityHistoryCache.put(his.getActivityHistoryId(), his);

                }
            }
            for (String id : historyIds) {

                histories.add(this.getActivityInstHistory(id));

            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return histories;
    }


    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryList(BPMCondition condition, RightConditionEnums conditionEnus) {
        ListResultModel<List<ActivityInstHistory>> historyModule = new ListResultModel<>();
        List<String> historyIds = null;
        try {
            WebRightCondition webcondition = new WebRightCondition();
            webcondition.setCondition(condition);
            webcondition.setRightCondition(conditionEnus);
            webcondition.setPage(condition.getPage());
            ListResultModel<List<String>> module = this.processHistoryService.getActivityInstHistoryList(webcondition);

            historyIds = module.get();
            historyModule.setSize(module.getSize());
            List<ActivityInstHistory> histories = fillActivityInstHistory(historyIds);
            historyModule.setData(histories);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return historyModule;
    }


    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition) {
        List<ProcessDefVersion> procesDefVersions = new ArrayList<ProcessDefVersion>();
        ListResultModel<List<ProcessDefVersion>> module = new ListResultModel<>();
        List<String> versionIds = new ArrayList<String>();

        try {
            ListResultModel<List<String>> idmodule = this.processDefService.getProcessDefVersionIdList(condition);

            versionIds = idmodule.get();
            module.setSize(idmodule.getSize());

            List<String> loadIds = new ArrayList<String>();

            for (String versionid : versionIds) {
                ProcessDefVersion version = this.processDefVersionCache.get(versionid);

                if (version == null) {
                    loadIds.add(versionid);
                }
            }
            if (loadIds.size() > 0) {
                List<ProcessDefVersion> remoteInsts = this.processDefService.getProcessDefVersionList(loadIds.toArray(new String[loadIds.size()])).get();
                for (ProcessDefVersion processDefVersion : remoteInsts) {
                    processDefVersion = new CtProcessDefVersion(processDefVersion);
                    processDefVersionCache.put(processDefVersion.getProcessDefVersionId(), processDefVersion);

                }
            }

            for (String id : versionIds) {
                procesDefVersions.add(this.getProcessDefVersion(id));
            }
            module.setData(procesDefVersions);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return module;
    }

    public ListResultModel<List<ProcessDef>> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) {
        List<String> defIds = new ArrayList<String>();
        ListResultModel<List<ProcessDef>> module = new ListResultModel<>();
        List<ProcessDef> procesDefs = new ArrayList<ProcessDef>();
        try {

            ListResultModel<List<String>> idmodule = this.processDefService.getProcessDefIdList(condition);

            defIds = idmodule.get();
            module.setSize(idmodule.getSize());
            List<String> loadIds = new ArrayList<String>();

            for (String defId : defIds) {
                ProcessDef ProcessDef = this.processDefCache.get(defId);
                if (ProcessDef == null) {
                    loadIds.add(defId);
                }
            }
            if (loadIds.size() > 0) {
                List<ProcessDef> remoteInsts = this.processDefService.getProcessDefList(loadIds.toArray(new String[loadIds.size()])).get();

                for (ProcessDef processDef : remoteInsts) {
                    processDef = new CtProcessDef(processDef);
                    processDefCache.put(processDef.getProcessDefId(), processDef);
                }
            }

            for (String id : defIds) {
                procesDefs.add(this.getProcessDef(id));
            }
            module.setData(procesDefs);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return module;
    }


    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) {
        List<ProcessInst> processInsts = new ArrayList<ProcessInst>();
        ListResultModel<List<ProcessInst>> module = new ListResultModel<>();
        List<String> processInstIds = null;
        try {
            WebRightCondition webcondition = new WebRightCondition();
            webcondition.setCondition(condition);
            webcondition.setRightCondition(conditionEnus);
            webcondition.setFilter(filter);
            webcondition.setCtx(ctx);
            webcondition.setPage(condition.getPage());

            ListResultModel<List<String>> idmodule = this.processInstService.getProcessInstList(webcondition);

            processInstIds = idmodule.get();
            for (String processInstId : processInstIds) {
                processInsts.add(this.getProcessInst(processInstId));
            }
            List<String> loadIds = new ArrayList<String>();

            for (String processInstId : processInstIds) {
                ProcessInst processInst = this.processInstCache.get(processInstId);
                if (processInst == null) {
                    loadIds.add(processInstId);
                }
            }
            if (loadIds.size() > 0) {
                List<ProcessInst> remoteInsts = this.processInstService.loadProcessInstLists(loadIds.toArray(new String[loadIds.size()])).get();

                for (ProcessInst processInst : remoteInsts) {
                    processInst = new CtProcessInst(processInst);
                    processInstCache.put(processInst.getProcessInstId(), processInst);


                }


            }
            for (String id : processInstIds) {
                processInsts.add(this.getProcessInst(id));
            }
            module.setData(processInsts);

        } catch (JDSException e) {
            e.printStackTrace();
        }

        return module;
    }

    public ListResultModel<List<ActivityInst>> getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) {
        List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();
        ListResultModel<List<ActivityInst>> activityInstsModel = new ListResultModel<>();
        List<String> activityInstIds = null;
        try {

            WebRightCondition webcondition = new WebRightCondition();
            webcondition.setCondition(condition);
            webcondition.setRightCondition(conditionEnus);
            webcondition.setFilter(filter);
            webcondition.setCtx(ctx);
            webcondition.setPage(condition.getPage());

            ListResultModel<List<String>> idmodel = this.processInstService.getActivityInstList(webcondition);


            activityInstIds = idmodel.get();
            activityInstsModel.setSize(idmodel.getSize());
//            for (String activityInstId : activityInstIds) {
//                activityInsts.add(this.getActivityInst(activityInstId));
//            }
            List<String> loadIds = new ArrayList<String>();

            for (String activityInstId : activityInstIds) {
                ActivityInst activityInst = this.activityInstCache.get(activityInstId);
                if (activityInst == null) {
                    loadIds.add(activityInstId);
                }
            }
            if (loadIds.size() > 0) {
                List<ActivityInst> remoteInsts = this.processInstService.loadActivityInstList(loadIds.toArray(new String[loadIds.size()])).get();
                for (ActivityInst activityInst : remoteInsts) {
                    activityInst = new CtActivityInst(activityInst);
                    activityInstCache.put(activityInst.getActivityInstId(), activityInst);
                }
            }


            for (String id : activityInstIds) {
                activityInsts.add(this.getActivityInst(id));
            }
            activityInstsModel.setData(activityInsts);

        } catch (JDSException e) {
            e.printStackTrace();
        }

        return activityInstsModel;
    }

    public List<ActivityInst> fillActivityInst(List<String> activityInstIds) throws JDSException {
        List<String> loadIds = new ArrayList<String>();
        List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();
        for (String activityInstId : activityInstIds) {
            ActivityInst activityInst = this.activityInstCache.get(activityInstId);
            if (activityInst == null) {
                loadIds.add(activityInstId);
            }
        }
        if (loadIds.size() > 0) {
            List<ActivityInst> remoteInsts = this.processInstService.loadActivityInstList(loadIds.toArray(new String[loadIds.size()])).get();
            for (ActivityInst activityInst : remoteInsts) {
                activityInst = new CtActivityInst(activityInst);
                activityInstCache.put(activityInst.getActivityInstId(), activityInst);
            }
        }
        for (String id : activityInstIds) {
            activityInsts.add(this.getActivityInst(id));
        }
        return activityInsts;

    }

    public List<ActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId) throws JDSException {
        List<String> activityInstIds = null;
        try {
            activityInstIds = this.processHistoryService.getActivityInstListByOutActvityInstHistory(activityInstHistoryId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return fillActivityInst(activityInstIds);

    }


    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId) {
        List<String> historyIds = null;
        try {
            historyIds = this.processHistoryService.getLastActivityInstHistoryListByActvityInst(actvityInstId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return fillActivityInstHistory(historyIds);
    }

    public List<ActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) {
        List<String> historyIds = null;
        try {
            historyIds = this.processHistoryService.getAllOutActivityInstHistoryByActvityInstHistory(historyHisroryId, noSplit).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return fillActivityInstHistory(historyIds);
    }


    public ActivityInst newProcess(String processDefId, String processInstName) throws JDSException {
        ActivityInst activityInst = this.getIdsClientService().newProcess(processDefId, processInstName).get();
        ActivityInst inst = new CtActivityInst(activityInst);
        this.activityInstCache.put(activityInst.getActivityInstId(), inst);

        return inst;
    }

    public ReturnType display(String activityInstID) throws JDSException {
        ActivityInst activityInst = this.getActivityInst(activityInstID);
        if (ActivityInstStatus.notStarted.equals(activityInst.getState())) {
            // 如果当前办理人只有一个人，而且配置了自动签收，则执行自动签收操作。
            List performers = (List) getActivityInstRightAttribute(activityInstID, ActivityInstRightAtt.PERFORMER);
            if (performers != null && performers.size() == 1) {
                ActivityDefPerformSequence performSequence = activityInst.getActivityDef().getRightAttribute().getPerformSequence();
                // 自动签收还未签收时
                if (ActivityDefPerformSequence.AUTOSIGN.equals(performSequence) && canSignReceive(activityInstID)) {
                    signReceive(activityInstID);
                }
            }
        }


        return this.getIdsClientService().display(activityInstID).get();
    }

    public List<RouteDef> getNextRoutes(String startActivityInstID) throws JDSException {
        List<String> routeIds = this.getActivityInst(startActivityInstID).getActivityDef().getOutRouteIds();
        List<RouteDef> routeDefs = new ArrayList<RouteDef>();
        for (String routeDefId : routeIds) {
            RouteDef routeDef = null;
            try {
                routeDef = CtBPMCacheManager.getInstance().getRouteDef(routeDefId);
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (routeDef != null) {
                RouteCondition conditionType = routeDef.getRouteConditionType();
                String condition = routeDef.getRouteCondition();
                if (conditionType.equals(RouteCondition.CONDITION) && condition != null && !condition.equals("")) {
                    Boolean result = false;
                    try {
                        result = (Boolean) JDSActionContext.getActionContext().Par(condition, Boolean.class);
                    } catch (Throwable throwable) {
                        log.error(throwable);
                    }
                    if (result) {
                        routeDefs.add(routeDef);
                    }
                } else {
                    routeDefs.add(routeDef);
                }

            }
        }
        return routeDefs;
    }


    public ReturnType copyTo(String activityHistoryInstId, List<String> readers) throws JDSException {
        return this.getIdsClientService().copyTo(activityHistoryInstId, readers.toArray(new String[readers.size()])).get();
    }

    public List<Listener> getActivityListeners(String activityDefId) {

        try {
            return this.processDefService.getActivityListeners(activityDefId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<AttributeDef> getRouteDefAttributes(String routeDefId) throws JDSException {
        List<AttributeDef> attributeDefs = attributeDefListCache.get(routeDefId);
        if (attributeDefs == null) {
            attributeDefs = new ArrayList<AttributeDef>();
            List<AttributeDef> realAttributeDefs = processDefService.loadRouteDefArrtibutes(routeDefId).get();
            for (AttributeDef attributeDef : realAttributeDefs) {
                if (attributeDef != null) {
                    attributeDef = new CtAttributeDef(attributeDef);
                    attributeDefs.add(attributeDef);
                    this.attributeDefCache.put(attributeDef.getId(), attributeDef);
                }
            }
            attributeDefListCache.put(routeDefId, attributeDefs);
        }
        return attributeDefs;
    }


    public void setActivityInstPersonAttribute(String activityInstId, String name, String value) {
    }

    public void setProcessInstPersonAttribute(String processInstId, String personId, String name, String value) {

    }

    public List<AttributeDef> getActivityDefAttributes(String activitydefId) {
        List<AttributeDef> attributeDefs = attributeDefListCache.get(activitydefId);
        try {
            if (attributeDefs == null) {
                attributeDefs = new ArrayList<AttributeDef>();
                List<AttributeDef> dbattributeDefs = processDefService.loadActivityDefArrtibutes(activitydefId).get();
                for (AttributeDef attributeDef : dbattributeDefs) {
                    if (attributeDef != null) {
                        attributeDef = new CtAttributeDef(attributeDef);
                        attributeDefs.add(attributeDef);
                        this.attributeDefCache.put(attributeDef.getId(), attributeDef);
                    }
                }
                attributeDefListCache.put(activitydefId, attributeDefs);

            }


        } catch (JDSException e) {
            e.printStackTrace();
        }
        return attributeDefs;
    }

    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId) throws JDSException {

        ActivityInst inst = getIdsClientService().copyActivityInstByHistory(activityHistoryInstId, false).get();
        return inst;
    }

    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Boolean isNew) throws JDSException {

        ActivityInst inst = getIdsClientService().copyActivityInstByHistory(activityHistoryInstId, isNew).get();
        return inst;
    }


    public ActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId) throws JDSException {
        ActivityInst inst = getIdsClientService().newActivityInstByActivityDefId(processInstId, activityDefId).get();
        return inst;
    }

    public Boolean canTakeBack(String activityInstID) throws JDSException {
        ActivityInst inst = this.getActivityInst(activityInstID);
        if (!inst.getState().equals(ActivityInstStatus.notStarted)) {
            return false;
        }

        String actionKey = activityInstID + "[canTakeBack]";
        Boolean canTakeBack = (Boolean) JDSActionContext.getActionContext().getContext().get(actionKey);
        if (canTakeBack == null) {
            canTakeBack = getIdsClientService().canTakeBack(activityInstID).get();
            JDSActionContext.getActionContext().getContext().put(actionKey, canTakeBack);
        }
        return canTakeBack;

    }

    public Boolean canEndRead(String activityInstID) throws JDSException {
        String actionKey = activityInstID + "[canEndRead]";

        ActivityInst inst = this.getActivityInst(activityInstID);
        if (!inst.getState().equals(ActivityInstStatus.READ)) {
            return false;
        }

        Boolean canEndRead = (Boolean) JDSActionContext.getActionContext().getContext().get(actionKey);
        if (canEndRead == null) {
            canEndRead = getIdsClientService().canEndRead(activityInstID).get();
            JDSActionContext.getActionContext().getContext().put(actionKey, canEndRead);
        }
        return canEndRead;
    }

    public ReturnType endTask(String activityInstId) {
        try {
            return getIdsClientService().endTask(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType abortedTask(String activityInstId) {
        try {
            return getIdsClientService().abortedTask(activityInstId).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType deleteHistory(String activityInstHistoryID) {
        try {
            return this.processHistoryService.deleteHistory(activityInstHistoryID).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType restoreHistory(String activityInstHistoryID) {
        try {
            return this.processHistoryService.restoreHistory(activityInstHistoryID).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public ReturnType clearHistory(String activityInstHistoryID) {
        try {
            return this.processHistoryService.clearHistory(activityInstHistoryID).get();
        } catch (JDSException e) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }
    }

    public Boolean canRouteBack(String activityInstID) throws BPMException {
        ActivityInst inst = this.getActivityInst(activityInstID);

        if (!inst.getState().equals(ActivityInstStatus.running)) {
            return false;
        }

        String actionKey = activityInstID + "[canRouteBack]";
        Boolean canRouteBack = (Boolean) JDSActionContext.getActionContext().getContext().get(actionKey);
        if (canRouteBack == null) {
            try {
                canRouteBack = getIdsClientService().canRouteBack(activityInstID).get();
            } catch (JDSException e) {
                canRouteBack = false;
            }
            JDSActionContext.getActionContext().getContext().put(actionKey, canRouteBack);
        }
        return canRouteBack;
    }

    public Boolean canPerform(String activityInstID) throws BPMException {


        ActivityInst inst = this.getActivityInst(activityInstID);

        if (!inst.getState().equals(ActivityInstStatus.running) && !inst.getRunStatus().equals(ActivityInstRunStatus.PROCESSNOTSTARTED)) {
            return false;
        }

        String actionKey = activityInstID + "[canPerform]";
        Boolean canPerform = (Boolean) JDSActionContext.getActionContext().getContext().get(actionKey);
        if (canPerform == null) {
            try {
                canPerform = getIdsClientService().canPerform(activityInstID).get();
            } catch (JDSException e) {
                canPerform = false;
            }
            JDSActionContext.getActionContext().getContext().put(actionKey, canPerform);
        }
        return canPerform;

    }

    public Boolean canSignReceive(String activityInstID) throws BPMException {
        String actionKey = activityInstID + "[canSignReceive]";
        ActivityInst inst = this.getActivityInst(activityInstID);
        if (!inst.getState().equals(ActivityInstStatus.notStarted)) {
            return false;
        }

        Boolean canSignReceive = (Boolean) JDSActionContext.getActionContext().getContext().get(actionKey);
        if (canSignReceive == null) {
            try {
                canSignReceive = getIdsClientService().canSignReceive(activityInstID).get();
            } catch (JDSException e) {
                throw new BPMException(e);
            }
            JDSActionContext.getActionContext().getContext().put(actionKey, canSignReceive);
        }
        return canSignReceive;
    }

    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws JDSException {
        String activityDefId = this.processDefService.getFirstActivityDefInProcess(processDefVersionId).get();
        return this.getActivityDef(activityDefId);
    }

    public void addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws JDSException {
        this.processHistoryService.addPersonTagToHistory(activityInstHistoryID, tagName).get();
    }

    public void deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws JDSException {
        this.processHistoryService.addPersonTagToHistory(activityInstHistoryID, tagName).get();
    }

//    public List<AttributeDef> getActivityDefAttributes(String activityDefId) throws JDSException {
//        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
//        List<String> attributeDefIds = attributeDefIdsCache.get(activityDefId);
//
//        if (attributeDefIds == null) {
//            attributeDefIds = this.processDefService.loadActivityDefArrtibuteIds(activityDefId).get();
//        }
//
//        List<String> loadattributeDefIds = new ArrayList<String>();
//        for (String attributeDefId : attributeDefIds) {
//            AttributeDef attributeDef = attributeDefCache.get(attributeDefId);
//            if (attributeDef != null) {
//                attributeDefs.add(attributeDef);
//            } else {
//                loadattributeDefIds.add(attributeDefId);
//            }
//        }
//        if (loadattributeDefIds.size() > 0) {
//            List<AttributeDef> remoteAttributeDefs = processDefService.loadActivityDefArrtibutes(loadattributeDefIds.toArray(new String[loadattributeDefIds.size()])).get();
//            for (AttributeDef attributeDef : remoteAttributeDefs) {
//                attributeDef = new CtAttributeDef(attributeDef);
//                attributeDefCache.put(attributeDef.getId(), attributeDef);
//                attributeDefs.add(attributeDef);
//            }
//
//        }
//        return attributeDefs;
//    }

    public List<AttributeDef> getAttributeDefs(String[] ids) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        for (String attributeDefId : ids) {
            AttributeDef attributeDef = attributeDefCache.get(attributeDefId);
            if (attributeDef != null) {
                attributeDefs.add(attributeDef);
            }
        }
        return attributeDefs;
    }


    public AttributeDef getAttributeDefById(String attributeDefId) {

        AttributeDef attributeDef = attributeDefCache.get(attributeDefId);

        return attributeDef;
    }


    public List<AttributeInst> getAttributeInsts(String[] ids) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        for (String attributeInstId : ids) {
            AttributeInst attributeInst = attributeInstCache.get(attributeInstId);
            if (attributeInst != null) {
                attributeInsts.add(attributeInst);
            }
        }
        return attributeInsts;
    }


    public Attribute getAttributeInst(String attributeInstId) {
        return attributeInstCache.get(attributeInstId);
    }
}

