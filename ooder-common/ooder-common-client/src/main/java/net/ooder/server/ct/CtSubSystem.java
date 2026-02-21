/**
 * $RCSfile: CtSubSystem.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.server.ct;


import net.ooder.common.ConfigCode;
import net.ooder.common.SystemType;
import net.ooder.common.TokenType;
import net.ooder.server.SubSystem;

public class CtSubSystem implements SubSystem {

    String sysId;
    String name;
    String url;
    SystemType type;
    ConfigCode configname;
    String enname;
    TokenType tokenType;
    String icon;
    String vfsPath;
    String orgId;
    String adminId;
    Integer serialindex;

    public CtSubSystem() {

    }

    public CtSubSystem(SubSystem system) {
        this.sysId = system.getSysId();
        this.url = system.getUrl();
        this.type = system.getType();
        this.configname = system.getConfigname();
        this.enname = system.getEnname();
        this.tokenType = system.getTokenType();
        this.icon = system.getIcon();
        this.vfsPath = system.getVfsPath();
        this.orgId = system.getOrgId();
        this.adminId = system.getAdminId();
        this.name = system.getName();
        this.serialindex = system.getSerialindex();
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public SystemType getType() {
        return type;
    }

    public void setType(SystemType type) {
        this.type = type;
    }

    @Override
    public ConfigCode getConfigname() {
        return configname;
    }

    public void setConfigname(ConfigCode configname) {
        this.configname = configname;
    }

    @Override
    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    @Override
    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getVfsPath() {
        return vfsPath;
    }

    public void setVfsPath(String vfsPath) {
        this.vfsPath = vfsPath;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Override
    public Integer getSerialindex() {
        return serialindex;
    }

    public void setSerialindex(Integer serialindex) {
        this.serialindex = serialindex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CtSubSystem) {
            SubSystem subsystem = (SubSystem) obj;
            return subsystem.getSysId().equals(this.getSysId());
        }
        return super.equals(obj);

    }

}
