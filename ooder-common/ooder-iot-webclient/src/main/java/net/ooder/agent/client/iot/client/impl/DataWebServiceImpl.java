package net.ooder.agent.client.iot.client.impl;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.client.DataWebService;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.json.AlarmMessageInfo;
import net.ooder.agent.client.iot.json.SensorHistoryDataInfo;
import net.ooder.agent.client.iot.json.device.SensorData;
import  net.ooder.common.JDSException;
import  net.ooder.common.util.DateUtility;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.config.ErrorListResultModel;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.msg.SensorMsg;
import  net.ooder.server.JDSClientService;
import  net.ooder.web.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EsbBeanAnnotation(id = "DataWebService", name = "网关命令服务", expressionArr = "DataWebServiceImpl()", desc = "网关命令服务")
public class DataWebServiceImpl implements DataWebService {
    private static final Logger logger = LoggerFactory.getLogger(DataWebService.class);

    public ResultModel<Boolean> addAlarm(SensorData sensor) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {

            String serialno = sensor.getSerialno();
            DeviceEndPoint ep = null;
            try {
                ep = getGWClient().getEndPointByIeee(serialno);
            } catch (HomeException e) {
            }
            if (ep == null) {
                try {
                    Device device = getGWClient().getDeviceByIeee(serialno);
                    if (device != null && device.getDeviceEndPoints().size() > 0) {
                        ep = device.getDeviceEndPoints().get(0);
                    }
                } catch (HomeException e) {
                }
            }

            if (ep != null) {
                if (sensor.getAttributename().equals(DeviceDataTypeKey.Status.getType())) {
                    sensor.setValue("1");
                }

                if (sensor.getValue().equals("0")) {
                    sensor.setValue("1");
                }

                HomeServer.getAppEngine().logging(ep.getDevice().getBindingaccount(), JSONObject.toJSONString(sensor).toString(), "addAlarm", sensor.getSerialno());
                if (sensor.getStatus().equals(DeviceStatus.OFFLINE.toString())) {
                    getGWClient().addAlarm(ep.getEndPointId(), DeviceDataTypeKey.Status, sensor.getValue(), sensor.getTime());
                } else {
                    getGWClient().addAlarm(ep.getEndPointId(), DeviceDataTypeKey.fromType(sensor.getAttributename()), sensor.getValue(), sensor.getTime());
                }
            }
            // getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<Boolean> addData(SensorData sensor) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {

            String serialno = sensor.getSerialno();
            DeviceEndPoint ep = null;
            try {
                ep = getGWClient().getEndPointByIeee(serialno);
            } catch (HomeException e) {
            }
            if (ep == null) {
                try {
                    Device device = getGWClient().getDeviceByIeee(serialno);
                    if (device != null && device.getDeviceEndPoints().size() > 0) {
                        ep = device.getDeviceEndPoints().get(0);
                    }
                } catch (HomeException e) {
                }
            }

            if (ep != null) {
                // 未知设备
                HomeServer.getAppEngine().logging(ep.getDevice().getBindingaccount(), JSONObject.toJSONString(sensor).toString(), "addData", sensor.getSerialno());
                if (sensor.getStatus().equals(DeviceStatus.OFFLINE.toString())) {
                    getGWClient().addData(ep.getEndPointId(), DeviceDataTypeKey.Status, DeviceStatus.OFFLINE.toString(), sensor.getTime());
                } else {
                    getGWClient().addData(ep.getEndPointId(), DeviceDataTypeKey.fromType(sensor.getAttributename()), sensor.getValue(), sensor.getTime());

                }
            }
            // getGWClient().commitTransaction();
        } catch (HomeException e) {
            e.printStackTrace();
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }


    public ResultModel<List<AlarmMessageInfo>> getAlarmMessageByPlaceId(String placeId) {
        ResultModel<List<AlarmMessageInfo>> userStatusInfo = new ResultModel<List<AlarmMessageInfo>>();
        try {
            List<AlarmMessageInfo> msgs = new ArrayList<AlarmMessageInfo>();
            try {
                List<SensorMsg> msglist = this.getJDSClientService().getAlarmMessageByPlaceId(placeId);
                for (SensorMsg msg : msglist) {
                    AlarmMessageInfo info = new AlarmMessageInfo();
                    info.setId(msg.getId());
                    info.setTitle(msg.getTitle());
                    info.setMessage(msg.getTitle());
                    info.setSensorId(msg.getSensorId());
                    info.setTime(DateUtility.formatDate(new Date(msg.getReceiveTime()), "yyyy-MM-dd HH:mm:ss"));

                    msgs.add(info);
                }
            } catch (Exception e) {
                throw new HomeException("数据读取错误！");
            }

            userStatusInfo.setData(msgs);
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }


    @Override
    public ResultModel<List<SensorHistoryDataInfo>> getHistorySensorData(String sensorId, Date starttime, Date endtime, Integer currentIndex, Integer pageSize) {
        ResultModel<List<SensorHistoryDataInfo>> userStatusInfo = new ResultModel<List<SensorHistoryDataInfo>>();
        try {
            List<SensorHistoryDataInfo> msgs = new ArrayList<SensorHistoryDataInfo>();

            List<SensorMsg> msglist = this.getJDSClientService().getSensorHistoryData(sensorId, starttime, endtime, currentIndex, pageSize);
            userStatusInfo = PageUtil.getDefaultPageList(msglist, SensorHistoryDataInfo.class);

        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<SensorHistoryDataInfo>> getLastSensorHistoryData(String sensorId, Integer currentIndex, Integer pageSize) {
        ListResultModel<List<SensorHistoryDataInfo>> userStatusInfo = new ListResultModel<List<SensorHistoryDataInfo>>();
        try {
            List<SensorHistoryDataInfo> msgList = new ArrayList<SensorHistoryDataInfo>();
            List<SensorMsg> msglist = this.getJDSClientService().getLastSensorHistoryData(sensorId, currentIndex, pageSize);
            userStatusInfo = PageUtil.getDefaultPageList(msglist, SensorHistoryDataInfo.class);

        } catch (HomeException e) {
            userStatusInfo = new ErrorListResultModel();
            ((ErrorListResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    private AppClient getJDSClientService() throws HomeException {
        AppClient appClient = null;

        JDSClientService client = (JDSClientService) EsbUtil.parExpression(JDSClientService.class);

        if (client == null) {
            throw new HomeException("not login!", 1005);
        }
        try {
            appClient = HomeServer.getInstance().getAppClient(client);
        } catch (JDSException e) {
            throw new HomeException("not login!", 1005);
        }
        if (appClient == null) {
            throw new HomeException("not login!", 1005);
        }

        return appClient;
    }

    private GWClient getGWClient() throws HomeException {
        GWClient gwClient = null;
        try {
            JDSClientService client = (JDSClientService) EsbUtil.parExpression("$JDSGWC");
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
