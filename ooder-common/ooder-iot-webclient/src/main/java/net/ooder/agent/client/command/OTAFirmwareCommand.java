package net.ooder.agent.client.command;

import com.alibaba.fastjson.JSON;
import  net.ooder.common.ContextType;
import  net.ooder.common.md5.MD5;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

import java.io.File;
import java.io.IOException;

@EsbBeanAnnotation(id = "OtaUpgrade",dataType=ContextType.Command, name = "传感器OTA", expressionArr = "OtaUpgradeCommand()", desc = "传感器OTA")
public class OTAFirmwareCommand extends FirmwareCommand {

    String sensorieee = "0086B000000000000A";

    String md5;
    String size;
    String value;

    public OTAFirmwareCommand() {
        super(CommandEnums.OtaUpgrade);
    }

    String groupname;
    String groupid;


    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSensorieee() {
        return sensorieee;
    }

    public void setSensorieee(String sensorieee) {
        this.sensorieee = sensorieee;
    }

    public static void main(String[] args) throws IOException {
        OTAFirmwareCommand fw=new OTAFirmwareCommand();
        File file=new File("g:\\gateway_ota_V4.01.002.bin");

        try {
            String  hash=MD5.getHashString(file);
            fw.setMd5(hash.toUpperCase());
            fw.setSize(""+file.length());
            fw.setValue(
                    "http://console.itjds.net/jds/gateway_ota_V4.01.002.bin");
            System.out.println(JSON.toJSON(fw));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
