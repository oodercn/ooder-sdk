package net.ooder.agent.client.iot.api.lock;


import net.ooder.agent.client.command.PasswordCommand;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.LockHistoryDataInfo;
import net.ooder.agent.client.iot.json.PMSSensorInfo;
import net.ooder.agent.client.iot.json.PMSSensorSearch;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;

import java.util.Date;
import java.util.List;

public interface IotLockService {

    /**
     * 获取锁历史数据
     *
     * @param ieee         编号
     * @param currentIndex
     * @param pageSize
     * @return
     */
    ListResultModel<List<LockHistoryDataInfo>> getLockSensorHisData(String ieee, String passId, Date startTime, Date endTime, Integer currentIndex, Integer pageSize);


    /**
     * 下发密码
     *
     * @param lockPassword
     * @return
     */
    public ResultModel<PasswordCommand> addPassword(AppLockPassword lockPassword);


    /**
     * 删除密码
     *
     * @param ieee
     * @param passId
     * @return
     */
    public ResultModel<Boolean> deletePassword(String ieee, Integer passId, Boolean isSend);


    /**
     *
     * @param search
     * @return
     */
    public ListResultModel<List<PMSSensorInfo>> searchLock(PMSSensorSearch search);

    /**
     *
     * @param sensorId
     * @param moduleId
     * @return
     */
    public ResultModel<Boolean> deletePasswordByModuleId(String sensorId, String moduleId);

    /**
     * 获取密码
     *
     * @param ieee
     * @return
     */
    public ListResultModel<List<AppLockPassword>> getPassword(String ieee);

    /**
     * 获取密码
     *
     * @param sensorId
     * @return
     */

    public ResultModel<Boolean> clearPassword(String sensorId);

    /**
     * 获取success密码
     *
     * @param
     * @return
     */
    public ListResultModel<List<AppLockPassword>> getRealPassword(String sensorId);

    /**
     * 获取sendIng密码
     *
     * @param
     * @return
     */
    public ListResultModel<List<PasswordCommand>> getPasswordCommandTask(String ieee);


    /**
     * @param valueOf
     * @return
     */
    public ListResultModel<List<PasswordCommand>> getLockCommandByPassId(Integer valueOf, String ieee);


    /**
     * 获取离线密码
     *
     * @return java.lang.String
     * @author eric
     * @date 10:34 AM 2019/1/4
     * @Param: startHour 开始小时
     * @Param: endHour   结束小时
     * @Param: ieee      ieee地址
     * @Param: passId    密码组id
     */
    public ResultModel<String> getOfflinePassword(Long startTime, Long endTime, String ieee,String phone, Integer passId);


    /**
     * 获取离线密码
     *
     * @return java.lang.String
     * @author eric
     * @date 10:34 AM 2019/1/4
     * @Param: startHour 开始小时
     * @Param: endHour   结束小时
     * @Param: ieee      ieee地址
     * @Param: passId    密码组id
     */
    public ListResultModel<List<AppLockPassword>> getOfflinePasswordList(String ieee);


    /**
     * 设置离线密码种子
     *
     * @return  net.ooder.config.ResultModel<String>
     * @author eric
     * @date 2:39 PM 2019/1/4
     * @Param: ieee  ieee地址
     * @Param: seed  离线密码种子
     * @Param: passId 密码组id
     */
    public ResultModel<PasswordCommand> setPwdSeed(String ieee, String seed, String phone, Integer passId);


}
