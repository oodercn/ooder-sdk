package net.ooder.agent.client.iot.api.inner;

import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.ZNode;
import  net.ooder.annotation.JLuceneIndex;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.common.Condition;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.agent.client.home.query.HomeConditionKey;
import  net.ooder.agent.client.home.query.IOTConditionKey;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
@MethodChinaName(cname="IOT管理应用")
@Controller
@RequestMapping("/jds/iot/deviceSearch/")
public class DeviceSearchServiceAPI implements DeviceSearchService {


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadZNodeList")
    public @ResponseBody ListResultModel<List<ZNode>> loadZNodeList(@RequestBody  String[] strings) {
        return getService().loadZNodeList(strings);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadDeviceList")
    public @ResponseBody ListResultModel<List<Device>> loadDeviceList(@RequestBody String[] strings) {
        return getService().loadDeviceList(strings);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadDeviceEndPointList")
    public @ResponseBody ListResultModel<List<DeviceEndPoint>> loadDeviceEndPointList(@RequestBody String[] strings) {
        return getService().loadDeviceEndPointList(strings);
    }







    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findGWDevicesByFactory")
    public @ResponseBody
    ListResultModel<Set<String>> findGWDevicesByFactory(String factoryName) {
        return getService().findGWDevicesByFactory(factoryName);
    }



    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findPlace")
    public @ResponseBody  ListResultModel<Set<String>> findPlace(@RequestBody Condition<HomeConditionKey,JLuceneIndex> condition) {
        return getService().findPlace(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findArea")
    public @ResponseBody  ListResultModel<Set<String>> findArea(@RequestBody Condition<HomeConditionKey,JLuceneIndex> condition) {
        return getService().findArea(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findDevice")
    public @ResponseBody ListResultModel<Set<String>> findDevice(@RequestBody Condition<IOTConditionKey,JLuceneIndex> condition) {
        return getService().findDevice(condition);
    }
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findEndPoint")
    public @ResponseBody ListResultModel<Set<String>> findEndPoint(@RequestBody Condition<IOTConditionKey,JLuceneIndex> condition) {
        return getService().findEndPoint(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findZNode")
    public @ResponseBody ListResultModel<Set<String>> findZNode(@RequestBody Condition<IOTConditionKey,JLuceneIndex> condition) {
        return getService().findZNode(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findAlarm")
    public @ResponseBody  ListResultModel<Set<String>> findAlarm(@RequestBody Condition<HomeConditionKey,JLuceneIndex> condition) {
        return getService().findAlarm(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findScene")
    public @ResponseBody ListResultModel<Set<String>> findScene(@RequestBody Condition<HomeConditionKey,JLuceneIndex> condition) {
        return getService().findScene(condition);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "findZNodeByIeee")
    public @ResponseBody
    ResultModel<ZNode> findZNodeByIeee(String serialno, String userID) {
        return getService().findZNodeByIeee(serialno,userID);
    }



    public DeviceSearchService getService() {
        DeviceSearchService service = (DeviceSearchService) EsbUtil.parExpression(DeviceSearchService.class);
        return service;
    }


}
