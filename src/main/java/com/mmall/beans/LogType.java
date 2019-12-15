package com.mmall.beans;

/**
 * 更新类型
 * @author wzy
 * @version 1.0
 * @date 2019/12/14 10:12
 */
public interface LogType {
    /**
     * 部门更新
     */
    int TYPE_DEPT = 1;

    /**
     * 用户更新
     */
    int TYPE_USER = 2;

    /**
     * 权限模块更新
     */
    int TYPE_ACL_MODULE = 3;

    /**
     * 权限点更新
     */
    int TYPE_ACL = 4;

    /**
     * 角色更新
     */
    int TYPE_ROLE = 5;

    /**
     * 角色权限更新
     */
    int TYPE_ROLE_ACL = 6;

    /**
     * 角色用户更新
     */
    int TYPE_ROLE_USER = 7;
}
