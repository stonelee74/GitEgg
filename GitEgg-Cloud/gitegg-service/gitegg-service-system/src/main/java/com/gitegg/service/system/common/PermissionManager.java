package com.gitegg.service.system.common;

import com.gitegg.platform.base.annotation.auth.WebPermission;
import com.gitegg.platform.base.permission.ActionPO;
import com.gitegg.platform.base.permission.ControllerPO;
import com.gitegg.platform.base.util.MyStringUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 权限管理器
 */
@Slf4j
@Component
public class PermissionManager implements ResourceLoaderAware {

    public static final String PROJECT_PACKAGE = "com.gitegg";

    private HashMap<String, ControllerPO> controllers = new HashMap<String, ControllerPO>();

//    private HashMap<String, ControllerPO> urlmapping = new HashMap<String, ControllerPO>();
//
    public HashMap<String, ControllerPO> getControllers() {
        return controllers;
    }
//
//    public HashMap<String, ControllerPO> getUrlMapping() {
//        return this.urlmapping;
//    }
//
//    public void setControllers(HashMap<String, ControllerPO> controllers) {
//        this.controllers = controllers;
//    }
//
//    public ControllerPO getController(String controllerName) {
//        return controllers.get(controllerName);
//    }

    private ResourceLoader resourceLoader;


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<Class> getControllerList() throws IOException {
        List<Class> classList = new ArrayList<Class>();

        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metaReader = new CachingMetadataReaderFactory(resourceLoader);
        Resource[] resources = resolver.getResources("classpath*:com/gitegg/service/system/controller/**/*.class");

        for (Resource r : resources) {
            MetadataReader reader = metaReader.getMetadataReader(r);
            String className = reader.getClassMetadata().getClassName();
            System.out.println("--- 发现类: " + className);

            // 取得文件对应的类
            try {
                Class clazz = Class.forName(className);
                if (clazz == null) continue;
                if (clazz.isInterface()) continue;
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                if (clazz.getAnnotation(WebPermission.class) == null) continue;
                if (clazz.getAnnotation(RequestMapping.class) == null) continue;
                classList.add(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classList;
    }

//    /**
//     * 返回控制器权限列表
//     *
//     * @param controllerName
//     * @return
//     */
//    public Set<String> getActionList(String controllerName) {
//        ControllerPO po = controllers.get(controllerName);
//        if (po == null) return new HashSet<>();
//        return po.getActionList();
//    }

    public void init() throws Exception {
        List<Class> list = getControllerList();

        for (Class clazz : list) {
            // 判断包名称
            String className = clazz.getName();
            System.out.println("--- 添加需授权控制器: " + className);
            addController(clazz);
        }
    }

    public void addController(Class controller) {

        // 取得控制器注解
        WebPermission ca = (WebPermission) controller.getAnnotation(WebPermission.class);
        if (null == ca) return;

        // 取得路由配置
        RequestMapping rm = (RequestMapping) controller.getAnnotation(RequestMapping.class);
        if (null == rm) return;

        int begin = PROJECT_PACKAGE.length() + 1;

        // 取得控制器PO
        String controllerCode = controller.getSimpleName().toLowerCase();
        ControllerPO routeController = controllers.get(controllerCode);
        if (routeController == null) {
            String path = ca.url();
            if (StringUtil.isEmpty(path)) {
                String[] paths = rm.value();
                path = paths[0];
            }

            routeController = new ControllerPO();
            routeController.setCode(controller.getName().substring(begin));
            routeController.setController(controller);
            routeController.setId(controllerCode);
            routeController.setName(ca.label());
            routeController.setAuth(ca.auth());
            routeController.setPath(path);

            // 创建动作PO
            Method[] cmethods = controller.getMethods();
            for (Method mt : cmethods) {
                WebPermission cam = mt.getAnnotation(WebPermission.class);
                if (null == cam || MyStringUtils.isEmpty(cam.label())) continue;

                String path1 = cam.url();
                if (StringUtil.isEmpty(path1)) {
                    path1 = path + path1;
                } else {
                    RequestMapping arm = mt.getAnnotation(RequestMapping.class);
                    if (arm != null) {
                        String[] paths = arm.value();
                        path1 = path + paths[0];
                    }
                }

                ActionPO routeAction = new ActionPO();
                routeAction.setId(mt.getName());
                routeAction.setName(cam.label());
                routeAction.setCode(mt.getName());
                routeAction.setController(controller);
                routeAction.setAuth(cam.auth());
                routeAction.setPath(path1);

                routeController.addActionPO(mt.getName(), routeAction);
            }
            controllers.put(controllerCode, routeController);
//            for (String url : rm.path()) {
//                urlmapping.put(url, routeController);
//            }
        }
    }
}