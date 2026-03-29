package net.ooder.scene.skill.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知查询条件
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class NotificationQuery {
    
    private List<NotificationType> types;
    private List<NotificationService.PushChannel> channels;
    private String sceneGroupId;
    private String senderId;
    private Boolean read;
    private Long createTimeFrom;
    private Long createTimeTo;
    private String keyword;
    private int pageNum = 1;
    private int pageSize = 20;
    private String sortBy = "createTime";
    private String sortOrder = "desc";
    
    public NotificationQuery() {}
    
    public List<NotificationType> getTypes() {
        return types;
    }
    
    public void setTypes(List<NotificationType> types) {
        this.types = types;
    }
    
    public NotificationQuery addType(NotificationType type) {
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        this.types.add(type);
        return this;
    }
    
    public List<NotificationService.PushChannel> getChannels() {
        return channels;
    }
    
    public void setChannels(List<NotificationService.PushChannel> channels) {
        this.channels = channels;
    }
    
    public NotificationQuery addChannel(NotificationService.PushChannel channel) {
        if (this.channels == null) {
            this.channels = new ArrayList<>();
        }
        this.channels.add(channel);
        return this;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public Boolean getRead() {
        return read;
    }
    
    public void setRead(Boolean read) {
        this.read = read;
    }
    
    public Long getCreateTimeFrom() {
        return createTimeFrom;
    }
    
    public void setCreateTimeFrom(Long createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }
    
    public Long getCreateTimeTo() {
        return createTimeTo;
    }
    
    public void setCreateTimeTo(Long createTimeTo) {
        this.createTimeTo = createTimeTo;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
