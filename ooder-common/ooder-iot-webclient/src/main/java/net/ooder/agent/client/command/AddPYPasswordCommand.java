package net.ooder.agent.client.command;

import com.alibaba.fastjson.JSONObject;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "AddPYPassword",dataType=ContextType.Command, name = "添加PY密码", expressionArr = "AddPYPasswordCommand()", desc = "添加PY密码")
public class AddPYPasswordCommand extends PasswordCommand {
    String seed = "12345678";
    String interval = new Integer(24 * 60 * 60 * 1000).toString();

    public AddPYPasswordCommand() {
        super(CommandEnums.AddPYPassword);
        this.setStartTime(null);
        this.setEndTime(null);

    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public static void main(String[] args) {
        AddPYPasswordCommand py = new AddPYPasswordCommand();
        py.setGatewayieee("30ae7b16vgpb");
        py.setSeed("11111111");
        py.setSensorieee("00158d0001b66971");
        py.setPassId(9999);
        py.setInterval(new Integer(24 * 60 * 60 * 1000).toString());
        String pyPassword = JSONObject.toJSON(py).toString();
        System.out.println(pyPassword);

    }
}
