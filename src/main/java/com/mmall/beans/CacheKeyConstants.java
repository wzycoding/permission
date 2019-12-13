package com.mmall.beans;

import lombok.Getter;

/**
 * 缓存前缀
 * @author wzy
 * @version 1.0
 * @date 2019/12/13 16:15
 */
@Getter
public enum CacheKeyConstants {
    /**系统级别 **/
    SYSTEM_ACLS,
    /**用户级别：需要绑定用户id进来**/
    USER_ACLS
}
