package net.ooder.scene.drivers.msg;

import java.util.HashSet;
import java.util.Set;

public class MsgCapabilities {

    private boolean supportPush = true;
    private boolean supportP2P = true;
    private boolean supportTopic = true;
    private boolean supportBroadcast = false;
    
    private boolean supportOffline = true;
    private boolean supportReadReceipt = true;
    private boolean supportDeliveryReceipt = false;
    private boolean supportAttachment = false;
    
    private boolean supportPriority = false;
    private boolean supportTTL = false;
    private boolean supportRetry = true;
    private boolean supportBatch = false;
    
    private int maxMessageSize = 4096;
    private int maxBatchSize = 100;
    private long defaultMessageTTL = 7 * 24 * 60 * 60 * 1000L;

    private String providerType = "local";
    private String skillEndpoint;
    private String skillId;

    public boolean isSupportPush() { return supportPush; }
    public void setSupportPush(boolean supportPush) { this.supportPush = supportPush; }
    public boolean isSupportP2P() { return supportP2P; }
    public void setSupportP2P(boolean supportP2P) { this.supportP2P = supportP2P; }
    public boolean isSupportTopic() { return supportTopic; }
    public void setSupportTopic(boolean supportTopic) { this.supportTopic = supportTopic; }
    public boolean isSupportBroadcast() { return supportBroadcast; }
    public void setSupportBroadcast(boolean supportBroadcast) { this.supportBroadcast = supportBroadcast; }
    public boolean isSupportOffline() { return supportOffline; }
    public void setSupportOffline(boolean supportOffline) { this.supportOffline = supportOffline; }
    public boolean isSupportReadReceipt() { return supportReadReceipt; }
    public void setSupportReadReceipt(boolean supportReadReceipt) { this.supportReadReceipt = supportReadReceipt; }
    public boolean isSupportDeliveryReceipt() { return supportDeliveryReceipt; }
    public void setSupportDeliveryReceipt(boolean supportDeliveryReceipt) { this.supportDeliveryReceipt = supportDeliveryReceipt; }
    public boolean isSupportAttachment() { return supportAttachment; }
    public void setSupportAttachment(boolean supportAttachment) { this.supportAttachment = supportAttachment; }
    public boolean isSupportPriority() { return supportPriority; }
    public void setSupportPriority(boolean supportPriority) { this.supportPriority = supportPriority; }
    public boolean isSupportTTL() { return supportTTL; }
    public void setSupportTTL(boolean supportTTL) { this.supportTTL = supportTTL; }
    public boolean isSupportRetry() { return supportRetry; }
    public void setSupportRetry(boolean supportRetry) { this.supportRetry = supportRetry; }
    public boolean isSupportBatch() { return supportBatch; }
    public void setSupportBatch(boolean supportBatch) { this.supportBatch = supportBatch; }
    public int getMaxMessageSize() { return maxMessageSize; }
    public void setMaxMessageSize(int maxMessageSize) { this.maxMessageSize = maxMessageSize; }
    public int getMaxBatchSize() { return maxBatchSize; }
    public void setMaxBatchSize(int maxBatchSize) { this.maxBatchSize = maxBatchSize; }
    public long getDefaultMessageTTL() { return defaultMessageTTL; }
    public void setDefaultMessageTTL(long defaultMessageTTL) { this.defaultMessageTTL = defaultMessageTTL; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getSkillEndpoint() { return skillEndpoint; }
    public void setSkillEndpoint(String skillEndpoint) { this.skillEndpoint = skillEndpoint; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }

    public Set<String> getUnsupportedCapabilities() {
        Set<String> unsupported = new HashSet<String>();
        if (!supportBroadcast) unsupported.add("broadcast");
        if (!supportDeliveryReceipt) unsupported.add("delivery-receipt");
        if (!supportAttachment) unsupported.add("attachment");
        if (!supportPriority) unsupported.add("priority");
        if (!supportTTL) unsupported.add("ttl");
        if (!supportBatch) unsupported.add("batch");
        return unsupported;
    }

    public boolean requiresFallback() {
        return !supportBroadcast || !supportDeliveryReceipt || !supportAttachment 
            || !supportPriority || !supportTTL || !supportBatch;
    }

    public static MsgCapabilities forLocal() {
        MsgCapabilities caps = new MsgCapabilities();
        caps.setProviderType("local");
        caps.setSkillId("skill-msg-local");
        caps.setSupportPush(true);
        caps.setSupportP2P(true);
        caps.setSupportTopic(true);
        caps.setSupportOffline(true);
        caps.setSupportReadReceipt(true);
        caps.setSupportRetry(true);
        return caps;
    }
}
