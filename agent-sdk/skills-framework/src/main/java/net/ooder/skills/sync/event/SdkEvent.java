package net.ooder.skills.sync.event;

public abstract class SdkEvent {
    
    private String source;
    private long timestamp;
    
    public SdkEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public SdkEvent(String source) {
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
