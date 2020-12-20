package com.mfy.limiter.handler;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class ComponentHandler {

    private final Class<? extends Annotation> annotationType;

    private final TypeFilter typeFilter;

    protected ComponentHandler(Class<? extends Annotation> annotationType) {
        this.typeFilter = new AnnotationTypeFilter(annotationType);
        this.annotationType = annotationType;
    }

    public TypeFilter getTypeFilter() {
        return this.typeFilter;
    }

    public void handle(ScannedGenericBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = beanDefinition.getMetadata()
                .getAnnotationAttributes(this.annotationType.getName());
        if (attributes != null) {
            doHandle(attributes, beanDefinition, registry);
        }
    }

    protected abstract void doHandle(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition,
                                     BeanDefinitionRegistry registry);
}
