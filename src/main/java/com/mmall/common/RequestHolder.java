package com.mmall.common;

import com.mmall.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * ThreadLocal 处理请求
 * @author wzy
 * @version 1.0
 * @date 2019/12/4 22:02
 */
public class RequestHolder {
    /**
     * 用户登录之后通过ThreadLocal存放登录的用户信息，使用时，直接拿回来用
     * ThreadLocal相当于是map, 当前线程去取值
     */
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }


}
