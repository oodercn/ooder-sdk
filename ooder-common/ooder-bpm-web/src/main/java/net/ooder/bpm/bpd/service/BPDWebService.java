
package net.ooder.bpm.bpd.service;

import net.ooder.config.ResultModel;
import net.ooder.org.Person;

import java.util.List;

/**
 * <p>
 * Title: 工作流设计工具（BPD）
 * </p>
 * <p>
 * Description: 应用发布的WebService服务
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: itjds
 * </p>
 *
 * @author wenzhangli 2018-9-30 17:51:51
 * @version 2.0
 */
public interface BPDWebService {


    /**
     * 保存工作流信息到数据库
     *
     * @param xpdlString XPDL文件的字符串
     * @return boolean
     */
    public ResultModel<Boolean> saveProcessDefListToDB(String xpdlString, String personId);

    /**
     * 删除数据库中的工作流信息
     *
     * @param versionIdsString 流程版本ID的字符串，以逗号分割
     * @return boolean
     */
    public ResultModel<Boolean> deleteProcessDefListToDB(String versionIdsString, String personId);

    /**
     * 从数据库中获得工作流XPDL字符串
     *
     * @param versionIdsString 流程版本ID的字符串，以逗号分割
     * @return xpdlString XPDL文件的字符串
     */
    public ResultModel<String> getProcessDefListFromDB(String versionIdsString, String personId);

    /**
     * 激活制定版本流程
     *
     * @param versionId
     * @return
     */
    public ResultModel<Boolean> activateProcessDefVersion(String versionId, String personId);

    /**
     * 冻结指定版本流程
     *
     * @param processDefVersionId
     * @return
     */
    public ResultModel<Boolean> freezeProcessDefVersion(String processDefVersionId, String personId);

    /**
     * 保存流程授权信息
     *
     * @param processId 流程Id
     * @param group     流程授权信息
     * @return 成功返回true，否则返回false
     */
    public ResultModel<Boolean> saveCommission(String processId, String group, String personIds);

    /**
     * 获取指定流程的流程授权信息
     *
     * @param processId 流程Id
     * @return 流程授权信息Map
     */
    public ResultModel<List<Person>> getCommissions(String processId, String group);


}
