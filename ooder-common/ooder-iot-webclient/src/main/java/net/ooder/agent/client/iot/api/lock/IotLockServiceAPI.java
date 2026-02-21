package net.ooder.agent.client.iot.api.lock;

import net.ooder.agent.client.command.PasswordCommand;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.LockHistoryDataInfo;
import net.ooder.agent.client.iot.json.PMSSensorInfo;
import net.ooder.agent.client.iot.json.PMSSensorSearch;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@MethodChinaName(cname = "智能门锁应用")
@Controller
@RequestMapping("/jds/iot/lock/")
public class IotLockServiceAPI implements IotLockService {

    @Override
    @RequestMapping(method = {RequestMethod.POST}, value = {"getLockSensorHisData"})
    @ResponseBody
    @MethodChinaName(cname = "获取开锁记录")
    public ListResultModel<List<LockHistoryDataInfo>> getLockSensorHisData(String ieee, String passId, Date startTime, Date endTime, Integer currentIndex, Integer pageSize) {
        return getService().getLockSensorHisData(ieee, passId, startTime, endTime, currentIndex, pageSize);
    }


    @RequestMapping(method = {RequestMethod.POST}, value = {"addPassword"})
    @ResponseBody
    @Override
    @MethodChinaName(cname = "添加密码")
    public ResultModel<PasswordCommand> addPassword(@RequestBody AppLockPassword lockPassword) {
        return getService().addPassword(lockPassword);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = {"getOfflinePassword"})
    @ResponseBody
    @Override
    @MethodChinaName(cname = "获取离线密码")
    public ResultModel<String> getOfflinePassword(Long startTime, Long endTime, String ieee, String phone, Integer passId) {
        return getService().getOfflinePassword(startTime, endTime, phone, ieee, passId);
    }

    @MethodChinaName(cname = "删除密码")
    @RequestMapping(method = {RequestMethod.POST}, value = {"deletePassword"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> deletePassword(String ieee, Integer passId, Boolean isSend) {

        return getService().deletePassword(ieee, passId, isSend);
    }

    @MethodChinaName(cname = "查询门锁")
    @RequestMapping(method = {RequestMethod.POST}, value = {"searchLock"})
    @ResponseBody
    @Override
    public ListResultModel<List<PMSSensorInfo>> searchLock(@RequestBody PMSSensorSearch search) {
        return getService().searchLock(search);
    }

    @MethodChinaName(cname = "根据类型删除密码")
    @RequestMapping(method = {RequestMethod.POST}, value = {"deletePasswordByModuleId"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> deletePasswordByModuleId(String ieee, String moduleId) {

        return getService().deletePasswordByModuleId(ieee, moduleId);
    }

    @MethodChinaName(cname = "获取密码列表")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getPassword"})
    @ResponseBody
    @Override
    public ListResultModel<List<AppLockPassword>> getPassword(String ieee) {

        return getService().getPassword(ieee);
    }

    @MethodChinaName(cname = "清空密码")
    @RequestMapping(method = {RequestMethod.POST}, value = {"clearPassword"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> clearPassword(String ieee) {

        return getService().clearPassword(ieee);
    }

    @MethodChinaName(cname = "获取真实密码")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getRealPassword"})
    @ResponseBody
    @Override
    public ListResultModel<List<AppLockPassword>> getRealPassword(String ieee) {

        return getService().getRealPassword(ieee);
    }

    @MethodChinaName(cname = "获取密码下发任务")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getPasswordCommandTask"})
    @ResponseBody
    @Override
    public ListResultModel<List<PasswordCommand>> getPasswordCommandTask(String ieee) {

        return getService().getPasswordCommandTask(ieee);
    }

    @MethodChinaName(cname = "获取指定ID密码下发任务")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getLockCommandByPassId"})
    @ResponseBody
    @Override
    public ListResultModel<List<PasswordCommand>> getLockCommandByPassId(Integer valueOf, String serialno) {
        return getService().getLockCommandByPassId(valueOf, serialno);
    }

    @MethodChinaName(cname = "设置种子")
    @RequestMapping(method = {RequestMethod.POST}, value = {"setPwdSeed"})
    @ResponseBody
    @Override
    public ResultModel<PasswordCommand> setPwdSeed(String ieee, String seed, String phone, Integer passId) {
        return getService().setPwdSeed(ieee, seed, phone, passId);
    }

    @MethodChinaName(cname = "获取离线密码列表")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getOfflinePasswordList"})
    @ResponseBody
    @Override
    public ListResultModel<List<AppLockPassword>> getOfflinePasswordList(String ieee) {
        return getService().getOfflinePasswordList(ieee);
    }

    public IotLockService getService() {
        IotLockService iotService = (IotLockService) EsbUtil.parExpression(IotLockService.class);
        return iotService;
    }

}
