package com.gitegg.platform.base.permission;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
public class ControllerPO {
    private String id;
    private String name;
    private String code;
    private String auth;
    private String path;
    private HashMap<String, ActionPO> actionPOs = new HashMap<String, ActionPO>();

    private Class<Object> controller;

    public void addActionPO(String key, ActionPO vo) {
        actionPOs.put(key, vo);
    }

    private HashSet<String> getStringList(String auth) {
        HashSet<String> result = new HashSet<>();
        if (StringUtils.isNotEmpty(auth)) {
            String[] auths = auth.split("\\,");
            for (String a : auths) {
                String b = StringUtils.trim(a);
                if (StringUtils.isNotEmpty(b))
                    result.add(b);
            }
        }
        return result;
    }

    public Set<String> getActionList() {
        HashSet<String> result = getStringList(getAuth());
        Collection<ActionPO> actions = this.getActionPOs().values();
        for (ActionPO action : actions) {
            result.addAll(getStringList(action.getAuth()));
        }
        return result;
    }

    @Override
    public String toString() {
        return "ControllerPO [id=" + id + ", name=" + name + "]";
    }
}
