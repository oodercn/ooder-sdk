package net.ooder.server.httpproxy.handler.cls;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.md5.MD5;
import net.ooder.common.util.StringUtility;
import net.ooder.config.JDSUtil;
import net.ooder.server.httpproxy.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class ClsHandler extends AbstractHandler implements Handler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, AbstractHandler.class);


    public static final ConfigOption RESOURCE_MOUNT_OPTION = new ConfigOption("resourceMount", "/", "A path within the classpath to the root of the folder to share.");
    public static final ConfigOption DEFAULT_RESOURCE_OPTION = new ConfigOption("default", "index.html", "The default resource name.");
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");

    private String resourceMount;
    private String defaultResource;


    Pattern rule;

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        this.resourceMount = RESOURCE_MOUNT_OPTION.getProperty(server, handlerName);
        this.defaultResource = DEFAULT_RESOURCE_OPTION.getProperty(server, handlerName);
        rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
        return true;
    }


    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        boolean ruleMatches = rule.matcher(path).matches();
        if (!ruleMatches) {
            return false;
        }
        String projectName = this.getProjectName(request);


        boolean projectMatches = false;
        if (projectName != null) {
            Pattern projectRule = Pattern.compile(".*/(" + projectName + ")?.*");
            projectMatches = projectRule.matcher(path).matches();
        }
        if (!projectMatches) {
            return false;
        }


        path = this.formatPath(request.getUrl(), projectName);
        String mimeType = getMimeType(path);
        path = StringUtility.replace(path, ".js", "");
        path = StringUtility.replace(path, ".cls", "");
        String className = StringUtility.replace(path, "/", ".");
        if (projectName.indexOf(VVVERSION) > -1) {
            projectName = projectName.split(VVVERSION)[0];
        }
        File file = new File(JDSUtil.getJdsRealPath() + "webapp/" + projectName + "/cls/" + MD5.getHashString(className));
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + resourceMount + "/" + projectName + "/cls/" + MD5.getHashString(className));
        }

        log.info("className=" + className);
        log.info("file=" + file.getPath());
        InputStream is = null;
        if (file.exists()) {
            is = new FileInputStream(file);
        }


        if (mimeType == null || is == null) {
            log.warn("Resource was not found or the mime type was not understood. (Found file=" + (is != null) + ") (Found mime-type=" + (mimeType != null) + ")");
            return false;
        }
        if (is != null) {
            response.setMimeType(mimeType);
            response.sendResponse(is, Integer.valueOf(Long.toString(file.length())));
            return true;
        } else {
            return false;
        }


    }


}
