package net.ooder.server.httpproxy.handler.cls;


import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.context.JDSActionContext;
import net.ooder.server.httpproxy.core.*;
import net.ooder.server.httpproxy.handler.DirectoryHandler;
import net.ooder.server.httpproxy.handler.ResourceHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ClsDispatcherHandle extends AbstractHandler implements Handler {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, ResourceHandler.class);
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
        if (!ruleMatches || !request.getMethod().toUpperCase().equals("GET")) {
            return false;
        }
        String projectName = this.getProjectName(request);

        String className = this.formatPath(request.getUrl(), projectName);

        if (className.indexOf("/") > -1) {
            return false;
        }


        if (projectName.indexOf(VVVERSION) > -1) {
            projectName = projectName.split(VVVERSION)[0];
        }


        List<Map> cssList = new ArrayList<Map>();
        File file = new File(JDSUtil.getJdsRealPath() + File.separator + "webapp/" + projectName + "/css");
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + "export/" + projectName + "/css");
        }

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File ifile : files) {
                Map<String, String> values = new HashMap<>();
                values.put("path", "css/" + ifile.getName());
                cssList.add(values);
            }
        }

        JDSActionContext.getActionContext().getContext().put("cssList", cssList);
        JDSActionContext.getActionContext().getContext().put("projectName", projectName);
        JDSActionContext.getActionContext().getContext().put("className", className.substring(0, className.length() - ".cls".length()));

        this.sendFtl(request, response, resourceMount + "/ftl/cls.ftl");
        return true;
    }

    ;

}
