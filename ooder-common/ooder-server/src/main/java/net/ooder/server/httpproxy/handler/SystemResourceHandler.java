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
import java.util.regex.Pattern;


public class SystemResourceHandler extends AbstractHandler implements Handler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, SystemResourceHandler.class);
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

        boolean ruleMatches = rule.matcher(request.getPath()).matches();
        if (!ruleMatches) {
            return false;
        }
        String path = request.getPath();
        String resource = Http.join(resourceMount, path.substring(getUrlPrefix().length()));
        if (resource.endsWith("/")) {
            resource += defaultResource;
        } else if (resource.lastIndexOf('.') < 0) {
            resource += "/" + defaultResource;
        }

        log.info("Loading resource: " + resource);

        String mimeType = getMimeType(resource);

        File file = new File(JDSUtil.getJdsRealPath() + resource);
        if (!file.exists()) {
            file = new File(JDSUtil.getJdsRealPath() + "ood/" + resource);
        }

        if (mimeType == null) {
            log.warn("mime type was not understood.(Found file=" + file.getAbsolutePath() + ")");
            return false;
        }
        if (file.exists()) {
            InputStream is  = new FileInputStream(file);
            if (is != null) {
                response.setMimeType(mimeType);
                response.sendResponse(is, Integer.valueOf(Long.toString(file.length())));
                return true;
            } else {
                return false;
            }
        }
        return false;

    }


}
