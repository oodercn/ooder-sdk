package net.ooder.web.util;

import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

public class RequestUtils {
//    /**
//     * 前端无请求默认为-1
//     */
//    public final static int NO_REQUEST = -1;
//
//    public static String getParameter(HttpServletRequest request, String name) {
//        return StringUtils.trim(request.getParameter(name));
//    }
//
//    public static int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? defaultValue : Integer.parseInt(parameter);
//    }
//
//    public static long getLongParameter(HttpServletRequest request, String name, long defaultValue) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? defaultValue : Long.parseLong(parameter);
//    }
//
//    public static boolean getBooleanParameter(HttpServletRequest request, String name, boolean defaultValue) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? defaultValue : Boolean.parseBoolean(parameter);
//    }
//
//    public static String getStringParameter(HttpServletRequest request, String name, String defaultValue) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? defaultValue : parameter;
//    }
//
//    public static String getTrimedStringParameter(HttpServletRequest request, String name, String defaultValue) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? defaultValue : StringUtils.trim(parameter);
//    }
//
//    public static String getTrimedStringParameterDefaultEmpty(HttpServletRequest request, String name) {
//        final String parameter = getParameter(request, name);
//
//        return StringUtils.isEmpty(parameter) ? "" : StringUtils.trim(parameter);
//    }
//
//    /**
//     * 功能描述：<br>
//     * 从请求中获取时间参数
//     *
//     * @param request
//     * @param key          时间字段在请求中的键
//     * @param defaultDate  默认时间 允许为空
//     * @param formatString 时间格式化字段 模式 参考 (yyyy-MM-dd HH:mm:ss)
//     * @return 按照时间可是转换之后的时间 null if request and defaultDate are null.
//     */
//    public static Date getDateParameter(HttpServletRequest request, String key, Date defaultDate, String formatString) {
//        final String parameter = getParameter(request, key);
//        try {
//            if (StringUtils.isNotEmpty(parameter)) {
//                SimpleDateFormat sdf = new SimpleDateFormat(formatString);
//                return StringUtils.isEmpty(parameter) ? defaultDate : sdf.parse(parameter);
//            } else {
//                return defaultDate;
//            }
//        } catch (Exception e) {
//            return defaultDate;
//        }
//    }
//
//    // 获取用户访问的IP
//    public static String getUserIPString(HttpServletRequest request) {
//
//        String ip = request.getHeader("x-real-ip");
//
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("x-forwarded-for");
//        }
//
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//
//        int pos = ip.indexOf(',');
//        if (pos >= 0) {
//            ip = ip.substring(0, pos);
//        }
//        return ip;
//    }
//
//    /**
//     * 功能描述：<br>
//     * 请求是否ajax请求
//     *
//     * @param request
//     * @return true是ajax请求 false非ajax请求
//     * @author huan.yan 2025-2-19 下午11:34:38 <br>
//     * 修改记录:
//     */
//    public static boolean isAjaxRequest(HttpServletRequest request) {
//        return StringUtils.equalsIgnoreCase(request.getHeader("x-requested-with"), "XMLHttpRequest");
//    }
//
//    /**
//     * 功能描述：<br>
//     * 序列号请求参数
//     *
//     * @param req
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public static String serializeRequestParam(HttpServletRequest req) {
//        StringBuilder sb = new StringBuilder();
//        Iterator<Entry<String, String[]>> iter = req.getParameterMap().entrySet().iterator();
//        boolean isFirstParam = true;
//        while (iter.hasNext()) {
//            Entry<String, String[]> entry = iter.next();
//            if (isFirstParam) {
//                isFirstParam = false;
//            } else {
//                sb.append("&");
//            }
//            sb.append(entry.getKey() + "=");
//            // sb.append(entry.getValue());
//            String[] paramValue = (String[]) entry.getValue();
//            for (int i = 0; i < paramValue.length; i++) {
//                if (i != 0) {
//                    sb.append(",");
//                }
//                sb.append(paramValue[i]);
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 将请求中的参数序列化成字符串
//     *
//     * @param request
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//    public static String convertParametersAsString(ServletRequest request) {
//        StringBuilder sb = new StringBuilder();
//        Enumeration paramEnum = request.getParameterNames();
//        boolean isAppend = false;
//        while (paramEnum.hasMoreElements()) {
//            String paramName = (String) paramEnum.nextElement();
//            if (isAppend) {
//                sb.append("&");
//            }
//            sb.append(paramName + "=" + request.getParameter(paramName));
//            isAppend = true;
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 获取相对的请求完整路径字符串:如:/room/list.htm?a=b&c=d
//     *
//     * @param request
//     * @return
//     */
//    public static String getRelativeReqUrl(HttpServletRequest request) {
//        String url = request.getServletPath();
//        if (RequestUtils.convertParametersAsString(request) != null && !RequestUtils.convertParametersAsString(request).equals("")) {
//            url = url + "?" + RequestUtils.convertParametersAsString(request);
//        }
//        return url;
//    }
}
