package net.ooder.scene.provider.model.health;

import java.util.List;

public class HealthReport {
    
    private String reportId;
    private long generatedAt;
    private String period;
    private HealthSummary summary;
    private List<ServiceHealth> services;
    private List<Recommendation> recommendations;
    
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public long getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public HealthSummary getSummary() {
        return summary;
    }
    
    public void setSummary(HealthSummary summary) {
        this.summary = summary;
    }
    
    public List<ServiceHealth> getServices() {
        return services;
    }
    
    public void setServices(List<ServiceHealth> services) {
        this.services = services;
    }
    
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public static class HealthSummary {
        private int totalServices;
        private int healthyServices;
        private int unhealthyServices;
        private double availability;
        
        public int getTotalServices() {
            return totalServices;
        }
        
        public void setTotalServices(int totalServices) {
            this.totalServices = totalServices;
        }
        
        public int getHealthyServices() {
            return healthyServices;
        }
        
        public void setHealthyServices(int healthyServices) {
            this.healthyServices = healthyServices;
        }
        
        public int getUnhealthyServices() {
            return unhealthyServices;
        }
        
        public void setUnhealthyServices(int unhealthyServices) {
            this.unhealthyServices = unhealthyServices;
        }
        
        public double getAvailability() {
            return availability;
        }
        
        public void setAvailability(double availability) {
            this.availability = availability;
        }
    }
    
    public static class ServiceHealth {
        private String serviceName;
        private String status;
        private double uptime;
        private long avgLatency;
        private int errorCount;
        
        public String getServiceName() {
            return serviceName;
        }
        
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public double getUptime() {
            return uptime;
        }
        
        public void setUptime(double uptime) {
            this.uptime = uptime;
        }
        
        public long getAvgLatency() {
            return avgLatency;
        }
        
        public void setAvgLatency(long avgLatency) {
            this.avgLatency = avgLatency;
        }
        
        public int getErrorCount() {
            return errorCount;
        }
        
        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }
    }
    
    public static class Recommendation {
        private String severity;
        private String message;
        private String action;
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
    }
}
