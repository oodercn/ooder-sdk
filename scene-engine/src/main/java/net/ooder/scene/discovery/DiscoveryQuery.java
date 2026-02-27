package net.ooder.scene.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryQuery {
    private DiscoveryType type;
    private String query;
    private DiscoveryScope scope;
    private Map<String, Object> filters;

    public DiscoveryQuery(DiscoveryType type, String query) {
        this.type = type;
        this.query = query;
        this.scope = DiscoveryScope.PERSONAL;
        this.filters = new ConcurrentHashMap<>();
    }

    public DiscoveryType getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public DiscoveryScope getScope() {
        return scope;
    }

    public void setScope(DiscoveryScope scope) {
        this.scope = scope;
    }

    public void addFilter(String key, Object value) {
        filters.put(key, value);
    }

    public Object getFilter(String key) {
        return filters.get(key);
    }

    public Map<String, Object> getFilters() {
        return filters;
    }
}
