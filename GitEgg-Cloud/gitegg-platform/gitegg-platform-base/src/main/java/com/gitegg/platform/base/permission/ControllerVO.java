package com.gitegg.platform.base.permission;

import com.gitegg.platform.base.annotation.auth.WebPermission;

public class ControllerVO {
    public static final short TYPE_MODULE = 0;
    public static final short TYPE_CONTROLLER = 1;
    public static final short TYPE_ACTION = 2;

    private short type;
    private String code;
    private WebPermission permission;
    private Class<Object> controller;

    public String getLabel() {
        if (null == permission) return "";
        else return permission.label();
    }

    public WebPermission getPermission() {
        return permission;
    }

    public void setPermission(WebPermission permission) {
        this.permission = permission;
    }

    public Class<Object> getController() {
        return controller;
    }

    public void setController(Class<Object> controller) {
        this.controller = controller;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ControllerVO{" +
                "code=" + code +
                ", type=" + type +
                ", controller=" + controller +
                ", permission=" + permission +
                '}';
    }
}
