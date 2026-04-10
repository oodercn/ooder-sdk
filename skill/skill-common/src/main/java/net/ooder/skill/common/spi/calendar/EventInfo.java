package net.ooder.skill.common.spi.calendar;

import java.util.List;

public class EventInfo {
    
    private String eventId;
    private String title;
    private String description;
    private String organizerId;
    private List<String> attendeeIds;
    private long startTime;
    private long endTime;
    private String location;
    private String platformSource;
    
    public EventInfo() {}
    
    public EventInfo(String title, String organizerId, long startTime, long endTime) {
        this.title = title;
        this.organizerId = organizerId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    public List<String> getAttendeeIds() { return attendeeIds; }
    public void setAttendeeIds(List<String> attendeeIds) { this.attendeeIds = attendeeIds; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getPlatformSource() { return platformSource; }
    public void setPlatformSource(String platformSource) { this.platformSource = platformSource; }
}
