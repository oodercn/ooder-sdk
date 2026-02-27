package net.ooder.scene.core;

/**
 * 用户身份信息
 */
public class IdentityInfo {
    private String userId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private String domain;

    public IdentityInfo() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}
