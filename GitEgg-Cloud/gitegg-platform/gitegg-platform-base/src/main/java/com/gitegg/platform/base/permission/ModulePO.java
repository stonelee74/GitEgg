package com.gitegg.platform.base.permission;


import java.util.Collection;
import java.util.HashMap;

public class ModulePO {
    private String id;
    private String name;
    private ControllerVO controllerVO;
    private HashMap<String, ControllerPO> routeControllers = new HashMap<String, ControllerPO>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ControllerPO getControllerPO(String key) {
        return routeControllers.get(key);
    }

    public void addControllerPO(ControllerPO vo) {
        routeControllers.put(vo.getName(), vo);
    }

    public Collection<ControllerPO> getControllerPOs() {
        return routeControllers.values();
    }

    public ControllerVO getControllerVO() {
        return controllerVO;
    }

    public void setControllerVO(ControllerVO controllerVO) {
        this.controllerVO = controllerVO;
    }

    @Override
    public String toString() {
        return "ModulePO [id=" + id + ", name=" + name + "]";
    }
}
