package net.ooder.agent.client.command;

import com.alibaba.fastjson.JSON;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.common.md5.MD5;
import  net.ooder.agent.client.enums.CommandEnums;

import java.io.File;
import java.io.IOException;


@EsbBeanAnnotation(id = "FirmwareDownload",dataType=ContextType.Command, name = "网关固件升级", expressionArr = "FirmwareDownloadCommand()", desc = "网关固件升级")

public class FirmwareDownloadCommand extends FirmwareCommand {

	public FirmwareDownloadCommand() {
		super(CommandEnums.FirmwareDownload);
	}
	public static void main(String[] args) throws IOException {
	    FirmwareDownloadCommand fw=new FirmwareDownloadCommand();
	    File file=new File("g:\\ModuleDoorLock.bin");

		try {
			String  hash=MD5.getHashString(file);
			fw.setMd5(hash.toUpperCase());
			fw.setSize(""+file.length());
			fw.setValue(
					"http://console.itjds.net/jds/ModuleDoorLock2.1.1.bin");
			System.out.println(JSON.toJSON(fw));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
