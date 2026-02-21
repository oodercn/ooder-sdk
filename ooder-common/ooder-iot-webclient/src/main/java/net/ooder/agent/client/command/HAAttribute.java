package net.ooder.agent.client.command;

import java.util.ArrayList;
import java.util.List;

public class HAAttribute {

    String attributename = "Status";
    Integer length = 1;
    String value = "1";

    public HAAttribute() {

    }

    public List<HAConfigReport> configReport = new ArrayList<>();

    public List<HAConfigReport> getConfigReport() {
        return configReport;
    }

    public void setConfigReport(List<HAConfigReport> configReport) {
        this.configReport = configReport;
    }

    public String getAttributename() {
        return attributename;
    }

    public void setAttributename(String attributename) {
        this.attributename = attributename;
    }

    public String getValue() {
        return value;


    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
