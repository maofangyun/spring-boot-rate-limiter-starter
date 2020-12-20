package com.mfy.limiter.handler;

import com.mfy.limiter.annotation.Limiter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.util.StringUtils;

import java.util.Map;

public class LimiterComponentHandler extends ComponentHandler{

    public LimiterComponentHandler() {
        super(Limiter.class);
    }

    @Override
    protected void doHandle(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FilterRegistrationBean.class);
        builder.addPropertyValue("filter", beanDefinition);
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("urlPatterns", extractUrlPatterns(attributes));
        // 向限流器加入key属性
        beanDefinition.getPropertyValues().add("key", extractKey(attributes));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    private String extractKey(Map<String, Object> attributes) {
        String luaName = (String)attributes.get("luaName");
        return luaName;
    }

    private String[] extractUrlPatterns(Map<String, Object> attributes) {
        String[] urlPatterns = (String[]) attributes.get("urlPatterns");
        return urlPatterns;
    }

    private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("limiterName")) ? attributes.get("limiterName")
                : beanDefinition.getBeanClassName());
    }

}
