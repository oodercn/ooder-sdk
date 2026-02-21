package net.ooder.server.httpproxy.handler;


import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSUtil;
import net.ooder.server.httpproxy.core.*;

import java.io.*;
import java.util.regex.Pattern;


public class OtherUrlProxyHandler extends AbstractHandler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, OtherUrlProxyHandler.class);
    public static final ConfigOption RESOURCE_MOUNT_OPTION = new ConfigOption("resourceMount", "/", "A path within the classpath to the root of the folder to share.");
    public static final ConfigOption DEFAULT_RESOURCE_OPTION = new ConfigOption("default", "index.html", "The default resource name.");

    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs.");
    Pattern rule;

    private String resourceMount;

    public boolean initialize(String handlerName, Server server) {
        try {
            super.initialize(handlerName, server);

            rule = Pattern.compile(RULE_OPTION.getProperty(server, handlerName));
            this.resourceMount = RESOURCE_MOUNT_OPTION.getProperty(server, handlerName);

            log.info("Rule=" + rule.pattern());

            return true;
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
            log.error(e.toString());
            return false;
        }
    }


    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        boolean ruleMatches = rule.matcher(request.getUrl()).matches();

        if (!ruleMatches) {
            return false;
        }

        String resource = request.getUrl();
        if (resource.startsWith("http://")) {
            resource = resource.substring("http://".length());
        }

        File file = new File(JDSUtil.getJdsRealPath() + "\\" + resourceMount + "\\" + resource);
        InputStream is = null;
        if (file.exists() && !file.isDirectory()) {
            is = new FileInputStream(file);
        } else {
            String url = resource;
            this.copyStreamToFile("http://" + url, file);
            if (file.exists() && !file.isDirectory()) {
                is = new FileInputStream(file);
            }
        }

        // Access-Control-Allow-Origin:*

        response.addHeader("Access-Control-Allow-Origin", "*");
        String mimeType = getMimeType(resource);
        response.setMimeType(mimeType);
        response.sendResponse(is, Integer.valueOf(Long.toString(file.length())));

        return true;
    }


    public InputStream getInputStream(String content, String charSet) {
        if (content == null) {
            return null;
        }
        try {
            return new ByteArrayInputStream(content.getBytes(charSet));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        Pattern rule = Pattern.compile(".*\\.(action|do|ajax)(|\\?.*)");
        System.out.println("aaa.action?aaa=" + rule.matcher("aaa.action?aaad").matches());


        String rules = rule.matcher("http//192.168.0.7/aaa.action?aaad=&bbba").replaceAll("aaa");
//	   for(int k=0;rules.length>k;k++){
//		   System.out.println("k="+k+rules[k]);
//	   }
        System.out.println(rules);
    }
}
