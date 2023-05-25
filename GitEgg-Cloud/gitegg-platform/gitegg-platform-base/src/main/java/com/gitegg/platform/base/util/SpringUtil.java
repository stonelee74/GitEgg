package com.gitegg.platform.base.util;

import com.gitegg.platform.base.exception.BusinessException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <pre>
 *
 * </pre>
 *
 * @author hailiang.xu
 * @version 1.0
 * @since 2017/12/18 15:08
 */
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (Exception e) {
            throw new BusinessException("无效的服务 " + name);
        }
    }
}
