package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.calendar.EventInfo;
import net.ooder.skill.common.spi.calendar.TimeSlot;

import java.util.List;

public interface CalendarService {
    
    EventInfo createEvent(EventInfo event);
    
    EventInfo getEvent(String eventId);
    
    EventInfo updateEvent(EventInfo event);
    
    void deleteEvent(String eventId);
    
    List<EventInfo> listEvents(String userId, long startTime, long endTime);
    
    List<TimeSlot> findFreeTime(List<String> userIds, long startTime, long endTime);
    
    void syncFromPlatform(String platform);
    
    List<String> getAvailablePlatforms();
}
