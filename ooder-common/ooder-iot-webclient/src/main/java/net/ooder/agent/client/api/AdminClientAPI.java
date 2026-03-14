package net.ooder.agent.client.api;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.agent.client.home.ct.CtAdminClientImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/jds/iot/sys/")
@MethodChinaName(cname = "管理接口")
public class AdminClientAPI {

    @MethodChinaName(cname = "发送命令", returnStr = "SendCommand($R('commandStr'),$R('gatewayieee'))")

    @RequestMapping(method = RequestMethod.POST, value = "sendCommand")
    public @ResponseBody
    ResultModel<Command> sendCommand(String commandStr, String gatewayieee) {
        ResultModel<Command> resultModel=new ResultModel<Command>();
        Command command=getAdminService().sendCommand(commandStr, gatewayieee);
        resultModel.setData(command);
        return resultModel;

    }

    @MethodChinaName(cname = "批量发送命令", returnStr = "sendCommands($R('commandStr'),$R('gatewayieee'),$R('num'),$R('times'))")

    @RequestMapping(method = RequestMethod.POST, value = "sendCommands")
    public ListResultModel<List<Command>> sendCommands(String commandStr, String gatewayieees, Integer num, Integer times) {
        ListResultModel<List<Command>> resultModel=new ListResultModel<List<Command>>();
        List<Command> commands=getAdminService().sendCommands(commandStr, gatewayieees, num, times);
        resultModel.setData(commands);
        return resultModel;
    }
    public static void main(String[] args) {
        System.out.println(JSONObject.toJSON(DeviceStatus.fromCode(1)));
    }


    private CtAdminClientImpl getAdminService() {
        return  new CtAdminClientImpl();
    }


}
