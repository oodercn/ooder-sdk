package net.ooder.scene.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * RequestMapping 配置
 *
 * <p>修复动态注册 RequestMapping 时的 UrlPathHelper 问题</p>
 *
 * @author Ooder Team
 * @since 2.3.1
 */
@Configuration
public class RequestMappingConfig {

    /**
     * 配置 RequestMappingHandlerMapping
     * 确保 UrlPathHelper 正确初始化
     */
    @Bean
    @Primary
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();

        // 配置 UrlPathHelper
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setAlwaysUseFullPath(true);
        urlPathHelper.setUrlDecode(true);
        urlPathHelper.setRemoveSemicolonContent(false);

        mapping.setUrlPathHelper(urlPathHelper);

        return mapping;
    }

    /**
     * 自定义 UrlPathHelper，确保 PATH 属性被设置
     */
    @Bean
    @Primary
    public UrlPathHelper urlPathHelper() {
        return new FixedUrlPathHelper();
    }

    /**
     * 修复的 UrlPathHelper
     */
    public static class FixedUrlPathHelper extends UrlPathHelper {

        private static final String PATH_ATTRIBUTE = FixedUrlPathHelper.class.getName() + ".PATH";

        @Override
        public String resolveAndCacheLookupPath(HttpServletRequest request) {
            // 检查是否已经解析过路径
            String lookupPath = (String) request.getAttribute(PATH_ATTRIBUTE);
            if (lookupPath != null) {
                return lookupPath;
            }

            // 解析路径
            lookupPath = getPathWithinApplication(request);

            // 缓存到 request 属性中
            request.setAttribute(PATH_ATTRIBUTE, lookupPath);

            return lookupPath;
        }
    }
}
