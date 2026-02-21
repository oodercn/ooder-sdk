package net.ooder.web.util;

import net.ooder.common.JDSBusException;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import net.ooder.esb.config.manager.EsbBean;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class SpringMVCTempXMLProxy extends AbstractAnnotationtExpressionTempManager {
    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, SpringMVCTempXMLProxy.class);
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private MetadataReaderFactory metadataReaderFactory;
    private EsbBean esbBean;

    public SpringMVCTempXMLProxy() {
    }

    public SpringMVCTempXMLProxy(EsbBean esbBean) {
        this.esbBean = esbBean;
    }


    @Override
    public Set<Class<?>> init() throws JDSBusException {
        Set<Class<?>> clazzs = scannerPackages(new String[]{esbBean.getPath()});
        this.fillBean(clazzs);
        return clazzs;
    }


    public void fillBean(Set<Class<?>> classList) {
        logger.info("start load SpringMVCTempBean beanId=" + esbBean.getId());
        List<String> tableNameList = new ArrayList<String>();
        for (Class clazz : classList) {
            Map valueMap = new HashMap();
            fillBean(clazz, valueMap);
        }
    }


    /**
     * 根据包路径获取包及子包下的所有类
     *
     * @param basePackages basePackage
     */
    private Set<Class<?>> scannerPackages(String[] basePackages) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(httpServletRequest.getServletContext());
        SpringPlugs springPlugs = webApplicationContext.getBean(SpringPlugs.class);
        return springPlugs.scannerPackages(basePackages);
    }
}