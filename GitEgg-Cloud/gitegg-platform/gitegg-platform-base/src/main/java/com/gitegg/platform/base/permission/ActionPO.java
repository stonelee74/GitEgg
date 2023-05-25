package com.gitegg.platform.base.permission;

public class ActionPO {
    private String id;
    private String name;
    private String code;
    private String auth;

    private String path;
    private Class<Object> controller;

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

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ActionPO [id=" + id + ", name=" + name + "]";
    }
}
