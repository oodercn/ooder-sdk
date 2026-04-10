package net.ooder.annotation.action.args;

import java.util.ArrayList;
import java.util.List;

public class ShowModuleArgs {

    String methodName = "{ood.showModule2()}";
    String params = "{args[1].tagVar}";
    String data = "{page.getData()}";
    String euClassName;
    String targetFrame;
    String childName;

    public ShowModuleArgs() {

    }

    public ShowModuleArgs(String euClassName, String targetFrame, String childName) {
        this.euClassName = euClassName;
        this.targetFrame = targetFrame;
        this.childName = childName;
    }

    public List<String> toArr() {
        List<String> args = new ArrayList<>();
        args.add(methodName);
        args.add(null);
        args.add(null);
        args.add(euClassName);
        args.add(targetFrame);
        args.add(childName);
        args.add(params);
        args.add(data);
        args.add("{page}");
        args.add("{true}");
        return args;
    }


    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getUiClassName() {
        return euClassName;
    }

    public void setUiClassName(String euClassName) {
        this.euClassName = euClassName;
    }

    public String getTargetFrame() {
        return targetFrame;
    }

    public void setTargetFrame(String targetFrame) {
        this.targetFrame = targetFrame;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}