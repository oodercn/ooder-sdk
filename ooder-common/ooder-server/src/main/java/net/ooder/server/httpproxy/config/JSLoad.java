package net.ooder.server.httpproxy.config;

public class JSLoad
{
  String path;
  String jsContent;

  public String getJsContent()
  {
    return this.jsContent;
  }
  public void setJsContent(String jsContent) {
    this.jsContent = jsContent;
  }
  public String getPath() {
    return this.path;
  }
  public void setPath(String path) {
    this.path = path;
  }
}