package net.ooder.agent.client.iot.client.api;

import net.ooder.agent.client.command.CommandReportStatus;
import net.ooder.agent.client.iot.client.CommandWebService;
import net.ooder.agent.client.iot.json.device.BindInfo;
import net.ooder.agent.client.iot.json.device.SenceMode;
import net.ooder.annotation.MethodChinaName;
import net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/jds/iot/commandservice/")
@MethodChinaName(cname = "命令管理")
public class CommandWebServiceAPI implements CommandWebService {

    @MethodChinaName(cname = "设置灯光场景状态", returnStr = "SetLightSensorStatusInfo($R('sensorId'),$R('value'))")
    @RequestMapping(method = RequestMethod.POST, value = "setLightSensorStatusInfo")
    @Override
    public @ResponseBody
    ResultModel<Boolean> setLightSensorStatusInfo(String sensorId, Integer value) {
        return getCommandWebService().setLightSensorStatusInfo(sensorId, value);
    }

    @MethodChinaName(cname = "切换传感器", returnStr = "SetOutLetSensorInfo($R('sensorId'),$R('value'))")
    @RequestMapping(method = RequestMethod.POST, value = "setOutLetSensorInfo")
    @Override
    public @ResponseBody
    ResultModel<Boolean> setOutLetSensorInfo(String sensorId, Integer value) {
        return getCommandWebService().setOutLetSensorInfo(sensorId, value);
    }

    @MethodChinaName(cname = "执行报告", returnStr = "CommandReport($CommandReportStatus)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"commandReport"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> commandReport(@RequestBody CommandReportStatus commandReport) {
        return getCommandWebService().commandReport(commandReport);
    }

    @MethodChinaName(cname = "绑定状态报告", returnStr = "BindingStatusReport($CommandReportStatus)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"bindingStatusReport"})
    @Override
    public @ResponseBody
    ResultModel<Boolean> bindingStatusReport(@RequestBody CommandReportStatus report) {
        return getCommandWebService().bindingStatusReport(report);
    }

    @MethodChinaName(cname = "绑定列表报告", returnStr = "BindListReport($BindInfo)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"bindListReport"})
    @Override
    @ResponseBody
    public ResultModel<Boolean> bindListReport(@RequestBody BindInfo bindInfo) {
        return getCommandWebService().bindListReport(bindInfo);
    }

    @MethodChinaName(cname = "模式列表报告", returnStr = "ModeListReport($SenceMode)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"modeListReport"})
    @Override
    @ResponseBody
    public ResultModel<Boolean> modeListReport(@RequestBody SenceMode mode) {
        return getCommandWebService().modeListReport(mode);
    }


    CommandWebService getCommandWebService() {
        return (CommandWebService) EsbUtil.parExpression(CommandWebService.class);
    }


}
