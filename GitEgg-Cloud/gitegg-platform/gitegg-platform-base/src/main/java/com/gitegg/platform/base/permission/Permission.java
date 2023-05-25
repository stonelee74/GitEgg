package com.gitegg.platform.base.permission;

import com.gitegg.platform.base.annotation.auth.WebPermission;
import com.gitegg.platform.base.exception.SystemException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liyan on 2016/11/2.
 */
public class Permission {
    public String module_code;

    public String module_label;

    public String controller_code;

    public String controller_label;

    public Map<String, Boolean> actions;

    public String getModule_code() {
        return module_code;
    }

    public void setModule_code(String module_code) {
        this.module_code = module_code;
    }

    public String getModule_label() {
        return module_label;
    }

    public void setModule_label(String module_label) {
        this.module_label = module_label;
    }

    public String getController_code() {
        return controller_code;
    }

    public void setController_code(String controller_code) {
        this.controller_code = controller_code;
    }

    public String getController_label() {
        return controller_label;
    }

    public void setController_label(String controller_label) {
        this.controller_label = controller_label;
    }

    public Map<String, Boolean> getActions() {
        return actions;
    }

    public void setActions(Map<String, Boolean> actions) {
        this.actions = actions;
    }

    /**
     * 取得权限列表
     *
     * @return
     */
    public static List<Permission> getPermissionList(Map<String, Object> voTree) {
        List<Permission> result = new ArrayList<>();

        // 循环所有模块
        for (Map.Entry<String, Object> m_entry : voTree.entrySet()) {
            Map<String, Object> moduleItem = (Map<String, Object>) m_entry.getValue();

            // 取得模块PO
            Object obj = moduleItem.get("_");
            if (obj == null) continue;

            String m_code = m_entry.getKey();
            ControllerVO m_vo = (ControllerVO) obj;
            WebPermission m_attr = m_vo.getPermission();

            if (m_attr == null) {
                throw new SystemException(String.format("模块 %s 未设定注解", m_code));
            }

            for (Map.Entry<String, Object> entry : moduleItem.entrySet()) {
                String c_code = entry.getKey();
                if ("_".equals(c_code)) continue;

                Map<String, ControllerVO> controllerItem = (Map<String, ControllerVO>) entry.getValue();
                ControllerVO c_vo = (ControllerVO) controllerItem.get("_");
                WebPermission c_attr = c_vo.getPermission();
                if (c_attr == null) {
                    throw new SystemException(String.format("控制器 %s 未设定注解", m_code));
                }

                Permission p = new Permission();
                p.setModule_code(m_code);
                p.setModule_label(m_attr.label());
                p.setController_code(c_code);
                p.setController_label(c_attr.label());
                Map<String, Boolean> actions = new HashMap<>();
                p.setActions(actions);

                boolean auth = false;

                // 取得非 Action 权限
                String m_auth = c_attr.auth();
                if (StringUtils.isNotEmpty(m_auth)) {
                    String[] auths = m_auth.split("\\,");
                    for (int i = 0; i < auths.length; i++) {
                        actions.put(auths[i], false);
                        p.setActions(actions);
                        auth = true;
                    }
                }

                // 取得 Action 权限
                for (Map.Entry<String, ControllerVO> actionEntry : controllerItem.entrySet()) {
                    String action_code = actionEntry.getKey();
                    if ("_".equals(action_code)) continue;
                    ControllerVO vo = actionEntry.getValue();
                    WebPermission attr = vo.getPermission();
                    if (attr == null) {
                        throw new SystemException(String.format("方法 %s 未设定注解", m_code));
                    }
                    String action = attr.auth();
                    if (StringUtils.isNotEmpty(action)) {
                        actions.put(action, false);
                        auth = true;
                    }
                }

                if (auth) result.add(p);
            }
        }
        return result;
    }
}
