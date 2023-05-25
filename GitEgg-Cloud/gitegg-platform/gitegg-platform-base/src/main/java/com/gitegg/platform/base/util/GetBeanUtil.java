package com.gitegg.platform.base.util;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class GetBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private GetBeanUtil() {
        super();
    }

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        applicationContext = appContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, String name) {
        return applicationContext.getBean(name, clazz);
    }
}