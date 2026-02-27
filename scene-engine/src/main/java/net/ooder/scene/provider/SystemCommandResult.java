package net.ooder.scene.provider;

/**
 * 系统命令执行结果
 */
public class SystemCommandResult {
    private String command;
    private int exitCode;
    private String output;
    private String error;
    private long duration;
    private long timestamp;

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
