package net.ooder.bpm.client.data;

/**
 * time 06-01-01
 *
 * @author wenzhang
 */

public class FormClassBean implements java.io.Serializable {

    private String name;
    private String id;
    private String experss;

    private String desc;
    private String jspUrl;
    private String path;
    private boolean isMain;

    private String actionurl;

    private String mainClass;


    public String getActionurl() {
        return actionurl;
    }

    public void setActionurl(String actionurl) {
        this.actionurl = actionurl;
    }

    public FormClassBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJspUrl() {
        return this.jspUrl;
    }

    public void setJspUrl(String jspUrl) {
        this.jspUrl = jspUrl;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExperss() {
        return experss;
    }

    public void setExperss(String experss) {
        this.experss = experss;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}