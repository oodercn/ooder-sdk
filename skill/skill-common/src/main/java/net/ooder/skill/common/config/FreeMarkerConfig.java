package net.ooder.skill.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FreeMarkerConfig implements WebMvcConfigurer {

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates/");
        configurer.setDefaultEncoding("UTF-8");
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("nx_version", "2.3.1");
        variables.put("nx_theme", "light");
        configurer.setFreemarkerVariables(variables);
        
        return configurer;
    }

    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setRequestContextAttribute("request");
        return resolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/dashboard").setViewName("pages/dashboard");
    }
}
