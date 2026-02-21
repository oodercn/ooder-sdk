package net.ooder.agent.client.command;

import com.alibaba.fastjson.JSON;
import  net.ooder.common.md5.MD5;
import  net.ooder.agent.client.enums.CommandEnums;

import java.io.File;
import java.io.IOException;

public class FirmwareCommand extends Command {

    String md5;
    String size;
    String value;

    public FirmwareCommand(CommandEnums command) {
        super(command);
        md5 = "EEC695A9DA0A2E934E14E0EA18319F28";
        size = "38867416";
        value = "http://console.itjds.net/jds/gateway_ota_V4.01.002.bin";

    }

    public static void main(String[] args) throws IOException {
        FirmwareCommand fw=new FirmwareCommand(CommandEnums.FirmwareDownload);
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
}
