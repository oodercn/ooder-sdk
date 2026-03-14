package net.ooder.agent.client.iot;

import  net.ooder.common.CommonConfig;

public class HomeConstants {
    public static final String SYSTEM_CODE = "systemCode";

    public static final String CONFIG_KEY = "home";

    public static final String CONFIG_ENGINE_KEY = "org";

    public static final String CONFIG_IOT_KEY = "iot";

    public static Integer ZNNODE_ALARM_STATUS_DEFAULT = 1;// 默认场景

    public static Integer ZNNODE_ALARM_STATUS_OTHER = 0;// 传感器

    public static String DEVICE_DATATYPE_STATEONOFF_ON = "1";// 开

    public static String DEVICE_DATATYPE_STATEONOFF_OFF = "0";// 关
    //
    // //
    public static String DEVICE_DATATYPE_ZNONE_STATUS = "Zone_Status";// 报警状态

    public static String DEVICE_DATATYPE_DEVICETROUBLE = "Device Trouble";

    //

    //
    public static String DEVICE_DEFAULT_EP = "01";// 默认HA节点

    // // 设备类型
    public static Integer DEVICE_TYPE_GATEWAY = 0;// 网关

    public static Integer DEVICE_TYPE_DEFAULT = 100;// 未知类型设备

    public static Integer DEVICE_TYPE_CAPTURE = 11;// 摄像头

    public static String DEVICE_EVENTTPE_HISCOMMAND = "HISCOMMAND";// 历史命令集合

    public static String DEVICE_EVENTTPE_ERRORREPORT = "ERRORREPORT";// 错误报告

    public static String DEVICE_EVENTTPE_SYSTEM = "SYSTEM";//

    public static String COMMAND_DIMMABLELIGHTOPERATION = "dimmableLightOperation";// 灯

    public static String COMMAND_OPERATION = "operation";// 切换

    public static String COMMAND_MAINSOUTLETOPERATION = "mainsOutLetOperation";// 电源设备

    public static String COMMAND_BINDDEVICE_CLUSTER = "clusterid";// 绑定

    public static String COMMAND_BINDDEVICE_CLUSTER_ONOFF = "on/off";// 开关标记

    public static String COMMAND_OPERATION_ON = "on";// 开

    public static String COMMAND_OPERATION_OFF = "off";// 关

    public static String COMMAND_OPERATION_PARAMETER = "parameter1";// 调光参数

    public static String GATEWAYFOLDERNAME = "GATEWAY";

    public static String GATEWAYNETWORKINFO = "NETWORKINFO";

    public static String GATEWAYCHECKLIST = "CHECKLIST";

    public static String DEVICE_FACTORY_MARVELL = "marvell";

    public static String DEVICE_FACTORY_NETVOX = "netvox";

    public static String DEVICE_FACTORY_HUOHE = "huohe";

    public static String DEVICE_FACTORY_GREENPEAK = "greenpeak";

    public static String SYSTEM_SERVER_TYPE_APP = "app";//
    public static String SYSTEM_SERVER_TYPE_CONSOL = "consol";//

    public static String SYSTEM_UDPSERVER = CommonConfig.getValue("HOME.UDPServer");

    public static String SYSTEM_COMMANDSERVERURL = "/comet";

    public static final String CTX_USER_ID = "GwEngine.USERID";

    /**
     * 创建指定数量的随机字符串
     * 
     * @param numberFlag
     *            是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length) {
	String retStr = "";
	String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
	int len = strTable.length();
	boolean bDone = true;
	do {
	    retStr = "";
	    int count = 0;
	    for (int i = 0; i < length; i++) {
		double dblR = Math.random() * len;
		int intR = (int) Math.floor(dblR);
		char c = strTable.charAt(intR);
		if (('0' <= c) && (c <= '9')) {
		    count++;
		}
		retStr += strTable.charAt(intR);
	    }
	    if (count >= 2) {
		bDone = false;
	    }
	} while (bDone);

	return retStr;
    }

}
