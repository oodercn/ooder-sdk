package net.ooder.server.httpproxy.handler;


import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.context.JDSActionContext;
import net.ooder.server.httpproxy.core.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class IndexHandler extends AbstractHandler implements Handler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, IndexHandler.class);
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");
    public static final ConfigOption RESOURCE_MOUNT_OPTION = new ConfigOption("resourceMount", "/", "A path within the classpath to the root of the folder to share.");


    Pattern rule;
    private String resourceMount;

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
        this.resourceMount = RESOURCE_MOUNT_OPTION.getProperty(server, handlerName);
        return true;
    }

    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {

        String path = request.getPath();

        boolean ruleMatches = rule.matcher(path).matches();
        String projectName =null;
        String indexPage =null;
        if (this.getServer().getProxyHost()!=null){
            projectName = this.getServer().getProxyHost().getProjectName();
            indexPage = this.getServer().getProxyHost().getIndexPage();
        }

        if (!ruleMatches ) {
            return false;
        }

        if (indexPage == null) {
            indexPage = "App.index";
        }

        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }

        if (projectName == null) {
            projectName = path;
            if (path.indexOf("/") > -1) {
                projectName = path.substring(0, path.indexOf("/"));
            }
        }

        String className = path.substring((projectName + "/").length());

        if (className.indexOf("/") > -1) {
            return false;
        }

        List<Map> cssList = new ArrayList<Map>();
        File file = new File(JDSUtil.getJdsRealPath()  + "webapp/" + projectName + "/css");
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            for (File ifile : files) {
                Map<String, String> values = new HashMap<>();
                if (ifile.getName().endsWith(".css")){
                    values.put("path", "css/" + ifile.getName());
                    cssList.add(values);
                }

            }
        }


        JDSActionContext.getActionContext().getContext().put("cssList", cssList);
        JDSActionContext.getActionContext().getContext().put("indexPage", indexPage);
        JDSActionContext.getActionContext().getContext().put("projectName", projectName);
        JDSActionContext.getActionContext().getContext().put("className", className.substring(0, className.length() - ".cls".length()));
        this.sendFtl(request, response, resourceMount + "/ftl/index.ftl");
        return true;
    }

    ;

}
