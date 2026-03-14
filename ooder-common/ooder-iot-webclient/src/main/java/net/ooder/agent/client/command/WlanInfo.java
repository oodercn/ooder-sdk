package net.ooder.agent.client.command;

public class WlanInfo {
    Integer mode = 0;// 0:动态IP,1:静态IP,2:PPOE,3:3G
    String account="jdsadmin";
    String password="12345678";

    public String getAccount() {
	return account;
    }

    public void setAccount(String account) {
	this.account = account;
    }

    public Integer getMode() {
	return mode;
    }

    public void setMode(Integer mode) {
	this.mode = mode;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

}
