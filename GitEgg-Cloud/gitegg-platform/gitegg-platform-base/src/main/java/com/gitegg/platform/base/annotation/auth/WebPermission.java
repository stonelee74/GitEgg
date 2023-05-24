package com.gitegg.platform.base.annotation.auth;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface WebPermission {

    /**
     * 代码
     *
     * @return
     */
    String code() default "";

    /**
     * 名称
     *
     * @return
     */
    String label() default "";

    /**
     * 权限
     *
     * @return
     */
    String auth() default "";

    /**
     * URL
     *
     * @return
     */
    String url() default "";

    /**
     * 是否需要登陆
     *
     * @return
     */
    boolean needLogin() default true;

    /**
     * 是否记录操作日志
     *
     * @return
     */
    boolean actionLog() default false;

    /**
     * 是否属于一个工作流程
     *
     * @return
     */
    String flow() default "";

    /**
     * 是否可以作为流程步骤入口
     *
     * @return
     */
    boolean flowEntry() default false;

    /**
     * 流程后续操作
     *
     * @return
     */
    String flowButtons() default "";
}