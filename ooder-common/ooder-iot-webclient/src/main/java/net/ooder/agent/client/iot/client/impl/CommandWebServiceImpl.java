package net.ooder.agent.client.iot.client.impl;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.CommandReportStatus;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.client.CommandWebService;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.json.device.BindInfo;
import net.ooder.agent.client.iot.json.device.SenceMode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.context.JDSActionContext;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.server.JDSClientService;
import org.springframework.web.bind.annotation.RequestBody;

@EsbBeanAnnotation(id = "CommandWebService", name = "网关命令服务", expressionArr = "CommandWebServiceImpl()", desc = "网关命令服务")
public class CommandWebServiceImpl implements CommandWebService {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CommandWebServiceImpl.class);

    public ResultModel<Boolean> bindListReport(@RequestBody BindInfo bindInfo) {
		ResultModel result = new ResultModel();
		DeviceEndPoint ep= null;
		try {
			ep = CtIotFactory.getCtIotService().getEndPointByIeee(bindInfo.getIeee());
			HomeServer.getAppEngine().logging(ep.getDevice().getBindingaccount(), JSONObject.toJSONString(bindInfo).toString(), "bindListReport",bindInfo.getIeee());
		} catch (JDSException e) {
			 result = new ErrorResultModel();
			((ErrorResultModel) result).setErrcode(3001);
			((ErrorResultModel) result).setErrdes(e.getMessage());
		}
	return result;
    }

    public ResultModel<Boolean> modeListReport(@RequestBody SenceMode mode) {
	ResultModel result = new ErrorResultModel();
	((ErrorResultModel) result).setErrcode(3001);
	((ErrorResultModel) result).setErrdes("未实现");
	return result;
    }



	void logging(Object obj, String methodName) {

		try {
			if (getGWClient().getConnectInfo() != null && getGWClient().getConnectInfo().getLoginName() != null) {
				HomeServer.getAppEngine().logging(getGWClient().getConnectInfo().getLoginName(), JSONObject.toJSONString(obj).toString(), methodName,null);
			}
		} catch (HomeException e) {
			e.printStackTrace();
		}


	}

    public ResultModel<Boolean> setLightSensorStatusInfo(String sensorId, Integer value) {

	ResultModel<Boolean> result = new ResultModel<Boolean>();
	try {
	    getJDSClientService().beginTransaction();
	    getJDSClientService().setLightSensor(sensorId, value);
	    getJDSClientService().commitTransaction();
	} catch (HomeException e) {
	    try {
		getGWClient().rollbackTransaction();
	    } catch (HomeException robacke) {

	    }
	    result = new ErrorResultModel();
	    result.setData(false);
	    ((ErrorResultModel) result).setErrcode(e.getErrorCode());
	    ((ErrorResultModel) result).setErrdes(e.getMessage());
	}
	return result;
    }

    public ResultModel<Boolean> setOutLetSensorInfo(String sensorId, Integer value) {

	ResultModel<Boolean> result = new ResultModel<Boolean>();
	try {
	    getJDSClientService().beginTransaction();
	    getJDSClientService().setOutLetSensor(sensorId, value == 0 ? false : true);
	    getJDSClientService().commitTransaction();
	} catch (HomeException e) {
	    try {
		getGWClient().rollbackTransaction();
	    } catch (HomeException robacke) {

	    }
	    result = new ErrorResultModel();
	    result.setData(false);
	    ((ErrorResultModel) result).setErrcode(e.getErrorCode());
	    ((ErrorResultModel) result).setErrdes(e.getMessage());
	}
	return result;
    }

    public ResultModel<Boolean> commandReport(CommandReportStatus commandReport) {
	ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

	try {
	    getGWClient().beginTransaction();
	    getGWClient().commandReport(commandReport.getCommandId(), commandReport.getStatus(), commandReport.getModeId(), CommandEventEnums.fromCode(commandReport.getCode()));
	    getGWClient().commitTransaction();
	} catch (HomeException e) {
	    try {
		getGWClient().rollbackTransaction();
	    } catch (HomeException robacke) {

	    }
	    userStatusInfo = new ErrorResultModel();
	    userStatusInfo.setData(false);
	    ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
	    ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
	}
	return userStatusInfo;
    }

    public ResultModel<Boolean> bindingStatusReport(CommandReportStatus report) {
	ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

	try {
	    getGWClient().beginTransaction();
	    if (report.getCommand().equals("bind")) {
		getGWClient().bindSuccess((String) report.getSensorieees().get(0));
	    } else {
		getGWClient().unbindSuccess((String) report.getSensorieees().get(0));
	    }
	    getGWClient().commitTransaction();
	} catch (HomeException e) {
	    try {
		getGWClient().rollbackTransaction();
	    } catch (HomeException robacke) {

	    }
	    userStatusInfo = new ErrorResultModel();
	    userStatusInfo.setData(false);
	    ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
	    ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
	}
	return userStatusInfo;
    }

    private AppClient getJDSClientService() throws HomeException {
	AppClient appClient = null;

	JDSClientService client = (JDSClientService) JDSActionContext.getActionContext().Par("$JDSC");

	if (client == null) {
	    throw new HomeException("not login!", 1005);
	}

	appClient = HomeServer.getInstance().getAppClient(client);

	if (appClient == null) {
	    throw new HomeException("not login!", 1005);
	}

	return appClient;
    }

    private GWClient getGWClient() throws HomeException {
	GWClient gwClient = null;
	try {
	    JDSClientService client = (JDSClientService) JDSActionContext.getActionContext().Par("$JDSGWC");
	    gwClient = HomeServer.getInstance().getGWClient(client);
	} catch (Exception e) {
		e.printStackTrace();
	    throw new HomeException("not login!", 1005);
	}

	if (gwClient == null) {
	    throw new HomeException("not login!", 1005);
	}

	return gwClient;
    }

}
