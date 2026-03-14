package net.ooder.agent.client.command;

public class HAConfigReport {

    String minReportInterval = "1";
    String maxReportInterval = "1800";
    String reportAbleChange = "1";

    public HAConfigReport() {

    }

    public String getMinReportInterval() {
        return minReportInterval;
    }

    public void setMinReportInterval(String minReportInterval) {
        this.minReportInterval = minReportInterval;
    }

    public String getMaxReportInterval() {
        return maxReportInterval;
    }

    public void setMaxReportInterval(String maxReportInterval) {
        this.maxReportInterval = maxReportInterval;
    }

    public String getReportAbleChange() {
        return reportAbleChange;
    }

    public void setReportAbleChange(String reportAbleChange) {
        this.reportAbleChange = reportAbleChange;
    }
}
