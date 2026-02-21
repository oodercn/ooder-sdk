package net.ooder.server.httpproxy.config;

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;


public class Handle
        implements Cacheable {
    String handleid;
    String classname;
    String handname;
    String urlPrefix;
    String rule;

    public Handle() {
    }

    public Handle(String handleid) {
        this.handleid = handleid;

    }


    public int getCachedSize() {
        int size = 0;
        size += CacheSizes.sizeOfString(this.handleid);
        size += CacheSizes.sizeOfString(this.classname);
        size += CacheSizes.sizeOfString(this.handname);
        size += CacheSizes.sizeOfString(this.urlPrefix);
        size += CacheSizes.sizeOfString(this.rule);
        return size;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getUrlPrefix() {
        return this.urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getHandleid() {
        return this.handleid;
    }

    public void setHandleid(String handleid) {
        this.handleid = handleid;
    }

    public String getHandname() {
        return this.handname;
    }

    public void setHandname(String handname) {
        this.handname = handname;
    }
}