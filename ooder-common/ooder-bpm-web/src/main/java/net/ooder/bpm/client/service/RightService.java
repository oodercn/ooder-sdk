package net.ooder.bpm.client.service;

import net.ooder.bpm.client.ActivityDefRight;

import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;

import java.util.List;

public interface RightService {


    /**
     * @param activityDefId
     * @return
     */
    ResultModel<ActivityDefRight> getActivityDefRight(String activityDefId);

    /**
     * @param activityInstId
     * @param group
     * @return
     */
    ResultModel<List<String>> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt group);

    /**
     * @param activityInstHistoryId
     * @param group
     * @return
     */
    ResultModel<List<String>> getActivityInstHistoryRightAttribute(String activityInstHistoryId, ActivityInstHistoryAtt group);

    /**
     * @param activityInstHistoryId
     * @return
     */
    ResultModel<List<RightGroupEnums>> queryAllPermissionToActivityInst(String activityInstHistoryId);


}
