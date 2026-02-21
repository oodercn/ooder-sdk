package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.server.httpproxy.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class ResourceHandler extends AbstractHandler implements Handler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, ResourceHandler.class);
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
        if (!rule.matcher(path).matches()) {
            return false;
        }
        String projectName = this.getProjectName(request);
        String filePath = this.formatPath(request.getUrl(), projectName);
        if (projectName.indexOf(VVVERSION) > -1) {
            projectName = projectName.split(VVVERSION)[0];
        }
        String mimeType = getMimeType(filePath);

        File file = new File(JDSUtil.getJdsRealPath() + resourceMount + "/" + filePath);
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + resourceMount + "/" + projectName + "/" + filePath);
        }
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + "export/" + projectName + "/" + filePath);
        }
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + "webapp/" + projectName + "/" + filePath);
        }

        log.info(file.getAbsolutePath());
        InputStream is = null;
        if (file.exists()) {
            is = new FileInputStream(file);
            if (mimeType == null || is == null) {
                log.warn("Resource was not found or the mime type was not understood. (Found file=" + (is != null) + ") (Found mime-type=" + (mimeType != null) + ")");
                return false;
            }
            response.setMimeType(mimeType);
            response.sendResponse(is, Integer.valueOf(Long.toString(file.length())));
            return true;
        } else {
            return false;

        }


    }


}
