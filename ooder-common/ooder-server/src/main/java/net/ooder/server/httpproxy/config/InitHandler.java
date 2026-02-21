package net.ooder.server.httpproxy.config;

public class InitHandler
{
  String indexPageName;
  String initJsPath;
  String limitTime;
  String rule;

  public String getRule()
  {
    return this.rule;
  }
  public void setRule(String rule) {
    this.rule = rule;
  }
  public String getIndexPageName() {
    return this.indexPageName;
  }
  public void setIndexPageName(String indexPageName) {
    this.indexPageName = indexPageName;
  }
  public String getInitJsPath() {
    return this.initJsPath;
  }
  public void setInitJsPath(String initJsPath) {
    this.initJsPath = initJsPath;
  }
  public String getLimitTime() {
    return this.limitTime;
  }
  public void setLimitTime(String limitTime) {
    this.limitTime = limitTime;
  }
}